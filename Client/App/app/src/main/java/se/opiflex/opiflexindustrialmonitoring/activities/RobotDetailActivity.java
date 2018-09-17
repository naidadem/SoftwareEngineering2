package se.opiflex.opiflexindustrialmonitoring.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import se.opiflex.opiflexindustrialmonitoring.BottomNavbar;
import se.opiflex.opiflexindustrialmonitoring.DatabaseConnection;
import se.opiflex.opiflexindustrialmonitoring.Error;
import se.opiflex.opiflexindustrialmonitoring.R;
import se.opiflex.opiflexindustrialmonitoring.Robot;
import se.opiflex.opiflexindustrialmonitoring.Settings;

public class RobotDetailActivity extends AppCompatActivity
{
    /* Parameters */
    private long updatePeriod = 5;

    /* Views */
    private ProgressBar pb_progress;
    private TextView tv_robotId;
    private TextView tv_robotCellId;
    private TextView tv_productName;
    private TextView tv_progress;
    private TextView tv_progresspercent;
    private TextView tv_timeleft;
    private TextView tv_input;
    private TextView tv_output;
    private TextView tv_status;
    private TextView tv_avgcycle;
    private TextView tv_speed;
    private TextView tv_starttime;
    private TextView tv_endtime;
    private TextView tv_station;
    private ListView lv_errors;

