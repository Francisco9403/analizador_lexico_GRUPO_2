package ast;

import llvm.CodeGeneratorHelper;

public class Read extends Expresion {
    private final String tipoRead;

    public Read(String tipoRead) {
        this.tipoRead = tipoRead;
        this.setTipoDato(tipoRead);
    }

    @Override
    public String getEtiqueta() {
        if (getTipoDato() != null && !getTipoDato().equals("UNKNOWN")) {
            return "READ: " + tipoRead + " (" + getTipoDato() + ")";
        }
        return "READ_" + tipoRead;
    }

    @Override
    public String generarCodigo() {
        StringBuilder codigo = new StringBuilder();

        // 1. Reservamos el espacio nativo en la pila (double para FLOAT, i32 para INT/BOOL)
        String ptrTemporal = CodeGeneratorHelper.getNewPointer();
        String tipoLLVM = "FLOAT".equals(tipoRead) ? "double" : "i32";

        codigo.append(String.format("  %s = alloca %s\n", ptrTemporal, tipoLLVM));

        // 2. Llamamos a scanf usando las cadenas NUEVAS exclusivas para lectura
        String refScanf = CodeGeneratorHelper.getNewPointer();
        if ("INT".equals(tipoRead) || "BOOL".equals(tipoRead)) {
            codigo.append(String.format("  %s = call i32 (i8*, ...) @scanf(i8* getelementptr inbounds ([3 x i8], [3 x i8]* @.str.read_int, i32 0, i32 0), i32* %s)\n",
                    refScanf, ptrTemporal));
        } else if ("FLOAT".equals(tipoRead)) {
            codigo.append(String.format("  %s = call i32 (i8*, ...) @scanf(i8* getelementptr inbounds ([4 x i8], [4 x i8]* @.str.read_float, i32 0, i32 0), double* %s)\n",
                    refScanf, ptrTemporal));
        }

        // 3. Cargamos el valor y ajustamos el bit si es un booleano
        if ("BOOL".equals(tipoRead)) {
            String valLeido = CodeGeneratorHelper.getNewPointer();
            codigo.append(String.format("  %s = load i32, i32* %s\n", valLeido, ptrTemporal));

            this.setIrRef(CodeGeneratorHelper.getNewPointer());
            codigo.append(String.format("  %s = icmp ne i32 %s, 0\n", this.getIrRef(), valLeido));
        } else {
            this.setIrRef(CodeGeneratorHelper.getNewPointer());
            codigo.append(String.format("  %s = load %s, %s* %s\n",
                    this.getIrRef(), tipoLLVM, tipoLLVM, ptrTemporal));
        }

        return codigo.toString();
    }
}