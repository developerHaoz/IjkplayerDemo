package com.developerhaoz.ijkplayerdemo.media;

import android.content.Context;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.MediaController;
import android.widget.TextView;

import com.developerhaoz.ijkplayerdemo.InfoHudViewHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import tv.danmaku.ijk.media.exo.IjkExoMediaPlayer;
import tv.danmaku.ijk.media.player.AndroidMediaPlayer;
import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;
import tv.danmaku.ijk.media.player.TextureMediaPlayer;

/**
 * @author Haoz
 * @date 2018/4/9.
 */
public class IjkVideoView extends FrameLayout implements MediaController.MediaPlayerControl {

    private static final String TAG = "IjkVideoView";

    private Uri mUri;
    private Map<String, String> mHeaders;

    private static final int STATE_ERROR = -1;
    private static final int STATE_IDLE = 0;
    private static final int STATE_PREPARING = 1;
    private static final int STATE_PREPARED = 2;
    private static final int STATE_PLAYING = 3;
    private static final int STATE_PAUSED = 4;
    private static final int STATE_PLAYBACK_COMPLETED = 5;

    private int mCurrentState = STATE_IDLE;
    private int mTargetState = STATE_IDLE;

    private IRenderView.ISurfaceHolder mSurfaceHolder = null;
    private IMediaPlayer mMediaPlayer = null;

    private int mVideoWidth;
    private int mVideoHeight;
    private int mSurfaceWidth;
    private int mSurfaceHeight;
    private int mVideoRotationDegree;
    private IMediaController mMediaController;
    private IMediaPlayer.OnCompletionListener mOnCompletionListener;
    private IMediaPlayer.OnPreparedListener mOnPreparedListener;
    private int mCurrentBufferPrecentage;
    private IMediaPlayer.OnErrorListener mOnErrorListener;
    private IMediaPlayer.OnInfoListener mOnInfoListener;
    private int mSeekWhenPrepared;
    private boolean mCanPause = true;
    private boolean mCanSeekBack = true;
    private boolean mCanSeekForward = true;

    private Context mAppContext;
    private Settings mSettings;
    private IRenderView mRenderView;
    private int mVideoSarNum;
    private int mVideoSarDen;

    private InfoHudViewHolder mHudViewHolder;

    private long mPrepareStartTime = 0;
    private long mPrepareEndTime = 0;

    private long mSeekStartTime = 0;
    private long mSeekEndTime = 0;

    private TextView subtitleDisplay;

    public IjkVideoView(@NonNull Context context) {
        super(context);
    }

    public IjkVideoView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public IjkVideoView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void initVideoView(Context context) {
        mAppContext = context.getApplicationContext();
        mSettings = new Settings(mAppContext);

        initBackground();

        initRenders();


    }

    private boolean mEnableBackgroundPlay = false;

    public void setRenderView(IRenderView rendView) {
        if (mRenderView != null) {
            if (mMediaPlayer != null) {
                mMediaPlayer.setDisplay(null);
            }

            View renderView = mRenderView.getView();
        }
    }

    public void setRender(int render) {
        switch (render) {
            case RENDER_NONE:
                break;
        }
    }

    /**
     * 启动后台播放
     */
    private void initBackground() {
        mEnableBackgroundPlay = mSettings.getEnableBackgroundPlay();
        if (mEnableBackgroundPlay) {
            MediaPlayerService.intentToStart(getContext());
            mMediaPlayer = MediaPlayerService.getMediaPlayer();
            if (mHudViewHolder != null) {
                mHudViewHolder.setMediaPlayer(mMediaPlayer);
            }
        }
    }

    public static final int RENDER_NONE = 0;
    public static final int RENDER_SURFACE_VIEW = 1;
    public static final int RENDER_TEXTURE_VIEW = 2;

    private List<Integer> mAllRenders = new ArrayList<>();
    private int mCurrentRenderIndex = 0;
    private int mCurrentRender = RENDER_NONE;

    private void initRenders() {
        mAllRenders.clear();

        if (mSettings.getEnableSurfaceView()) {
            mAllRenders.add(RENDER_SURFACE_VIEW);
        }

        if (mSettings.getEnableTextureView() && Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH)
            ;
        {
            mAllRenders.add(RENDER_TEXTURE_VIEW);
        }

        if (mSettings.getEnableNoView()) {
            mAllRenders.add(RENDER_NONE);
        }

        if (mAllRenders.isEmpty()) {
            mAllRenders.add(RENDER_SURFACE_VIEW);
        }

        mCurrentRender = mAllRenders.get(mCurrentRenderIndex);

    }

