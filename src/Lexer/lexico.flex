package Lexer;
import TablaSimbolo.TablaSimbolo;

/**
 * Analizador Léxico para el TPI - Grupo 2
 */
%%

%public
%class Lexer
%unicode
%type Token
%line
%column

%{

    public static TablaSimbolo tablaSimbolos = new TablaSimbolo();

    /* Variables para strings y comentarios */
    StringBuffer string = new StringBuffer();
    int string_yyline = 0;
    int string_yycolumn = 0;

    private String ultimoTokenSignificativo = null;

    private Token token(String nombre) {
        ultimoTokenSignificativo = nombre;
        return new Token(nombre, this.yyline + 1, this.yycolumn + 1);
    }

    private Token token(String nombre, Object valor) {
        ultimoTokenSignificativo = nombre;
        return new Token(nombre, this.yyline + 1, this.yycolumn + 1, valor);
    }

    private java.util.Stack<Integer> nivelesIndentacion = new java.util.Stack<>();
    private int espaciosActuales = 0;
    private java.util.LinkedList<Token> colaTokens = new java.util.LinkedList<>();

    {
                nivelesIndentacion.push(0);
                yybegin(INDENTANDO);
            }

    public Token proximoToken() throws java.io.IOException {
                // 1. Si hay tokens en la cola (DEDENTs pendientes), los largamos de a uno
                if (!colaTokens.isEmpty()) return colaTokens.removeFirst();

                Token t = yylex();

                // 2. Si yylex() devolvió null es porque llegó al final del archivo (EOF)
                if (t == null) {
                    // Si la pila tiene más que el nivel base (0), generamos los DEDENTs finales
                    if (nivelesIndentacion.size() > 1) {
                        nivelesIndentacion.pop();
                        return token("V_DEDENT");
                    }
                    return null; // Fin real de la transmisión
                }
                return t;
            }

%}

/* Macros */
LineTerminator = \r|\n|\r\n
WhiteSpace     = [ \t\f]

/* Letras Unicode para soportar tildes y eñes según el estándar */
Letter         = [:letter:]
Digit          = [0-9]
Identifier     = {Letter}({Letter}|{Digit}|_)*

DecIntegerLiteral = 0 | [1-9][0-9]*
/* Formatos: 99.99 , 99. , .999 */
FloatLiteral      = ({Digit}+ "." {Digit}*) | ("." {Digit}+)

%state INDENTANDO
%state CADENA
%state COMENTARIO
%state ARREGLO

%%
<INDENTANDO> {
  " "  { espaciosActuales++; }
  \t   { espaciosActuales += 4; }
  {LineTerminator} { espaciosActuales = 0; }

  [^ \t\r\n] {
      int nivelAnterior = nivelesIndentacion.peek();
      boolean lineaContinuacionConComa = ",".equals(yytext());

      if (!lineaContinuacionConComa) {
          if (espaciosActuales > nivelAnterior) {
              nivelesIndentacion.push(espaciosActuales);
              colaTokens.add(token("V_INDENT"));
          }
          else if (espaciosActuales < nivelAnterior) {
              while (espaciosActuales < nivelesIndentacion.peek()) {
                  nivelesIndentacion.pop();
                  colaTokens.add(token("V_DEDENT"));
              }
          }
      }
      yypushback(1);
      yybegin(YYINITIAL);

      if (!colaTokens.isEmpty()) {
          return colaTokens.removeFirst();
      }
  }
}

