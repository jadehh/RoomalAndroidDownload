package cn.sddman.download.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.aplayer.aplayerandroid.APlayerAndroid;
import com.coorchice.library.SuperTextView;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import cn.sddman.download.R;
import cn.sddman.download.common.BaseActivity;
import cn.sddman.download.common.Const;
import cn.sddman.download.mvp.p.PlayerPresenter;
import cn.sddman.download.mvp.p.PlayerPresenterImp;
import cn.sddman.download.mvp.v.PlayerView;
import cn.sddman.download.thread.HidePlayControl;
import cn.sddman.download.thread.UpdataUIPlayHandler;
import cn.sddman.download.thread.UpdatePlayUIProcess;
import cn.sddman.download.util.APlayerSationUtil;
import cn.sddman.download.util.FileTools;
import cn.sddman.download.util.SystemConfig;
import cn.sddman.download.util.TimeUtil;
import cn.sddman.download.util.Util;
import cn.sddman.download.view.PlayerPopupWindow;

import static com.aplayer.aplayerandroid.APlayerAndroid.CONFIGID.READPOSITION;

@ContentView(R.layout.activity_player)
public class PlayerActivity extends BaseActivity implements PlayerView{
    @ViewInject(R.id.play_view_root)
    private RelativeLayout mRootView;
    @ViewInject(R.id.fullscreen_content)
    private SurfaceView surfaceView;
    @ViewInject(R.id.top_panel)
    private RelativeLayout topPanel;
    @ViewInject(R.id.bottom_panel)
    private LinearLayout bottomPanel;
    @ViewInject(R.id.play_time)
    private TextView playTime;
    @ViewInject(R.id.play_seek_bar)
    private SeekBar mSeekBarPlayProgress;
    @ViewInject(R.id.play_pause)
    private ImageView playPause;
    @ViewInject(R.id.close_view)
    private SuperTextView closeView;
    @ViewInject(R.id.battery_icon)
    private ImageView batteryIcon;
    @ViewInject(R.id.system_time)
    private TextView systemTime;

