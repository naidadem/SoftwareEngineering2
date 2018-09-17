package se.opiflex.opiflexindustrialmonitoring;

import com.google.firebase.firestore.DocumentSnapshot;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;


public class Robot
{
    /* Robot information */
    private String robotId;
    private String robotCellId;
    private String productName;
    private String station; //Unsure what "station" means at the moment.
    private String log;
    private long startTime; //JSON specifies int, but int is too small for UNIX time.
    private long currentTime;
    private int producedItems;
    private int target;
    private int cycleTime;
    private int eventCode;
    private int inputPallet;
    private int outputPallet;
    private int speed;
    private boolean running;
    private boolean error;
    private boolean sendPushNotifications;

    private ArrayList<String> errorList = new ArrayList<>();

    /* Constructor wont be used, since onCreate is the constructor */
    public Robot()
    {
        sendPushNotifications = false; //True or false by default?
    }

    public Robot(DocumentSnapshot doc) {
        robotId = doc.getString("robotid");
        robotCellId = doc.getString("robotcellid");
        productName = doc.getString("productname");
        station = doc.getString("station");
        log = doc.getString("log");
        startTime = doc.getLong("starttime");
        currentTime = doc.getLong("currenttime");
        producedItems = (int) (long) doc.getLong("produced");
        target = (int) (long) doc.getLong("target");
        cycleTime = (int) (long) doc.getLong("cycletime");
        eventCode = (int) (long) doc.getLong("eventcode");
        inputPallet = (int) (long) doc.getLong("inputpallet");
        outputPallet = (int) (long) doc.getLong("outputpallet");
        speed = (int) (long) doc.getLong("speed");
        running = doc.getBoolean("running");
        error = doc.getBoolean("error");
        sendPushNotifications = false; //True or false by default?
    }

    public ArrayList<String> getErrorList()
    {
        return errorList;
    }

    public int getTarget()
    {
        return target;
    }

    public String getRobotId()
    {
        return robotId;
    }

    public String getRobotCellId()
    {
        return robotCellId;
    }

    public String getProductName()
    {
        return productName;
    }

    public String getStation()
    {
        return station;
    }

    public String getLog()
    {
        return log;
    }

    public long getStartTime()
    {
        return startTime;
    }

    public long getCurrentTime()
    {
        return currentTime;
    }

    public int getProducedItems()
    {
        return producedItems;
    }

    public int getCycleTime()
    {
        return cycleTime;
    }

    public int getEventCode()
    {
        return eventCode;
    }

    public int getInputPallet()
    {
        return inputPallet;
    }

    public int getOutputPallet()
    {
        return outputPallet;
    }

    public int getSpeed()
    {
        return speed;
    }

    public boolean isRunning()
    {
        return running;
    }

    public boolean isError()
    {
        return error;
    }

    /* Probably redundant at the moment */
    public void getInformationFromDb()
    {
        /* Dummy values */
        target = 90;
        producedItems = 50;
        running = true;
        robotId = "R.DANEEL";
        robotCellId = "StationShoes";
        productName = "Shoes";
        cycleTime = 30;
        inputPallet = 90;
        outputPallet = 60;
        startTime = System.currentTimeMillis()/1000;
        speed = 5;
        station = "Station 5";
    }

    public String formatTime(long time, boolean useTimezone)
    {
        if (time == 0)
            return "00:00:00";

        DateFormat df = new SimpleDateFormat("HH:mm:ss");
        df.setTimeZone(TimeZone.getTimeZone("UTC"));
        if (useTimezone)
            df.setTimeZone(TimeZone.getTimeZone("Europe/Stockholm"));

        Date dTime = new Date(time*1000);

        return df.format(dTime);
    }

    public void addItemToErrorList(String msg)
    {
        errorList.add(msg);
    }

    public HashMap<String,String> toHashMap() {
        HashMap<String,String> hashmap = new HashMap<>();

        hashmap.put("robotid", robotId);
        hashmap.put("station", station);
        hashmap.put("productname", productName);
        hashmap.put("produceditems", Integer.toString(producedItems));
        hashmap.put("outputpallet", Integer.toString(outputPallet));
        hashmap.put("inputpallet", Integer.toString(inputPallet));


        return hashmap;
    }
}
