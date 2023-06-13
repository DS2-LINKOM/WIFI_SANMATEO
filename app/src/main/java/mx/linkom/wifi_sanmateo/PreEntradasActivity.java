package mx.linkom.wifi_sanmateo;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class PreEntradasActivity extends mx.linkom.wifi_sanmateo.Menu2 {
    private Configuracion Conf;
    private RadioGroup rdgGrupo;
    RadioButton visi,prove,taxi;
    String valor;
    JSONArray ja1,ja2,ja3,ja4,ja5,ja6;
    EditText Nombre,Numero,Placas;
    Spinner Calle,Pasajeros;
    ArrayList<String>calles,names;
    String f1,f2,f3;
    LinearLayout espacio1,espacio2,espacio3,espacio4,espacio5,espacio6,espacio7,espacio8,espacio9,espacio10;
    LinearLayout registrar1,registrar2,registrar3,registrar4;
    Button reg1,reg2,reg3,reg4,btn_foto1,btn_foto2,btn_foto3;
    LinearLayout Foto1View,Foto2View,Foto3View;
    LinearLayout Foto1,Foto2,Foto3;
    ImageView view1,view2,view3;
    TextView nombre_foto1,nombre_foto2,nombre_foto3;

    FirebaseStorage storage;
    StorageReference storageReference;
    Bitmap bitmap,bitmap2,bitmap3;
    ProgressDialog pd,pd2,pd3;
    int foto;
    int fotos1,fotos2,fotos3;
    Uri uri_img,uri_img2,uri_img3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preentradas);
        Conf = new Configuracion(this);


        storage= FirebaseStorage.getInstance();
        storageReference=storage.getReference();

        reg1 = (Button) findViewById(R.id.reg1);
        reg2 = (Button) findViewById(R.id.reg2);
        reg3 = (Button) findViewById(R.id.reg3);
        reg4 = (Button) findViewById(R.id.reg4);
        btn_foto1 = (Button) findViewById(R.id.btn_foto1);
        btn_foto2 = (Button) findViewById(R.id.btn_foto2);
        btn_foto3 = (Button) findViewById(R.id.btn_foto3);

        nombre_foto1 = (TextView) findViewById(R.id.nombre_foto1);
        nombre_foto2 = (TextView) findViewById(R.id.nombre_foto2);
        nombre_foto3 = (TextView) findViewById(R.id.nombre_foto3);

        view1 = (ImageView) findViewById(R.id.view1);
        view2 = (ImageView) findViewById(R.id.view2);
        view3 = (ImageView) findViewById(R.id.view3);

        espacio1 = (LinearLayout) findViewById(R.id.espacio1);
        espacio2 = (LinearLayout) findViewById(R.id.espacio2);
        espacio3 = (LinearLayout) findViewById(R.id.espacio3);
        espacio4 = (LinearLayout) findViewById(R.id.espacio4);
        espacio5 = (LinearLayout) findViewById(R.id.espacio5);
        espacio6 = (LinearLayout) findViewById(R.id.espacio6);
        espacio7 = (LinearLayout) findViewById(R.id.espacio7);
        espacio8 = (LinearLayout) findViewById(R.id.espacio8);
        espacio9 = (LinearLayout) findViewById(R.id.espacio9);
        espacio10 = (LinearLayout) findViewById(R.id.espacio10);
        registrar1 = (LinearLayout) findViewById(R.id.registrar1);
        registrar2 = (LinearLayout) findViewById(R.id.registrar2);
        registrar3 = (LinearLayout) findViewById(R.id.registrar3);
        registrar4 = (LinearLayout) findViewById(R.id.registrar4);
        Foto1View = (LinearLayout) findViewById(R.id.Foto1View);
        Foto2View = (LinearLayout) findViewById(R.id.Foto2View);
        Foto3View = (LinearLayout) findViewById(R.id.Foto3View);
        Foto1 = (LinearLayout) findViewById(R.id.Foto1);
        Foto2 = (LinearLayout) findViewById(R.id.Foto2);
        Foto3 = (LinearLayout) findViewById(R.id.Foto3);

        Conf = new Configuracion(this);
        calles = new ArrayList<String>();
        names = new ArrayList<String>();
        rdgGrupo = (RadioGroup)findViewById(R.id.rdgGrupo);
        visi = (RadioButton)findViewById(R.id.Visita);
        prove = (RadioButton)findViewById(R.id.Proveedor);
        taxi = (RadioButton)findViewById(R.id.Taxista);
        Nombre = (EditText)findViewById(R.id.setNombre);
        Numero = (EditText)findViewById(R.id.setNumero);
        Placas = (EditText)findViewById(R.id.setPlacas);


        Calle = (Spinner)findViewById(R.id.setCalle);
        Pasajeros = (Spinner)findViewById(R.id.setPasajeros);

        cargarSpinner2();
        calles();
        menu();
       // Visita();

        pd= new ProgressDialog(this);
        pd.setMessage("Subiendo Imagen 1...");

        pd2= new ProgressDialog(this);
        pd2.setMessage("Subiendo Imagen 2...");

        pd3= new ProgressDialog(this);
        pd3.setMessage("Subiendo Imagen 3...");



        reg1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Verificar();
            }
        });

        reg2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Verificar();
            }
        });

        reg3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Verificar();
            }
        });

        reg4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Verificar();
            }
        });

        btn_foto1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                foto=1;
                imgFoto();
            }
        });

        btn_foto2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                foto=2;
                imgFoto2();
            }
        });

        btn_foto3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                foto=3;
                imgFoto3();
            }
        });
        Placas.setText(Conf.getPlacas().trim());
    }

    //ALETORIO
    Random primero = new Random();
    int prime= primero.nextInt(9);

    String[] segundo = {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j",
            "k", "l", "m","n","o","p","q","r","s","t","u","v","w", "x","y","z" };
    int numRandonsegun = (int) Math.round(Math.random() * 26 ) ;

    Random tercero = new Random();
    int tercer= tercero.nextInt(9);

    String[] cuarto = {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j",
            "k", "l", "m","n","o","p","q","r","s","t","u","v","w", "x","y","z" };
    int numRandoncuart = (int) Math.round(Math.random() * 26 ) ;

    String numero_aletorio=prime+segundo[numRandonsegun]+tercer+cuarto[numRandoncuart];


    //ALETORIO2

    Random primero2 = new Random();
    int prime2= primero2.nextInt(9);

    String[] segundo2 = {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j",
            "k", "l", "m","n","o","p","q","r","s","t","u","v","w", "x","y","z" };
    int numRandonsegun2 = (int) Math.round(Math.random() * 26 ) ;

    Random tercero2 = new Random();
    int tercer2= tercero2.nextInt(9);

    String[] cuarto2 = {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j",
            "k", "l", "m","n","o","p","q","r","s","t","u","v","w", "x","y","z" };
    int numRandoncuart2 = (int) Math.round(Math.random() * 26 ) ;

    String numero_aletorio2=prime2+segundo2[numRandonsegun2]+tercer2;



    //ALETORIO3

    Random primero3 = new Random();
    int prime3= primero3.nextInt(9);

    String[] segundo3 = {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j",
            "k", "l", "m","n","o","p","q","r","s","t","u","v","w", "x","y","z" };
    int numRandonsegun3 = (int) Math.round(Math.random() * 26 ) ;

    Random tercero3 = new Random();
    int tercer3= tercero3.nextInt(9);

    String[] cuarto3 = {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j",
            "k", "l", "m","n","o","p","q","r","s","t","u","v","w", "x","y","z" };
    int numRandoncuart3 = (int) Math.round(Math.random() * 26 ) ;

    String numero_aletorio3=prime3+segundo3[numRandonsegun3]+tercer3+cuarto3[numRandoncuart3];

    Calendar fecha = Calendar.getInstance();
    int anio = fecha.get(Calendar.YEAR);
    int mes = fecha.get(Calendar.MONTH) + 1;
    int dia = fecha.get(Calendar.DAY_OF_MONTH);



    public void cargarSpinner2(){
        names.add("Selecciona...");
        names.add("1");
        names.add("2");
        names.add("3");
        names.add("4");
        names.add("5");
        names.add("6");
        names.add("7");
        names.add("8");
        names.add("9");
        names.add("10");
        names.add("11");
        names.add("12");
        names.add("13");
        names.add("14");
        names.add("15");
        names.add("16");
        names.add("17");
        names.add("18");

        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line,names);
        Pasajeros.setAdapter(adapter1);


    }

    //FOTOS

    public void imgFoto(){
        Intent intentCaptura = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intentCaptura.addFlags(intentCaptura.FLAG_GRANT_READ_URI_PERMISSION);
        File foto = new File(getApplication().getExternalFilesDir(null),"preentradas1.png");
        uri_img= FileProvider.getUriForFile(getApplicationContext(),getApplicationContext().getPackageName()+".provider",foto);
        intentCaptura.putExtra(MediaStore.EXTRA_OUTPUT,uri_img);
        startActivityForResult( intentCaptura, 0);
    }

    public void imgFoto2(){
        Intent intentCaptura = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intentCaptura.addFlags(intentCaptura.FLAG_GRANT_READ_URI_PERMISSION);
        File foto = new File(getApplication().getExternalFilesDir(null),"preentradas2.png");
        uri_img2= FileProvider.getUriForFile(getApplicationContext(),getApplicationContext().getPackageName()+".provider",foto);
        intentCaptura.putExtra(MediaStore.EXTRA_OUTPUT,uri_img2);
        startActivityForResult( intentCaptura, 1);
    }

    public void imgFoto3(){
        Intent intentCaptura = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intentCaptura.addFlags(intentCaptura.FLAG_GRANT_READ_URI_PERMISSION);
        File foto = new File(getApplication().getExternalFilesDir(null),"preentradas3.png");
        uri_img3= FileProvider.getUriForFile(getApplicationContext(),getApplicationContext().getPackageName()+".provider",foto);
        intentCaptura.putExtra(MediaStore.EXTRA_OUTPUT,uri_img3);
        startActivityForResult( intentCaptura, 2);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (resultCode == RESULT_OK) {
            if (requestCode == 0) {

                Bitmap bitmap = BitmapFactory.decodeFile(getApplicationContext().getExternalFilesDir(null) + "/preentradas1.png");

                view1.setVisibility(View.VISIBLE);
                view1.setImageBitmap(bitmap);
                fotos1=1;

            }
            if (requestCode == 1) {

                Bitmap bitmap2 = BitmapFactory.decodeFile(getApplicationContext().getExternalFilesDir(null) + "/preentradas2.png");

                view2.setVisibility(View.VISIBLE);
                view2.setImageBitmap(bitmap2);
                fotos2=1;


            }

            if (requestCode == 2) {


                Bitmap bitmap3 = BitmapFactory.decodeFile(getApplicationContext().getExternalFilesDir(null) + "/preentradas3.png");

                view3.setVisibility(View.VISIBLE);
                view3.setImageBitmap(bitmap3);
                fotos3=1;



            }
        }
    }


    public void calles(){

        String URL = "https://sanmateoresidencial.mx/plataforma/casetaV2/controlador/WIFI_SANMATEO/vst_reg_1.php?bd_name="+Conf.getBd()+"&bd_user="+Conf.getBdUsu()+"&bd_pwd="+Conf.getBdCon();
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {


            @Override
            public void onResponse(String response) {
                response = response.replace("][",",");
                if (response.length()>0){
                    try {
                        ja1 = new JSONArray(response);
                        cargarSpinner();
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

    public void cargarSpinner(){

        calles.add("Seleccionar...");

        try{
            for (int i=0;i<ja1.length();i+=1){
                calles.add(ja1.getString(i+0));
            }

            ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line,calles);
            Calle.setAdapter(adapter1);

        }catch (Exception e){
            e.printStackTrace();
        }
    }


    public void menu() {
        String URL = "https://sanmateoresidencial.mx/plataforma/casetaV2/controlador/WIFI_SANMATEO/menu.php?bd_name="+Conf.getBd()+"&bd_user="+Conf.getBdUsu()+"&bd_pwd="+Conf.getBdCon();
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                response = response.replace("][", ",");
                if (response.length() > 0) {
                    try {
                        ja2 = new JSONArray(response);
                        submenu(ja2.getString(0));
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
                params.put("id_residencial", Conf.getResid());

                return params;
            }
        };
        requestQueue.add(stringRequest);
    }

    public void submenu(final String id_app) {
        String URL = "https://sanmateoresidencial.mx/plataforma/casetaV2/controlador/WIFI_SANMATEO/menu_2.php?bd_name="+Conf.getBd()+"&bd_user="+Conf.getBdUsu()+"&bd_pwd="+Conf.getBdCon();
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                if(response.equals("error")){
                    int $arreglo[]={0};
                    try {
                        ja3 = new JSONArray($arreglo);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    imagenes();
                }else {
                    response = response.replace("][", ",");
                    if (response.length() > 0) {
                        try {
                            ja3 = new JSONArray(response);

                            imagenes();
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
                params.put("id_app", id_app.trim());
                params.put("id_residencial", Conf.getResid());

                return params;
            }
        };
        requestQueue.add(stringRequest);
    }


    public void imagenes(){
        try {
            Visita();
            if(ja3.getString(0).equals("0") || ja3.getString(3).equals("0")) {
                registrar1.setVisibility(View.VISIBLE);
                espacio1.setVisibility(View.VISIBLE);

                Foto1.setVisibility(View.GONE);
                espacio2.setVisibility(View.GONE);
                Foto1View.setVisibility(View.GONE);
                espacio3.setVisibility(View.GONE);
                registrar2.setVisibility(View.GONE);
                espacio4.setVisibility(View.GONE);
                Foto2.setVisibility(View.GONE);
                espacio5.setVisibility(View.GONE);
                Foto2View.setVisibility(View.GONE);
                espacio6.setVisibility(View.GONE);
                registrar3.setVisibility(View.GONE);
                espacio7.setVisibility(View.GONE);
                Foto3.setVisibility(View.GONE);
                espacio8.setVisibility(View.GONE);
                Foto3View.setVisibility(View.GONE);
                espacio9.setVisibility(View.GONE);
                registrar4.setVisibility(View.VISIBLE);
                espacio10.setVisibility(View.VISIBLE);
            }else if(ja3.getString(3).equals("1") && ja3.getString(5).equals("0") && ja3.getString(7).equals("0")){
                registrar1.setVisibility(View.GONE);
                espacio1.setVisibility(View.GONE);

                Foto1.setVisibility(View.VISIBLE);
                espacio2.setVisibility(View.VISIBLE);
                nombre_foto1.setVisibility(View.VISIBLE);
                nombre_foto1.setText(ja3.getString(4)+":");

                Foto1View.setVisibility(View.VISIBLE);
                espacio3.setVisibility(View.VISIBLE);
                registrar2.setVisibility(View.GONE);
                espacio4.setVisibility(View.GONE);
                Foto2.setVisibility(View.GONE);
                espacio5.setVisibility(View.GONE);
                Foto2View.setVisibility(View.GONE);
                espacio6.setVisibility(View.GONE);
                registrar3.setVisibility(View.GONE);
                espacio7.setVisibility(View.GONE);
                Foto3.setVisibility(View.GONE);
                espacio8.setVisibility(View.GONE);
                Foto3View.setVisibility(View.GONE);
                espacio9.setVisibility(View.GONE);
                registrar4.setVisibility(View.VISIBLE);
                espacio10.setVisibility(View.VISIBLE);
            }else if(ja3.getString(3).equals("1") && ja3.getString(5).equals("1") && ja3.getString(7).equals("0")){
                registrar1.setVisibility(View.GONE);
                espacio1.setVisibility(View.GONE);

                Foto1.setVisibility(View.VISIBLE);
                espacio2.setVisibility(View.VISIBLE);
                nombre_foto1.setVisibility(View.VISIBLE);
                nombre_foto1.setText(ja3.getString(4)+":");
                Foto1View.setVisibility(View.VISIBLE);
                espacio3.setVisibility(View.VISIBLE);

                registrar2.setVisibility(View.GONE);
                espacio4.setVisibility(View.GONE);
                nombre_foto2.setVisibility(View.VISIBLE);
                nombre_foto2.setText(ja3.getString(6)+":");
                Foto2.setVisibility(View.VISIBLE);
                espacio5.setVisibility(View.VISIBLE);
                Foto2View.setVisibility(View.VISIBLE);
                espacio6.setVisibility(View.VISIBLE);

                registrar3.setVisibility(View.GONE);
                espacio7.setVisibility(View.GONE);
                Foto3.setVisibility(View.GONE);
                espacio8.setVisibility(View.GONE);
                Foto3View.setVisibility(View.GONE);
                espacio9.setVisibility(View.GONE);
                registrar4.setVisibility(View.VISIBLE);
                espacio10.setVisibility(View.VISIBLE);
            }else if(ja3.getString(3).equals("1") && ja3.getString(5).equals("1") && ja3.getString(7).equals("1")){
                registrar1.setVisibility(View.GONE);
                espacio1.setVisibility(View.GONE);

                Foto1.setVisibility(View.VISIBLE);
                espacio2.setVisibility(View.VISIBLE);
                nombre_foto1.setVisibility(View.VISIBLE);
                nombre_foto1.setText(ja3.getString(4)+":");
                Foto1View.setVisibility(View.VISIBLE);
                espacio3.setVisibility(View.VISIBLE);

                registrar2.setVisibility(View.GONE);
                espacio4.setVisibility(View.GONE);
                nombre_foto2.setVisibility(View.VISIBLE);
                nombre_foto2.setText(ja3.getString(6)+":");
                Foto2.setVisibility(View.VISIBLE);
                espacio5.setVisibility(View.VISIBLE);
                Foto2View.setVisibility(View.VISIBLE);
                espacio6.setVisibility(View.VISIBLE);

                registrar3.setVisibility(View.GONE);
                espacio7.setVisibility(View.GONE);
                nombre_foto3.setVisibility(View.VISIBLE);
                nombre_foto3.setText(ja3.getString(8)+":");
                Foto3.setVisibility(View.VISIBLE);
                espacio8.setVisibility(View.VISIBLE);
                Foto3View.setVisibility(View.VISIBLE);
                espacio9.setVisibility(View.VISIBLE);
                registrar4.setVisibility(View.VISIBLE);
                espacio10.setVisibility(View.VISIBLE);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void Visita(){

        String URL = "https://sanmateoresidencial.mx/plataforma/casetaV2/controlador/WIFI_SANMATEO/vst_reg_5.php?bd_name="+Conf.getBd()+"&bd_user="+Conf.getBdUsu()+"&bd_pwd="+Conf.getBdCon();
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                response = response.replace("][",",");
                if (response.length()>0){
                    try {
                        ja5 = new JSONArray(response);
                        Dtl(ja5.getString(0));

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
                params.put("id", Conf.getQR());
                params.put("id_residencial", Conf.getResid().trim());
                return params;
            }
        };
        requestQueue.add(stringRequest);
    }

    public void Dtl(final String IdUsu){

        String URL = "https://sanmateoresidencial.mx/plataforma/casetaV2/controlador/WIFI_SANMATEO/vst_reg_8.php?bd_name="+Conf.getBd()+"&bd_user="+Conf.getBdUsu()+"&bd_pwd="+Conf.getBdCon();
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                response = response.replace("][",",");
                if (response.length()>0){
                    try {
                        ja6 = new JSONArray(response);

                        Datos();
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
                params.put("id", IdUsu.trim());
                params.put("id_residencial", Conf.getResid().trim());
                return params;
            }
        };
        requestQueue.add(stringRequest);
    }


    public void Datos(){
        try {
            Nombre.setText(ja5.getString(7));

            if(ja5.getString(4).equals("1") || ja5.getString(4).equals("0")){
                visi.setChecked(true);
                taxi.setChecked(false);
                prove.setChecked(false);
            }else if(ja5.getString(4).equals("2")){
                visi.setChecked(false);
                taxi.setChecked(true);
                prove.setChecked(false);
            }else if(ja5.getString(4).equals("3")){
                visi.setChecked(false);
                taxi.setChecked(false);
                prove.setChecked(true);
            }
            storageReference.child("caseta/"+ja6.getString(11))
                    .getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {

                @Override

                public void onSuccess(Uri uri) {
                    Picasso.with(PreEntradasActivity.this)
                            .load(uri)
                            .error(R.drawable.log)
                            .fit()
                            .centerInside()
                            .into(view1);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle any errors
                }
            });

            storageReference.child("caseta/"+ja6.getString(12))
                    .getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {

                @Override

                public void onSuccess(Uri uri) {
                    Picasso.with(PreEntradasActivity.this)
                            .load(uri)
                            .error(R.drawable.log)
                            .fit()
                            .centerInside()
                            .into(view2);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle any errors
                }
            });

            storageReference.child("caseta/"+ja6.getString(13))
                    .getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {

                @Override

                public void onSuccess(Uri uri) {
                    Picasso.with(PreEntradasActivity.this)
                            .load(uri)
                            .error(R.drawable.log)
                            .fit()
                            .centerInside()
                            .into(view3);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle any errors
                }
            });




            Placas.setText(Conf.getPlacas());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void Verificar() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(PreEntradasActivity.this);
        alertDialogBuilder.setTitle("Alerta");
        alertDialogBuilder
                .setMessage("¿ Desea registrar la entrada ?")
                .setPositiveButton("Ok",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        busqueda();
                    }
                }).create().show();
    }


    public void busqueda(){

        String URL = "https://sanmateoresidencial.mx/plataforma/casetaV2/controlador/WIFI_SANMATEO/vst_reg_2.php?bd_name="+Conf.getBd()+"&bd_user="+Conf.getBdUsu()+"&bd_pwd="+Conf.getBdCon();
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                if(response.equals("error")){

                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(PreEntradasActivity.this);
                    alertDialogBuilder.setTitle("Alerta");
                    alertDialogBuilder
                            .setMessage("No existe UP")
                            .setPositiveButton("Ok",new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {

                                }
                            }).create().show();

                }else {
                    response = response.replace("][",",");
                    try {
                        ja4 = new JSONArray(response);
                        registro();
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
                params.put("calle", Calle.getSelectedItem().toString());
                params.put("numero", Numero.getText().toString().trim());
                return params;
            }
        };
        requestQueue.add(stringRequest);
    }
    public void registro(){


        if(Placas.getText().toString().equals("") ){
            Toast.makeText(getApplicationContext(),"Campo de placas", Toast.LENGTH_SHORT).show();
        }else if(Placas.getText().toString().equals(" ") ){
            Toast.makeText(getApplicationContext(),"Campo de placas ", Toast.LENGTH_SHORT).show();
        }else if( Placas.getText().toString().equals("N/A") ){
            Toast.makeText(getApplicationContext(),"Campo de placas", Toast.LENGTH_SHORT).show();
        }else{

            String URL = "https://sanmateoresidencial.mx/plataforma/casetaV2/controlador/WIFI_SANMATEO/vst_reg_3.php?bd_name="+Conf.getBd()+"&bd_user="+Conf.getBdUsu()+"&bd_pwd="+Conf.getBdCon();
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {

                @Override
                public void onResponse(String response){
                    if(response.equals("error")){
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(PreEntradasActivity.this);
                        alertDialogBuilder.setTitle("Alerta");
                        alertDialogBuilder
                                .setMessage("Visita No Exitosa")
                                .setPositiveButton("Ok",new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        Toast.makeText(getApplicationContext(),"Visita No Registrada", Toast.LENGTH_SHORT).show();
                                        Intent i = new Intent(getApplicationContext(), EscaneoVisitaActivity.class);
                                        startActivity(i);
                                        finish();
                                    }
                                }).create().show();
                    }else {

                            if(fotos1==1){
                                upload1();
                            }
                            if(fotos2==1){
                                upload2();
                            }
                            if(fotos3==1){
                                upload3();
                            }

                        Terminar();

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

                    try {
                        if(fotos1==1){
                            f1="app"+anio+mes+dia+Placas.getText().toString()+"-"+numero_aletorio;
                        }else{
                            f1=ja6.getString(11);
                        }
                        if(fotos2==1){
                            f2="app"+anio+mes+dia+Placas.getText().toString()+"-"+numero_aletorio2;
                        }else{
                            f2=ja6.getString(12);
                        }
                        if(fotos3==1){
                            f3="app"+anio+mes+dia+Placas.getText().toString()+"-"+numero_aletorio3;
                        }else{
                            f3=ja6.getString(13);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    if(visi.isChecked()){
                        valor="1";
                    }else if(taxi.isChecked()){
                        valor="2";
                    }else if(prove.isChecked()) {
                        valor="3";
                    }


                    Map<String, String> params = new HashMap<>();
                    try {

                        params.put("id_residencial", Conf.getResid().trim());
                        params.put("id_usuario", ja4.getString(0));
                        params.put("id_tipo",valor);
                        params.put("nombre", Nombre.getText().toString().trim());


                        params.put("placas", Placas.getText().toString().trim());
                        params.put("pasajeros", Pasajeros.getSelectedItem().toString());
                        params.put("guardia_de_entrada", Conf.getUsu().trim());

                        params.put("foto1", f1);
                        params.put("foto2", f2);
                        params.put("foto3", f3);

                        params.put("usuario",ja4.getString(1).trim() + " " + ja4.getString(2).trim() + " " + ja4.getString(3).trim());
                        params.put("token", ja4.getString(5).trim());
                        params.put("correo",ja4.getString(4).trim());



                    } catch (JSONException e) {
                        Log.e("TAG","Error: " + e.toString());
                    }
                    return params;
                }
            };
            requestQueue.add(stringRequest);
        }
    }

    public void upload1(){

        StorageReference mountainImagesRef = null;
        mountainImagesRef = storageReference.child("caseta/app"+anio+mes+dia+Placas.getText().toString()+"-"+numero_aletorio);

        UploadTask uploadTask = mountainImagesRef.putFile(uri_img);


        // Listen for state changes, errors, and completion of the upload.
        uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                pd.show(); // double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                //System.out.println("Upload is " + progress + "% done");
                // Toast.makeText(getApplicationContext(),"Cargando Imagen INE " + progress + "%", Toast.LENGTH_SHORT).show();

            }
        }).addOnPausedListener(new OnPausedListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onPaused(UploadTask.TaskSnapshot taskSnapshot) {
                //Toast.makeText(AccesoActivity.this,"Pausado",Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(PreEntradasActivity.this,"Fallado", Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                pd.dismiss();
                //upload2();

            }
        });
    }



    public void upload2(){

        StorageReference mountainImagesRef2 = null;
        mountainImagesRef2 = storageReference.child("caseta/app"+anio+mes+dia+Placas.getText().toString()+"-"+numero_aletorio2);

        UploadTask uploadTask = mountainImagesRef2.putFile(uri_img2);


        // Listen for state changes, errors, and completion of the upload.
        uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                pd2.show(); // double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                //System.out.println("Upload is " + progress + "% done");
                // Toast.makeText(getApplicationContext(),"Cargando Imagen INE " + progress + "%", Toast.LENGTH_SHORT).show();

            }
        }).addOnPausedListener(new OnPausedListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onPaused(UploadTask.TaskSnapshot taskSnapshot) {
                //Toast.makeText(AccesoActivity.this,"Pausado",Toast.LENGTH_SHORT).show();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {

                Toast.makeText(PreEntradasActivity.this,"Fallado", Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                pd2.dismiss();

            }
        });
    }



    public void upload3(){

        StorageReference mountainImagesRef3 = null;
        mountainImagesRef3 = storageReference.child("caseta/app"+anio+mes+dia+Placas.getText().toString()+"-"+numero_aletorio3);

        UploadTask uploadTask = mountainImagesRef3.putFile(uri_img3);

        // Listen for state changes, errors, and completion of the upload.
        uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                // double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                //System.out.println("Upload is " + progress + "% done");
                //Toast.makeText(getApplicationContext(),"Cargando Imagen PLACA " + progress + "%", Toast.LENGTH_SHORT).show();
                pd3.show();
            }
        }).addOnPausedListener(new OnPausedListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onPaused(UploadTask.TaskSnapshot taskSnapshot) {
                //Toast.makeText(AccesoActivity.this,"Pausado",Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(PreEntradasActivity.this,"Fallado", Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                pd3.dismiss();
            }
        });
    }

    public void Terminar() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(PreEntradasActivity.this);
        alertDialogBuilder.setTitle("Alerta");
        alertDialogBuilder
                .setMessage("Entrada de Visita Exitosa")
                .setPositiveButton("Ok",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent i = new Intent(getApplicationContext(), Dashboard.class);
                        startActivity(i);
                        finish();
                    }
                }).create().show();
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(), Dashboard.class);
        startActivity(intent);
        finish();
    }


}