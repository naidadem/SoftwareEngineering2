package se.opiflex.opiflexindustrialmonitoring;

import android.content.Context;
import android.content.SharedPreferences;

public class Settings {

    private SharedPreferences settings;

    public Settings(Context ctx)
    {
        settings = ctx.getSharedPreferences("settings", 0);
    }

    public void saveSettings(String id, boolean checked)
    {
        SharedPreferences.Editor editor = settings.edit();

        editor.putBoolean(id, checked);
        editor.apply();
    }

    public SharedPreferences loadSettings()
    {
        return settings;
    }

}
