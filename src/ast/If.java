package ast;

import llvm.CodeGeneratorHelper;
import java.util.ArrayList;
import java.util.List;

public class If extends Nodo {
    private final Expresion condicion;
    private final List<Nodo> bloqueThen;
    private final List<Nodo> elifs;
    private final List<Nodo> bloqueElse;

    public If(Expresion condicion, List<Nodo> bloqueThen, List<Nodo> elifs, List<Nodo> bloqueElse) {
        this.condicion = condicion;
        this.bloqueThen = bloqueThen;
        this.elifs = elifs;
        this.bloqueElse = bloqueElse;
    }

    @Override
    public String getEtiqueta() { return "IF"; }

    @Override
    public List<Nodo> getHijos() {
        List<Nodo> hijos = new ArrayList<>();
        // Agrupamos visualmente para Graphviz
        hijos.add(new Bloque("Condición", List.of(condicion)));
        if (bloqueThen != null && !bloqueThen.isEmpty()) {
            hijos.add(new Bloque("THEN", bloqueThen));
        }
        if (bloqueElse != null && !bloqueElse.isEmpty()) {
            hijos.add(new Bloque("ELSE", bloqueElse));
        }
        return hijos;
    }

    private boolean terminaConSalto(String codigoBloque) {
        if (codigoBloque == null || codigoBloque.trim().isEmpty()) return false;
        String[] lineas = codigoBloque.trim().split("\n");
        String ultimaLinea = lineas[lineas.length - 1].trim();
        return ultimaLinea.startsWith("br ") || ultimaLinea.startsWith("ret ");
    }

    @Override
    public String generarCodigo() {
        StringBuilder codigo = new StringBuilder();
        String labelThen = CodeGeneratorHelper.getNewLabel();
        String labelElse = (bloqueElse != null && !bloqueElse.isEmpty()) ? CodeGeneratorHelper.getNewLabel() : null;
        String labelEnd = CodeGeneratorHelper.getNewLabel();
        String destinoFalso = (labelElse != null) ? labelElse : labelEnd;

        // 1. Evaluar condición
        codigo.append(condicion.generarCodigo());
        codigo.append(String.format("  br i1 %s, label %%%s, label %%%s\n",
                condicion.getIrRef(), labelThen, destinoFalso));

        // 2. Bloque THEN
        codigo.append("\n").append(labelThen).append(":\n");
        StringBuilder codThen = new StringBuilder();
        if (bloqueThen != null) {
            for (Nodo sent : bloqueThen) codThen.append(sent.generarCodigo());
        }
        codigo.append(codThen);
        if (!terminaConSalto(codThen.toString())) {
            codigo.append(String.format("  br label %%%s\n", labelEnd));
        }

        // 3. Bloque ELSE
        if (labelElse != null) {
            codigo.append("\n").append(labelElse).append(":\n");
            StringBuilder codElse = new StringBuilder();
            for (Nodo sentElse : bloqueElse) codElse.append(sentElse.generarCodigo());
            codigo.append(codElse);
            if (!terminaConSalto(codElse.toString())) {
                codigo.append(String.format("  br label %%%s\n", labelEnd));
            }
        }

        codigo.append("\n").append(labelEnd).append(":\n");
        return codigo.toString();
    }
}