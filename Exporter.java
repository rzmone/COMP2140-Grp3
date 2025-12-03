import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.time.format.DateTimeFormatter;

public class Exporter { 

    private static final DateTimeFormatter format = 
        DateTimeFormatter.ofPattern("mm-dd-yyyy hh:mm:ss");

    // =============================================================
    // EXPORT TRANSACTION HISTORY (ACTIVE)
    // =============================================================
   /* public static void export(List<Log> transactions, String fileName) 
            throws IOException {

        if (transactions == null || transactions.isEmpty()) {
            System.out.println("Error! No information found. Export Failed.");
            return;
        }

        try (FileWriter outputfile = new FileWriter(fileName)) {

            // Header
            outputfile.append("DocID,Time,Type,Amount,LoggedBy,Details,PrimaryDoc\n");

            for (Log t : transactions) {
                outputfile.append(t.getDocId()).append(",");
                outputfile.append(t.getTime().format(format)).append(",");
                outputfile.append(t.getType()).append(",");
                outputfile.append(String.valueOf(t.getAmount())).append(",");
                outputfile.append(t.getUserId()).append(",");
                outputfile.append(t.getDetails().replace(",", "")).append(",");
                outputfile.append(t.getPrimaryDoc() != null ? t.getPrimaryDoc() : "")
                         .append("\n");
            }

            System.out.println("\nHistory has successfully been exported to " + fileName);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }


    // =============================================================
    // EXPORT PLAIN LIST OF STRINGS (COMMENTED OUT — ENABLE LATER)
    // =============================================================
    /*
    public static void exportStrings(List<String> lines, String fileName) 
            throws IOException {

        if (lines == null || lines.isEmpty()) {
            System.out.println("Error! No data found. Export Failed.");
            return;
        }

        try (FileWriter outputfile = new FileWriter(fileName)) {
            for (String line : lines) {
                outputfile.write(line);
                outputfile.write(System.lineSeparator());
            }
            System.out.println("\nFile has successfully been exported to " + fileName);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    */
}
