package engine.model;

public enum CommissionType {
    ON_PURCHASE("on-purchase"),
    ON_CLOSE("on-close");

    private final String label;

    CommissionType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public static CommissionType fromXmlValue(String raw) {
        if (raw == null) {
            return null;
        }
        String trimmed = raw.trim();
        for (CommissionType type : values()) {
            if (type.label.equalsIgnoreCase(trimmed)) {
                return type;
            }
        }
        return null;
    }
}
