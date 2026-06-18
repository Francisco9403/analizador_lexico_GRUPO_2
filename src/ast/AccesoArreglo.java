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

        // --- EXTRACCIÓN SEGURA DEL TAMAÑO (Para evitar el StringIndexOutOfBoundsException) ---
        String tipoDeclarado = Parser.Parser.tablaSimbolos.getType(id);
        int tamanoArreglo = 0;

        // Comprobamos si el tipo viene de la tabla con el formato "FLOAT_ARRAY[10]"
        if (tipoDeclarado != null && tipoDeclarado.contains("[") && tipoDeclarado.contains("]")) {
            int inicioCorchete = tipoDeclarado.indexOf('[') + 1;
            int finCorchete = tipoDeclarado.indexOf(']');
            tamanoArreglo = Integer.parseInt(tipoDeclarado.substring(inicioCorchete, finCorchete));
        } else {
            // Si el tipo es "UNKNOWN" (porque es un arreglo literal temporal como _arr_temp_1),
            // le ponemos un tamaño muy grande por defecto para saltear la validación,
            // ya que los literales se controlan en su propia estructura.
            tamanoArreglo = 999999;
        }
        // -------------------------------------------------------------------------------------

        // --- INYECCIÓN DE BOUNDS CHECKING ---
        String labelOk = "idx_ok_" + CodeGeneratorHelper.getNewLabel();
        String labelError = "idx_err_" + CodeGeneratorHelper.getNewLabel();

        // Comparamos: ¿índice < tamañoArreglo?
        String regCond = CodeGeneratorHelper.getNewPointer();
        codigo.append(String.format("  %s = icmp slt i32 %s, %d\n", regCond, indice.getIrRef(), tamanoArreglo));

        // Bifurcación condicional
        codigo.append(String.format("  br i1 %s, label %%%s, label %%%s\n", regCond, labelOk, labelError));

        // -- BLOQUE DE ERROR (Abortar) --
        codigo.append(String.format("\n%s:\n", labelError));
        codigo.append("  call void @exit(i32 1)\n");
        codigo.append("  unreachable\n");

        // -- BLOQUE OK (Continuar normal) --
        codigo.append(String.format("\n%s:\n", labelOk));
        // ------------------------------------

        // 2. Extraer el puntero real del arreglo de la caja de la variable
        String basePtr = CodeGeneratorHelper.getNewPointer();
        codigo.append(String.format("  %s = load double*, double** %%%s\n", basePtr, id));

        // 3. Calcular la dirección exacta basándonos en el 'basePtr'
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