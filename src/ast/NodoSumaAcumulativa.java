package ast;

import java.util.ArrayList;
import java.util.List;

public class NodoSumaAcumulativa extends NodoC {

    private NodoC limite;
    private NodoC arreglo;

    // Nodos sintéticos (Los hijos que arman el árbol del pizarrón)
    private NodoAsignacion asigSuma;
    private NodoAsignacion asigIndice;
    private NodoWhile bucleWhile;

    // Pasamos el ID del arreglo como String porque NodoAccesoArreglo lo necesita así
    public NodoSumaAcumulativa(NodoC limite, NodoC arreglo, String nombreArreglo) {
        this.limite = limite;
        this.arreglo = arreglo;
        this.setTipoDato("FLOAT");

        // === 1. FABRICAR EL SUB-ÁRBOL SINTÉTICO (Árbol puro) ===

        // a) sumatoria = 0.0
        NodoIdentificador idSum1 = new NodoIdentificador("_sum_temp"); idSum1.setTipoDato("FLOAT");
        NodoHoja ceroFloat = new NodoHoja("0.0"); ceroFloat.setTipoDato("FLOAT");
        this.asigSuma = new NodoAsignacion(idSum1, ceroFloat);

        // b) indice = 0
        NodoIdentificador idIndice1 = new NodoIdentificador("_i_temp"); idIndice1.setTipoDato("INT");
        NodoHoja ceroInt = new NodoHoja("0"); ceroInt.setTipoDato("INT");
        this.asigIndice = new NodoAsignacion(idIndice1, ceroInt);

        // c) Condición del While: indice < limite
        NodoIdentificador idIndice2 = new NodoIdentificador("_i_temp"); idIndice2.setTipoDato("INT");
        NodoOperacion condicion = new NodoOperacion("<", idIndice2, limite); condicion.setTipoDato("BOOL");

        // d) Cuerpo del While: sumatoria = sumatoria + arreglo[indice]
        NodoIdentificador idIndice3 = new NodoIdentificador("_i_temp"); idIndice3.setTipoDato("INT");
        NodoAccesoArreglo acceso = new NodoAccesoArreglo(nombreArreglo, idIndice3); acceso.setTipoDato("FLOAT");

        NodoIdentificador idSum2 = new NodoIdentificador("_sum_temp"); idSum2.setTipoDato("FLOAT");
        NodoOperacion suma = new NodoOperacion("+", idSum2, acceso); suma.setTipoDato("FLOAT");

        NodoIdentificador idSum3 = new NodoIdentificador("_sum_temp"); idSum3.setTipoDato("FLOAT");
        NodoAsignacion asigAcumular = new NodoAsignacion(idSum3, suma);

        // e) Incremento: indice = indice + 1
        NodoIdentificador idIndice4 = new NodoIdentificador("_i_temp"); idIndice4.setTipoDato("INT");
        NodoHoja unoInt = new NodoHoja("1"); unoInt.setTipoDato("INT");
        NodoOperacion incremento = new NodoOperacion("+", idIndice4, unoInt); incremento.setTipoDato("INT");

        NodoIdentificador idIndice5 = new NodoIdentificador("_i_temp"); idIndice5.setTipoDato("INT");
        NodoAsignacion asigIncrementar = new NodoAsignacion(idIndice5, incremento);

        // f) Armar el bloque del While
        List<NodoC> bloqueWhile = new ArrayList<>();
        bloqueWhile.add(asigAcumular);
        bloqueWhile.add(asigIncrementar);

        // g) Crear el Nodo While final
        this.bucleWhile = new NodoWhile(condicion, bloqueWhile, new ArrayList<>());
    }

    @Override
    public String getEtiqueta() {
        return "Suma Acumulativa";
    }

    @Override
    public List<NodoC> getHijos() {
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