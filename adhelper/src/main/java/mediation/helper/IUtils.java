package mediation.helper;

import android.content.Context;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import android.util.Log;

public class IUtils {

    public static boolean isPackageInstalled(String pkgName, Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            packageManager.getPackageInfo(pkgName, PackageManager.GET_ACTIVITIES);

            return true;

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();


        }
        return false;
    }

    public static boolean isContainPkg(String pkgName) {
        if(TextUtils.isEmpty(pkgName) ||pkgName == null)
            return false;
        if (pkgName.contains("play.google.com/"))
            return true;
        else
            return false;

    }
    public static String getPackageName(String url){
        String[] gPkg = url.split("=");
        return gPkg[1];
    }
}
