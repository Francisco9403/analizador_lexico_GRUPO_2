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
        String labelEnd = CodeGeneratorHelper.getNewLabel();

        // Si hay un ELSE, reservamos su etiqueta, si no, apuntamos al final
        String labelElse = (this.bloqueElse != null && !this.bloqueElse.isEmpty()) ? CodeGeneratorHelper.getNewLabel() : labelEnd;

        List<String> labelsElif = new ArrayList<>();
        if (this.elifs != null) {
            for (int i = 0; i < this.elifs.size(); i++) {
                labelsElif.add(CodeGeneratorHelper.getNewLabel());
            }
        }

        // 1. Evaluar IF principal
        String destinoFalsoPrincipal = labelsElif.isEmpty() ? labelElse : labelsElif.get(0);

        codigo.append(this.condicion.generarCodigo());
        codigo.append(String.format("  br i1 %s, label %%%s, label %%%s\n",
                this.condicion.getIrRef(), labelThen, destinoFalsoPrincipal));

        // 2. Bloque THEN principal
        codigo.append("\n").append(labelThen).append(":\n");
        StringBuilder codigoThen = new StringBuilder();
        if (this.bloqueThen != null) {
            for (NodoC sent : this.bloqueThen) {
                codigoThen.append(sent.generarCodigo());
            }
        }
        codigo.append(codigoThen);
        if (!terminaConSalto(codigoThen.toString())) {
            codigo.append(String.format("  br label %%%s\n", labelEnd));
        }

        // 3. Cadena de bloques ELIF
        if (this.elifs != null && !this.elifs.isEmpty()) {
            for (int i = 0; i < this.elifs.size(); i++) {
                codigo.append("\n").append(labelsElif.get(i)).append(":\n");

                NodoIf elifNodo = (NodoIf) this.elifs.get(i);

                // Acá armamos la cadena: si es falso, va al siguiente ELIF o al ELSE
                String destinoFalsoElif;
                if (i < this.elifs.size() - 1) {
                    destinoFalsoElif = labelsElif.get(i + 1);
                } else {
                    destinoFalsoElif = labelElse;
                }

                String labelElifThen = CodeGeneratorHelper.getNewLabel();

                codigo.append(elifNodo.condicion.generarCodigo());
                codigo.append(String.format("  br i1 %s, label %%%s, label %%%s\n",
                        elifNodo.condicion.getIrRef(), labelElifThen, destinoFalsoElif));

                codigo.append("\n").append(labelElifThen).append(":\n");
                StringBuilder codigoElifThen = new StringBuilder();
                if (elifNodo.bloqueThen != null) {
                    for (NodoC sent : elifNodo.bloqueThen) {
                        codigoElifThen.append(sent.generarCodigo());
                    }
                }
                codigo.append(codigoElifThen);
                if (!terminaConSalto(codigoElifThen.toString())) {
                    codigo.append(String.format("  br label %%%s\n", labelEnd));
                }
            }
        }

        // 4. Bloque ELSE final
        if (this.bloqueElse != null && !this.bloqueElse.isEmpty()) {
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

        codigo.append("\n").append(labelEnd).append(":\n");

        return codigo.toString();
    }
}