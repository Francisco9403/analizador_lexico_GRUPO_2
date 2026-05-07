package ast;

import java.util.ArrayList;
import java.util.List;

public class NodosAST {

    public static class NodoPrograma extends Nodo {
        private final List<Nodo> declaraciones;
        private final List<Nodo> sentencias;
        public NodoPrograma(List<Nodo> declaraciones, List<Nodo> sentencias) {
            this.declaraciones = declaraciones;
            this.sentencias = sentencias;
        }
        @Override public String getEtiqueta() { return "PROGRAM"; }
        @Override public List<Nodo> getHijos() {
            List<Nodo> hijos = new ArrayList<>();
            hijos.addAll(declaraciones);
            hijos.addAll(sentencias);
            return hijos;
        }
    }

    public static class NodoAsignacion extends Nodo {
        private Nodo identificador;
        private Nodo expresion;
        public NodoAsignacion(Nodo identificador, Nodo expresion) {
            this.identificador = identificador;
            this.expresion = expresion;
        }
        @Override public String getEtiqueta() { return "ASIG"; }
        @Override public List<Nodo> getHijos() { return List.of(identificador, expresion); }
    }

    public static class NodoHoja extends Nodo {
        private final String valor;
        public NodoHoja(Object valor) { this.valor = String.valueOf(valor); }
        @Override public String getEtiqueta() { return valor; }
    }

    // --- RESTO DE LOS NODOS ---
    public static class NodoDeclaracion extends Nodo {
        private String tipo;
        private String id;
        public NodoDeclaracion(String tipo, String id) { this.tipo = tipo; this.id = id; }
        @Override public String getEtiqueta() { return "DECL\\n" + tipo + " " + id; }
    }

    public static class NodoIdentificador extends Nodo {
        private String id;
        public NodoIdentificador(String id) { this.id = id; }
        @Override public String getEtiqueta() { return "ID: " + id; }
    }

    public static class NodoAccesoArreglo extends Nodo {
        private String id;
        private Nodo indice;
        public NodoAccesoArreglo(String id, Nodo indice) { this.id = id; this.indice = indice; }
        @Override public String getEtiqueta() { return "ACCESO\\n" + id + "[]"; }
        @Override public List<Nodo> getHijos() { return List.of(indice); }
    }

    public static class NodoImpresion extends Nodo {
        private Nodo expresion;
        private String textoExtra;
        public NodoImpresion(Nodo expresion) { this.expresion = expresion; this.textoExtra = ""; }
        public NodoImpresion(String texto, Nodo expresion) { this.textoExtra = texto; this.expresion = expresion; }
        @Override public String getEtiqueta() { return "PRINT" + (textoExtra.isEmpty() ? "" : "\\n\"" + textoExtra + "\""); }
        @Override public List<Nodo> getHijos() { return expresion != null ? List.of(expresion) : new ArrayList<>(); }
    }

    public static class NodoOperacion extends Nodo {
        private String operador;
        private Nodo izq, der;
        public NodoOperacion(String operador, Nodo izq, Nodo der) { this.operador = operador; this.izq = izq; this.der = der; }
        @Override public String getEtiqueta() { return "OP: " + operador; }
        @Override public List<Nodo> getHijos() { return List.of(izq, der); }
    }

    public static class NodoUnario extends Nodo {
        private String operador;
        private Nodo operando;
        public NodoUnario(String operador, Nodo operando) { this.operador = operador; this.operando = operando; }
        @Override public String getEtiqueta() { return "UNARIO: " + operador; }
        @Override public List<Nodo> getHijos() { return List.of(operando); }
    }

    public static class NodoIf extends Nodo {
        private Nodo condicion;
        private List<Nodo> bloqueTrue, bloqueElif, bloqueElse;
        public NodoIf(Nodo c, List<Nodo> bT, List<Nodo> bElif, List<Nodo> bE) { condicion = c; bloqueTrue = bT; bloqueElif = bElif; bloqueElse = bE; }
        @Override public String getEtiqueta() { return "IF"; }
        @Override public List<Nodo> getHijos() {
            List<Nodo> hijos = new ArrayList<>();
            hijos.add(condicion); hijos.addAll(bloqueTrue); hijos.addAll(bloqueElif); hijos.addAll(bloqueElse);
            return hijos;
        }
    }

    public static class NodoWhile extends Nodo {
        private Nodo condicion;
        private List<Nodo> bloque, altWhile;
        public NodoWhile(Nodo c, List<Nodo> b, List<Nodo> aw) { condicion = c; bloque = b; altWhile = aw; }
        @Override public String getEtiqueta() { return "WHILE"; }
        @Override public List<Nodo> getHijos() {
            List<Nodo> hijos = new ArrayList<>();
            hijos.add(condicion); hijos.addAll(bloque); hijos.addAll(altWhile);
            return hijos;
        }
    }

    public static class NodoControl extends Nodo {
        private String tipo;
        public NodoControl(String tipo) { this.tipo = tipo; }
        @Override public String getEtiqueta() { return tipo; }
    }

    public static class NodoArreglo extends Nodo {
        private List<Nodo> elementos;
        public NodoArreglo(List<Nodo> elementos) { this.elementos = elementos; }
        @Override public String getEtiqueta() { return "ARREGLO"; }
        @Override public List<Nodo> getHijos() { return elementos; }
    }

    public static class NodoLlamada extends Nodo {
        private String funcion;
        private List<Nodo> argumentos;
        public NodoLlamada(String funcion, List<Nodo> argumentos) { this.funcion = funcion; this.argumentos = argumentos; }
        @Override public String getEtiqueta() { return "CALL: " + funcion; }
        @Override public List<Nodo> getHijos() { return argumentos; }
    }
}