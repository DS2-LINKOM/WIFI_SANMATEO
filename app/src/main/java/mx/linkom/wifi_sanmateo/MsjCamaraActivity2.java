package mx.linkom.wifi_sanmateo;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;

public class MsjCamaraActivity2 extends   Menu {

    private Button button;
    MediaPlayer mp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mesjcam);

        Global.ocultarBarrasNavegacionEstado(this);
        Global.aumentarVolumen(this);
        Global.evitarSuspenderPantalla(this);

        mp = MediaPlayer.create(this,R.raw.audio8_salida);

//        button = (Button)findViewById(R.id.button);
//
//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//
//
//                Intent docugen = new Intent(getApplication(), EscaneoVisitaSalidaActivity.class);
//                startActivity(docugen);
//                finish();
//
//            }
//        });
    }

    @Override
    public void onStart() {
        super.onStart();
        mp.start();
        esperarParaCambio(5000);
    }

    public void esperarParaCambio(int milisegundos) {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                Intent i = new Intent(getApplicationContext(), EscaneoVisitaSalidaActivity.class);
                startActivity(i);
                finish();
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
