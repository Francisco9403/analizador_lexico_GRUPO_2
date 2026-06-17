package ast;

import llvm.CodeGeneratorHelper;
import java.util.List;
import java.util.ArrayList;

public class Impresion extends Nodo {
    private final String stringOpcional;
    private final Expresion expresion;

    // 1. Imprime solo una variable
    public Impresion(Expresion expresion) {
        this.stringOpcional = null;
        this.expresion = expresion;
    }

    // 2. Imprime un texto Y una variable
    public Impresion(String stringOpcional, Expresion expresion) {
        this.stringOpcional = stringOpcional;
        this.expresion = expresion;
    }

    // 3. Imprime SOLO un texto
    public Impresion(String stringSolo) {
        this.stringOpcional = stringSolo;
        this.expresion = null;
    }

    @Override
    public String getEtiqueta() {
        if (stringOpcional != null && expresion != null) return "PRINT(" + stringOpcional + ", ...)";
        if (stringOpcional != null && expresion == null) return "PRINT(" + stringOpcional + ")";
        return "PRINT";
    }

    @Override
    public List<Nodo> getHijos() {
        // Si no hay expresión matemática colgando, devolvemos una lista vacía para Graphviz
        return expresion != null ? List.of(expresion) : new ArrayList<>();
    }

    @Override
    public String generarCodigo() {
        StringBuilder codigo = new StringBuilder();

        // --- CASO A: ES SOLO UN TEXTO PURO ---
        if (expresion == null) {
            String formatStr = stringOpcional.replace("\"", "");
            String[] globalData = CodeGeneratorHelper.addGlobalString(formatStr).split("\\|\\|");
            String globalRef = globalData[0];
            String size = globalData[1];

            // Pedimos el ID justo en el momento en que lo vamos a usar
            String refPrint = CodeGeneratorHelper.getNewPointer();

            codigo.append(String.format("  %s = call i32 (i8*, ...) @printf(i8* getelementptr inbounds ([%s x i8], [%s x i8]* %s, i32 0, i32 0))\n",
                    refPrint, size, size, globalRef));

            return codigo.toString();
        }

        // --- CASO B: HAY UNA VARIABLE (CON O SIN TEXTO) ---
        // 1. PRIMERO generamos el código de la expresión (que consumirá sus propios IDs numéricos)
        codigo.append(expresion.generarCodigo());
        String regValor = expresion.getIrRef();
        String tipoDato = expresion.getTipoDato();

        // 2. DESPUÉS pedimos el ID para el Print (así nos aseguramos de que sea el número mayor)
        String refPrint = CodeGeneratorHelper.getNewPointer();

        if (stringOpcional != null) {
            String formatStr = stringOpcional.replace("\"", "");
            formatStr += "INT".equals(tipoDato) ? " %d" : " %f";

            String[] globalData = CodeGeneratorHelper.addGlobalString(formatStr).split("\\|\\|");
            String globalRef = globalData[0];
            String size = globalData[1];
            String tipoC = "INT".equals(tipoDato) ? "i32" : "double";

            codigo.append(String.format("  %s = call i32 (i8*, ...) @printf(i8* getelementptr inbounds ([%s x i8], [%s x i8]* %s, i32 0, i32 0), %s %s)\n",
                    refPrint, size, size, globalRef, tipoC, regValor));
        } else {
            if ("INT".equals(tipoDato)) {
                codigo.append(String.format("  %s = call i32 (i8*, ...) @printf(i8* getelementptr inbounds ([4 x i8], [4 x i8]* @.str.int, i32 0, i32 0), i32 %s)\n", refPrint, regValor));
            } else if ("FLOAT".equals(tipoDato)) {
                codigo.append(String.format("  %s = call i32 (i8*, ...) @printf(i8* getelementptr inbounds ([4 x i8], [4 x i8]* @.str.float, i32 0, i32 0), double %s)\n", refPrint, regValor));
            }
        }

        return codigo.toString();
    }
}