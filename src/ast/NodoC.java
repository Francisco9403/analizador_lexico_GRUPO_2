package ast;

import java.util.ArrayList;
import java.util.List;

public abstract class NodoC {
    private static int contadorNodos = 0;
    private final int idNodo;
    protected String tipoDato = "UNKNOWN";

    public NodoC() {
        this.idNodo = contadorNodos++;
    }

    public int getIdNodo() {
        return idNodo;
    }

    // Cada nodo define su propio texto visual en el gráfico
    public abstract String getEtiqueta();

    // Las subclases devuelven su lista de nodos hijos apuntados
    public List<NodoC> getHijos() {
        return new ArrayList<>();
    }

    public String getTipoDato() {
        return tipoDato;
    }

    public void setTipoDato(String tipoDato) {
        this.tipoDato = tipoDato;
    }

    // Genera la cadena completa en formato DOT para Graphviz
    public String generarGrafoDot() {
        StringBuilder sb = new StringBuilder();
        sb.append("digraph AST {\n");
        sb.append("  node [shape=box, style=filled, color=lightblue];\n");
        generarNodosYAristas(sb, this);
        sb.append("}\n");
        return sb.toString();
    }

    private void generarNodosYAristas(StringBuilder sb, NodoC nodo) {
        if (nodo == null) return;
        String etiquetaEscapada = nodo.getEtiqueta().replace("\"", "\\\"");

        sb.append("  nodo").append(nodo.getIdNodo())
                .append(" [label=\"").append(etiquetaEscapada).append("\"];\n");

        for (NodoC hijo : nodo.getHijos()) {
            if (hijo != null) {
                sb.append("  nodo").append(nodo.getIdNodo())
                        .append(" -> nodo").append(hijo.getIdNodo()).append(";\n");
                generarNodosYAristas(sb, hijo);
            }
        }
    }
}