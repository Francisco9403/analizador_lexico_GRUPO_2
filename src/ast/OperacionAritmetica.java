package ast;

import llvm.CodeGeneratorHelper;

public class OperacionAritmetica extends OperacionBinaria {
    private final String operador;

    public OperacionAritmetica(String operador, Expresion izquierda, Expresion derecha) {
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

        // Evaluar hijos primero (Post-orden standard)
        codigo.append(this.getIzquierda().generarCodigo());
        codigo.append(this.getDerecha().generarCodigo());

        this.setIrRef(CodeGeneratorHelper.getNewPointer());

        String tipoOperandos = this.getIzquierda().getTipoDato();
        String typeLLVM = CodeGeneratorHelper.mapearTipoLLVM(tipoOperandos);

        // Mapeo local exclusivo de aritmética
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