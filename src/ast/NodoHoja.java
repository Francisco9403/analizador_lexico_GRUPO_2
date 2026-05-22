package ast;

public class NodoHoja extends Expresion {
    private final Object valor;

    public NodoHoja(Object valor) {
        this.valor = valor;
    }

    public Object getValor() {
        return valor;
    }

    @Override
    public String getEtiqueta() {
        return "Const: " + valor.toString();
    }
}