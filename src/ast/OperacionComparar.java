package ast;

import llvm.CodeGeneratorHelper;

public class OperacionComparar extends OperacionBinaria {
    private final String operador;

    public OperacionComparar(String operador, Expresion izquierda, Expresion derecha) {
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

        codigo.append(this.getIzquierda().generarCodigo());
        codigo.append(this.getDerecha().generarCodigo());

        this.setIrRef(CodeGeneratorHelper.getNewPointer());

        String tipoOperandos = this.getIzquierda().getTipoDato();
        String typeLLVM = CodeGeneratorHelper.mapearTipoLLVM(tipoOperandos);

        String opLLVM;
        boolean esFloat = tipoOperandos != null && tipoOperandos.equals("FLOAT");
        switch(operador) {
            case "==": opLLVM = esFloat ? "fcmp oeq" : "icmp eq"; break;
            case "!=": opLLVM = esFloat ? "fcmp one" : "icmp ne"; break;
            case ">":  opLLVM = esFloat ? "fcmp ogt" : "icmp sgt"; break;
            case "<":  opLLVM = esFloat ? "fcmp olt" : "icmp slt"; break;
            case ">=": opLLVM = esFloat ? "fcmp oge" : "icmp sge"; break;
            case "<=": opLLVM = esFloat ? "fcmp ole" : "icmp sle"; break;
            default: opLLVM = "unknown_op";
        }

        codigo.append(String.format("  %s = %s %s %s, %s\n",
                this.getIrRef(), opLLVM, typeLLVM,
                this.getIzquierda().getIrRef(), this.getDerecha().getIrRef()));

        return codigo.toString();
    }
}