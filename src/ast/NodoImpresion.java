package ast;

import llvm.CodeGeneratorHelper;
import java.util.List;

public class NodoImpresion extends NodoC {
    private final String stringOpcional;
    private final NodoC expresion;

    public NodoImpresion(NodoC expresion) {
        this.stringOpcional = null;
        this.expresion = expresion;
    }

    public NodoImpresion(String stringOpcional, NodoC expresion) {
        this.stringOpcional = stringOpcional;
        this.expresion = expresion;
    }

    @Override
    public String getEtiqueta() {
        return stringOpcional != null ? "PRINT(" + stringOpcional + ", ...)" : "PRINT";
    }

    @Override
    public List<NodoC> getHijos() {
        return List.of(expresion);
    }

    @Override
    public String generarCodigo() {
        StringBuilder codigo = new StringBuilder();

        codigo.append(expresion.generarCodigo());

        String regValor = expresion.getIrRef();
        String tipoDato = expresion.getTipoDato();

        // ¡LA SOLUCIÓN! Reservamos explícitamente el contador para el resultado de printf
        String refPrint = CodeGeneratorHelper.getNewPointer();

        if ("INT".equals(tipoDato)) {
            codigo.append(String.format("  %s = call i32 (i8*, ...) @printf(i8* getelementptr inbounds ([4 x i8], [4 x i8]* @.str.int, i32 0, i32 0), i32 %s)\n", refPrint, regValor));
        } else if ("FLOAT".equals(tipoDato)) {
            codigo.append(String.format("  %s = call i32 (i8*, ...) @printf(i8* getelementptr inbounds ([4 x i8], [4 x i8]* @.str.float, i32 0, i32 0), double %s)\n", refPrint, regValor));
        }

        return codigo.toString();
    }
}