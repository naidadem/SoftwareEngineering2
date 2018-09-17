package se.opiflex.opiflexindustrialmonitoring;

import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;

public class Error extends ArrayList<Error> implements Comparable<Error> {
    private String id;
    private int count;
    private String robotId;
    private String errorMessage;
    private String messages[];
    private boolean solved;
    private long time;
    private boolean isError;


    public Error(DocumentSnapshot doc) {
        id = doc.getString("errorid");
        robotId = doc.getString("robotid");
        errorMessage = doc.getString("errormessage");
        time = doc.getLong("timestamp");
        solved = doc.getBoolean("solved");
        isError = doc.getBoolean("iserror");
    }

    public Error(String message)
    {
        this.messages[count] = message;
        solved = false;
    }

    public void addMessage(String message)
    {
        this.messages[count] = message;
        count++;
    }

    public void toggleSolved()
    {
        solved = !solved;
    }

    public String getId() {
        return id;
    }

    public int getCount() {
        return count;
    }

    public String getRobotId() {
        return robotId;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public boolean isSolved() {
        return solved;
    }

    public long getTime() {
        return time;
    }

    public boolean isError() {
        return isError;
    }

    public int compareTo(Error other) {
        if (getTime() - other.getTime() < 0) {
            return 1;
        }
        else if (getTime() - other.getTime() > 0) {
            return -1;
        }
        else return 0;
    }
}
