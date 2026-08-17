package ui;

public enum MenuCommand {
    LOAD_XML("Load a market file (XML)"),
    SHOW_EVENTS("Show all events"),
    SHOW_EVENT_STATE("Show an event's trading state"),
    BUY_SHARES("Participate in an event (buy shares)"),
    CLOSE_EVENT("Close (resolve) an event"),
    SAVE_STATE("Save system state to a file [bonus]"),
    LOAD_STATE("Load system state from a file [bonus]"),
    EXIT("Exit");

    private final String title;

    MenuCommand(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public int getNumber() {
        return ordinal() + 1;
    }

    public static MenuCommand fromChoice(int choice) {
        MenuCommand[] all = values();
        if (choice < 1 || choice > all.length) {
            return null;
        }
        return all[choice - 1];
    }
}
