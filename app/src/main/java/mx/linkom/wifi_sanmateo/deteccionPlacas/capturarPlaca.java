package mx.linkom.wifi_sanmateo.deteccionPlacas;

import android.app.ActivityManager;
import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.IBinder;
import android.util.Base64;
import android.util.Log;
import android.util.SparseArray;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import mx.linkom.wifi_sanmateo.Configuracion;
import mx.linkom.wifi_sanmateo.Global;
import mx.linkom.wifi_sanmateo.fotosSegundoPlano.UrisContentProvider;
import mx.linkom.wifi_sanmateo.fotosSegundoPlano.subirFotos;

public class capturarPlaca extends Service {

    private objectDetectorClass objectDetectorClass;
    ArrayList<String> id;

    NotificationManagerCompat notificationManager;
    NotificationCompat.Builder builder;
    String numero_aletorio, rutaImagen, nombreFoto = "";
    Configuracion Conf;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        new Thread(
                new Runnable() {
                    @Override
                    public void run() {


                        try {
                            objectDetectorClass = new objectDetectorClass(getAssets(), "detectPLacaLKM.tflite", "labelmapTf.txt", 320);
                            Log.e("capturarPlaca", "Modelo cargado correctamente");

                            Conf = new Configuracion(capturarPlaca.this);

                            id = (ArrayList<String>) intent.getExtras().getSerializable("id");

                            Log.e("IdUser", id.get(0));
                            Log.e("NipUser", Conf.getPin().trim());
                            Log.e("idResUser", Conf.getResid().trim());

                            fotoPlaca();

                        } catch (IOException e) {
                            Log.e("capturarPlaca", "Error al cargar modelo");
                            stopSelf();
                            onDestroy();
                        }
                    }
                }
        ).start();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Log.e("capturarPlaca", "Se destruyo el servicio");
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void fotoPlaca() {

        String imageUrl = "https://sanmateoresidencial.mx/plataforma/casetaV2/controlador/CC/capturaplaca/capturaimagen.php";
        String IP = "189.180.60.108";

        //String IP = Global.getIpCamaraPlacas();

        if (!IP.isEmpty()) {
            Log.e("IPMIG", "En servicio: " + Global.getIpCamaraPlacas().trim());

            RequestQueue queue = Volley.newRequestQueue(this);
            StringRequest stringRequest = new StringRequest(Request.Method.POST, imageUrl,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            byte[] imageBytes = Base64.decode(response, Base64.DEFAULT);
                            Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);

                            Log.e("FOTO", "Si obtuve la imagen");


                            try {
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
                                numero_aletorio = prime + segundo[numRandonsegun] + tercer + cuarto[numRandoncuart];

                                Bitmap foto = bitmap;
                                File directory = getExternalFilesDir(null);
                                nombreFoto = "appPL" + numero_aletorio + "-" + id.get(0) + ".png";
                                File file = new File(directory, nombreFoto);
                                rutaImagen = file.getAbsolutePath();
                                FileOutputStream outputStream = new FileOutputStream(file);
                                foto.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                                outputStream.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            ContentValues val_img1 = ValuesImagen(nombreFoto, Conf.getPin().trim() + "/caseta/" + nombreFoto.trim(), rutaImagen);
                            Uri uri = getContentResolver().insert(UrisContentProvider.URI_CONTENIDO_FOTOS_OFFLINE, val_img1);

                            //Solo ejecutar si el servicio no se esta ejecutando
                            if (!servicioFotos()) {
                                Intent cargarFotos = new Intent(capturarPlaca.this, subirFotos.class);
                                startService(cargarFotos);
                            }

                            reconocerPlaca(bitmap);
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // Handle error
                            Log.e("errorPost", error.toString());
                            stopSelf();
                            onDestroy();
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("ip", IP);
                    //params.put("ip", Global.getIpCamaraPlacas().trim());
                    return params;
                }
            };
            queue.add(stringRequest);
        }else {
            stopSelf();
            onDestroy();
        }




