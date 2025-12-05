package com.groupthree.sims;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.ArrayList;
import java.util.HashMap;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class HistorySys {

    // Stores list of logs
    public static final DateTimeFormatter timeForm =
            DateTimeFormatter.ofPattern("MM-dd-yyyy hh:mm:ss");

    // ============================================================
    // LOG SALES  (Sale temporarily disabled until group classes added)
    // ============================================================
    public static Log logSales(User user, Sale sale)
    {
        String details = "SALES: " + sale.toString();
        Log log = new Log(details);

        Map<String, Object> values = new HashMap<>();
        values.put("userID", user.getId());
        values.put("details", log.getDetails());
        values.put("time", java.sql.Timestamp.valueOf(LocalDateTime.now()));

        int logId = Database.insertWithPk("logs", values);

        return new Log(logId, log.getUserID(), log.getDetails(), log.getTime());
    }

    // ============================================================
    // LOG MODIFICATION
    // ============================================================
    public static Log logAny(User user, String title, String details)
    {
        Log log = new Log(title.toUpperCase() + ": " + details, user.getId());

        Map<String, Object> values = new HashMap<>();
        values.put("userID", user.getId());
        values.put("details", log.getDetails());
        values.put("time", java.sql.Timestamp.valueOf(LocalDateTime.now()));

        int logId = Database.insertWithPk("logs", values);

        return new Log(logId, log.getUserID(), log.getDetails(), log.getTime());
    }

    public static List<Log> getAllHistory()
    {
        List<Map<String, Object>> records = Database.selectAll("logs");
        List<Log> logs = new ArrayList<>();

        for (Map<String, Object> record : records) {
            int id = (int) record.get("id");
            int userID = (int) record.get("userID");
            String details = (String) record.get("details");
            LocalDateTime logTime = null;

            Object logTimeObj = record.get("time");
            if (logTimeObj != null)
            {
                if (logTimeObj instanceof Timestamp) {
                    logTime = ((Timestamp) logTimeObj).toLocalDateTime();
                } else if (logTimeObj instanceof LocalDateTime) {
                    logTime = (LocalDateTime) logTimeObj;
                } else if (logTimeObj instanceof String) {
                    // Fallback if driver returns a String
                    logTime = LocalDateTime.parse((String) logTimeObj);
                }
            }

            Log log = new Log(id, userID, details, logTime);
            logs.add(log);
        }

        return logs;
    }

    // ============================================================
    // GET HISTORY (optional user filter)
    // ============================================================
    public List<Log> getHistoryByUserId(String filterUserID)
    {
        if (filterUserID == null || filterUserID.trim().isEmpty()) {
            return getAllHistory();
        }

        int useriD = Integer.parseInt(filterUserID);

        return getAllHistory().stream()
                .filter(log -> log.getUserID() == useriD)
                .collect(Collectors.toList());
    }

    // ============================================================
    // SEARCH LOG BY ID
    // ============================================================
    public Optional<Log> searchLog(int logID) {
        return getAllHistory().stream()
                .filter(log -> log.getID() == logID)
                .findFirst();
    }

    // ============================================================
    // EDIT LOG (creates a new MODIFICATION log)
    // ============================================================
    public Log editLog(int originalLogID, String newDetails, User user)
    {
        Optional<Log> original = searchLog(originalLogID);

        if (original.isEmpty()) {
            System.err.println("Error: Cannot edit log. Original Log ID not found: " + originalLogID);
            return null;
        }

        String modDetails = String.format(
                "[EDITED] [%s]. | [PREVIOUSLY]: [%s]",
                original.get().getDetails(),
                newDetails
        );

        String where = "id = " + originalLogID;
        boolean result = Database.update("logs", Map.of("details", modDetails), where) > 1;

        if (!result)
        {
            System.err.println("Error: Failed to edit log ID " + originalLogID);
            return null;
        }

        return new Log(originalLogID, user.getId(), modDetails, original.get().getTime());
    }


    // ============================================================
    // REMOVE LOG — DISABLED
    // ============================================================
    public boolean removeLog(String logID) {
        System.out.println("History logs cannot be removed (Read Only)");
        return false;
    }


    // ============================================================
    // EXPORT HISTORY (Exporter disabled until linked)
    // ============================================================
    public void exportHistory(String file)
    {
        
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        for (Log log : getAllHistory()) {
            sb.append("[")
                    .append(log.getTime())
                    .append("] User: ").append(log.getUserID())
                    .append(" | ").append(log.getDetails())
                    .append(" (ID: ").append(log.getID()).append(")")
                    .append("\n");
        }

        return sb.toString();
    }
}
