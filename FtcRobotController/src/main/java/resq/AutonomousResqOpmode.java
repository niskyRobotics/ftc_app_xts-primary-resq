package resq;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by hexafraction on 9/26/15.
 */
public class AutonomousResqOpmode extends RockerBogieCommon{

    long nanoTimeStart = -1;
    enum Color {
        RED, BLUE, INVALID
    }
    Color color;
    @Override
    public void init() {
        super.init();
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this.hardwareMap.appContext);
        color = Color.valueOf(sharedPref.getString("auton_team_color", "INVALID").replaceAll("\"", ""));
    }

    @Override
    public void loop() {
        if(nanoTimeStart==-1){
            nanoTimeStart = System.nanoTime();

        }
    }
}
