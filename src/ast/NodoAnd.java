package ast;

public class NodoAnd extends OperacionBinaria {
    public NodoAnd(Expresion izquierda, Expresion derecha) {
        super(izquierda, derecha);
    }

    @Override
    protected String getNombreOperacion() {
        return "AND";
    }
}