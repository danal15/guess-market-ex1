package engine.model;

import java.io.Serializable;

public final class Trade implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String optionName;
    private final long quantity;
    private final double sharesCost;
    private final double commissionPaid;

    public Trade(String optionName, long quantity, double sharesCost, double commissionPaid) {
        this.optionName = optionName;
        this.quantity = quantity;
        this.sharesCost = sharesCost;
        this.commissionPaid = commissionPaid;
    }

    public String getOptionName() {
        return optionName;
    }

    public long getQuantity() {
        return quantity;
    }

    public double getSharesCost() {
        return sharesCost;
    }

    public double getCommissionPaid() {
        return commissionPaid;
    }
}
