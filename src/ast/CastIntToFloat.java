package ast;

import llvm.CodeGeneratorHelper;
import java.util.List;

public class CastIntToFloat extends Expresion {
    private final Expresion expresionInt;

    public CastIntToFloat(Expresion expresionInt) {
        this.expresionInt = expresionInt;
        this.setTipoDato("FLOAT");
    }

    @Override
    public String getEtiqueta() {
        return "CAST\n(int -> float)";
    }

    @Override
    public List<Nodo> getHijos() {
        return List.of(expresionInt);
    }

    @Override
    public String generarCodigo() {
        StringBuilder codigo = new StringBuilder();

        // 1. Generar el código de la expresión entera original
        codigo.append(expresionInt.generarCodigo());

        // 2. Crear un nuevo registro para guardar el valor ya convertido
        this.setIrRef(CodeGeneratorHelper.getNewPointer());

        // 3. Emitir la instrucción de casteo de LLVM (sitofp)
        codigo.append(String.format("  %s = sitofp i32 %s to double\n",
                this.getIrRef(), expresionInt.getIrRef()));

        return codigo.toString();
    }
}