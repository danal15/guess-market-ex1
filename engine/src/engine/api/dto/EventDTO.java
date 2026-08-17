package engine.api.dto;

public final class EventDTO {

    private final int id;
    private final String name;
    private final String description;
    private final int commissionPercent;
    private final String commissionTypeLabel;
    private final String option1Name;
    private final String option2Name;
    private final String statusLabel;

    public EventDTO(int id, String name, String description, int commissionPercent,
                     String commissionTypeLabel, String option1Name, String option2Name, String statusLabel) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.commissionPercent = commissionPercent;
        this.commissionTypeLabel = commissionTypeLabel;
        this.option1Name = option1Name;
        this.option2Name = option2Name;
        this.statusLabel = statusLabel;
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

    public String getCommissionTypeLabel() {
        return commissionTypeLabel;
    }

    public String getOption1Name() {
        return option1Name;
    }

    public String getOption2Name() {
        return option2Name;
    }

    public String getStatusLabel() {
        return statusLabel;
    }
}
