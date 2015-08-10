package kaizhou.fenjifund;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Vibrator;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;


public class MainActivity extends ActionBarActivity {
  ////  private Vibrator vibrator;
    EditText thresholdText;


    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            ArrayList<FenJiData> list = intent.getParcelableArrayListExtra("data");

            // Toast.makeText(context, message, Toast.LENGTH_SHORT).show();

            ListView listView = (ListView) findViewById(R.id.MyListView);

            //?????????????
            ArrayList<HashMap<String, String>> mylist = new ArrayList<>(list.size());

            for(FenJiData data : list)
            {
                HashMap<String, String> map = new HashMap<>();
                map.put("ItemCode", String.format("%s %s", data.aCode, data.aName));
                map.put("ItemYiJiaLv", Float.toString(data.yiJiaLv));
                mylist.add(map);
            }

            //????????===?ListItem
            SimpleAdapter mSchedule = new SimpleAdapter(context, //?????
                    mylist,//????
                    R.layout.my_listitem,//ListItem?XML??
                    new String[] {"ItemCode", "ItemYiJiaLv"},//?????ListItem?????
                    new int[] {R.id.ItemTitle,R.id.ItemText});//ListItem?XML???????TextView ID
            //??????
            listView.setAdapter(mSchedule);

            TextView text = (TextView) findViewById(R.id.textView);
            SimpleDateFormat   formatter   =   new SimpleDateFormat("HH:mm:ss");
            Date curDate   =   new   Date(System.currentTimeMillis());//??????
            String   str   =   formatter.format(curDate);
            text.setText(str);
        }
    };


    private FenJiService fenJiService;
    private boolean bound;
    private final ServiceConnection serviceConnection = new ServiceConnection() {
        public void onServiceConnected( ComponentName className, IBinder service ) {
            fenJiService = (FenJiService) ( (FenJiService.FenJiServiceBinder) service ).getService();
            bound = true;
        }

        public void onServiceDisconnected( ComponentName className ) {
            fenJiService = null;
            bound = false;
        }
    };

    void doBindService() {
        boolean bound = bindService( new Intent( this, FenJiService.class ), serviceConnection, Context.BIND_AUTO_CREATE );
        if ( bound ) {
            Log.d("MainAty", "Successfully bound to service" );
        }
        else {
            Log.d("MainAty", "Failed to bind service" );
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Vibrator vibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
        if (vibrator.hasVibrator()) {
            long [] pattern = {100,400,100,400};   // ?? ?? ?? ??
            vibrator.vibrate(pattern, -1);         //???????pattern ?????????index??-1
        } else {
            Log.d("Can Vibrate", "NO");
        }

        registerReceiver(mMessageReceiver, new IntentFilter("FenJiList"));

        thresholdText = (EditText)findViewById(R.id.editTextThreshold);
        thresholdText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                    String text = String.valueOf(thresholdText.getText());
                    if (!text.isEmpty() && fenJiService != null) {
                        fenJiService.setThreshold(Float.parseFloat(String.valueOf("-" + text)));
                    }
            }
        });

        this.findViewById(R.id.buttonStartService).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent serviceIntent = new Intent(MainActivity.this,FenJiService.class);

                String text = String.valueOf(thresholdText.getText());
                serviceIntent.putExtra("Threshold", Float.parseFloat(String.valueOf("-" + text)));
                startService(serviceIntent);
                doBindService();
            }
        });

        this.findViewById(R.id.buttonStopService).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent serviceIntent = new Intent(MainActivity.this, FenJiService.class);
                stopService(serviceIntent);
                unbindService(serviceConnection);
            }
        });

        this.findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("MainAty", "OnClick");
                Intent intent = new Intent("FenJiList");
                ArrayList<FenJiData> list = new ArrayList<FenJiData>();
                FenJiData item = new FenJiData();
                item.aCode = "2342";
                item.aName = "test";
                item.yiJiaLv = -2;
                list.add(item);
                intent.putParcelableArrayListExtra("data", list);
                sendBroadcast(intent);
            }
        });
    }
}
