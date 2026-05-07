package ast;

import java.util.ArrayList;
import java.util.List;

public abstract class Nodo {
    // Generador de IDs únicos para que Graphviz no mezcle los nodos
    private static int contadorNodos = 0;
    private final int idNodo;

    public Nodo() {
        this.idNodo = contadorNodos++;
    }

    public int getIdNodo() {
        return idNodo;
    }

    public abstract String getEtiqueta();

    public List<Nodo> getHijos() {
        return new ArrayList<>();
    }

    // --- LÓGICA PARA EXPORTAR A GRAPHVIZ (.DOT) ---
    public String generarGrafoDot() {
        StringBuilder sb = new StringBuilder();
        sb.append("digraph AST {\n");
        sb.append("  node [shape=box, style=filled, color=lightblue];\n");
        generarNodosYAristas(sb, this);
        sb.append("}\n");
        return sb.toString();
    }

    private void generarNodosYAristas(StringBuilder sb, Nodo nodo) {
        // ¡LA MAGIA ESTÁ ACÁ! Escapamos las comillas internas para que Graphviz no se rompa
        String etiquetaEscapada = nodo.getEtiqueta().replace("\"", "\\\"");

        // Declaramos el nodo actual con su ID único y su etiqueta escapada
        sb.append("  nodo").append(nodo.getIdNodo())
                .append(" [label=\"").append(etiquetaEscapada).append("\"];\n");

        // Recorremos los hijos para enlazarlos
        for (Nodo hijo : nodo.getHijos()) {
            if (hijo != null) {
                // Generamos la flecha del padre al hijo
                sb.append("  nodo").append(nodo.getIdNodo())
                        .append(" -> nodo").append(hijo.getIdNodo()).append(";\n");
                // Llamada recursiva para el hijo
                generarNodosYAristas(sb, hijo);
            }
        }
    }
}