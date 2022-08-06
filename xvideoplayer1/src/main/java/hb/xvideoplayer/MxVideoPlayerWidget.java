package hb.xvideoplayer;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Handler;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import com.jiajunhui.xapp.medialoader.bean.VideoItem;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;

import mxvideoplayer.app.com.xvideoplayer.R;
import tv.danmaku.ijk.media.player.misc.IjkTrackInfo;

import static hb.xvideoplayer.MxMediaManager.mTextureView;
import static hb.xvideoplayer.MxUtils.getAppComptActivity;

public class MxVideoPlayerWidget extends MxVideoPlayer {

    private static final int VOLUME_ITEM = 2;
    private static final int SURFACE_BEST_FIT = 0;
    private static final int SURFACE_FIT_SCREEN = 1;
    private static final int SURFACE_FILL = 2;
    private static final int SURFACE_16_9 = 3;
    private static final int SURFACE_4_3 = 4;
    private static final int SURFACE_ORIGINAL = 5;
    protected static Timer DISMISS_CONTROL_VIEW_TIMER;
    private static int CURRENT_SIZE = SURFACE_BEST_FIT;
    public ImageView mBackButton;
    public ProgressBar mBottomProgressBar, mLoadingProgressBar;
    public TextView mTitleTextView;
    public ImageView mThumbImageView;
    public ImageView mTinyBackImageView;
    public ImageButton exo_volume, exo_next, exo_prev, rotate, audio, exo_crop, lock, unlock, share;
    public int mVolSave;
    protected Dialog mProgressDialog;
    //protected Dialog mVolumeDialog;
    protected Dialog mBrightnessDialog;
    protected ProgressBar mDialogVolumeProgressBar;
    protected ProgressBar mDialogBrightnessProgressBar;
    protected ProgressBar mDialogProgressBar;
    protected TextView mDialogSeekTime;
    protected TextView mDialogTotalTime;
    protected ImageView mDialogIcon;
    protected boolean mIsShowBottomProgressBar;
    protected DismissControlViewTimerTask mDismissControlViewTimerTask;
    float valuespeed = 1;
//    RelativeLayout childNativeLayout;
    //    @Override
//    public boolean onTouch(View v, MotionEvent event) {
//        int id = v.getId();
//        if (id == R.id.mx_surface_container) {
//            gestureDetector.onTouchEvent(event);
//        }
//        return true;
//    }
    AtomicInteger atomicInteger;
    IjkTrackInfo[] trackDescriptions;
    int clickCount = 0;
    int hideTime = 1500;
    android.view.ViewGroup.LayoutParams lp;
    private boolean mIsAutoPlay = false;
    private boolean mIsAutoProcessUI = false;
    private UIStatusChangeListener mUIListener;
    private ImageButton exo_ffwd, exo_rew, repeat;
    private TextView exo_position, exo_duration;
    private TextView pspeed;
    private Dialog mVolumeDialog;
    private Context mcontext;
    private TextView dspeed;
    private TextView centerTextView;
    private Boolean lockstatus = false, repeatstatus = false, isResume = false;
    private LinearLayout titleControl, leftControl, rightControl, bottomControl;
    private Animation bottomUp, bottomDown;
    private LinearLayout mx_layout_top;
    private FrameLayout mx_surface_container;
    private RelativeLayout main_control;
    private int screenWidth, screenHeight;
    private long audiotrack;
    private List<VideoItem> videoItems = new ArrayList<>();
    private int position = 0;
//    private RelativeLayout nativeLayout;
    private boolean fromaudiotrack = false;


//    @Override
//    public boolean onTouch(View v, MotionEvent event) {
//        int id = v.getId();
//        if (id == R.id.mx_surface_container) {
//
//
//            startDismissControlViewTimer();
//
//        }
//        return false;
//    }
    private File currentFile = new File("");
    private boolean fromlock = false;
    private LinearLayout volume_dialoge;
    private boolean notrack = false;
    /**
     * 显示比例
     * 注意，VideoType.setShowType是全局静态生效，除非重启APP。
     */
    private int mType = 0;

    public MxVideoPlayerWidget(Context context) {
        super(context);
    }

