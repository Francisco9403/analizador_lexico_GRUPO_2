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

    /* Métodos auxiliares para crear tokens con posición correcta */
    private Token token(String nombre) {
        return new Token(nombre, this.yyline + 1, this.yycolumn + 1);
    }

    private Token token(String nombre, Object valor) {
        return new Token(nombre, this.yyline + 1, this.yycolumn + 1, valor);
    }

%}

/* Macros */
LineTerminator = \r|\n|\r\n
WhiteSpace     = {LineTerminator} | [ \t\f]

/* Letras Unicode para soportar tildes y eñes según el estándar */
Letter         = [:letter:]
Digit          = [0-9]
Identifier     = {Letter}({Letter}|{Digit}|_)*

DecIntegerLiteral = 0 | [1-9][0-9]*
/* Formatos: 99.99 , 99. , .999 */
FloatLiteral      = ({Digit}+ "." {Digit}*) | ("." {Digit}+)

%state CADENA
%state COMENTARIO

%%

<YYINITIAL> {
  /* Palabras Reservadas */
  "PROGRAM"            { return token("PROGRAM", yytext()); }
  "DECLARE"            { return token("DECLARE", yytext()); }
  "IF"                 { return token("IF", yytext()); }
  "ELIF"               { return token("ELIF", yytext()); }
  "ELSE"               { return token("ELSE", yytext()); }
  "WHILE"              { return token("WHILE", yytext()); }
  "ALT_WHILE"          { return token("ALT_WHILE", yytext()); }
  "INDENT"             { return token("INDENT", yytext()); }
  "DEDENT"             { return token("DEDENT", yytext()); }
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

  "FIN_PROGRAM"        { return token("FIN_PROGRAM", yytext()); }
  "FIN_DECLARE"        { return token("FIN_DECLARE", yytext()); }

  /* Identificadores y Números */
  {Identifier}         { return token("ID", yytext()); }
  {DecIntegerLiteral}  { return token("CTE_INT", yytext()); }
  {FloatLiteral}       { return token("CTE_FLOAT", yytext()); }

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
  "["                  { return token("CORCH_A", yytext()); }
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

<CADENA> {
  \"                   { yybegin(YYINITIAL);
                         return token("CTE_STR", string.toString());
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