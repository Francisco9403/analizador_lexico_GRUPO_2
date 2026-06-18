package ast;

import llvm.CodeGeneratorHelper;
import java.util.ArrayList;
import java.util.List;

public class Programa extends Nodo {
    private final List<Nodo> declaraciones;
    private final List<Nodo> sentencias;

    public Programa(List<Nodo> declaraciones, List<Nodo> sentencias) {
        this.declaraciones = declaraciones != null ? declaraciones : new ArrayList<>();
        this.sentencias = sentencias != null ? sentencias : new ArrayList<>();
    }

    public List<Nodo> getDeclaraciones() { return declaraciones; }
    public List<Nodo> getSentencias() { return sentencias; }

    @Override
    public String getEtiqueta() {
        return "PROGRAM";
    }

    @Override
    public List<Nodo> getHijos() {
        List<Nodo> hijos = new ArrayList<>();
        hijos.addAll(declaraciones);
        hijos.addAll(sentencias);
        return hijos;
    }

    @Override
    public String generarCodigo() {
        StringBuilder resultado = new StringBuilder();

        // --- PASO 1: GENERAR EL CUERPO (Para registrar los strings globales) ---
        StringBuilder cuerpoMain = new StringBuilder();
        for(Nodo decl : this.getDeclaraciones()) {
            cuerpoMain.append(decl.generarCodigo());
        }
        for(Nodo sent : this.getSentencias()) {
            cuerpoMain.append(sent.generarCodigo());
        }

        // --- PASO 2: CABECERAS Y DECLARACIONES GLOBALES ---
        resultado.append("; Compilador UNNOBA 2026\n");
        resultado.append("target datalayout = \"e-m:w-p270:32:32-p271:32:32-p272:64:64-i64:64-f80:128-n8:16:32:64-S128\"\n");
        resultado.append("target triple = \"x86_64-pc-windows-msvc\"\n\n");

        resultado.append("declare i32 @printf(i8*, ...)\n");
        resultado.append("declare i32 @scanf(i8*, ...)\n\n");

        resultado.append("declare void @exit(i32)\n\n");

        resultado.append("@.str.int = private constant [4 x i8] c\"%d\\0A\\00\"\n");
        resultado.append("@.str.float = private constant [4 x i8] c\"%f\\0A\\00\"\n");

        resultado.append(CodeGeneratorHelper.getGlobalStringsDef()).append("\n");

        // --- PASO 3: FUNCIÓN MAIN Y ENSAMBLAJE FINAL ---
        resultado.append("define i32 @main() {\n");
        resultado.append("entry:\n");

        // Pegamos to-do lo que generamos al principio
        resultado.append(cuerpoMain.toString());

        resultado.append("  ret i32 0\n");
        resultado.append("}\n");

        return resultado.toString();
    }
}