# Compilador - Analizador Léxico (UNNOBA 2026)

Analizador léxico de ejemplo construido con JFlex y Java 21.

## Estructura del proyecto

```
analizador_lexico/
├── README.md
├── pom.xml
└── src/
      ├── Generador.java      # Regenera MiLexico.java desde lexico.flex
      ├── Main_lexer.java     # Punto de entrada principal
      ├── Token.java          # Clase Token
      ├── input_1.txt         # Archivo de prueba
      ├── lexico.flex         # Definición del léxico (fuente JFlex)
      └── Lexer.java          # Generado por JFlex (no editar a mano)
```

## Prerrequisitos

- Java 21 (LTS) o superior
- Maven 3.6 o superior

## Primer uso: generar el léxico

`Lexer.java` se genera a partir de `lexico.flex` con JFlex.
Antes de compilar por primera vez (o si modificás `lexico.flex`), ejecutá:

```bash
mvn exec:java -Dexec.mainClass="Generador"
```

## Compilar y ejecutar

```bash
# Compilar
mvn clean compile

# Ejecutar el analizador léxico
mvn exec:java -Dexec.mainClass="Main_lexer"
```

Al ejecutar, el programa pregunta:

```
=== Analizador Léxico ===
¿Desde dónde desea leer?
  1 - Desde consola
  2 - Desde archivo (./src/input_1.txt)
Ingrese su opción:
```

- **Opción 1 (consola):** escribí expresiones línea a línea. Para terminar, ingresá `FIN`.
- **Opción 2 (archivo):** lee y muestra todos los tokens de `input_1.txt`.

## Token FIN

El token `FIN` es una palabra reservada del léxico. Al reconocerlo, el análisis
se detiene inmediatamente, tanto en modo consola como en modo archivo.

## Tokens reconocidos

| Token            | Descripción                  |
|------------------|------------------------------|
| `FIN`            | Corta la ejecución           |
| `ABSTRACT`       | Palabra reservada `abstract` |
| `BOOLEAN`        | Palabra reservada `boolean`  |
| `BREAK`          | Palabra reservada `break`    |
| `INTEGER_LITERAL`| Constante entera             |
| `ID`             | Identificador                |
| `EQ`             | `=`                          |
| `EQEQ`           | `==`                         |
| `PLUS`           | `+`                          |
| `STRING_LITERAL` | Cadena entre comillas dobles |
