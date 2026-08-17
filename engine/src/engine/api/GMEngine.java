package engine.api;

import engine.api.dto.BuyResultDTO;
import engine.api.dto.EventDTO;
import engine.api.dto.EventStateDTO;

import java.util.List;

public interface GMEngine {

    void loadMarketFile(String path);

    boolean isLoaded();

    List<EventDTO> getEvents();

    List<EventDTO> getActiveEvents();

    EventStateDTO getEventState(int eventId);

    BuyResultDTO buyShares(int eventId, int optionIndex, long quantity);

    EventStateDTO closeEvent(int eventId, int winningOptionIndex);

    void saveState(String pathWithoutExtension);

    void loadState(String pathWithoutExtension);
}
