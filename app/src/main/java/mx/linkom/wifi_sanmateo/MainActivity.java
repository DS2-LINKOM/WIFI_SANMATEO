package mx.linkom.wifi_sanmateo;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "TOKEN";
    private FirebaseAuth fAuth;
    private Configuracion Conf;
    EditText User, Pass, Pin;
    Button Iniciar;
    JSONArray ja1, ja2, ja3, ja4;
    CheckBox ver;
    String Tipo;
    int var1, var2, var3, var4, var5;

    private static final int MULTIPLE_PERMISSIONS_REQUEST_CODE = 12;
    private String[] permissions = new String[]{
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.CAMERA,
            android.Manifest.permission.INTERNET,
            android.Manifest.permission.ACCESS_NETWORK_STATE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.CHANGE_NETWORK_STATE,
            android.Manifest.permission.ACCESS_WIFI_STATE,
            android.Manifest.permission.CHANGE_WIFI_STATE,
            android.Manifest.permission.ACCESS_COARSE_LOCATION,
            android.Manifest.permission.BLUETOOTH,
            android.Manifest.permission.BLUETOOTH_ADMIN
    };

    private Handler handler;
    private Runnable runnable;
    ScrollView scrollView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        handler = new Handler();

        runnable = new Runnable() {
            @Override
            public void run() {
                // Handle idle time here
                Intent intent = new Intent(MainActivity.this, ProtectorPantalla.class);
                startActivity(intent);
            }
        };

        scrollView = (ScrollView) findViewById(R.id.scrollView);

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            /// Log.d("MIAPP", "Estás online");
        } else {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
            alertDialogBuilder.setTitle("Alerta");
            alertDialogBuilder
                    .setMessage("Conexión de Internet Inestable")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                        }
                    }).create().show();
            //Log.d("MIAPP", "Estás offline");
        }


        Conf = new Configuracion(this);
        Conf.setLOGIN("no");

        if (Global.TOKEN.equals("")) {
            Global.TOKEN = FirebaseInstanceId.getInstance().getToken();
            //  Log.d(TAG, "LINKOM ST: " + Global.TOKEN);
        }

        fAuth = FirebaseAuth.getInstance();

        User = (EditText) findViewById(R.id.usu);
        Pin = (EditText) findViewById(R.id.pin);
        Pass = (EditText) findViewById(R.id.contra);
        Iniciar = (Button) findViewById(R.id.entrar);
        ver = (CheckBox) findViewById(R.id.ver);

        TextWatcher textWatcher = new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // This method is called before the text is changed
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // This method is called when the text is changed
                if (count > before) {
                    handler.removeCallbacks(runnable); // Reset the delay timer
                    handler.postDelayed(runnable, Global.getTiemempoBloqueo());
                    Log.e("EVENT", "-_- Entradas Text");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                // This method is called after the text has been changed
            }
        };

        Pin.addTextChangedListener(textWatcher);
        User.addTextChangedListener(textWatcher);
        Pass.addTextChangedListener(textWatcher);

        ver.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (!isChecked) {
                    Pass.setTransformationMethod(PasswordTransformationMethod.getInstance());
                } else {
                    Pass.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }
            }
        });

        Iniciar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Iniciar.setEnabled(false);

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        botonPresionado(Iniciar, 0);

                        PIN();
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
    public void onStop() {
        super.onStop();
        handler.removeCallbacks(runnable); // Reset the delay timer
        Log.e("CICLO", "onStop");
    }

    public void botonPresionado(Button button, int estado){
        //estado --> 0=presionado   1=restablecer

        int tamanoTxt = 0;

        if (estado == 0){
            button.setBackgroundResource(R.drawable.usuario_bbt_press);
            button.setTextColor(0xFF5A6C81);
        }else if (estado == 1){
            button.setBackgroundResource(R.drawable.ripple_effect);
            button.setTextColor(0xFF27374A);
        }

    }


    public void PIN() {

        if (Pin.toString().isEmpty()) {

            Iniciar.setEnabled(true);
            botonPresionado(Iniciar, 1);

        } else {

            String URL = "https://sanmateoresidencial.mx/plataforma/casetaV2/controlador/WIFI_SM/pin.php";
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {

                @Override
                public void onResponse(String response) {
                    response = response.replace("][", ",");
                    if (response.length() > 0) {
                        try {
                            ja3 = new JSONArray(response);

                            Conf.setNomResi(ja3.getString(1));
                            Conf.setBd(ja3.getString(2));
                            Conf.setBdUsu(ja3.getString(3));
                            Conf.setBdCon(ja3.getString(4));
                            Conf.setPin(ja3.getString(6));
                            Residencial();

                        } catch (JSONException e) {

                            Iniciar.setEnabled(true);
                            botonPresionado(Iniciar, 1);

                            Toast.makeText(getApplicationContext(), "Datos Incorrectos", Toast.LENGTH_LONG).show();

                            e.printStackTrace();
                        }
                    } else {
                        Iniciar.setEnabled(true);
                        botonPresionado(Iniciar, 1);

                        Toast.makeText(getApplicationContext(), "Datos Incorrectos", Toast.LENGTH_LONG).show();
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
                    params.put("pin", Pin.getText().toString().trim());
                    return params;
                }
            };
            requestQueue.add(stringRequest);
        }
    }

    public void Residencial() {

        String URL = "https://sanmateoresidencial.mx/plataforma/casetaV2/controlador/WIFI_SM/residencial.php?bd_name=" + Conf.getBd() + "&bd_user=" + Conf.getBdUsu() + "&bd_pwd=" + Conf.getBdCon();

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                response = response.replace("][", ",");
                if (response.length() > 0) {
                    try {
                        ja4 = new JSONArray(response);
                        Conf.setResid(ja4.getString(0));

                        Login();
                    } catch (JSONException e) {
                        Toast.makeText(getApplicationContext(), "Datos Incorrectos", Toast.LENGTH_LONG).show();

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
                params.put("pin", Pin.getText().toString().trim());
                return params;
            }
        };
        requestQueue.add(stringRequest);

    }

    public void Login() {

        if (User.toString().isEmpty()) {

            Iniciar.setEnabled(true);
            botonPresionado(Iniciar, 1);

        } else {


            String URL = "https://sanmateoresidencial.mx/plataforma/casetaV2/controlador/WIFI_SM/session.php?bd_name=" + Conf.getBd() + "&bd_user=" + Conf.getBdUsu() + "&bd_pwd=" + Conf.getBdCon();
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {

                @Override
                public void onResponse(String response) {

                    response = response.replace("][", ",");
                    if (response.length() > 0) {
                        try {
                            ja1 = new JSONArray(response);

                            Conf.setUsu(ja1.getString(0));
                            Conf.setNombre(ja1.getString(3));
                            Conf.setTipoUsuario(ja1.getString(6));

                            Global.USER = ja1.getString(3);
                            Global.PASS = ja1.getString(4);
                            Global.EMAIL = ja1.getString(5);
                            Global.TIPO_U = ja1.getString(6);

                            //$tipoGuardia = array(array(0, 'Entrada'), array(1, 'Salida'), array(2, 'Súper Usuario'),array(3, 'Recepcionista'),array(4, 'Vigilante'));

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                CharSequence channelName = "LINK WIFI SAN MATEO";
                                String channelDescription = "Subir fotografías";
                                int importance = NotificationManager.IMPORTANCE_DEFAULT;
                                NotificationChannel channel = new NotificationChannel("upload_photos_id", channelName, importance);
                                channel.setDescription(channelDescription);
                                channel.enableVibration(true); // Enable vibration for this channel

                                NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                                notificationManager.createNotificationChannel(channel);
                            }


                            if (Conf.getTipoUsuario().equals("0") || Conf.getTipoUsuario().equals("1")) {
                                Notificacion();
                            } else if (Conf.getTipoUsuario().equals("4")) {

                                Intent i = new Intent(getApplication(), DashboardVigilante.class);
                                startActivity(i);
                                finish();
                            } else if (Conf.getTipoUsuario().equals("2") || Conf.getTipoUsuario().equals("3")) {
                                Intent i = new Intent(getApplication(), Dashboard.class);
                                startActivity(i);
                                finish();
                            }


                        } catch (JSONException e) {

                            Iniciar.setEnabled(true);
                            botonPresionado(Iniciar, 1);

                            Toast.makeText(getApplicationContext(), "Usuario y/o Contraseña Incorrectos", Toast.LENGTH_LONG).show();

                            e.printStackTrace();
                        }
                    } else {
                        User.setText("");
                        Pass.setText("");

                        Iniciar.setEnabled(true);
                        botonPresionado(Iniciar, 1);

                        Toast.makeText(getApplicationContext(), "Usuario y/o Contraseña Incorrectos", Toast.LENGTH_LONG).show();

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
                    params.put("Usuario", User.getText().toString().trim());
                    params.put("Pass", Pass.getText().toString().trim());
                    params.put("Token", mx.linkom.wifi_sanmateo.Global.TOKEN.trim());
                    params.put("Residencial", Conf.getResid());

                    return params;
                }
            };
            requestQueue.add(stringRequest);
        }
    }


    public void Notificacion() {


        String URL = "https://sanmateoresidencial.mx/plataforma/casetaV2/controlador/WIFI_SM/plumas.php?bd_name=" + Conf.getBd() + "&bd_user=" + Conf.getBdUsu() + "&bd_pwd=" + Conf.getBdCon();
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                response = response.replace("][", ",");
                if (response.length() > 0) {
                    try {
                        ja2 = new JSONArray(response);
                        Conf.setNombrelPluma(ja2.getString(0));
                        Conf.setTokenVigi(ja2.getString(1));
                        Conf.setiDVigilante(ja2.getString(2));

                        if (Conf.getTipoUsuario().equals("0")) {
                            Intent i = new Intent(getApplication(), DashboardEntradas.class);
                            // Intent i = new Intent(getApplication(), CamaraActivity.class);
                            startActivity(i);
                            finish();
                        } else if (Conf.getTipoUsuario().equals("1")) {
                            Intent i = new Intent(getApplication(), DashboardSalidas.class);
                            startActivity(i);
                            finish();
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

                if (Conf.getTipoUsuario().equals("0")) { //ENTRADAS
                    Tipo = "1";
                } else if (Conf.getTipoUsuario().equals("1")) { //SALIDAS
                    Tipo = "2";
                }
                Map<String, String> params = new HashMap<>();
                params.put("idApp", Conf.getUsu().trim());
                params.put("idTipo", Tipo);

                return params;
            }
        };
        requestQueue.add(stringRequest);

    }


    @Override
    protected void onStart() {


        super.onStart();
        if (ActivityCompat.checkSelfPermission(MainActivity.this, permissions[0]) != PackageManager.PERMISSION_GRANTED) {
            //Si alguno de los permisos no esta concedido lo solicita
            ActivityCompat.requestPermissions(MainActivity.this, permissions, MULTIPLE_PERMISSIONS_REQUEST_CODE);
        } else {
            //Si todos los permisos estan concedidos prosigue con el flujo normal
            permissionGranted();

            FirebaseUser fUser = fAuth.getCurrentUser();
            if (fUser != null) {


                if (Conf.getTipoUsuario().equals("0")) {
                    Intent i = new Intent(getApplication(), DashboardEntradas.class);
                    //Intent i = new Intent(getApplication(), CamaraActivity.class);

                    startActivity(i);
                    finish();
                } else if (Conf.getTipoUsuario().equals("1")) {
                    Intent i = new Intent(getApplication(), DashboardSalidas.class);
                    startActivity(i);
                    finish();
                } else if (Conf.getTipoUsuario().equals("4")) {

                    Intent i = new Intent(getApplication(), DashboardVigilante.class);
                    startActivity(i);
                    finish();
                } else if (Conf.getTipoUsuario().equals("2") || Conf.getTipoUsuario().equals("3")) {
                    Intent i = new Intent(getApplication(), Dashboard.class);
                    startActivity(i);
                    finish();
                }


            } else {
                Toast.makeText(getApplicationContext(), "Inicie Sesion", Toast.LENGTH_SHORT).show();
            }
        }


    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MULTIPLE_PERMISSIONS_REQUEST_CODE:
                if (validatePermissions(grantResults)) {
                    permissionGranted();
                } else {
                    permissionRejected();
                }
                break;
        }
    }

    private boolean validatePermissions(int[] grantResults) {
        boolean allGranted = false;
        for (int i = 0; i < permissions.length; i++) {
            if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                allGranted = true;
            } else {
                allGranted = false;
                break;
            }
        }
        return allGranted;
    }

    public void permissionGranted() {
        //Toast.makeText(getApplicationContext(), getString(R.string.permission_granted), Toast.LENGTH_SHORT).show();
    }

    public void permissionRejected() {
        Toast.makeText(getApplicationContext(), getString(R.string.permission_rejected), Toast.LENGTH_SHORT).show();

    }


}
