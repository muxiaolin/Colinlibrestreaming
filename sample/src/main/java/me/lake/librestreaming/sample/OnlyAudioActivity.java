package me.lake.librestreaming.sample;

import android.Manifest;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;


import com.google.android.material.snackbar.Snackbar;

import me.lake.librestreaming.client.RESClient;
import me.lake.librestreaming.core.listener.RESConnectionListener;
import me.lake.librestreaming.filter.audiofilter.SetVolumeAudioFilter;
import me.lake.librestreaming.model.RESConfig;
import me.lake.librestreaming.model.Size;

/**
 * @author 彭林
 * @version v1.0
 * @desc librestreaming
 * @date 2020/12/28
 */
public class OnlyAudioActivity extends AppCompatActivity implements RESConnectionListener, View.OnClickListener {
    private final static String TAG = "OnlyAudioActivity";
    protected RESClient resClient;
    protected TextView tv_speed;
    protected TextView tv_rtmp;
    protected Button btn_toggle;
    protected boolean started;
    RESConfig resConfig;
    //    protected String rtmpaddr = "rtmp://push-emergency5g.tingdao.com/jlkasjdfliajdsf/dafdsaflkjasdlfas234234?auth_key=1604889208-0-0-99223c445f5855f2e063f714c4a6b871";
//    protected String rtmpaddr = "rtmp://10.57.9.88/live/livestream";
    protected String rtmpaddr = "rtmp://push-emergency5g.tingdao.com/test123123/11123123?auth_key=1609755793-0-0-951e3cb0f05f3553901020188ea3e2b4";
    protected Handler mainHander = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            tv_speed.setText("byteSpeed=" + (resClient.getAVSpeed() / 1024) + ";drawFPS=" + resClient.getDrawFrameRate() + ";sendFPS=" + resClient.getSendFrameRate() + ";sendbufferfreepercent=" + resClient.getSendBufferFreePercent());
            sendEmptyMessageDelayed(0, 3000);
            if (resClient.getSendBufferFreePercent() <= 0.05) {
                Toast.makeText(OnlyAudioActivity.this, "sendbuffer is full,netspeed is low!", Toast.LENGTH_SHORT).show();
            }
        }
    };

    boolean authorized = false;
    private static final int REQUEST_STREAM = 1;
    private static String[] PERMISSIONS_STREAM = {
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        started = false;
        setContentView(R.layout.activity_only_audio);
        tv_speed = (TextView) findViewById(R.id.tv_speed);
        tv_rtmp = (TextView) findViewById(R.id.tv_rtmp);
        btn_toggle = (Button) findViewById(R.id.btn_toggle);
        btn_toggle.setOnClickListener(this);

        resClient = new RESClient(true);
        resConfig = RESConfig.obtain();
        resConfig.setFilterMode(RESConfig.FilterMode.HARD);
        resConfig.setTargetVideoSize(new Size(720, 480));
        resConfig.setBitRate(750 * 1024);
        resConfig.setVideoFPS(20);
        resConfig.setVideoGOP(1);
        resConfig.setRenderingMode(RESConfig.RenderingMode.OpenGLES);
        resConfig.setRtmpAddr(rtmpaddr);
        if (!resClient.prepare(resConfig)) {
            resClient = null;
            Log.e(TAG, "prepare,failed!!");
            Toast.makeText(this, "RESClient prepare failed", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        Size s = resClient.getVideoSize();
        Log.d(TAG, "version=" + resClient.getVertion());
        resClient.setConnectionListener(this);

        mainHander.sendEmptyMessage(0);
        resClient.setSoftAudioFilter(new SetVolumeAudioFilter());

        verifyPermissions();
    }

    public void verifyPermissions() {
        int RECORD_AUDIO_permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);
        int WRITE_EXTERNAL_STORAGE_permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (RECORD_AUDIO_permission != PackageManager.PERMISSION_GRANTED ||
                WRITE_EXTERNAL_STORAGE_permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    PERMISSIONS_STREAM,
                    REQUEST_STREAM
            );
            authorized = false;
        } else {
            authorized = true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_STREAM) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                authorized = true;
            }
        }
    }

    @Override
    protected void onDestroy() {
        if (mainHander != null) {
            mainHander.removeCallbacksAndMessages(null);
        }
        if (started) {
            resClient.stopStreaming();
        }
        if (resClient != null) {
            resClient.destroy();
            resClient = null;
        }
        super.onDestroy();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_toggle:
                if (authorized) {
                    if (!started) {
                        btn_toggle.setText("stop");
                        resClient.startStreaming();
                    } else {
                        btn_toggle.setText("start");
                        resClient.stopStreaming();
                    }
                    started = !started;
                } else {
                    Snackbar.make(getWindow().getDecorView().getRootView(), "streaming need permissions!", Snackbar.LENGTH_LONG)
                            .setAction("auth", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    verifyPermissions();
                                }
                            }).show();
                }
                break;
        }
    }

    @Override
    public void onOpenConnectionResult(int result) {
        if (result == 0) {
            Log.e(TAG, "server IP = " + resClient.getServerIpAddr());
        } else {
            Toast.makeText(this, "startfailed", Toast.LENGTH_SHORT).show();
        }
        /**
         * result==0 success
         * result!=0 failed
         */
        tv_rtmp.setText("open=" + result);
    }

    @Override
    public void onWriteError(int errno) {
        if (errno == 9) {
            resClient.stopStreaming();
            resClient.startStreaming();
            Toast.makeText(this, "errno==9,restarting", Toast.LENGTH_SHORT).show();
        }
        /**
         * failed to write data,maybe restart.
         */
        tv_rtmp.setText("writeError=" + errno);
    }

    @Override
    public void onCloseConnectionResult(int result) {
        /**
         * result==0 success
         * result!=0 failed
         */
        tv_rtmp.setText("close=" + result);
    }

}
