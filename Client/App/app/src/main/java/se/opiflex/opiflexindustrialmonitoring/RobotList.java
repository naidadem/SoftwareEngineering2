package se.opiflex.opiflexindustrialmonitoring;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class RobotList
{
    ArrayList<HashMap<String, String>>  Robot_List = new ArrayList<>();

    private Robot Test = new Robot();
    public int timeout = 5;

    /*Main function to use for filling the list, this one is configured to handle a JSON object, switch to handle an Arraylist<Robot>>*/
    public ArrayList<HashMap<String,String>>  fillList () {

        String url = "Url to database";
        if (url != null) {
            try {
                JSONObject List = new JSONObject();
                JSONArray RobotList = (JSONArray) List.get("url");

                for (int i = 0; i < RobotList.length(); i++) {
                    JSONObject l = RobotList.getJSONObject(i);
                    String robotid = l.getString("robotid");
                    String robotcellid = l.getString("robotcellid");
                    String productname = l.getString("productname");
                    int starttime = l.getInt("starttime");
                    int currenttime = l.getInt("currenttime");
                    Boolean running = l.getBoolean("running");
                    int produced = l.getInt("produced");
                    int target = l.getInt("target");
                    int cycletime = l.getInt("cycletime");
                    String statin = l.getString("station");
                    Boolean error = l.getBoolean("error");
                    int eventcode = l.getInt("eventcode");
                    int inputpallet = l.getInt("inputpallet");
                    int outputpallet = l.getInt("outputpallet");
                    int speed = l.getInt("speed");
                    String log = l.getString("log");

                    HashMap<String, String> hashmap = new HashMap<>();
                    hashmap.put("robotid", robotid);
                    hashmap.put("robotcellid" , robotcellid);
                    hashmap.put("productname", productname);
                    hashmap.put("produceditems", Integer.toString(produced));
                    hashmap.put("outputpallet", Integer.toString(outputpallet));
                    hashmap.put("inputpallet", Integer.toString(inputpallet));

                    Robot_List.add(hashmap);
                }

            } catch (final JSONException e) {
                /* put error handling here for later*/
            }
        }
        return Robot_List;
    }



    /* Test function, use the modified above*/
    public ArrayList<HashMap<String,String>>  TestList () {


        Test.getInformationFromDb();
        String robotid = Test.getRobotId();
        String robotcellid = Test.getRobotCellId();
        String productname = Test.getProductName();
        long starttime = Test.getStartTime();
        long currenttime = Test.getCurrentTime();
        Boolean running = Test.isRunning();
        int produced = Test.getProducedItems();
        int target = Test.getTarget();
        int cycletime = Test.getCycleTime();
        String station = Test.getStation();
        Boolean error = Test.isError();
        int eventcode = Test.getEventCode();
        int inputpallet = Test.getInputPallet();
        int outputpallet = Test.getOutputPallet();
        int speed = Test.getSpeed();
        String log = Test.getLog();

        HashMap<String, String> hashmap =new HashMap<>();
        hashmap.put("robotid", robotid);
        hashmap.put("station" , station);
        hashmap.put("productname", productname);
        hashmap.put("produceditems", Integer.toString(produced));
        hashmap.put("outputpallet", Integer.toString(outputpallet));
        hashmap.put("inputpallet", Integer.toString(inputpallet));
        Robot_List.add(hashmap);
        Robot_List.add(hashmap);
        Robot_List.add(hashmap);
        Robot_List.add(hashmap);
        return Robot_List;
    }
    public String Get_ID (int position){
        String R = "";
        HashMap<String, String> iterator = new HashMap<>();
        iterator = Robot_List.get(position);
        R = iterator.get("robotid");

        return R;
    }

}
