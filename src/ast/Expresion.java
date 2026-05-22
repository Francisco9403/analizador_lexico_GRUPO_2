package ast;

public abstract class Expresion extends NodoC {
    protected String tipoDato = "UNKNOWN";

    public String getTipoDato() { return tipoDato; }
    public void setTipoDato(String tipoDato) { this.tipoDato = tipoDato; }
}