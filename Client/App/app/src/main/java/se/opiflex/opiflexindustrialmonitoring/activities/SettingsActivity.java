package se.opiflex.opiflexindustrialmonitoring.activities;

import android.content.Context;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Toast;

import se.opiflex.opiflexindustrialmonitoring.BottomNavbar;
import se.opiflex.opiflexindustrialmonitoring.R;
import se.opiflex.opiflexindustrialmonitoring.Settings;

public class SettingsActivity extends AppCompatActivity {

    private Settings settings;
    private BottomNavigationView navbar;
    private boolean allSelected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        navbar = findViewById(R.id.navigation);

        settings = new Settings(this);
        loadCheckboxState();
        /* Init bottom navbar */
        BottomNavbar navbar = new BottomNavbar(this, R.id.Main_menu_Button,getIntent().getExtras().getString("companyid"));
        navbar.setupClickListeners(SettingsActivity.this);

        final Button selectAll = findViewById(R.id.bt_selectAll);
        final Button save = findViewById(R.id.bt_save);

        selectAll.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                toggleCheckboxes();
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                saveCheckboxState();
            }
        });
    }

    private void toggleCheckboxes()
    {
        LinearLayout robotInformation = findViewById(R.id.ll_displayInformation);

        allSelected = !allSelected;

        for (int i = 0; i < robotInformation.getChildCount(); i++)
        {
            View v = robotInformation.getChildAt(i);

            if (v instanceof CheckBox)
            {
                if (allSelected)
                {
                    ((CheckBox) v).setChecked(true);
                }
                else
                {
                    ((CheckBox) v).setChecked(false);
                }
            }
        }
    }

    private void saveCheckboxState()
    {
        LinearLayout robotInformation = findViewById(R.id.ll_displayInformation);
        CheckBox tmp;
        String id;

        Context context = getApplicationContext();
        CharSequence toastText = "Settings saved";
        Toast saved = Toast.makeText(context, toastText, Toast.LENGTH_SHORT);

        for (int i = 0; i < robotInformation.getChildCount(); i++)
        {
            View v = robotInformation.getChildAt(i);

            if (v instanceof CheckBox)
            {
                tmp = ((CheckBox) v);
                id = tmp.getTag().toString();
                settings.saveSettings(id, tmp.isChecked());
            }
        }
        saved.show();
    }

    private void loadCheckboxState()
    {
        LinearLayout robotInformation = findViewById(R.id.ll_displayInformation);
        String id;
        CheckBox tmp;

        for (int i = 0; i < robotInformation.getChildCount(); i++)
        {
            View v = robotInformation.getChildAt(i);

            if (v instanceof CheckBox)
            {
                tmp = ((CheckBox) v);
                id = tmp.getTag().toString();
                tmp.setChecked(settings.loadSettings().getBoolean(id, false));
            }
        }
    }
}
