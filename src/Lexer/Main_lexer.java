package Lexer;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;

/**
 * Punto de entrada del compilador en modo consola.
 * Permite leer desde consola o archivo y ejecutar analisis lexico+sintactico.
 */

public class Main_lexer {

    private static final String INPUT_FILE = "./src/input_1.txt";

    public static void main(String[] args) {

        Scanner teclado = new Scanner(System.in);

        try (teclado) {
            service.CompilerService compilerService = new service.CompilerService();
            System.out.println("=== Analizador Léxico ===");
            System.out.println("¿Desde dónde desea leer?");
            System.out.println("  1 - Desde consola");
            System.out.println("  2 - Desde archivo (" + INPUT_FILE + ")");
            System.out.print("Ingrese su opción: ");
            String opcion = teclado.nextLine().trim();
            String source;
            if (opcion.equals("1")) {
                System.out.println("\nModo consola. Ingrese programa completo.");
                System.out.println("Finalice con una linea que contenga solo FIN.\n");

                StringBuilder sb = new StringBuilder();
                while (true) {
                    String line = teclado.nextLine();
                    sb.append(line).append('\n');
                    if ("FIN".equals(line.trim())) {
                        break;
                    }
                }
                source = sb.toString();
            } else if (opcion.equals("2")) {
                System.out.println("\nLeyendo desde: " + INPUT_FILE + "\n");
                source = Files.readString(Path.of(INPUT_FILE), StandardCharsets.UTF_8);
            } else {
                System.out.println("Opción inválida. Saliendo.");
                return;
            }

            String result = compilerService.compileSource(source);
            System.out.println(result);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}