    /* Objects */
    DatabaseConnection db = new DatabaseConnection();
    private String robotid;
    private Robot robot;
    private ScheduledExecutorService scheduleUpdateInfo;
    ArrayList<Error> errors;

    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_robot);

        /* Initialize all views from layout  */
        pb_progress = findViewById(R.id.pb_progress);
        tv_robotId = findViewById(R.id.tv_robotId);
        tv_robotCellId = findViewById(R.id.tv_cell);
        tv_productName = findViewById(R.id.tv_productname);
        tv_progress = findViewById(R.id.tv_progress);
        tv_progresspercent = findViewById(R.id.tv_progresspercent);
        tv_timeleft = findViewById(R.id.tv_timeleftValue);
        tv_input = findViewById(R.id.tv_inputValue);
        tv_output = findViewById(R.id.tv_outputValue);
        tv_status = findViewById(R.id.tv_statusVal);
        tv_avgcycle = findViewById(R.id.tv_avgVal);
        tv_speed = findViewById(R.id.tv_speedVal);
        tv_starttime = findViewById(R.id.tv_startVal);
        tv_endtime = findViewById(R.id.tv_endVal);
        tv_station = findViewById(R.id.tv_stationVal);
        lv_errors = findViewById(R.id.lv_errors);

        //robot = new Robot();

        Bundle extras = getIntent().getExtras();
        if (extras != null && extras.getString("companyid") != null) {
            db = new DatabaseConnection(extras.getString("companyid"));
        }
        else {
            db = new DatabaseConnection();
        }

        /* Get robot id from RobotListActivity */
        Intent intent = getIntent();
        robotid = intent.getStringExtra(RobotListActivity.ROBOT_ID);

        /* Set up scheduled task to update information and error list */
        scheduleUpdateInfo = Executors.newSingleThreadScheduledExecutor();
        scheduleUpdateInfo.scheduleAtFixedRate(updateInformation(), 0, updatePeriod, TimeUnit.MINUTES);

        /* Init bottom navbar */
        BottomNavbar navbar = new BottomNavbar(this, R.id.Main_menu_Button,db.getCompanyId());
        navbar.setupClickListeners(RobotDetailActivity.this);

        lv_errors.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
            {

                Error tmp = errors.get(i);
                Intent displayErrorNotes = new Intent(RobotDetailActivity.this, ErrorDetailActivity.class);
                displayErrorNotes.putExtra("error_id", tmp.getId());
                displayErrorNotes.putExtra("error_desc", tmp.getErrorMessage());
                startActivity(displayErrorNotes);
            }
        });

        lv_errors.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener()
        {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l)
            {
                Error tmp = errors.get(i);
                markErrorAsSolved(tmp.getId());
                errors.remove(i);
                robot.getErrorList().remove(i);
                adapter.notifyDataSetChanged();

                return true;
            }
        });
    }

    @Override
    public void onResume()
    {
        super.onResume();
        loadItemDisplaySettings();
    }

    private void markErrorAsSolved(String errorId)
    {
        db.markErrorAsSolved(robotid, errorId).addOnCompleteListener(new OnCompleteListener<Void>()
        {
            @Override
            public void onComplete(@NonNull Task<Void> task)
            {
                if (task.isSuccessful())
                {
                    Toast solved = Toast.makeText(RobotDetailActivity.this, "Error solved", Toast.LENGTH_SHORT);
                    solved.show();
                }
                else {
                    Log.e("RobotListActivity", "Could not mark as solved! " + task.getException().getMessage());
                }
            }
        });
    }

    private Runnable updateInformation()
    {
        return new Runnable()
        {
            @Override
            public void run() {
                
                db.onRobotByIdChange(robotid, new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {
                        robot = new Robot(documentSnapshot);
                        updateFields();
                        loadItemDisplaySettings();
                        //BELOW : to improve, if a new error arise, it will not be updated automatically
                        db.getLogsForRobot(robotid).addOnCompleteListener(new OnCompleteListener<ArrayList<Error>>() {
                            @Override
                            public void onComplete(@NonNull Task<ArrayList<Error>> task) {
                                if (task.isSuccessful()) {
                                    errors = task.getResult();

                                    for (Error error : errors) {
                                        robot.addItemToErrorList(error.getErrorMessage());
                                    }
                                    adapter = new ArrayAdapter<String>(RobotDetailActivity.this, android.R.layout.simple_list_item_1, robot.getErrorList());
                                    lv_errors.setAdapter(adapter);
                                    adapter.notifyDataSetChanged();
                                }
                                else {
                                    Log.e("RobotDetailActivity", "GetLogsForRobot didn't worked! " + task.getException().getMessage());
                                }
                            }
                        });
                    }
                });

            }

        };
    }

    private void updateFields()
    {
        /* Set actionbar title */
        setTitle(getString(R.string.robot_actionbar, robot.getRobotId()));

        String robotId = robot.getRobotId();
        String robotCellId = robot.getRobotCellId();
        String productName = robot.getProductName();
        String station = robot.getStation(); //Unsure what "station" means at the moment.
        String log = robot.getLog();
        long startTime = robot.getStartTime(); //JSON specifies int, but int is too small for UNIX time.
        long currentTime = robot.getCurrentTime();
        int producedItems = robot.getProducedItems();
        int target = robot.getTarget();
        int cycleTime = robot.getCycleTime();
        int eventCode = robot.getEventCode();
        int inputPallet = robot.getInputPallet();
        int outputPallet = robot.getOutputPallet();
        int speed = robot.getSpeed();
        boolean running = robot.isRunning();
        long timeLeft;
        int progressPercent;

        int inputWarning = 10;

        int inputLeft = inputPallet - producedItems;

        /* Default values */
        tv_status.setText(getString(R.string.robot_statusValueFalse));
        tv_input.setTextColor(Color.rgb(105,155,6));
        tv_output.setTextColor(Color.rgb(105,155,6));

        if (running)
        {
            tv_status.setText(getString(R.string.robot_statusValueTrue));
        }

        /* Change text color to red if near limit */
        if (inputLeft <= inputWarning)
        {
            tv_input.setTextColor(Color.rgb(218, 97, 97));
        }
        if (producedItems >= (outputPallet - inputWarning))
        {
            tv_output.setTextColor(Color.rgb(218, 97, 97));
        }

        /* Calculate time left for full output pallet if producing until stopped */
        if (target == -1)
            target = outputPallet;


        timeLeft = (target - producedItems) * (long)cycleTime;
        progressPercent = (int)(((float)producedItems/(float)target)*100f);

        pb_progress.setProgress(progressPercent);
        tv_progresspercent.setText(getString(R.string.robot_progresspercent, progressPercent));
        tv_timeleft.setText(robot.formatTime(timeLeft, false));
        tv_progress.setText(getString(R.string.robot_progress, producedItems, target));
        tv_robotId.setText(robotId);
        tv_robotCellId.setText(robotCellId);
        tv_productName.setText(productName);
        tv_input.setText(getString(R.string.robot_inputValue, inputLeft, inputPallet));
        tv_output.setText(getString(R.string.robot_inputValue, producedItems, outputPallet));
        tv_avgcycle.setText(getString(R.string.robot_avgCycleValue, cycleTime));
        tv_starttime.setText(robot.formatTime(startTime, true));
        tv_endtime.setText(robot.formatTime(startTime + timeLeft, true));
        tv_speed.setText(Integer.toString(speed));
        tv_station.setText(station);

        loadItemDisplaySettings();
    }

    private void loadItemDisplaySettings()
    {
        Settings settings = new Settings(this);
        SharedPreferences displaySettings = settings.loadSettings();

        TableLayout items = findViewById(R.id.tl_information);

        for (int i = 0; i < items.getChildCount(); i++)
        {
            View v = items.getChildAt(i);
            String tag = v.getTag().toString();

            switch (tag)
            {
                case "status":
                    v.setVisibility(displaySettings.getBoolean(tag, false) ? View.VISIBLE : View.GONE);
                    break;
                case "station":
                    v.setVisibility(displaySettings.getBoolean(tag, false) ? View.VISIBLE : View.GONE);
                    break;
                case "cycle":
                    v.setVisibility(displaySettings.getBoolean(tag, false) ? View.VISIBLE : View.GONE);
                    break;
                case "speed":
                    v.setVisibility(displaySettings.getBoolean(tag, false) ? View.VISIBLE : View.GONE);
                    break;
                case "starttime":
                    v.setVisibility(displaySettings.getBoolean(tag, false) ? View.VISIBLE : View.GONE);
                    break;
                case "endtime":
                        v.setVisibility(displaySettings.getBoolean(tag, false) ? View.VISIBLE : View.GONE);
                    break;
                default:
                    v.setVisibility(View.VISIBLE);
            }

        }
    }
}
