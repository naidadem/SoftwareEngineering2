package se.opiflex.opiflexindustrialmonitoring.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import se.opiflex.opiflexindustrialmonitoring.BottomNavbar;
import se.opiflex.opiflexindustrialmonitoring.DatabaseConnection;
import se.opiflex.opiflexindustrialmonitoring.Error;
import se.opiflex.opiflexindustrialmonitoring.R;
import se.opiflex.opiflexindustrialmonitoring.lvAdapter;


public class ErrorLogActivity extends AppCompatActivity {

    public ListView lv;
    public lvAdapter adapter;
    public List<Error> mErrorList;
    DatabaseConnection db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_error_log);

        lv = (ListView) findViewById(R.id.ErrorList);

        Bundle extras = getIntent().getExtras();
        if (extras != null && extras.getString("companyid") != null) {
            db = new DatabaseConnection(extras.getString("companyid"));
        } else {
            db = new DatabaseConnection();
        }

        /* Init bottom navbar */
        BottomNavbar navbar = new BottomNavbar(this, R.id.Error_log_Button, db.getCompanyId());
        navbar.setupClickListeners(ErrorLogActivity.this);

        mErrorList = new ArrayList<Error>();

        db.onLogsChange(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                ArrayList<Error> errors = new ArrayList<>();

                for(DocumentSnapshot snapshot : documentSnapshots) {
                    errors.add(new Error(snapshot));
                }

                Collections.sort(errors);

                mErrorList.clear();

                mErrorList.addAll(errors);

                adapter = new lvAdapter(getApplicationContext(), mErrorList);
                lv.setAdapter(adapter);
                lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Error tmp = mErrorList.get(position);
                        Intent displayErrorNotes = new Intent(ErrorLogActivity.this, ErrorDetailActivity.class);
                        displayErrorNotes.putExtra("error_id", tmp.getId());
                        displayErrorNotes.putExtra("error_desc", tmp.getErrorMessage());
                        displayErrorNotes.putExtra("companyid",db.getCompanyId());
                        startActivity(displayErrorNotes);
                    }

                });
            }
        });
    }
}