    public MxVideoPlayerWidget(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public static void shareFile(Context context, String path) {
        String urtText = "Please Download Amazing " + context.getString(R.string.app_name)
                + " from google play store:" + "\nhttp://play.google.com/store/apps/details?id=" +
                context.getPackageName();
        File file = new File(path);
        Uri uri = FileProvider.getUriForFile(context, context.getString(R.string.file_provider_authority), file);
        Intent my = new Intent(Intent.ACTION_SEND);
        my.putExtra(Intent.EXTRA_STREAM, uri);
        my.putExtra(Intent.EXTRA_SUBJECT, file.getName());
        my.putExtra(Intent.EXTRA_TEXT, urtText);
        my.setType("video/*");
        my.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        context.startActivity(Intent.createChooser(my, "Share Video"));
    }

    public void setList(List<VideoItem> videoItem, int position1) {
        videoItems = videoItem;
        position = position1;
        Log.d("position", "setList: " + position1);

    }

    @Override
    public void initView(Context context) {
        super.initView(context);
        mcontext = context;
//        nativeLayout = findViewById(R.id.nativeLayout);
//        nativeLayout.setVisibility(View.GONE);
//        childNativeLayout = findViewById(R.id.childNativeLayout);
//        new NativeAdCustomPlayer(getAppComptActivity(context), childNativeLayout);
        bottomUp = AnimationUtils.loadAnimation(context, R.anim.bottom_up);
        bottomDown = AnimationUtils.loadAnimation(context, R.anim.bottom_down);

        mBottomProgressBar = (ProgressBar) findViewById(R.id.mx_bottom_progress);
        mTitleTextView = (TextView) findViewById(R.id.title);
        mBackButton = (ImageView) findViewById(R.id.back);
        mThumbImageView = (ImageView) findViewById(R.id.mx_thumb);
        mLoadingProgressBar = (ProgressBar) findViewById(R.id.mx_loading);
        mTinyBackImageView = (ImageView) findViewById(R.id.mx_quit_tiny);

        bottomControl = findViewById(R.id.bottom_control);
        leftControl = findViewById(R.id.left_control);
        rightControl = findViewById(R.id.right_control);
        titleControl = findViewById(R.id.title_control);
        main_control = findViewById(R.id.main_control);
        share = findViewById(R.id.share);
        share.setOnClickListener(this);
        repeat = findViewById(R.id.repeat);
        repeat.setOnClickListener(this);


        mx_surface_container = findViewById(R.id.mx_surface_container);
        lock = findViewById(R.id.lock);
        lock.setOnClickListener(this);
        exo_ffwd = findViewById(R.id.exo_ffwd);
        exo_rew = findViewById(R.id.exo_rew);
        exo_position = findViewById(R.id.exo_position);
        exo_volume = findViewById(R.id.exo_volume);
        exo_next = findViewById(R.id.exo_next);
        exo_prev = findViewById(R.id.exo_prev);
        rotate = findViewById(R.id.rotate);
        pspeed = findViewById(R.id.pspeed);
        exo_duration = findViewById(R.id.exo_duration);
        centerTextView = findViewById(R.id.centerTextView);
        exo_crop = findViewById(R.id.exo_crop);
        exo_crop.setOnClickListener(this);
        audio = findViewById(R.id.audio);
        mx_surface_container.setOnClickListener(this);

        mx_layout_top = findViewById(R.id.title_control);
        mBackButton.setOnClickListener(this);
        // mx_layout_top.setVisibility(View.GONE);


        unlock = findViewById(R.id.unlock);
        unlock.setOnClickListener(this);
        exo_ffwd.setOnClickListener(this);
        exo_rew.setOnClickListener(this);
        exo_volume.setOnClickListener(this);
        exo_next.setOnClickListener(this);
        exo_prev.setOnClickListener(this);
        rotate.setOnClickListener(this);
        pspeed.setOnClickListener(this);
        audio.setOnClickListener(this);


        mThumbImageView.setOnClickListener(this);
        mBackButton.setOnClickListener(this);
        mTinyBackImageView.setOnClickListener(this);
        mChangePosition = true;
        audiotrack = MxMediaManager.getInstance().getPlayer().getAudioCachedDuration();


    }

    private void audioback() {
        int c = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        if (c == 0) {
            exo_volume.setImageDrawable(getResources().getDrawable(R.drawable.ic_volume_off));
        } else {
            exo_volume.setImageDrawable(getResources().getDrawable(R.drawable.volume));
        }
    }

    public boolean autoStartPlay(String url, int screen, Object... objects) {
        mIsAutoPlay = true;
        boolean result = false;
        if (startPlay(url, screen, objects)) {
//            mPlayUrl coming  null check it


//            if (TextUtils.isEmpty(mPlayUrl)) {
//                Toast.makeText(getContext(), getResources().getString(R.string.no_url),
//                        Toast.LENGTH_SHORT).show();
//                return false;
//            }
            if (mCurrentState == CURRENT_STATE_NORMAL) {
                if (isShowNetworkStateDialog()) {
                    changeUiShowState(Mode.MODE_NORMAL);
                    return false;
                }
            }
//            if (mCurrentState==-1)
//            {
//                changeUiShowState(Mode.MODE_NORMAL);
//
//            }
            preparePlayVideo();
            result = true;
        }
        mIsAutoPlay = false;
        return result;
    }

    @Override
    public boolean startPlay(String url, int screen, Object... objects) {
        Log.e(TAG, "startPlay: ");
        if (objects.length == 0) {
            return false;
        }
        if (super.startPlay(url, screen, objects)) {

            if (mCurrentScreen == SCREEN_WINDOW_FULLSCREEN) {

                mFullscreenButton.setImageResource(R.drawable.mx_shrink);
                mBackButton.setVisibility(View.VISIBLE);
                mTinyBackImageView.setVisibility(View.INVISIBLE);
            } else if (mCurrentScreen == SCREEN_LAYOUT_LIST ||
                    mCurrentScreen == SCREEN_LAYOUT_NORMAL) {

                mFullscreenButton.setImageResource(R.drawable.mx_enlarge);
                mBackButton.setVisibility(View.GONE);
                mTinyBackImageView.setVisibility(View.INVISIBLE);
            } else if (mCurrentScreen == SCREEN_WINDOW_TINY) {
                mTinyBackImageView.setVisibility(View.VISIBLE);
                setAllControlsVisible(View.INVISIBLE, View.INVISIBLE, View.INVISIBLE,
                        View.INVISIBLE, View.INVISIBLE, View.INVISIBLE);
            }
            return true;
        }
        return false;
    }

    public void setUIStatusListener(UIStatusChangeListener listener) {
        mUIListener = listener;
    }

    public void setAutoProcessUI(boolean autoPrcUI) {
        mIsAutoProcessUI = autoPrcUI;
    }

    @Override
    public void setUiPlayState(int state) {
        super.setUiPlayState(state);
        switch (mCurrentState) {
            case CURRENT_STATE_NORMAL:
                if (!mIsAutoPlay) {
                    changeUiShowState(Mode.MODE_AUTO_PLAY);
//                    changeUiShowState(Mode.MODE_NORMAL);
                } else {
                    changeUiShowState(Mode.MODE_AUTO_PLAY);
                }
                break;
            case CURRENT_STATE_PREPARING:
                File currentFile = new File(videoItems.get(position).getPath());
                autoStartPlay(videoItems.get(position).getPath(),
                        SCREEN_WINDOW_FULLSCREEN, currentFile.getName());
                changeUiShowState(Mode.MODE_PREPARING);
                // startDismissControlViewTimer();
                mBottomProgressBar.setProgress(0);
                break;
            case CURRENT_STATE_PLAYING:
                Log.e(TAG, "setUiPlayState: play=======>");
                mVideoPlayPauseAdsListener.onPlayVideo();
//                nativeLayout.setVisibility(GONE);
                changeUiShowState(Mode.MODE_PLAYING);
                //startDismissControlViewTimer();
                break;
            case CURRENT_STATE_PAUSE:
                Log.e(TAG, "setUiPlayState: puase======>");
                mVideoPlayPauseAdsListener.onPauseVideo();
                if (!fromaudiotrack) {
//                    nativeLayout.setVisibility(VISIBLE);
                }
                changeUiShowState(Mode.MODE_PAUSE);
                cancelDismissControlViewTimer();
                break;
            case CURRENT_STATE_ERROR:
                changeUiShowState(Mode.MODE_ERROR);
                break;
            case CURRENT_STATE_AUTO_COMPLETE:
                changeUiShowState(Mode.MODE_COMPLETE);
                cancelDismissControlViewTimer();
                mBottomProgressBar.setProgress(100);
                break;
            case CURRENT_STATE_PLAYING_BUFFERING_START:
                changeUiShowState(Mode.MODE_BUFFERING);
                break;
            default:
                break;
        }
    }

    private void onClickUiToggle() {
        if (mCurrentState == CURRENT_STATE_PREPARING) {
            if (mBottomContainer.getVisibility() == View.VISIBLE) {
                changeUiShowState(Mode.MODE_PREPARING_CLEAR);
            } else {
                changeUiShowState(Mode.MODE_PREPARING);
            }
        } else if (mCurrentState == CURRENT_STATE_PLAYING) {
            if (mBottomContainer.getVisibility() == View.VISIBLE) {
                changeUiShowState(Mode.MODE_PLAYING_CLEAR);
            } else {
                changeUiShowState(Mode.MODE_PLAYING);
            }
        } else if (mCurrentState == CURRENT_STATE_PAUSE) {
            if (mBottomProgressBar.getVisibility() == View.VISIBLE) {
                changeUiShowState(Mode.MODE_PAUSE_CLEAR);
            } else {
                changeUiShowState(Mode.MODE_PAUSE);
            }
        } else if (mCurrentState == CURRENT_STATE_AUTO_COMPLETE) {
            if (mBottomContainer.getVisibility() == View.VISIBLE) {
                changeUiShowState(Mode.MODE_COMPLETE_CLEAR);
            } else {
                changeUiShowState(Mode.MODE_COMPLETE);
            }
        } else if (mCurrentState == CURRENT_STATE_PLAYING_BUFFERING_START) {
            if (mBottomContainer.getVisibility() == View.VISIBLE) {
                changeUiShowState(Mode.MODE_BUFFERING_CLEAR);
            } else {
                changeUiShowState(Mode.MODE_BUFFERING);
            }
        }
    }

    private void changeUiShowState(Mode mode) {
//        if (mCurrentScreen == SCREEN_WINDOW_TINY) {
//            return;
//        }

        if (mUIListener != null) {
            mUIListener.onUIChange(mode);
        }

        if (mIsAutoProcessUI) {
            if (mode == Mode.MODE_NORMAL || mode == Mode.MODE_BUFFERING_CLEAR ||
                    mode == Mode.MODE_PLAYING || mode == Mode.MODE_PAUSE ||
                    mode == Mode.MODE_COMPLETE || mode == Mode.MODE_COMPLETE_CLEAR
                    || mode == Mode.MODE_ERROR) {
                updateStartImage();
            }
            return;
        }

        switch (mode) {
            case MODE_NORMAL:
                setAllControlsVisible(View.VISIBLE, View.INVISIBLE, View.VISIBLE,
                        View.INVISIBLE, View.VISIBLE, View.INVISIBLE);
                updateStartImage();
                break;
            case MODE_BUFFERING:
                setAllControlsVisible(View.VISIBLE, View.VISIBLE, View.INVISIBLE,
                        View.VISIBLE, View.INVISIBLE, View.INVISIBLE);
                break;
            case MODE_BUFFERING_CLEAR:
                setAllControlsVisible(View.INVISIBLE, View.INVISIBLE, View.INVISIBLE,
                        View.VISIBLE, View.INVISIBLE, View.VISIBLE);
                updateStartImage();
                break;
            case MODE_AUTO_PLAY:
                setAllControlsVisible(View.INVISIBLE, View.INVISIBLE, View.INVISIBLE,
                        View.VISIBLE, View.INVISIBLE, View.INVISIBLE);
                break;
            case MODE_PREPARING:
                setAllControlsVisible(View.VISIBLE, View.INVISIBLE, View.INVISIBLE,
                        View.VISIBLE, View.VISIBLE, View.INVISIBLE);
                break;
            case MODE_PREPARING_CLEAR:
                setAllControlsVisible(View.VISIBLE, View.INVISIBLE, View.INVISIBLE,
                        View.VISIBLE, View.VISIBLE, View.INVISIBLE);
                break;
            case MODE_PLAYING:
                setAllControlsVisible(View.VISIBLE, View.VISIBLE, View.VISIBLE,
                        View.INVISIBLE, View.INVISIBLE, View.INVISIBLE);
                updateStartImage();
                break;
            case MODE_PLAYING_CLEAR:
                setAllControlsVisible(View.INVISIBLE, View.INVISIBLE, View.INVISIBLE,
                        View.INVISIBLE, View.INVISIBLE, View.VISIBLE);
                break;
            case MODE_PAUSE:
                setAllControlsVisible(View.VISIBLE, View.VISIBLE, View.VISIBLE,
                        View.INVISIBLE, View.INVISIBLE, View.INVISIBLE);
                updateStartImage();
                break;
            case MODE_PAUSE_CLEAR:
                setAllControlsVisible(View.INVISIBLE, View.INVISIBLE, View.INVISIBLE,
                        View.INVISIBLE, View.INVISIBLE, View.INVISIBLE);
                break;
            case MODE_COMPLETE:
                setAllControlsVisible(View.VISIBLE, View.VISIBLE, View.VISIBLE,
                        View.INVISIBLE, View.VISIBLE, View.INVISIBLE);
                updateStartImage();
                break;
            case MODE_COMPLETE_CLEAR:
                setAllControlsVisible(View.INVISIBLE, View.INVISIBLE, View.VISIBLE,
                        View.INVISIBLE, View.VISIBLE, View.VISIBLE);
                updateStartImage();
                break;
            case MODE_ERROR:
                setAllControlsVisible(View.INVISIBLE, View.INVISIBLE, View.VISIBLE,
                        View.INVISIBLE, View.INVISIBLE, View.INVISIBLE);
                updateStartImage();
                break;
        }
    }

    private void cancelDismissControlViewTimer() {
        if (DISMISS_CONTROL_VIEW_TIMER != null) {
            DISMISS_CONTROL_VIEW_TIMER.cancel();
        }
        if (mDismissControlViewTimerTask != null) {
            mDismissControlViewTimerTask.cancel();
        }
    }

    public void startDismissControlViewTimer() {
        cancelDismissControlViewTimer();
        DISMISS_CONTROL_VIEW_TIMER = new Timer();
        mDismissControlViewTimerTask = new DismissControlViewTimerTask();
        DISMISS_CONTROL_VIEW_TIMER.schedule(mDismissControlViewTimerTask, 100);
    }

    private void updateStartImage() {
        if (mCurrentState == CURRENT_STATE_PLAYING) {
            mPlayControllerButton.setImageResource(R.drawable.pause);
        } else if (mCurrentState == CURRENT_STATE_ERROR) {
            mPlayControllerButton.setImageResource(R.drawable.mx_click_error_selector);
        } else {
            mPlayControllerButton.setImageResource(R.drawable.play);
        }
    }

    public void setAllControlsVisible(int topCon, int bottomCon, int startBtn, int loadingPro,
                                      int thumbImg, int bottomPro) {
        mTopContainer.setVisibility(VISIBLE);
        // mBottomContainer.setVisibility(VISIBLE);
        //mPlayControllerButton.setVisibility(startBtn);
        mLoadingProgressBar.setVisibility(GONE);
        if (thumbImg == View.VISIBLE) {
            mThumbImageView.setVisibility(View.GONE);
        } else {
            mThumbImageView.setVisibility(View.GONE);
        }
        if (mIsShowBottomProgressBar) {
            mBottomProgressBar.setVisibility(View.VISIBLE);
        } else {
            mBottomProgressBar.setVisibility(View.VISIBLE);
        }
    }

    private void setProgressDrawable(Drawable drawable) {
        if (drawable != null) {
            //mProgressBar.setProgressDrawable(drawable);
        }
    }

    private void setTitleSize(int size) {
        mTitleTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, size);
    }

