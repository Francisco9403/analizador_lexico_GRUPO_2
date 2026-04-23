package Lexer;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Genera el Lexer (JFlex) y el Parser (CUP).
 */
public class Generador {

    public static void main(String[] args) {
        // 1. Generar el Analizador Léxico (JFlex)
        String pathLexer = "./src/Lexer/lexico.flex";
        System.out.println("--- 1. Generando Léxico ---");
        System.out.println("Archivo: " + pathLexer);
        File fileLexer = new File(pathLexer);
        jflex.generator.LexGenerator generator = new jflex.generator.LexGenerator(fileLexer);
        generator.generate();
        System.out.println("Lexer.java generado correctamente.\n");

        // 2. Generar el Analizador Sintáctico (CUP)
        System.out.println("--- 2. Generando Sintáctico ---");
        String[] paramsCUP = {
                "-destdir", "./src/Parser",
                "-parser", "Parser",
                "-symbols", "ParserSym",
                "-expect", "50",
                "./src/Parser/parser.cup"
        };

        try {
            java_cup.Main.main(paramsCUP);
            System.out.println("Parser.java y ParserSym.java generados correctamente.");
        } catch (Exception ex) {
            Logger.getLogger(Generador.class.getName()).log(Level.SEVERE, "Error compilando CUP", ex);
        }
    }
}