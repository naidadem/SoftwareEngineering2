package se.opiflex.opiflexindustrialmonitoring;

/* This class is not currently used, push notifications are handled
 * by the Firebase server */

public class PushNotification
{
    private String robotId;
    private String message;
    private int type;

    public PushNotification(String robotId, String message, int type)
    {
        this.robotId = robotId;
        this.message = message;
        this.type = type;

        sendNotification();
    }

    private void sendNotification()
    {

    }
}
