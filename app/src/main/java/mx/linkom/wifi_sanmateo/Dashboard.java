package mx.linkom.wifi_sanmateo;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import mx.linkom.wifi_sanmateo.adaptadores.ModuloClassGrid;
import mx.linkom.wifi_sanmateo.adaptadores.adaptador_Modulo;


public class Dashboard extends   mx.linkom.wifi_sanmateo.Menu2 {

    private FirebaseAuth fAuth;
    private Configuracion Conf;
    private GridView gridList3,gridList2;
    JSONArray ja1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        fAuth = FirebaseAuth.getInstance();
        Conf = new Configuracion(this);
        gridList2 = (GridView)findViewById(R.id.gridList2);
        gridList3 = (GridView)findViewById(R.id.gridList3);

        Global.ocultarBarrasNavegacionEstado(this);
        Global.aumentarVolumen(this);
        Global.evitarSuspenderPantalla(this);


    }

    @Override
    public void onStart() {
        super.onStart();

        Registro();
        Sesion();
        menu();
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

                            if(ja1.getString(3).equals("1") && ja1.getString(4).equals("0")) {
                                llenado2();
                            }else if(ja1.getString(3).equals("1") && ja1.getString(4).equals("1")){
                                llenado3();
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


    public void llenado2(){
        ArrayList<ModuloClassGrid> lista2 = new ArrayList<ModuloClassGrid>();

        lista2.add(new ModuloClassGrid(R.drawable.entradas,"Entradas","#FF4081"));
        Conf.setPlacas("");
        lista2.add(new ModuloClassGrid(R.drawable.salidas,"Salidas","#4cd2c7"));

        gridList2.setAdapter(new adaptador_Modulo(this, R.layout.activity_modulo_lista, lista2){
            @Override
            public void onEntrada(Object entrada, View view) {
                if (entrada != null) {
                    ImageView add = (ImageView) view.findViewById(R.id.imageView);
                    if (add != null)
                        add.setImageResource(((ModuloClassGrid) entrada).getImagen());

                    final TextView title = (TextView) view.findViewById(R.id.title);
                    if (title != null)
                        title.setText(((ModuloClassGrid) entrada).getTitle());

                    final LinearLayout line = (LinearLayout) view.findViewById(R.id.line);
                    if (line != null)
                        line.setBackgroundColor(Color.parseColor(((ModuloClassGrid) entrada).getColorCode()));

                    gridList2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                            if(position==0) {
                                Conf.setPlacas("");
                                 Intent docugen = new Intent(getApplication(), AccesoRegistroActivity.class);
                                 startActivity(docugen);
                                  finish();
                            }else if(position==1){
                                Intent docugen = new Intent(getApplication(), SalidasActivity.class);
                                 startActivity(docugen);
                                 finish();
                            }

                        }
                    });

                }
            }

        });
    }

    public void llenado3(){
        ArrayList<ModuloClassGrid> lista3 = new ArrayList<ModuloClassGrid>();

        lista3.add(new ModuloClassGrid(R.drawable.entradas,"Entradas P","#FF4081"));
        lista3.add(new ModuloClassGrid(R.drawable.salidas,"Salidas","#4cd2c7"));

        gridList3.setAdapter(new adaptador_Modulo(this, R.layout.activity_modulo_lista, lista3){
            @Override
            public void onEntrada(Object entrada, View view) {
                if (entrada != null) {
                    ImageView add = (ImageView) view.findViewById(R.id.imageView);
                    if (add != null)
                        add.setImageResource(((ModuloClassGrid) entrada).getImagen());

                    final TextView title = (TextView) view.findViewById(R.id.title);
                    if (title != null)
                        title.setText(((ModuloClassGrid) entrada).getTitle());

                    final LinearLayout line = (LinearLayout) view.findViewById(R.id.line);
                    if (line != null)
                        line.setBackgroundColor(Color.parseColor(((ModuloClassGrid) entrada).getColorCode()));

                    gridList3.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                            if(position==0) {

                                 Intent docugen = new Intent(getApplication(), EntradasActivity.class);
                                 startActivity(docugen);
                                 finish();
                            }else if(position==1){
                                 Intent docugen = new Intent(getApplication(), SalidasActivity.class);
                                 startActivity(docugen);
                                  finish();
                            }

                        }
                    });

                }
            }

        });
    }







    @Override
    public void onBackPressed(){
        super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(), Dashboard.class);
        startActivity(intent);
        finish();
    }


}
