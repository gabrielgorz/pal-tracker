package io.pivotal.pal.tracker;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.DistributionSummary;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/time-entries")
public class TimeEntryController {

    private TimeEntryRepository timeEntriesRepo;
    private final DistributionSummary timeEntrySummary;
    private final Counter actionCounter;


    public TimeEntryController(
            TimeEntryRepository timeEntriesRepo,
            MeterRegistry meterRegistry
    ) {
        this.timeEntriesRepo = timeEntriesRepo;

        this.timeEntrySummary = meterRegistry.summary("timeEntry.summary");
        this.actionCounter = meterRegistry.counter("timeEntry.actionCounter");
    }

    @PostMapping
    public ResponseEntity<TimeEntry> create(@RequestBody TimeEntry timeEntryToCreate) {
        TimeEntry createdEntry = timeEntriesRepo.create(timeEntryToCreate);

        actionCounter.increment();
        timeEntrySummary.record(timeEntriesRepo.list().size());

        return ResponseEntity.created(URI.create("/time-entries/"+createdEntry.getId())).body(createdEntry);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TimeEntry> read(@PathVariable("id") long timeEntryId) {
        TimeEntry timeEntry = timeEntriesRepo.find(timeEntryId);
        if(null == timeEntry)
            return ResponseEntity.notFound().build();
        actionCounter.increment();
        return ResponseEntity.ok(timeEntry);
    }

    @GetMapping
    public ResponseEntity<List<TimeEntry>> list() {
        actionCounter.increment();
        return ResponseEntity.ok(timeEntriesRepo.list());
    }

    @PutMapping(path = "/{id}")
    public ResponseEntity update(@PathVariable("id") long timeEntryId, @RequestBody TimeEntry expected) {
        TimeEntry updated = timeEntriesRepo.update(timeEntryId, expected);
        if(null == updated)
            return ResponseEntity.notFound().build();
        actionCounter.increment();
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity delete(@PathVariable("id") long timeEntryId) {
        timeEntriesRepo.delete(timeEntryId);
        actionCounter.increment();
        timeEntrySummary.record(timeEntriesRepo.list().size());
        return ResponseEntity.noContent().build();
    }
}
