package mx.linkom.wifi_sanmateo;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AccesosActivity extends mx.linkom.wifi_sanmateo.Menu {

    Configuracion Conf;
    LinearLayout rlPermitido, rlDenegado,rlVista;
    TextView  tvMensaje,tvMensaje2,tvMensaje4;
    JSONArray ja1,ja2,ja3,ja4;
    String f1,f2,f3;
    int tiempo,tiempo2;
    MediaPlayer mp,mp2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accesos);


        Conf = new Configuracion(this);
        tiempo=14000;
        tiempo2=2000;
        mp = MediaPlayer.create(this,R.raw.audio2_invitacionvalida);
        mp2 = MediaPlayer.create(this,R.raw.audio3_qrnovalido);

        tvMensaje = (TextView) findViewById(R.id.setMensaje);
        tvMensaje2 = (TextView) findViewById(R.id.setMensaje2);
        tvMensaje4 = (TextView) findViewById(R.id.setMensaje4);
        rlVista = (LinearLayout) findViewById(R.id.rlVista);
        rlPermitido = (LinearLayout) findViewById(R.id.rlPermitido);
        rlDenegado = (LinearLayout) findViewById(R.id.rlDenegado);


        //SI ES ACEPTADO O DENEGAODO
        if(Conf.getST().equals("Aceptado")){
            rlVista.setVisibility(View.VISIBLE);
            rlPermitido.setVisibility(View.GONE);
            rlDenegado.setVisibility(View.GONE);
            Visita();
        }else if(Conf.getST().equals("Denegado")){
            rlDenegado.setVisibility(View.VISIBLE);
            rlVista.setVisibility(View.GONE);
            rlPermitido.setVisibility(View.GONE);
            tvMensaje.setText("Error: QR Inexistente");
                Notificar("");
            mp2.start();
            esperarParaCambio(tiempo);
        }


    }

    public void Visita(){

        String URL = "https://sanmateoresidencial.mx/plataforma/casetaV2/controlador/WIFI_SM/vst_php1.php?bd_name="+Conf.getBd()+"&bd_user="+Conf.getBdUsu()+"&bd_pwd="+Conf.getBdCon();
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {


            @Override
            public void onResponse(String response) {
                response = response.replace("][",",");
                if (response.length()>0){
                    try {
                        ja1 = new JSONArray(response);
                        Usuario(ja1.getString(2));
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
                params.put("QR", Conf.getQR().trim());
                params.put("id_residencial", Conf.getResid().trim());
                return params;
            }
        };
        requestQueue.add(stringRequest);
    }

    public void Usuario(final String IdUsu){ //DATOS USUARIO

        String URL = "https://sanmateoresidencial.mx/plataforma/casetaV2/controlador/WIFI_SM/vst_php2.php?bd_name="+Conf.getBd()+"&bd_user="+Conf.getBdUsu()+"&bd_pwd="+Conf.getBdCon();
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                response = response.replace("][",",");
                if (response.length()>0){
                    try {
                        ja2 = new JSONArray(response);
                        dtlLugar(ja2.getString(0));

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
                params.put("IdUsu", IdUsu.trim());
                params.put("id_residencial", Conf.getResid().trim());
                return params;
            }
        };
        requestQueue.add(stringRequest);
    }

    public void dtlLugar(final String idUsuario){
        String URLResidencial = "https://sanmateoresidencial.mx/plataforma/casetaV2/controlador/WIFI_SM/vst_php3.php?bd_name="+Conf.getBd()+"&bd_user="+Conf.getBdUsu()+"&bd_pwd="+Conf.getBdCon();
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLResidencial, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                if (response.equals("error")) {
                    sincasa();
                } else {

                response = response.replace("][", ",");
                if (response.length() > 0) {
                    try {
                        ja3 = new JSONArray(response);
                        salidas(ja1.getString(0));
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
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("id_usuario", idUsuario.trim());
                params.put("id_residencial", Conf.getResid().trim());

                return params;
            }
        };
        requestQueue.add(stringRequest);
    }

    public void salidas (final String id_visitante){
        String URLResidencial = "https://sanmateoresidencial.mx/plataforma/casetaV2/controlador/WIFI_SM/vst_php4.php?bd_name="+Conf.getBd()+"&bd_user="+Conf.getBdUsu()+"&bd_pwd="+Conf.getBdCon();
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLResidencial, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                try {
                    if (response.trim().equals("error")){

                        int $arreglo[]={0};
                        ja4 = new JSONArray($arreglo);
                        ValidarQR();

                    }else{
                        response = response.replace("][",",");
                        ja4 = new JSONArray(response);
                        ValidarQR();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("TAG", "Error: " + error.toString());
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("id_visitante", id_visitante.trim());
                params.put("id_residencial", Conf.getResid().trim());

                return params;
            }
        };
        requestQueue.add(stringRequest);
    }



    public void sincasa(){

        rlVista.setVisibility(View.GONE);
        rlPermitido.setVisibility(View.GONE);
        rlDenegado.setVisibility(View.VISIBLE);
        tvMensaje.setText("Error: No tiene asignada una unidad privativa.");
        try {
            Notificar(ja1.getString(7));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mp2.start();
        esperarParaCambio(tiempo);

    }


    public void ValidarQR(){

        try {
            Calendar c = Calendar.getInstance();
            DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date dateentrada = (Date)formatter.parse(ja1.getString(10));
            Date datesalida = (Date)formatter.parse(ja1.getString(11));

            //ANTES DE LA ENTRADA
            if(c.getTime().before(dateentrada) && ja4.getString(0).equals("0")) {//NUEVO
                rlVista.setVisibility(View.GONE);
                rlPermitido.setVisibility(View.GONE);
                rlDenegado.setVisibility(View.VISIBLE);
                tvMensaje.setText("Error: Aún no es hora de entrada");
                Notificar(ja1.getString(7));
                mp2.start();
                esperarParaCambio(tiempo);

                //EN MEDIO
            } else if( c.getTime().equals(dateentrada) || c.getTime().before(datesalida) ) {

                if (ja4.getString(0).equals("0")) { //NUEVO
                   Registrar_pluma();
                } else if (ja4.getString(0).equals("1")) { //Entro y quiere volver a entrar
                    rlVista.setVisibility(View.GONE);
                    rlPermitido.setVisibility(View.GONE);
                    rlDenegado.setVisibility(View.VISIBLE);
                    tvMensaje.setText("Error: Esté auto se encuentra dentro del complejo");
                    Notificar(ja1.getString(7));
                    mp2.start();
                    esperarParaCambio(tiempo);
                } else if (ja4.getString(0).equals("2")) { //Entro y salio ; y quiere volver a entrar
                    rlVista.setVisibility(View.GONE);
                    rlPermitido.setVisibility(View.GONE);
                    rlDenegado.setVisibility(View.VISIBLE);
                    tvMensaje.setText("Error: Esté código QR ya fue utilizado");
                    Notificar(ja1.getString(7));
                    mp2.start();
                    esperarParaCambio(tiempo);
                }
            }else if(c.getTime().after(datesalida)  && ja4.getString(0).equals("0") ) { //NUEVO
                rlVista.setVisibility(View.GONE);
                rlPermitido.setVisibility(View.GONE);
                rlDenegado.setVisibility(View.VISIBLE);
                tvMensaje.setText("Error: Esté código QR ha caducado");
                Notificar(ja1.getString(7));
                mp2.start();
                esperarParaCambio(tiempo);
            }else    if(c.getTime().after(datesalida)  && ja4.getString(0).equals("2") ){ //ENTRO Y SALIO
                rlVista.setVisibility(View.GONE);
                rlPermitido.setVisibility(View.GONE);
                rlDenegado.setVisibility(View.VISIBLE);
                tvMensaje.setText("Error: Esté código QR ha caducado");
                Notificar(ja1.getString(7));
                mp2.start();
                esperarParaCambio(tiempo);
            } else if(c.getTime().after(datesalida)  && ja4.getString(0).equals("1") ){//ESTA ADENTRO
                rlVista.setVisibility(View.GONE);
                rlPermitido.setVisibility(View.GONE);
                rlDenegado.setVisibility(View.VISIBLE);
                tvMensaje.setText("Error: Esté código QR ha caducado, Leer Salida");
                Notificar(ja1.getString(7));
                mp2.start();
                esperarParaCambio(tiempo);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    /*public void Registrar_pluma(){

        String URL = "https://sanmateoresidencial.mx/plataforma/casetaV2/controlador/WIFI_SM/plumas_registro_1.php?bd_name="+Conf.getBd()+"&bd_user="+Conf.getBdUsu()+"&bd_pwd="+Conf.getBdCon();
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response){

                try {
                    if(response.equals("error")){
                        rlVista.setVisibility(View.GONE);
                        rlPermitido.setVisibility(View.GONE);
                        rlDenegado.setVisibility(View.VISIBLE);
                        tvMensaje.setText("Error:  No Se Puedo Registrar La Entrada");

                        Notificar(ja1.getString(7));
                        mp2.start();
                        esperarParaCambio(tiempo);
                    }else {
                        Registrar();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
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
                try {
                    params.put("id_residencial", Conf.getResid().trim());
                    params.put("id_visita", ja1.getString(0).trim());
                    params.put("id_vigilante", Conf.getiDVigilante().trim());
                    params.put("id_pluma", Conf.getUsu().trim());

                } catch (JSONException e) {
                    Log.e("TAG","Error: " + e.toString());
                }

                return params;
            }
        };
        requestQueue.add(stringRequest);
    }*/


    public void Registrar_pluma() {

        rlVista.setVisibility(View.GONE);
        rlDenegado.setVisibility(View.GONE);
        rlPermitido.setVisibility(View.VISIBLE);
        try {
            tvMensaje2.setText(ja1.getString(7).trim());
            tvMensaje4.setText(ja3.getString(0).trim());

        } catch (JSONException e) {
            e.printStackTrace();
        }
        mp.start();
        esperarParaCambio2(tiempo2);

    }


    /*public void Registrar(){

            String URL = "https://sanmateoresidencial.mx/plataforma/casetaV2/controlador/WIFI_SM/vst_php5.php?bd_name="+Conf.getBd()+"&bd_user="+Conf.getBdUsu()+"&bd_pwd="+Conf.getBdCon();
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {

                @Override
                public void onResponse(String response){

                    try {
                    if(response.equals("error")){
                        rlVista.setVisibility(View.GONE);
                        rlPermitido.setVisibility(View.GONE);
                        rlDenegado.setVisibility(View.VISIBLE);
                        tvMensaje.setText("Error:  No Se Puedo Registrar La Entrada");

                        Notificar(ja1.getString(7));
                        mp2.start();
                        esperarParaCambio(tiempo);
                    }else {
                        rlVista.setVisibility(View.GONE);
                        rlDenegado.setVisibility(View.GONE);
                        rlPermitido.setVisibility(View.VISIBLE);
                        try {
                            tvMensaje2.setText(ja1.getString(7).trim());
                            tvMensaje4.setText(ja3.getString(0).trim());

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        mp.start();
                        esperarParaCambio2(tiempo2);

                    }
                    } catch (JSONException e) {
                        e.printStackTrace();
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

                            f1="";
                            f2="";
                            f3="";
                    Map<String, String> params = new HashMap<>();
                    try {
                        params.put("id_residencial", Conf.getResid().trim());
                        params.put("id_visita", ja1.getString(0).trim());
                        params.put("guardia_de_entrada", Conf.getUsu().trim());
                        params.put("pasajeros","");
                        params.put("placas", "");
                        params.put("foto1", f1);
                        params.put("foto2", f2);
                        params.put("foto3", f3);
                        params.put("usuario",ja2.getString(1).trim() + " " + ja2.getString(2).trim() + " " + ja2.getString(3).trim());
                        params.put("token", ja2.getString(5).trim());
                        params.put("correo",ja2.getString(6).trim());
                        params.put("visita",ja1.getString(7).trim());
                        params.put("pluma_nombre",Conf.getNombrelPluma());
                        params.put("pluma_token",Conf.getTokenVigi());

                    } catch (JSONException e) {
                        Log.e("TAG","Error: " + e.toString());
                    }
                    return params;
                }
            };
            requestQueue.add(stringRequest);
        }*/

    public void esperarParaCambio(int milisegundos) {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                Intent i = new Intent(getApplicationContext(), DashboardEntradas.class);
                startActivity(i);
                finish();
            }
        }, milisegundos);
    }


    public void esperarParaCambio2(int milisegundos) {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                Intent intent = new Intent(getApplicationContext(), CamaraActivity.class);
                try {
                    intent.putExtra("id_residencial", Conf.getResid().trim());
                    intent.putExtra("id_visita", ja1.getString(0).trim());
                    intent.putExtra("guardia_de_entrada", Conf.getUsu().trim());
                    intent.putExtra("usuario", ja2.getString(1).trim() + " " + ja2.getString(2).trim() + " " + ja2.getString(3).trim());
                    intent.putExtra("token", ja2.getString(5).trim());
                    intent.putExtra("correo", ja2.getString(6).trim());
                    intent.putExtra("visita", ja1.getString(7).trim());
                    intent.putExtra("pluma_nombre", Conf.getNombrelPluma());
                    intent.putExtra("pluma_token", Conf.getTokenVigi());


                    intent.putExtra("id_vigilante", Conf.getiDVigilante().trim());
                    intent.putExtra("id_pluma", Conf.getUsu().trim());

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                startActivity(intent);
                finish();
            }
        }, milisegundos);
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
        super.onBackPressed();
        mp.stop();
        mp2.stop();
        Intent intent = new Intent(getApplicationContext(), DashboardEntradas.class);
        startActivity(intent);
        finish();
    }
}