    @Override
    public void start() {
        if (isInPlaybackState()) {
            mMediaPlayer.start();
            mCurrentState = STATE_PLAYING;
        }
        mTargetState = STATE_PLAYING;
    }

    @Override
    public void pause() {
        if(isInPlaybackState()) {
            mMediaPlayer.pause();
            mCurrentState = STATE_PAUSED;
        }
        mTargetState = STATE_PAUSED;
    }

    private boolean isInPlaybackState() {
        return (mMediaPlayer != null &&
                mCurrentState != STATE_ERROR &&
                mCurrentState != STATE_IDLE &&
                mCurrentState != STATE_PREPARED);
    }

    @Override
    public int getDuration() {
        return 0;
    }

    @Override
    public int getCurrentPosition() {
        if(isInPlaybackState()) {
            return (int) mMediaPlayer.getCurrentPosition();
        }
        return 0;
    }

    @Override
    public void seekTo(int msec) {
        if(isInPlaybackState()) {
            mSeekStartTime = System.currentTimeMillis();
            mMediaPlayer.seekTo(msec);
            mSeekWhenPrepared = 0;
        } else {
            mSeekWhenPrepared = msec;
        }
    }

    IRenderView.IRenderCallback mSHCallback = new IRenderView.IRenderCallback() {
        @Override
        public void onSurfaceCreated(@NonNull IRenderView.ISurfaceHolder holder, int width, int height) {
            if (holder.getRenderView() != mRenderView) {
                Log.e(TAG, "onSurfaceCreated: unmatched render callback");
                return;
            }

            mSurfaceHolder = holder;
            if (mMediaPlayer != null) {

            }
        }

        @Override
        public void onSurfacehanged(@NonNull IRenderView.ISurfaceHolder holder, int format, int width, int height) {
            if (holder.getRenderView() != mRenderView) {
                Log.e(TAG, "onSurfaceCreated: unmatched render callback\n");
                return;
            }

            mSurfaceWidth = width;
        }

        @Override
        public void onSurfaceDestroyed(@NonNull IRenderView.ISurfaceHolder holder) {

        }
    };

    private void bindSurfaceHolder(IMediaPlayer mp, IRenderView.ISurfaceHolder holder) {
        if (mp == null) {
            return;
        }

        if (holder == null) {
            mp.setDisplay(null);
            return;
        }
        holder.bindToMediaPlayer(mp);
    }

    private void openVideo() {
        if (mUri == null || mSurfaceHolder == null) {
            return;
        }
        release(false);

        AudioManager am = (AudioManager) mAppContext.getSystemService(Context.AUDIO_SERVICE);
        am.requestAudioFocus(null, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);

        mMediaPlayer = createPlayer(mSettings.getPlay());
        final Context context = getContext();
//        mMediaPlayer.setOnPreparedListener();
    }

    IMediaPlayer.OnVideoSizeChangedListener mSizeChangedListener =
            new IMediaPlayer.OnVideoSizeChangedListener() {
                @Override
                public void onVideoSizeChanged(IMediaPlayer mp, int width, int height, int sarNum, int sarDen) {
                    mVideoWidth = mp.getVideoWidth();
                    mVideoHeight = mp.getVideoHeight();
                    mVideoSarNum = mp.getVideoSarNum();
                    mVideoSarDen = mp.getVideoSarDen();
                    if (mVideoWidth != 0 && mVideoHeight != 0) {
                        if (mRenderView != null) {
                            mRenderView.setVideoSize(mVideoWidth, mVideoHeight);
                            mRenderView.setVideoSampleAspectRatio(mVideoSarNum, mVideoSarDen);
                        }
                        requestLayout();
                    }
                }
            };


