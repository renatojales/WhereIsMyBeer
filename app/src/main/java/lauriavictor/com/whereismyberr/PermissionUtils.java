package lauriavictor.com.whereismyberr;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is found in the android documentation.
 */

public class PermissionUtils {

    //Requesting permission.
    public static boolean validate(Activity activity, int requestCode, String... permissions) {
        List<String> list = new ArrayList<>();
        for(String permission : permissions) {

            //Validate permission.
            boolean ok = ContextCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED;
            if(!ok) {
                list.add(permission);
            }
        }
        if(list.isEmpty()) {
            //if ok, return true.
            return true;
        }

        //Unspecified list of permissions.
        String[] newPermissions = new String[list.size()];
        list.toArray(newPermissions);

        //Requesting permission.
        ActivityCompat.requestPermissions(activity, newPermissions, 1);

        return false;
    }
}
