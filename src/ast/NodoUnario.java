package ast;

import ast.Expresion;
import ast.NodoC;
import java.util.Collections;
import java.util.List;

public class NodoUnario extends NodoC {
    private final String operador;
    private final NodoC nodo;

    public NodoUnario(String operador, NodoC nodo) {
        this.operador = operador;
        this.nodo = nodo;
    }

    @Override
    public String getEtiqueta() {
        return "Operación Unaria: " + operador;
    }

    @Override
    public List<NodoC> getHijos() {
        return Collections.singletonList(nodo);
    }
}