package in.balamt.app.udhavi.services;

import android.app.Service;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.media.AudioManager;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.slider.Slider;

import in.balamt.app.udhavi.R;
import in.balamt.app.udhavi.admin.DevicePowerManagerAdmin;
import in.balamt.app.udhavi.util.UdhaviUtil;

public class UdhaviFloatingWidgetService extends Service implements View.OnClickListener {

    private WindowManager mWindowManager;
    private AudioManager mAudioManager;
    private View mOverlayView;
    private View mCollapseView, mExpandView, mButtonSimplifiedCoding, mVolumeSliderLayout;

    private ImageButton mExpandBtn;
    private ImageView mCloseButton, mPowerBtn, mLockBtn, mHomeBtn, mVolumeBtn, mRingerBtn;

    private Slider mVolumeSlider;
    private TextView mVolumeText;
    private boolean isRingerVolume = false;
    private boolean isRingerSliderVisible = false;

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

        mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);

        final WindowManager.LayoutParams params = getLayoutParams();

        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        mWindowManager.addView(mOverlayView, params);

        mCollapseView = mOverlayView.findViewById(R.id.layoutCollapsed);
        mExpandView = mOverlayView.findViewById(R.id.layoutExpanded);

        mButtonSimplifiedCoding = mExpandView.findViewById(R.id.buttonSimplifiedCoding);

        mVolumeSliderLayout = mExpandView.findViewById(R.id.volumeSliderLayout);
        mVolumeText = (TextView) mVolumeSliderLayout.findViewById(R.id.volumeValue);

        mVolumeSlider = (Slider) mVolumeSliderLayout.findViewById(R.id.volumeSlider);

        mVolumeSlider.addOnSliderTouchListener(new Slider.OnSliderTouchListener() {
            @Override
            public void onStartTrackingTouch(@NonNull Slider slider) {

            }

            @Override
            public void onStopTrackingTouch(@NonNull Slider slider) {
                updateVolumeText(slider.getValue());
            }
        });

        mVolumeSlider.addOnChangeListener(new Slider.OnChangeListener() {
            @Override
            public void onValueChange(@NonNull Slider slider, float value, boolean fromUser) {
                if(isRingerVolume) {
                    mAudioManager.setStreamVolume(AudioManager.STREAM_RING, Math.round(value),
                            AudioManager.FLAG_SHOW_UI);
                }else{
                    mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, Math.round(value),
                            AudioManager.FLAG_PLAY_SOUND | AudioManager.FLAG_SHOW_UI);
                }
            }
        });

        mVolumeSlider.addOnSliderTouchListener(new Slider.OnSliderTouchListener() {
            @Override
            public void onStartTrackingTouch(@NonNull Slider slider) {

            }

            @Override
            public void onStopTrackingTouch(@NonNull Slider slider) {
                //toggleVolumeSlider();
            }
        });

        mCloseButton = (ImageView) mExpandView.findViewById(R.id.closeBtn);
        mCloseButton.setOnClickListener(this);

        mPowerBtn = (ImageView) mButtonSimplifiedCoding.findViewById(R.id.powerBtn);
        mPowerBtn.setOnClickListener(this);

        mLockBtn = (ImageView) mButtonSimplifiedCoding.findViewById(R.id.lockBtn);
        mLockBtn.setOnClickListener(this);

        mHomeBtn = (ImageView) mButtonSimplifiedCoding.findViewById(R.id.homeBtn);
        mHomeBtn.setOnClickListener(this);

        mVolumeBtn = (ImageView) mButtonSimplifiedCoding.findViewById(R.id.volumeBtn);
        mVolumeBtn.setOnClickListener(this);

        mRingerBtn = (ImageView) mButtonSimplifiedCoding.findViewById(R.id.ringerBtn);
        mRingerBtn.setOnClickListener(this);

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

        mExpandBtn = (ImageButton) mCollapseView.findViewById(R.id.expandBtn);
        mExpandBtn.setOnClickListener(this);

        mExpandBtn.setOnTouchListener(new View.OnTouchListener() {
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
                        mCollapseView.setVisibility(View.GONE);
                        mExpandView.setVisibility(View.VISIBLE);
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

        mCollapseView.setOnTouchListener(new View.OnTouchListener() {
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

        UdhaviUtil.showToast(getApplicationContext(),
                "You have clicked the Udhavi App Floater",
                Toast.LENGTH_LONG).show();

    }

    private void updateVolumeText(float slider) {
        mVolumeText.setText(String.valueOf(Math.round(slider)));
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
            case R.id.volumeBtn:
                isRingerVolume = false;
                isRingerSliderVisible = false;
                adjustVolume(AudioManager.STREAM_MUSIC);
                toggleVolumeSlider();
                break;
            case R.id.ringerBtn:
                adjustVolume(AudioManager.STREAM_RING);
                isRingerVolume = true;
                toggleVolumeSlider();
                break;
            case R.id.expandBtn:
                mCollapseView.setVisibility(View.GONE);
                mExpandView.setVisibility(View.VISIBLE);
                break;
            case R.id.closeBtn:
                mExpandView.setVisibility(View.GONE);
                mCollapseView.setVisibility(View.VISIBLE);
                break;
            case R.id.homeBtn:
                Intent startMain = new Intent(Intent.ACTION_MAIN);
                startMain.addCategory(Intent.CATEGORY_HOME);
                startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(startMain);
                break;
            case R.id.lockBtn:
                if(active){
                    UdhaviUtil.showToast(getApplicationContext(),
                            "Lock Activated",
                            Toast.LENGTH_SHORT).show();
                    deviceManger
                            .lockNow();
                }
                break;
        }
    }

    private void adjustVolume(int streamMusic) {
        mVolumeSlider.setValueTo(mAudioManager.getStreamMaxVolume(streamMusic));
        mVolumeSlider.setValue(mAudioManager.getStreamVolume(streamMusic));
        updateVolumeText(mAudioManager.getStreamVolume(streamMusic));
    }

    private void toggleVolumeSlider() {
        if(isRingerSliderVisible){
            mVolumeSliderLayout.setVisibility(View.GONE);
            isRingerSliderVisible = false;
            return;
        }else{
            isRingerSliderVisible = true;
        }

        if (mVolumeSliderLayout.getVisibility() == View.GONE) {
            mVolumeSliderLayout.setVisibility(View.VISIBLE);
        } else {
            mVolumeSliderLayout.setVisibility(View.GONE);
        }
    }
}
