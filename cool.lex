/*
 * CS164: Spring 2004
 * Programming Assignment 2
 *
 * The scanner definition for Cool.
 *
 */

import java_cup.runtime.Symbol;

%%

/* Code enclosed in %{ %} is copied verbatim to the lexer class definition.
 * All extra variables/methods you want to use in the lexer actions go
 * here.  Don't remove anything that was here initially.  */
%{
    // Max size of string constants
    static int MAX_STR_CONST = 1024;

    // For assembling string constants
    StringBuffer string_buf = new StringBuffer();
    // For nested comments
    int commentdepth = 0;
    // For line numbers
    private int curr_lineno = 1;
    // EOF reached
    private boolean eof_reached = false;
    int get_curr_lineno() {
	return curr_lineno;
    }

    private AbstractSymbol filename;
    
    void set_filename(String fname) {
	filename = AbstractTable.stringtable.addString(fname);
    }

    AbstractSymbol curr_filename() {
	return filename;
    }

    /*
     * Add extra field and methods here.
     */

%}


/*  Code enclosed in %init{ %init} is copied verbatim to the lexer
 *  class constructor, all the extra initialization you want to do should
 *  go here. */
%init{
    // empty for now
%init}

/*  Code enclosed in %eofval{ %eofval} specifies java code that is
 *  executed when end-of-file is reached.  If you use multiple lexical
 *  states and want to do something special if an EOF is encountered in
 *  one of those states, place your code in the switch statement.
 *  Ultimately, you should return the EOF symbol, or your lexer won't
 *  work. */
%eofval{
    switch(yy_lexical_state) {
    case YYINITIAL:
	/* nothing special to do in the initial state */
	break;

        /* If necessary, add code for other states here, e.g: */
    case COMMENT: 
        if (!eof_reached) {
            eof_reached = true;
            return new Symbol(TokenConstants.ERROR, "EOF in comment");
        } else break;
    case STRING:
        if (!eof_reached) {
            eof_reached = true;
            return new Symbol(TokenConstants.ERROR, "EOF in string");
        } else break;
    }
    return new Symbol(TokenConstants.EOF);
%eofval}

/* Do not modify the following two jlex directives */
%class CoolLexer
%cup

