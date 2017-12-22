package com.example.utsav.speechtotext;
import java.util.ArrayList;
import java.util.Locale;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import android.os.Handler;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Build;
import android.util.Log;
import android.widget.Button;

import static android.text.TextUtils.isDigitsOnly;
class Global{
    public static boolean dis = false;
    public static int distance;
    public static boolean deg = false;
    public static int degree;
}
public class MainActivity extends Activity {
    private static final String TAG = "bluetooth1";
    Button Fwd, Bwd,Left,Right,Stop;

    private BluetoothAdapter btAdapter = null;
    private BluetoothSocket btSocket = null;
    private OutputStream outStream = null;

    // SPP UUID service
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    // MAC-address of Bluetooth module (you must edit this line)
    private static String address = "00:21:13:01:A0:A4";
    private TextView txtSpeechInput;
    private ImageButton btnSpeak;
    private final int REQ_CODE_SPEECH_INPUT = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Fwd = (Button) findViewById(R.id.fwd);
        Bwd = (Button) findViewById(R.id.bwd);
        Left = (Button) findViewById(R.id.left);
        Right = (Button) findViewById(R.id.right);
        Stop = (Button) findViewById(R.id.stp);
        txtSpeechInput = (TextView) findViewById(R.id.txtSpeechInput);
        btnSpeak = (ImageButton) findViewById(R.id.btnSpeak);
        btAdapter = BluetoothAdapter.getDefaultAdapter();

