package ast;

import java.util.List;
import java.util.ArrayList;

public class NodoImpresion extends NodoC {
    private final String stringOpcional;
    private final NodoC expresion;

    // Constructor para: PRINT(expresion)
    public NodoImpresion(NodoC expresion) {
        this.stringOpcional = null;
        this.expresion = expresion;
    }

    // Constructor para: PRINT("string", expresion)
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

        // 1. Generar código de lo que queremos imprimir
        codigo.append(expresion.generarCodigo());

        // 2. Definir el tipo y el formato según la variable
        String regValor = expresion.getIrRef();
        String tipoDato = expresion.getTipoDato();

        if ("INT".equals(tipoDato)) {
            // LLVM IR para printf de enteros
            codigo.append(String.format("  call i32 (i8*, ...) @printf(i8* getelementptr inbounds ([4 x i8], [4 x i8]* @.str.int, i32 0, i32 0), i32 %s)\n", regValor));
        } else if ("FLOAT".equals(tipoDato)) {
            // LLVM IR para printf de flotantes (double)
            codigo.append(String.format("  call i32 (i8*, ...) @printf(i8* getelementptr inbounds ([4 x i8], [4 x i8]* @.str.float, i32 0, i32 0), double %s)\n", regValor));
        }

        return codigo.toString();
    }

}