package mx.linkom.wifi_sanmateo;

import static android.view.View.GONE;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AnimationDrawable;
import android.media.ExifInterface;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import mx.linkom.wifi_sanmateo.deteccionPlacas.DetectarPlaca;
import mx.linkom.wifi_sanmateo.deteccionPlacas.capturarPlaca;
import mx.linkom.wifi_sanmateo.deteccionPlacas.objectDetectorClass;
import mx.linkom.wifi_sanmateo.fotosSegundoPlano.UrisContentProvider;
import mx.linkom.wifi_sanmateo.fotosSegundoPlano.subirFotos;

public class FotoPlaca extends mx.linkom.wifi_sanmateo.Menu implements  View.OnClickListener {

    Button btnTomarFoto, btnRegistrar;
    String nombreImagen1, rutaImagen1;
    Uri uri_img;
    LinearLayout linLayCampoPlacas;
    EditText editTextPlacas;
    boolean modeloCargado = false;
    mx.linkom.wifi_sanmateo.deteccionPlacas.objectDetectorClass objectDetectorClass;
    ImageView imageViewFotoPlaca;
    Configuracion Conf;
    //MediaPlayer mp;
    ProgressDialog pd;
    //Handler han3;
    String intent_id_residencial, intent_id_visita, intent_foto, intent_ruta_foto, intent_guardia_de_entrada, intent_usuario, intent_token, intent_correo, intent_visita, intent_pluma_nombre, intent_pluma_token, intent_id_vigilante, intent_id_pluma;
    Date FechaA;
    String FechaC;

