package mx.linkom.wifi_sanmateo;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Set;

public class BT_Conexion extends Menu {

    GridView IdLista;

    //DEPURACION DE LOGCAT
    private static final  String TAG="DsipositivosVinculados";
    //STRING ENVIARA ALA ACTIVIDAD PRINCIPAL DATOSBTACTIVIY
    public static String EXTRA_DEVICE_ADDRESS="device_address";

    //DECLARACIÓN DE CAMPOS
    private BluetoothAdapter mBtAdapter;
    private ArrayAdapter mPairedDevicesArrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cbta);

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

            finishAffinity();
            // REALIZA UN ITEM
            //TOMA UN EXTRA_DEVICE_ADDRESS que es la dirección MAC
            Intent intent = new Intent(BT_Conexion.this, BT_Datos.class);
            intent.putExtra(EXTRA_DEVICE_ADDRESS, address);
            startActivity(intent);
        }
    };

    private void verificarEstadoBT() {
        // COMPRUEBA QUE EL DISPOSITVO TIENE BT Y QUE ESTA CONECTADO
        mBtAdapter=BluetoothAdapter.getDefaultAdapter(); // CHECK THIS OUT THAT IT WORKS!!!
        if(mBtAdapter==null) {
            Toast.makeText(getBaseContext(), "El dispositivo no soporta Bluetooth", Toast.LENGTH_SHORT).show();
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
        Toast.makeText(getBaseContext(), "Regresa desde el menú", Toast.LENGTH_SHORT).show();

    }
}
