package myapp.lenovo.httpclient;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;

/**
 * Created by Lenovo on 2017/1/27.
 */

public class DensityUtils {
    private static float destiny=-1F;

    private static float getDensity(Context context){
        if(destiny<=0F){
            destiny=context.getResources().getDisplayMetrics().density;
        }
        return destiny;
    }
    public static int dipToPx(Context context,float dp){
        return (int)(dp*getDensity(context)+0.5F);
    }

    public static int getScreenHeight(Context context){
        DisplayMetrics dm=new DisplayMetrics();
        WindowManager wm= (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(dm);
        return dm.heightPixels;
    }

    public static int getScreenWidth(Context context){
        DisplayMetrics dm=new DisplayMetrics();
        WindowManager wm= (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(dm);
        return dm.widthPixels;
    }

}
