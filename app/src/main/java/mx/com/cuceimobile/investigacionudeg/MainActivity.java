package mx.com.cuceimobile.investigacionudeg;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import mx.com.cuceimobile.investigacionudeg.db.Respuesta;
import mx.com.cuceimobile.investigacionudeg.db.RespuestaDbHelper;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    private ProgressBar mprogressBar;
    private ImageButton b1,b2,b3,b4,b5;
    private TextView texto,variable,palabra, contexto;
    private int cont_contexto;
    private int pos;
    private int progreso;
    private int cant_progreso;
    private String[] arr_contexto;
    private String[] arr_mensaje_contexto;
    private String[] arr_resultados;
    private String arr[];
    private String Palabra;
    private int Agradable;
    private int Reaccion;
    private int Control;
    private int TotalReaccion;
    private int TotalControl;
    private int id_usuario;
    private Boolean get_next_word;
    private static final int USER_FORM = 841;
    public static final int RESULT_OK = 744;
    private String Nombre;
    private int Edad;
    private String Tipo;
    private String Genero;
    private boolean usuario_enviado;
    private int palabra_escoger[][] = {
            {0,0,2,2,3},
            {0,1,2,3,4},
            {0,1,2,3,4},
            {0,1,2,3,4},
            {1,1,3,4,4}
    };

    private static final String URL = "http://hypatia.cucei.udg.mx/rapem/palabras.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent intent = new Intent(this, UserForm.class);
        startActivityForResult(intent, this.USER_FORM);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Guardar todos los botones en la vista
        b1=(ImageButton)findViewById(R.id.c1);
        b2=(ImageButton)findViewById(R.id.c2);
        b3=(ImageButton)findViewById(R.id.c3);
        b4=(ImageButton)findViewById(R.id.c4);
        b5=(ImageButton)findViewById(R.id.c5);

        // Texto presentado en la parte superior
        texto=(TextView)findViewById(R.id.textView2);

        // Palabra que está al centro
        palabra=(TextView)findViewById(R.id.textView);

        // Barra de progreso
        mprogressBar = (ProgressBar)findViewById(R.id.progressBar);
        mprogressBar.setRotation(-90);
        mprogressBar.setProgress(0);
        TotalReaccion = 0;
        TotalControl = 0;

        // Obtener arreglos de contextos predefinidos
        arr_contexto = getResources().getStringArray(R.array.contexto);
        arr_mensaje_contexto = getResources().getStringArray(R.array.mensaje_contexto);
        arr_resultados = getResources().getStringArray(R.array.respuestas);
        get_next_word = false;

        this.usuario_enviado = false;
        this.id_usuario = 0;
        // Cargado inicial
        load();
    }

    public void click(View view){
        int value = 0;
        // Depende al botón presionado, se guarda un valor entero
        switch (view.getId()) {
            case R.id.c1:
                value = 1;
                break;
            case R.id.c2:
                value = 2;
                break;
            case R.id.c3:
                value = 3;
                break;
            case R.id.c4:
                value = 4;
                break;
            case R.id.c5:
                value = 5;
                break;
        }
        switch (cont_contexto) {
            case 0:
                Agradable = value;
                break;
            case 1:
                Reaccion = value;
                break;
            case 2:
                Control = value;
                break;
        }
        if (progreso >= 99)
            mprogressBar.setProgress(100);
        else {
            progreso += cant_progreso;
            mprogressBar.setProgress(progreso);
        }

        // Obtener el siguiente contexto que tendrá la palabra
        next_context();
        // Obtener siguiente palabra
        if (get_next_word){
            get_next_word = false;
            handleActionPostPalabra(Palabra, Agradable, Reaccion, Control);
            if (pos < arr.length) {
                next_word();
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(getResources().getString(R.string.mensaje_final));
                builder.setTitle(getResources().getString(R.string.titulo_final));
                builder.setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        load();
                        dialogInterface.cancel();
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        int x = TotalControl/arr.length;
                        int y = TotalReaccion/arr.length;
                        dialogInterface.cancel();
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setMessage(arr_resultados[palabra_escoger[x][y]]).setTitle("Resultado");
                        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                TotalReaccion = 0;
                                TotalControl = 0;
                                dialogInterface.cancel();
                                Intent intent = new Intent(MainActivity.this, UserForm.class);
                                startActivityForResult(intent, USER_FORM);
                            }
                        });
                        AlertDialog dialog = builder.create();
                        dialog.setCanceledOnTouchOutside(false);
                        dialog.show();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();
            }
        }
    }

    public void next_word(){
        this.TotalControl += Control;
        this.TotalReaccion += Reaccion;
        Palabra = arr[pos++];
        palabra.setText(Palabra);
    }

    public void next_context(){
        if (cont_contexto == -1) {
            cont_contexto = 0;
        } else if (cont_contexto == arr_contexto.length - 1) {
            cont_contexto = 0;
            get_next_word = true;
        } else {
            cont_contexto++;
        }
        // Asignar palabra a TextView
        // contexto.setText(arr_contexto[cont_contexto]);

        // Modificar iconos de acuerdo al siguiente estado/contexto
        switch (cont_contexto) {
            case 0:
                b1.setImageResource(R.drawable.agradable1);
                b2.setImageResource(R.drawable.agradable2);
                b3.setImageResource(R.drawable.agradable3);
                b4.setImageResource(R.drawable.agradable4);
                b5.setImageResource(R.drawable.agradable5);
                break;
            case 1:
                b1.setImageResource(R.drawable.reaccion1);
                b2.setImageResource(R.drawable.reaccion2);
                b3.setImageResource(R.drawable.reaccion3);
                b4.setImageResource(R.drawable.reaccion4);
                b5.setImageResource(R.drawable.reaccion5);
                break;
            case 2:
                b1.setImageResource(R.drawable.control1);
                b2.setImageResource(R.drawable.control2);
                b3.setImageResource(R.drawable.control3);
                b4.setImageResource(R.drawable.control4);
                b5.setImageResource(R.drawable.control5);
                break;
        }

        // Asignar nuevo texto a la parte superior
        texto.setText(arr_mensaje_contexto[cont_contexto]);
    }


    private void handleActionPostPalabra(final String palabra, final int agradable, final int reaccion, final int control){
        try {
            RequestQueue request = Volley.newRequestQueue(this);
            JSONObject jsonbody = new JSONObject();
            JSONObject jsonrespuesta = new JSONObject();
            JSONObject jsonusuario = new JSONObject();
            jsonbody.put("palabra", palabra);
            jsonrespuesta.put("Agradable", agradable);
            jsonrespuesta.put("Reaccion", reaccion);
            jsonrespuesta.put("Control", control);
            jsonbody.put("respuesta", jsonrespuesta);
            if (this.id_usuario == 0) {
                jsonusuario.put("nombre", this.Nombre);
                jsonusuario.put("edad", this.Edad);
                jsonusuario.put("tipo", this.Tipo);
                jsonusuario.put("genero", this.Genero);
                jsonbody.put("usuario", jsonusuario);
            } else {
                jsonbody.put("id_usuario", this.id_usuario);
            }
            final String requestBody = jsonbody.toString();
            Log.i("Request", requestBody);
            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject res = new JSONObject(response);
                        id_usuario = res.getInt("id_usuario");
                    } catch (JSONException e){
                        id_usuario = 0;
                        e.printStackTrace();
                    }
                    Log.i("Palabras POST", response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    RespuestaDbHelper respuestaDbHelper = new RespuestaDbHelper(MainActivity.this);
                    SQLiteDatabase db = respuestaDbHelper.getWritableDatabase();
                    ContentValues values = new ContentValues();
                    values.put(Respuesta.RespuestaEntry.COLUMN_NAME_PALABRA, palabra);
                    values.put(Respuesta.RespuestaEntry.COLUMN_NAME_AGRADABLE, agradable);
                    values.put(Respuesta.RespuestaEntry.COLUMN_NAME_DOMINIO, control);
                    values.put(Respuesta.RespuestaEntry.COLUMN_NAME_REACCION, reaccion);
                    values.put(Respuesta.RespuestaEntry.COLUMN_NAME_USUARIO, id_usuario);
                    db.insert(
                            Respuesta.RespuestaEntry.TABLE_NAME,
                            null,
                            values
                    );
                    Log.e("Palabras POST", error.toString());
                }
            }) {
                @Override
                public String getBodyContentType() {
                    return "application/json; charset=utf-8";
                }

                @Override
                public byte[] getBody() throws AuthFailureError {
                    try {
                        return requestBody == null ? null : requestBody.getBytes("utf-8");
                    } catch (UnsupportedEncodingException uee) {
                        VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", requestBody, "utf-8");
                        return null;
                    }
                }
            };

            request.add(stringRequest);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void load(){
        check_offline();
        RequestQueue request = Volley.newRequestQueue(this);
        JsonArrayRequest stringRequest = new JsonArrayRequest(URL,
            new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
                    pos = 0;
                    List<String> palabras_response = new ArrayList<>();
                    JSONObject jsonObject;
                    Log.d("Get Palabras", response.toString());
                    try {
                        for (int i = 0; i < response.length(); i++) {
                            jsonObject = (JSONObject)response.get(i);
                            palabras_response.add(jsonObject.getString("palabra"));
                        }
                        arr = palabras_response.toArray(new String[palabras_response.size()]);

                        if (arr.length == 0) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                            builder.setMessage("El estudio ha terminado. Muchas gracias por tu colaboración.").setTitle("Estudio terminado");
                            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.cancel();
                                }
                            });
                            AlertDialog dialog = builder.create();
                            dialog.show();
                            palabra.setText("");
                            contexto.setText("");
                            mprogressBar.setMax(100);
                            mprogressBar.setProgress(100);
                            return;
                        }

                        Palabra = arr[pos++];

                        palabra.setText(Palabra);

                        // Inicializar el primer contexto
                        cont_contexto = - 1;
                        next_context();

                        /* Inicializar barra de progreso y obtener la cantidad que irá aumentando
                         * conforme vaya progresando la elección de estados.
                         */
                        progreso = 0;
                        cant_progreso = 100 / (arr.length*3);
                        Log.d("Progreso", String.valueOf(cant_progreso));
                        Log.d("Tamaño", String.valueOf(arr.length));
                        mprogressBar.setMax(cant_progreso*(arr.length*3));
                        mprogressBar.setProgress(0);
                    } catch (JSONException e) {
                        //e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyLog.d("Get Palabras", "Error: " + error.getMessage());
                    palabra.setText("");
                    //contexto.setText("");
                    mprogressBar.setMax(100);
                    mprogressBar.setProgress(100);
                    Toast.makeText(getApplicationContext(),
                            "Error en la conexión, volviendo a intentar conectarse a los servidores", Toast.LENGTH_SHORT).show();
                }
            });
        request.add(stringRequest);
    }

    private void check_offline() {
        RespuestaDbHelper respuestaDbHelper = new RespuestaDbHelper(this);

        String[] projection = {
                Respuesta.RespuestaEntry._ID,
                Respuesta.RespuestaEntry.COLUMN_NAME_PALABRA,
                Respuesta.RespuestaEntry.COLUMN_NAME_DOMINIO,
                Respuesta.RespuestaEntry.COLUMN_NAME_REACCION,
                Respuesta.RespuestaEntry.COLUMN_NAME_AGRADABLE
        };

        SQLiteDatabase db = respuestaDbHelper.getReadableDatabase();
        int usuario_tem = id_usuario;
        try {
            Cursor c = db.query(Respuesta.RespuestaEntry.TABLE_NAME, projection, null, null, null, null, null);
            while (c.moveToNext()) {
                int id = c.getInt(c.getColumnIndexOrThrow(Respuesta.RespuestaEntry._ID));
                int agradable = c.getInt(c.getColumnIndex(Respuesta.RespuestaEntry.COLUMN_NAME_AGRADABLE));
                int dominio = c.getInt(c.getColumnIndex(Respuesta.RespuestaEntry.COLUMN_NAME_DOMINIO));
                int reaccion = c.getInt(c.getColumnIndex(Respuesta.RespuestaEntry.COLUMN_NAME_REACCION));
                //id_usuario = c.getInt(c.getColumnIndex(Respuesta.RespuestaEntry.COLUMN_NAME_USUARIO));//Esta linea está comentada porque tronaba la aplicacion y no se encontró utilidad
                String palabra = c.getString(c.getColumnIndex(Respuesta.RespuestaEntry.COLUMN_NAME_PALABRA));

                handleActionPostPalabra(palabra, agradable, reaccion, dominio);

                String selection = Respuesta.RespuestaEntry._ID + " = ?";
                String selectionArgs[] = {String.valueOf(id)};
                respuestaDbHelper.getWritableDatabase().delete(
                        Respuesta.RespuestaEntry.TABLE_NAME,
                        selection,
                        selectionArgs
                );
            }
        }
        catch (SQLiteException e)
        {

        }

        id_usuario = usuario_tem;
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == MainActivity.USER_FORM) {
            if (resultCode == MainActivity.RESULT_OK) {
                this.Nombre = data.getStringExtra("Nombre");
                this.Edad = data.getIntExtra("Edad", 18);
                this.Tipo = data.getStringExtra("Tipo");
                this.Genero = data.getStringExtra("Genero");
            }
        }
    }
}
