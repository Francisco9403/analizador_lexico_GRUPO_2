package TablaSimbolo;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

public class TablaSimbolo {
    private final Map<String, Symbol> table = new HashMap<>();

    // Verificamos si ya existe (como en el ejemplo que te pasaron)
    public boolean exists(String name) {
        return table.containsKey(name);
    }

    public void addId(String name, String token) {
        if (!exists(name)) {
            table.put(name, new Symbol(name, token, "", "", ""));
        }
    }

    public void addConstant(String name, String token, String value, String length) {
        if (!exists(name)) {
            table.put(name, new Symbol(name, token, "", value, length));
        }
    }

    public void setType(String name, String type) {
        if (exists(name)) {
            table.get(name).type = type;
        } else {
            System.err.println("Error: Intentando asignar tipo a variable no declarada: " + name);
        }
    }

    public void generateFile() {
        try (PrintWriter writer = new PrintWriter(new FileWriter("ts.txt"))) {
            writer.printf("%-20s | %-15s | %-15s | %-20s | %-5s%n", "NOMBRE", "TOKEN", "TIPO", "VALOR", "LONG");
            writer.println("---------------------------------------------------------------------------------------");
            for (Symbol s : table.values()) {
                writer.printf("%-20s | %-15s | %-15s | %-20s | %-5s%n",
                        s.name, s.token, s.type, s.value, s.length);
            }
        } catch (IOException e) {
            System.err.println("Error al generar ts.txt: " + e.getMessage());
        }
    }

    private static class Symbol {
        String name, token, type, value, length;
        Symbol(String n, String t, String ty, String v, String l) {
            name = n; token = t; type = ty; value = v; length = l;
        }
    }
}