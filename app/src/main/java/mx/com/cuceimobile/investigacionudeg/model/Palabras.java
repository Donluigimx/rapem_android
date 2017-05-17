package mx.com.cuceimobile.investigacionudeg.model;

/**
 * Created by donluigimx on 25/10/16.
 */

public class Palabras {

    private String Palabra;
    private String Contexto;
    private int Cara;

    public Palabras(String palabra, String contexto, int cara) {
        Palabra = palabra;
        Contexto = contexto;
        Cara = cara;
    }

    public String getPalabra() {
        return Palabra;
    }

    public void setPalabra(String palabra) {
        Palabra = palabra;
    }

    public String getContexto() {
        return Contexto;
    }

    public void setContexto(String contexto) {
        Contexto = contexto;
    }

    public int getCara() {
        return Cara;
    }

    public void setCara(int cara) {
        Cara = cara;
    }
}
