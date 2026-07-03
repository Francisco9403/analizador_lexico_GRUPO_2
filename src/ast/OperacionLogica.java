package ast;

import llvm.CodeGeneratorHelper;

public class OperacionLogica extends OperacionBinaria {
    private final String operador;

    public OperacionLogica(String operador, Expresion izquierda, Expresion derecha) {
        super(izquierda, derecha);
        this.operador = operador;

        if (!"BOOL".equals(izquierda.getTipoDato()) || !"BOOL".equals(derecha.getTipoDato())) {
            throw new RuntimeException("Error semántico: Los operadores lógicos ('!','&&', '||') solo aceptan operandos de tipo BOOL.");
        }
        this.setTipoDato("BOOL");
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

            // Pedimos el puntero para el alloca al principio (porque se escribe ahora mismo)
            String ptrRes = CodeGeneratorHelper.getNewPointer();

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

            // --- ACÁ ESTÁ LA MAGIA ---
            // Pedimos el turno justo en el momento en que lo vamos a inyectar al String
            String regFinal = CodeGeneratorHelper.getNewPointer();
            codigo.append(String.format("  %s = load i1, i1* %s\n", regFinal, ptrRes));

            this.setIrRef(regFinal);
            return codigo.toString();
        }

        // --- CORTOCIRCUITO OR (||) ---
        if (operador.equals("||")) {
            String lblEvalDer = CodeGeneratorHelper.getNewLabel();
            String lblFin = CodeGeneratorHelper.getNewLabel();

            // Pedimos el puntero para el alloca al principio
            String ptrRes = CodeGeneratorHelper.getNewPointer();

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

            // Pedimos el turno justo en el momento en que lo vamos a inyectar al String
            String regFinal = CodeGeneratorHelper.getNewPointer();
            codigo.append(String.format("  %s = load i1, i1* %s\n", regFinal, ptrRes));

            this.setIrRef(regFinal);
            return codigo.toString();
        }

        return "";
    }
}