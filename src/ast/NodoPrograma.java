package ast;

import java.util.ArrayList;
import java.util.List;

public class NodoPrograma extends NodoC {
    private final List<NodoC> declaraciones;
    private final List<NodoC> sentencias;

    public NodoPrograma(List<NodoC> declaraciones, List<NodoC> sentencias) {
        this.declaraciones = declaraciones != null ? declaraciones : new ArrayList<>();
        this.sentencias = sentencias != null ? sentencias : new ArrayList<>();
    }

    @Override
    public String getEtiqueta() {
        return "PROGRAM";
    }

    @Override
    public List<NodoC> getHijos() {
        List<NodoC> hijos = new ArrayList<>();
        hijos.addAll(declaraciones);
        hijos.addAll(sentencias);
        return hijos;
    }
}