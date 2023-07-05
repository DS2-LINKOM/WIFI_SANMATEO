package mx.linkom.wifi_sanmateo;

import android.content.Intent;
import android.media.AudioManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONArray;
import org.json.JSONException;
import org.opencv.android.OpenCVLoader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

import mx.linkom.wifi_sanmateo.deteccionPlacas.objectDetectorClass;


public class DashboardEntradas extends Menu implements View.OnClickListener {

    private FirebaseAuth fAuth;
    private Configuracion Conf;
    private Button button;
    JSONArray ja1;
    TextView nombreResi;
    TextView alerta2;
    ImageView bt;

    private Handler handler;
    private Runnable runnable;
    ScrollView scrollView;

    static {
        if (OpenCVLoader.initDebug()){
            Log.e("MainActivity", "OpenCV funcionando");
        }else {
            Log.e("MainActivity", "OpenCV no funciona");
        }
    }

    private objectDetectorClass objectDetectorClass;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_dashboardentradas);

        //actualizarIP();

        handler = new Handler();

        runnable = new Runnable() {
            @Override
                public void run() {
                    // Handle idle time here
                Intent intent = new Intent(DashboardEntradas.this, ProtectorPantalla.class);
                startActivity(intent);
            }
        };

        scrollView = (ScrollView) findViewById(R.id.scrollView);

        if (OpenCVLoader.initDebug()) Log.e("openCV", "Ya funciona :D");
        else Log.e("openCV", "NO funciona :D");

        try {
            objectDetectorClass = new objectDetectorClass(getAssets(), "detectPLacaLKM.tflite", "labelmapTf.txt", 320);
            Log.e("MainActivity", "Modelo cargado correctamente");
        } catch (IOException e) {
            Log.e("MainActivity", "Error al cargar modelo");
        }

        Global.ocultarBarrasNavegacionEstado(this);
        Global.aumentarVolumen(this);
        Global.evitarSuspenderPantalla(this);

        fAuth = FirebaseAuth.getInstance();
        Conf = new Configuracion(this);
        button = (Button)findViewById(R.id.button);

        nombreResi = (TextView)findViewById(R.id.nombreResi);
        alerta2 = (TextView)findViewById(R.id.alerta2);
        bt = (ImageView) findViewById(R.id.icono);

        nombreResi.setText("ENTRADAS "+Conf.getNomResi()+" : "+Conf.getNombre());
        alerta2.setText("¡ Bienvenido a "+Conf.getNomResi()+" !");

        Conf.setLOGIN("si");


        /*if(Conf.getMAC()==""){
            bt.setImageResource(R.drawable.ic_enlace_inactivo);
            button.setVisibility(View.GONE);
        }else{
           // bt.setImageResource(R.drawable.ic_enlace_activo);
            menu();
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent docugen = new Intent(getApplication(), MsjCamaraActivity.class);
                    startActivity(docugen);
                    finish();
                }
            });
        }*/

        menu();
        button.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {

                button.setEnabled(false);

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        button.setBackgroundResource(R.drawable.usuario_bbt_press);
                        button.setTextColor(0xFF5A6C81);

                        ViewGroup.LayoutParams params = button.getLayoutParams();
                        //Para tener en dp
                        params.width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 225, getResources().getDisplayMetrics());
                        params.height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 58, getResources().getDisplayMetrics());
                        button.setLayoutParams(params);

                        button.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 25); // set the text size to 20 density-independent pixels

                        Intent docugen = new Intent(getApplication(), MsjCamaraActivity.class);
                        startActivity(docugen);
                        finish();
                    }
                }, 300);

            }
        });

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


    }

    @Override
    public void onClick(View view) {
        // Handle click events here
        Log.e("EVENT", "-_- Entradas");
        handler.removeCallbacks(runnable); // Reset the delay timer
        handler.postDelayed(runnable, Global.getTiemempoBloqueo()); // Set the delay to 5 seconds
    }

    @Override
    public void onStart() {
        super.onStart();
        Registro();
        Sesion();
    }

    @Override
    public void onStop() {
        super.onStop();
        handler.removeCallbacks(runnable); // Reset the delay timer
        Log.e("CICLO", "onStop");
    }

    public void Registro (){
        try {
            fAuth.createUserWithEmailAndPassword(Global.EMAIL, Global.PASS)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Log.i("TAG","Se ha registrado exitosamente");
                            } else {
                                Log.e("TAG","Ha fallado el registro " + task.getException());

                            }
                        }
                    });
        } catch (Exception e) {
            Log.e("TAG","Error ",e);
        }
    }

    public void Sesion (){
        try {
            fAuth.signInWithEmailAndPassword(Global.EMAIL, Global.PASS)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Log.i("TAG","Se ha logeado exitosamente");
                            } else {
                                Log.e("TAG","Ha fallado la autenticación " + task.getException());
                            }
                        }
                    });
        }catch (Exception e) {
            Log.e("TAG","Error ",e);
        }
    }

    public void menu() {

        String URL = "https://sanmateoresidencial.mx/plataforma/casetaV2/controlador/WIFI_SM/menu.php?bd_name="+Conf.getBd()+"&bd_user="+Conf.getBdUsu()+"&bd_pwd="+Conf.getBdCon();
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                if(response.equals("error")){
                } else {
                    response = response.replace("][", ",");
                    if (response.length() > 0) {
                        try {
                            ja1 = new JSONArray(response);

                            if(ja1.getString(2).equals("1")) {
                                llenado();
                            }
                        } catch (JSONException e) {
                            Toast.makeText(getApplicationContext(), "Usuario y/o Contraseña Incorrectos", Toast.LENGTH_LONG).show();

                            e.printStackTrace();
                        }
                    }
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("TAG", "Error: " + error.toString());
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("id_residencial", Conf.getResid());

                return params;
            }
        };
        requestQueue.add(stringRequest);
    }

    public void llenado(){

        button.setVisibility(View.VISIBLE);

    }


    @Override
    public void onBackPressed(){
        super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(), DashboardEntradas.class);
        startActivity(intent);
        finish();
    }


    public void actualizarIP(){
        GetPublicIP task = new GetPublicIP(new GetPublicIP.OnTaskCompleted() {
            @Override
            public void onTaskCompleted(String result) {
                // Do something with the result
                Global.setIpCamaraPlacas(result);
                Toast.makeText(DashboardEntradas.this, "IP: "+result, Toast.LENGTH_SHORT).show();
            }
        });
        task.execute();
    }


    public static class GetPublicIP extends AsyncTask<Void, Void, String> {

        private OnTaskCompleted listener;

        public GetPublicIP(OnTaskCompleted listener) {
            this.listener = listener;
        }

        @Override
        protected String doInBackground(Void... params) {
            String publicIP = "";
            try {
                URL url = new URL("http://checkip.amazonaws.com/");
                URLConnection conn = url.openConnection();
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                publicIP = in.readLine();
                in.close();

                Log.e("IP", publicIP);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return publicIP;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            listener.onTaskCompleted(result);
        }

        public interface OnTaskCompleted {
            void onTaskCompleted(String result);
        }
    }


}
