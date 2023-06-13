package mx.linkom.wifi_sanmateo.fotosSegundoPlano;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class ContentProvider extends android.content.ContentProvider {

    //Objeto UriMatcher para comprobar el content Uri
    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    // Casos
    public static final int FOTOS_OFFLINE = 200;



    public static final String AUTORIDAD = "mx.linkom.wifi_sanmateo";

    //Static inicializer, se ejecuta la primera vez que algo es llamado desde la clase
    static {
        uriMatcher.addURI(AUTORIDAD, "fotosOffline", FOTOS_OFFLINE);
    }

    //Inicializa el provider y el objetivo database Helper
    private Database database;
    private SQLiteDatabase bd;

    @Override
    public boolean onCreate() {
        database = new Database(getContext());
        bd = database.getWritableDatabase();
        return true;
    }

    //Realiza la solicitud para la Uri, Nececita projection, projection, selection, selection arguments, and sort order
    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        //Log.d(TAG, "Query en " + uri);

        Cursor cursor = null;

        int match = uriMatcher.match(uri);
        switch (match){
            case FOTOS_OFFLINE:
                cursor = bd.rawQuery("SELECT titulo, direccionFirebase, rutaDispositivo FROM fotosOffline WHERE rutaDispositivo != '' ",null);
                break;
            default:
                Log.e("error", "Error al ejecutar query:  " + uri.toString() );
                break;
        }

        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {

        //Log.d(TAG, "Inserción en " + uri + "( " + values.toString() + " )n");

        long insert = 0;

        String id = null;

        switch (uriMatcher.match(uri)){
            case FOTOS_OFFLINE:
                insert = bd.insert("fotosOffline", null, values);
                if (insert == -1){
                    Log.e("error", "Error al registrar en fotos_offline");
                    return null;
                }
                break;
            default:
                Log.e("error", "Error al insertar el registro:  " + uri.toString() );
                break;
        }
        return ContentUris.withAppendedId(uri,insert);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        //Log.d(TAG, "Delete registro : " + uri.toString());

        int delete = -1;

        switch (uriMatcher.match(uri)){
            case FOTOS_OFFLINE:
                delete = bd.delete("fotosOffline", selection, null);
                break;
            default:
                Log.e("error", "Error al eliminar el registro:  " + uri.toString() );
                break;
        }

        return delete;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {

        int actualizar = -1;

        switch (uriMatcher.match(uri)){
            default:
                break;
        }
        return actualizar;
    }
}
