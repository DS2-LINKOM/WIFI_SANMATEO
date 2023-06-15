package mx.linkom.wifi_sanmateo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;

public class LimiteVelocidad extends AppCompatActivity {

    MediaPlayer mp;
    Handler han3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_limite_velocidad);
        han3 = new Handler();

        mp = MediaPlayer.create(this, R.raw.audio6_graciasentrada);
        mp.start();
        esperarParaCambio3(8000);
    }

    public void esperarParaCambio3(int milisegundos) {


        han3.postDelayed(new Runnable() {
            public void run() {
                Intent intent = new Intent(LimiteVelocidad.this, DashboardEntradas.class);
                startActivity(intent);
            }
        }, milisegundos);
    }


    @Override
    public void onBackPressed(){
        super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(), DashboardEntradas.class);
        startActivity(intent);
        finish();
    }
}