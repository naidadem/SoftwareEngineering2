package se.opiflex.opiflexindustrialmonitoring.activities;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;

import se.opiflex.opiflexindustrialmonitoring.BottomNavbar;
import se.opiflex.opiflexindustrialmonitoring.DatabaseConnection;
import se.opiflex.opiflexindustrialmonitoring.R;
import se.opiflex.opiflexindustrialmonitoring.Robot;


public class RobotListActivity extends AppCompatActivity {

    //Message sent to RobotDetailActivity
    public static final String ROBOT_ID = "robot_id";

    // Views
    public ListView List;

    // Objects for functions
    HashMap<String, String> Temp;
    ArrayList<HashMap<String, String>> Robot_List;
    ArrayList<Robot> Robots;

    public ListAdapter ListAdp;
    private DatabaseConnection db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        Bundle extras = getIntent().getExtras();
        if (extras != null && extras.getString("companyid") != null) {
            db = new DatabaseConnection(extras.getString("companyid"));
        }
        else {
            db = new DatabaseConnection();
        }
        setContentView(R.layout.activity_robot__list);
        Temp = new HashMap<String, String>();
        List = findViewById(R.id.RobotList);
        List.setAdapter(ListAdp);
        List.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                Temp = (HashMap<String, String>) adapterView.getItemAtPosition(position);
                String Message = Temp.get("robotid");
                Intent intent = new Intent(RobotListActivity.this, RobotDetailActivity.class);
                intent.putExtra(ROBOT_ID, Message);
                intent.putExtra("companyid",db.getCompanyId());
                startActivity(intent);
            }
        });

        /* Init bottom navbar */
        BottomNavbar navbar = new BottomNavbar(this, R.id.Robot_Button,db.getCompanyId());
        navbar.setupClickListeners(RobotListActivity.this);

        db.onRobotListChange(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                ArrayList<HashMap<String, String>> robotlist = new ArrayList<>();
                for (DocumentSnapshot doc : documentSnapshots) {
                    robotlist.add(new Robot(doc).toHashMap());
                }
                Robot_List = robotlist;
                ListAdp = new SimpleAdapter(RobotListActivity.this, robotlist, R.layout.list_layout, new String[]{"robotid", "station", "productname","produceditems", "outputpallet", "inputpallet"},
                        new int[]{R.id.RobotID, R.id.Station, R.id.ProductName, R.id.ProducedItems, R.id.OutputPallet, R.id.InputPallet});
                List.setAdapter(ListAdp);
            }
        });
    }
}

