package ast;

import java.util.ArrayList;
import java.util.List;

public class NodoIf extends Sentencia {
    private final NodoC condicion;
    private final List<NodoC> bloqueThen;
    private final List<NodoC> elifs;
    private final List<NodoC> bloqueElse;

    // Actualizado para recibir todos los bloques como Listas (como lo manda el Parser)
    public NodoIf(NodoC condicion, List<NodoC> bloqueThen, List<NodoC> elifs, List<NodoC> bloqueElse) {
        this.condicion = condicion;
        this.bloqueThen = bloqueThen;
        this.elifs = elifs;
        this.bloqueElse = bloqueElse;
    }

    @Override
    public String getEtiqueta() {
        return "IF-ELIF-ELSE";
    }

    @Override
    public List<NodoC> getHijos() {
        List<NodoC> hijos = new ArrayList<>();
        hijos.add(condicion);
        if (bloqueThen != null) hijos.addAll(bloqueThen);
        if (elifs != null) hijos.addAll(elifs);
        if (bloqueElse != null) hijos.addAll(bloqueElse);
        return hijos;
    }
}