    private String videoPath;
    private PlayerPresenter playerPresenter;
    private APlayerAndroid aPlayer = null;
    private PlayerPopupWindow mPopupWindow = null;
    private Thread mUpdateThread = null;
    private Handler handle=null;
    private HidePlayControl hidePlayControl;
    private volatile boolean mIsTouchingSeekbar = false;
    private boolean mIsNeedUpdateUIProgress = false;   //标志位，是否需要更新播放进度
    private boolean mVisible=false;
    private float downX,downY;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        Intent getIntent = getIntent();
        videoPath=getIntent.getStringExtra("videoPath");
        if(null==videoPath || !FileTools.exists(videoPath)){
            Util.alert(this,"视频不存在",Const.ERROR_ALERT);
            return;
        }
        playerPresenter=new PlayerPresenterImp(this,aPlayer,videoPath);

    }

    @Override
    public void initPlayer() {
        if (null != aPlayer) {
            return;
        }
        handle=new UpdataUIPlayHandler(this);
        hidePlayControl=new HidePlayControl(this);
        hidePlayControl.startHideTimer();
        aPlayer = new APlayerAndroid();
        aPlayer.setView(surfaceView);
        aPlayer.setOnOpenSuccessListener(new APlayerAndroid.OnOpenSuccessListener() {
            @Override
            public void onOpenSuccess() {
                playerPresenter.setHistoryCurrentPlayTimeMs();
                aPlayer.play();
                startUIUpdateThread();
            }
        });
        aPlayer.setOnPlayCompleteListener(new APlayerAndroid.OnPlayCompleteListener() {
            @Override
            public void onPlayComplete(String playRet) {
                boolean isUseCallStop = APlayerSationUtil.isStopByUserCall(playRet);
                if (!isUseCallStop) {
                    finish();
                }
            }
        });
        aPlayer.setOnOpenCompleteListener(new APlayerAndroid.OnOpenCompleteListener() {
            public void onOpenComplete(boolean isOpenSuccess) {
              // String s="";
            }
        });
        registerListener();
    }

    private void registerListener() {
        mSeekBarPlayProgress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            private static final int SEEK_MIN_GATE_MS = 1000;
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (null == aPlayer || !fromUser) {
                    return;
                }
                mIsTouchingSeekbar = true;
                userSeekPlayProgress(progress);
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mIsTouchingSeekbar = false;
                startUIUpdateThread();
            }
        });
        View.OnTouchListener playViewClick= new View.OnTouchListener() {
            @Override
            public boolean onTouch(final View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        downX = motionEvent.getX();
                        downY = motionEvent.getY();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                switch (view.getId()){
                                    case R.id.play_view_root:
                                        controlViewToggle();
                                        break;
                                    case R.id.top_panel:
                                    case R.id.bottom_panel:
                                        controlViewShow();
                                        break;
                                }
                                destroyPopWind();
                            }
                        }, 100);

                        break;
                    case MotionEvent.ACTION_MOVE:
                        mIsTouchingSeekbar = true;
                        float distanceX = motionEvent.getX() - downX;
                        float distanceY = motionEvent.getY() - downY;
                        if (Math.abs(distanceY) < 50 && distanceX > 100) {
                            setPlaySpeedTime(downX,motionEvent.getX());
                            downX = motionEvent.getX();
                            downY = motionEvent.getY();
                        }else if (Math.abs(distanceY) < 50 && distanceX < -100){
                            setPlaySpeedTime(downX,motionEvent.getX());
                            downX = motionEvent.getX();
                            downY = motionEvent.getY();
                        }else{
                           // controlViewToggle();;
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        setPlaySpeedTime(downX,motionEvent.getX());
                        mIsTouchingSeekbar = false;
                        startUIUpdateThread();
                        break;
                }
                return true;
            }
        };
        mRootView.setOnTouchListener(playViewClick);
        topPanel.setOnTouchListener(playViewClick);
        bottomPanel.setOnTouchListener(playViewClick);
        //surfaceView.setOnTouchListener(playViewClick);
    }
    private void setPlaySpeedTime(float distanceX, float distance2X){
        float offset =   distance2X-distanceX;
        int newProgress = (int) (aPlayer.getPosition() + (offset*10));
        userSeekPlayProgress(newProgress);
        hidePlayControl.resetHideTimer();
    }
    private void destroyPopWind() {
        if (null != mPopupWindow) {
            mPopupWindow.dismiss();
        }
    }

    @Override
    public void controlViewToggle(){
        if (mVisible && !mIsTouchingSeekbar) {
            controlViewHide();
        } else {
            controlViewShow();
        }
    }
    @Override
    public void controlViewShow(){
        if(null==aPlayer) return;
        mVisible=true;
        topPanel.setVisibility(View.VISIBLE);
        bottomPanel.setVisibility(View.VISIBLE);
        hidePlayControl.resetHideTimer();
    }
    @Override
    public void controlViewHide(){
        if(null==aPlayer) return;
        if(!mIsTouchingSeekbar) {
            mVisible = false;
            topPanel.setVisibility(View.GONE);
            bottomPanel.setVisibility(View.GONE);
        }
    }

    @Override
    public void setVideoTile(String name) {
        closeView.setText(name);
    }
    @Override
    public void userSeekPlayProgress(int seekPostionMs) {
        if(null==aPlayer) return;
        int currentPlayPos = aPlayer.getPosition();
        boolean isChangeOverSeekGate = isOverSeekGate(seekPostionMs, currentPlayPos);
        if (!isChangeOverSeekGate) {
            return;
        }
        if(mIsTouchingSeekbar){
            stopUIUpdateThread();
        }
        //mIsTouchingSeekbar = true;
        controlViewShow();
        setTimeTextView(seekPostionMs,aPlayer.getDuration());
        aPlayer.setPosition(seekPostionMs);
    }

    @Override
    public void updateUIPlayStation(int currentPlayTimeMs, int durationTimeMs) {
        setTimeTextView(currentPlayTimeMs,durationTimeMs);
       if (durationTimeMs > 0 && currentPlayTimeMs >= 0) {
            mSeekBarPlayProgress.setMax(durationTimeMs);
            mSeekBarPlayProgress.setProgress(currentPlayTimeMs);
        } else {
            mSeekBarPlayProgress.setProgress(0);
        }

    }

    private void startUIUpdateThread() {
        if (null == mUpdateThread) {
            mIsNeedUpdateUIProgress = true;
            mUpdateThread = new Thread(new UpdatePlayUIProcess(this,handle,aPlayer));
            mUpdateThread.start();
        }
    }
    private void stopUIUpdateThread() {
        mIsNeedUpdateUIProgress = false;
        mUpdateThread = null;
    }
    private boolean isOverSeekGate(int seekBarPositionMs, int currentPlayPosMs) {
        final int SEEK_MIN_GATE_MS = 1000;
        boolean isChangeOverSeekGate = Math.abs(currentPlayPosMs - seekBarPositionMs) > SEEK_MIN_GATE_MS;
        return isChangeOverSeekGate;
    }

    @Override
    public boolean ismIsNeedUpdateUIProgress() {
        return mIsNeedUpdateUIProgress;
    }

    public boolean ismIsTouchingSeekbar() {
        return mIsTouchingSeekbar;
    }

    @Override
    public void setTimeTextView(int currentPlayTimeMs, int durationTimeMs) {
        int currSecond = currentPlayTimeMs / 1000;
        currSecond = currSecond > 0 ? currSecond : 0;
        int duraSecond = durationTimeMs / 1000;
        duraSecond = duraSecond > 0 ? duraSecond : 0;
        playTime.setText(getString(R.string.play_time,TimeUtil.formatFromSecond(duraSecond),TimeUtil.formatFromSecond(currSecond)));
        int batteryicon= SystemConfig.getInstance().getBatteryIcon();
        batteryIcon.setImageDrawable(getResources().getDrawable(batteryicon));
        systemTime.setText(TimeUtil.getNowTime("HH:mm"));
        playerPresenter.uaDataPlayerTime(currentPlayTimeMs,durationTimeMs);
    }

    @Override
    public void openVideo(String path) {
        aPlayer.open(path);
    }

    @Override
    public void playPause() {
        if (null == aPlayer) {
            return;
        }
        int state=aPlayer.getState();
        if(state== APlayerAndroid.PlayerState.APLAYER_PLAYING){
            playPause.setImageDrawable(getResources().getDrawable(R.drawable.ic_video_player));
            aPlayer.pause();
        }else{
            playPause.setImageDrawable(getResources().getDrawable(R.drawable.ic_video_stop));
            aPlayer.play();
        }
    }
    @Event(value = R.id.play_pause)
    private void playPauseClick(View view) {
        playPause();
        controlViewShow();
    }
    @Event(value = R.id.more_view)
    private void moreViewClick(View view) {
        if (null == aPlayer) {
            return;
        }
        if(mPopupWindow==null){
            mPopupWindow = new PlayerPopupWindow(this,mRootView,aPlayer);
        }
        mPopupWindow.showAtLocation(mRootView, Gravity.RIGHT, 0, 0);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode){
            case KeyEvent.KEYCODE_VOLUME_UP:
                case KeyEvent.KEYCODE_VOLUME_DOWN:
                    if (null != aPlayer) {
                        if(mPopupWindow==null){
                            mPopupWindow = new PlayerPopupWindow(this,mRootView,aPlayer);
                        }
                        mPopupWindow.setSystemPlayAudio();
                    }
                    break;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onStop() {
        if (null != aPlayer) {
            aPlayer.play();
            playPause();
        }
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        if (null != aPlayer) {
            aPlayer.close();
            aPlayer.destroy();
            destroyPopWind();
        }
        super.onDestroy();
    }
}
