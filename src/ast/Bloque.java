package ast;

import java.util.ArrayList;
import java.util.List;

public class Bloque extends Nodo {
    private final String etiqueta;
    private final List<Nodo> instrucciones;

    public Bloque(String etiqueta, List<Nodo> instrucciones) {
        this.etiqueta = etiqueta;
        this.instrucciones = instrucciones != null ? instrucciones : new ArrayList<>();
    }

    @Override
    public String getEtiqueta() { return etiqueta; }

    @Override
    public List<Nodo> getHijos() { return instrucciones; }

    @Override
    public String generarCodigo() {
        StringBuilder sb = new StringBuilder();
        for (Nodo n : instrucciones) {
            sb.append(n.generarCodigo());
        }
        return sb.toString();
    }
}