package mx.com.cuceimobile.investigacionudeg.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public final class RespuestaDbHelper extends SQLiteOpenHelper {
    private static final String TEXT_TYPE = " TEXT";
    private static final String INTEGER_TYPE = " INTEGER";
    private static final String COMMA_SEP = ",";
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + Respuesta.RespuestaEntry.TABLE_NAME + " (" +
                    Respuesta.RespuestaEntry._ID + " INTEGER PRIMARY KEY," +
                    Respuesta.RespuestaEntry.COLUMN_NAME_PALABRA + TEXT_TYPE + COMMA_SEP +
                    Respuesta.RespuestaEntry.COLUMN_NAME_AGRADABLE + INTEGER_TYPE + COMMA_SEP +
                    Respuesta.RespuestaEntry.COLUMN_NAME_REACCION + INTEGER_TYPE + COMMA_SEP +
                    Respuesta.RespuestaEntry.COLUMN_NAME_DOMINIO + INTEGER_TYPE  + COMMA_SEP +
                    Respuesta.RespuestaEntry.COLUMN_NAME_USUARIO + INTEGER_TYPE + " )";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + Respuesta.RespuestaEntry.TABLE_NAME;
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Respuesta.db";

    public RespuestaDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL(SQL_DELETE_ENTRIES);
        onCreate(sqLiteDatabase);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
