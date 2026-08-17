package engine.api.dto;

public final class BuyResultDTO {

    private final double sharesCost;
    private final double commissionPaid;
    private final double totalPaid;
    private final EventStateDTO afterState;

    public BuyResultDTO(double sharesCost, double commissionPaid, double totalPaid, EventStateDTO afterState) {
        this.sharesCost = sharesCost;
        this.commissionPaid = commissionPaid;
        this.totalPaid = totalPaid;
        this.afterState = afterState;
    }

    public double getSharesCost() {
        return sharesCost;
    }

    public double getCommissionPaid() {
        return commissionPaid;
    }

    public double getTotalPaid() {
        return totalPaid;
    }

    public EventStateDTO getAfterState() {
        return afterState;
    }
}
