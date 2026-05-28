package ast;

import llvm.CodeGeneratorHelper;
import java.util.ArrayList;
import java.util.List;

public class NodoPrograma extends NodoC {
    private final List<NodoC> declaraciones;
    private final List<NodoC> sentencias;

    public NodoPrograma(List<NodoC> declaraciones, List<NodoC> sentencias) {
        this.declaraciones = declaraciones != null ? declaraciones : new ArrayList<>();
        this.sentencias = sentencias != null ? sentencias : new ArrayList<>();
    }

    public List<NodoC> getDeclaraciones() { return declaraciones; }
    public List<NodoC> getSentencias() { return sentencias; }

    @Override
    public String getEtiqueta() {
        return "PROGRAM";
    }

    @Override
    public List<NodoC> getHijos() {
        List<NodoC> hijos = new ArrayList<>();
        hijos.addAll(declaraciones);
        hijos.addAll(sentencias);
        return hijos;
    }

    @Override
    public String generarCodigo() {
        StringBuilder resultado = new StringBuilder();

        // --- CABECERAS Y DECLARACIONES GLOBALES ---
        resultado.append("; Compilador UNNOBA 2026\n");
        resultado.append("target datalayout = \"e-m:w-p270:32:32-p271:32:32-p272:64:64-i64:64-f80:128-n8:16:32:64-S128\"\n");
        resultado.append("target triple = \"x86_64-pc-windows-msvc\"\n\n");

        resultado.append("declare i32 @printf(i8*, ...)\n");
        resultado.append("declare i32 @scanf(i8*, ...)\n");

        resultado.append("declare double @suma_cumulativa(i32, ptr)\n\n");
        resultado.append("@.str.int = private constant [4 x i8] c\"%d\\0A\\00\"\n");
        resultado.append("@.str.float = private constant [4 x i8] c\"%f\\0A\\00\"\n\n");

        // --- FUNCIÓN MAIN ---
        resultado.append("define i32 @main() {\n");
        resultado.append("entry:\n");

        CodeGeneratorHelper.reset();

        for(NodoC decl : this.getDeclaraciones()) {
            resultado.append(decl.generarCodigo());
        }

        for(NodoC sent : this.getSentencias()) {
            resultado.append(sent.generarCodigo());
        }

        resultado.append("  ret i32 0\n");
        resultado.append("}\n");

        return resultado.toString();
    }
}