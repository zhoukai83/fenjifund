package kaizhou.fenjifund;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.Vibrator;
import android.util.Log;

import java.util.ArrayList;

public class FenJiService extends Service {

    public class FenJiServiceBinder extends Binder
    {
        Service getService()
        {
            return FenJiService.this;
        }
    }


    public void setThreshold(float value)
    {
        this.threshold = value / 100;
    }

    private boolean bRunning = true;

    private float threshold = (float) -2.0 / 100;

    private Thread runningThread;

    private final FenJiServiceBinder binder = new FenJiServiceBinder();

    public FenJiService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        bRunning = true;

        threshold = intent.getFloatExtra("Threshold", (float) -2.0) / 100;
        return binder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        bRunning = true;

        threshold = intent.getFloatExtra("Threshold", (float) -2.0) / 100;

        if(runningThread != null && !runningThread.isAlive())
        {
            runningThread.start();
        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        runningThread = new Thread(new Runnable() {
            @Override
            public void run() {
                FenJiHelper helper = new FenJiHelper();
                ArrayList<FenJiData> list = helper.FetchFenJiData();

                while(bRunning)
                {
                    try {
                        helper.UpdateFenJiValue(list);
                        helper.GetMotherFundValue(list);
                        if(helper.Notify(list, threshold))
                        {
                            Vibrator vibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
                            if (vibrator.hasVibrator()) {
                                long [] pattern = {100,400,100,400};   // ?? ?? ?? ??
                                vibrator.vibrate(pattern, -1);         //???????pattern ?????????index??-1
                            }
                        }

                        sendMessageToActivity(list);
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void sendMessageToActivity(ArrayList<FenJiData> list) {
        Intent intent = new Intent("FenJiList");
        /* You can also include some extra data. */
        intent.putParcelableArrayListExtra("data", list);
        sendBroadcast(intent);
    }

    @Override
    public void onDestroy() {
        bRunning = false;
        runningThread = null;
        Log.d("FenJiService", "Destroy");
        super.onDestroy();
    }
}
