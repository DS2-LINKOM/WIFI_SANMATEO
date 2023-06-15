package mx.linkom.wifi_sanmateo.fotosSegundoPlano;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.ArrayList;

import mx.linkom.wifi_sanmateo.R;

public class subirFotos extends Service {
    NotificationManagerCompat notificationManager;
    NotificationCompat.Builder builder;

    FirebaseStorage storage;
    StorageReference storageReference;
    ArrayList<String> nombres;
    ArrayList<String> rutasFirebase;
    ArrayList<String> rutasDispositivo;

    int i;

    int PROGRESS_MAX = 100;
    int PROGRESS_CURRENT = 0;

    int promedio;
    String TAG = "SERVICIO_FOTOS";


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        storage = FirebaseStorage.getInstance();
                        storageReference = storage.getReference();

                        nombres = new ArrayList<String>();
                        rutasFirebase = new ArrayList<String>();
                        rutasDispositivo = new ArrayList<String>();

                        Log.e(TAG, "Servicio de fotos");

                        //Solo ejecutar si el servicio no se esta ejecutando

                        notificationManager = NotificationManagerCompat.from(getApplicationContext());
                        builder = new NotificationCompat.Builder(getApplicationContext(), "upload_photos_id");
                        builder.setContentTitle("Cargando...")
                                .setContentText("Subiendo imágenes del módulo caseta.")
                                .setSmallIcon(R.drawable.ic_subir)
                                .setPriority(NotificationCompat.PRIORITY_LOW);


                        builder.setProgress(PROGRESS_MAX, PROGRESS_CURRENT, false);
                        notificationManager.notify(10045, builder.build());

                        Cursor cursoFotos = null;


                        cursoFotos = getContentResolver().query(UrisContentProvider.URI_CONTENIDO_FOTOS_OFFLINE, null, null, null);

                        if (cursoFotos.moveToFirst()) {
                            do {
                                nombres.add(cursoFotos.getString(0));
                                rutasFirebase.add(cursoFotos.getString(1));
                                rutasDispositivo.add(cursoFotos.getString(2));

                            } while (cursoFotos.moveToNext());
                        }else {
                            onDestroy();
                        }

                        cursoFotos.close();

                        //Si los array son vacios terminar el servicio
                        if (nombres.isEmpty() || rutasDispositivo.isEmpty() || rutasFirebase.isEmpty()) {
                            stopSelf();
                            onDestroy();
                        }



                        promedio = (int) 100 / rutasDispositivo.size();

                        subirImagenes();
                    }
                }
        ).start();

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Log.e(TAG, "Se destruyo el servicio");
        super.onDestroy();
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void subirImagenes() {

        if (nombres.size() != 0 && rutasDispositivo.size() != 0) {
            if (i < rutasDispositivo.size()) {
                i++;
                PROGRESS_CURRENT += promedio;
                System.out.println("Progreso: " + PROGRESS_CURRENT);

                StorageReference ImageRef = storageReference.child(rutasFirebase.get(i - 1));
                Uri uri = Uri.fromFile(new File(rutasDispositivo.get(i - 1)));
                ImageRef.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        System.out.println("******************************************************************************************************************************************");
                        System.out.println("******************************************************************************************************************************************");
                        System.out.println("Imagen " + nombres.get(i - 1) + " subida a firebase");
                        builder.setContentTitle("Imagenes completadas " + i + " de " + rutasDispositivo.size());
                        builder.setProgress(PROGRESS_MAX, PROGRESS_CURRENT, false);
                        notificationManager.notify(10045, builder.build());
                        System.out.println("******************************************************************************************************************************************");
                        System.out.println("******************************************************************************************************************************************");

                        File path = new File(rutasDispositivo.get(i - 1));
                        path.delete();

                        int eliminar = getContentResolver().delete(UrisContentProvider.URI_CONTENIDO_FOTOS_OFFLINE, "titulo = " + "'" + nombres.get(i - 1) + "'", null);

                        System.out.println("Valor de eliminar en recibir fotos offline: " + eliminar);

                        //Si la imagen se subio con exito, volver a llamar el método
                        if (i == rutasDispositivo.size()) {
                            builder.setContentText("Carga completada")
                                    .setProgress(0, 0, false);
                            notificationManager.notify(10045, builder.build());

                            stopSelf();
                            onDestroy();
                        }
                        subirImagenes();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "error al cargar imagen");
                        stopSelf();
                        onDestroy();
                    }
                });

            } else {
                stopSelf();
                onDestroy();
            }
        } else {
            stopSelf();
            onDestroy();
        }


    }
}
