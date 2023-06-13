package mx.linkom.wifi_sanmateo.fotosSegundoPlano;

import android.net.Uri;

public class UrisContentProvider {

    public static final String CONTENT_AUTHORITY = "mx.linkom.wifi_sanmateo";
    public static final Uri URI_BASE = Uri.parse("content://"+CONTENT_AUTHORITY);

    private static final String RUTA_FOTOSOFFLINE = "fotosOffline";

    public static final Uri URI_CONTENIDO_FOTOS_OFFLINE = Uri.withAppendedPath(URI_BASE, RUTA_FOTOSOFFLINE);

}
