package ast;

public class Constante extends Expresion {
    private final Object valor;

    public Constante(Object valor) {
        this.valor = valor;
    }

    public Object getValor() {
        return valor;
    }

    @Override
    public String getEtiqueta() {
        if (getTipoDato() != null && !getTipoDato().equals("UNKNOWN")) {
            return "Const: " + valor + " (" + getTipoDato() + ")";
        }
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