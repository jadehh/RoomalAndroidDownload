package cn.sddman.download.util;

import android.app.Activity;
import android.content.Context;

import com.irozon.sneaker.Sneaker;

import org.xutils.x;

import java.text.DecimalFormat;

import cn.sddman.download.R;
import cn.sddman.download.common.Const;

public class Util {

    public static void alert(Activity activity, String msg, int msgType){
        if(Const.ERROR_ALERT==msgType) {
            Sneaker.with(activity)
                    .setTitle(activity.getResources().getString(R.string.title_dialog), R.color.white)
                    .setMessage(msg, R.color.white)
                    .setDuration(2000)
                    .autoHide(true)
                    .setIcon(R.drawable.ic_error, R.color.white, false)
                    .sneak(R.color.colorAccent);
        }else if(Const.SUCCESS_ALERT==msgType) {
            Sneaker.with(activity)
                    .setTitle(activity.getResources().getString(R.string.title_dialog), R.color.white)
                    .setMessage(msg, R.color.white)
                    .setDuration(2000)
                    .autoHide(true)
                    .setIcon(R.drawable.ic_success, R.color.white, false)
                    .sneak(R.color.success);
        }else if(Const.WARNING_ALERT==msgType) {
            Sneaker.with(activity)
                    .setTitle(activity.getResources().getString(R.string.title_dialog), R.color.white)
                    .setMessage(msg, R.color.white)
                    .setDuration(2000)
                    .autoHide(true)
                    .setIcon(R.drawable.ic_warning, R.color.white, false)
                    .sneak(R.color.warning);
        }
    }

}
