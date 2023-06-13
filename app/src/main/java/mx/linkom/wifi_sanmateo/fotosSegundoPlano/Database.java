package mx.linkom.wifi_sanmateo.fotosSegundoPlano;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class Database extends SQLiteOpenHelper {

    public static int VERSION = 1;
    public Database(@Nullable Context context) {
        super(context, "caseta_modulo.db", null, VERSION);
    }

    //Se ejcuta cuando la base de datos se  va a crear
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        createbd(sqLiteDatabase);
    }

    //Se ejecuta cuando se va a hacer una a ctualización en la versión
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS fotosOffline");

        onCreate(db);
    }

    public void createbd(SQLiteDatabase db){
        //Status 0 = No se ha modificado, 1=  Insertado desde SQLite, 2 = Editado desde SQLite

        String fotosOffline = "CREATE TABLE fotosOffline" +
                "(id INTEGER PRIMARY KEY, " +
                "titulo TEXT, " +
                "direccionFirebase TEXT, " +
                "rutaDispositivo TEXT)";

        db.execSQL(fotosOffline);
    }
}
