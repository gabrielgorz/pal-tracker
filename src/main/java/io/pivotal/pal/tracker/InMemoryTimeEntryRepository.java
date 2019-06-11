package io.pivotal.pal.tracker;

import java.util.*;

public class InMemoryTimeEntryRepository implements TimeEntryRepository {

    private final Map<Long, TimeEntry> database;

    private Long indexCounter;

    public InMemoryTimeEntryRepository() {
        indexCounter = 0L;
        database = new HashMap<>();
    }

    private TimeEntry timeEntryWithId(Long id, TimeEntry timeEntry){
        return new TimeEntry(id, timeEntry.getProjectId(), timeEntry.getUserId(), timeEntry.getDate(), timeEntry.getHours());
    }

    private Long getNextId() {
        return ++indexCounter;
    }

    public TimeEntry create(TimeEntry timeEntry) {
        long timeEntryId = getNextId();
        TimeEntry created = timeEntryWithId(timeEntryId, timeEntry);
        database.put(timeEntryId, created);
        return created;
    }

    public TimeEntry find(long timeEntryId) {
        return database.get(timeEntryId);
    }

    public List<TimeEntry> list() {
        return new ArrayList<>(database.values());
    }

    public TimeEntry update(long id, TimeEntry timeEntry) {
        if (!database.containsKey(id))
            return null;
        TimeEntry updated = timeEntryWithId(id, timeEntry);
        database.put(id, updated);
        return updated;
    }

    public void delete(long id) {
        database.remove(id, database.get(id));
    }
}
