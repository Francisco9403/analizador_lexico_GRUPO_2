package ast;

import java.util.List;

public abstract class OperacionBinaria extends Expresion {
    protected final Expresion izquierda;
    protected final Expresion derecha;

    public OperacionBinaria(Expresion izquierda, Expresion derecha) {
        this.izquierda = izquierda;
        this.derecha = derecha;
    }

    protected abstract String getNombreOperacion();

    public Expresion getIzquierda() { return izquierda; }
    public Expresion getDerecha() { return derecha; }

    @Override
    public String getEtiqueta() {
        return getNombreOperacion();
    }

    @Override
    public List<Nodo> getHijos() {
        return List.of(izquierda, derecha);
    }
}