package ilisi.smarthomeproject;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import java.util.StringTokenizer;
import ilisi.smarthomeproject.ledControl;

public class DHTActivity extends AppCompatActivity {

    private String readMessage = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent newint = getIntent();
        readMessage = newint.getStringExtra(ledControl.EXTRA_DHT); //receive the address of the bluetooth device

        setContentView(R.layout.activity_dht);

        DHT result = treatInputStream(readMessage);

        TextView EtatCapteur = (TextView) findViewById(R.id.txtCapteur);
        TextView Humidity = (TextView) findViewById(R.id.txtHumid);
        TextView Temperature = (TextView) findViewById(R.id.txtTemp);

        Button Actualiser = (Button) findViewById(R.id.btnActualiser);
        Actualiser.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(DHTActivity.this, ledControl.class);
                setResult(ledControl.DHT_ACTIVITY,intent);
                finish();

            }
        });

        Button retRoom = (Button) findViewById(R.id.btnRetRooms);
        retRoom.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // TODO Auto-generated method stub
                // Make an intent to start next activity.
                System.exit(0);
            }
        });

        EtatCapteur.setText(result.getEtatCapteur());
        Humidity.setText(result.getHumidity());
        Temperature.setText(result.getTemperature());


    }


    private DHT treatInputStream(String readMessage) {


        if(!readMessage.contains("|"))
        {
            Intent intent = new Intent();
            setResult(ledControl.DHT_ACTIVITY,intent);
            finish();

        }

        DHT result = new DHT("OK", "27.00", "65.00");
        //System.out.println(readMessage);
        Log.d("ReadMessage", readMessage);
        StringTokenizer st = new StringTokenizer(readMessage,"|");
        if(st.hasMoreTokens()) {
            result.setHumidity(st.nextToken());
            Log.d("H", result.getHumidity());
            //System.out.println(result.getHumidity());
        }
        if(st.hasMoreTokens()) {
            result.setTemperature(st.nextToken());
            Log.d("T", result.getTemperature());
            //System.out.println(result.getTemperature());
        }
        return result;
    }


}
