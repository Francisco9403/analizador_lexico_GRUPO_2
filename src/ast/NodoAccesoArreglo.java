package ast;

import java.util.List;

public class NodoAccesoArreglo extends NodoC {
    private String id;
    private NodoC indice;

    public NodoAccesoArreglo(String id, NodoC indice) {
        this.id = id;
        this.indice = indice;
    }

    @Override
    public String getEtiqueta() {
        return "ACCESO\\n" + id + "[]";
    }

    @Override
    public List<NodoC> getHijos() {
        return List.of(indice);
    }
}
