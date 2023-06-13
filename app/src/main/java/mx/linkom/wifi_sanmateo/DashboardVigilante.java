package mx.linkom.wifi_sanmateo;

import static com.github.mikephil.charting.utils.ColorTemplate.*;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.github.mikephil.charting.charts.BarChart;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import mx.linkom.wifi_sanmateo.adaptadores.ListasClassColoresGrid;
import mx.linkom.wifi_sanmateo.adaptadores.adaptador_Modulo;


public class DashboardVigilante  extends Menu2 {

    private BarChart chart;


    private FirebaseAuth fAuth;
    private Configuracion Conf;
    JSONArray ja1,ja2,ja3,ja4;
    Handler handler = new Handler();
    private final int TIEMPO = 6000;
    //private final int TIEMPO = 30000;
    TextView fecha;
    int anio,mes,dia;
    private GridView gridList,gridList2;
    TextView nombreResi;

    TextView perma,sali,entr;
    String var1,var2,var3,var4,var5;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboardvigilantes);

        fAuth = FirebaseAuth.getInstance();
        Conf = new Configuracion(this);
        gridList = (GridView) findViewById(R.id.gridList);
        gridList2 = (GridView) findViewById(R.id.gridList2);

        perma = (TextView)findViewById(R.id.setPermanecen);
        sali = (TextView)findViewById(R.id.setSalidas);
        entr = (TextView)findViewById(R.id.setEntradas);

        fecha = (TextView) findViewById(R.id.fecha);
        nombreResi = (TextView)findViewById(R.id.nombreResi);

        nombreResi.setText("VIGILANTE: "+Conf.getNomResi());

        menu();

    }

    @Override
    public void onStart() {
        super.onStart();
        Registro();
        Sesion();
        onMapReady();
    }

    public void onMapReady() {
        handler.postDelayed(new Runnable() {
            public void run() {

                // función a ejecutar
                menu();
                //  Toast.makeText(getApplicationContext(), "Cuenta", Toast.LENGTH_SHORT).show();

                handler.postDelayed(this, TIEMPO);
            }

        }, TIEMPO);

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

                                Contador();
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

    public void Contador(){

        String URL = "https://sanmateoresidencial.mx/plataforma/casetaV2/controlador/WIFI_SM/contadores.php?bd_name="+Conf.getBd()+"&bd_user="+Conf.getBdUsu()+"&bd_pwd="+Conf.getBdCon();
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                response = response.replace("][",",");
                if (response.length()>0){
                    try {
                        ja4 = new JSONArray(response);
                        Info();
                        Entradas();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
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
                params.put("id_residencial", Conf.getResid().trim());

                return params;
            }
        };
        requestQueue.add(stringRequest);
    }

    public void Info(){
        try {
            //RESIDENTES


                if (ja4.getString(0) != "null") {
                    var1 = ja4.getString(0);

                } else {
                    var1 = "0";
                }

                if (ja4.getString(1) != "null") {
                    var2 = ja4.getString(1);
                } else {
                    var2 = "0";
                }

                if (ja4.getString(2) != "null") {
                    var3 = ja4.getString(2);
                } else {
                    var3 = "0";
                }

                if (ja4.getString(3) != "null") {
                    var4 = ja4.getString(3);
                } else {
                    var4 = "0";
                }

                if (ja4.getString(4) != "null") {
                    var5 = ja4.getString(4);
                } else {
                    var5 = "0";
                }


                int ent = Integer.parseInt(var2) + Integer.parseInt(var3);
                int per = Integer.parseInt(var4) + Integer.parseInt(var5);

                sali.setText(var1);
                entr.setText(String.valueOf(ent));
                perma.setText(String.valueOf(per));



        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void Entradas(){
        String URL = "https://sanmateoresidencial.mx/plataforma/casetaV2/controlador/WIFI_SM/plumas_registro_3.php?bd_name="+Conf.getBd()+"&bd_user="+Conf.getBdUsu()+"&bd_pwd="+Conf.getBdCon();
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
               // Log.e("TAG", "LINKOM ST: " + response );

                if(response.equals("error")){
                    Salidas();
                } else {

                    response = response.replace("][", ",");
                    if (response.length() > 0) {
                        try {
                            ja2 = new JSONArray(response);
                            llenado();
                            Salidas();
                        } catch (JSONException e) {
                            Salidas();
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
                params.put("guardia_de_entrada", Conf.getUsu().trim());

                return params;
            }
        };
        requestQueue.add(stringRequest);
    }

    public void Salidas(){

        String URL = "https://sanmateoresidencial.mx/plataforma/casetaV2/controlador/WIFI_SM/plumas_registro_4.php?bd_name="+Conf.getBd()+"&bd_user="+Conf.getBdUsu()+"&bd_pwd="+Conf.getBdCon();
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                if(response.equals("error")){
                } else {
                    response = response.replace("][", ",");
                    if (response.length() > 0) {
                        try {
                            ja3 = new JSONArray(response);
                            llenado2();
                        } catch (JSONException e) {

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
                params.put("guardia_de_entrada", Conf.getUsu().trim());

                return params;
            }
        };
        requestQueue.add(stringRequest);
    }

    public void llenado(){


        if(ja2.length()>0){
                ArrayList<ListasClassColoresGrid> ubicacion = new ArrayList<ListasClassColoresGrid>();

                for (int i = 0; i < ja2.length(); i += 3) {
                    try {
                        String[] t=ja2.getString(i + 0).split("");

                        if(t[0].equals("V")){
                            ubicacion.add(new ListasClassColoresGrid(ja2.getString(i + 1),"| "+ja2.getString(i + 2) ,"ID:"+ja2.getString(i + 0),"#1A9BCA"));

                        }else if(t[0].equals("A")){
                            ubicacion.add(new ListasClassColoresGrid(ja2.getString(i + 1),"| "+ja2.getString(i + 2) ,"ID:"+ja2.getString(i + 0),"#FFE31111"));

                        }

                    }catch (JSONException e){
                        e.printStackTrace();
                    }
                }




        gridList.setAdapter(new adaptador_Modulo(this, R.layout.activity_listas_visita2, ubicacion){
            @Override
            public void onEntrada(Object entrada, View view) {
                if (entrada != null) {

                    final TextView title = (TextView) view.findViewById(R.id.title);
                    if (title != null)
                        title.setText(((ListasClassColoresGrid) entrada).getTitle());


                    final TextView title2 = (TextView) view.findViewById(R.id.title2);
                    if (title2 != null)
                        title2.setText(((ListasClassColoresGrid) entrada).getTitle2());


                    final TextView subtitle = (TextView) view.findViewById(R.id.sub);
                    if (subtitle != null)
                        subtitle.setText(((ListasClassColoresGrid) entrada).getSubtitle());

                    final LinearLayout line = (LinearLayout) view.findViewById(R.id.line);
                    if (line != null)
                        line.setBackgroundColor(Color.parseColor(((ListasClassColoresGrid) entrada).getColorCode()));

                    gridList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {


                            int posicion=position*3;
                            try {
                                //RONDIN DIA
                                Conf.setVisita(ja2.getString(posicion));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            Intent i = new Intent(getApplicationContext(), RegistroPlumasEntradasActivity.class);
                            startActivity(i);
                            finish();


                        }
                    });


                }
            }

        });
            }else{
                ArrayList<ListasClassColoresGrid> ubicacion = new ArrayList<ListasClassColoresGrid>();

                ubicacion.add(new ListasClassColoresGrid("n/a", "","","#1A9BCA"));

            }
    }


    public void llenado2(){


        if(ja3.length()>0){
            ArrayList<ListasClassColoresGrid> ubicacion = new ArrayList<ListasClassColoresGrid>();

            for (int i = 0; i < ja3.length(); i += 3) {
                try {
                    String[] t=ja3.getString(i + 0).split("");

                    if(t[0].equals("V")){
                        ubicacion.add(new ListasClassColoresGrid(ja3.getString(i + 1),"| "+ja3.getString(i + 2) ,"ID:"+ja3.getString(i + 0),"#1A9BCA"));

                    }else if(t[0].equals("A")){
                        ubicacion.add(new ListasClassColoresGrid(ja3.getString(i + 1),"| "+ja3.getString(i + 2) ,"ID:"+ja3.getString(i + 0),"#FFE31111"));

                    }

                }catch (JSONException e){
                    e.printStackTrace();
                }
            }




            gridList2.setAdapter(new adaptador_Modulo(this, R.layout.activity_listas_visita2, ubicacion){
                @Override
                public void onEntrada(Object entrada, View view) {
                    if (entrada != null) {

                        final TextView title = (TextView) view.findViewById(R.id.title);
                        if (title != null)
                            title.setText(((ListasClassColoresGrid) entrada).getTitle());


                        final TextView title2 = (TextView) view.findViewById(R.id.title2);
                        if (title2 != null)
                            title2.setText(((ListasClassColoresGrid) entrada).getTitle2());


                        final TextView subtitle = (TextView) view.findViewById(R.id.sub);
                        if (subtitle != null)
                            subtitle.setText(((ListasClassColoresGrid) entrada).getSubtitle());

                        final LinearLayout line = (LinearLayout) view.findViewById(R.id.line);
                        if (line != null)
                            line.setBackgroundColor(Color.parseColor(((ListasClassColoresGrid) entrada).getColorCode()));

                        gridList2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {


                                int posicion=position*3;
                                try {
                                    //RONDIN DIA
                                    Conf.setVisita(ja3.getString(posicion));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                Intent i = new Intent(getApplicationContext(), RegistroPlumasSalidasActivity.class);
                                startActivity(i);
                                finish();


                            }
                        });


                    }
                }

            });
        }else{
            ArrayList<ListasClassColoresGrid> ubicacion = new ArrayList<ListasClassColoresGrid>();

            ubicacion.add(new ListasClassColoresGrid("n/a", "","","#1A9BCA"));

        }
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





    @Override
    public void onBackPressed(){
        super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(), DashboardVigilante.class);
        startActivity(intent);
        finish();
    }




}
