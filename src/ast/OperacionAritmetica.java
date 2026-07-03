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

        // 1. Generamos el código de los hijos primero
        codigo.append(this.getIzquierda().generarCodigo());
        codigo.append(this.getDerecha().generarCodigo());

        String tipoIzquierda = this.getIzquierda().getTipoDato();
        String tipoDerecha = this.getDerecha().getTipoDato();

        // -----------------------------------------------------------------
        // CASO A: Operaciones que involucran Arreglos (Vectores)
        // -----------------------------------------------------------------
        if ((tipoIzquierda != null && tipoIzquierda.startsWith("FLOAT_ARRAY")) ||
                (tipoDerecha != null && tipoDerecha.startsWith("FLOAT_ARRAY"))) {

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

            // PROCESAR OPERANDO IZQUIERDO (Con Auto-Cast)
            String valIzq;
            if (tipoIzquierda != null && tipoIzquierda.startsWith("FLOAT_ARRAY")) {
                String elemPtrIzq = CodeGeneratorHelper.getNewPointer();
                valIzq = CodeGeneratorHelper.getNewPointer();
                codigo.append(String.format("  %s = getelementptr double, double* %s, i32 %s\n",
                        elemPtrIzq, this.getIzquierda().getIrRef(), currentIdx));
                codigo.append(String.format("  %s = load double, double* %s\n", valIzq, elemPtrIzq));
            } else {
                String refIzq = this.getIzquierda().getIrRef();
                // Coerción Implícita de INT a DOUBLE
                if ("INT".equals(tipoIzquierda)) {
                    if (!refIzq.startsWith("%")) {
                        refIzq += ".0"; // Constante (ej. 10 -> 10.0)
                    } else {
                        String castReg = CodeGeneratorHelper.getNewPointer();
                        codigo.append(String.format("  %s = sitofp i32 %s to double\n", castReg, refIzq));
                        refIzq = castReg;
                    }
                }
                valIzq = CodeGeneratorHelper.getNewPointer();
                codigo.append(String.format("  %s = fadd double 0.0, %s\n", valIzq, refIzq));
            }

            // PROCESAR OPERANDO DERECHO (Con Auto-Cast)
            String valDer;
            if (tipoDerecha != null && tipoDerecha.startsWith("FLOAT_ARRAY")) {
                String elemPtrDer = CodeGeneratorHelper.getNewPointer();
                valDer = CodeGeneratorHelper.getNewPointer();
                codigo.append(String.format("  %s = getelementptr double, double* %s, i32 %s\n",
                        elemPtrDer, this.getDerecha().getIrRef(), currentIdx));
                codigo.append(String.format("  %s = load double, double* %s\n", valDer, elemPtrDer));
            } else {
                String refDer = this.getDerecha().getIrRef();
                // Coerción Implícita de INT a DOUBLE
                if ("INT".equals(tipoDerecha)) {
                    if (!refDer.startsWith("%")) {
                        refDer += ".0"; // Constante (ej. 10 -> 10.0)
                    } else {
                        String castReg = CodeGeneratorHelper.getNewPointer();
                        codigo.append(String.format("  %s = sitofp i32 %s to double\n", castReg, refDer));
                        refDer = castReg;
                    }
                }
                valDer = CodeGeneratorHelper.getNewPointer();
                codigo.append(String.format("  %s = fadd double 0.0, %s\n", valDer, refDer));
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
        // CASO B: Operaciones Escalares Estándar (Con Auto-Cast)
        // -----------------------------------------------------------------
        boolean hayFloat = "FLOAT".equals(tipoIzquierda) || "FLOAT".equals(tipoDerecha);
        String refIzq = this.getIzquierda().getIrRef();
        String refDer = this.getDerecha().getIrRef();

        if (hayFloat) {
            // Promoción del lado izquierdo si es INT
            if ("INT".equals(tipoIzquierda)) {
                if (!refIzq.startsWith("%")) {
                    refIzq += ".0";
                } else {
                    String castReg = CodeGeneratorHelper.getNewPointer();
                    codigo.append(String.format("  %s = sitofp i32 %s to double\n", castReg, refIzq));
                    refIzq = castReg;
                }
            }

            // Promoción del lado derecho si es INT
            if ("INT".equals(tipoDerecha)) {
                if (!refDer.startsWith("%")) {
                    refDer += ".0";
                } else {
                    String castReg = CodeGeneratorHelper.getNewPointer();
                    codigo.append(String.format("  %s = sitofp i32 %s to double\n", castReg, refDer));
                    refDer = castReg;
                }
            }

            this.setIrRef(CodeGeneratorHelper.getNewPointer());
            String opLLVM;
            switch(operador) {
                case "+": opLLVM = "fadd"; break;
                case "-": opLLVM = "fsub"; break;
                case "*": opLLVM = "fmul"; break;
                case "/": opLLVM = "fdiv"; break;
                default: opLLVM = "unknown_op";
            }
            codigo.append(String.format("  %s = %s double %s, %s\n",
                    this.getIrRef(), opLLVM, refIzq, refDer));

        } else {
            // Operación puramente entera
            this.setIrRef(CodeGeneratorHelper.getNewPointer());
            String opLLVM;
            switch(operador) {
                case "+": opLLVM = "add"; break;
                case "-": opLLVM = "sub"; break;
                case "*": opLLVM = "mul"; break;
                case "/": opLLVM = "sdiv"; break;
                default: opLLVM = "unknown_op";
            }
            codigo.append(String.format("  %s = %s i32 %s, %s\n",
                    this.getIrRef(), opLLVM, refIzq, refDer));
        }

        return codigo.toString();
    }
}