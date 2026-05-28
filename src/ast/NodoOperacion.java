package ast;
import llvm.CodeGeneratorHelper;
import java.util.List;

public class NodoOperacion extends NodoC {
    private final String operador;
    private final NodoC izquierda;
    private final NodoC derecha;

    public NodoOperacion(String operador, NodoC izquierda, NodoC derecha) {
        this.operador = operador;
        this.izquierda = izquierda;
        this.derecha = derecha;
    }

    public NodoC getIzquierda() { return izquierda; }
    public NodoC getDerecha() { return derecha; }
    public String getOperador() { return operador; }

    @Override
    public String getEtiqueta() {
        if (getTipoDato() != null && !getTipoDato().equals("UNKNOWN")) {
            return operador + " (" + getTipoDato() + ")";
        }
        return operador;
    }

    @Override
    public List<NodoC> getHijos() { return List.of(izquierda, derecha); }

    @Override
    public String generarCodigo() {
        StringBuilder codigo = new StringBuilder();

        codigo.append(this.getIzquierda().generarCodigo());
        codigo.append(this.getDerecha().generarCodigo());

        this.setIrRef(CodeGeneratorHelper.getNewPointer());

        String tipoOperandos = this.getIzquierda().getTipoDato();
        String typeLLVM = CodeGeneratorHelper.mapearTipoLLVM(tipoOperandos);

        // Transformación directa: forzar 1 bit para compuertas lógicas
        if (operador.equals("&&") || operador.equals("||")) {
            typeLLVM = "i1";
        }

        String opLLVM = get_llvm_op_code(this.getOperador(), tipoOperandos);

        codigo.append(String.format("  %s = %s %s %s, %s\n",
                this.getIrRef(), opLLVM, typeLLVM,
                this.getIzquierda().getIrRef(), this.getDerecha().getIrRef()));

        return codigo.toString();
    }

    private String get_llvm_op_code(String operador, String tipoOperandos) {
        boolean esFloat = tipoOperandos != null && tipoOperandos.equals("FLOAT");

        switch(operador) {
            case "+": return esFloat ? "fadd" : "add";
            case "-": return esFloat ? "fsub" : "sub";
            case "*": return esFloat ? "fmul" : "mul";
            case "/": return esFloat ? "fdiv" : "sdiv";
            case "==": return esFloat ? "fcmp oeq" : "icmp eq";
            case "!=": return esFloat ? "fcmp one" : "icmp ne";
            case ">":  return esFloat ? "fcmp ogt" : "icmp sgt";
            case "<":  return esFloat ? "fcmp olt" : "icmp slt";
            case ">=": return esFloat ? "fcmp oge" : "icmp sge";
            case "<=": return esFloat ? "fcmp ole" : "icmp sle";
            case "&&": return "and";
            case "||": return "or";
            default: return "unknown_op";
        }
    }
}