    IMediaPlayer.OnPreparedListener mPreparedListener = new IMediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(IMediaPlayer mp) {
            mPrepareEndTime = System.currentTimeMillis();
            mHudViewHolder.updateLoadCost(mPrepareEndTime - mPrepareStartTime);
            mCurrentState = STATE_PREPARED;

            mMediaPlayer.seekTo(0);
            ((IjkMediaPlayer) mMediaPlayer).setSpeed(1);

            if (mOnPreparedListener != null) {
                mOnPreparedListener.onPrepared(mMediaPlayer);
            }
            if (mMediaController != null) {
                mMediaController.setEnabled(true);
            }
            mVideoWidth = mp.getVideoWidth();
            mVideoHeight = mp.getVideoHeight();

            int seekToPosition = mSeekWhenPrepared;
            if (seekToPosition != 0) {
                seekTo(seekToPosition);
            }
            if (mVideoWidth != 0 && mVideoHeight != 0) {

                if (mRenderView != null) {
                    mRenderView.setVideoSize(mVideoWidth, mVideoHeight);
                    mRenderView.setVideoSampleAspectRatio(mVideoSarNum, mVideoSarDen);
                    if (!mRenderView.shouldWaitForResize() || mSurfaceWidth == mVideoWidth &&
                    mSurfaceHeight == mVideoHeight){
                        if (mTargetState == STATE_PLAYING) {
                            start();
                            if(mMediaController != null) {
                                mMediaController.show();
                            }
                        } else if(!isPlaying() &&
                                (seekToPosition != 0 || getCurrentPosition() > 0)){
                            if(mMediaController != null) {
                                mMediaController.show(0);
                            }
                        }
                    }
                }
            } else {
                if(mTargetState == STATE_PLAYING) {
                    start();
                }
            }

        }
    };

    public void release(boolean cleartargetstate) {
        if (mMediaPlayer != null) {
            mMediaPlayer.reset();
            mMediaPlayer.release();
            mMediaPlayer = null;
            mCurrentState = STATE_IDLE;
            if (cleartargetstate) {
                mTargetState = STATE_IDLE;
            }
            AudioManager am = (AudioManager) mAppContext.getSystemService(Context.AUDIO_SERVICE);
            am.abandonAudioFocus(null);
        }
    }

    public IMediaPlayer createPlayer(int playerType) {
        IMediaPlayer mediaPlayer = null;

        switch (playerType) {
            case Settings.PV_PLAYER_IjkExoMediaPlayer:
                IjkExoMediaPlayer IjkExoMediaPlayer = new IjkExoMediaPlayer(mAppContext);
                mediaPlayer = IjkExoMediaPlayer;
                break;
            case Settings.PV_PLAYER_AndroidMediaPlayer:
                AndroidMediaPlayer androidMediaPlayer = new AndroidMediaPlayer();
                mediaPlayer = androidMediaPlayer;
                break;
            case Settings.PV_PLAYER_IjkMediaPlayer:
            default:
                IjkMediaPlayer ijkMediaPlayer = null;
                if (mUri != null) {
                    ijkMediaPlayer = new IjkMediaPlayer();
                    IjkMediaPlayer.native_setLogLevel(IjkMediaPlayer.IJK_LOG_DEBUG);

                    if (mSettings.getUsingMediaCodec()) {
                        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "meidacodec", 1);
                        if (mSettings.getUsingMediaCodecAutoRotate()) {
                            ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec-auto-rotate", 1);
                        } else {
                            ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec-auto-rotate", 0);
                        }
                        if (mSettings.getMediaCodecHandleResolutionChange()) {
                            ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec-handle-resolution-change", 1);
                        } else {
                            ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec-handle-resolution-change", 0);
                        }
                    } else {
                        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec", 0);
                    }

                    if (mSettings.getUsingOpenSLES()) {
                        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "opensles", 1);
                    } else {
                        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "opensles", 0);
                    }

                    String pixelFormat = mSettings.getPixelFormat();
                    if (TextUtils.isEmpty(pixelFormat)) {
                        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "overlay-format", IjkMediaPlayer.SDL_FCC_RV32);
                    } else {
                        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "overlay-format", pixelFormat);
                    }
                    ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "framedrop", 1);
                    ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "start-on-prepared", 0);

                    ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "http-detect-range-support", 0);

                    ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_CODEC, "skip-loop-filter", 48);
                }
                mediaPlayer = ijkMediaPlayer;
                break;
        }

        if (mSettings.getEnableDetachedSurfaceTextureView()) {
            mediaPlayer = new TextureMediaPlayer(mediaPlayer);
        }

        return mediaPlayer;
    }

    @Override
    public boolean isPlaying() {
        return false;
    }

    @Override
    public int getBufferPercentage() {
        return 0;
    }

    @Override
    public boolean canPause() {
        return false;
    }

    @Override
    public boolean canSeekBackward() {
        return false;
    }

    @Override
    public boolean canSeekForward() {
        return false;
    }

    @Override
    public int getAudioSessionId() {
        return 0;
    }
}
