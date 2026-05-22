package ast;

import java.util.ArrayList;
import java.util.List;

public class NodoWhile extends Sentencia {
    private final NodoC condicion;
    private final List<NodoC> cuerpo;
    private final List<NodoC> altWhiles;

    // Actualizado para recibir la condición y las listas del cuerpo y alt_whiles
    public NodoWhile(NodoC condicion, List<NodoC> cuerpo, List<NodoC> altWhiles) {
        this.condicion = condicion;
        this.cuerpo = cuerpo;
        this.altWhiles = altWhiles;
    }

    @Override
    public String getEtiqueta() {
        return "WHILE";
    }

    @Override
    public List<NodoC> getHijos() {
        List<NodoC> hijos = new ArrayList<>();
        hijos.add(condicion);
        if (cuerpo != null) hijos.addAll(cuerpo);
        if (altWhiles != null) hijos.addAll(altWhiles);
        return hijos;
    }
}