        /*Log.e("FOTO", "Método obtenerImagen");
        String imageUrl = "https://sanmateoresidencial.mx/plataforma/casetaV2/controlador/CC/capturaplaca/capturaimagen.php";
        RequestQueue queue = Volley.newRequestQueue(this);

        ImageRequest imageRequest = new ImageRequest(imageUrl,
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap bitmap) {
                        // Do something with the loaded bitmap
                        Log.e("FOTO", "Si obtuve la imagen");


                        try {
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
                            numero_aletorio = prime + segundo[numRandonsegun] + tercer + cuarto[numRandoncuart];

                            Bitmap foto = bitmap;
                            File directory = getExternalFilesDir(null);
                            nombreFoto = "appPL"+numero_aletorio+"-"+id.get(0)+".png";
                            File file = new File(directory, nombreFoto);
                            rutaImagen = file.getAbsolutePath();
                            FileOutputStream outputStream = new FileOutputStream(file);
                            foto.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                            outputStream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        ContentValues val_img1 = ValuesImagen(nombreFoto, Conf.getPin().trim() + "/caseta/" + nombreFoto.trim(), rutaImagen);
                        Uri uri = getContentResolver().insert(UrisContentProvider.URI_CONTENIDO_FOTOS_OFFLINE, val_img1);

                        //Solo ejecutar si el servicio no se esta ejecutando
                        if (!servicioFotos()) {
                            Intent cargarFotos = new Intent(capturarPlaca.this, subirFotos.class);
                            startService(cargarFotos);
                        }

                        reconocerPlaca(bitmap);
                    }
                },
                0,
                0,
                ImageView.ScaleType.CENTER,
                Bitmap.Config.RGB_565,
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle error
                        Log.e("errorPlaca", error.toString());
                        onDestroy();

                    }
                });
        queue.add(imageRequest);*/
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

    public void reconocerPlaca(Bitmap bitmap) {
        //Convertir bitmap image a Mat image
        //CV_8UC4: RGBA image
        //CV_8UC1: Grayscale image
        Mat selected_image = new Mat(bitmap.getHeight(), bitmap.getWidth(), CvType.CV_8UC4);
        Utils.bitmapToMat(bitmap, selected_image);

        //Pasar la imagen al metodo para reeconocer placa
        selected_image = objectDetectorClass.recognizePhoto(selected_image);
        //Convertir mat image a bitmap
        Bitmap bitmap1 = null;
        bitmap1 = Bitmap.createBitmap(selected_image.cols(), selected_image.rows(), Bitmap.Config.ARGB_8888);

        Utils.matToBitmap(selected_image, bitmap1);

        getTextFromImage(bitmap1);
    }

