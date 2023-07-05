package mx.linkom.wifi_sanmateo;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import mx.linkom.wifi_sanmateo.adaptadores.ListasClassGrid;
import mx.linkom.wifi_sanmateo.adaptadores.adaptador_Modulo;


public class ListaGrupalSalidaActivity extends mx.linkom.wifi_sanmateo.Menu implements View.OnClickListener {

    TextView evento;
    private GridView gridList;
    private Configuracion Conf;
    JSONArray ja1;
    ArrayList<String> ubicacion;

    LinearLayout  rlDenegado,rlVista;
    TextView  tvMensaje,tvMensaje2,tvMensaje4;
    int tiempo;

    LinearLayout button;
    Button Regresar;
    MediaPlayer mp;

    private Handler handler;
    private Runnable runnable;
    ScrollView scrollView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acitivity_listagrupalsalida);
        tiempo=10000;

        handler = new Handler();

        runnable = new Runnable() {
            @Override
            public void run() {
                // Handle idle time here
                Intent intent = new Intent(ListaGrupalSalidaActivity.this, ProtectorPantalla.class);
                startActivity(intent);
            }
        };

        scrollView = (ScrollView) findViewById(R.id.scrollView);

        mp = MediaPlayer.create(this,R.raw.audio9_visitasnombres);
        mp.start();
        rlVista = (LinearLayout) findViewById(R.id.rlVista);
        rlDenegado = (LinearLayout) findViewById(R.id.rlDenegado);
        tvMensaje = (TextView) findViewById(R.id.setMensaje);
        tvMensaje2 = (TextView) findViewById(R.id.setMensaje2);
        tvMensaje4 = (TextView) findViewById(R.id.setMensaje4);

        Conf = new Configuracion(this);
        ubicacion = new ArrayList<String>();
        evento = (TextView) findViewById(R.id.evento);
        gridList = (GridView) findViewById(R.id.gridList);
        button = (LinearLayout) findViewById(R.id.button);

        evento.setText(Conf.getEvento());

        Regresar = (Button) findViewById(R.id.regresar);

        Regresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent docugen = new Intent(getApplication(), DashboardSalidas.class);
                startActivity(docugen);
                finish();
            }
        });

        Global.ocultarBarrasNavegacionEstado(this);
        Global.aumentarVolumen(this);
        Global.evitarSuspenderPantalla(this);

        //SI ES ACEPTADO O DENEGAODO
        if(Conf.getST().equals("Aceptado")){
            rlVista.setVisibility(View.VISIBLE);
            rlDenegado.setVisibility(View.GONE);
            invitados();

        }else if(Conf.getST().equals("Denegado")){
            rlDenegado.setVisibility(View.VISIBLE);
            rlVista.setVisibility(View.GONE);
            tvMensaje.setText("Error: Aún no es hora de entrada");
            Notificar("");
            esperarParaCambio(tiempo);
        }


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
    public void onStop() {
        super.onStop();
        handler.removeCallbacks(runnable); // Reset the delay timer
        Log.e("CICLO", "onStop");
    }




    public void invitados() {
        String URL = "https://sanmateoresidencial.mx/plataforma/casetaV2/controlador/WIFI_SM/vst_gru_3.php?bd_name="+Conf.getBd()+"&bd_user="+Conf.getBdUsu()+"&bd_pwd="+Conf.getBdCon();
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                button.setVisibility(View.VISIBLE);
                Regresar.setVisibility(View.VISIBLE);
                if(response.equals("error")){
                    llenado2();
                }else {
                response = response.replace("][", ",");
                if (response.length() > 0) {
                    try {
                        ja1 = new JSONArray(response);

                        llenado();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Error ", "Id: " + error.toString());
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {


                Map<String, String> params = new HashMap<>();
                params.put("qr", Conf.getQR().trim());
                params.put("id_residencial", Conf.getResid().trim());
                return params;
            }
        };
        requestQueue.add(stringRequest);
    }




    public void llenado(){
        ArrayList<ListasClassGrid> ubicacion = new ArrayList<ListasClassGrid>();

        for (int i = 0; i < ja1.length(); i += 16) {
            try {

                ubicacion.add(new ListasClassGrid(ja1.getString(i + 7), "ID:"+ja1.getString(i + 0)));
            }catch (JSONException e){
                e.printStackTrace();
            }
        }

        gridList.setAdapter(new adaptador_Modulo(this, R.layout.activity_listas, ubicacion){
            @Override
            public void onEntrada(Object entrada, View view) {
                if (entrada != null) {

                    final TextView title = (TextView) view.findViewById(R.id.title);
                    if (title != null)
                        title.setText(((ListasClassGrid) entrada).getTitle());

                    final TextView subtitle = (TextView) view.findViewById(R.id.sub);
                    if (subtitle != null)
                        subtitle.setText(((ListasClassGrid) entrada).getSubtitle());

                    gridList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                            mp.stop();


                            int posicion=position*16;
                            try {
                                //RONDIN DIA
                                Conf.setIdvisita(ja1.getString(posicion));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            Intent i = new Intent(getApplicationContext(), AccesosSalidasGrupalActivity.class);
                            startActivity(i);
                            finish();


                        }
                    });


                }
            }

        });
    }


    public void llenado2(){
        ArrayList<ListasClassGrid> ubicacion = new ArrayList<ListasClassGrid>();



        ubicacion.add(new ListasClassGrid("Comunícate con tu anfitrión", ""));


        gridList.setAdapter(new adaptador_Modulo(this, R.layout.activity_listas, ubicacion){
            @Override
            public void onEntrada(Object entrada, View view) {
                if (entrada != null) {

                    final TextView title = (TextView) view.findViewById(R.id.title);
                    if (title != null)
                        title.setText(((ListasClassGrid) entrada).getTitle());

                    final TextView subtitle = (TextView) view.findViewById(R.id.sub);
                    if (subtitle != null)
                        subtitle.setText(((ListasClassGrid) entrada).getSubtitle());


                }
            }

        });
    }


    public void esperarParaCambio(int milisegundos) {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                finalizarApp();
            }
        }, milisegundos);
    }


    public void finalizarApp() {
        Intent i = new Intent(getApplicationContext(), DashboardSalidas   .class);
        startActivity(i);
        finish();

    }

    public void Notificar(String Visita){

        String URL = "https://sanmateoresidencial.mx/plataforma/casetaV2/controlador/WIFI_SM/vst_php9.php?bd_name="+Conf.getBd()+"&bd_user="+Conf.getBdUsu()+"&bd_pwd="+Conf.getBdCon();
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response){

                if(response.equals("error")){

                }else {

                }
            }
        }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("TAG","Error: " + error.toString());
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();


                params.put("visita",Visita);
                params.put("pluma_nombre",Conf.getNombrelPluma());
                params.put("pluma_token",Conf.getTokenVigi());


                return params;
            }
        };
        requestQueue.add(stringRequest);
    }


    @Override
    public void onBackPressed(){
        mp.stop();
        super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(), DashboardSalidas.class);
        startActivity(intent);
        finish();
    }

}