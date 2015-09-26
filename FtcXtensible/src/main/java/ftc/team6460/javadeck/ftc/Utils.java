package ftc.team6460.javadeck.ftc;

import android.content.SharedPreferences;

/**
 * Created by hexafraction on 9/23/15.
 */
public class Utils {
    public static double safeDouble(String s, double d){
        try{
            return Double.parseDouble(s);
        } catch (Exception e){
            return d;
        }
    }
    public static int safeInt(String s, int d){
        try{
            return Integer.parseInt(s);
        } catch (Exception e){
            return d;
        }
    }

    public static double getSafeDoublePref(String s, SharedPreferences sp, double d){
        return safeDouble(sp.getString(s, Double.toString(d)), d);
    }

    public static int getSafeIntPref(String s, SharedPreferences sp, int d){
        return safeInt(sp.getString(s, Integer.toString(d)), d);
    }


}
