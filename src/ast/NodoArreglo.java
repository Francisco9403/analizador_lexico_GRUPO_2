package ast;

import java.util.ArrayList;
import java.util.List;

public class NodoArreglo extends Expresion {
    private final List<NodoC> elementos;

    public NodoArreglo(List<NodoC> elementos) {
        this.elementos = elementos != null ? elementos : new ArrayList<>();
    }

    @Override
    public String getEtiqueta() {
        return "Arreglo Literal";
    }

    @Override
    public List<NodoC> getHijos() {
        return new ArrayList<>(elementos);
    }
}