<YYINITIAL> {

  {LineTerminator} { espaciosActuales = 0; yybegin(INDENTANDO); }

  /* Palabras Reservadas */
  "PROGRAM"            { return token("PROGRAM", yytext()); }
  "IF"                 { return token("IF", yytext()); }
  "ELIF"               { return token("ELIF", yytext()); }
  "ELSE"               { return token("ELSE", yytext()); }
  "WHILE"              { return token("WHILE", yytext()); }
  "ALT_WHILE"          { return token("ALT_WHILE", yytext()); }
  "BREAK"              { return token("BREAK", yytext()); }
  "CONTINUE"           { return token("CONTINUE", yytext()); }
  "PRINT"              { return token("PRINT", yytext()); }

  /* Tipos de Datos */
  "integer"            { return token("TYPE_INT", yytext()); }
  "float"              { return token("TYPE_FLOAT", yytext()); }
  "boolean"            { return token("TYPE_BOOL", yytext()); }
  "float_array"        { return token("TYPE_ARRAY", yytext()); }

  /* Lectura */
  "READ_INT"           { return token("READ_INT", yytext()); }
  "READ_FLOAT"         { return token("READ_FLOAT", yytext()); }
  "READ_BOOL"          { return token("READ_BOOL", yytext()); }

  /* Constantes lógicas */
  "true"               { return token("CONST_BOOL", yytext()); }
  "false"              { return token("CONST_BOOL", yytext()); }

  /* TEMA ESPECIAL: Grupo 2 */
  "suma_cumulativa"    { return token("SUMA_ACUM", yytext()); }

  /* Identificadores y Números*/

{Identifier} {
    // Agregamos el ID a la tabla. Por ahora sin tipo (se lo dará el Parser)
    tablaSimbolos.addId(yytext(), "ID");
    return token("ID", yytext());
}

{DecIntegerLiteral} {
    // Las constantes se agregan con su valor y longitud
    tablaSimbolos.addConstant("_" + yytext(), "CTE_INT", yytext(), String.valueOf(yytext().length()));
    return token("CTE_INT", yytext());
}

{FloatLiteral} {
    tablaSimbolos.addConstant("_" + yytext(), "CTE_FLOAT", yytext(), String.valueOf(yytext().length()));
    return token("CTE_FLOAT", yytext());
}

  /* Operadores Aritméticos */
  "+"                  { return token("OP_SUMA", yytext()); }
  "-"                  { return token("OP_RESTA", yytext()); }
  "*"                  { return token("OP_MULT", yytext()); }
  "/"                  { return token("OP_DIV", yytext()); }

  /* Operadores de Comparación */
  "=="                 { return token("COMP_IGUAL", yytext()); }
  "!="                 { return token("COMP_DISTINTO", yytext()); }
  ">="                 { return token("COMP_MAYOR_IGUAL", yytext()); }
  "<="                 { return token("COMP_MENOR_IGUAL", yytext()); }
  ">"                  { return token("COMP_MAYOR", yytext()); }
  "<"                  { return token("COMP_MENOR", yytext()); }
  "="                  { return token("ASIG", yytext()); }

  /* Operadores Lógicos */
  "&&"                 { return token("AND", yytext()); }
  "||"                 { return token("OR", yytext()); }
  "!"                  { return token("NOT", yytext()); }

   /* Puntuación y Delimitadores */
   "("                  { return token("PAR_A", yytext()); }
   ")"                  { return token("PAR_C", yytext()); }
   "["                  {
                          if ("ASIG".equals(ultimoTokenSignificativo) || "COMA".equals(ultimoTokenSignificativo)) {
                              string.setLength(0);
                              string_yyline = this.yyline;
                              string_yycolumn = this.yycolumn;
                              string.append("[");
                              yybegin(ARREGLO);
                          } else {
                              return token("CORCH_A", yytext());
                          }
                        }
   "]"                  { return token("CORCH_C", yytext()); }
   ","                  { return token("COMA", yytext()); }
   ":"                  { return token("DOS_PUNTOS", yytext()); }

  /* Comentarios unilínea */
  "%".* { /* Ignorar */ }

  /* Comentarios multilínea {* ... *} */
  "{*"                 { yybegin(COMENTARIO); }

  /* Espacios y cadenas */
  {WhiteSpace}         { /* Ignorar */ }
  \"                   { string.setLength(0);
                         string_yyline = this.yyline;
                         string_yycolumn = this.yycolumn;
                         yybegin(CADENA);
                       }
}

<COMENTARIO> {
  "*}"                 { yybegin(YYINITIAL); }
  [^]                  { /* Ignorar contenido */ }
  <<EOF>>              { throw new Error("Error Léxico: Comentario multilínea sin cerrar."); }
}

<ARREGLO> {
  "]"                  {
                           yybegin(YYINITIAL);
                           String arreglo = string.append("]").toString();
                           tablaSimbolos.addConstant("_" + arreglo, "CTE_ARREGLO", arreglo, String.valueOf(arreglo.length()));
                           return token("CTE_ARREGLO", arreglo);
                       }
  [^]                  { string.append(yytext()); }
  <<EOF>>              { throw new Error("Error Léxico: Arreglo sin cerrar al final del archivo."); }
}

<CADENA> {
  \"                   {
                           yybegin(YYINITIAL);
                           String cadena = string.toString();
                           tablaSimbolos.addConstant("_" + cadena, "CTE_STR", cadena, String.valueOf(cadena.length()));
                           return token("CTE_STR", cadena);
                       }
  "\\\""               { string.append("\""); }
  "\\n"                { string.append("\n"); }
  "\\t"                { string.append("\t"); }
  "\\\\"               { string.append("\\"); }
  [^]                  { string.append(yytext()); }
  <<EOF>>              { throw new Error("Error Léxico: Cadena sin cerrar al final del archivo."); }
}

/* Fallback de errores */
[^]                    { throw new Error("Carácter inválido <" + yytext() + "> en línea " + (yyline+1)); }