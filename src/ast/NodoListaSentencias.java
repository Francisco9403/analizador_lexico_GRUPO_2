package ast;

import java.util.ArrayList;
import java.util.List;

public class NodoListaSentencias extends NodoC {
    private final List<NodoC> sentencias;

    public NodoListaSentencias() {
        this.sentencias = new ArrayList<>();
    }

    public void agregarSentencia(NodoC sentencia) {
        if (sentencia != null) {
            this.sentencias.add(sentencia);
        }
    }

    @Override
    public String getEtiqueta() {
        return "Bloque de Sentencias";
    }

    @Override
    public List<NodoC> getHijos() {
        return new ArrayList<>(sentencias);
    }
}