package ast;

import llvm.CodeGeneratorHelper;

public class OperacionAritmetica extends OperacionBinaria {
    private final String operador;

    public OperacionAritmetica(String operador, Expresion izquierda, Expresion derecha) {
        super(izquierda, derecha);
        this.operador = operador;

        if ("BOOL".equals(izquierda.getTipoDato()) || "BOOL".equals(derecha.getTipoDato())) {
            throw new RuntimeException("Error semántico: No se permiten expresiones booleanas en operaciones aritméticas.");
        }
    }

    @Override
    protected String getNombreOperacion() {
        if (getTipoDato() != null && !getTipoDato().equals("UNKNOWN")) {
            return operador + " (" + getTipoDato() + ")";
        }
        return operador;
    }

    @Override
    public String generarCodigo() {
        StringBuilder codigo = new StringBuilder();

        String tipoIzquierda = this.getIzquierda().getTipoDato();
        String tipoDerecha = this.getDerecha().getTipoDato();

        // -----------------------------------------------------------------
        // CASO A: Operaciones que involucran Arreglos (Vectores)
        // -----------------------------------------------------------------
        if ((tipoIzquierda != null && tipoIzquierda.startsWith("FLOAT_ARRAY")) ||
                (tipoDerecha != null && tipoDerecha.startsWith("FLOAT_ARRAY"))) {

            codigo.append(this.getIzquierda().generarCodigo());
            codigo.append(this.getDerecha().generarCodigo());

            String tipoDeclarado = tipoIzquierda.startsWith("FLOAT_ARRAY") ? tipoIzquierda : tipoDerecha;
            int tamaño = 5;
            if (tipoDeclarado.contains("[") && tipoDeclarado.contains("]")) {
                int inicio = tipoDeclarado.indexOf('[') + 1;
                int fin = tipoDeclarado.indexOf(']');
                tamaño = Integer.parseInt(tipoDeclarado.substring(inicio, fin));
            }

            String resArrayPtr = CodeGeneratorHelper.getNewPointer();
            codigo.append(String.format("  %s = alloca [%d x double]\n", resArrayPtr, tamaño));

            String idxPtr = CodeGeneratorHelper.getNewPointer();
            codigo.append(String.format("  %s = alloca i32\n", idxPtr));
            codigo.append(String.format("  store i32 0, i32* %s\n", idxPtr));

            String lblCond = "arr_op_cond_" + CodeGeneratorHelper.getNewLabel();
            String lblCuerpo = "arr_op_body_" + CodeGeneratorHelper.getNewLabel();
            String lblFin = "arr_op_end_" + CodeGeneratorHelper.getNewLabel();

            codigo.append(String.format("  br label %%%s\n", lblCond));

            // EVALUACIÓN DE CONDICIÓN
            codigo.append("\n").append(lblCond).append(":\n");
            String currentIdx = CodeGeneratorHelper.getNewPointer();
            codigo.append(String.format("  %s = load i32, i32* %s\n", currentIdx, idxPtr));
            String cmpReg = CodeGeneratorHelper.getNewPointer();
            codigo.append(String.format("  %s = icmp slt i32 %s, %d\n", cmpReg, currentIdx, tamaño));
            codigo.append(String.format("  br i1 %s, label %%%s, label %%%s\n", cmpReg, lblCuerpo, lblFin));

            // CUERPO DEL BUCLE
            codigo.append("\n").append(lblCuerpo).append(":\n");

            // CORRECCIÓN: Pedir registros estrictamente en orden de uso LLVM
            String valIzq;
            if (tipoIzquierda.startsWith("FLOAT_ARRAY")) {
                String elemPtrIzq = CodeGeneratorHelper.getNewPointer(); // Pide el %11
                valIzq = CodeGeneratorHelper.getNewPointer();            // Pide el %12
                codigo.append(String.format("  %s = getelementptr double, double* %s, i32 %s\n",
                        elemPtrIzq, this.getIzquierda().getIrRef(), currentIdx));
                codigo.append(String.format("  %s = load double, double* %s\n", valIzq, elemPtrIzq));
            } else {
                valIzq = CodeGeneratorHelper.getNewPointer();
                codigo.append(String.format("  %s = fadd double 0.0, %s\n", valIzq, this.getIzquierda().getIrRef()));
            }

            // CORRECCIÓN: Pedir registros estrictamente en orden de uso LLVM
            String valDer;
            if (tipoDerecha.startsWith("FLOAT_ARRAY")) {
                String elemPtrDer = CodeGeneratorHelper.getNewPointer(); // Pide el %13
                valDer = CodeGeneratorHelper.getNewPointer();            // Pide el %14
                codigo.append(String.format("  %s = getelementptr double, double* %s, i32 %s\n",
                        elemPtrDer, this.getDerecha().getIrRef(), currentIdx));
                codigo.append(String.format("  %s = load double, double* %s\n", valDer, elemPtrDer));
            } else {
                valDer = CodeGeneratorHelper.getNewPointer();
                codigo.append(String.format("  %s = fadd double 0.0, %s\n", valDer, this.getDerecha().getIrRef()));
            }

            String opLLVM = "fadd";
            switch(operador) {
                case "-": opLLVM = "fsub"; break;
                case "*": opLLVM = "fmul"; break;
                case "/": opLLVM = "fdiv"; break;
            }
            String resOpScalar = CodeGeneratorHelper.getNewPointer();
            codigo.append(String.format("  %s = %s double %s, %s\n", resOpScalar, opLLVM, valIzq, valDer));

            String destElemPtr = CodeGeneratorHelper.getNewPointer();
            codigo.append(String.format("  %s = getelementptr [%d x double], [%d x double]* %s, i32 0, i32 %s\n",
                    destElemPtr, tamaño, tamaño, resArrayPtr, currentIdx));
            codigo.append(String.format("  store double %s, double* %s\n", resOpScalar, destElemPtr));

            String nextIdx = CodeGeneratorHelper.getNewPointer();
            codigo.append(String.format("  %s = add i32 %s, 1\n", nextIdx, currentIdx));
            codigo.append(String.format("  store i32 %s, i32* %s\n", nextIdx, idxPtr));
            codigo.append(String.format("  br label %%%s\n", lblCond));

            // FIN DEL BUCLE
            codigo.append("\n").append(lblFin).append(":\n");
            this.setIrRef(CodeGeneratorHelper.getNewPointer());
            codigo.append(String.format("  %s = getelementptr [%d x double], [%d x double]* %s, i32 0, i32 0\n",
                    this.getIrRef(), tamaño, tamaño, resArrayPtr));

            return codigo.toString();
        }

        // -----------------------------------------------------------------
        // CASO B: Operaciones Escalares Estándar
        // -----------------------------------------------------------------
        codigo.append(this.getIzquierda().generarCodigo());
        codigo.append(this.getDerecha().generarCodigo());

        this.setIrRef(CodeGeneratorHelper.getNewPointer());

        String tipoOperandos = this.getIzquierda().getTipoDato();
        String typeLLVM = CodeGeneratorHelper.mapearTipoLLVM(tipoOperandos);

        String opLLVM;
        boolean esFloat = tipoOperandos != null && tipoOperandos.equals("FLOAT");
        switch(operador) {
            case "+": opLLVM = esFloat ? "fadd" : "add"; break;
            case "-": opLLVM = esFloat ? "fsub" : "sub"; break;
            case "*": opLLVM = esFloat ? "fmul" : "mul"; break;
            case "/": opLLVM = esFloat ? "fdiv" : "sdiv"; break;
            default: opLLVM = "unknown_op";
        }

        codigo.append(String.format("  %s = %s %s %s, %s\n",
                this.getIrRef(), opLLVM, typeLLVM,
                this.getIzquierda().getIrRef(), this.getDerecha().getIrRef()));

        return codigo.toString();
    }
}