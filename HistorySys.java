import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.ArrayList;
import java.time.format.DateTimeFormatter;

public class HistorySys {

    // Stores list of logs
    private static final List<Log> history = new ArrayList<>();
    public static final DateTimeFormatter timeForm =
            DateTimeFormatter.ofPattern("MM-dd-yyyy hh:mm:ss");


    // ============================================================
    // LOG SALES  (Sale temporarily disabled until group classes added)
    // ============================================================
    public Log logSales(String userID, /* Sale sale */ Object salePlaceholder) {
        /*
        When Sale class is ready, replace Object with Sale
        and uncomment the line below:

        String details = "SALES: " + sale.toString();
        */

        String details = "SALES ACTION (Sale class not linked yet)";
        Log log = new Log(details, userID);

        // database.save(log);  // waiting for group integration
        history.add(log);
        return log;
    }


    // ============================================================
    // LOG MODIFICATION
    // ============================================================
    public Log logModify(String details, String userID) {
        Log log = new Log("MODIFICATION: " + details, userID);

        // database.save(log); // commented until DB is added
        history.add(log);
        return log;
    }


    // ============================================================
    // GET HISTORY (optional user filter)
    // ============================================================
    public List<Log> getHistory(String filterUserID) {
        if (filterUserID == null || filterUserID.trim().isEmpty()) {
            return new ArrayList<>(history);
        }

        return history.stream()
                .filter(log -> log.getUserID().equals(filterUserID))
                .collect(Collectors.toList());
    }


    // ============================================================
    // SEARCH LOG BY ID
    // ============================================================
    public Optional<Log> searchLog(String logID) {
        return history.stream()
                .filter(log -> log.getID().equals(logID))
                .findFirst();
    }


    // ============================================================
    // EDIT LOG (creates a new MODIFICATION log)
    // ============================================================
    public Log editLog(String originalLogID, String newDetails, String userID) {

        Optional<Log> original = searchLog(originalLogID);

        if (original.isEmpty()) {
            System.err.println("Error: Cannot edit log. Original Log ID not found: " + originalLogID);
            return null;
        }

        String modDetails = String.format(
                "EDITED Log ID: %s. Previous Details: [%s]. New Details: [%s]",
                originalLogID,
                original.get().getDetails(),
                newDetails
        );

        return logModify(modDetails, userID);
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
    public void exportHistory(String file) {
        try {
            /*
            Exporter.exportLogs(history, file);
            */
            System.out.println("Exporting disabled — waiting for Exporter integration.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        for (Log log : history) {
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
