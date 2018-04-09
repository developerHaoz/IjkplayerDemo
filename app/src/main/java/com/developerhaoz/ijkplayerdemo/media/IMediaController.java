package com.developerhaoz.ijkplayerdemo.media;

import android.view.View;
import android.widget.MediaController;

/**
 * @author Haoz
 * @date 2018/4/9.
 */
public interface IMediaController {
    void hide();

    boolean isShowing();

    void setAnchorView(View view);

    void setEnabled(boolean enabled);

    void setMediaPlayer(MediaController.MediaPlayerControl player);

    void show(int timeout);

    void show();

    void showOnce(View view);
}






























