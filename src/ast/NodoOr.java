package ast;

public class NodoOr extends OperacionBinaria {
    public NodoOr(Expresion izquierda, Expresion derecha) {
        super(izquierda, derecha);
    }

    @Override
    protected String getNombreOperacion() {
        return "or";
    }
}