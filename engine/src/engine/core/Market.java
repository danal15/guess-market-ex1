package engine.core;

import engine.model.Event;
import engine.model.EventStatus;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class Market implements Serializable {

    private static final long serialVersionUID = 1L;

    private final List<Event> events;

    public Market(List<Event> events) {
        this.events = new ArrayList<>(events);
    }

    public List<Event> getEvents() {
        return Collections.unmodifiableList(events);
    }

    public List<Event> getActiveEvents() {
        List<Event> active = new ArrayList<>();
        for (Event event : events) {
            if (event.getStatus() == EventStatus.ACTIVE) {
                active.add(event);
            }
        }
        return active;
    }

    public Optional<Event> findById(int id) {
        for (Event event : events) {
            if (event.getId() == id) {
                return Optional.of(event);
            }
        }
        return Optional.empty();
    }
}
