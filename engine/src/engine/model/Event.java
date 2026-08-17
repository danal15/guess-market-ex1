package engine.model;

import engine.core.lmsr.LmsrMath;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Event implements Serializable {

    private static final long serialVersionUID = 1L;

    private final int id;
    private final String name;
    private final String description;
    private final int commissionPercent;
    private final CommissionType commissionType;
    private final int b;
    private final Option[] options;
    private final Account account;
    private double totalCommissionCollected;
    private final List<Trade> trades;
    private EventStatus status;
    private Integer winningOptionIndex;

    public Event(int id, String name, String description, int commissionPercent,
                 CommissionType commissionType, int b, String option1Name, String option2Name) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.commissionPercent = commissionPercent;
        this.commissionType = commissionType;
        this.b = b;
        this.options = new Option[] { new Option(option1Name), new Option(option2Name) };
        this.account = new Account();
        this.totalCommissionCollected = 0.0;
        this.trades = new ArrayList<>();
        this.status = EventStatus.ACTIVE;
        this.winningOptionIndex = null;
    }

    public double priceOf(int optionIndex) {
        checkIndex(optionIndex);
        int other = 1 - optionIndex;
        return LmsrMath.price(options[optionIndex].getSharesBought(), options[other].getSharesBought(), b);
    }

    /** Buys shares of one option; returns {sharesCost, commissionPaid, totalPaid}. */
    public double[] buy(int optionIndex, long quantity) {
        checkIndex(optionIndex);
        if (status != EventStatus.ACTIVE) {
            throw new IllegalStateException("Event '" + name + "' is not active - trading is closed.");
        }
        if (quantity < 1) {
            throw new IllegalArgumentException("Quantity must be at least 1.");
        }

        long qYes = options[0].getSharesBought();
        long qNo = options[1].getSharesBought();
        double before = LmsrMath.cost(qYes, qNo, b);
        long afterYes = optionIndex == 0 ? qYes + quantity : qYes;
        long afterNo = optionIndex == 1 ? qNo + quantity : qNo;
        double after = LmsrMath.cost(afterYes, afterNo, b);
        double sharesCost = after - before;
        double commission = commissionType == CommissionType.ON_PURCHASE
                ? sharesCost * commissionPercent / 100.0
                : 0.0;

        options[optionIndex].addShares(quantity);
        account.deposit(sharesCost + commission);
        totalCommissionCollected += commission;
        trades.add(new Trade(options[optionIndex].getName(), quantity, sharesCost, commission));

        return new double[] { sharesCost, commission, sharesCost + commission };
    }

    /**
     * Closes the event, declaring a winning option. Per the lecturer's forum
     * correction the account is NOT reset afterward — its final value (which
     * may be negative) is left as-is to show the market maker's net result.
     */
    public void close(int winningIndex) {
        checkIndex(winningIndex);
        if (status != EventStatus.ACTIVE) {
            throw new IllegalStateException("Event '" + name + "' is already closed.");
        }
        long winningShares = options[winningIndex].getSharesBought();
        double payoutBase = winningShares * 1.0;
        double fee = commissionType == CommissionType.ON_CLOSE
                ? payoutBase * commissionPercent / 100.0
                : 0.0;
        totalCommissionCollected += fee;
        account.withdraw(payoutBase - fee);
        winningOptionIndex = winningIndex;
        status = EventStatus.CLOSED;
    }

    private void checkIndex(int optionIndex) {
        if (optionIndex != 0 && optionIndex != 1) {
            throw new IllegalArgumentException("Option index must be 0 or 1, got: " + optionIndex);
        }
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getCommissionPercent() {
        return commissionPercent;
    }

    public CommissionType getCommissionType() {
        return commissionType;
    }

    public int getB() {
        return b;
    }

    public Option getOption(int index) {
        checkIndex(index);
        return options[index];
    }

    public Account getAccount() {
        return account;
    }

    public double getTotalCommissionCollected() {
        return totalCommissionCollected;
    }

    public List<Trade> getTrades() {
        return Collections.unmodifiableList(trades);
    }

    public EventStatus getStatus() {
        return status;
    }

    public Integer getWinningOptionIndex() {
        return winningOptionIndex;
    }
}
