package ast;

import llvm.CodeGeneratorHelper;

public class OperacionLogica extends OperacionBinaria {
    private final String operador;

    public OperacionLogica(String operador, Expresion izquierda, Expresion derecha) {
        super(izquierda, derecha);
        this.operador = operador;
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

        // --- CORTOCIRCUITO AND (&&) ---
        if (operador.equals("&&")) {
            String lblEvalDer = CodeGeneratorHelper.getNewLabel();
            String lblFin = CodeGeneratorHelper.getNewLabel();
            String ptrRes = CodeGeneratorHelper.getNewPointer();
            String regFinal = CodeGeneratorHelper.getNewPointer();

            codigo.append(String.format("  %s = alloca i1\n", ptrRes));
            codigo.append(String.format("  store i1 false, i1* %s\n", ptrRes));

            codigo.append(this.getIzquierda().generarCodigo());

            codigo.append(String.format("  br i1 %s, label %%%s, label %%%s\n",
                    this.getIzquierda().getIrRef(), lblEvalDer, lblFin));

            codigo.append("\n").append(lblEvalDer).append(":\n");
            codigo.append(this.getDerecha().generarCodigo());
            codigo.append(String.format("  store i1 %s, i1* %s\n", this.getDerecha().getIrRef(), ptrRes));
            codigo.append(String.format("  br label %%%s\n", lblFin));

            codigo.append("\n").append(lblFin).append(":\n");
            codigo.append(String.format("  %s = load i1, i1* %s\n", regFinal, ptrRes));

            this.setIrRef(regFinal);
            return codigo.toString();
        }

        // --- CORTOCIRCUITO OR (||) ---
        if (operador.equals("||")) {
            String lblEvalDer = CodeGeneratorHelper.getNewLabel();
            String lblFin = CodeGeneratorHelper.getNewLabel();
            String ptrRes = CodeGeneratorHelper.getNewPointer();
            String regFinal = CodeGeneratorHelper.getNewPointer();

            codigo.append(String.format("  %s = alloca i1\n", ptrRes));
            codigo.append(String.format("  store i1 true, i1* %s\n", ptrRes));

            codigo.append(this.getIzquierda().generarCodigo());

            codigo.append(String.format("  br i1 %s, label %%%s, label %%%s\n",
                    this.getIzquierda().getIrRef(), lblFin, lblEvalDer));

            codigo.append("\n").append(lblEvalDer).append(":\n");
            codigo.append(this.getDerecha().generarCodigo());
            codigo.append(String.format("  store i1 %s, i1* %s\n", this.getDerecha().getIrRef(), ptrRes));
            codigo.append(String.format("  br label %%%s\n", lblFin));

            codigo.append("\n").append(lblFin).append(":\n");
            codigo.append(String.format("  %s = load i1, i1* %s\n", regFinal, ptrRes));

            this.setIrRef(regFinal);
            return codigo.toString();
        }

        return "";
    }
}