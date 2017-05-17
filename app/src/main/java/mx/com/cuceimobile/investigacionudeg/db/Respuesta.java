package mx.com.cuceimobile.investigacionudeg.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

/**
 * Created by donluigimx on 24/11/16.
 */

public final class Respuesta {

    private Respuesta() {}

    public static class RespuestaEntry implements BaseColumns {
        public static final String TABLE_NAME = "respuesta";
        public static final String COLUMN_NAME_PALABRA = "palabra";
        public static final String COLUMN_NAME_AGRADABLE = "agradable";
        public static final String COLUMN_NAME_REACCION = "reaccion";
        public static final String COLUMN_NAME_DOMINIO = "dominio";
        public static final String COLUMN_NAME_USUARIO = "id_usuario";
    }
}

