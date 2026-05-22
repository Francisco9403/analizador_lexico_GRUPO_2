package ast;
import java.util.List;

public class NodoOperacion extends NodoC {
    private final String operador;
    private final NodoC izquierda;
    private final NodoC derecha;

    public NodoOperacion(String operador, NodoC izquierda, NodoC derecha) {
        this.operador = operador;
        this.izquierda = izquierda;
        this.derecha = derecha;
    }

    @Override
    public String getEtiqueta() { return operador; }

    @Override
    public List<NodoC> getHijos() { return List.of(izquierda, derecha); }
}