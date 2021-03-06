package com.example.myapplication;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class MyMqttService extends Service {

    public final String TAG = "MyMqttService";

    public String Host = "tcp://192.168.0.117:1883";
    public String Username = "admin";
    public String Password = "public";
    public static String public_topic = "publish";
    public static String response_topic = "response";

    /**
     * Client id
     */
    public String CLIENTSIDE = "test2";
    private static MqttAndroidClient mqttAndroidClient;
    private MqttConnectOptions mMqttConnectOptions;

    public MyMqttService() {
    }

    public static void startService(Context ctx){
        ctx.startService(new Intent(ctx, MyMqttService.class));
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        init();
        return super.onStartCommand(intent, flags, startId);
    }

    private void init() {
        mqttAndroidClient = new MqttAndroidClient(this, Host, CLIENTSIDE);
        mqttAndroidClient.setCallback(mqttCallback);
        // config class
        mMqttConnectOptions = new MqttConnectOptions();
        mMqttConnectOptions.setCleanSession(true);
        mMqttConnectOptions.setConnectionTimeout(10);
        mMqttConnectOptions.setUserName(Username);
        mMqttConnectOptions.setPassword(Password.toCharArray());
        boolean doConnect = true;
        String message = "{\"terminal_uid\":\"" + CLIENTSIDE + "\"}";
        try{
            mMqttConnectOptions.setWill(public_topic,message.getBytes(),2,false);
        }catch (Exception e){
            e.printStackTrace();
            doConnect = false;
            iMqttActionListener.onFailure(null,e);
        }

        if(doConnect){
            doClientConnection();
        }

    }

    private void doClientConnection() {
        if(!mqttAndroidClient.isConnected() && isConnectIsNormal()){
            try {
                mqttAndroidClient.connect(mMqttConnectOptions,null,iMqttActionListener);
            }catch (MqttException e){
                e.printStackTrace();
            }
        }
    }


    public static void publish(String message){
        try {
            mqttAndroidClient.publish(public_topic,message.getBytes(),2,false);
        }catch (MqttException e){
            e.printStackTrace();
        }
    }

    public static void dismiss(){
        try {
            mqttAndroidClient.disconnect();
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void response(String message){
        try {
            mqttAndroidClient.publish(public_topic,message.getBytes(),2,false);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }



    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }


    /**
     * ????????????????????????
     */
    private boolean isConnectIsNormal() {
        ConnectivityManager connectivityManager = (ConnectivityManager) this.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectivityManager.getActiveNetworkInfo();
        if (info != null && info.isAvailable()) {
            String name = info.getTypeName();
            Log.i(TAG, "?????????????????????" + name);
            return true;
        } else {
            Log.i(TAG, "??????????????????");
            /*????????????????????????????????????3??????????????????*/
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    doClientConnection();
                }
            }, 3000);
            return false;
        }
    }

    private MqttCallback mqttCallback = new MqttCallback() {
        @Override
        public void connectionLost(Throwable cause) {
            // ?????????????????????
            Log.i(TAG, "connection missing");
            // doClientConnection();
        }

        @Override
        public void messageArrived(String topic, MqttMessage message) throws Exception {
            Log.i(TAG, "??????????????? " + new String(message.getPayload()));
            // ???????????????????????????Toast???????????????????????????UI???????????????????????????EventBus????????????
            Toast.makeText(getApplicationContext(), "messageArrived: " + new String(message.getPayload()), Toast.LENGTH_LONG).show();
            // ????????????????????????????????????????????????????????????????????????????????????????????????
            response("message arrived");
        }

        @Override
        public void deliveryComplete(IMqttDeliveryToken token) {
            Log.i(TAG, "deliver Complete");
        }
    };

    private IMqttActionListener iMqttActionListener = new IMqttActionListener(){

        @Override
        public void onSuccess(IMqttToken asyncActionToken) {
            Log.i(TAG, "???????????? ");
            Toast.makeText(MyMqttService.this,"connect success",Toast.LENGTH_SHORT).show();
            try {
                // ?????????????????????????????????????????????
                mqttAndroidClient.subscribe(public_topic, 2);
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
            exception.printStackTrace();
            Log.i(TAG, "???????????? ");
            Toast.makeText(MyMqttService.this,"connect failed",Toast.LENGTH_SHORT).show();
            // ?????????????????????????????????????????????????????????
            doClientConnection();
        }
    };


    @Override
    public void onDestroy() {
        try {
            mqttAndroidClient.disconnect();
        } catch (MqttException e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }
}