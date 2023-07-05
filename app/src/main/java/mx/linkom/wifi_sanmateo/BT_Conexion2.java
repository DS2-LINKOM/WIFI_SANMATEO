package mx.linkom.wifi_sanmateo;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.TextView;

import java.util.Set;

public class BT_Conexion2 extends Menu {

    GridView IdLista;
    Configuracion Conf;

    //DEPURACION DE LOGCAT
    private static final  String TAG="DispositivosVinculados";
    //STRING ENVIARA ALA ACTIVIDAD PRINCIPAL DATOSBTACTIVIY
    public static   String EXTRA_DEVICE_ADDRESS="device_address";

    //DECLARACIÓN DE CAMPOS
    private BluetoothAdapter mBtAdapter;
    private ArrayAdapter mPairedDevicesArrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cbta);
        Conf = new Configuracion(this);

        Global.ocultarBarrasNavegacionEstado(this);
        Global.aumentarVolumen(this);
        Global.evitarSuspenderPantalla(this);

    }

    @Override
    public void onResume(){
        super.onResume();

        //------------------
        verificarEstadoBT();
        //INCIARLIZA LA ARRAY QUE CONTENDRA LA LISTA DE DISPOSITIVOS VINCULADP
        mPairedDevicesArrayAdapter=new ArrayAdapter(this, R.layout.device_name);
        //PRESENTAMOS LA LISTA
        IdLista = (GridView) findViewById(R.id.IdLista);
        IdLista.setAdapter(mPairedDevicesArrayAdapter);
        //IdLista.setOnClickListener((View.OnClickListener) mPairedDevicesArrayAdapter);
        IdLista.setOnItemClickListener(mDeviceClickListener);
        //IdLista.setOnItemClickListener(mDeviceClickListener);

        //OBTIENE EL ADAPTADOR LOCAL BT
        mBtAdapter= BluetoothAdapter.getDefaultAdapter();

        Set<BluetoothDevice> pairedDevices= mBtAdapter.getBondedDevices();
        //ADICIONA UN DISPOSITIVOS EMPAREJADOS
        if(pairedDevices.size()>0){
            for (BluetoothDevice device:pairedDevices){
                mPairedDevicesArrayAdapter.add(device.getName()+"\n"+device.getAddress());
            }
        }
        //---------------------
    }

    // CONFIGURA CLICK LITA
    private AdapterView.OnItemClickListener mDeviceClickListener = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3) {
            //OBTENER LA DIRECICÓN MAC DEL DISPOSITIVO SON 17CARACTERES
            String info = ((TextView) v).getText().toString();
            String address = info.substring(info.length() - 17);

            Conf.setMAC(address);

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(BT_Conexion2.this);
            alertDialogBuilder.setTitle("Alerta");
            alertDialogBuilder
                    .setMessage("Conexión Exitosa")
                    .setPositiveButton("Ok",new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Intent i = new Intent(getApplicationContext(), DashboardEntradas.class);
                            startActivity(i);
                            finish();
                        }
                    }).create().show();

          //  finishAffinity();
            // REALIZA UN ITEM
            //TOMA UN EXTRA_DEVICE_ADDRESS que es la dirección MAC

           // Intent intent = new Intent(BT_Conexion_Activity.this, DatosBTAcitivity.class);
           // intent.putExtra(EXTRA_DEVICE_ADDRESS, address);
           // startActivity(intent);




        }
    };

    private void verificarEstadoBT() {
        // COMPRUEBA QUE EL DISPOSITVO TIENE BT Y QUE ESTA CONECTADO
        mBtAdapter=BluetoothAdapter.getDefaultAdapter(); // CHECK THIS OUT THAT IT WORKS!!!
        if(mBtAdapter==null) {

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(BT_Conexion2.this);
            alertDialogBuilder.setTitle("Alerta");
            alertDialogBuilder
                    .setMessage("El dispositivo no soporta Bluetooth")
                    .setPositiveButton("Ok",new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Intent i = new Intent(getApplicationContext(), DashboardEntradas.class);
                            startActivity(i);
                            finish();
                        }
                    }).create().show();

            //Toast.makeText(getBaseContext(), "El dispositivo no soporta Bluetooth", Toast.LENGTH_SHORT).show();
        } else {
            if (mBtAdapter.isEnabled()) {
                Log.d(TAG, "...Bluetooth Activado...");
            } else {
                //SOLICITA AL USUARIO QUE ACTIVE BT
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, 1);

            }
        }
    }
    @Override
    public void onBackPressed(){
        super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(), DashboardEntradas.class);
        startActivity(intent);
        finish();
    }
}
