package mx.linkom.wifi_sanmateo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ScrollView;

public class LimiteVelocidad extends mx.linkom.wifi_sanmateo.Menu implements View.OnClickListener {

    MediaPlayer mp;
    Handler han3;

    private Handler handler;
    private Runnable runnable;
    ScrollView scrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_limite_velocidad);
        han3 = new Handler();

        handler = new Handler();

        runnable = new Runnable() {
            @Override
            public void run() {
                // Handle idle time here
                Intent intent = new Intent(LimiteVelocidad.this, ProtectorPantalla.class);
                startActivity(intent);
            }
        };

        scrollView = (ScrollView) findViewById(R.id.scrollView);

        mp = MediaPlayer.create(this, R.raw.audio6_graciasentrada);
        mp.start();

        ViewTreeObserver.OnScrollChangedListener scrollChangedListener = new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                int scrollY = scrollView.getScrollY();
                // El valor de scrollY indica la posición vertical actual de la vista de desplazamiento
                // Puedes hacer algo aquí en función de la posición de desplazamiento
                Log.e("EVENT", "-_- Entradas Scroll");
                handler.removeCallbacks(runnable); // Reset the delay timer
                handler.postDelayed(runnable, Global.getTiemempoBloqueo()); // Set the delay to 5 seconds
            }
        };
        scrollView.getViewTreeObserver().addOnScrollChangedListener(scrollChangedListener);

        View layout = findViewById(android.R.id.content);
        layout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                Log.e("EVENT", "-_-");
                handler.removeCallbacks(runnable); // Reset the delay timer
                handler.postDelayed(runnable, Global.getTiemempoBloqueo()); // Set the delay to 5 seconds
                return false;
            }
        });


        // Get the view that you want to simulate a touch event for
        View view = layout;

        // Create a new MotionEvent object for the touch event
        long downTime = SystemClock.uptimeMillis();
        long eventTime = SystemClock.uptimeMillis() + 100;
        int action = MotionEvent.ACTION_DOWN;
        float x = view.getWidth() / 2f;
        float y = view.getHeight() / 2f;
        int metaState = 0;
        MotionEvent motionEvent = MotionEvent.obtain(
                downTime, eventTime, action, x, y, metaState
        );

        // Dispatch the touch event to the view
        view.dispatchTouchEvent(motionEvent);

        Global.ocultarBarrasNavegacionEstado(this);
        Global.aumentarVolumen(this);
        Global.evitarSuspenderPantalla(this);

        esperarParaCambio3(8000);
    }

    @Override
    public void onClick(View view) {
        // Handle click events here
        Log.e("EVENT", "-_- Entradas");
        handler.removeCallbacks(runnable); // Reset the delay timer
        handler.postDelayed(runnable, Global.getTiemempoBloqueo()); // Set the delay to 5 seconds
    }

    @Override
    public void onStop() {
        super.onStop();
        handler.removeCallbacks(runnable); // Reset the delay timer
        Log.e("CICLO", "onStop");
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