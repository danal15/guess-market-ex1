package engine.model;

public enum EventStatus {
    ACTIVE("Active"),
    CLOSED("Closed");

    private final String label;

    EventStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
