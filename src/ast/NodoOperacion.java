package ast;
import llvm.CodeGeneratorHelper;
import java.util.List;

public class NodoOperacion extends Expresion {
    private final String operador;
    private final Expresion izquierda;
    private final Expresion derecha;

    public NodoOperacion(String operador, Expresion izquierda, Expresion derecha) {
        this.operador = operador;
        this.izquierda = izquierda;
        this.derecha = derecha;
    }

    public Expresion getIzquierda() { return izquierda; }
    public Expresion getDerecha() { return derecha; }
    public String getOperador() { return operador; }

    @Override
    public String getEtiqueta() {
        if (getTipoDato() != null && !getTipoDato().equals("UNKNOWN")) {
            return operador + " (" + getTipoDato() + ")";
        }
        return operador;
    }

    @Override
    public List<Nodo> getHijos() { return List.of(izquierda, derecha); }

    @Override
    public String generarCodigo() {
        StringBuilder codigo = new StringBuilder();

        // =========================================================
        // 1. LÓGICA DE CORTOCIRCUITO PARA AND (&&)
        // =========================================================
        if (operador.equals("&&")) {
            String lblEvalDer = CodeGeneratorHelper.getNewLabel();
            String lblFin = CodeGeneratorHelper.getNewLabel();
            String ptrRes = CodeGeneratorHelper.getNewPointer();
            String regFinal = CodeGeneratorHelper.getNewPointer();

            // Reservar memoria y asumir FALSE por defecto
            codigo.append(String.format("  %s = alloca i1\n", ptrRes));
            codigo.append(String.format("  store i1 false, i1* %s\n", ptrRes));

            // Evaluar lado izquierdo
            codigo.append(this.getIzquierda().generarCodigo());

            // Si Izq es TRUE, salto a evaluar Der. Si es FALSE, corto y salto al fin.
            codigo.append(String.format("  br i1 %s, label %%%s, label %%%s\n",
                    this.getIzquierda().getIrRef(), lblEvalDer, lblFin));

            // Evaluar lado derecho
            codigo.append("\n").append(lblEvalDer).append(":\n");
            codigo.append(this.getDerecha().generarCodigo());
            codigo.append(String.format("  store i1 %s, i1* %s\n", this.getDerecha().getIrRef(), ptrRes));
            codigo.append(String.format("  br label %%%s\n", lblFin));

            // Bloque final
            codigo.append("\n").append(lblFin).append(":\n");
            codigo.append(String.format("  %s = load i1, i1* %s\n", regFinal, ptrRes));

            this.setIrRef(regFinal);
            return codigo.toString();
        }

        // =========================================================
        // 2. LÓGICA DE CORTOCIRCUITO PARA OR (||)
        // =========================================================
        if (operador.equals("||")) {
            String lblEvalDer = CodeGeneratorHelper.getNewLabel();
            String lblFin = CodeGeneratorHelper.getNewLabel();
            String ptrRes = CodeGeneratorHelper.getNewPointer();
            String regFinal = CodeGeneratorHelper.getNewPointer();

            // Reservar memoria y asumir TRUE por defecto
            codigo.append(String.format("  %s = alloca i1\n", ptrRes));
            codigo.append(String.format("  store i1 true, i1* %s\n", ptrRes));

            // Evaluar lado izquierdo
            codigo.append(this.getIzquierda().generarCodigo());

            // Si Izq es TRUE, corto y salto al fin. Si es FALSE, salto a evaluar Der.
            codigo.append(String.format("  br i1 %s, label %%%s, label %%%s\n",
                    this.getIzquierda().getIrRef(), lblFin, lblEvalDer));

            // Evaluar lado derecho
            codigo.append("\n").append(lblEvalDer).append(":\n");
            codigo.append(this.getDerecha().generarCodigo());
            codigo.append(String.format("  store i1 %s, i1* %s\n", this.getDerecha().getIrRef(), ptrRes));
            codigo.append(String.format("  br label %%%s\n", lblFin));

            // Bloque final
            codigo.append("\n").append(lblFin).append(":\n");
            codigo.append(String.format("  %s = load i1, i1* %s\n", regFinal, ptrRes));

            this.setIrRef(regFinal);
            return codigo.toString();
        }

        // =========================================================
        // 3. LÓGICA NORMAL PARA MATEMÁTICAS Y COMPARACIONES
        // =========================================================
        codigo.append(this.getIzquierda().generarCodigo());
        codigo.append(this.getDerecha().generarCodigo());

        this.setIrRef(CodeGeneratorHelper.getNewPointer());

        String tipoOperandos = this.getIzquierda().getTipoDato();
        String typeLLVM = CodeGeneratorHelper.mapearTipoLLVM(tipoOperandos);

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