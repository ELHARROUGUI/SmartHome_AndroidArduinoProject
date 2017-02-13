package ilisi.smarthomeproject;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Locale;
import android.content.ActivityNotFoundException;


import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import android.widget.TextView;
import android.speech.RecognizerIntent;

public class ledControl extends AppCompatActivity {

    public static String EXTRA_DHT = "DHT";
    static final int DHT_ACTIVITY = 2;

    Button btnOn1, btnOn2, btnOn3, btnOff1, btnOff2, btnOff3, btnDis, btnTemp;
    String address = null;
    private ProgressDialog progress;
    BluetoothAdapter myBluetooth = null;
    BluetoothSocket btSocket = null;
    private boolean isBtConnected = false;
    byte[] buffer = new byte[1024];
    private InputStream btInputStream = null;
    int bytes;
    private final int SPEECH_RECOGNITION_CODE = 1;
    private TextView txtOutput;
    private ImageButton btnMicrophone;

    //SPP UUID. Look for it
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent newint = getIntent();
        address = newint.getStringExtra(DeviceList.EXTRA_ADDRESS); //receive the address of the bluetooth device

        //view of the ledControl
        setContentView(R.layout.activity_led_control);

        //call the widgtes
        btnOn1 = (Button) findViewById(R.id.button2);
        btnOff1 = (Button) findViewById(R.id.button3);
        btnOn2 = (Button) findViewById(R.id.buttonLED2on);
        btnOff2 = (Button) findViewById(R.id.buttonLED2off);
        btnOn3 = (Button) findViewById(R.id.buttonLED3on);
        btnOff3 = (Button) findViewById(R.id.buttonLED3off);
        btnDis = (Button) findViewById(R.id.button4);
        btnTemp = (Button) findViewById(R.id.buttonTemp);

        btnMicrophone = (ImageButton) findViewById(R.id.btn_mic);
        btnMicrophone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSpeechToText();
            }
        });

        new ConnectBT().execute(); //Call the class to connect

        //commands to be sent to bluetooth
        btnOn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendCmd("LED1:1");      //method to turn on
            }
        });
        btnOn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendCmd("LED2:1");      //method to turn on
            }
        });
        btnOn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendCmd("LED3:1");      //method to turn on
            }
        });

        btnOff1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendCmd("LED1:0");   //method to turn off
            }
        });
        btnOff2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendCmd("LED2:0");   //method to turn off
            }
        });
        btnOff3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendCmd("LED3:0");   //method to turn off
            }
        });

        btnDis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Disconnect(); //close connection
            }
        });
        btnTemp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendCmd("T");
            }
        });

    }

    private void Disconnect() {
        if (btSocket != null) //If the btSocket is busy
        {
            try {
                btSocket.close(); //close connection
            } catch (IOException e) {
                msg("Error");
            }
        }
        finish(); //return to the first layout

    }


    private void sendCmd(String CMD) {
        if (btSocket != null) {
            try {
                btSocket.getOutputStream().write(CMD.toString().getBytes());

                if (CMD.equals("T")) {
                    String readMessage = new String("khawya");
                    //btSocket.getOutputStream().write(CMD.toString().getBytes());
                    try {

                        int i;
                        btSocket.getInputStream();
                        bytes = btSocket.getInputStream().read(buffer);
                        //bytes = btSocket.getInputStream().read(buffer);
                        //bytes = btSocket.getInputStream().read(buffer);


                        readMessage = new String(buffer, StandardCharsets.UTF_8);

                        Log.d("ledControl", readMessage);
                        //msg(readMessage);
                    } catch (Exception e) {
                        msg("Error readMessage");
                    }

                    // Make an intent to start next activity.
                    Intent i = new Intent(ledControl.this, DHTActivity.class);
                    //Change the activity.
                    i.putExtra(EXTRA_DHT, readMessage); //this will be received at ledControl (class) Activity

                    startActivityForResult(i, DHT_ACTIVITY);

                }

                // Keep looping to listen for received message
            } catch (IOException e) {
                msg("Error");
            }
        }
    }

    // fast way to call Toast
    private void msg(String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_led_control, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class ConnectBT extends AsyncTask<Void, Void, Void>  // UI thread
    {
        private boolean ConnectSuccess = true; //if it's here, it's almost connected

        @Override
        protected void onPreExecute() {
            progress = ProgressDialog.show(ledControl.this, "Connecting...", "Please wait!!!");  //show a progress dialog
        }

        @Override
        protected Void doInBackground(Void... devices) //while the progress dialog is shown, the connection is done in background
        {
            try {
                if (btSocket == null || !isBtConnected) {
                    myBluetooth = BluetoothAdapter.getDefaultAdapter();//get the mobile bluetooth device
                    BluetoothDevice dispositivo = myBluetooth.getRemoteDevice(address);//connects to the device's address and checks if it's available
                    btSocket = dispositivo.createInsecureRfcommSocketToServiceRecord(myUUID);//create a RFCOMM (SPP) connection
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                    btSocket.connect();//start connection
                }
            } catch (IOException e) {
                ConnectSuccess = false;//if the try failed, you can check the exception here
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) //after the doInBackground, it checks if everything went fine
        {
            super.onPostExecute(result);

            if (!ConnectSuccess) {
                msg("Connection Failed. Is it a SPP Bluetooth? Try again.");
                finish();
            } else {
                msg("Connected.");
                isBtConnected = true;
            }
            progress.dismiss();
        }
    }

    // Call Back method  to get the Message form other Activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // check if the request code is same as what is passed  here it is 2
        switch (requestCode) {
            case SPEECH_RECOGNITION_CODE: {
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    String text = result.get(0);
                    txtOutput.setText(text);
                }
            }break;
            case DHT_ACTIVITY:
                sendCmd("T");
                break;
        }
    }

    /**
     * Start speech to text intent. This opens up Google Speech Recognition API dialog box to listen the speech input.
     * */
    private void startSpeechToText() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                "Speak something...");
        try {
            startActivityForResult(intent, SPEECH_RECOGNITION_CODE);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    "Sorry! Speech recognition is not supported in this device.",
                    Toast.LENGTH_SHORT).show();
        }
    }

}
