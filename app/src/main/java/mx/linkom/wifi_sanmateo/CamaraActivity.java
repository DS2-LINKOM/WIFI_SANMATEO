package mx.linkom.wifi_sanmateo;

import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AnimationDrawable;
import android.hardware.Camera;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import mx.linkom.wifi_sanmateo.deteccionPlacas.capturarPlaca;
import mx.linkom.wifi_sanmateo.fotosSegundoPlano.UrisContentProvider;
import mx.linkom.wifi_sanmateo.fotosSegundoPlano.subirFotos;

public class CamaraActivity extends mx.linkom.wifi_sanmateo.Menu implements SurfaceHolder.Callback, Camera.PictureCallback, View.OnClickListener {

    private SurfaceHolder surfaceHolder;
    private Camera camera;
    private SurfaceView surfaceView;
    ImageView view1;
    Button startBtn, siguiente;
    Bitmap bitmap;
    LinearLayout Botones;
    int tiempo, tiempo2;
    public final int CAMERA_FACING_FRONT = 1;
    JSONArray ja1;
    Configuracion Conf;
    ProgressDialog pd;
    Uri uri_img;
    FirebaseStorage storage;
    StorageReference storageReference;
    ImageView contador;
    AnimationDrawable frameAnimation;
    MediaPlayer mp, mp2, mp3;
    Handler han3;
    String rutaImagen, nombreFoto;
    //BT

    /*Handler bluetoothIn;
    final int handlerState = 0;
    private BluetoothAdapter btAdapter = null;
    private BluetoothSocket btSocket = null;
    private StringBuilder DataStringIn = new StringBuilder();
    private CamaraActivity.ConnectedThread MyConexionBT;

    //IDENTIFICADOR  UNICO DE SERVICIO-SPP UUID
    private static final UUID BTMODULEUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    // String para la dirección MAC
    public static String address = null;*/

    /*String i_id_residencial, i_id_visita, i_guardia_de_entrada, i_foto1, i_foto2, i_foto3, i_usuario, i_token, i_correo, i_visita, i_pluma_nombre, i_pluma_token, i_id_vigilante, i_id_pluma;
    Date FechaA;
    String FechaC;*/

    private Handler handler;
    private Runnable runnable;
    ScrollView scrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camara);

        handler = new Handler();

        runnable = new Runnable() {
            @Override
            public void run() {
                // Handle idle time here
                Intent intent = new Intent(CamaraActivity.this, ProtectorPantalla.class);
                startActivity(intent);
            }
        };

        scrollView = (ScrollView) findViewById(R.id.scrollView);

        han3 = new Handler();

          /*btAdapter= BluetoothAdapter.getDefaultAdapter();
          VericaEstadoBT();*/
        tiempo2 = 4000;
        mp = MediaPlayer.create(this, R.raw.audio6_graciasentrada);
        mp2 = MediaPlayer.create(this, R.raw.audio4_identificacion);
        mp3 = MediaPlayer.create(this, R.raw.audio5_repetiridentificacion);
        mp2.start();

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        Conf = new Configuracion(this);
        tiempo = 5000;
        surfaceView = (SurfaceView) findViewById(R.id.surfaceView);
        view1 = (ImageView) findViewById(R.id.view1);
        startBtn = (Button) findViewById(R.id.startBtn);
        siguiente = (Button) findViewById(R.id.siguiente);
        Botones = (LinearLayout) findViewById(R.id.botones);
        contador = (ImageView) findViewById(R.id.contador);

        setupSurfaceHolder();
        Visita();

