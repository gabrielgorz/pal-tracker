package io.pivotal.pal.tracker;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import javax.sql.DataSource;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class JdbcTimeEntryRepository implements TimeEntryRepository {

    private JdbcTemplate jdbcTemplate;

    public JdbcTimeEntryRepository(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public TimeEntry create(TimeEntry timeEntry) {
        final String createSql = "insert into time_entries (project_id, user_id, date, hours) values (?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(createSql, new String[]{"ID"});
            ps.setLong(1, timeEntry.getProjectId());
            ps.setLong(2, timeEntry.getUserId());
            ps.setDate(3, Date.valueOf(timeEntry.getDate()));
            ps.setInt(4, timeEntry.getHours());
            return ps;
        }, keyHolder);

        return this.find(keyHolder.getKey().longValue());
    }

    @Override
    public TimeEntry find(long timeEntryId) {
        final String querySql = "select * from time_entries where id = ?";
        Map<String, Object> foundEntry = null;
        try {
            foundEntry = jdbcTemplate.queryForMap("Select * from time_entries where id = ?", timeEntryId);
        }catch (EmptyResultDataAccessException ex){

        }
        return null == foundEntry || foundEntry.size() < 1 ? null : buildTimeEntry(foundEntry);
    }

    private TimeEntry buildTimeEntry(Map<String, Object> foundEntry) {
        return new TimeEntry((long)foundEntry.get("id"), (long)foundEntry.get("project_id"), (long)foundEntry.get("user_id"), ((Date)foundEntry.get("Date")).toLocalDate(), (int)foundEntry.get("hours"));
    }

    @Override
    public List<TimeEntry> list() {
        List<Map<String, Object>> foundEntries = null;
        try {
            foundEntries = jdbcTemplate.queryForList("Select * from time_entries");
        }catch (EmptyResultDataAccessException ex){

        }
        List<TimeEntry> results = new ArrayList<>();
        if( null == foundEntries){
            return results;
        }
        for(Map<String, Object> item: foundEntries){
            results.add(buildTimeEntry(item));
        }
        return results;
    }

    @Override
    public TimeEntry update(long timeEntryId, TimeEntry timeEntry) {
        final String updateSql = "update time_entries set project_id = ?, user_id = ?, date = ?, hours = ? where id = ?";

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(updateSql, new String[]{"ID"});
            ps.setLong(1, timeEntry.getProjectId());
            ps.setLong(2, timeEntry.getUserId());
            ps.setDate(3, Date.valueOf(timeEntry.getDate()));
            ps.setInt(4, timeEntry.getHours());
            ps.setLong(5, timeEntryId);
            return ps;
        });

        return this.find(timeEntryId);
    }

    @Override
    public void delete(long timeEntryId) {
        jdbcTemplate.update("delete from time_entries where id = ?", timeEntryId);
    }
}
