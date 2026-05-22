package ast;

import java.util.ArrayList;
import java.util.List;

public class NodoLlamada extends Expresion {
    private final String nombreFuncion;
    private final List<NodoC> argumentos;

    public NodoLlamada(String nombreFuncion, List<NodoC> argumentos) {
        this.nombreFuncion = nombreFuncion;
        this.argumentos = argumentos != null ? argumentos : new ArrayList<>();
    }

    @Override
    public String getEtiqueta() {
        return "Llamada: " + nombreFuncion;
    }

    @Override
    public List<NodoC> getHijos() {
        return new ArrayList<>(argumentos);
    }
}