    public void setBottomProgressBarVisibility(boolean visibility) {
        mIsShowBottomProgressBar = visibility;
    }

    @Override
    public void onClick(final View v) {
        super.onClick(v);
        int id = v.getId();

        if (id == R.id.mx_thumb) {
            try {
                if (MxMediaManager.getInstance().getPlayer() != null) {


                    if (TextUtils.isEmpty(mPlayUrl)) {
                        Toast.makeText(getContext(), getResources().getString(R.string.no_url), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (mCurrentState == CURRENT_STATE_NORMAL) {
//                if (!mPlayUrl.startsWith("file") && !MxUtils.isWifiConnected(getContext()) && !WIFI_TIP_DIALOG_SHOWED) {
//                    //showWifiDialog();
//                    return;
//                }
                        preparePlayVideo();
                        mTitleTextView.setText(videoItems.get(position).getDisplayName());
                    } else if (mCurrentState == CURRENT_STATE_AUTO_COMPLETE) {
                        onClickUiToggle();
                    }
                }
            } catch (Exception e) {

            }
        } else if (id == R.id.mx_surface_container) {
            //startDismissControlViewTimer();
        }
        if (id == R.id.back) {
            getAppComptActivity(mcontext).onBackPressed();
        } else if (id == R.id.mx_quit_tiny) {
            if (MxVideoPlayerManager.mCurScrollListener.get() != null) {
                if (!MxVideoPlayerManager.mCurScrollListener.get().getUrl().
                        equals(MxMediaManager.getInstance().getPlayer().getDataSource())) {
                    releaseAllVideos();
                    return;
                }
            }
            backPress();
        } else if (id == R.id.exo_ffwd) {


            mTouchingProgressBar = true;
            if (getCurrentPositionWhenPlaying() != 0) {

//                if (mChangePosition) {
                int totalTimeDuration = getDuration();
                mSeekTimePosition = (int) (getCurrentPositionWhenPlaying() + 100 * 100);
                if (mSeekTimePosition > totalTimeDuration) {
                    mSeekTimePosition = totalTimeDuration;
                }
                String seekTime = MxUtils.stringForTime(mSeekTimePosition);
                String totalTime = MxUtils.stringForTime(totalTimeDuration);
                showProgressDialog(100, seekTime, mSeekTimePosition, totalTime, totalTimeDuration);

                mTouchingProgressBar = false;
                mChangePosition = true;
                if (mChangePosition) {
                    onActionEvent(MxUserAction.ON_TOUCH_SCREEN_SEEK_POSITION);
                    MxMediaManager.getInstance().getPlayer().seekTo(mSeekTimePosition);
                    int duration = getDuration();
                    int progress = mSeekTimePosition * 100 / (duration == 0 ? 1 : duration);
                    mProgressBar.setProgress(progress);
                    Log.d(TAG, "onClick: " + mSeekTimePosition + "...." + getCurrentPositionWhenPlaying() + "progress" + progress);
                }

                startProgressTimer();
                cancelDismissControlViewTimer();
                startDismissControlViewTimer();

//                final android.os.Handler handler = new Handler();
//                handler.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        dismissProgressDialog();
//                    }
//                }, 2000);
            }

        } else if (id == R.id.exo_rew) {


            mTouchingProgressBar = true;
            if (getCurrentPositionWhenPlaying() != 0) {

//                if (mChangePosition) {
                int totalTimeDuration = getDuration();
                mSeekTimePosition = (int) (getCurrentPositionWhenPlaying() - 100 * 100);
                if (mSeekTimePosition > totalTimeDuration) {
                    mSeekTimePosition = totalTimeDuration;
                }
                String seekTime = MxUtils.stringForTime(mSeekTimePosition);
                String totalTime = MxUtils.stringForTime(totalTimeDuration);
                showProgressDialog(-100, seekTime, mSeekTimePosition, totalTime, totalTimeDuration);

                mTouchingProgressBar = false;
                mChangePosition = true;
                if (mChangePosition) {
                    onActionEvent(MxUserAction.ON_TOUCH_SCREEN_SEEK_POSITION);
                    MxMediaManager.getInstance().getPlayer().seekTo(mSeekTimePosition);
                    int duration = getDuration();
                    int progress = mSeekTimePosition * 100 / (duration == 0 ? 1 : duration);
                    mProgressBar.setProgress(progress);
                }
                startProgressTimer();
                cancelDismissControlViewTimer();
                startDismissControlViewTimer();
                final android.os.Handler handler = new Handler();
//                handler.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        if (!getAppComptActivity(mcontext).isFinishing())
//                            dismissProgressDialog();
//                    }
//                }, 2000);
            }

        } else if (id == R.id.exo_volume) {
            int c = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            mVolSave = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            if (c == 0) {
                // mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 30, 0);

                if (mVolSave == 0) {
                    mVolSave = 15;
                }
                mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, mVolSave, 0);
                exo_volume.setImageDrawable(getResources().getDrawable(R.drawable.volume));
            } else {
                exo_volume.setImageDrawable(getResources().getDrawable(R.drawable.ic_volume_off));
                mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
            }

        } else if (id == R.id.exo_next) {
            prevNextSong(1);

        } else if (id == R.id.exo_prev) {
            prevNextSong(-1);
        } else if (id == R.id.rotate) {
            int ori = getResources().getConfiguration().orientation;
            if (ori == Configuration.ORIENTATION_PORTRAIT) {
                getAppComptActivity(mcontext).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
            } else {
                getAppComptActivity(mcontext).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            }
        } else if (id == R.id.pspeed) {


            Dialog dialog = new Dialog(getAppComptActivity(mcontext), R.style.control);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#000000")));
            LayoutInflater inflater = LayoutInflater.from(mcontext);
            View v1 = inflater.inflate(R.layout.playback_speed, null);
            dialog.setContentView(v1);
            ImageButton sdown, sup;
            sdown = v1.findViewById(R.id.sdown);
            sup = v1.findViewById(R.id.sup);
            dspeed = v1.findViewById(R.id.dspeed);
            dspeed.setText(String.format("%.0f", valuespeed * 100));


            sdown.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v2) {

                    if (valuespeed > 0.1) {
//                        if (valuespeed >= .1) {
                        valuespeed = valuespeed - .1f;
                        MxMediaManager.getInstance().getPlayer().setSpeed(valuespeed);

                        dspeed.setText(String.format("%.0f", valuespeed * 100));
                        pspeed.setText(String.format("%.1f" + "x", valuespeed));
//                        } else {
//                            valuespeed = 0.0f;
//                        }

                    } else {
                        valuespeed = 0.0f;
                        MxMediaManager.getInstance().getPlayer().setSpeed(valuespeed);

                        dspeed.setText(String.format("%.0f", valuespeed * 100));
                        pspeed.setText(String.format("%.1f" + "x", valuespeed));
                    }


                    PreferenceUtil.getInstance(mcontext).saveLastSpeed(valuespeed);

                }
            });
            sup.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v22) {
                    if (valuespeed < 2) {
                        valuespeed = valuespeed + .1f;
                    }
//                    else if (valuespeed < 2) {
//                        valuespeed = valuespeed + .1f;
//
//                    }
                    MxMediaManager.getInstance().getPlayer().setSpeed(valuespeed);
                    dspeed.setText(String.format("%.0f", valuespeed * 100));

                    //  pspeed.setText(String.format("%.0f", valuespeed * 100));
                    pspeed.setText(String.format("%.1f" + "x", valuespeed));
                    PreferenceUtil.getInstance(mcontext).saveLastSpeed(valuespeed);
                }
            });

            dialog.show();

        } else if (id == R.id.audio) {
            if (MxMediaManager.getInstance().getPlayer() != null) {
                //  audioTrack();
                audioTrack1();
            }
        } else if (id == R.id.exo_crop) {
            Ratio();
        } else if (id == R.id.lock) {
            lock();
        } else if (id == R.id.unlock) {
            unlock();
        } else if (id == R.id.repeat) {
            if (!repeatstatus) {
                repeat.setImageResource(R.drawable.repeatone);
                repeatstatus = !repeatstatus;
            } else {
                repeat.setImageResource(R.drawable.repeat);
                repeatstatus = !repeatstatus;
            }
        } else if (id == R.id.share) {
            currentFile = new File(videoItems.get(position).getPath());
//            onActionEvent(MxUserAction.ON_CLICK_PAUSE);
//            MxMediaManager.getInstance().getPlayer().pause();
//            setUiPlayState(CURRENT_STATE_PAUSE);
            shareFile(getAppComptActivity(mcontext), currentFile.toString());
        }
    }

    private void setSpeed(int d) {
        dspeed.setText(Integer.toString(d));
        float x = d / 100.0f;
        pspeed.setText(String.format("%sX", x));
        //  mMediaPlayer.setRate(x);
        MxMediaManager.getInstance().getPlayer().setSpeed(atomicInteger.get());
        PreferenceUtil.getInstance(getContext()).saveLastSpeed(x);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int id = v.getId();
        if (id == R.id.mx_surface_container) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_UP:
                    startDismissControlViewTimer();
                    if (mChangePosition) {
                        int duration = getDuration();
                        int progress = mSeekTimePosition * 100 / (duration == 0 ? 1 : duration);
                        mBottomProgressBar.setProgress(progress);
                    }
                    if (!mChangePosition && !mChangeVolume) {
                        onClickUiToggle();
                    }
                    break;
                default:
                    break;
            }
        } else if (id == R.id.mx_progress) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    cancelDismissControlViewTimer();
                    break;
                case MotionEvent.ACTION_UP:
                    startDismissControlViewTimer();
                    break;
                default:
                    break;
            }
        }
        return super.onTouch(v, event);
    }

    private void lock() {
        fromlock = true;
        lockstatus = !lockstatus;
        controlsInvisible();
        unlock.setVisibility(View.VISIBLE);
        PreferenceUtil.getInstance(mcontext).setLock(true);

    }

    private void controlsInvisible() {
        TranslateAnimation animate = new TranslateAnimation(0, 0, 0, -titleControl.getHeight());
        animate.setDuration(500);
        animate.setFillAfter(true);
        titleControl.startAnimation(animate);
        titleControl.setVisibility(View.GONE);
        // main_control.setAnimation(bottomDown);
        main_control.setVisibility(View.GONE);
        leftControl.setAnimation(bottomDown);
        leftControl.setVisibility(View.GONE);
        rightControl.setAnimation(bottomDown);
        rightControl.setVisibility(View.GONE);


        mx_layout_top.setVisibility(View.GONE);
        if (fromlock) {
            mx_surface_container.setEnabled(false);
        } else
            mx_surface_container.setEnabled(true);


    }

    private void unlock() {
        fromlock = false;
        lockstatus = !lockstatus;
        //controlsVisible();
        unlock.setVisibility(View.GONE);
        PreferenceUtil.getInstance(mcontext).setLock(false);
        controlsVisible();
    }

    private void controlsVisible() {
        if (!PreferenceUtil.getInstance(mcontext).getLock()) {
            main_control.setAnimation(bottomUp);
            main_control.setVisibility(VISIBLE);
            bottomControl.setVisibility(View.VISIBLE);
            leftControl.setAnimation(bottomUp);
            leftControl.setVisibility(View.VISIBLE);
            bottomControl.setAnimation(bottomUp);
            bottomControl.setVisibility(View.VISIBLE);
            TranslateAnimation animate = new TranslateAnimation(0, 0, -titleControl.getHeight(), 0);
            animate.setDuration(500);
            animate.setFillAfter(true);
            titleControl.startAnimation(animate);
            rightControl.setVisibility(View.VISIBLE);
            mx_surface_container.setEnabled(true);
            mx_layout_top.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void startWindowFullscreen() {
        MxMediaManager.getInstance().mIsShowBottomProgressBar = mIsShowBottomProgressBar;
        super.startWindowFullscreen();
    }

    @Override
    public void setProgressAndTime(int progress, int secProgress, int currentTime,
                                   int totalTime) {
        super.setProgressAndTime(progress, secProgress, currentTime, totalTime);
        if (progress != 0) {
            mBottomProgressBar.setProgress(progress);
        }
        if (secProgress != 0) {
            mBottomProgressBar.setSecondaryProgress(secProgress);
        }
    }

    @Override
    public void resetProgressAndTime() {
        super.resetProgressAndTime();
        mBottomProgressBar.setProgress(0);
        mBottomProgressBar.setSecondaryProgress(0);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        super.onStartTrackingTouch(seekBar);
        cancelDismissControlViewTimer();
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        super.onStopTrackingTouch(seekBar);
        startDismissControlViewTimer();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.mx_video_layout_mobile;
    }

    @Override
    protected void initAttributeSet(Context context, AttributeSet attrs) {
        if (attrs == null) {
            mIsShowBottomProgressBar = MxMediaManager.getInstance().mIsShowBottomProgressBar;
            return;
        }
        TypedArray attr = context.obtainStyledAttributes(attrs, R.styleable.MxVideoPlayer);
        Drawable drawable = attr.getDrawable(R.styleable.MxVideoPlayer_progress_drawable);
        setProgressDrawable(drawable);
        int defaultTextSize = context.getResources().getDimensionPixelSize(R.dimen.mx_title_textSize);
        int size = attr.getDimensionPixelSize(R.styleable.MxVideoPlayer_title_size, defaultTextSize);
        setTitleSize(size);
        boolean isShowBottomProgressBar = attr.getBoolean(R.styleable.MxVideoPlayer_showBottomProgress, true);
        setBottomProgressBarVisibility(isShowBottomProgressBar);
        attr.recycle();
    }

    @Override
    protected boolean isShowNetworkStateDialog() {
//        if (!mPlayUrl.startsWith("file") && !MxUtils.isWifiConnected(getContext()) && !WIFI_TIP_DIALOG_SHOWED) {
//            showWifiDialog();
//            return true;
//        }
        return false;
    }

    private void showWifiDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage(getResources().getString(R.string.tips_not_wifi));
        builder.setPositiveButton(getResources().getString(R.string.tips_not_wifi_confirm),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        preparePlayVideo();
                        WIFI_TIP_DIALOG_SHOWED = true;
                    }
                });
        builder.setNegativeButton(getResources().getString(R.string.tips_not_wifi_cancel),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        builder.create().show();
    }

    @Override
    protected void showProgressDialog(float deltaX, String seekTime,
                                      int seekTimePosition, String totalTime, int totalTimeDuration) {
        if (mProgressDialog == null) {
            View localView = View.inflate(getContext(), R.layout.mx_progress_dialog, null);
            mDialogProgressBar = ((ProgressBar) localView.findViewById(R.id.duration_progressbar));
            mDialogSeekTime = ((TextView) localView.findViewById(R.id.video_current));
            mDialogTotalTime = ((TextView) localView.findViewById(R.id.video_duration));
            mDialogIcon = ((ImageView) localView.findViewById(R.id.duration_image_tip));
            mProgressDialog = new Dialog(getContext(), R.style.mx_style_dialog_progress);
            mProgressDialog.setContentView(localView);
            if (mProgressDialog.getWindow() != null) {
                mProgressDialog.getWindow().addFlags(Window.FEATURE_ACTION_BAR);
                mProgressDialog.getWindow().addFlags(32);
                mProgressDialog.getWindow().addFlags(16);
                mProgressDialog.getWindow().setLayout(-2, -2);
            }
            WindowManager.LayoutParams params = mProgressDialog.getWindow().getAttributes();
            params.gravity = 49;
            params.y = getResources().getDimensionPixelOffset(R.dimen.mx_progress_dialog_margin_top);
            params.width = getContext().getResources()
                    .getDimensionPixelOffset(R.dimen.mx_mobile_dialog_width);
            mProgressDialog.getWindow().setAttributes(params);
        }
        if (!mProgressDialog.isShowing()) {
            mProgressDialog.show();
        }

        mDialogSeekTime.setText(seekTime);
        mDialogTotalTime.setText(String.format(" / %s", totalTime));
        mDialogProgressBar.setProgress(totalTimeDuration <= 0 ? 0 : (seekTimePosition * 100 / totalTimeDuration));
        //  mDialogProgressBar.setProgress(seekTimePosition );
        if (deltaX > 0) {
            mDialogIcon.setBackgroundResource(R.drawable.mx_forward_icon);
        } else {
            mDialogIcon.setBackgroundResource(R.drawable.mx_backward_icon);
        }
        Log.d(TAG, "showProgressDialog: " + seekTimePosition + ">>" + seekTime);
    }

    @Override
    protected void showVolumeDialog(float v, int volumePercent) {

        if (mVolumeDialog == null) {
            View localView = View.inflate(getContext(), R.layout.mx_mobile_volume_dialog, null);
            mDialogVolumeProgressBar = ((ProgressBar) localView.findViewById(R.id.volume_progressbar));
            mVolumeDialog = new Dialog(getContext(), R.style.mx_style_dialog_progress);
            mVolumeDialog.setContentView(localView);
            if (mVolumeDialog.getWindow() != null) {
                mVolumeDialog.getWindow().addFlags(8);
                mVolumeDialog.getWindow().addFlags(32);
                mVolumeDialog.getWindow().addFlags(16);
                mVolumeDialog.getWindow().setLayout(-2, -2);
            }
            WindowManager.LayoutParams params = mVolumeDialog.getWindow().getAttributes();
            params.gravity = 49;
            params.y = getContext().getResources()
                    .getDimensionPixelOffset(R.dimen.mx_volume_dialog_margin_top);
            params.width = getContext().getResources()
                    .getDimensionPixelOffset(R.dimen.mx_mobile_dialog_width);
            mVolumeDialog.getWindow().setAttributes(params);
        }
        if (!mVolumeDialog.isShowing()) {
            mVolumeDialog.show();
        }
        mDialogVolumeProgressBar.setProgress(volumePercent);
        mVolSave = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        if (mVolSave == 0)
            exo_volume.setImageDrawable(getResources().getDrawable(R.drawable.ic_volume_off));
        else {
            exo_volume.setImageDrawable(getResources().getDrawable(R.drawable.volume));

        }


    }

    @Override
    protected void showBrightnessDialog(float v, int brightnessPercent) {
        if (mBrightnessDialog == null) {
            View localView = View.inflate(getContext(), R.layout.mx_mobile_brightness_dialog, null);
            mDialogBrightnessProgressBar = ((ProgressBar) localView.findViewById(R.id.brightness_progressbar));
            mBrightnessDialog = new Dialog(getContext(), R.style.mx_style_dialog_progress);
            mBrightnessDialog.setContentView(localView);
            if (mBrightnessDialog.getWindow() != null) {
                mBrightnessDialog.getWindow().addFlags(8);
                mBrightnessDialog.getWindow().addFlags(32);
                mBrightnessDialog.getWindow().addFlags(16);
                mBrightnessDialog.getWindow().setLayout(-2, -2);
            }
            WindowManager.LayoutParams params = mBrightnessDialog.getWindow().getAttributes();
            params.gravity = 49;
            params.y = getContext().getResources()
                    .getDimensionPixelOffset(R.dimen.mx_volume_dialog_margin_top);
            params.width = getContext().getResources()
                    .getDimensionPixelOffset(R.dimen.mx_mobile_dialog_width);
            mBrightnessDialog.getWindow().setAttributes(params);
        }
        if (!mBrightnessDialog.isShowing()) {
            mBrightnessDialog.show();
        }
        mDialogBrightnessProgressBar.setProgress(brightnessPercent);
    }

    @Override
    protected void dismissVolumeDialog() {
        if (mVolumeDialog != null) {
            mVolumeDialog.dismiss();
        }
    }

    @Override
    protected void dismissBrightnessDialog() {
        if (mBrightnessDialog != null) {
            mBrightnessDialog.dismiss();
        }
    }

    @Override
    protected void dismissProgressDialog() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
    }

    @Override
    public void onPrepared() {
        if (videoItems.size() != 0) {
            mTitleTextView.setText(videoItems.get(position).getDisplayName());
        }
        super.onPrepared();
    }

    @Override
    public void onCompletion() {

        if (repeatstatus) {
            prevNextSong(0);
        } else {
            prevNextSong(1);
        }
    }

    public void Ratio() {

        if (clickCount == 0) {
            lp = mTextureView.getLayoutParams();
            lp.width = MxMediaManager.getInstance().mainvideowidth;
            lp.height = MxMediaManager.getInstance().mainvideoheight;
            mTextureView.setLayoutParams(lp);
            showInfo(getResources().getString(R.string.surface_original), hideTime);
            exo_crop.setImageDrawable(getResources().getDrawable(R.drawable.fit));
            clickCount = 1;
        } else if (clickCount == 1) {
            getDeviceWidthAndHeight();
            lp.width = screenWidth - 300;
            lp.height = screenHeight - 100;
            mTextureView.setLayoutParams(lp);
            //resizeVideo.setText("Full Screen");
            showInfo(getResources().getString(R.string.surface_fill), hideTime);
            exo_crop.setImageDrawable(getResources().getDrawable(R.drawable.verticel_fit));
            clickCount = 2;
        } else if (clickCount == 2) {
            getDeviceWidthAndHeight();
            lp.width = screenWidth;
            lp.height = screenHeight;
            mTextureView.setLayoutParams(lp);
            // resizeVideo.setText("100%");
            showInfo("16:9", hideTime);
            exo_crop.setImageDrawable(getResources().getDrawable(R.drawable.ic_crop_16_9));
            clickCount = 3;
        } else if (clickCount == 3) {
            getDeviceWidthAndHeight();
            lp.width = screenWidth;
            lp.height = screenHeight - 500;
            mTextureView.setLayoutParams(lp);
            showInfo("4:3", hideTime);
            exo_crop.setImageDrawable(getResources().getDrawable(R.drawable.ic_crop_4_3));
            // resizeVideo.setText("Fit to Screen");
            clickCount = 4;
        } else if (clickCount == 4) {

            getDeviceWidthAndHeight();
            lp.width = screenWidth + 10;
            lp.height = screenHeight;
            mTextureView.setLayoutParams(lp);
            //  resizeVideo.setText("100%");
            showInfo(getResources().getString(R.string.surface_best_fit), hideTime);
            exo_crop.setImageDrawable(getResources().getDrawable(R.drawable.full));
            clickCount = 0;
        }
    }

    private void resolveRotateUI() {

        //  mTextureView.setRotation(mRotate);
        mTextureView.requestLayout();
    }

    private void resolveTypeUI() {

        if (mType == 1) {
//            exo_crop.setText("16:9");
            exo_crop.setImageDrawable(getResources().getDrawable(R.drawable.ic_crop_16_9));
            VideoType.setShowType(VideoType.SCREEN_TYPE_16_9);
        } else if (mType == 2) {
//            exo_crop.setText("4:3");
            exo_crop.setImageDrawable(getResources().getDrawable(R.drawable.ic_crop_4_3));
            VideoType.setShowType(VideoType.SCREEN_TYPE_4_3);
        } else if (mType == 3) {
            exo_crop.setImageDrawable(getResources().getDrawable(R.drawable.full));
//            exo_crop.setText("\n" +
//                    "full screen");
            VideoType.setShowType(VideoType.SCREEN_TYPE_FULL);
        } else if (mType == 4) {
//            exo_crop.setText("\n" + "Stretch full screen");
            exo_crop.setImageDrawable(getResources().getDrawable(R.drawable.ic_strech));
            ;
            VideoType.setShowType(VideoType.SCREEN_MATCH_FULL);
        } else if (mType == 0) {
            exo_crop.setImageDrawable(getResources().getDrawable(R.drawable.full));
//            exo_crop.setText("\n" +
//                    "Default scale");
            VideoType.setShowType(VideoType.SCREEN_TYPE_DEFAULT);
        }
        changeTextureViewShowType();
        if (mTextureView != null)
            mTextureView.requestLayout();
    }

    protected void changeTextureViewShowType() {
        if (mTextureView != null) {
            int params = getTextureParams();
            ViewGroup.LayoutParams layoutParams = mTextureView.getLayoutParams();
            layoutParams.width = params;
            layoutParams.height = params;
            mTextureView.setLayoutParams(layoutParams);
        }
    }

    protected int getTextureParams() {
        boolean typeChanged = (VideoType.getShowType() != VideoType.SCREEN_TYPE_DEFAULT);
        return (typeChanged) ? ViewGroup.LayoutParams.WRAP_CONTENT : ViewGroup.LayoutParams.MATCH_PARENT;
    }

    public void getDeviceWidthAndHeight() {
        lp = mTextureView.getLayoutParams();
        screenWidth = getAppComptActivity(mcontext).getWindow().getDecorView().getWidth();
        screenHeight = getAppComptActivity(mcontext).getWindow().getDecorView().getHeight();


    }

    private void showInfo(String surface_best_fit, int hideTime) {
        centerTextView.setText(surface_best_fit);
        centerTextView.setVisibility(View.VISIBLE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                centerTextView.setVisibility(View.GONE);
            }
        }, hideTime);
    }

    private void audioTrack1() {
        fromaudiotrack = true;
        onvideoPause();
        trackDescriptions = MxMediaManager.getInstance().getPlayer().getTrackInfo();
        if (trackDescriptions == null) {
            Toast.makeText(getAppComptActivity(getContext()), "Audio track not available !", Toast.LENGTH_SHORT).show();
            return;
        }
        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.audio_tracks_dialog);
        List<String> stringList = new ArrayList<>();  // here is list
        for (IjkTrackInfo trackDescription : trackDescriptions) {


            stringList.add(trackDescription.getLanguage());
        }
        RadioGroup rg = dialog.findViewById(R.id.radio_group);

        for (int i = 0; i < stringList.size(); i++) {
            RadioButton radioButtonView = new RadioButton(dialog.getContext()); // dynamically creating RadioButton and adding to RadioGroup.
            radioButtonView.setText("Audio track #-" + i + "" + stringList.get(i).toUpperCase());
            if (MxMediaManager.getInstance().getPlayer().getSelectedTrack(2) == i) {
                radioButtonView.setChecked(true);
                radioButtonView.setEnabled(false);
            }
            RadioGroup.LayoutParams params = new RadioGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(0, 15, 0, 0);
            radioButtonView.setLayoutParams(params);
            rg.addView(radioButtonView);
            if (stringList.get(i).equalsIgnoreCase("und")) {
                radioButtonView.setVisibility(View.GONE);
            }
        }

        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int childCount = group.getChildCount();
                for (int x = 0; x < childCount; x++) {
                    RadioButton btn = (RadioButton) group.getChildAt(x);
                    if (btn.getId() == checkedId) {
                        MxMediaManager.getInstance().getPlayer().selectTrack(x);


                        break;
                    }
                }

                dialog.dismiss();
            }
        });
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                fromaudiotrack = false;
                onVideoResume(true);
            }
        });

        dialog.show();
    }

    private void audioTrack() {
        onvideoPause();
        // ses(CURRENT_STATE_PAUSE);


        trackDescriptions = MxMediaManager.getInstance().getPlayer().getTrackInfo();
        if (trackDescriptions == null) {
            Toast.makeText(getAppComptActivity(getContext()), "Audio track not available !", Toast.LENGTH_SHORT).show();
            return;
        }
        final Dialog dialog = new Dialog(getAppComptActivity(getContext()));
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.audio_tracks_dialog);
        final RadioButton first = dialog.findViewById(R.id.first);
        final RadioButton second = dialog.findViewById(R.id.second);
        if (trackDescriptions.length != 0) {


            if (trackDescriptions.length == 1) {
                first.setVisibility(GONE);
                second.setVisibility(GONE);
                // stringList.add(trackDescriptions[1].getLanguage());
            } else if (trackDescriptions.length == 2) {

                second.setVisibility(GONE);
                if (trackDescriptions[1].getLanguage().equalsIgnoreCase("und")) {
                    first.setVisibility(GONE);
                } else {
                    first.setVisibility(VISIBLE);

                    if (trackDescriptions[1].getLanguage().equalsIgnoreCase("hin")) {
                        first.setText("Audio track #1- Hindi");
                    } else if (trackDescriptions[1].getLanguage().equalsIgnoreCase("eng")) {
                        first.setText("Audio track #1- English");

                    }
                }


            } else if (trackDescriptions.length >= 3) {
                if (trackDescriptions[1].getLanguage().equalsIgnoreCase("und")) {
                    first.setVisibility(GONE);
                    if (trackDescriptions[2].getLanguage().equalsIgnoreCase("und")) {
                        second.setVisibility(GONE);
                    }

                } else if (trackDescriptions[2].getLanguage().equalsIgnoreCase("und")) {
                    second.setVisibility(GONE);
                    if (trackDescriptions[1].getLanguage().equalsIgnoreCase("und")) {
                        first.setVisibility(GONE);
                    }
                } else {
                    first.setVisibility(VISIBLE);
                    second.setVisibility(VISIBLE);
                    if (trackDescriptions[1].getLanguage().equalsIgnoreCase("hin")) {
                        first.setText("Audio track #1- Hindi");
                    } else if (trackDescriptions[1].getLanguage().equalsIgnoreCase("eng")) {
                        first.setText("Audio track #1- English");

                    }
                    if (trackDescriptions[2].getLanguage().equalsIgnoreCase("hin")) {
                        second.setText("Audio track #2- Hindi");
                    } else if (trackDescriptions[2].getLanguage().equalsIgnoreCase("eng")) {
                        second.setText("Audio track #2- English");

                    }
                }

            }
        }

        if (MxMediaManager.getInstance().getPlayer().getSelectedTrack(2) == 1) {
            first.setChecked(true);
            second.setChecked(false);

        } else {
            second.setChecked(true);
            first.setChecked(false);
        }
        first.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //if (!first.isChecked()) {
                MxMediaManager.getInstance().getPlayer().selectTrack(1);
                //}

                onVideoResume(true);
                dialog.dismiss();
            }
        });

        second.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // if (!second.isChecked()) {
                MxMediaManager.getInstance().getPlayer().selectTrack(2);
                //  }
                onVideoResume(true);
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void prevNextSong(int pos) {

        clearCacheImage();
        if (videoItems != null) {
            if (pos == 1) {
                position++;
                if (position == videoItems.size()) {
                    position = 0;
                }
            } else if (pos == -1) {
                position--;
                if (position == -1) {
                    position = 0;
                }
            } else if (pos == 0) {
                preparePlayVideo();
                return;
            }

            File currentFile = new File(videoItems.get(position).getPath());

            autoStartPlay(videoItems.get(position).getPath(),
                    SCREEN_WINDOW_FULLSCREEN, currentFile.getName());
        }
    }

    public void downVolume() {
        int currentVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        int maxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, currentVolume - VOLUME_ITEM, 0);
        int volumePercent = (currentVolume - VOLUME_ITEM) * 100 / maxVolume;
        showVolumeDialog(VOLUME_ITEM, volumePercent);
    }

    public void upVolume() {
        int currentVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        int maxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, currentVolume + VOLUME_ITEM, 0);
        int volumePercent = (currentVolume + VOLUME_ITEM) * 100 / maxVolume;
        showVolumeDialog(VOLUME_ITEM, volumePercent);
    }

    public boolean requestKeyDown(int keyCode, KeyEvent event) {
        //        mVolSave = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);

        boolean result = false;
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_DOWN:
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                if (mVolSave == 0) {
                    exo_volume.setImageDrawable(getResources().getDrawable(R.drawable.ic_volume_off));
                }
                downVolume();

                result = true;
                break;
            case KeyEvent.KEYCODE_DPAD_UP:
            case KeyEvent.KEYCODE_VOLUME_UP:
                result = true;
                if (mVolSave != 0) {
                    exo_volume.setImageDrawable(getResources().getDrawable(R.drawable.volume));
                }
                upVolume();
                break;

            case KeyEvent.KEYCODE_BACK:
                result = true;
                MxUtils.getAppComptActivity(getContext()).onBackPressed();
                break;
            default:
                break;
        }

        return result;
    }

    public boolean requestKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_ENTER:
                mPlayControllerButton.performClick();
                break;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
            case KeyEvent.KEYCODE_DPAD_LEFT:

                break;
            case KeyEvent.KEYCODE_DPAD_DOWN:
            case KeyEvent.KEYCODE_DPAD_UP:
            case KeyEvent.KEYCODE_VOLUME_UP:
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                break;
            default:
                onClickUiToggle();
                startDismissControlViewTimer();
                break;
        }

        return false;
    }

    public enum Mode {
        MODE_NORMAL,
        MODE_PREPARING,
        MODE_PREPARING_CLEAR,
        MODE_AUTO_PLAY,
        MODE_PLAYING,
        MODE_PLAYING_CLEAR,
        MODE_PAUSE,
        MODE_PAUSE_CLEAR,
        MODE_COMPLETE,
        MODE_COMPLETE_CLEAR,
        MODE_BUFFERING,
        MODE_BUFFERING_CLEAR,
        MODE_ERROR
    }

    public interface OnVideoPlayPauseShowAds {
        void onPlayVideo();

        void onPauseVideo();
    }

    OnVideoPlayPauseShowAds mVideoPlayPauseAdsListener;
    public void setVideoPlayPauseShowAdsListener(OnVideoPlayPauseShowAds listener) {
        mVideoPlayPauseAdsListener = listener;
    }

    public interface UIStatusChangeListener {
        void onUIChange(Mode mode);
    }

    public class DismissControlViewTimerTask extends TimerTask {

        @Override
        public void run() {
            if (mCurrentState != CURRENT_STATE_NORMAL
                    && mCurrentState != CURRENT_STATE_ERROR
                    && mCurrentState != CURRENT_STATE_AUTO_COMPLETE) {
                if (getContext() != null && getContext() instanceof Activity) {
                    ((Activity) getContext()).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (main_control.getVisibility() == View.VISIBLE) {
                                controlsInvisible();
                            } else {
                                PreferenceUtil.getInstance(mcontext).setLock(false);
                                controlsVisible();
                            }
                            //  mBottomContainer.setVisibility(View.INVISIBLE);
                            // mTopContainer.setVisibility(View.INVISIBLE);
                            //  mPlayControllerButton.setVisibility(View.INVISIBLE);

                        }
                    });
                }
            }
        }
    }


}
