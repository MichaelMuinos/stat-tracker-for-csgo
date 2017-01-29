package sharedpreferences;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPref {

    private static final String SHARED_PREFS_NAME = "SHARED_PREFS";

    private static SharedPref sharedPref = null;
    private static SharedPreferences sharedPreferences;
    private static SharedPreferences.Editor editor;

    protected SharedPref() {
        // Defeat instantiation
    }

    public static SharedPref getInstance(Context context) {
        if(sharedPref == null) {
            // create new instance of our class
            sharedPref = new SharedPref();
            // instantiate shared preferences
            sharedPreferences = context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE);
            editor = sharedPreferences.edit();
        }
        return sharedPref;
    }

    // add boolean value
    public void addValue(String name, boolean val) {
        editor.putBoolean(name, val);
        editor.commit();
    }

    // get boolean value
    public boolean getValue(String name) {
        // default = false
        return sharedPreferences.getBoolean(name, false);
    }

}
