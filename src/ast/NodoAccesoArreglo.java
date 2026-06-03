package ast;

import llvm.CodeGeneratorHelper;
import java.util.List;

public class NodoAccesoArreglo extends NodoC {
    private String id;
    private NodoC indice;

    public NodoAccesoArreglo(String id, NodoC indice) {
        this.id = id;
        this.indice = indice;
    }

    @Override
    public String getEtiqueta() {
        return "ACCESO\\n" + id + "[]";
    }

    @Override
    public List<NodoC> getHijos() {
        return List.of(indice);
    }

    @Override
    public String generarCodigo() {
        StringBuilder codigo = new StringBuilder();

        // 1. Generar código del índice para obtener su irRef
        codigo.append(indice.generarCodigo());

        String tipoLLVM = "FLOAT".equals(this.getTipoDato()) ? "double" : "i32";
        String elemPtr = CodeGeneratorHelper.getNewPointer();

        // 2. Calcular la dirección exacta basándonos en el puntero de la variable del arreglo
        // %elemPtr = getelementptr tipo, tipo* %id, i32 %indice
        codigo.append(String.format("  %s = getelementptr %s, %s* %%%s, i32 %s\n",
                elemPtr, tipoLLVM, tipoLLVM, id, indice.getIrRef()));

        // 3. Cargar el valor almacenado en esa dirección a un registro temporal
        this.setIrRef(CodeGeneratorHelper.getNewPointer());
        codigo.append(String.format("  %s = load %s, %s* %s\n",
                this.getIrRef(), tipoLLVM, tipoLLVM, elemPtr));

        return codigo.toString();
    }
}