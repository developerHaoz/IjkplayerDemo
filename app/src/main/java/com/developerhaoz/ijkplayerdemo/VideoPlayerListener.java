package com.developerhaoz.ijkplayerdemo;

import tv.danmaku.ijk.media.player.IMediaPlayer;

/**
 * 提供回调的接口
 *
 * @author Haoz
 * @date 2018/4/6.
 */
public abstract class VideoPlayerListener implements IMediaPlayer.OnBufferingUpdateListener, IMediaPlayer.OnCompletionListener, IMediaPlayer.OnPreparedListener, IMediaPlayer.OnInfoListener, IMediaPlayer.OnVideoSizeChangedListener, IMediaPlayer.OnErrorListener, IMediaPlayer.OnSeekCompleteListener{
}
