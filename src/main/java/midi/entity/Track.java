package midi.entity;

import java.util.ArrayList;
import java.util.List;

public class Track {

    private final int trackNumber;

    private final List<Event> eventList;

    public Track(int trackNumber) {
        this.trackNumber = trackNumber;
        eventList = new ArrayList<>();
    }

    public int getTrackNumber() {
        return trackNumber;
    }

    public List<Event> getEventList() {
        return eventList;
    }

}
