package ast;

import llvm.CodeGeneratorHelper;
import java.util.ArrayList;
import java.util.List;

public class While extends Expresion {
    private final Expresion condicion;
    private final List<Nodo> cuerpo;
    private final List<Nodo> altWhiles;

    public While(Expresion condicion, List<Nodo> cuerpo, List<Nodo> altWhiles) {
        this.condicion = condicion;
        this.cuerpo = cuerpo;
        this.altWhiles = altWhiles;
    }

    @Override
    public String getEtiqueta() {
        return "WHILE";
    }

    @Override
    public List<Nodo> getHijos() {
        List<Nodo> hijos = new ArrayList<>();
        hijos.add(condicion);
        if (cuerpo != null) hijos.addAll(cuerpo);
        if (altWhiles != null) hijos.addAll(altWhiles);
        return hijos;
    }

    @Override
    public String generarCodigo() {
        StringBuilder codigo = new StringBuilder();

        String labelCond = CodeGeneratorHelper.getNewLabel();
        String labelCuerpo = CodeGeneratorHelper.getNewLabel();
        String labelEnd = CodeGeneratorHelper.getNewLabel();

        // Si hay sentencias alternativas, saltamos a ellas al fallar la condición; si no, al fin.
        String labelFalso = (this.altWhiles != null && !this.altWhiles.isEmpty()) ? CodeGeneratorHelper.getNewLabel() : labelEnd;

        // Registrar el contexto del bucle (para break/continue si los usas)
        CodeGeneratorHelper.enterLoop(labelCond, labelEnd);

        // Salto inicial obligatorio hacia la evaluación de condición
        codigo.append(String.format("  br label %%%s\n", labelCond));

        // 1. Bloque de evaluación de condición
        codigo.append("\n").append(labelCond).append(":\n");
        codigo.append(condicion.generarCodigo());
        codigo.append(String.format("  br i1 %s, label %%%s, label %%%s\n",
                condicion.getIrRef(), labelCuerpo, labelFalso));

        // 2. Bloque del cuerpo del bucle
        codigo.append("\n").append(labelCuerpo).append(":\n");
        if (cuerpo != null) {
            for (Nodo sent : cuerpo) {
                codigo.append(sent.generarCodigo());
            }
        }
        codigo.append(String.format("  br label %%%s\n", labelCond));

        // 3. Bloque Alternativo (altWhiles)
        if (this.altWhiles != null && !this.altWhiles.isEmpty()) {
            codigo.append("\n").append(labelFalso).append(":\n");
            for (Nodo sentAlt : altWhiles) {
                codigo.append(sentAlt.generarCodigo());
            }
            codigo.append(String.format("  br label %%%s\n", labelEnd));
        }

        // 4. Fin del bloque
        codigo.append("\n").append(labelEnd).append(":\n");

        CodeGeneratorHelper.exitLoop();
        return codigo.toString();
    }
}