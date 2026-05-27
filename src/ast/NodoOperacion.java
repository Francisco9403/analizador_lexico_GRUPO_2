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
        // Mostramos el operador y, si el parser ya le seteó el tipo, se lo agregamos
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


        // El tipo de los datos que se van a operar/comparar
        String tipoOperandos = this.getIzquierda().getTipoDato();
        String typeLLVM = CodeGeneratorHelper.mapearTipoLLVM(tipoOperandos);

        String opLLVM = get_llvm_op_code(this.getOperador(), this.getTipoDato());

        // 4. Emitir la instrucción
        // Ej: %3 = add i32 %1, %2
        codigo.append(String.format("  %s = %s %s %s, %s\n",
                this.getIrRef(), opLLVM, typeLLVM,
                this.getIzquierda().getIrRef(), this.getDerecha().getIrRef()));

        return codigo.toString();
    }

    // Método auxiliar para mapear el símbolo (+, -) al comando de LLVM
    private String get_llvm_op_code(String operador, String tipoDato) {
        boolean esFloat = tipoDato.equals("FLOAT");
        switch(operador) {
            case "+": return esFloat ? "fadd" : "add";
            case "-": return esFloat ? "fsub" : "sub";
            case "*": return esFloat ? "fmul" : "mul";
            case "/": return esFloat ? "fdiv" : "sdiv";
            default: return "unknown_op";
        }
    }
}