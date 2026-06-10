package ast;

import llvm.CodeGeneratorHelper;
import java.util.ArrayList;
import java.util.List;

public class Arreglo extends Expresion {
    private final List<Expresion> elementos;

    public Arreglo(List<Expresion> elementos) {
        this.elementos = elementos != null ? elementos : new ArrayList<>();
    }

    @Override
    public String getEtiqueta() {
        return "Arreglo Literal";
    }

    @Override
    public List<Nodo> getHijos() {
        return new ArrayList<>(elementos);
    }

    @Override
    public String generarCodigo() {
        StringBuilder codigo = new StringBuilder();
        int tamaño = elementos.size();

        if (tamaño == 0) {
            this.setIrRef("null");
            return "";
        }

        // Detectar el tipo base de los elementos del arreglo
        String tipoBase = elementos.get(0).getTipoDato();
        String tipoLLVM = "FLOAT".equals(tipoBase) ? "double" : "i32";

        // 1. Reservar espacio en la pila para el arreglo: %array = alloca [tamaño x tipo]
        String arrayPtr = CodeGeneratorHelper.getNewPointer();
        codigo.append(String.format("  %s = alloca [%d x %s]\n", arrayPtr, tamaño, tipoLLVM));

        // 2. Evaluar y guardar cada elemento secuencialmente
        for (int i = 0; i < tamaño; i++) {
            Expresion elem = elementos.get(i);
            codigo.append(elem.generarCodigo());

            // Obtener el puntero indexado: %elem_ptr = getelementptr [N x T], [N x T]* %array, i32 0, i32 i
            String elemPtr = CodeGeneratorHelper.getNewPointer();
            codigo.append(String.format("  %s = getelementptr [%d x %s], [%d x %s]* %s, i32 0, i32 %d\n",
                    elemPtr, tamaño, tipoLLVM, tamaño, tipoLLVM, arrayPtr, i));

            // Guardar valor: store tipo %val, tipo* %elem_ptr
            codigo.append(String.format("  store %s %s, %s* %s\n",
                    tipoLLVM, elem.getIrRef(), tipoLLVM, elemPtr));
        }

        // 3. Dejar en irRef un puntero directo al elemento 0 (tipo T*) para que sea consumido fácilmente
        this.setIrRef(CodeGeneratorHelper.getNewPointer());
        codigo.append(String.format("  %s = getelementptr [%d x %s], [%d x %s]* %s, i32 0, i32 0\n",
                this.getIrRef(), tamaño, tipoLLVM, tamaño, tipoLLVM, arrayPtr));

        return codigo.toString();
    }
}