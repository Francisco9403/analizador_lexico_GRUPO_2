package ast;

import llvm.CodeGeneratorHelper;
import java.util.List;

public class Impresion extends Nodo {
    private final String stringOpcional;
    private final Expresion expresion;

    public Impresion(Expresion expresion) {
        this.stringOpcional = null;
        this.expresion = expresion;
    }

    public Impresion(String stringOpcional, Expresion expresion) {
        this.stringOpcional = stringOpcional;
        this.expresion = expresion;
    }

    @Override
    public String getEtiqueta() {
        return stringOpcional != null ? "PRINT(" + stringOpcional + ", ...)" : "PRINT";
    }

    @Override
    public List<Nodo> getHijos() {
        return List.of(expresion);
    }

    @Override
    public String generarCodigo() {
        StringBuilder codigo = new StringBuilder();
        codigo.append(expresion.generarCodigo());

        String regValor = expresion.getIrRef();
        String tipoDato = expresion.getTipoDato();
        String refPrint = CodeGeneratorHelper.getNewPointer();

        // Si tenemos un string opcional, creamos su formato global
        if (stringOpcional != null) {
            String formatStr = stringOpcional.replace("\"", "");
            // Le pegamos el formato numérico al final del string del usuario
            formatStr += "INT".equals(tipoDato) ? " %d" : " %f";

            // Registramos el string completo como global
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