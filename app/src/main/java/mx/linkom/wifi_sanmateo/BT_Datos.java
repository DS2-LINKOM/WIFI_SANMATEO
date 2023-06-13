package mx.linkom.wifi_sanmateo;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class BT_Datos extends Menu {

    EditText edtTextoOut;
    Button btnEnviar,btnAdelante,btnReversa,btnIzquierda,btnDerecha,btnStop,btnDesconectar;
    TextView tvtMensaje;

    Handler bluetoothIn;
    final int handlerState = 0;
    private BluetoothAdapter btAdapter = null;
    private BluetoothSocket btSocket = null;
    private StringBuilder DataStringIn = new StringBuilder();
    private ConnectedThread MyConexionBT;

    //IDENTIFICADOR  UNICO DE SERVICIO-SPP UUID
    private static  UUID BTMODULEUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    // String para la dirección MAC
    public static String address = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_datosbt);


        bluetoothIn= new Handler(){
            public void handleMessage(android.os.Message msg){
                if(msg.what == handlerState){
                    char MyCaracter= (char) msg.obj;

                    if(MyCaracter == 'a'){
                        tvtMensaje.setText("ACELERANDO");
                    }

                    if(MyCaracter == 'i'){
                        tvtMensaje.setText("GIRO A LA IZQUIERDA");
                    }

                    if(MyCaracter == 'd'){
                        tvtMensaje.setText("GIRO A LA DERCHA");
                    }

                    if(MyCaracter == 'r'){
                        tvtMensaje.setText("RETROCEDIENDO");
                    }

                    if(MyCaracter == 's'){
                        tvtMensaje.setText("DETENIDO");
                    }
                }
            }
        };

        btAdapter= BluetoothAdapter.getDefaultAdapter();
        VericaEstadoBT();

        edtTextoOut = (EditText) findViewById(R.id.edtTextoOut);
        btnEnviar = (Button) findViewById(R.id.btnEnviar);
        btnAdelante = (Button) findViewById(R.id.btnAdelante);
        btnReversa = (Button) findViewById(R.id.btnReversa);
        btnIzquierda = (Button) findViewById(R.id.btnIzquierda);
        btnDerecha = (Button) findViewById(R.id.btnDerecha);
        btnStop = (Button) findViewById(R.id.btnStop);
        btnDesconectar = (Button) findViewById(R.id.btnDesconectar);
        tvtMensaje = (TextView) findViewById(R.id.tvtMensaje);

        btnEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    String GetData= edtTextoOut.getText().toString();
                    //tvtMensaje.setText(GetData);

                    MyConexionBT.write(GetData);
            }});

        btnAdelante.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyConexionBT.write("A");
            }});

        btnReversa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyConexionBT.write("R");
            }});

        btnIzquierda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyConexionBT.write("I");
            }});

        btnDerecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyConexionBT.write("D");
            }});


        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyConexionBT.write("S");
            }});

        btnDesconectar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               if(btSocket!=null){
                   try {
                     btSocket.close();
                   }catch (IOException e){
                       Toast.makeText(getBaseContext(),"Error",Toast.LENGTH_LONG).show();;
                   }
                   finish();
               }
            }});


    }

    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {
        //CREA UNA CONEXIÓN SEGURA DE SALIDA PARA EL DISPOSITIVO USANDO EL SERVICIO UUID
        return  device.createRfcommSocketToServiceRecord(BTMODULEUUID);
    }

    @Override
    public void onResume() {
        super.onResume();

        Intent intent = getIntent();
        address = intent.getStringExtra(BT_Conexion.EXTRA_DEVICE_ADDRESS);

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

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(), BT_Conexion.class);
        startActivity(intent);
        finish();
    }
}
