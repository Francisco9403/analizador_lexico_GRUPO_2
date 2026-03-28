

import java.io.File;

/**
 * Genera MiLexico.java a partir de lexico.flex usando JFlex.
 * Ejecutar este main antes de compilar el proyecto si se modifica lexico.flex.
 */
public class Generador {

    public static void main(String[] args) {
        String path = "./src/lexico.flex";
        System.out.println("Generando léxico desde: " + path);
        File file = new File(path);
        jflex.generator.LexGenerator generator = new jflex.generator.LexGenerator(file);
        generator.generate();
        System.out.println("MiLexico.java generado correctamente.");
    }
}
