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

    @Override
    public String generarCodigo() {
        // Para una constante, el "registro" es su propio valor literal.
        // Si es un string o arreglo, requiere otro trato, pero para INT/FLOAT es directo.
        this.setIrRef(this.getValor().toString());

        return ""; // No genera instrucciones, solo devuelve su valor hacia arriba
    }
}