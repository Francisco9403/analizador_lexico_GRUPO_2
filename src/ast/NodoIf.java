package ast;

import llvm.CodeGeneratorHelper;
import java.util.ArrayList;
import java.util.List;

public class NodoIf extends Sentencia {
    private final NodoC condicion;
    private final List<NodoC> bloqueThen;
    private final List<NodoC> elifs;
    private final List<NodoC> bloqueElse;

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

    // --- MÉTODO AUXILIAR PARA EVITAR DOBLE SALTO ---
    private boolean terminaConSalto(String codigoBloque) {
        if (codigoBloque == null || codigoBloque.trim().isEmpty()) return false;
        String[] lineas = codigoBloque.trim().split("\n");
        String ultimaLinea = lineas[lineas.length - 1].trim();
        // Si el bloque ya tiene un break, continue o return, no agregamos otro salto
        return ultimaLinea.startsWith("br ") || ultimaLinea.startsWith("ret ");
    }

    @Override
    public String generarCodigo() {
        StringBuilder codigo = new StringBuilder();

        String labelThen = CodeGeneratorHelper.getNewLabel();
        String labelEnd = CodeGeneratorHelper.getNewLabel();

        String labelElse = null;
        if (this.bloqueElse != null && !this.bloqueElse.isEmpty()) {
            labelElse = CodeGeneratorHelper.getNewLabel();
        }

        String destinoFalsoPrincipal = labelEnd;
        List<String> labelsElifCond = new ArrayList<>();

        if (this.elifs != null && !this.elifs.isEmpty()) {
            for (int i = 0; i < this.elifs.size(); i++) {
                labelsElifCond.add(CodeGeneratorHelper.getNewLabel());
            }
            destinoFalsoPrincipal = labelsElifCond.get(0);
        } else if (labelElse != null) {
            destinoFalsoPrincipal = labelElse;
        }

        // Evaluar condición IF
        codigo.append(this.condicion.generarCodigo());
        codigo.append(String.format("  br i1 %s, label %%%s, label %%%s\n",
                this.condicion.getIrRef(), labelThen, destinoFalsoPrincipal));

        // Bloque THEN
        codigo.append("\n").append(labelThen).append(":\n");
        StringBuilder codigoThen = new StringBuilder();
        if (this.bloqueThen != null) {
            for (NodoC sent : this.bloqueThen) {
                codigoThen.append(sent.generarCodigo());
            }
        }
        codigo.append(codigoThen);
        // Solo agregamos el salto de salida si no hay un break/continue previo
        if (!terminaConSalto(codigoThen.toString())) {
            codigo.append(String.format("  br label %%%s\n", labelEnd));
        }

        // Bloque ELIF
        if (this.elifs != null && !this.elifs.isEmpty()) {
            for (int i = 0; i < this.elifs.size(); i++) {
                codigo.append("\n").append(labelsElifCond.get(i)).append(":\n");

                NodoC elifNodo = this.elifs.get(i);
                String codigoElif = elifNodo.generarCodigo();
                codigo.append(codigoElif);

                if (!terminaConSalto(codigoElif)) {
                    codigo.append(String.format("  br label %%%s\n", labelEnd));
                }
            }
        }

        // Bloque ELSE
        if (labelElse != null) {
            codigo.append("\n").append(labelElse).append(":\n");
            StringBuilder codigoElse = new StringBuilder();
            for (NodoC sentElse : this.bloqueElse) {
                codigoElse.append(sentElse.generarCodigo());
            }
            codigo.append(codigoElse);
            if (!terminaConSalto(codigoElse.toString())) {
                codigo.append(String.format("  br label %%%s\n", labelEnd));
            }
        }

        // Etiqueta Final
        codigo.append("\n").append(labelEnd).append(":\n");

        return codigo.toString();
    }
}