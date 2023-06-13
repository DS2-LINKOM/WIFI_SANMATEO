package mx.linkom.wifi_sanmateo;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;

import com.google.firebase.auth.FirebaseAuth;

import java.lang.reflect.Field;
import java.lang.reflect.Method;


public class Menu extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener, android.widget.PopupMenu.OnMenuItemClickListener {
    private static final String TAG ="TOKEN";
    private Configuracion Conf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_documentos);

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            /// Log.d("MIAPP", "Estás online");
        } else {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Menu.this);
            alertDialogBuilder.setTitle("Alerta");
            alertDialogBuilder
                    .setMessage("Conexión de Internet Inestable")
                    .setPositiveButton("Ok",new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                        }
                    }).create().show();
            //Log.d("MIAPP", "Estás offline");
        }



        Conf = new Configuracion(this);


    }

    public void showPopup (View v){
        android.widget.PopupMenu popup= new android.widget.PopupMenu(this,v);
        popup.setOnMenuItemClickListener(this);
        popup.inflate(R.menu.menu_main);


        try {
            Field[] fields = popup.getClass().getDeclaredFields();
            for (Field field : fields) {
                if ("mPopup".equals(field.getName())) {
                    field.setAccessible(true);
                    Object menuPopupHelper = field.get(popup);
                    Class<?> classPopupHelper = Class.forName(menuPopupHelper.getClass().getName());
                    Method setForceIcons = classPopupHelper.getMethod("setForceShowIcon", boolean.class);
                    setForceIcons.invoke(menuPopupHelper, true);

                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        popup.getMenu().findItem(R.id.Usuario).setTitle(Conf.getNombre());

        popup.show();
    }
    @Override
    public boolean onMenuItemClick(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.Inicio) {
            if (Conf.getTipoUsuario().equals("0")) {
                Intent i = new Intent(getApplication(), DashboardEntradas.class);
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

            return true;
        }else if (id == R.id.Cerrar) {
            Conf.setMAC("");
            FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
            firebaseAuth.signOut();
            startActivity(new Intent(getBaseContext(), MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP));
            finish();

            return true;
        }else if (id == R.id.Versión) {

            return true;
        }else if (id == R.id.Usuario) {

            return true;
//        }else  if (id == R.id.BT) {
//                Intent i = new Intent(getApplication(), BT_Conexion.class);
//                startActivity(i);
//                finish();
//            return true;

        }else  if (id == R.id.BT2) {
        Intent i = new Intent(getApplication(), BT_Conexion2.class);
        startActivity(i);
        finish();
        return true;
        }

        return super.onOptionsItemSelected(item);
    }




}
