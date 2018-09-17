package se.opiflex.opiflexindustrialmonitoring;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.view.MenuItem;
import android.widget.PopupMenu;

import com.google.firebase.auth.FirebaseAuth;

import se.opiflex.opiflexindustrialmonitoring.activities.ErrorLogActivity;
import se.opiflex.opiflexindustrialmonitoring.activities.LoginActivity;
import se.opiflex.opiflexindustrialmonitoring.activities.RobotListActivity;
import se.opiflex.opiflexindustrialmonitoring.activities.SettingsActivity;

public class BottomNavbar {

    private BottomNavigationView navbar;
    private Activity activity;
    private Context context;
    private String companyId;

    public BottomNavbar(Activity caller, int itemId, String companyid)
    {
        this.activity = caller;
        navbar = activity.findViewById(R.id.navigation);
        navbar.setSelectedItemId(itemId);
        companyId = companyid;
    }

    public void setupClickListeners(final Context _context)
    {
        context = _context;
        navbar.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.Robot_Button:
                                Intent swapToList = new Intent(context, RobotListActivity.class);
                                swapToList.putExtra("companyid",companyId);
                                swapToList.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                activity.startActivity(swapToList);
                                break;
                            case R.id.Main_menu_Button:
                                showPopupMenu();
                                break;
                            case R.id.Error_log_Button:
                                Intent swapToLog = new Intent(context, ErrorLogActivity.class);
                                swapToLog.putExtra("companyid",companyId);
                                swapToLog.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                activity.startActivity(swapToLog);
                                break;
                        }
                        return false;
                    }
                });
    }

    private void showPopupMenu()
    {
        PopupMenu menu = new PopupMenu(context, navbar);
        menu.getMenuInflater().inflate(R.menu.popup_nav, menu.getMenu());

        menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.it_Settings)
                {
                    Intent swapToSettings = new Intent(context, SettingsActivity.class);
                    swapToSettings.putExtra("companyid",companyId);
                    swapToSettings.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    activity.startActivity(swapToSettings);
                }
                if(item.getItemId() == R.id.it_LogOut)
                {
                    FirebaseAuth mAuth = FirebaseAuth.getInstance();
                    mAuth.signOut();
                    Intent swapToLogout = new Intent(context, LoginActivity.class);
                    swapToLogout.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    activity.startActivity(swapToLogout);
                }
                return true;
            }
        });
        menu.show();
    }
}
