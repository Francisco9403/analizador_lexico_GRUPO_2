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

    @Override
    public String generarCodigo() {
        StringBuilder codigo = new StringBuilder();

        String labelThen = CodeGeneratorHelper.getNewLabel();
        String labelEnd = CodeGeneratorHelper.getNewLabel();

        // 1. Determinar de antemano a dónde saltar si falla el IF principal
        String destinoFalsoPrincipal = labelEnd;
        List<String> labelsElifCond = new ArrayList<>();

        if (this.elifs != null && !this.elifs.isEmpty()) {
            for (int i = 0; i < this.elifs.size(); i++) {
                labelsElifCond.add(CodeGeneratorHelper.getNewLabel());
            }
            destinoFalsoPrincipal = labelsElifCond.get(0); // Va al primer ELIF
        } else if (this.bloqueElse != null && !this.bloqueElse.isEmpty()) {
            destinoFalsoPrincipal = CodeGeneratorHelper.getNewLabel(); // Va directo al ELSE
        }

        // 2. Evaluar la condición del IF principal
        codigo.append(this.condicion.generarCodigo());
        codigo.append(String.format("  br i1 %s, label %%%s, label %%%s\n",
                this.condicion.getIrRef(), labelThen, destinoFalsoPrincipal));

        // 3. Bloque THEN
        codigo.append("\n").append(labelThen).append(":\n");
        if (this.bloqueThen != null) {
            for (NodoC sent : this.bloqueThen) {
                codigo.append(sent.generarCodigo());
            }
        }
        codigo.append(String.format("  br label %%%s\n", labelEnd));

        // 4. Procesar los ELIFs de manera encadenada
        if (this.elifs != null && !this.elifs.isEmpty()) {
            String labelElseReal = (this.bloqueElse != null && !this.bloqueElse.isEmpty()) ? destinoFalsoPrincipal : labelEnd;

            for (int i = 0; i < this.elifs.size(); i++) {
                codigo.append("\n").append(labelsElifCond.get(i)).append(":\n");

                // Si este ELIF falla, salta al siguiente ELIF, o en su defecto al ELSE real/End.
                String siguienteDestinoFalso = (i + 1 < this.elifs.size()) ? labelsElifCond.get(i + 1) : labelElseReal;

                NodoC elifNodo = this.elifs.get(i);

                // NOTA: Si tu parser guardó los elifs como estructuras que ya saben evaluar
                // su condición interna (por ejemplo, sub-nodos con su propia condición y bloque),
                // asegúrate de pasarle o estructurar el salto condicional.
                codigo.append(elifNodo.generarCodigo());
                codigo.append(String.format("  br label %%%s\n", labelEnd));
            }
        }

        // 5. Bloque ELSE
        if (this.bloqueElse != null && !this.bloqueElse.isEmpty()) {
            // El label del else ya fue definido arriba en 'destinoFalsoPrincipal' o 'labelElseReal'
            String labelElse = (this.elifs != null && !this.elifs.isEmpty()) ? destinoFalsoPrincipal : destinoFalsoPrincipal;
            codigo.append("\n").append(destinoFalsoPrincipal).append(":\n");
            for (NodoC sentElse : this.bloqueElse) {
                codigo.append(sentElse.generarCodigo());
            }
            codigo.append(String.format("  br label %%%s\n", labelEnd));
        }

        // 6. Etiqueta de salida unificada
        codigo.append("\n").append(labelEnd).append(":\n");

        return codigo.toString();
    }
}