    private Handler handler;
    private Runnable runnable;
    ScrollView scrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_foto_placa);

        btnTomarFoto = (Button) findViewById(R.id.btnTomarFoto);
        btnRegistrar = (Button) findViewById(R.id.btnRegistrar);
        linLayCampoPlacas = (LinearLayout) findViewById(R.id.linLayCampoPlacas);
        editTextPlacas = (EditText) findViewById(R.id.editTextPlacas);
        imageViewFotoPlaca = (ImageView) findViewById(R.id.imageViewFotoPlaca);
        Conf = new Configuracion(this);

        //mp = MediaPlayer.create(this, R.raw.audio6_graciasentrada);

        pd = new ProgressDialog(this);
        pd.setMessage("Registrando...");

        Global.ocultarBarrasNavegacionEstado(this);
        Global.aumentarVolumen(this);
        Global.evitarSuspenderPantalla(this);

        //han3 = new Handler();

        handler = new Handler();

        runnable = new Runnable() {
            @Override
            public void run() {
                // Handle idle time here
                Intent intent = new Intent(FotoPlaca.this, ProtectorPantalla.class);
                startActivity(intent);
            }
        };

        scrollView = (ScrollView) findViewById(R.id.scrollView);

        botonPresionado(btnRegistrar, 0);

        try {
            objectDetectorClass = new objectDetectorClass(getAssets(), "detectPLacaLKM.tflite", "labelmapTf.txt", 320);
            Log.e("FotoPLaca", "Modelo cargado correctamente");
            modeloCargado = true;
        } catch (IOException e) {
            Log.e("FotoPLaca", "Error al cargar modelo");
            modeloCargado = false;
        }

        btnTomarFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnTomarFoto.setEnabled(false);

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        botonPresionado(btnTomarFoto, 0);
                        tomarFotoPlaca();
                    }
                }, 300);
            }
        });

        btnRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnRegistrar.setEnabled(false);

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        pd.show();
                        Registrar_pluma();
                    }
                }, 300);
            }
        });

        Intent intent = getIntent();
        intent_id_residencial = intent.getStringExtra("id_residencial");
        intent_id_visita = intent.getStringExtra("id_visita");
        intent_foto = intent.getStringExtra("foto1");
        intent_ruta_foto = intent.getStringExtra("rutafoto1");
        intent_guardia_de_entrada = intent.getStringExtra("guardia_de_entrada");
        intent_usuario = intent.getStringExtra("usuario");
        intent_token = intent.getStringExtra("token");
        intent_correo = intent.getStringExtra("correo");
        intent_visita = intent.getStringExtra("visita");
        intent_pluma_nombre = intent.getStringExtra("pluma_nombre");
        intent_pluma_token = intent.getStringExtra("pluma_token");
        intent_id_vigilante = intent.getStringExtra("id_vigilante");
        intent_id_pluma = intent.getStringExtra("id_pluma");

        if (intent_id_residencial == null || intent_id_visita == null || intent_foto == null) {
            Log.e("INTENT", "No se enviaron los datos");
            intent_id_residencial = "";
            intent_id_visita = "";
            intent_foto = "";
            intent_ruta_foto = "";
            intent_guardia_de_entrada = "";
            intent_usuario = "";
            intent_token = "";
            intent_correo = "";
            intent_visita = "";
            intent_pluma_nombre = "";
            intent_pluma_token = "";
            intent_id_vigilante = "";
            intent_id_pluma = "";
            Toast.makeText(this, "No se enviaron los datos", Toast.LENGTH_SHORT).show();
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

    //ALETORIO
    Random primero = new Random();
    int prime = primero.nextInt(9);

    String[] segundo = {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j",
            "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"};
    int numRandonsegun = (int) Math.round(Math.random() * 25);

    Random tercero = new Random();
    int tercer = tercero.nextInt(9);

    String[] cuarto = {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j",
            "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"};
    int numRandoncuart = (int) Math.round(Math.random() * 25);

    String numero_aletorio = prime + segundo[numRandonsegun] + tercer + cuarto[numRandoncuart];

    Calendar fecha = Calendar.getInstance();
    int anio = fecha.get(Calendar.YEAR);
    int mes = fecha.get(Calendar.MONTH) + 1;
    int dia = fecha.get(Calendar.DAY_OF_MONTH);
    int segundos = fecha.get(Calendar.SECOND);


    public void tomarFotoPlaca() {
        Intent intentCaptura = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intentCaptura.addFlags(intentCaptura.FLAG_GRANT_READ_URI_PERMISSION);

        if (intentCaptura.resolveActivity(getPackageManager()) != null) {

            File foto = null;
            try {
                nombreImagen1 = "app" + anio + mes + dia + segundos + "-" + numero_aletorio + ".png";
                foto = new File(getApplication().getExternalFilesDir(null), nombreImagen1);
                rutaImagen1 = foto.getAbsolutePath();
            } catch (Exception ex) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(FotoPlaca.this);
                alertDialogBuilder.setTitle("Alerta");
                alertDialogBuilder
                        .setMessage("Error al capturar la foto")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                            }
                        }).create().show();
            }
            if (foto != null) {

                uri_img = FileProvider.getUriForFile(getApplicationContext(), getApplicationContext().getPackageName() + ".provider", foto);
                intentCaptura.putExtra(MediaStore.EXTRA_OUTPUT, uri_img);
                startActivityForResult(intentCaptura, 0);
            }
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 0) {


                Bitmap bitmap = BitmapFactory.decodeFile(getApplicationContext().getExternalFilesDir(null) + "/" + nombreImagen1);

                if (modeloCargado) {
                    String txtPlaca = DetectarPlaca.getTextFromImage(DetectarPlaca.reconocerPlaca(bitmap, objectDetectorClass, 1), FotoPlaca.this);
                    if (!txtPlaca.isEmpty()) editTextPlacas.setText(txtPlaca);

                }

                Bitmap foto = DetectarPlaca.fechaHoraFoto(bitmap);
                FileOutputStream fos = null;

                try {
                    fos = new FileOutputStream(rutaImagen1);
                    foto.compress(Bitmap.CompressFormat.JPEG, 100, fos); // compress and save as JPEG
                    fos.flush();
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                linLayCampoPlacas.setVisibility(View.VISIBLE);
                imageViewFotoPlaca.setImageBitmap(foto);
                imageViewFotoPlaca.setVisibility(View.VISIBLE);
                btnTomarFoto.setEnabled(true);
                btnRegistrar.setEnabled(true);
                botonPresionado(btnRegistrar, 1);
                botonPresionado(btnTomarFoto, 1);

            }
        }
    }

    public void Registrar_pluma() {

        String URL = "https://sanmateoresidencial.mx/plataforma/casetaV2/controlador/WIFI_SM/plumas_registro_1.php?bd_name=" + Conf.getBd() + "&bd_user=" + Conf.getBdUsu() + "&bd_pwd=" + Conf.getBdCon();
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                if (response.equals("error")) {
                    pd.dismiss();
                    Notificar(intent_visita);
                    Toast.makeText(FotoPlaca.this, "Error al registrar entrada, código de error: regplu7354FP", Toast.LENGTH_SHORT).show();
                    btnRegistrar.setEnabled(true);
                    botonPresionado(btnRegistrar, 1);
                } else {
                    RegistrarVisita();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("TAG", "Error: " + error.toString());
                pd.dismiss();
                Toast.makeText(FotoPlaca.this, "Error al registrar entrada, código de error: regplu7354FP1", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {


                Map<String, String> params = new HashMap<>();
                params.put("id_residencial", intent_id_residencial);
                params.put("id_visita", intent_id_visita);
                params.put("id_vigilante", intent_id_vigilante);
                params.put("id_pluma", intent_id_pluma);

                return params;
            }
        };
        requestQueue.add(stringRequest);
    }

    public void RegistrarVisita() {


        FechaA = Calendar.getInstance().getTime();
        FechaC = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(FechaA);


        String URL = "https://sanmateoresidencial.mx/plataforma/casetaV2/controlador/WIFI_SM/vst_php5.php?bd_name=" + Conf.getBd() + "&bd_user=" + Conf.getBdUsu() + "&bd_pwd=" + Conf.getBdCon();
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                pd.dismiss();

                if (response.equals("error")) {
                    Toast.makeText(FotoPlaca.this, "Error al registrar entrada, código de error: regvis7354FP", Toast.LENGTH_SHORT).show();
                    Notificar(intent_visita);
                    btnRegistrar.setEnabled(true);
                    botonPresionado(btnRegistrar, 1);
                } else {

                    ContentValues val_img1 = ValuesImagen(nombreImagen1, Conf.getPin().trim() + "/caseta/" + nombreImagen1.trim(), rutaImagen1);
                    Uri uri = getContentResolver().insert(UrisContentProvider.URI_CONTENIDO_FOTOS_OFFLINE, val_img1);

                    ContentValues val_img2 = ValuesImagen(intent_foto, Conf.getPin().trim() + "/caseta/" + intent_foto.trim(), intent_ruta_foto);
                    Uri uri2 = getContentResolver().insert(UrisContentProvider.URI_CONTENIDO_FOTOS_OFFLINE, val_img2);

                    //Solo ejecutar si el servicio no se esta ejecutando
                    if (!servicioFotos()) {
                        Intent cargarFotos = new Intent(FotoPlaca.this, subirFotos.class);
                        startService(cargarFotos);
                    }
                    /*mp.start();
                    esperarParaCambio3(8000);*/

                    Intent intent = new Intent(FotoPlaca.this, LimiteVelocidad.class);
                    startActivity(intent);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("TAG", "Error: " + error.toString());
                pd.dismiss();
                Toast.makeText(FotoPlaca.this, "Error al registrar entrada, código de error: regvis7354FP1", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {


                Map<String, String> params = new HashMap<>();

                params.put("id_residencial", intent_id_residencial);
                params.put("id_visita", intent_id_visita);
                params.put("guardia_de_entrada", intent_guardia_de_entrada);
                params.put("pasajeros", "");
                params.put("placas", editTextPlacas.getText().toString().trim());
                params.put("foto1", intent_foto);
                params.put("foto2", nombreImagen1);
                params.put("foto3", "");
                params.put("usuario", intent_usuario);
                params.put("token", intent_token);
                params.put("correo", intent_correo);
                params.put("visita", intent_visita);
                params.put("pluma_nombre", intent_pluma_nombre);
                params.put("pluma_token", intent_pluma_token);

                return params;
            }
        };
        requestQueue.add(stringRequest);
    }

    /*public void esperarParaCambio3(int milisegundos) {


        han3.postDelayed(new Runnable() {
            public void run() {
                pd.dismiss();

                Intent intent = new Intent(FotoPlaca.this, DashboardEntradas.class);
                startActivity(intent);
            }
        }, milisegundos);
    }*/

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

    public void botonPresionado(Button button, int estado) {
        //estado --> 0=presionado   1=restablecer

        int ancho = 0, alto = 0, tamanoTxt = 0;

        if (estado == 0) {
            button.setBackgroundResource(R.drawable.usuario_bbt_press);
            ancho = 255;
            alto = 58;
            tamanoTxt = 25;
            button.setTextColor(0xFF5A6C81);
        } else if (estado == 1) {
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
}