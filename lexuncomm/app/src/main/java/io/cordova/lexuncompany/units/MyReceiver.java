package io.cordova.lexuncompany.units;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

import cn.jpush.android.api.JPushInterface;
import io.cordova.lexuncompany.bean.CardItem;
import io.cordova.lexuncompany.bean.base.Request;
import io.cordova.lexuncompany.view.CardContentActivity;

/**
 * 自定义接收器
 * <p>
 * 如果不定义这个 Receiver，则：
 * 1) 默认用户会打开主界面
 * 2) 接收不到自定义消息
 */
public class MyReceiver extends BroadcastReceiver {
    private static final String TAG = "JIGUANG-Example";
    private static JSONObject mJsoCustomMessage = null;

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            Bundle bundle = intent.getExtras();
            Log.e(TAG, "[MyReceiver] onReceive - " + intent.getAction() + ", extras: " + printBundle(bundle));

            if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
                String regId = bundle.getString(JPushInterface.EXTRA_REGISTRATION_ID);
                Log.d(TAG, "[MyReceiver] 接收Registration Id : " + regId);
                //send the Registration Id to your server...

            } else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
                Log.d(TAG, "[MyReceiver] 接收到推送下来的自定义消息: " + bundle.getString(JPushInterface.EXTRA_MESSAGE));
//                mJsoCustomMessage = new JsonParser().parse(bundle.getString(JPushInterface.EXTRA_MESSAGE)).getAsJsonObject();
                mJsoCustomMessage = new JSONObject(bundle.getString(JPushInterface.EXTRA_MESSAGE));

            } else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
                int notifactionId = bundle.getInt(JPushInterface.EXTRA_NOTIFICATION_ID);
                mJsoCustomMessage = new JSONObject(bundle.getString(JPushInterface.EXTRA_MESSAGE));
                Log.d(TAG, "[MyReceiver] 接收到推送下来的通知的ID: " + notifactionId);
                Log.e(TAG,"推送消息内容体："+mJsoCustomMessage.toString());

            } else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
                Log.d(TAG, "[MyReceiver] 用户点击打开了通知");
                Log.d(TAG, "[MyReceiver] 用户点击接收到推送下来的自定义消息: " + bundle.getString(JPushInterface.EXTRA_MESSAGE));

                jump(context);

            } else if (JPushInterface.ACTION_RICHPUSH_CALLBACK.equals(intent.getAction())) {
                Log.d(TAG, "[MyReceiver] 用户收到到RICH PUSH CALLBACK: " + bundle.getString(JPushInterface.EXTRA_EXTRA));
                //在这里根据 JPushInterface.EXTRA_EXTRA 的内容处理代码，比如打开新的Activity， 打开一个网页等..

            } else if (JPushInterface.ACTION_CONNECTION_CHANGE.equals(intent.getAction())) {
                boolean connected = intent.getBooleanExtra(JPushInterface.EXTRA_CONNECTION_CHANGE, false);
                Log.w(TAG, "[MyReceiver]" + intent.getAction() + " connected state change to " + connected);
            } else {
                Log.d(TAG, "[MyReceiver] Unhandled intent - " + intent.getAction());
            }
        } catch (Exception e) {

        }

    }

    private void jump(Context context) throws JSONException {
        //打开自定义的Activity
        Intent i = new Intent();
        if (mJsoCustomMessage == null) {
            Log.e(TAG, "1");
            i.setClass(context, CardContentActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            context.startActivity(i);
        } else {
            i.putExtra("cardId", mJsoCustomMessage.getString("CardID"));
            i.putExtra("url", mJsoCustomMessage.getString("Url"));
            Log.e(TAG, mJsoCustomMessage.getString("CardID"));
            Log.e(TAG, mJsoCustomMessage.getString("Url"));

            if (FormatUtils.getIntances().isEmpty(mJsoCustomMessage.getString("Url"))) {
                Log.e(TAG, "2");
                i.setClass(context, CardContentActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                context.startActivity(i);
            } else {
                Log.e(TAG, "3");
                if (CardContentActivity.isRunning) {
                    Log.e(TAG, "4");

                    i.setAction(Request.Broadcast.RELOADURL);
                    i.putExtra("id", mJsoCustomMessage.getString("CardID"));
                    i.putExtra("url", mJsoCustomMessage.getString("Url"));
                    context.sendBroadcast(i);
                } else {
                    Log.e(TAG, "5");

                    CardItem cardItem = new CardItem();
                    cardItem.setCardID(mJsoCustomMessage.getString("CardID"));
                    cardItem.setCardUrl(mJsoCustomMessage.getString("Url"));
                    i.setClass(context, CardContentActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    i.putExtra("cardItem", cardItem);
                    i.putExtra("type", Request.StartActivityRspCode.PUSH_CARDCONTENT_JUMP);
                    context.startActivity(i);

                }
            }
        }
    }

    // 打印所有的 intent extra 数据
    private static String printBundle(Bundle bundle) {
        StringBuilder sb = new StringBuilder();
        for (String key : bundle.keySet()) {
            if (key.equals(JPushInterface.EXTRA_NOTIFICATION_ID)) {
                sb.append("\nkey:" + key + ", value:" + bundle.getInt(key));
            } else if (key.equals(JPushInterface.EXTRA_CONNECTION_CHANGE)) {
                sb.append("\nkey:" + key + ", value:" + bundle.getBoolean(key));
            } else if (key.equals(JPushInterface.EXTRA_EXTRA)) {
                if (TextUtils.isEmpty(bundle.getString(JPushInterface.EXTRA_EXTRA))) {
                    Log.i(TAG, "This message has no Extra data");
                    continue;
                }

                try {
                    JSONObject json = new JSONObject(bundle.getString(JPushInterface.EXTRA_EXTRA));
                    Iterator<String> it = json.keys();

                    while (it.hasNext()) {
                        String myKey = it.next();
                        sb.append("\nkey:" + key + ", value: [" +
                                myKey + " - " + json.optString(myKey) + "]");
                    }
                } catch (JSONException e) {
                    Log.e(TAG, "Get message extra JSON error!");
                }

            } else {
                sb.append("\nkey:" + key + ", value:" + bundle.getString(key));
            }
        }
        return sb.toString();
    }
}
