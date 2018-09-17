package se.opiflex.opiflexindustrialmonitoring.activities;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ListView;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;

import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import se.opiflex.opiflexindustrialmonitoring.BottomNavbar;
import se.opiflex.opiflexindustrialmonitoring.DatabaseConnection;
import se.opiflex.opiflexindustrialmonitoring.Note;
import se.opiflex.opiflexindustrialmonitoring.R;

public class ErrorDetailActivity extends AppCompatActivity {

    private long updatePeriod = 5;

    private TextView tv_errorNumber;
    private TextView tv_description;
    private TextView tv_notes;
    private TextView tv_errorNo;
    private ListView lv_notes;
    private EditText et_editNote;
    private Button b_sendNote;

    DatabaseConnection db = new DatabaseConnection();
    private String error_id;
    private String error_desc;
    private ArrayList<Note> noteObjects = new ArrayList<Note>();
    private ArrayList<String> notes = new ArrayList<>();
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_error_detail);

        /* Initialize all views from layout  */
        tv_errorNumber = findViewById(R.id.tv_errorNumber);
        tv_description = findViewById(R.id.tv_description);
        tv_notes = findViewById(R.id.tv_notes);
        tv_errorNo = findViewById(R.id.tv_errorNo);
        lv_notes = findViewById(R.id.lv_notes);
        et_editNote= findViewById(R.id.editNote);
        b_sendNote= findViewById(R.id.sendNote);

        /* Get company id from initiating activity */
        Bundle extras = getIntent().getExtras();
        if (extras != null && extras.getString("companyid") != null) {
            db = new DatabaseConnection(extras.getString("companyid"));
        }
        else {
            db = new DatabaseConnection();
        }

        /* Get error id and desc from RobotDetailActivity */
        if (extras != null && extras.getString("error_desc") != null) {
            error_desc = extras.getString("error_desc");
        }
        else {
            error_desc = "";
        }
        if (extras != null && extras.getString("error_desc") != null) {
            error_id = extras.getString("error_id");
        }
        else {
            error_id = "";
        }

        /* Init bottom navbar */
        BottomNavbar navbar = new BottomNavbar(this, R.id.Main_menu_Button, db.getCompanyId());
        navbar.setupClickListeners(ErrorDetailActivity.this);

        /* Setup scheduled task to update notes */
        ScheduledExecutorService scheduleUpdateInfo = Executors.newSingleThreadScheduledExecutor();
        scheduleUpdateInfo.scheduleAtFixedRate(updateNoteInformation(), 0, updatePeriod, TimeUnit.MINUTES);

        updateFields();

        /* Long press on note to delete it */
        lv_notes.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener()
        {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l)
            {
                Toast deleted = Toast.makeText(ErrorDetailActivity.this, R.string.errordetail_deleted, Toast.LENGTH_LONG);

                Note tmp = noteObjects.get(i);
                deleteNote(tmp.getNoteId());
                noteObjects.remove(i);
                notes.remove(i);
                adapter.notifyDataSetChanged();

                deleted.show();
                return false;
            }
        });

        b_sendNote.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Code here executes on main thread after user presses button
                db.pushNote(error_id, et_editNote.getText().toString()).addOnCompleteListener(new OnCompleteListener<DocumentReference>()
                {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task)
                    {
                        if (task.isSuccessful())
                        {
                            updateNoteInformation().run();
                            et_editNote.setText("");
                        }
                        else
                        {
                            Log.e("ErrorDetailActivity", "pushNote didn't worked! " + task.getException().getMessage());
                        }
                    }
                });
            }
        });
    }

    private void updateFields() {
        tv_errorNo.setText(error_id);
        tv_description.setText(error_desc);
    }

    private void deleteNote(final String noteId)
    {
        db.deleteErrorNote(error_id, noteId).addOnCompleteListener(new OnCompleteListener<Void>()
        {
            @Override
            public void onComplete(@NonNull Task<Void> task)
            {
                if (task.isSuccessful())
                {
                    updateNoteInformation();
                }
                else
                {
                    Log.e("RobotListActivity", "Deleting note failed! " + task.getException().getMessage());
                }
            }
        });
    }

    private Runnable updateNoteInformation() {
        return new Runnable() {
            @Override
            public void run() {
                db.getNotesForError(error_id).addOnCompleteListener(new OnCompleteListener<ArrayList<Note>>()
                {
                    @Override
                    public void onComplete(@NonNull Task<ArrayList<Note>> task)
                    {
                        if (task.isSuccessful())
                        {
                            notes.clear();
                            noteObjects.clear();

                            noteObjects.addAll(task.getResult());

                            /* Create a copy of noteObjects with just messages for the ListView */
                            for (Note item : noteObjects)
                            {
                                notes.add(item.getMessage());
                            }

                            /*Set content of noteObjects*/
                            adapter = new ArrayAdapter<>(ErrorDetailActivity.this, android.R.layout.simple_list_item_1, notes);

                            lv_notes.setAdapter(adapter);
                            adapter.notifyDataSetChanged();

                        } else{
                            Log.e("ErrorDetailActivity", "GetNotesForError didn't worked! " + task.getException().getMessage());
                        }
                    }
                });
            }
        };
    }
}