package engine.model;

import java.io.Serializable;

public class Option implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String name;
    private long sharesBought;

    public Option(String name) {
        this.name = name;
        this.sharesBought = 0L;
    }

    void addShares(long quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity to add must be positive: " + quantity);
        }
        sharesBought += quantity;
    }

    public String getName() {
        return name;
    }

    public long getSharesBought() {
        return sharesBought;
    }
}
