package ast;

import java.util.List;

public class NodoAsignacion extends NodoC {
    private NodoC identificador;
    private NodoC expresion;

    public NodoAsignacion(NodoC identificador, NodoC expresion) {
        this.identificador = identificador;
        this.expresion = expresion;
    }

    @Override
    public String getEtiqueta() {
        return "ASIG";
    }

    @Override
    public List<NodoC> getHijos() {
        return List.of(identificador, expresion);
    }
}