    private void getTextFromImage(Bitmap bitmap) {
        TextRecognizer recognizer = new TextRecognizer.Builder(this).build();
        if (!recognizer.isOperational()) {
            Log.e("capturarPlaca", "Ocurrio un error al obtener texto de imagen");
            stopSelf();
            onDestroy();
        } else {
            Frame frame = new Frame.Builder().setBitmap(bitmap).build();
            SparseArray<TextBlock> textBlockSparseArray = recognizer.detect(frame);
            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < textBlockSparseArray.size(); i++) {
                TextBlock textBlock = textBlockSparseArray.valueAt(i);
                stringBuilder.append(textBlock.getValue());
                stringBuilder.append("\n");
            }

            Log.e("PlacaCompleta", "" + stringBuilder.toString());

            String placa = getLicensePlate(stringBuilder.toString());

            Log.e("Placa", "" + placa);

            String txtPlaca = "";

            for (char c : placa.toCharArray()) {
                if (!esGuion(c + "")) {
                    txtPlaca += c;
                }
            }

            Log.e("Placa2", "" + txtPlaca);

            ActualizarRegistro(txtPlaca);
        }
    }


    //------------------------OBTENER SOLO LA PLACA-------------------------------------------------


    //-------------------------------PATRONES DE UNA PLACA------------------------------------------
    //      GK-5631-C
    //      LE-99-914
    //      ULS-914-G
    //      A-479-TGG
    //      Y35-APX
    //      96TUK6
    //      650-ZUX
    //      NVM-41-48
    //      U03-BAF
    //      06-HB-2B
    //
    //----------------------------------------------------------------------------------------------

    public static String getLicensePlate(String input) {
        String licensePlate = "";
        String[] words = input.split("\\s+|,\\s*");
        for (String word : words) {
            if (word.length() >= 5) {
                try {
                    if ((isNumbers(word.substring(0, 2)) && isUppercaseLetters(word.substring(2, 5))) //96TUK6
                            || (isUppercaseLetters(word.substring(0, 3)) && isNumbers(word.substring(3, 4)) && isUppercaseLetters(word.substring(4, 5))) //EMF5S
                            || (isNumbers(word.substring(0, 4)) && isUppercaseLetters(word.substring(4, 5)) && isNumbers(word.substring(5, 6))) //6537E7
                            || (isNumbers(word.substring(0, 2)) && esGuion(word.substring(2, 3)) && isUppercaseLetters(word.substring(3, 4))) //650-ZUX
                            || (isNumbers(word.substring(0, 2)) && esGuion(word.substring(2, 3)) && isUppercaseLetters(word.substring(3, 5))) //650-ZUX
                            || (isUppercaseLetters(word.substring(0, 1)) && isNumbers(word.substring(1, 2)) && isUppercaseLetters(word.substring(2, 5))) //S3ERS
                            || (isNumbers(word.substring(0, 3)) && esGuion(word.substring(3, 4)) && isNumbers(word.substring(4, 5)) && isUppercaseLetters(word.substring(5, 6))) //968-7PX
                            || (isUppercaseLetters(word.substring(0, 2)) && esGuion(word.substring(2, 3)) && isNumbers(word.substring(3, 5))) //LE-99-914
                            || (isUppercaseLetters(word.substring(0, 1)) && esGuion(word.substring(1, 2)) && isNumbers(word.substring(2, 5))) //A-479-TGG
                            || (isUppercaseLetters(word.substring(0, 1)) && esGuion(word.substring(1, 2)) && isNumbers(word.substring(2, 5))) //A-479-TGG
                            || (isUppercaseLetters(word.substring(0, 3)) && esGuion(word.substring(3, 4)) && isNumbers(word.substring(4, 6))) //NVM-41-48
                            || (isUppercaseLetters(word.substring(0, 3)) && esGuion(word.substring(3, 4)) && isNumbers(word.substring(4, 7))) //ULS-914-G
                            || (isUppercaseLetters(word.substring(0, 1)) && isNumbers(word.substring(1, 3)) && esGuion(word.substring(3, 4)) && isUppercaseLetters(word.substring(4, 7))) //Y35-APX
                            || (isUppercaseLetters(word.substring(0, 2)) && esGuion(word.substring(2, 3)) && isNumbers(word.substring(3, 7))) //GK-5631-C
                            || (isNumbers(word.substring(0, 3)) && esGuion(word.substring(3, 4)) && isNumbers(word.substring(4, 6))) //650-ZUX
                            || (isNumbers(word.substring(0, 3)) && esGuion(word.substring(3, 4)) && isUppercaseLetters(word.substring(4, 7))) //650-ZUX
                    ) {

                        licensePlate = word;
                        break;
                    }
                } catch (Exception e) {
                    Log.e("getLicensePlate", e.toString());
                }
            }

        }
        return licensePlate;
    }

    private static boolean esGuion(String input) {
        boolean car;
        if (input.equals("-")) {
            car = true;
        } else {
            car = false;
        }

        return car;
    }

    private static boolean isUppercaseLetters(String input) {
        for (char c : input.toCharArray()) {
            if (!Character.isUpperCase(c)) {
                return false;
            }
        }
        return true;
    }

    private static boolean isNumbers(String input) {
        for (char c : input.toCharArray()) {
            if (!Character.isDigit(c)) {
                return false;
            }
        }
        return true;
    }

    public void ActualizarRegistro(String placa) {

        String URL = "https://sanmateoresidencial.mx/plataforma/casetaV2/controlador/WIFI_SM/placas1.php?bd_name=" + Conf.getBd() + "&bd_user=" + Conf.getBdUsu() + "&bd_pwd=" + Conf.getBdCon();
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                if (response.equals("error")) {
                    Log.e("capturarPlaca", "No se actualizo el registro");
                    stopSelf();
                    onDestroy();
                } else {
                    Log.e("capturarPlaca", "Se actualizo el registro");
                    stopSelf();
                    onDestroy();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("TAG", "Error: " + error.toString());
                stopSelf();
                onDestroy();            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> params = new HashMap<>();
                params.put("id_residencial", Conf.getResid());
                params.put("id_visita", id.get(0));
                params.put("fotoPlaca", nombreFoto);
                params.put("txtPlaca", placa);

                return params;
            }
        };
        requestQueue.add(stringRequest);
    }
}