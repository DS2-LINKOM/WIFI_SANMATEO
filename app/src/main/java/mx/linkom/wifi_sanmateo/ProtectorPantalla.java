package mx.linkom.wifi_sanmateo;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class ProtectorPantalla extends AppCompatActivity implements View.OnClickListener{

    private Configuracion Conf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_protector_pantalla);

        Conf = new Configuracion(this);
    }

    @Override
    public void onClick(View view) {
        // Handle click events here
        Log.e("CLIC", ":D -_- :o");
        if (Conf.getLOGIN().trim().equals("si")){
            Intent intent = new Intent(ProtectorPantalla.this, DashboardEntradas.class);
            startActivity(intent);
        }else {
            Intent intent = new Intent(ProtectorPantalla.this, MainActivity.class);
            startActivity(intent);
        }

    }
}