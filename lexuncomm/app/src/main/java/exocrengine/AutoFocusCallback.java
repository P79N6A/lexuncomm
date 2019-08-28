package exocr.exocrengine;

import android.hardware.Camera;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 * description: 对焦, 每秒1次
 * create by kalu on 2018/11/20 10:25
 */
public class AutoFocusCallback implements Camera.AutoFocusCallback {

    private static final String TAG = AutoFocusCallback.class.getSimpleName();

    private static final long AUTOFOCUS_INTERVAL_MS = 1000L;

    private Handler autoFocusHandler;
    private int autoFocusMessage;

    public AutoFocusCallback() {
    }

    void setHandler(Handler autoFocusHandler, int autoFocusMessage) {
        this.autoFocusHandler = autoFocusHandler;
        this.autoFocusMessage = autoFocusMessage;
    }

    public void onAutoFocus(boolean success, Camera camera) {
        if (success) {
            Log.i(TAG, "myAutoFocusCallback: success...");
            //对焦成功则不再继续对焦
            //afsuccess = true;
            //return;
        } else {
            Log.i(TAG, "myAutoFocusCallback: 失败了...");
        }
        if (autoFocusHandler != null) {
            Message message = autoFocusHandler.obtainMessage(autoFocusMessage, success);
            // Simulate continuous autofocus by sending a focus request every
            // AUTOFOCUS_INTERVAL_MS milliseconds.
            //Log.d(TAG, "Got auto-focus callback; requesting another");
            autoFocusHandler.sendMessageDelayed(message, AUTOFOCUS_INTERVAL_MS);
            autoFocusHandler = null;
        } else {
            Log.d(TAG, "Got auto-focus callback, but no handler for it");
        }
    }
}