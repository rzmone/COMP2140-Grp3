import java.time.LocalDateTime;
import java.util.UUID;

public class Log {

    private String id; //ID for document
    private String userID; //User identification to indicate who made change
    private String details; //Information telling what happened (modify or else)
    private LocalDateTime time;

    public Log(String details, String userID) {
        this.id= UUID.randomUUID().toString();
        this.userID=userID;
        this.details=details;
        this.time=LocalDateTime.now();
    }
    
    public String getID () {
        return id;
    }

    public String getUserID () {
        return userID;
    }

    public String getDetails () {
        return details;
    }

    public LocalDateTime getTime () {
        return time;
    }

    public String toString(){
        return "[" + this.time.toString() + " ] User: " + this.userID + " | " + this.details + "ID: " + this.id + "." ;
    }
    
}
