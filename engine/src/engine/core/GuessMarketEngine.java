package engine.core;

import engine.api.GMEngine;
import engine.api.dto.BuyResultDTO;
import engine.api.dto.EventDTO;
import engine.api.dto.EventStateDTO;
import engine.api.dto.OptionStateDTO;
import engine.api.dto.TradeDTO;
import engine.api.exception.StatePersistenceException;
import engine.core.xml.MarketFileLoader;
import engine.model.Event;
import engine.model.Trade;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class GuessMarketEngine implements GMEngine {

    private static final String STATE_FILE_EXTENSION = ".gmstate";

    private Market market;

    @Override
    public void loadMarketFile(String path) {
        Market loaded = MarketFileLoader.load(path);
        this.market = loaded;
    }

    @Override
    public boolean isLoaded() {
        return market != null;
    }

    @Override
    public List<EventDTO> getEvents() {
        requireLoaded();
        List<EventDTO> result = new ArrayList<>();
        for (Event event : market.getEvents()) {
            result.add(toEventDTO(event));
        }
        return result;
    }

    @Override
    public List<EventDTO> getActiveEvents() {
        requireLoaded();
        List<EventDTO> result = new ArrayList<>();
        for (Event event : market.getActiveEvents()) {
            result.add(toEventDTO(event));
        }
        return result;
    }

    @Override
    public EventStateDTO getEventState(int eventId) {
        requireLoaded();
        return toEventStateDTO(requireEvent(eventId));
    }

    @Override
    public BuyResultDTO buyShares(int eventId, int optionIndex, long quantity) {
        requireLoaded();
        Event event = requireEvent(eventId);
        double[] result = event.buy(optionIndex, quantity);
        return new BuyResultDTO(result[0], result[1], result[2], toEventStateDTO(event));
    }

    @Override
    public EventStateDTO closeEvent(int eventId, int winningOptionIndex) {
        requireLoaded();
        Event event = requireEvent(eventId);
        event.close(winningOptionIndex);
        return toEventStateDTO(event);
    }

    @Override
    public void saveState(String pathWithoutExtension) {
        requireLoaded();
        String trimmed = requireNonEmptyPath(pathWithoutExtension);
        File target = new File(trimmed + STATE_FILE_EXTENSION);
        File parentDir = target.getAbsoluteFile().getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            throw new StatePersistenceException("Cannot save state: the folder " + parentDir + " does not exist.");
        }
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(target))) {
            out.writeObject(market);
        } catch (IOException e) {
            throw new StatePersistenceException("Could not save state to " + target + ": " + e.getMessage());
        }
    }

    @Override
    public void loadState(String pathWithoutExtension) {
        String trimmed = requireNonEmptyPath(pathWithoutExtension);
        File source = new File(trimmed + STATE_FILE_EXTENSION);
        if (!source.exists() || !source.isFile()) {
            throw new StatePersistenceException("No saved state exists at: " + source);
        }
        Object loaded;
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(source))) {
            loaded = in.readObject();
        } catch (InvalidClassException e) {
            throw new StatePersistenceException(
                    "The file " + source + " was saved by an incompatible version of this program.");
        } catch (IOException | ClassNotFoundException e) {
            throw new StatePersistenceException("The file " + source + " is not a valid saved state: " + e.getMessage());
        }
        if (!(loaded instanceof Market)) {
            throw new StatePersistenceException("The file " + source + " is not a valid saved state.");
        }
        this.market = (Market) loaded;
    }

    private String requireNonEmptyPath(String path) {
        if (path == null || path.trim().isEmpty()) {
            throw new StatePersistenceException("No file path was entered.");
        }
        return path.trim();
    }

    private void requireLoaded() {
        if (market == null) {
            throw new IllegalStateException("No valid file is loaded yet - use the load command first.");
        }
    }

    private Event requireEvent(int eventId) {
        Optional<Event> event = market.findById(eventId);
        if (event.isEmpty()) {
            throw new IllegalArgumentException("No event with id " + eventId + " exists.");
        }
        return event.get();
    }

    private EventDTO toEventDTO(Event event) {
        return new EventDTO(
                event.getId(),
                event.getName(),
                event.getDescription(),
                event.getCommissionPercent(),
                event.getCommissionType().getLabel(),
                event.getOption(0).getName(),
                event.getOption(1).getName(),
                event.getStatus().getLabel()
        );
    }

    private EventStateDTO toEventStateDTO(Event event) {
        OptionStateDTO option1State = new OptionStateDTO(
                event.getOption(0).getName(), event.priceOf(0), event.getOption(0).getSharesBought());
        OptionStateDTO option2State = new OptionStateDTO(
                event.getOption(1).getName(), event.priceOf(1), event.getOption(1).getSharesBought());

        List<Trade> trades = event.getTrades();
        List<TradeDTO> tradesNewestFirst = new ArrayList<>();
        for (int i = trades.size() - 1; i >= 0; i--) {
            Trade t = trades.get(i);
            tradesNewestFirst.add(new TradeDTO(t.getOptionName(), t.getQuantity(), t.getSharesCost(), t.getCommissionPaid()));
        }

        String winnerName = null;
        Integer winningIndex = event.getWinningOptionIndex();
        if (winningIndex != null) {
            winnerName = event.getOption(winningIndex).getName();
        }

        return new EventStateDTO(
                toEventDTO(event),
                option1State,
                option2State,
                event.getAccount().getBalance(),
                event.getTotalCommissionCollected(),
                Collections.unmodifiableList(tradesNewestFirst),
                winnerName
        );
    }
}
