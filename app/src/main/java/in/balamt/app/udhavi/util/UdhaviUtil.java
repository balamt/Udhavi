package in.balamt.app.udhavi.util;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class UdhaviUtil {

    private UdhaviUtil(){}

    public static Toast showToast(Context context, String message, int duration)
    {
        return Toast.makeText(context,
                message,
                Toast.LENGTH_LONG);
    }
}