//        Intent intent = getIntent();
//        i_id_residencial = intent.getStringExtra("id_residencial");
//        i_id_visita = intent.getStringExtra("id_visita");
//        i_guardia_de_entrada = intent.getStringExtra("guardia_de_entrada");
//        i_foto1 = intent.getStringExtra("foto1");
//        i_foto2 = intent.getStringExtra("foto2");
//        i_foto3 = intent.getStringExtra("foto3");
//        i_usuario = intent.getStringExtra("usuario");
//        i_token = intent.getStringExtra("token");
//        i_correo = intent.getStringExtra("correo");
//        i_visita = intent.getStringExtra("visita");
//        i_pluma_nombre = intent.getStringExtra("pluma_nombre");
//        i_pluma_token = intent.getStringExtra("pluma_token");
//        i_id_vigilante = intent.getStringExtra("id_vigilante");
//        i_id_pluma = intent.getStringExtra("id_pluma");
//
//        if (i_id_residencial == null || i_id_visita == null || i_guardia_de_entrada == null) {
//            Log.e("INTENT", "No se enviaron los datos");
//            i_id_residencial = "";
//            i_id_visita = "";
//            i_guardia_de_entrada = "";
//            i_foto1 = "";
//            i_foto2 = "";
//            i_foto3 = "";
//            i_usuario = "";
//            i_token = "";
//            i_correo = "";
//            i_visita = "";
//            i_pluma_nombre = "";
//            i_pluma_token = "";
//            i_id_vigilante = "";
//            i_id_pluma = "";
//        }

        contador.setBackgroundResource(R.drawable.loading);
        frameAnimation = (AnimationDrawable) contador.getBackground();

        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // han3.removeCallbacksAndMessages(null);
                // mp3.stop();


                startBtn.setEnabled(false);

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        botonPresionado(startBtn, 0);

                        contador.setBackgroundResource(R.drawable.loading);
                        frameAnimation = (AnimationDrawable) contador.getBackground();
                        frameAnimation.start();

                        bitmap = null;
                        surfaceView.setVisibility(View.VISIBLE);
                        view1.setVisibility(View.GONE);
                        esperarParaCambio(tiempo);
                    }
                }, 300);
            }
        });

        pd = new ProgressDialog(this);
        pd.setMessage("Registrando...");

        siguiente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                siguiente.setEnabled(false);

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        botonPresionado(siguiente, 0);

                        //Registrar_pluma();
                        Registrar();
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

    public void botonPresionado(Button button, int estado){
        //estado --> 0=presionado   1=restablecer

        int ancho = 0, alto = 0, tamanoTxt = 0;

        if (estado == 0){
            button.setBackgroundResource(R.drawable.usuario_bbt_press);
            ancho = 255;
            alto = 58;
            tamanoTxt = 25;
            button.setTextColor(0xFF5A6C81);
        }else if (estado == 1){
            button.setBackgroundResource(R.drawable.ripple_effect);
            ancho = 250;
            alto = 63;
            tamanoTxt = 30;
            button.setTextColor(0xFF27374A);
        }



        ViewGroup.LayoutParams params = button.getLayoutParams();
        //Para tener en dp
        params.width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, ancho, getResources().getDisplayMetrics());
        params.height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, alto, getResources().getDisplayMetrics());
        button.setLayoutParams(params);

        button.setTextSize(TypedValue.COMPLEX_UNIT_DIP, tamanoTxt); // set the text size to 20 density-independent pixels
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

    @Override
    public void onStart() {
        super.onStart();
        esperarParaCambio(tiempo);
        frameAnimation.start();
    }


    public void esperarParaCambio(int milisegundos) {


        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                esperarParaCambio2(2);
                frameAnimation.stop();
                contador.setBackgroundResource(R.drawable.law_6);
            }
        }, milisegundos);
    }

    public void esperarParaCambio2(int milisegundos) {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                captureImage();

            }
        }, milisegundos);
    }


    Calendar fecha = Calendar.getInstance();
    int anio = fecha.get(Calendar.YEAR);
    int mes = fecha.get(Calendar.MONTH) + 1;
    int dia = fecha.get(Calendar.DAY_OF_MONTH);


    //BT INICIO
    /*private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {
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
        MyConexionBT = new CamaraActivity.ConnectedThread(btSocket);
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
    }*/
    //BT FINAL


    public void Visita() {

        String URL = "https://sanmateoresidencial.mx/plataforma/casetaV2/controlador/WIFI_SM/vst_php1.php?bd_name=" + Conf.getBd() + "&bd_user=" + Conf.getBdUsu() + "&bd_pwd=" + Conf.getBdCon();
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {


            @Override
            public void onResponse(String response) {
                response = response.replace("][", ",");
                if (response.length() > 0) {
                    try {
                        ja1 = new JSONArray(response);
                        Log.e("Visita", "LLega");

                        //Solo ejecutar si el servicio no se esta ejecutando
                        if (!servicioPlacas()) {
                            Toast.makeText(CamaraActivity.this, "El servicio de placas inicio", Toast.LENGTH_SHORT).show();
                            ArrayList<String> id = new ArrayList<String>();
                            try {
                                id.add(ja1.getString(0).trim());
                                Intent cargarFotos = new Intent(CamaraActivity.this, capturarPlaca.class);
                                cargarFotos.putExtra("id", id);
                                startService(cargarFotos);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }else {
                            Toast.makeText(CamaraActivity.this, "El servici ode placas ya se esta ejecutando", Toast.LENGTH_SHORT).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
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
                params.put("QR", Conf.getQR().trim());
                params.put("id_residencial", Conf.getResid().trim());
                return params;
            }
        };
        requestQueue.add(stringRequest);
    }

    //Método para saber si es que el servicio ya se esta ejecutando
    public boolean servicioPlacas() {
        //Obtiene los servicios que se estan ejecutando
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        //Se recorren todos los servicios obtnidos para saber si el servicio creado ya se esta ejecutando
        for (ActivityManager.RunningServiceInfo service : activityManager.getRunningServices(Integer.MAX_VALUE)) {
            if (capturarPlaca.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private void setupSurfaceHolder() {
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);
    }

    public void captureImage() {
        if (camera != null) {
            camera.takePicture(null, null, this);
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        startCamera();
    }

    private void startCamera() {
        camera = Camera.open(0);
        camera.setDisplayOrientation(0);

        try {
            camera.setPreviewDisplay(surfaceHolder);
            camera.startPreview();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        resetCamera();
    }

    public void resetCamera() {
        if (surfaceHolder.getSurface() == null) {
            return;
        }

        if (camera != null) {
            camera.stopPreview();
            try {
                camera.setPreviewDisplay(surfaceHolder);
            } catch (IOException e) {
                e.printStackTrace();
            }
            camera.startPreview();
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        releaseCamera();
    }

    private void releaseCamera() {
        if (camera != null) {
            camera.stopPreview();
            camera.release();
            camera = null;
        }
    }

    @Override
    public void onPictureTaken(byte[] bytes, Camera camera) {
        try {
            saveImage(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
        resetCamera();
    }

    private void saveImage(byte[] bytes) throws IOException {
        FileOutputStream outStream;
        try {
            nombreFoto = "app" + ja1.getString(0) + "_" + anio + mes + dia;
            File foto = new File(getApplication().getExternalFilesDir(null), nombreFoto);
            outStream = new FileOutputStream(foto);
            outStream.write(bytes);
            outStream.close();
            rutaImagen = foto.getAbsolutePath();

            uri_img = FileProvider.getUriForFile(getApplicationContext(), getApplicationContext().getPackageName() + ".provider", foto);

            bitmap = BitmapFactory.decodeFile(getApplicationContext().getExternalFilesDir(null) + "/" + nombreFoto);
            surfaceView.setVisibility(View.GONE);
            Botones.setVisibility(View.VISIBLE);
            view1.setVisibility(View.VISIBLE);
            view1.setImageBitmap(bitmap);
//                        Toast.makeText(CamaraActivity.this, "Picture Saved: " + "ine", Toast.LENGTH_LONG).show();
            //dato=0;
            //esperarParaCambio3(10000);
            mp3.start();

            startBtn.setEnabled(true);
            botonPresionado(startBtn, 1);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

//    public void Registrar_pluma(){
//
//        String URL = "https://sanmateoresidencial.mx/plataforma/casetaV2/controlador/WIFI_SM/plumas_registro_1.php?bd_name="+Conf.getBd()+"&bd_user="+Conf.getBdUsu()+"&bd_pwd="+Conf.getBdCon();
//        RequestQueue requestQueue = Volley.newRequestQueue(this);
//        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
//
//            @Override
//            public void onResponse(String response){
//
//                if(response.equals("error")){
//                    siguiente.setEnabled(true);
//                    botonPresionado(siguiente, 1);
//                    /*rlVista.setVisibility(View.GONE);
//                    rlPermitido.setVisibility(View.GONE);
//                    rlDenegado.setVisibility(View.VISIBLE);
//                    tvMensaje.setText("Error:  No Se Puedo Registrar La Entrada");
//
//                    Notificar(ja1.getString(7));
//                    mp2.start();
//                    esperarParaCambio(tiempo);*/
//                }else {
//                    RegistrarVisita();
//                }
//            }
//        }, new Response.ErrorListener(){
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                Log.e("TAG","Error: " + error.toString());
//            }
//        }){
//            @Override
//            protected Map<String, String> getParams() throws AuthFailureError {
//
//
//                Map<String, String> params = new HashMap<>();
//                params.put("id_residencial", i_id_residencial);
//                params.put("id_visita", i_id_visita);
//                params.put("id_vigilante", i_id_vigilante);
//                params.put("id_pluma", i_id_pluma);
//
//                return params;
//            }
//        };
//        requestQueue.add(stringRequest);
//    }
//
//    public void RegistrarVisita() {
//
//
//        FechaA = Calendar.getInstance().getTime();
//        FechaC = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(FechaA);
//
//
//        String URL = "https://sanmateoresidencial.mx/plataforma/casetaV2/controlador/WIFI_SM/vst_php5.php?bd_name=" + Conf.getBd() + "&bd_user=" + Conf.getBdUsu() + "&bd_pwd=" + Conf.getBdCon();
//        RequestQueue requestQueue = Volley.newRequestQueue(this);
//        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
//
//            @Override
//            public void onResponse(String response) {
//
//
//                if (response.equals("error")) {
//                    siguiente.setEnabled(true);
//                    botonPresionado(siguiente, 1);
//                    /*rlVista.setVisibility(View.GONE);
//                    rlPermitido.setVisibility(View.GONE);
//                    rlDenegado.setVisibility(View.VISIBLE);
//                    tvMensaje.setText("Error: No Se Puedo Registrar La Entrada");
//                    try {
//                        Notificar(ja1.getString(7));
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                    mp2.start();
//                    esperarParaCambio(tiempo);*/
//                } else {
//                    /*rlVista.setVisibility(View.GONE);
//                    rlDenegado.setVisibility(View.GONE);
//                    rlPermitido.setVisibility(View.VISIBLE);
//                    try {
//                        tvMensaje2.setText(ja1.getString(7).trim());
//                        tvMensaje4.setText(ja3.getString(0).trim());
//
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                    mp.start();
//                    esperarParaCambio2(tiempo2);*/
//
//                    mp3.stop();
//
//                    ContentValues val_img1 = ValuesImagen(nombreFoto, Conf.getPin().trim() + "/caseta/" + nombreFoto.trim(), rutaImagen);
//                    Uri uri = getContentResolver().insert(UrisContentProvider.URI_CONTENIDO_FOTOS_OFFLINE, val_img1);
//                    //upload();
//                    //Solo ejecutar si el servicio no se esta ejecutando
//                    if (!servicioFotos()) {
//                        Intent cargarFotos = new Intent(CamaraActivity.this, subirFotos.class);
//                        startService(cargarFotos);
//                    }
//                    mp.start();
//                    pd.show();
//                    esperarParaCambio3(8000);
//                }
//
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                Log.e("TAG", "Error: " + error.toString());
//            }
//        }) {
//            @Override
//            protected Map<String, String> getParams() throws AuthFailureError {
//
//
//                Map<String, String> params = new HashMap<>();
//                try {
//                    params.put("id_residencial", i_id_residencial);
//                    params.put("id_visita", i_id_visita);
//                    params.put("guardia_de_entrada", i_guardia_de_entrada);
//                    params.put("pasajeros", "");
//                    params.put("placas", "");
//                    params.put("foto1", "app" + ja1.getString(0) + "_" + anio + mes + dia);
//                    params.put("foto2", "");
//                    params.put("foto3", "");
//                    params.put("usuario", i_usuario);
//                    params.put("token", i_token);
//                    params.put("correo", i_correo);
//                    params.put("visita", i_id_visita);
//                    params.put("pluma_nombre", i_pluma_nombre);
//                    params.put("pluma_token", i_pluma_token);
//
//                } catch (JSONException e) {
//                    Log.e("TAG", "Error: " + e.toString());
//                }
//                return params;
//            }
//        };
//        requestQueue.add(stringRequest);
//    }

    public void Registrar() {

        String URL = "https://sanmateoresidencial.mx/plataforma/casetaV2/controlador/WIFI_SM/plumas_registro_15.php?bd_name=" + Conf.getBd() + "&bd_user=" + Conf.getBdUsu() + "&bd_pwd=" + Conf.getBdCon();
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                if (response.equals("error")) {
                    Toast.makeText(CamaraActivity.this, "No se registro", Toast.LENGTH_SHORT).show();
                } else {
                    mp3.stop();

                    ContentValues val_img1 = ValuesImagen(nombreFoto, Conf.getPin().trim() + "/caseta/" + nombreFoto.trim(), rutaImagen);
                    Uri uri = getContentResolver().insert(UrisContentProvider.URI_CONTENIDO_FOTOS_OFFLINE, val_img1);
                    //upload();
                    //Solo ejecutar si el servicio no se esta ejecutando
                    if (!servicioFotos()) {
                        Intent cargarFotos = new Intent(CamaraActivity.this, subirFotos.class);
                        startService(cargarFotos);
                    }
                    mp.start();
                    pd.show();
                    esperarParaCambio3(8000);
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
                try {
                    params.put("id_residencial", Conf.getResid().trim());
                    params.put("id_visita", ja1.getString(0).trim());
                    params.put("foto1", "app" + ja1.getString(0) + "_" + anio + mes + dia);

                } catch (JSONException e) {
                    Log.e("TAG", "Error: " + e.toString());
                }
                return params;
            }
        };
        requestQueue.add(stringRequest);
    }

//    public void upload(){
//
//        StorageReference mountainImagesRef = null;
//        try {
//            mountainImagesRef = storageReference.child("23/caseta/app"+ja1.getString(0)+"_"+anio+mes+dia);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//        UploadTask uploadTask = mountainImagesRef.putFile(uri_img);
//
//        // Listen for state changes, errors, and completion of the upload.
//        uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
//            @Override
//            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
//                // double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
//                //System.out.println("Upload is " + progress + "% done");
//                //Toast.makeText(getApplicationContext(),"Cargando Imagen PLACA " + progress + "%", Toast.LENGTH_SHORT).show();
//            }
//        }).addOnPausedListener(new OnPausedListener<UploadTask.TaskSnapshot>() {
//            @Override
//            public void onPaused(UploadTask.TaskSnapshot taskSnapshot) {
//                //Toast.makeText(AccesoActivity.this,"Pausado",Toast.LENGTH_SHORT).show();
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception exception) {
//                Toast.makeText(CamaraActivity.this,"Fallado", Toast.LENGTH_SHORT).show();
//            }
//        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//            @Override
//            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                pd.dismiss();
//                EnviarBT();
//            }
//        });
//    }

    public void esperarParaCambio3(int milisegundos) {


        han3.postDelayed(new Runnable() {
            public void run() {
                pd.dismiss();
                //EnviarBT();
            }
        }, milisegundos);
    }

//    public void EnviarBT2() {
//        Toast.makeText(CamaraActivity.this, "Registro Exitoso", Toast.LENGTH_LONG).show();
//        Intent intent = new Intent(getApplicationContext(), DashboardEntradas.class);
//        startActivity(intent);
//        finish();
//
//    }

    /*public void EnviarBT(){
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
    }*/

    public void Notificar(String Visita) {

        String URL = "https://sanmateoresidencial.mx/plataforma/casetaV2/controlador/WIFI_SM/vst_php9.php?bd_name=" + Conf.getBd() + "&bd_user=" + Conf.getBdUsu() + "&bd_pwd=" + Conf.getBdCon();
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                if (response.equals("error")) {

                } else {

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


                params.put("visita", Visita);
                params.put("pluma_nombre", Conf.getNombrelPluma());
                params.put("pluma_token", Conf.getTokenVigi());


                return params;
            }
        };
        requestQueue.add(stringRequest);
    }

    //Método para saber si es que el servicio ya se esta ejecutando
    public boolean servicioFotos() {
        //Obtiene los servicios que se estan ejecutando
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        //Se recorren todos los servicios obtnidos para saber si el servicio creado ya se esta ejecutando
        for (ActivityManager.RunningServiceInfo service : activityManager.getRunningServices(Integer.MAX_VALUE)) {
            if (subirFotos.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public ContentValues ValuesImagen(String nombre, String rutaFirebase, String rutaDispositivo) {
        ContentValues values = new ContentValues();
        values.put("titulo", nombre);
        values.put("direccionFirebase", rutaFirebase);
        values.put("rutaDispositivo", rutaDispositivo);
        return values;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(), DashboardEntradas.class);
        startActivity(intent);
        finish();
    }
}