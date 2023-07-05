package mx.linkom.wifi_sanmateo;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AccesoAutosActivity extends Menu {

    Configuracion Conf;
    LinearLayout rlPermitido, rlDenegado,rlVista;
    TextView  tvMensaje,tvMensaje2,tvMensaje4;
    JSONArray ja1,ja2,ja3,ja4,ja5,ja6,ja7,ja8,ja9;
    String f1,f2,f3;
    int tiempo,tiempo2;
    MediaPlayer mp,mp2;

    //BT

    Handler bluetoothIn;
    final int handlerState = 0;
    private BluetoothAdapter btAdapter = null;
    private BluetoothSocket btSocket = null;
    private StringBuilder DataStringIn = new StringBuilder();
    private ConnectedThread MyConexionBT;

    //IDENTIFICADOR  UNICO DE SERVICIO-SPP UUID
    private static final UUID BTMODULEUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    // String para la dirección MAC
    private static String address = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_autosaccesos);

          btAdapter= BluetoothAdapter.getDefaultAdapter();
          VericaEstadoBT();
          tiempo2=2000;
        mp = MediaPlayer.create(this,R.raw.audio2_invitacionvalida);
        mp2 = MediaPlayer.create(this,R.raw.audio3_qrnovalido);

        Conf = new Configuracion(this);
        tiempo=14000;
        tvMensaje = (TextView) findViewById(R.id.setMensaje);
        tvMensaje2 = (TextView) findViewById(R.id.setMensaje2);
        tvMensaje4 = (TextView) findViewById(R.id.setMensaje4);
        rlVista = (LinearLayout) findViewById(R.id.rlVista);
        rlPermitido = (LinearLayout) findViewById(R.id.rlPermitido);
        rlDenegado = (LinearLayout) findViewById(R.id.rlDenegado);

        Global.ocultarBarrasNavegacionEstado(this);
        Global.aumentarVolumen(this);
        Global.evitarSuspenderPantalla(this);

        //SI ES ACEPTADO O DENEGAODO
        if(Conf.getST().equals("Aceptado")){
            rlVista.setVisibility(View.VISIBLE);
            rlPermitido.setVisibility(View.GONE);
            rlDenegado.setVisibility(View.GONE);
            Autos();
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


    //BT INICIO
    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {
        //CREA UNA CONEXIÓN SEGURA DE SALIDA PARA EL DISPOSITIVO USANDO EL SERVICIO UUID
        return  device.createRfcommSocketToServiceRecord(BTMODULEUUID);
    }

    @Override
    public void onResume() {
        super.onResume();

        //Intent intent = getIntent();
        //address = intent.getStringExtra(CBTActivity.EXTRA_DEVICE_ADDRESS);

        address=Conf.getMAC();
        //SETEA LA DIRECCIÓN MAC
        BluetoothDevice device = btAdapter.getRemoteDevice(address);

        try {
            btSocket = createBluetoothSocket(device);
        } catch (IOException e) {
            Toast.makeText(getBaseContext(), "La creacción del Socket fallo", Toast.LENGTH_LONG).show();
        }
        // ESTABLECE LA CONEXIÓN CON EL  Bluetooth socket .
        try
        {
            btSocket.connect();
        } catch (IOException e) {
            try
            {
                btSocket.close();
            } catch (IOException e2)
            {
                //insert code to deal with this
            }
        }
        MyConexionBT = new ConnectedThread(btSocket);
        MyConexionBT.start();

    }

    @Override
    public void onPause()
    {
        super.onPause();
        try
        {
            //CUANDO SE SALE DE LA APP ESTA PARTE PERMITE QUE NO SE DEJE ABIERTO EL SOCKET
            btSocket.close();
        } catch (IOException e2) {
        }
    }

    //COMPRUEBA QUE EL DISPOSITIVO BT ESTA DISPONIBLE Y SOLICITA QUE SE ACTIVE SI ESTA DESCONECTADO
    private void VericaEstadoBT() {

        if(btAdapter==null) {
            Toast.makeText(getBaseContext(), "El dispositivo no soporta bluetooth", Toast.LENGTH_LONG).show();
        } else {
            if (btAdapter.isEnabled()) {
            } else {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, 1);
            }
        }
    }

    //CREA LA CLASE QUE PERMITE CREAR EL EVENTO DE CONEXIÓN
    private class ConnectedThread extends Thread {
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        //creation of the connect thread
        public ConnectedThread(BluetoothSocket socket) {
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            try {
                //Create I/O streams for connection
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) { }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }


        public void run() {
            byte[] byte_in = new byte[1];

            // SE MANTIEN EN MODO ESCUCHA PARA DETERMINAR EL INGRESO DE DATOS
            while (true) {
                try {
                    mmInStream.read(byte_in);
                    char ch= (char) byte_in[0];
                    bluetoothIn.obtainMessage(handlerState, ch).sendToTarget();
                } catch (IOException e) {
                    break;
                }
            }
        }
        //ENVIO DE LA TRAMA
        public void write(String input) {
            try {
                mmOutStream.write(input.getBytes());
            } catch (IOException e) {
                //SI NO ES POSIBLE ENVIAR DATOS SE CIERRA LA CONEXIÓN
                Toast.makeText(getBaseContext(), "La Conexión fallo", Toast.LENGTH_LONG).show();
                finish();

            }
        }
    }
    //BT FINAL

    public void Autos(){

        String url = "https://sanmateoresidencial.mx/plataforma/casetaV2/controlador/WIFI_SM/auto1.php?bd_name=" + Conf.getBd() + "&bd_user=" + Conf.getBdUsu() + "&bd_pwd=" + Conf.getBdCon();

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                response = response.replace("][",",");
                if (response.length()>0){
                    try {
                        ja7 = new JSONArray(response);
                        Usuario(ja7.getString(2));

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Error ", "Id: " + error.toString());
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> params = new HashMap<>();
                params.put("QR", Conf.getQR());
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
                            cajones();
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

    public void cajones(){
        String URLResidencial = "https://sanmateoresidencial.mx/plataforma/casetaV2/controlador/WIFI_SM/auto5.php?bd_name="+Conf.getBd()+"&bd_user="+Conf.getBdUsu()+"&bd_pwd="+Conf.getBdCon();
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLResidencial, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {


                if (response.equals("error")) {
                    int $arreglo[]={0};
                    try {
                        ja9 = new JSONArray($arreglo);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    salidas();

                } else {

                    response = response.replace("][", ",");
                    if (response.length() > 0) {
                        try {
                            ja9 = new JSONArray(response);
                            Log.e("TAG", "LKMST: " + ja9);


                            salidas();
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
                try {
                    params.put("usuario", ja2.getString(0));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                params.put("id_residencial", Conf.getResid().trim());

                return params;
            }
        };
        requestQueue.add(stringRequest);
    }


    public void salidas(){

        String url = "https://sanmateoresidencial.mx/plataforma/casetaV2/controlador/WIFI_SM/auto2.php?bd_name=" + Conf.getBd() + "&bd_user=" + Conf.getBdUsu() + "&bd_pwd=" + Conf.getBdCon();

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {


            @Override
            public void onResponse(String response) {

                try {
                    if (response.trim().equals("error")){

                        int $arreglo[]={0};
                        ja8 = new JSONArray($arreglo);
                        ValidarQR();

                    }else{
                        response = response.replace("][",",");
                        ja8 = new JSONArray(response);
                        ValidarQR();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }


        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Error ", "Id: " + error.toString());
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> params = new HashMap<>();
                params.put("id_residencial", Conf.getResid().trim());
                try {
                    params.put("id_usuario", ja7.getString(2));
                    params.put("id_auto", ja7.getString(0));


                } catch (JSONException e) {
                    e.printStackTrace();
                }

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
            Notificar(ja2.getString(1) + " " + ja2.getString(2) + " " + ja2.getString(3));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mp2.start();
        esperarParaCambio(tiempo);

    }


    public void ValidarQR(){

        try {
            if(ja8.getString(0).equals("0")){
                Registrar_pluma();
            }else{
                if(ja8.getString(11).equals("1")){

                    rlVista.setVisibility(View.GONE);
                    rlPermitido.setVisibility(View.GONE);
                    rlDenegado.setVisibility(View.VISIBLE);
                    tvMensaje.setText("Esté auto se encuentra dentro del complejo");
                    Notificar(ja2.getString(1) + " " + ja2.getString(2) + " " + ja2.getString(3));
                    mp2.start();
                    esperarParaCambio(tiempo);
                }else if(ja8.getString(11).equals("2")){
                    Registrar_pluma();
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }



    public void Registrar_pluma(){

        String URL = "https://sanmateoresidencial.mx/plataforma/casetaV2/controlador/WIFI_SM/plumas_registro_9.php?bd_name=" + Conf.getBd() + "&bd_user=" + Conf.getBdUsu() + "&bd_pwd=" + Conf.getBdCon();
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
                    params.put("id_auto", ja7.getString(0));
                    params.put("id_vigilante", Conf.getiDVigilante().trim());
                    params.put("id_pluma", Conf.getUsu().trim());

                } catch (JSONException e) {
                    Log.e("TAG","Error: " + e.toString());
                }

                return params;
            }
        };
        requestQueue.add(stringRequest);
    }


    public void Registrar(){



            String URL = "https://sanmateoresidencial.mx/plataforma/casetaV2/controlador/WIFI_SM/auto3.php?bd_name=" + Conf.getBd() + "&bd_user=" + Conf.getBdUsu() + "&bd_pwd=" + Conf.getBdCon();

            RequestQueue requestQueue = Volley.newRequestQueue(this);
            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {

                @Override
                public void onResponse(String response){


                    if(response.equals("error")){

                        rlVista.setVisibility(View.GONE);
                        rlPermitido.setVisibility(View.GONE);
                        rlDenegado.setVisibility(View.VISIBLE);
                        tvMensaje.setText("Entrada de Auto No Existosa");
                        try {
                            Notificar(ja2.getString(1) + " " + ja2.getString(2) + " " + ja2.getString(3));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        mp2.start();
                        esperarParaCambio2(tiempo2);

                    }else {
                        rlVista.setVisibility(View.GONE);
                        rlDenegado.setVisibility(View.GONE);
                        rlPermitido.setVisibility(View.VISIBLE);

                        try {
                            tvMensaje2.setText(ja2.getString(1).trim() + " " + ja2.getString(2).trim() + " " + ja2.getString(3).trim());
                            tvMensaje4.setText(ja3.getString(0).trim());

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        mp.start();
                        esperarParaCambio2(tiempo2);
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

                    params.put("id_residencial", Conf.getResid().trim());
                    try {
                        params.put("id_usuario", ja7.getString(2));
                        params.put("id_auto", ja7.getString(0));
                        params.put("usuario",ja2.getString(1).trim() + " " + ja2.getString(2).trim() + " " + ja2.getString(3).trim());
                        params.put("token", ja2.getString(5).trim());
                        params.put("correo",ja2.getString(6).trim());
                        params.put("nom_residencial",Conf.getNomResi().trim());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    params.put("guardia", Conf.getUsu().trim());
                    params.put("foto1", f1);
                    params.put("foto2", f2);
                    params.put("foto3", f3);




                    return params;
                }
            };
            requestQueue.add(stringRequest);
    }

    public void esperarParaCambio2(int milisegundos) {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                EnviarBT();
            }
        }, milisegundos);
    }

    public void EnviarBT2() {
        Toast.makeText(AccesoAutosActivity.this, "Registro Exitoso", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(getApplicationContext(), DashboardEntradas.class);
        startActivity(intent);
        finish();

    }

public void EnviarBT(){
    MyConexionBT.write("OPEN");
    bluetoothIn = new Handler() {
        public void handleMessage(android.os.Message msg) {
            if (msg.what == handlerState) {
                char MyCaracter = (char) msg.obj;

                if (MyCaracter == 'o') {
                    Intent intent = new Intent(getApplicationContext(), DashboardEntradas.class);
                    startActivity(intent);
                    finish();
                } else {
                    try {
                        Notificar(ja1.getString(7));
                        mp2.start();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Intent intent = new Intent(getApplicationContext(), DashboardEntradas.class);
                    startActivity(intent);
                    finish();

                }
            }
        }
    };
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




    public void esperarParaCambio(int milisegundos) {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                finalizarApp();
            }
        }, milisegundos);
    }


    public void finalizarApp() {
        Intent i = new Intent(getApplicationContext(), DashboardEntradas.class);
        startActivity(i);
        finish();

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
