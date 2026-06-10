package ast;

import java.util.ArrayList;
import java.util.List;

public class SumaAcumulativa extends Expresion {

    private Expresion limite;
    private Expresion arreglo;

    // Nodos sintéticos (Los hijos que arman el árbol del pizarrón)
    private Asignacion asigSuma;
    private Asignacion asigIndice;
    private While bucleWhile;

    // Pasamos el ID del arreglo como String porque NodoAccesoArreglo lo necesita así
    public SumaAcumulativa(Expresion limite, Expresion arreglo, String nombreArreglo) {
        this.limite = limite;
        this.arreglo = arreglo;
        this.setTipoDato("FLOAT");

        // === 1. FABRICAR EL SUB-ÁRBOL SINTÉTICO (Árbol puro) ===

        // a) sumatoria = 0.0
        Identificador idSum1 = new Identificador("_sum_temp"); idSum1.setTipoDato("FLOAT");
        NodoHoja ceroFloat = new NodoHoja("0.0"); ceroFloat.setTipoDato("FLOAT");
        this.asigSuma = new Asignacion(idSum1, ceroFloat);

        // b) indice = 0
        Identificador idIndice1 = new Identificador("_i_temp"); idIndice1.setTipoDato("INT");
        NodoHoja ceroInt = new NodoHoja("0"); ceroInt.setTipoDato("INT");
        this.asigIndice = new Asignacion(idIndice1, ceroInt);

        // c) Condición del While: indice < limite
        Identificador idIndice2 = new Identificador("_i_temp"); idIndice2.setTipoDato("INT");
        OperacionComparar condicion = new OperacionComparar("<", idIndice2, limite); condicion.setTipoDato("BOOL");

        // d) Cuerpo del While: sumatoria = sumatoria + arreglo[indice]
        Identificador idIndice3 = new Identificador("_i_temp"); idIndice3.setTipoDato("INT");
        AccesoArreglo acceso = new AccesoArreglo(nombreArreglo, idIndice3); acceso.setTipoDato("FLOAT");

        Identificador idSum2 = new Identificador("_sum_temp"); idSum2.setTipoDato("FLOAT");
        OperacionAritmetica suma = new OperacionAritmetica("+", idSum2, acceso); suma.setTipoDato("FLOAT");

        Identificador idSum3 = new Identificador("_sum_temp"); idSum3.setTipoDato("FLOAT");
        Asignacion asigAcumular = new Asignacion(idSum3, suma);

        // e) Incremento: indice = indice + 1
        Identificador idIndice4 = new Identificador("_i_temp"); idIndice4.setTipoDato("INT");
        NodoHoja unoInt = new NodoHoja("1"); unoInt.setTipoDato("INT");
        OperacionAritmetica incremento = new OperacionAritmetica("+", idIndice4, unoInt); incremento.setTipoDato("INT");

        Identificador idIndice5 = new Identificador("_i_temp"); idIndice5.setTipoDato("INT");
        Asignacion asigIncrementar = new Asignacion(idIndice5, incremento);

        // f) Armar el bloque del While
        List<Nodo> bloqueWhile = new ArrayList<>();
        bloqueWhile.add(asigAcumular);
        bloqueWhile.add(asigIncrementar);

        // g) Crear el Nodo While final
        this.bucleWhile = new While(condicion, bloqueWhile, new ArrayList<>());
    }

    @Override
    public String getEtiqueta() {
        return "Suma Acumulativa";
    }

    @Override
    public List<Nodo> getHijos() {
        // === LA MAGIA PARA GRAPHVIZ ===
        // Acá mostramos exactamente lo que el profe hizo en el pizarrón
        return List.of(asigSuma, asigIndice, bucleWhile);
    }

    @Override
    public String generarCodigo() {
        StringBuilder codigo = new StringBuilder();

        // 1. Crear las variables temporales en memoria LLVM (alloca)
        codigo.append("  %_sum_temp = alloca double\n");
        codigo.append("  %_i_temp = alloca i32\n");

        // 2. Generar el código de los hijos sintéticos (Se compilan solos!)
        codigo.append(asigSuma.generarCodigo());
        codigo.append(asigIndice.generarCodigo());
        codigo.append(bucleWhile.generarCodigo());

        // 3. El resultado final es el valor que quedó en _sum_temp
        this.setIrRef(llvm.CodeGeneratorHelper.getNewPointer());
        codigo.append(String.format("  %s = load double, double* %%_sum_temp\n", this.getIrRef()));

        return codigo.toString();
    }
}