VTAB = \x0b
/* Define names for regular expressions here. */
NEWLINE		= [\n]
WHITESPACE	= [" "|\b|\t|\r|\f]+
LINE_COMMENT = "--"[^\n]*\n
OPEN_COMMENT = "(*"
OBJECT_ID = [a-z][0-9a-zA-Z_]*
TYPE_ID = [A-Z][0-9a-zA-Z_]*
OPEN_STRING = [\"]




/* This defines a new start condition for line comments.
 * .
 * Hint: You might need additional start conditions. */


%state COMMENT
CLOSE_COMMENT = "*)"
OPEN_COMMENT = "(*"
NEWLINE = \n
CONTENT = [^"*)"|^"(*"|^\n]

%state STRING
STR_CONTENT = [^\"|^\\\n|^\0]
NEWLINE = \n 
NEWLINEPLUS = \\\n
ACCEPTED = \\.
NULL = \0
CLOSE_STRING = \"

/* Define lexical rules after the %% separator.  There is some code
 * provided for you that you may wish to use, but you may change it
 * if you like.
 * .
 * Some things you must fill-in (not necessarily a complete list):
 *   + Handle (* *) comments.  These comments should be properly nested.
 *   + Some additional multiple-character operators may be needed.  One
 *     (DARROW) is provided for you.
 *   + Handle strings.  String constants adhere to C syntax and may
 *     contain escape sequences: \c is accepted for all characters c
 *     (except for \n \t \b \f) in which case the result is c.
 * .
 * The complete Cool lexical specification is given in the Cool
 * Reference Manual (CoolAid).  Please be sure to look there. */
%%

<YYINITIAL>{NEWLINE}	 { curr_lineno += 1; } 
<YYINITIAL>{WHITESPACE} { /* Skip */ } 
<YYINITIAL>{VTAB} { /* Special case for VTAB, skip */ }
<YYINITIAL>{LINE_COMMENT}  {  
    curr_lineno += 1; 
} 
<YYINITIAL>{OPEN_COMMENT}  {
    commentdepth += 1; 
    yybegin(COMMENT);
}
<YYINITIAL>{OPEN_STRING} {
    string_buf = new StringBuffer();
    yybegin(STRING);
}

<COMMENT>{NEWLINE} { curr_lineno += 1; }
<COMMENT>{OPEN_COMMENT} { commentdepth += 1; }
<COMMENT>{CLOSE_COMMENT} { 
    commentdepth -= 1;
    if (commentdepth == 0) {
        yybegin(YYINITIAL);
    }    
}
<COMMENT>{CONTENT} { }

<STRING>{STR_CONTENT}* { string_buf.append(yytext()); }

<STRING>{NEWLINE}      { curr_lineno += 1; yybegin(YYINITIAL);
                           return new Symbol(TokenConstants.ERROR, "Unterminated string constant."); }

<STRING>{NULL}         { curr_lineno += 1; yybegin(YYINITIAL);
    return new Symbol(TokenConstants.ERROR, "String contains null character."); }

<STRING>{NEWLINEPLUS}  { curr_lineno += 1; string_buf.append("\n"); }

<STRING>{ACCEPTED} { String output = yytext();
                         if (output.equals("\\t")) string_buf.append("\t");
                         else if (output.equals("\\b")) string_buf.append("\b");
                         else if (output.equals("\\f")) string_buf.append("\f");
                         else if (output.equals("\\n")) string_buf.append("\n");
                         else string_buf.append(output.substring(1,2)); }

<STRING>{CLOSE_STRING} { yybegin(YYINITIAL); 
                        String output = string_buf.toString();
                        if (output.length() >= MAX_STR_CONST) {
                            return new Symbol(TokenConstants.ERROR, "String constant too long.");
                        } else {
                            return new Symbol(TokenConstants.STR_CONST,
                                AbstractTable.stringtable.addString(string_buf.toString())); }
                        }


 
<YYINITIAL>"=>"		{ return new Symbol(TokenConstants.DARROW); }





<YYINITIAL>[0-9][0-9]*  { /* Integers */
                          return new Symbol(TokenConstants.INT_CONST,
					    AbstractTable.inttable.addString(yytext())); }





<YYINITIAL>[Cc][Aa][Ss][Ee]	{ return new Symbol(TokenConstants.CASE); }
<YYINITIAL>[Cc][Ll][Aa][Ss][Ss] { return new Symbol(TokenConstants.CLASS); }
<YYINITIAL>[Ee][Ll][Ss][Ee]  	{ return new Symbol(TokenConstants.ELSE); }
<YYINITIAL>[Ee][Ss][Aa][Cc]	{ return new Symbol(TokenConstants.ESAC); }
<YYINITIAL>f[Aa][Ll][Ss][Ee]	{ return new Symbol(TokenConstants.BOOL_CONST, Boolean.FALSE); }
<YYINITIAL>[Ff][Ii]             { return new Symbol(TokenConstants.FI); }
<YYINITIAL>[Ii][Ff]  		{ return new Symbol(TokenConstants.IF); }
<YYINITIAL>[Ii][Nn]             { return new Symbol(TokenConstants.IN); }
<YYINITIAL>[Ii][Nn][Hh][Ee][Rr][Ii][Tt][Ss] { return new Symbol(TokenConstants.INHERITS); }
<YYINITIAL>[Ii][Ss][Vv][Oo][Ii][Dd] { return new Symbol(TokenConstants.ISVOID); }
<YYINITIAL>[Ll][Ee][Tt]         { return new Symbol(TokenConstants.LET); }
<YYINITIAL>[Ll][Oo][Oo][Pp]  	{ return new Symbol(TokenConstants.LOOP); }
<YYINITIAL>[Nn][Ee][Ww]		{ return new Symbol(TokenConstants.NEW); }
<YYINITIAL>[Nn][Oo][Tt] 	{ return new Symbol(TokenConstants.NOT); }
<YYINITIAL>[Oo][Ff]		{ return new Symbol(TokenConstants.OF); }
<YYINITIAL>[Pp][Oo][Oo][Ll]  	{ return new Symbol(TokenConstants.POOL); }
<YYINITIAL>[Tt][Hh][Ee][Nn]   	{ return new Symbol(TokenConstants.THEN); }
<YYINITIAL>t[Rr][Uu][Ee]	{ return new Symbol(TokenConstants.BOOL_CONST, Boolean.TRUE); }
<YYINITIAL>[Ww][Hh][Ii][Ll][Ee] { return new Symbol(TokenConstants.WHILE); }

<YYINITIAL>{OBJECT_ID}  {
    return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext()));
}
<YYINITIAL>{TYPE_ID}  {
    return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext()));
}




<YYINITIAL>"+"			{ return new Symbol(TokenConstants.PLUS); }
<YYINITIAL>"/"			{ return new Symbol(TokenConstants.DIV); }
<YYINITIAL>"-"			{ return new Symbol(TokenConstants.MINUS); }
<YYINITIAL>"*"			{ return new Symbol(TokenConstants.MULT); }
<YYINITIAL>"="			{ return new Symbol(TokenConstants.EQ); }
<YYINITIAL>"<"			{ return new Symbol(TokenConstants.LT); }
<YYINITIAL>"."			{ return new Symbol(TokenConstants.DOT); }
<YYINITIAL>"~"			{ return new Symbol(TokenConstants.NEG); }
<YYINITIAL>","			{ return new Symbol(TokenConstants.COMMA); }
<YYINITIAL>";"			{ return new Symbol(TokenConstants.SEMI); }
<YYINITIAL>":"			{ return new Symbol(TokenConstants.COLON); }
<YYINITIAL>"("			{ return new Symbol(TokenConstants.LPAREN); }
<YYINITIAL>")"			{ return new Symbol(TokenConstants.RPAREN); }
<YYINITIAL>"@"			{ return new Symbol(TokenConstants.AT); }
<YYINITIAL>"}"			{ return new Symbol(TokenConstants.RBRACE); }
<YYINITIAL>"{"			{ return new Symbol(TokenConstants.LBRACE); }
<YYINITIAL>"<="                 { return new Symbol(TokenConstants.LE); }
<YYINITIAL>"<-"                 { return new Symbol(TokenConstants.ASSIGN); }
<YYINITIAL>.                    { return new Symbol(TokenConstants.ERROR, yytext()); }

.                { /*
                    *  This should be the very last rule and will match
                    *  everything not matched by other lexical rules.
                    */
                   System.err.println("LEXER BUG - UNMATCHED: " + yytext()); }
