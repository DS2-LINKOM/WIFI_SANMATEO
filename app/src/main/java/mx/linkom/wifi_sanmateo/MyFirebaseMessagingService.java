package mx.linkom.wifi_sanmateo;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Random;

public class MyFirebaseMessagingService extends FirebaseMessagingService {


    private mx.linkom.wifi_sanmateo.Configuracion Conf;

    private static final String TAG = "NOTIFICACION";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Conf = new mx.linkom.wifi_sanmateo.Configuracion(this);
        String title = "";
        String mes = "";
        String tema = "";
        String visita = "";
        String pluma = "";
        String opcion = "";
        String idMensaje = "";
        String codigoMensaje = "";


        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
            title = remoteMessage.getData().get("title");
            tema = remoteMessage.getData().get("tema");
            visita = remoteMessage.getData().get("visita");
            pluma = remoteMessage.getData().get("pluma");
            mes = remoteMessage.getData().get("body");
            opcion = remoteMessage.getData().get("opcion");
            idMensaje = remoteMessage.getData().get("idMensaje");
            codigoMensaje = remoteMessage.getData().get("codigoMensaje");


        }

        sendNotification(title,mes, opcion, idMensaje, codigoMensaje,tema,visita,pluma);
    }

    //PROCESA NOTIFICACION
    private void sendNotification(final String title, final String messageBody, final String opcion,
                                  final String idMensaje, final String codigoMensaje,final String tema,final String visita,final String pluma){

        Conf.setNoti(tema);
        Conf.setVisita(visita);
      //  Conf.setNombrelPluma(pluma);
     //   Log.e("TAG","ANDY: " + Conf.getNoti());


        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Intent intent = new Intent(this,mx.linkom.wifi_sanmateo.NavegadorActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        String id="mensaje";
        NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, id);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel nc = new NotificationChannel(id, "nuevo",NotificationManager.IMPORTANCE_HIGH);
            nc.setShowBadge(true);
            assert nm != null;
            nm.createNotificationChannel(nc);

        }
        builder.setAutoCancel(true)
                .setWhen(System.currentTimeMillis())
                .setContentTitle(title)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentText(messageBody)
                .setSound(soundUri)
                .setContentIntent(pendingIntent);

        Random random = new Random();
        int idNotify = random.nextInt(8000);

        assert nm != null;
        nm.notify(idNotify,builder.build());

    }
}
