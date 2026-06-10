package ast;

public abstract class Expresion extends Nodo {

    protected String tipoDato = "UNKNOWN";

    protected String irRef;

    public String getIrRef() {
        return irRef;
    }

    public void setIrRef(String irRef) {
        this.irRef = irRef;
    }

    public String getTipoDato() {
        return tipoDato;
    }

    public void setTipoDato(String tipoDato) {
        this.tipoDato = tipoDato;
    }

    public Expresion() {
        super();
    }

}