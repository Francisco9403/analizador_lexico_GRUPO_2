package ast;

import llvm.CodeGeneratorHelper;
import java.util.List;

public class AccesoArreglo extends Expresion {
    private String id;
    private Expresion indice;

    public AccesoArreglo(String id, Expresion indice) {
        this.id = id;
        this.indice = indice;
    }

    @Override
    public String getEtiqueta() {
        return "ACCESO\\n" + id + "[]";
    }

    @Override
    public List<Nodo> getHijos() {
        return List.of(indice);
    }

    @Override
    public String generarCodigo() {
        StringBuilder codigo = new StringBuilder();

        // 1. Generar código del índice para obtener su irRef
        codigo.append(indice.generarCodigo());

        String tipoLLVM = "FLOAT".equals(this.getTipoDato()) ? "double" : "i32";

        // 2. NUEVO: ¡Extraer el puntero real del arreglo de la caja de la variable!
        String basePtr = CodeGeneratorHelper.getNewPointer();
        codigo.append(String.format("  %s = load double*, double** %%%s\n", basePtr, id));

        // 3. CORREGIDO: Calcular la dirección exacta basándonos en el 'basePtr', no en el '%id'
        String elemPtr = CodeGeneratorHelper.getNewPointer();
        codigo.append(String.format("  %s = getelementptr %s, %s* %s, i32 %s\n",
                elemPtr, tipoLLVM, tipoLLVM, basePtr, indice.getIrRef()));

        // 4. Cargar el valor numérico almacenado en esa dirección
        this.setIrRef(CodeGeneratorHelper.getNewPointer());
        codigo.append(String.format("  %s = load %s, %s* %s\n",
                this.getIrRef(), tipoLLVM, tipoLLVM, elemPtr));

        return codigo.toString();
    }
}