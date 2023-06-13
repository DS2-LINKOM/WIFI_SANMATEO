package mx.linkom.wifi_sanmateo;

import android.content.Intent;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;



public class NavegadorActivity extends mx.linkom.wifi_sanmateo.Menu2 {

    private mx.linkom.wifi_sanmateo.Configuracion Conf;
    private FirebaseAuth fAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navegador);

        fAuth = FirebaseAuth.getInstance();

        Conf = new mx.linkom.wifi_sanmateo.Configuracion(this);

        if(Conf.getNoti().equals("PLUMA ENTRADA")){

            Intent i = new Intent(getApplication(), RegistroPlumasEntradasActivity.class);
            startActivity(i);
            finish();

        }else if(Conf.getNoti().equals("PLUMA SALIDA")){

            Intent i = new Intent(getApplication(), RegistroPlumasSalidasActivity.class);
            startActivity(i);
            finish();

        }else if(Conf.getNoti().equals("ERROR")){

            Intent i = new Intent(getApplication(), DashboardVigilante.class);
            startActivity(i);
            finish();

        }


    }

    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main2, menu);

        return true;
    }


    @Override
    public void onBackPressed(){
        super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(), mx.linkom.wifi_sanmateo.DashboardVigilante.class);
        startActivity(intent);
    }
}