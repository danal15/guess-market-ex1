package engine.api.dto;

public final class OptionStateDTO {

    private final String name;
    private final double price;
    private final long sharesBought;

    public OptionStateDTO(String name, double price, long sharesBought) {
        this.name = name;
        this.price = price;
        this.sharesBought = sharesBought;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public long getSharesBought() {
        return sharesBought;
    }
}
