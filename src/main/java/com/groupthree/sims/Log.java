package com.groupthree.sims;

import java.time.LocalDateTime;
/**
 * Represents a log entry in the system history.
 *
 * A Log contains:
 *  - A unique internal ID (numeric)
 *  - The user ID of the user who performed the action
 *  - Details describing the action taken
 *  - A timestamp of when the action occurred
 *
 * This class acts as a data model for logging system activities.
 */
public class Log {

    private int id; //ID for document
    private int userID; //User identification to indicate who made change
    private String details; //Information telling what happened (modify or else)
    private LocalDateTime time;

    public Log(String details)
    {
        this.details = details;
    }

    public Log(String details, LocalDateTime time)
    {
        this.details = details;
        this.time = time;
    }

    public Log(String detauls, int userID)
    {
        this.details = detauls;
        this.userID = userID;
    }

    public Log(int id, int userID, String details)
    {
        this.id = id;
        this.userID = userID;
        this.details = details;
        this.time = LocalDateTime.now();
    }

    public Log(int id, int userID, String details, LocalDateTime time)
    {
        this.id = id;
        this.userID = userID;
        this.details = details;
        this.time = time;
    }
    
    public int getID () {
        return id;
    }

    public int getUserID () {
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
