package edu.teco.dustradarnonegame;

import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

import edu.teco.dustradarnonegame.blebridge.KeepAliveManager;
import edu.teco.dustradarnonegame.bluetooth.BLEService;
import edu.teco.dustradarnonegame.data.DataService;
import edu.teco.dustradarnonegame.gps.GPSService;
import edu.teco.dustradarnonegame.http.HTTPIntent;
import edu.teco.dustradarnonegame.http.HTTPService;

import static edu.teco.dustradarnonegame.blebridge.BLEBridgeHandler.*;

public class RecordingActivity extends AppCompatActivity {
    
    private Button stopRecordind;
    boolean doubleBackToExitPressedOnce = false;
    private String deviceAddress;
    
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recorcing);
    
        deviceAddress = getIntent().getExtras().getString(DataService.EXTRA_DATASERVICE_ADDRESS);
        Intent startRecordingIntent = new Intent(DataService.BROADCAST_DATASERVICE_START_RECORDING);
        startRecordingIntent.putExtra(DataService.EXTRA_DATASERVICE_ADDRESS, deviceAddress);
        RecordingActivity.this.sendBroadcast(startRecordingIntent);
    
        Intent startTransmitIntent = new Intent(HTTPService.BROADCAST_HTTPSERVICE_START_TRANSMIT);
        startTransmitIntent.putExtra(DataService.EXTRA_DATASERVICE_ADDRESS, deviceAddress);
        RecordingActivity.this.sendBroadcast(startTransmitIntent);
    
        stopRecordind = (Button) findViewById(R.id.stop_recording_btn);
        
        stopRecordind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopServices();
                onDoubleBack();
                //finish();
               // System.exit(0);
            }
        });
        
    }
    
    @Override
    public void onBackPressed() {
        Log.i("GameActivity", "RecordingActivity: onBackPressed()");
        
  
        if (doubleBackToExitPressedOnce) {
            onDoubleBack();
            stopServices();
            super.onBackPressed();
            return;
        }
    
        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Tap twice and fast to exit", Toast.LENGTH_SHORT).show();
    
        new Handler().postDelayed(new Runnable() {
        
            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 200);
       
    }
    @Override
    public void onPause() {
        Log.i(deviceAddress, "onPause");
        unregisterHandlerReceiver();
        
        Intent stopRecordingIntent = new Intent(DataService.BROADCAST_DATASERVICE_STOP_RECORDING);
        stopRecordingIntent.putExtra(DataService.EXTRA_DATASERVICE_ADDRESS, deviceAddress);
        RecordingActivity.this.getApplicationContext().sendBroadcast(stopRecordingIntent);
    
        Intent stopTransmitIntent = new Intent(HTTPService.BROADCAST_HTTPSERVICE_STOP_TRANSMIT);
        stopTransmitIntent.putExtra(DataService.EXTRA_DATASERVICE_ADDRESS, deviceAddress);
        RecordingActivity.this.getApplicationContext().sendBroadcast(stopTransmitIntent);
        super.onPause();
    }
    
    
    @Override
    public void onResume() {
        super.onResume();
        Log.i(deviceAddress, "onResume");
    
        registerHandlerReceiver();
        
        Intent startRecordingIntent = new Intent(DataService.BROADCAST_DATASERVICE_START_RECORDING);
        startRecordingIntent.putExtra(DataService.EXTRA_DATASERVICE_ADDRESS, deviceAddress);
        RecordingActivity.this.sendBroadcast(startRecordingIntent);
    
        Intent startTransmitIntent = new Intent(HTTPService.BROADCAST_HTTPSERVICE_START_TRANSMIT);
        startTransmitIntent.putExtra(DataService.EXTRA_DATASERVICE_ADDRESS, deviceAddress);
        RecordingActivity.this.sendBroadcast(startTransmitIntent);
    }
    
    public void onDoubleBack(){
        Intent resetIntent = new Intent(getApplicationContext(), IntroMainActivity.class);
        resetIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        getApplicationContext().startActivity(resetIntent);
    }
    
    private void stopServices() {
        KeepAliveManager.stopService(this);
        
        DataService.stopService(this);
        BLEService.stopService(this);
        GPSService.stopService(this);
        HTTPService.stopService(this);
        isTransmitting = false;
    }
    
    // BroadcastReceivers // not used in none-gamified version
    
    private final BroadcastReceiver mHandlerReceiver = (new BroadcastReceiver() {
        
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            
            if (DataService.BROADCAST_DATASERVICE_DATA_STORED.equals(action)) {
                //updateView();
                return;
            }
            
            if (BLEService.BROADCAST_BLESERVICE_DATA_AVAILABLE.equals(action)) {
                String address = intent.getStringExtra(BLEService.EXTRA_BLESERVICE_ADDRESS);
                if (deviceAddress.equals(address)) {
                    //lastData = intent.getStringExtra(BLEService.EXTRA_BLESERVICE_DATA);
                    //bleConnectionStatus = "Connected";
                    //updateView();
                }
                return;
            }
            
            if (BLEService.BROADCAST_BLESERVICE_GATT_CONNECTED.equals(action)) {
                String address = intent.getStringExtra(BLEService.EXTRA_BLESERVICE_ADDRESS);
                if (deviceAddress.equals(address)) {
                    // bleConnectionStatus = "Connected";
                    //updateView();
                }
                return;
            }
            
            if (BLEService.BROADCAST_BLESERVICE_GATT_DISCONNECTED.equals(action)) {
                String address = intent.getStringExtra(BLEService.EXTRA_BLESERVICE_ADDRESS);
                if (deviceAddress.equals(address)) {
                    // bleConnectionStatus = "Disconnected";
                    // updateView();
                }
                return;
            }
            
            if (GPSService.BROADCAST_GPSSERVICE_LOCATION_AVAILABLE.equals(action)) {
                //  gpsConnectionStatus = "Available";
                //updateView();
                return;
            }
            
            if (HTTPIntent.BROADCAST_HTTPINTENT_POST_SUCCESS.equals(action)) {
                //updateView();
                return;
            }
            
            if (HTTPService.BROADCAST_HTTPSERVICE_TIMEOUT.equals(action)) {
                //updateView();
                return;
            }
            
            if (HTTPService.BROADCAST_HTTPSERVICE_NOTHING_TO_TRANSMIT.equals(action)) {
               // lastViewUpdate = 0;
               // updateView();
                return;
            }
        }
    });
    
    private void registerHandlerReceiver() {
        IntentFilter filter = new IntentFilter();
        
        filter.addAction(DataService.BROADCAST_DATASERVICE_DATA_STORED);
        filter.addAction(BLEService.BROADCAST_BLESERVICE_DATA_AVAILABLE);
        filter.addAction(BLEService.BROADCAST_BLESERVICE_GATT_CONNECTED);
        filter.addAction(BLEService.BROADCAST_BLESERVICE_GATT_DISCONNECTED);
        filter.addAction(GPSService.BROADCAST_GPSSERVICE_LOCATION_AVAILABLE);
        filter.addAction(HTTPIntent.BROADCAST_HTTPINTENT_POST_SUCCESS);
        filter.addAction(HTTPService.BROADCAST_HTTPSERVICE_TIMEOUT);
        filter.addAction(HTTPService.BROADCAST_HTTPSERVICE_NOTHING_TO_TRANSMIT);
    
        RecordingActivity.this.registerReceiver(mHandlerReceiver, filter);
    }
    
    private void unregisterHandlerReceiver() {
        try {
            RecordingActivity.this.unregisterReceiver(mHandlerReceiver);
        }
        catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

}
