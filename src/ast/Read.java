package ast;

import llvm.CodeGeneratorHelper;

public class Read extends Expresion {
    private final String tipoRead; // "INT", "FLOAT" o "BOOL"

    public Read(String tipoRead) {
        this.tipoRead = tipoRead;
        this.setTipoDato(tipoRead); // Le asignamos el tipo de dato inmediatamente
    }

    @Override
    public String getEtiqueta() {
        return "READ_" + tipoRead;
    }

    @Override
    public String generarCodigo() {
        StringBuilder codigo = new StringBuilder();

        // 1. Necesitamos un puntero temporal en la pila para que scanf guarde el valor
        String ptrTemporal = CodeGeneratorHelper.getNewPointer();
        String tipoLLVM = "FLOAT".equals(tipoRead) ? "double" : "i32";

        codigo.append(String.format("  %s = alloca %s\n", ptrTemporal, tipoLLVM));

        // 2. Llamar a scanf usando el formato correcto de entrada
        String refScanf = CodeGeneratorHelper.getNewPointer();
        if ("INT".equals(tipoRead) || "BOOL".equals(tipoRead)) {
            // Reutilizamos @.str.int de formato o definís un @.str.scan.int sin el \n si preferís
            codigo.append(String.format("  %s = call i32 (i8*, ...) @scanf(i8* getelementptr inbounds ([4 x i8], [4 x i8]* @.str.int, i32 0, i32 0), i32* %s)\n",
                    refScanf, ptrTemporal));
        } else if ("FLOAT".equals(tipoRead)) {
            codigo.append(String.format("  %s = call i32 (i8*, ...) @scanf(i8* getelementptr inbounds ([4 x i8], [4 x i8]* @.str.float, i32 0, i32 0), double* %s)\n",
                    refScanf, ptrTemporal));
        }

        // 3. Cargamos el valor del puntero temporal a un registro final para que la expresión lo devuelva
        this.setIrRef(CodeGeneratorHelper.getNewPointer());
        codigo.append(String.format("  %s = load %s, %s* %s\n",
                this.getIrRef(), tipoLLVM, tipoLLVM, ptrTemporal));

        return codigo.toString();
    }
}