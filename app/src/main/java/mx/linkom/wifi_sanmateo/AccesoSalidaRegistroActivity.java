package mx.linkom.wifi_sanmateo;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

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
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.HashMap;
import java.util.Map;

public class AccesoSalidaRegistroActivity extends mx.linkom.wifi_sanmateo.Menu2{
    Configuracion Conf;
    JSONArray ja1,ja2,ja3,ja4,ja5,ja6;
    TextView Nombre,Numero,Placas,Calle,Pasajeros,Tipo,Residente;
    Button Registrar;
    ImageView view1,view2,view3;
    TextView nombre_foto1,nombre_foto2,nombre_foto3;

    LinearLayout rlPermitido, rlDenegado,rlVista;
    LinearLayout Foto1, Foto2,Foto3,Foto1View,Foto2View,Foto3View,espacio2,espacio3,espacio4,espacio5,espacio6,espacio8,espacio9,espacio10;
    TextView  tvMensaje;
    String Tipos;
    FirebaseStorage storage;
    StorageReference storageReference;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accesossalidaregistro);

        storage= FirebaseStorage.getInstance();
        storageReference=storage.getReference();

        Foto1 = (LinearLayout) findViewById(R.id.Foto1);
        Foto2 = (LinearLayout) findViewById(R.id.Foto2);
        Foto3 = (LinearLayout) findViewById(R.id.Foto3);
        Foto1View = (LinearLayout) findViewById(R.id.Foto1View);
        Foto2View = (LinearLayout) findViewById(R.id.Foto2View);
        Foto3View = (LinearLayout) findViewById(R.id.Foto3View);
        espacio2 = (LinearLayout) findViewById(R.id.espacio2);
        espacio3 = (LinearLayout) findViewById(R.id.espacio3);
        espacio4 = (LinearLayout) findViewById(R.id.espacio4);
        espacio5 = (LinearLayout) findViewById(R.id.espacio5);
        espacio6 = (LinearLayout) findViewById(R.id.espacio6);
        espacio8 = (LinearLayout) findViewById(R.id.espacio8);
        espacio9 = (LinearLayout) findViewById(R.id.espacio9);
        espacio10 = (LinearLayout) findViewById(R.id.espacio10);
        rlVista = (LinearLayout) findViewById(R.id.rlVista);
        rlPermitido = (LinearLayout) findViewById(R.id.rlPermitido);
        rlDenegado = (LinearLayout) findViewById(R.id.rlDenegado);
        tvMensaje = (TextView)findViewById(R.id.setMensaje);

        Registrar = (Button) findViewById(R.id.Registrar);

        nombre_foto1 = (TextView) findViewById(R.id.nombre_foto1);
        nombre_foto2 = (TextView) findViewById(R.id.nombre_foto2);
        nombre_foto3 = (TextView) findViewById(R.id.nombre_foto3);

        view1 = (ImageView) findViewById(R.id.view1);
        view2 = (ImageView) findViewById(R.id.view2);
        view3 = (ImageView) findViewById(R.id.view3);

        Conf = new Configuracion(this);
        Residente = (TextView)findViewById(R.id.setResi);
        Nombre = (TextView)findViewById(R.id.setNombre);
        Numero = (TextView)findViewById(R.id.setNumero);
        Placas = (TextView)findViewById(R.id.setPlacas);
        Calle = (TextView)findViewById(R.id.setCalle);
        Pasajeros = (TextView)findViewById(R.id.setPasajeros);
        Placas = (TextView)findViewById(R.id.setPlacas);
        Tipo = (TextView)findViewById(R.id.setTipo);

        Registrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Verificar();
            }
        });

            menu();


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
                        ja5 = new JSONArray(response);
                        submenu(ja5.getString(0));
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
                        ja6 = new JSONArray($arreglo);
                        Visita();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }else {
                    response = response.replace("][", ",");
                    if (response.length() > 0) {
                        try {
                            ja6 = new JSONArray(response);
                            Visita();
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
    public void Visita(){

        String URL = "https://sanmateoresidencial.mx/plataforma/casetaV2/controlador/WIFI_SANMATEO/vst_reg_5.php?bd_name="+Conf.getBd()+"&bd_user="+Conf.getBdUsu()+"&bd_pwd="+Conf.getBdCon();
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
                params.put("id", Conf.getQR());
                params.put("id_residencial", Conf.getResid().trim());
                return params;
            }
        };
        requestQueue.add(stringRequest);
    }



    public void Usuario(final String IdUsu){ //DATOS USUARIO

        String URL = "https://sanmateoresidencial.mx/plataforma/casetaV2/controlador/WIFI_SANMATEO/vst_php2.php?bd_name="+Conf.getBd()+"&bd_user="+Conf.getBdUsu()+"&bd_pwd="+Conf.getBdCon();
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
        String URLResidencial = "https://sanmateoresidencial.mx/plataforma/casetaV2/controlador/WIFI_SANMATEO/vst_reg_6.php?bd_name="+Conf.getBd()+"&bd_user="+Conf.getBdUsu()+"&bd_pwd="+Conf.getBdCon();
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
        String URLResidencial = "https://sanmateoresidencial.mx/plataforma/casetaV2/controlador/WIFI_SANMATEO/vst_php6.php?bd_name="+Conf.getBd()+"&bd_user="+Conf.getBdUsu()+"&bd_pwd="+Conf.getBdCon();
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLResidencial, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    if (response.trim().equals("error")){

                        String $arreglo[]={"0","0","0"};
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
        tvMensaje.setText(" No tiene asignada una unidad privativa.");

    }


    public void ValidarQR(){



        try {


                if(ja4.getString(2).equals("0")){ //NUEVO
                    rlVista.setVisibility(View.GONE);
                    rlPermitido.setVisibility(View.GONE);
                    rlDenegado.setVisibility(View.VISIBLE);
                    tvMensaje.setText("No estas dentro del complejo");

                }else if(ja4.getString(2).equals("1")){ //ENTRO Y QUIERE SALIR
                    rlVista.setVisibility(View.GONE);
                    rlPermitido.setVisibility(View.VISIBLE);
                    rlDenegado.setVisibility(View.GONE);

                    Residente.setText(ja2.getString(1)+" "+ja2.getString(2)+" "+ja2.getString(3));
                    Nombre.setText(ja1.getString(7));

                    if(ja1.getString(4).equals("1") || ja1.getString(4).equals("0")){
                        Tipo.setText("Visita");
                    }else if(ja1.getString(4).equals("2")){
                        Tipo.setText("Taxista");
                    }else if(ja1.getString(4).equals("3")){
                        Tipo.setText("Proveedor / Servicios");
                    }
                    Calle.setText(ja3.getString(0));
                    Numero.setText(ja3.getString(1));

                    Pasajeros.setText(ja4.getString(6));
                    Placas.setText(ja4.getString(7));

                    if(ja4.getString(3).equals("")){
                        Foto1.setVisibility(View.GONE);
                        espacio2.setVisibility(View.GONE);
                        Foto1View.setVisibility(View.GONE);
                        espacio3.setVisibility(View.GONE);

                    }else{

                        nombre_foto1.setText(ja6.getString(4)+":");

                        storageReference.child("caseta/"+ja4.getString(3))
                                .getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {

                            @Override

                            public void onSuccess(Uri uri) {
                                Picasso.with(AccesoSalidaRegistroActivity.this)
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
                    }

                    if(ja4.getString(4).equals("")){
                        Foto2.setVisibility(View.GONE);
                        espacio5.setVisibility(View.GONE);
                        Foto2View.setVisibility(View.GONE);
                        espacio6.setVisibility(View.GONE);
                    }else{
                        nombre_foto2.setText(ja6.getString(6)+":");

                        storageReference.child("caseta/"+ja4.getString(4))
                                .getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {

                            @Override

                            public void onSuccess(Uri uri) {
                                Picasso.with(AccesoSalidaRegistroActivity.this)
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
                    }

                    if(ja4.getString(5).equals("")){
                        Foto3.setVisibility(View.GONE);
                        espacio8.setVisibility(View.GONE);
                        Foto3View.setVisibility(View.GONE);
                        espacio9.setVisibility(View.GONE);
                    }else{
                        nombre_foto3.setText(ja6.getString(8)+":");

                        storageReference.child("caseta/"+ja4.getString(5))
                                .getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {

                            @Override

                            public void onSuccess(Uri uri) {
                                Picasso.with(AccesoSalidaRegistroActivity.this)
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
                    }
                }else if(ja4.getString(2).equals("2")) { //ENTRO Y QUIERE SALIR
                    rlVista.setVisibility(View.GONE);
                    rlPermitido.setVisibility(View.GONE);
                    rlDenegado.setVisibility(View.VISIBLE);
                    tvMensaje.setText("No estas dentro del complejo");
                 }
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    public void Verificar(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(AccesoSalidaRegistroActivity.this);
        alertDialogBuilder.setTitle("Alerta");
        alertDialogBuilder
                .setMessage("¿ Desea registrar la salida ?")
                .setPositiveButton("Ok",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        registrar();
                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        Intent i = new Intent(getApplicationContext(), Dashboard.class);
                        startActivity(i);
                        finish();
                    }
                }).create().show();

    }


    public void registrar (){
        String URL = "https://sanmateoresidencial.mx/plataforma/casetaV2/controlador/WIFI_SANMATEO/vst_reg_7.php?bd_name="+Conf.getBd()+"&bd_user="+Conf.getBdUsu()+"&bd_pwd="+Conf.getBdCon();
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                if(response.equals("succes")){

                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(AccesoSalidaRegistroActivity.this);
                    alertDialogBuilder.setTitle("Alerta");
                    alertDialogBuilder
                            .setMessage("Salida de Visita Exitosa")
                            .setPositiveButton("Ok",new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    Intent i = new Intent(getApplicationContext(), Dashboard.class);
                                    startActivity(i);
                                    finish();
                                }
                            }).create().show();


                }else {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(AccesoSalidaRegistroActivity.this);
                    alertDialogBuilder.setTitle("Alerta");
                    alertDialogBuilder
                            .setMessage("Salida de Visita No Exitosa")
                            .setPositiveButton("Ok",new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    Intent i = new Intent(getApplicationContext(), Dashboard.class);
                                    startActivity(i);
                                    finish();
                                }
                            }).create().show();
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
                    params.put("id", ja4.getString(0).trim());
                    params.put("id_visita", ja1.getString(0).trim());
                    params.put("guardia_de_salida", Conf.getUsu().trim());
                    params.put("id_residencial", Conf.getResid().trim());

                    params.put("usuario",ja2.getString(1).trim() + " " + ja2.getString(2).trim() + " " + ja2.getString(3).trim());
                    params.put("token", ja2.getString(5).trim());
                    params.put("correo",ja2.getString(6).trim());
                    params.put("visita",ja1.getString(7).trim());

                } catch (JSONException e) {
                    Log.e("TAG","Error: " + e.toString());
                }
                return params;
            }
        };
        requestQueue.add(stringRequest);


    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(), Dashboard.class);
        startActivity(intent);
        finish();
    }

}
