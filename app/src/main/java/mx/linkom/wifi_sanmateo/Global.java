package mx.linkom.wifi_sanmateo;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.os.PowerManager;
import android.view.View;
import android.view.WindowManager;

public class Global {

    public static String TOKEN = "";
    public static String EMAIL = "";
    public static String USER = "";
    public static String PASS = "";
    public static String TIPO_U = "";
    public static String IP_CAMARA_PLACAS = "";

    public static int TIEMEMPO_BLOQUEO = 30000;

    public static void aumentarVolumen(Context context) {
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, maxVolume, 0);
    }

    public static void ocultarBarrasNavegacionEstado(Activity activity) {
        View decorView = activity.getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
    }

    public static void evitarSuspenderPantalla(Activity activity) {
        PowerManager powerManager = (PowerManager) activity.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK, "mx.linkom.wifi_sanmateo:WakeLockTag");
        wakeLock.acquire();

        activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    public static String getIpCamaraPlacas() {
        return IP_CAMARA_PLACAS;
    }

    public static void setIpCamaraPlacas(String ipCamaraPlacas) {
        IP_CAMARA_PLACAS = ipCamaraPlacas;
    }

    public static int getTiemempoBloqueo() {
        return TIEMEMPO_BLOQUEO;
    }

    public static void setTiemempoBloqueo(int tiemempoBloqueo) {
        TIEMEMPO_BLOQUEO = tiemempoBloqueo;
    }
}
