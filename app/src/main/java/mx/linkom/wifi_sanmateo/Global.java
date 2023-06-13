package mx.linkom.wifi_sanmateo;

public class Global {

    public static String TOKEN = "";
    public static String EMAIL = "";
    public static String USER = "";
    public static String PASS = "";
    public static String TIPO_U = "";
    public static String IP_CAMARA_PLACAS = "";

    public static int TIEMEMPO_BLOQUEO = 30000;

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
