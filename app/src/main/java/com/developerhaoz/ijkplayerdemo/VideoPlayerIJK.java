package com.developerhaoz.ijkplayerdemo;

import android.content.Context;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import java.io.IOException;

import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

/**
 * @author Haoz
 * @date 2018/4/6.
 */
public class VideoPlayerIJK extends FrameLayout {

    private IMediaPlayer mMediaPlayer = null;

    private String mPath = "";

    private SurfaceView mSurfaceView;

    private VideoPlayerListener mListener;
    private Context mContext;

    public VideoPlayerIJK(@NonNull Context context) {
        super(context);
        initVideoView(context);
    }

    public VideoPlayerIJK(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initVideoView(context);
    }

    public VideoPlayerIJK(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initVideoView(context);
    }

    private void initVideoView(Context context) {
        this.mContext = context;
        setFocusable(true);
    }

    public void setVideoPath(String path) {
        if (TextUtils.equals("", mPath)) {
            mPath = path;
            createSurfaceView();
        } else {
            mPath = path;
            load();
        }
    }

    private void createSurfaceView() {
        mSurfaceView = new SurfaceView(mContext);
        mSurfaceView.getHolder().addCallback(new LmnSurfaceCallback());
        LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT
                , ViewGroup.LayoutParams.MATCH_PARENT, Gravity.CENTER);
        mSurfaceView.setLayoutParams(layoutParams);
        this.addView(mSurfaceView);
    }

    private class LmnSurfaceCallback implements SurfaceHolder.Callback {

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            load();
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {

        }
    }

    private void load() {
        createPlayer();
        try {
            mMediaPlayer.setDataSource(mPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        mMediaPlayer.setDisplay(mSurfaceView.getHolder());
        mMediaPlayer.prepareAsync();
    }

    private void createPlayer() {
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.setDisplay(null);
            mMediaPlayer.release();
        }
        IjkMediaPlayer ijkMediaPlayer = new IjkMediaPlayer();
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "meidacodec", 1);

        mMediaPlayer = ijkMediaPlayer;

        if (mListener != null) {
            mMediaPlayer.setOnPreparedListener(mListener);
            mMediaPlayer.setOnInfoListener(mListener);
            mMediaPlayer.setOnSeekCompleteListener(mListener);
            mMediaPlayer.setOnBufferingUpdateListener(mListener);
            mMediaPlayer.setOnErrorListener(mListener);
        }
    }

    public void setListener(VideoPlayerListener listener) {
        this.mListener = listener;
        if (mMediaPlayer != null) {
            mMediaPlayer.setOnPreparedListener(listener);
        }
    }

    public void start() {
        if (mMediaPlayer != null) {
            mMediaPlayer.start();
        }
    }

    public void release() {
        if (mMediaPlayer != null) {
            mMediaPlayer.reset();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    public void pause() {
        if (mMediaPlayer != null){
            mMediaPlayer.pause();
        }
    }

    public void stop(){
        if(mMediaPlayer != null){
            mMediaPlayer.stop();
        }
    }

    public void reset(){
        if(mMediaPlayer != null){
            mMediaPlayer.reset();
        }
    }

    public long getDuration(){
        if(mMediaPlayer != null){
            return mMediaPlayer.getDuration();
        } else {
            return  0;
        }
    }

    public long getCurrentPosition(){
        if(mMediaPlayer != null){
            return mMediaPlayer.getCurrentPosition();
        } else {
            return 0;
        }
    }

    public void seekTo(long l) {
        if(mMediaPlayer != null){
            mMediaPlayer.seekTo(l);
        }
    }
}
















