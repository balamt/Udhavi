package in.balamt.app.udhavi.services;

import android.app.Service;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.media.Image;
import android.os.Handler;
import android.os.IBinder;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import in.balamt.app.udhavi.MainActivity;
import in.balamt.app.udhavi.R;
import in.balamt.app.udhavi.admin.DevicePowerManagerAdmin;

public class UdhaviFloatingWidgetService extends Service implements View.OnClickListener {

    private WindowManager mWindowManager;
    private View mOverlayView;
    private View mCollapseView, mExpandView, mButtonSimplifiedCoding;

    private Button mExpandBtn;
    private ImageView mCloseButton, mPowerBtn;

    DevicePolicyManager deviceManger;
    ComponentName compName;
    boolean active;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        setTheme(R.style.Theme_Udhavi);

        mOverlayView = LayoutInflater.from(this).inflate(R.layout.floater_new, null);
//        mFloaterPanelLayout = mOverlayView.findViewById(R.id.floaterPanelLayout);
        deviceManger = (DevicePolicyManager)
                getSystemService(Context. DEVICE_POLICY_SERVICE );
        compName = new ComponentName( this, DevicePowerManagerAdmin.class );
        active = deviceManger.isAdminActive( compName );


        final WindowManager.LayoutParams params = getLayoutParams();

        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        mWindowManager.addView(mOverlayView, params);

        mCollapseView = mOverlayView.findViewById(R.id.layoutCollapsed);
        mExpandView = mOverlayView.findViewById(R.id.layoutExpanded);
        mButtonSimplifiedCoding = mExpandView.findViewById(R.id.buttonSimplifiedCoding);
        mCloseButton = (ImageView) mButtonSimplifiedCoding.findViewById(R.id.closeBtn);
        mCloseButton.setOnClickListener(this);
        mPowerBtn = (ImageView) mButtonSimplifiedCoding.findViewById(R.id.powerBtn);
        mPowerBtn.setOnClickListener(this);

        mOverlayView.findViewById(R.id.buttonClose).setOnClickListener(this);
        mExpandView.setOnClickListener(this);

        mOverlayView.findViewById(R.id.relativeLayoutParent).setOnTouchListener(new View.OnTouchListener() {
            private int initialX;
            private int initialY;
            private float initialTouchX;
            private float initialTouchY;

            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        initialX = params.x;
                        initialY = params.y;
                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();
                        return true;
                    case MotionEvent.ACTION_UP:
//                        mCollapseView.setVisibility(View.GONE);
//                        mExpandView.setVisibility(View.VISIBLE);
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        params.x = initialX + (int) (event.getRawX() - initialTouchX);
                        params.y = initialY + (int) (event.getRawY() - initialTouchY);
                        mWindowManager.updateViewLayout(mOverlayView, params);
                        return true;
                }

                return false;
            }
        });

        mExpandBtn = (Button) mCollapseView.findViewById(R.id.expandBtn);
        mExpandBtn.setOnClickListener(this);
        Toast toast = Toast.makeText(getApplicationContext(),
                "Hello This is Udhavi App Toast",
                Toast.LENGTH_LONG);
        toast.show();
    }

    @NonNull
    private WindowManager.LayoutParams getLayoutParams() {
        final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);
        return params;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mOverlayView != null){
            mWindowManager.removeView(mOverlayView);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.expandBtn:
                Toast toast = Toast.makeText(getApplicationContext(),
                        "You have clicked the Udhavi App Floater",
                        Toast.LENGTH_LONG);
                toast.show();
                mCollapseView.setVisibility(View.GONE);
                mExpandView.setVisibility(View.VISIBLE);


                break;
            case R.id.closeBtn:
                mExpandView.setVisibility(View.GONE);
                mCollapseView.setVisibility(View.VISIBLE);
                break;
            case R.id.buttonClose:
                stopSelf();
                break;
            case R.id.powerBtn:
                Toast toast1 = Toast.makeText(getApplicationContext(),
                        "You have clicked the Udhavi App Floater",
                        Toast.LENGTH_LONG);
                toast1.show();
                if(active){
                    Toast toast2 = Toast.makeText(getApplicationContext(),
                            "You have clicked the Udhavi App Floater",
                            Toast.LENGTH_LONG);
                    toast2.show();
                    deviceManger
                            .lockNow();
                }
                break;
        }
    }
}
