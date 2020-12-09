package me.lake.librestreaming.filter.base;

import android.graphics.Canvas;


/**
 * Created by lake on 02/12/16.
 * librestreaming project.
 */
public interface IFakeView {

    void init(int VWidth, int VHeight);

    void onDraw(int cameraTexture, int targetFrameBuffer, Canvas canvas);

    void destroy();
}
