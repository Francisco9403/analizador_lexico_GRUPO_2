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

    @Override
    public String getEtiqueta() {
        return getNombreOperacion();
    }

    @Override
    public List<NodoC> getHijos() {
        return List.of(izquierda, derecha);
    }
}