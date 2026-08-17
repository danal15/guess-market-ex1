package ui;

import engine.api.GMEngine;
import engine.api.dto.BuyResultDTO;
import engine.api.dto.EventDTO;
import engine.api.dto.EventStateDTO;
import engine.api.dto.OptionStateDTO;
import engine.api.dto.TradeDTO;

import java.util.List;

public class ConsoleUI {

    private final GMEngine engine;
    private final ConsoleInput input;

    public ConsoleUI(GMEngine engine) {
        this.engine = engine;
        this.input = new ConsoleInput();
    }

    public void run() {
        System.out.println("Welcome to Guess Market.");
        boolean running = true;
        while (running) {
            printMenu();
            int choice = input.readIntInRange("Choose a command: ", 1, MenuCommand.values().length);
            MenuCommand command = MenuCommand.fromChoice(choice);
            try {
                switch (command) {
                    case LOAD_XML -> handleLoadXml();
                    case SHOW_EVENTS -> handleShowEvents();
                    case SHOW_EVENT_STATE -> handleEventState();
                    case BUY_SHARES -> handleBuy();
                    case CLOSE_EVENT -> handleClose();
                    case SAVE_STATE -> handleSaveState();
                    case LOAD_STATE -> handleLoadState();
                    case EXIT -> running = false;
                }
            } catch (RuntimeException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
        System.out.println("Goodbye.");
    }

    private void printMenu() {
        System.out.println();
        System.out.println("=== Menu ===");
        for (MenuCommand command : MenuCommand.values()) {
            System.out.println(command.getNumber() + ". " + command.getTitle());
        }
    }

    private void handleLoadXml() {
        String path = input.readNonEmptyLine("Enter the full path to the market XML file: ");
        engine.loadMarketFile(path);
        int count = engine.getEvents().size();
        System.out.println("The file was loaded successfully. " + count + " event(s) are now available.");
    }

    private void handleShowEvents() {
        requireLoaded();
        List<EventDTO> events = engine.getEvents();
        if (events.isEmpty()) {
            System.out.println("There are no events loaded.");
            return;
        }
        printEventList(events);
    }

    private void handleEventState() {
        requireLoaded();
        List<EventDTO> events = engine.getEvents();
        if (events.isEmpty()) {
            System.out.println("There are no events loaded.");
            return;
        }
        printEventList(events);
        int choice = input.readIntInRange("Choose an event by number: ", 1, events.size());
        EventDTO chosen = events.get(choice - 1);
        printEventState(engine.getEventState(chosen.getId()));
    }

    private void handleBuy() {
        requireLoaded();
        List<EventDTO> active = engine.getActiveEvents();
        if (active.isEmpty()) {
            System.out.println("There are no active events to participate in.");
            return;
        }
        printEventList(active);
        int choice = input.readIntInRange("Choose an event by number: ", 1, active.size());
        EventDTO chosen = active.get(choice - 1);
        EventStateDTO state = engine.getEventState(chosen.getId());
        printEventState(state);

        System.out.println("1. " + state.getOption1State().getName());
        System.out.println("2. " + state.getOption2State().getName());
        int optionChoice = input.readIntInRange("Choose an option by number: ", 1, 2);
        long quantity = input.readPositiveLong("Enter the quantity of shares to buy: ");

        BuyResultDTO result = engine.buyShares(chosen.getId(), optionChoice - 1, quantity);
        System.out.printf("You paid a total of %.2f (shares: %.2f, commission: %.2f)%n",
                result.getTotalPaid(), result.getSharesCost(), result.getCommissionPaid());
        printEventState(result.getAfterState());
    }

    private void handleClose() {
        requireLoaded();
        List<EventDTO> active = engine.getActiveEvents();
        if (active.isEmpty()) {
            System.out.println("There are no active events to close.");
            return;
        }
        printEventList(active);
        int choice = input.readIntInRange("Choose an event by number: ", 1, active.size());
        EventDTO chosen = active.get(choice - 1);
        EventStateDTO state = engine.getEventState(chosen.getId());
        printEventState(state);

        System.out.println("1. " + state.getOption1State().getName());
        System.out.println("2. " + state.getOption2State().getName());
        int winnerChoice = input.readIntInRange("Choose the winning option by number: ", 1, 2);

        EventStateDTO closedState = engine.closeEvent(chosen.getId(), winnerChoice - 1);
        System.out.println("The event has been closed.");
        printEventState(closedState);
    }

    private void handleSaveState() {
        requireLoaded();
        String path = input.readNonEmptyLine("Enter the full path (without extension) to save the state to: ");
        engine.saveState(path);
        System.out.println("State saved to " + path + ".gmstate");
    }

    private void handleLoadState() {
        String path = input.readNonEmptyLine("Enter the full path (without extension) to load the state from: ");
        engine.loadState(path);
        System.out.println("State loaded from " + path + ".gmstate");
    }

    private void requireLoaded() {
        if (!engine.isLoaded()) {
            throw new IllegalStateException("No valid file is loaded yet - use the load command first.");
        }
    }

    private void printEventList(List<EventDTO> events) {
        int number = 1;
        for (EventDTO event : events) {
            printEventBlock(number, event);
            number++;
        }
    }

    private void printEventBlock(int number, EventDTO event) {
        System.out.println(number + ". " + event.getName());
        System.out.println("   Description: " + event.getDescription());
        System.out.printf("   Commission: %d%% (%s)%n", event.getCommissionPercent(), event.getCommissionTypeLabel());
        System.out.println("   Options: " + event.getOption1Name() + " / " + event.getOption2Name());
        System.out.println("   Status: " + event.getStatusLabel());
    }

    private void printEventState(EventStateDTO state) {
        System.out.println("--- " + state.getEvent().getName() + " ---");
        OptionStateDTO o1 = state.getOption1State();
        OptionStateDTO o2 = state.getOption2State();
        System.out.printf("   %s: price %.2f, shares bought %d%n", o1.getName(), o1.getPrice(), o1.getSharesBought());
        System.out.printf("   %s: price %.2f, shares bought %d%n", o2.getName(), o2.getPrice(), o2.getSharesBought());
        System.out.printf("   Account balance: %.2f%n", state.getAccountBalance());
        System.out.printf("   Total commission collected: %.2f%n", state.getTotalCommissionCollected());

        List<TradeDTO> trades = state.getTradesNewestFirst();
        if (trades.isEmpty()) {
            System.out.println("   No trades were made in this event yet.");
        } else {
            System.out.println("   Trade history (newest first):");
            for (TradeDTO trade : trades) {
                System.out.printf("     Bought %d x '%s' for %.2f%n",
                        trade.getQuantity(), trade.getOptionName(), trade.getSharesCost());
            }
        }

        if (state.getWinningOptionName() != null) {
            System.out.println("   This event is closed. Winning option: " + state.getWinningOptionName());
        }
    }
}