        Fwd.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                sendData("*forward#");
               // Toast.makeText(getBaseContext(), "Forward", Toast.LENGTH_SHORT).show();
            }
        });

        Bwd.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                sendData("*backward#");
                //Toast.makeText(getBaseContext(), "Backward", Toast.LENGTH_SHORT).show();
            }
        });
        Left.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                sendData("*left#");
                //Toast.makeText(getBaseContext(), "Left", Toast.LENGTH_SHORT).show();
            }
        });

        Right.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                sendData("*right#");
                //Toast.makeText(getBaseContext(), "Right", Toast.LENGTH_SHORT).show();
            }
        });
        Stop.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                sendData("*stp#");
                //Toast.makeText(getBaseContext(), "Stop", Toast.LENGTH_SHORT).show();
            }
        });
        checkBTState();
        // hide the action bar
        getActionBar().hide();

        btnSpeak.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                promptSpeechInput();
                txtSpeechInput.addTextChangedListener(
                new TextWatcher() {
                    @Override public void onTextChanged(CharSequence s, int start, int before, int count) { }
                    @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

                    private Timer timer=new Timer();
                    private final long DELAY = 0; // milliseconds

                    @Override
                    public void afterTextChanged(final Editable s) {


                        String[] str = txtSpeechInput.getText().toString().split(" ");
                        for (int i = 0;i<str.length;i++){
                            if(i>0){
                                if(str[i].equals("meter") || str[i].equals("meters") || str[i].equals("m") || str[i].equals("metres")|| str[i].equals("metre")){
                                    if(isDigitsOnly(str[i-1])){
                                        Global.distance = Integer.parseInt(str[i-1]);
                                        Global.dis = true;
                                    }
                                }
                                if(str[i].equals("degree") || str[i].equals("degrees")){
                                    if(isDigitsOnly(str[i-1])){
                                        Global.degree = Integer.parseInt(str[i-1]);
                                        Global.deg = true;
                                    }
                                }


                            }
                        }
                        timer.cancel();
                        timer = new Timer();
                        timer.schedule(
                                new TimerTask() {
                                    @Override
                                    public void run() {
                                        // TODO: do what you need here (refresh list)
                                        // you will probably need to use runOnUiThread(Runnable action) for some specific actions

                                            if(kmp("forward",txtSpeechInput.getText().toString())){
                                                runOnUiThread(new Runnable() {
                                                    public void run() {
                                                    sendData("*forward#");
                                                    //Toast.makeText(getBaseContext(), "Forward", Toast.LENGTH_SHORT).show();
                                                        if(Global.dis) {
                                                            int delay = Global.distance*35000;
                                                            new Handler().postDelayed(new Runnable() {
                                                                @Override
                                                                public void run() {
                                                                    sendData("*stop#");
                                                                }
                                                            }, delay);
                                                        }
                                                    }
                                                });

                                            }
                                            else if(kmp("backward",txtSpeechInput.getText().toString())){
                                                runOnUiThread(new Runnable() {
                                                    public void run() {
                                                    sendData("*backward#");
                                                    //Toast.makeText(getBaseContext(), "Backward", Toast.LENGTH_SHORT).show();
                                                        if(Global.dis) {
                                                            int delay = Global.distance*35000;
                                                            new Handler().postDelayed(new Runnable() {
                                                                @Override
                                                                public void run() {
                                                                    sendData("*stop#");
                                                                }
                                                            }, delay);
                                                        }
                                                    }
                                                });
                                            }
                                            else if(kmp("left",txtSpeechInput.getText().toString()) && !txtSpeechInput.getText().toString().contains("turn")){
                                                runOnUiThread(new Runnable() {
                                                    public void run() {
                                                        sendData("*left#");
                                                     //   Toast.makeText(getBaseContext(), "Left", Toast.LENGTH_SHORT).show();
                                                        new Handler().postDelayed(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                sendData("*stop#");
                                                            }
                                                        }, 10000);
                                                    }
                                                });
                                            }
                                            else if(kmp("right",txtSpeechInput.getText().toString())&& !txtSpeechInput.getText().toString().contains("turn")){
                                                runOnUiThread(new Runnable() {
                                                    public void run() {
                                                        sendData("*right#");
                                                       // Toast.makeText(getBaseContext(), "Right", Toast.LENGTH_SHORT).show();
                                                        new Handler().postDelayed(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                sendData("*stop#");
                                                            }
                                                        }, 10000);
                                                    }
                                                });
                                            }
                                            else if(kmp("stop",txtSpeechInput.getText().toString())){
                                                runOnUiThread(new Runnable() {
                                                    public void run() {
                                                        sendData("*stop#");
                                                        //Toast.makeText(getBaseContext(), "Stop", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                            }
                                            else if(kmp("turn",txtSpeechInput.getText().toString())){
                                                runOnUiThread(new Runnable() {
                                                    public void run() {
                                                        if(txtSpeechInput.getText().toString().contains("left")){
                                                            if(Global.deg) {
                                                                int delay = ((int)Global.degree/10) * 1000;
                                                                sendData("*left#");
                                                                new Handler().postDelayed(new Runnable() {
                                                                    @Override
                                                                    public void run() {
                                                                        sendData("*stop#");
                                                                    }
                                                                }, delay);

                                                            }
                                                        }

                                                        else if(txtSpeechInput.getText().toString().contains("right")){
                                                            if(Global.deg) {
                                                                int delay = ((int)Global.degree/10) * 1000;
                                                                sendData("*right#");
                                                                new Handler().postDelayed(new Runnable() {
                                                                    @Override
                                                                    public void run() {
                                                                        sendData("*stop#");
                                                                    }
                                                                }, delay);

                                                            }
                                                        }
                                                    }
                                                });
                                            }
                                            else{
                                                runOnUiThread(new Runnable() {
                                                    public void run() {

                                                        Toast.makeText(getBaseContext(), "Invalid", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                            }

                                        }

                                },
                                DELAY
                        );
                    }
                }
        );
            }
        });


    }
    public int[] patt(String[] str){
        int[] pat = new int[str.length];
        int i = 1;int j = 0;
        pat[0]= 0;
        /*for (int l=0;l<txt.length;l++){
            System.out.print(txt[l]+" ");
        }*/
        //System.out.println();
        while(i<str.length){
            //System.out.println("> "+j+" "+i);
            if(str[j].equals(str[i])){
                pat[i] = j+1;
                i++;
                j++;
            }else{
                while(true){
                    //          System.out.println(">> "+j+" "+i);
                    if(j>0){
                        j = pat[j-1];
                        if(str[j].equals(str[i])){
                            pat[i] = j+1;
                            i++;
                            j++;
                            break;
                        }
                    }else if(j==0){
                        pat[i] = j;
                        i++;
                        break;
                    }

                }
            }
        }
        return pat;
    }

    public boolean Contains(String[] str,String[] txt,int[] pat){
        int m = 0;
        int i = 0;
        boolean pre = false;
        while(i<txt.length){
            //  System.out.println("?? "+i+" "+m);
            if(str[m].equals(txt[i])){
                if(m == str.length-1){
                    pre = true;
                    break;
                }
                m++;
                i++;
            }else if(m>0){
                m = pat[m-1];
            }else if(m==0){
                i++;
            }
        }
        //System.out.println(pre);
        return pre;
    }
    public boolean kmp(String st,String text){
        System.out.println(">>"+st+" "+text);

        String[] str = new String[st.length()];
        String[] txt = new String[text.length()];

        for (int l=0;l<str.length;l++){
            str[l] = String.valueOf(st.charAt(l));
        }
        for (int l=0;l<txt.length;l++){
            txt[l] = String.valueOf(text.charAt(l));
        }
        /*for (int l=0;l<txt.length;l++){
            System.out.print(txt[l]+" ");
        }*/
        //System.out.println(str.length+" "+txt.length);

        /*for (int k = 0; k < str.length; k++) {
            System.out.print(pat[k]+" ");
       }*/
        int[] pat = patt(str);
        String[] d = {"d","o","n","'","t"};
        String[] n = {"n","o","t"};
        String[] nt = {"n","o"};
        int[] dA = patt(d);
        int[] nA = patt(n);
        int[] ntA = patt(nt);
        if(Contains(str,txt,pat) && !Contains(d,txt,dA) && !Contains(n,txt,nA)&& !Contains(nt,txt,ntA)){
            return true;
        }else
            return false;
    }
    /**
     * Showing google speech input dialog
     * */
    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                getString(R.string.speech_prompt));
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.speech_not_supported),
                    Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Receiving speech input
     * */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    txtSpeechInput.setText(result.get(0).toLowerCase());
                    //System.out.println(txtSpeechInput.getText());
                    //Toast.makeText(getApplicationContext(),
                      //      result.get(0),
                        //    Toast.LENGTH_SHORT).show();
                    //sendData("*forward#");

                }
                break;
            }

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {
        if(Build.VERSION.SDK_INT >= 10){
            try {
                final Method  m = device.getClass().getMethod("createInsecureRfcommSocketToServiceRecord", new Class[] { UUID.class });
                return (BluetoothSocket) m.invoke(device, MY_UUID);
            } catch (Exception e) {
                Log.e(TAG, "Could not create Insecure RFComm Connection",e);
            }
        }
        return  device.createRfcommSocketToServiceRecord(MY_UUID);
    }

    @Override
    public void onResume() {
        super.onResume();

        Log.d(TAG, "...onResume - try connect...");

        // Set up a pointer to the remote node using it's address.
        BluetoothDevice device = btAdapter.getRemoteDevice(address);

        // Two things are needed to make a connection:
        //   A MAC address, which we got above.
        //   A Service ID or UUID.  In this case we are using the
        //     UUID for SPP.

        try {
            btSocket = createBluetoothSocket(device);
        } catch (IOException e1) {
            errorExit("Fatal Error", "In onResume() and socket create failed: " + e1.getMessage() + ".");
        }

    /*try {
      btSocket = device.createRfcommSocketToServiceRecord(MY_UUID);
    } catch (IOException e) {
      errorExit("Fatal Error", "In onResume() and socket create failed: " + e.getMessage() + ".");
    }*/

        // Discovery is resource intensive.  Make sure it isn't going on
        // when you attempt to connect and pass your message.
        btAdapter.cancelDiscovery();

        // Establish the connection.  This will block until it connects.
        Log.d(TAG, "...Connecting...");
        try {
            btSocket.connect();
            Log.d(TAG, "...Connection ok...");
        } catch (IOException e) {
            try {
                btSocket.close();
            } catch (IOException e2) {
                errorExit("Fatal Error", "In onResume() and unable to close socket during connection failure" + e2.getMessage() + ".");
            }
        }

        // Create a data stream so we can talk to server.
        Log.d(TAG, "...Create Socket...");

        try {
            outStream = btSocket.getOutputStream();
        } catch (IOException e) {
            errorExit("Fatal Error", "In onResume() and output stream creation failed:" + e.getMessage() + ".");
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        Log.d(TAG, "...In onPause()...");

        if (outStream != null) {
            try {
                outStream.flush();
            } catch (IOException e) {
                errorExit("Fatal Error", "In onPause() and failed to flush output stream: " + e.getMessage() + ".");
            }
        }

        try     {
            btSocket.close();
        } catch (IOException e2) {
            errorExit("Fatal Error", "In onPause() and failed to close socket." + e2.getMessage() + ".");
        }
    }

    private void checkBTState() {
        // Check for Bluetooth support and then check to make sure it is turned on
        // Emulator doesn't support Bluetooth and will return null
        if(btAdapter==null) {
            errorExit("Fatal Error", "Bluetooth not support");
        } else {
            if (btAdapter.isEnabled()) {
                Log.d(TAG, "...Bluetooth ON...");
            } else {
                //Prompt user to turn on Bluetooth
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, 1);
            }
        }
    }

    private void errorExit(String title, String message){
        Toast.makeText(getBaseContext(), title + " - " + message, Toast.LENGTH_LONG).show();
        finish();
    }

    private void sendData(String message) {
        byte[] msgBuffer = message.getBytes();

        Log.d(TAG, "...Send data: " + message + "...");

        try {
            outStream.write(msgBuffer);
        } catch (IOException e) {
            String msg = "In onResume() and an exception occurred during write: " + e.getMessage();
            if (address.equals("00:00:00:00:00:00"))
                msg = msg + ".\n\nUpdate your server address from 00:00:00:00:00:00 to the correct address on line 35 in the java code";
            msg = msg +  ".\n\nCheck that the SPP UUID: " + MY_UUID.toString() + " exists on server.\n\n";

            errorExit("Fatal Error", msg);
        }
    }
}



