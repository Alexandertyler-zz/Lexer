/*
 * CS164: Spring 2004
 * Programming Assignment 2
 *
 * The scanner definition for Cool.
 *
 */
import java_cup.runtime.Symbol;


class CoolLexer implements java_cup.runtime.Scanner {
	private final int YY_BUFFER_SIZE = 512;
	private final int YY_F = -1;
	private final int YY_NO_STATE = -1;
	private final int YY_NOT_ACCEPT = 0;
	private final int YY_START = 1;
	private final int YY_END = 2;
	private final int YY_NO_ANCHOR = 4;
	private final int YY_BOL = 128;
	private final int YY_EOF = 129;

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
	private java.io.BufferedReader yy_reader;
	private int yy_buffer_index;
	private int yy_buffer_read;
	private int yy_buffer_start;
	private int yy_buffer_end;
	private char yy_buffer[];
	private boolean yy_at_bol;
	private int yy_lexical_state;

	CoolLexer (java.io.Reader reader) {
		this ();
		if (null == reader) {
			throw (new Error("Error: Bad input stream initializer."));
		}
		yy_reader = new java.io.BufferedReader(reader);
	}

	CoolLexer (java.io.InputStream instream) {
		this ();
		if (null == instream) {
			throw (new Error("Error: Bad input stream initializer."));
		}
		yy_reader = new java.io.BufferedReader(new java.io.InputStreamReader(instream));
	}

	private CoolLexer () {
		yy_buffer = new char[YY_BUFFER_SIZE];
		yy_buffer_read = 0;
		yy_buffer_index = 0;
		yy_buffer_start = 0;
		yy_buffer_end = 0;
		yy_at_bol = true;
		yy_lexical_state = YYINITIAL;

    // empty for now
	}

	private boolean yy_eof_done = false;
	private final int STRING = 2;
	private final int YYINITIAL = 0;
	private final int COMMENT = 1;
	private final int yy_state_dtrans[] = {
		0,
		71,
		75
	};
	private void yybegin (int state) {
		yy_lexical_state = state;
	}
	private int yy_advance ()
		throws java.io.IOException {
		int next_read;
		int i;
		int j;

		if (yy_buffer_index < yy_buffer_read) {
			return yy_buffer[yy_buffer_index++];
		}

		if (0 != yy_buffer_start) {
			i = yy_buffer_start;
			j = 0;
			while (i < yy_buffer_read) {
				yy_buffer[j] = yy_buffer[i];
				++i;
				++j;
			}
			yy_buffer_end = yy_buffer_end - yy_buffer_start;
			yy_buffer_start = 0;
			yy_buffer_read = j;
			yy_buffer_index = j;
			next_read = yy_reader.read(yy_buffer,
					yy_buffer_read,
					yy_buffer.length - yy_buffer_read);
			if (-1 == next_read) {
				return YY_EOF;
			}
			yy_buffer_read = yy_buffer_read + next_read;
		}

		while (yy_buffer_index >= yy_buffer_read) {
			if (yy_buffer_index >= yy_buffer.length) {
				yy_buffer = yy_double(yy_buffer);
			}
			next_read = yy_reader.read(yy_buffer,
					yy_buffer_read,
					yy_buffer.length - yy_buffer_read);
			if (-1 == next_read) {
				return YY_EOF;
			}
			yy_buffer_read = yy_buffer_read + next_read;
		}
		return yy_buffer[yy_buffer_index++];
	}
	private void yy_move_end () {
		if (yy_buffer_end > yy_buffer_start &&
		    '\n' == yy_buffer[yy_buffer_end-1])
			yy_buffer_end--;
		if (yy_buffer_end > yy_buffer_start &&
		    '\r' == yy_buffer[yy_buffer_end-1])
			yy_buffer_end--;
	}
	private boolean yy_last_was_cr=false;
	private void yy_mark_start () {
		yy_buffer_start = yy_buffer_index;
	}
	private void yy_mark_end () {
		yy_buffer_end = yy_buffer_index;
	}
	private void yy_to_mark () {
		yy_buffer_index = yy_buffer_end;
		yy_at_bol = (yy_buffer_end > yy_buffer_start) &&
		            ('\r' == yy_buffer[yy_buffer_end-1] ||
		             '\n' == yy_buffer[yy_buffer_end-1] ||
		             2028/*LS*/ == yy_buffer[yy_buffer_end-1] ||
		             2029/*PS*/ == yy_buffer[yy_buffer_end-1]);
	}
	private java.lang.String yytext () {
		return (new java.lang.String(yy_buffer,
			yy_buffer_start,
			yy_buffer_end - yy_buffer_start));
	}
	private int yylength () {
		return yy_buffer_end - yy_buffer_start;
	}
	private char[] yy_double (char buf[]) {
		int i;
		char newbuf[];
		newbuf = new char[2*buf.length];
		for (i = 0; i < buf.length; ++i) {
			newbuf[i] = buf[i];
		}
		return newbuf;
	}
	private final int YY_E_INTERNAL = 0;
	private final int YY_E_MATCH = 1;
	private java.lang.String yy_error_string[] = {
		"Error: Internal error.\n",
		"Error: Unmatched input.\n"
	};
	private void yy_error (int code,boolean fatal) {
		java.lang.System.out.print(yy_error_string[code]);
		java.lang.System.out.flush();
		if (fatal) {
			throw new Error("Fatal Error.\n");
		}
	}
	private int[][] unpackFromString(int size1, int size2, String st) {
		int colonIndex = -1;
		String lengthString;
		int sequenceLength = 0;
		int sequenceInteger = 0;

		int commaIndex;
		String workString;

		int res[][] = new int[size1][size2];
		for (int i= 0; i < size1; i++) {
			for (int j= 0; j < size2; j++) {
				if (sequenceLength != 0) {
					res[i][j] = sequenceInteger;
					sequenceLength--;
					continue;
				}
				commaIndex = st.indexOf(',');
				workString = (commaIndex==-1) ? st :
					st.substring(0, commaIndex);
				st = st.substring(commaIndex+1);
				colonIndex = workString.indexOf(':');
				if (colonIndex == -1) {
					res[i][j]=Integer.parseInt(workString);
					continue;
				}
				lengthString =
					workString.substring(colonIndex+1);
				sequenceLength=Integer.parseInt(lengthString);
				workString=workString.substring(0,colonIndex);
				sequenceInteger=Integer.parseInt(workString);
				res[i][j] = sequenceInteger;
				sequenceLength--;
			}
		}
		return res;
	}
	private int yy_acpt[] = {
		/* 0 */ YY_NOT_ACCEPT,
		/* 1 */ YY_NO_ANCHOR,
		/* 2 */ YY_NO_ANCHOR,
		/* 3 */ YY_NO_ANCHOR,
		/* 4 */ YY_NO_ANCHOR,
		/* 5 */ YY_NO_ANCHOR,
		/* 6 */ YY_NO_ANCHOR,
		/* 7 */ YY_NO_ANCHOR,
		/* 8 */ YY_NO_ANCHOR,
		/* 9 */ YY_NO_ANCHOR,
		/* 10 */ YY_NO_ANCHOR,
		/* 11 */ YY_NO_ANCHOR,
		/* 12 */ YY_NO_ANCHOR,
		/* 13 */ YY_NO_ANCHOR,
		/* 14 */ YY_NO_ANCHOR,
		/* 15 */ YY_NO_ANCHOR,
		/* 16 */ YY_NO_ANCHOR,
		/* 17 */ YY_NO_ANCHOR,
		/* 18 */ YY_NO_ANCHOR,
		/* 19 */ YY_NO_ANCHOR,
		/* 20 */ YY_NO_ANCHOR,
		/* 21 */ YY_NO_ANCHOR,
		/* 22 */ YY_NO_ANCHOR,
		/* 23 */ YY_NO_ANCHOR,
		/* 24 */ YY_NO_ANCHOR,
		/* 25 */ YY_NO_ANCHOR,
		/* 26 */ YY_NO_ANCHOR,
		/* 27 */ YY_NO_ANCHOR,
		/* 28 */ YY_NO_ANCHOR,
		/* 29 */ YY_NO_ANCHOR,
		/* 30 */ YY_NO_ANCHOR,
		/* 31 */ YY_NO_ANCHOR,
		/* 32 */ YY_NO_ANCHOR,
		/* 33 */ YY_NO_ANCHOR,
		/* 34 */ YY_NO_ANCHOR,
		/* 35 */ YY_NO_ANCHOR,
		/* 36 */ YY_NO_ANCHOR,
		/* 37 */ YY_NO_ANCHOR,
		/* 38 */ YY_NO_ANCHOR,
		/* 39 */ YY_NO_ANCHOR,
		/* 40 */ YY_NO_ANCHOR,
		/* 41 */ YY_NO_ANCHOR,
		/* 42 */ YY_NO_ANCHOR,
		/* 43 */ YY_NO_ANCHOR,
		/* 44 */ YY_NO_ANCHOR,
		/* 45 */ YY_NO_ANCHOR,
		/* 46 */ YY_NO_ANCHOR,
		/* 47 */ YY_NO_ANCHOR,
		/* 48 */ YY_NO_ANCHOR,
		/* 49 */ YY_NO_ANCHOR,
		/* 50 */ YY_NOT_ACCEPT,
		/* 51 */ YY_NO_ANCHOR,
		/* 52 */ YY_NO_ANCHOR,
		/* 53 */ YY_NO_ANCHOR,
		/* 54 */ YY_NO_ANCHOR,
		/* 55 */ YY_NO_ANCHOR,
		/* 56 */ YY_NO_ANCHOR,
		/* 57 */ YY_NO_ANCHOR,
		/* 58 */ YY_NO_ANCHOR,
		/* 59 */ YY_NO_ANCHOR,
		/* 60 */ YY_NO_ANCHOR,
		/* 61 */ YY_NO_ANCHOR,
		/* 62 */ YY_NO_ANCHOR,
		/* 63 */ YY_NO_ANCHOR,
		/* 64 */ YY_NO_ANCHOR,
		/* 65 */ YY_NO_ANCHOR,
		/* 66 */ YY_NO_ANCHOR,
		/* 67 */ YY_NO_ANCHOR,
		/* 68 */ YY_NO_ANCHOR,
		/* 69 */ YY_NO_ANCHOR,
		/* 70 */ YY_NO_ANCHOR,
		/* 71 */ YY_NOT_ACCEPT,
		/* 72 */ YY_NO_ANCHOR,
		/* 73 */ YY_NO_ANCHOR,
		/* 74 */ YY_NO_ANCHOR,
		/* 75 */ YY_NOT_ACCEPT,
		/* 76 */ YY_NO_ANCHOR,
		/* 77 */ YY_NO_ANCHOR,
		/* 78 */ YY_NO_ANCHOR,
		/* 79 */ YY_NO_ANCHOR,
		/* 80 */ YY_NO_ANCHOR,
		/* 81 */ YY_NO_ANCHOR,
		/* 82 */ YY_NO_ANCHOR,
		/* 83 */ YY_NO_ANCHOR,
		/* 84 */ YY_NO_ANCHOR,
		/* 85 */ YY_NO_ANCHOR,
		/* 86 */ YY_NO_ANCHOR,
		/* 87 */ YY_NO_ANCHOR,
		/* 88 */ YY_NO_ANCHOR,
		/* 89 */ YY_NO_ANCHOR,
		/* 90 */ YY_NO_ANCHOR,
		/* 91 */ YY_NO_ANCHOR,
		/* 92 */ YY_NO_ANCHOR,
		/* 93 */ YY_NO_ANCHOR,
		/* 94 */ YY_NO_ANCHOR,
		/* 95 */ YY_NO_ANCHOR,
		/* 96 */ YY_NO_ANCHOR,
		/* 97 */ YY_NO_ANCHOR,
		/* 98 */ YY_NO_ANCHOR,
		/* 99 */ YY_NO_ANCHOR,
		/* 100 */ YY_NO_ANCHOR,
		/* 101 */ YY_NO_ANCHOR,
		/* 102 */ YY_NO_ANCHOR,
		/* 103 */ YY_NO_ANCHOR,
		/* 104 */ YY_NO_ANCHOR,
		/* 105 */ YY_NO_ANCHOR,
		/* 106 */ YY_NO_ANCHOR,
		/* 107 */ YY_NO_ANCHOR,
		/* 108 */ YY_NO_ANCHOR,
		/* 109 */ YY_NO_ANCHOR,
		/* 110 */ YY_NO_ANCHOR,
		/* 111 */ YY_NO_ANCHOR,
		/* 112 */ YY_NO_ANCHOR,
		/* 113 */ YY_NO_ANCHOR,
		/* 114 */ YY_NO_ANCHOR,
		/* 115 */ YY_NO_ANCHOR,
		/* 116 */ YY_NO_ANCHOR,
		/* 117 */ YY_NO_ANCHOR,
		/* 118 */ YY_NO_ANCHOR,
		/* 119 */ YY_NO_ANCHOR,
		/* 120 */ YY_NO_ANCHOR,
		/* 121 */ YY_NO_ANCHOR,
		/* 122 */ YY_NO_ANCHOR,
		/* 123 */ YY_NO_ANCHOR,
		/* 124 */ YY_NO_ANCHOR,
		/* 125 */ YY_NO_ANCHOR,
		/* 126 */ YY_NO_ANCHOR,
		/* 127 */ YY_NO_ANCHOR,
		/* 128 */ YY_NO_ANCHOR,
		/* 129 */ YY_NO_ANCHOR,
		/* 130 */ YY_NO_ANCHOR,
		/* 131 */ YY_NO_ANCHOR,
		/* 132 */ YY_NO_ANCHOR,
		/* 133 */ YY_NO_ANCHOR,
		/* 134 */ YY_NO_ANCHOR,
		/* 135 */ YY_NO_ANCHOR,
		/* 136 */ YY_NO_ANCHOR,
		/* 137 */ YY_NO_ANCHOR,
		/* 138 */ YY_NO_ANCHOR,
		/* 139 */ YY_NO_ANCHOR,
		/* 140 */ YY_NO_ANCHOR,
		/* 141 */ YY_NO_ANCHOR,
		/* 142 */ YY_NO_ANCHOR,
		/* 143 */ YY_NO_ANCHOR,
		/* 144 */ YY_NO_ANCHOR,
		/* 145 */ YY_NO_ANCHOR,
		/* 146 */ YY_NO_ANCHOR,
		/* 147 */ YY_NO_ANCHOR,
		/* 148 */ YY_NO_ANCHOR,
		/* 149 */ YY_NO_ANCHOR,
		/* 150 */ YY_NO_ANCHOR,
		/* 151 */ YY_NO_ANCHOR,
		/* 152 */ YY_NO_ANCHOR,
		/* 153 */ YY_NO_ANCHOR,
		/* 154 */ YY_NO_ANCHOR,
		/* 155 */ YY_NO_ANCHOR,
		/* 156 */ YY_NO_ANCHOR
	};
	private int yy_cmap[] = unpackFromString(1,130,
"9:8,3:2,1,3:2,10,9:18,3,9:7,6,8,7,51,56,4,54,52,13:10,58,57,53,11,12,9,59,1" +
"5,50,14,28,17,20,50,23,21,50:2,18,50,22,27,29,50,24,16,25,32,26,30,50:3,9:3" +
",5,49,9,33,34,35,36,37,19,34,38,39,34:2,40,34,41,42,43,34,44,45,31,46,47,48" +
",34:3,61,2,60,55,9,0:2")[0];

	private int yy_rmap[] = unpackFromString(1,157,
"0,1:2,2,3,1,4,1:2,5,6,7,8,1:13,9,10,11,10,1,10:9,9,10,9,10:3,1:4,12,13,14,1" +
"5,10,9,16,9:14,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31,32,33,34,35,36," +
"37,38,39,40,41,42,43,44,45,46,47,48,49,50,51,52,53,54,55,56,57,58,59,60,61," +
"62,63,64,65,66,67,68,69,70,71,72,73,74,75,76,77,78,79,80,81,82,83,84,85,86," +
"87,88,89,90,91,10,9,92,93,94,95,96,97,98,99,100")[0];

	private int yy_nxt[][] = unpackFromString(101,62,
"1,2,3:2,4,5,6,7,8,5,3,9,5,10,11,146:2,148,106,12,52,73,108,146:2,150,146,76" +
",146,152,154,53,146,147:2,149,147,151,147,74,107,109,77,153,147:4,155,5,146" +
",13,14,15,16,17,18,19,20,21,22,23,-1:64,3:2,-1:6,3,-1:55,50,-1:64,24,-1:66," +
"25,-1:62,10,-1:61,146:2,110,146:2,112,146:14,110,146:6,112,146:10,-1:24,147" +
":2,156,147:5,26,147:11,156,147:5,26,147:11,-1:24,147:38,-1:24,146:38,-1:24," +
"146:10,132,146:14,132,146:12,-1:12,30,50:60,-1:7,48,-1:67,146:8,54,146:17,5" +
"4,146:11,-1:24,147:10,111,113,147:13,111,147:5,113,147:6,-1:24,147:10,137,1" +
"47:14,137,147:12,-1:11,1,46,5,47:2,5,51,72,5,47:53,-1:8,49,-1:66,146:3,120," +
"146:2,27:2,146,28,146:18,28,146:3,120,146:5,-1:24,147:3,123,147:2,55:2,147," +
"56,147:18,56,147:3,123,147:5,-1:11,1,-1,5:8,-1,5:51,-1:13,146:6,29:2,146:30" +
",-1:24,147:6,57:2,147:30,-1:24,146:12,31,146:5,31,146:19,-1:24,147:12,58,14" +
"7:5,58,147:19,-1:24,146:17,32,146:17,32,146:2,-1:24,147:17,59,147:17,59,147" +
":2,-1:24,146:12,33,146:5,33,146:19,-1:24,147:12,60,147:5,60,147:19,-1:24,14" +
"6:4,34,146:19,34,146:13,-1:24,147:9,65,147:18,65,147:9,-1:24,146,35,146:20," +
"35,146:15,-1:24,147:4,40,147:19,40,147:13,-1:24,146:4,36,146:19,36,146:13,-" +
"1:24,147:4,61,147:19,61,147:13,-1:24,146:16,37,146:13,37,146:7,-1:24,147,62" +
",147:20,62,147:15,-1:24,146:9,38,146:18,38,146:9,-1:24,147:4,63,147:19,63,1" +
"47:13,-1:24,146:5,39,146:21,39,146:10,-1:24,147:16,64,147:13,64,147:7,-1:24" +
",146:3,41,146:28,41,146:5,-1:24,147:5,66,147:21,66,147:10,-1:24,146:4,43,14" +
"6:19,43,146:13,-1:24,147:4,42,147:19,42,147:13,-1:24,146:15,44,146:7,44,146" +
":14,-1:24,147:3,67,147:28,67,147:5,-1:24,146:3,45,146:28,45,146:5,-1:24,147" +
":4,68,147:19,68,147:13,-1:24,147:15,69,147:7,69,147:14,-1:24,147:3,70,147:2" +
"8,70,147:5,-1:24,146:4,78,146:9,118,146:9,78,146:4,118,146:8,-1:24,147:4,79" +
",147:9,125,147:9,79,147:4,125,147:8,-1:24,146:4,80,146:9,82,146:9,80,146:4," +
"82,146:8,-1:24,147:4,81,147:9,83,147:9,81,147:4,83,147:8,-1:24,146:3,84,146" +
":28,84,146:5,-1:24,147:4,85,147:19,85,147:13,-1:24,146:2,128,146:17,128,146" +
":17,-1:24,147:19,87,147:13,87,147:4,-1:24,146:2,86,146:17,86,146:17,-1:24,1" +
"47:3,89,147:28,89,147:5,-1:24,146:3,88,146:28,88,146:5,-1:24,147:2,133,147:" +
"17,133,147:17,-1:24,146:14,90,146:14,90,146:8,-1:24,147:2,91,147:17,91,147:" +
"17,-1:24,146:13,130,146:20,130,146:3,-1:24,147:3,93,147:28,93,147:5,-1:24,1" +
"46:4,92,146:19,92,146:13,-1:24,147:13,135,147:20,135,147:3,-1:24,146:14,94," +
"146:14,94,146:8,-1:24,147:14,95,147:14,95,147:8,-1:24,146:8,134,146:17,134," +
"146:11,-1:24,147:14,97,147:14,97,147:8,-1:24,146:3,96,146:28,96,146:5,-1:24" +
",147:8,139,147:17,139,147:11,-1:24,146:14,136,146:14,136,146:8,-1:24,147:3," +
"99,147:28,99,147:5,-1:24,146:4,138,146:19,138,146:13,-1:24,147:3,101,147:28" +
",101,147:5,-1:24,146:5,98,146:21,98,146:10,-1:24,147:14,141,147:14,141,147:" +
"8,-1:24,146:8,100,146:17,100,146:11,-1:24,147:4,143,147:19,143,147:13,-1:24" +
",146:11,140,146:19,140,146:6,-1:24,147:5,103,147:21,103,147:10,-1:24,146:8," +
"142,146:17,142,146:11,-1:24,147:8,104,147:17,104,147:11,-1:24,146:12,102,14" +
"6:5,102,146:19,-1:24,147:11,144,147:19,144,147:6,-1:24,147:8,145,147:17,145" +
",147:11,-1:24,147:12,105,147:5,105,147:19,-1:24,146:3,114,146,116,146:21,11" +
"6,146:4,114,146:5,-1:24,147:2,115,147:2,117,147:14,115,147:6,117,147:10,-1:" +
"24,146:10,122,146:14,122,146:12,-1:24,147:3,119,147,121,147:21,121,147:4,11" +
"9,147:5,-1:24,146:14,124,146:14,124,146:8,-1:24,147:14,127,147:14,127,147:8" +
",-1:24,146:10,126,146:14,126,146:12,-1:24,147:10,129,147:14,129,147:12,-1:2" +
"4,147:5,131,147:21,131,147:10,-1:11");

	public java_cup.runtime.Symbol next_token ()
		throws java.io.IOException {
		int yy_lookahead;
		int yy_anchor = YY_NO_ANCHOR;
		int yy_state = yy_state_dtrans[yy_lexical_state];
		int yy_next_state = YY_NO_STATE;
		int yy_last_accept_state = YY_NO_STATE;
		boolean yy_initial = true;
		int yy_this_accept;

		yy_mark_start();
		yy_this_accept = yy_acpt[yy_state];
		if (YY_NOT_ACCEPT != yy_this_accept) {
			yy_last_accept_state = yy_state;
			yy_mark_end();
		}
		while (true) {
			if (yy_initial && yy_at_bol) yy_lookahead = YY_BOL;
			else yy_lookahead = yy_advance();
			yy_next_state = YY_F;
			yy_next_state = yy_nxt[yy_rmap[yy_state]][yy_cmap[yy_lookahead]];
			if (YY_EOF == yy_lookahead && true == yy_initial) {

    switch(yy_lexical_state) {
    case YYINITIAL:
	/* nothing special to do in the initial state */
	break;
        /* If necessary, add code for other states here, e.g: */
    case COMMENT: 
        if (!eof_reached) {
            eof_reached = true;
            return new Symbol(TokenConstants.ERROR, "EOF before closed comment");
        } else break;
    case STRING:
        if (!eof_reached) {
            eof_reached = true;
            return new Symbol(TokenConstants.ERROR, "EOF before closed string");
        } else break;
    }
    return new Symbol(TokenConstants.EOF);
			}
			if (YY_F != yy_next_state) {
				yy_state = yy_next_state;
				yy_initial = false;
				yy_this_accept = yy_acpt[yy_state];
				if (YY_NOT_ACCEPT != yy_this_accept) {
					yy_last_accept_state = yy_state;
					yy_mark_end();
				}
			}
			else {
				if (YY_NO_STATE == yy_last_accept_state) {
					throw (new Error("Lexical Error: Unmatched Input."));
				}
				else {
					yy_anchor = yy_acpt[yy_last_accept_state];
					if (0 != (YY_END & yy_anchor)) {
						yy_move_end();
					}
					yy_to_mark();
					switch (yy_last_accept_state) {
					case 1:
						
					case -2:
						break;
					case 2:
						{ curr_lineno += 1; }
					case -3:
						break;
					case 3:
						{ /* Skip */ }
					case -4:
						break;
					case 4:
						{ return new Symbol(TokenConstants.MINUS); }
					case -5:
						break;
					case 5:
						{ /*
                    *  This should be the very last rule and will match
                    *  everything not matched by other lexical rules.
                    */
                   System.err.println("LEXER BUG - UNMATCHED: " + yytext()); }
					case -6:
						break;
					case 6:
						{ return new Symbol(TokenConstants.LPAREN); }
					case -7:
						break;
					case 7:
						{ return new Symbol(TokenConstants.MULT); }
					case -8:
						break;
					case 8:
						{ return new Symbol(TokenConstants.RPAREN); }
					case -9:
						break;
					case 9:
						{ return new Symbol(TokenConstants.EQ); }
					case -10:
						break;
					case 10:
						{ /* Integers */
                          return new Symbol(TokenConstants.INT_CONST,
					    AbstractTable.inttable.addString(yytext())); }
					case -11:
						break;
					case 11:
						{
    return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext()));
}
					case -12:
						break;
					case 12:
						{
    return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext()));
}
					case -13:
						break;
					case 13:
						{ return new Symbol(TokenConstants.PLUS); }
					case -14:
						break;
					case 14:
						{ return new Symbol(TokenConstants.DIV); }
					case -15:
						break;
					case 15:
						{ return new Symbol(TokenConstants.LT); }
					case -16:
						break;
					case 16:
						{ return new Symbol(TokenConstants.DOT); }
					case -17:
						break;
					case 17:
						{ return new Symbol(TokenConstants.NEG); }
					case -18:
						break;
					case 18:
						{ return new Symbol(TokenConstants.COMMA); }
					case -19:
						break;
					case 19:
						{ return new Symbol(TokenConstants.SEMI); }
					case -20:
						break;
					case 20:
						{ return new Symbol(TokenConstants.COLON); }
					case -21:
						break;
					case 21:
						{ return new Symbol(TokenConstants.AT); }
					case -22:
						break;
					case 22:
						{ return new Symbol(TokenConstants.RBRACE); }
					case -23:
						break;
					case 23:
						{ return new Symbol(TokenConstants.LBRACE); }
					case -24:
						break;
					case 24:
						{
    commentdepth += 1; 
    yybegin(COMMENT);
}
					case -25:
						break;
					case 25:
						{ return new Symbol(TokenConstants.DARROW); }
					case -26:
						break;
					case 26:
						{ return new Symbol(TokenConstants.FI); }
					case -27:
						break;
					case 27:
						{ return new Symbol(TokenConstants.IF); }
					case -28:
						break;
					case 28:
						{ return new Symbol(TokenConstants.IN); }
					case -29:
						break;
					case 29:
						{ return new Symbol(TokenConstants.OF); }
					case -30:
						break;
					case 30:
						{  
    curr_lineno += 1; 
}
					case -31:
						break;
					case 31:
						{ return new Symbol(TokenConstants.LET); }
					case -32:
						break;
					case 32:
						{ return new Symbol(TokenConstants.NEW); }
					case -33:
						break;
					case 33:
						{ return new Symbol(TokenConstants.NOT); }
					case -34:
						break;
					case 34:
						{ return new Symbol(TokenConstants.CASE); }
					case -35:
						break;
					case 35:
						{ return new Symbol(TokenConstants.ESAC); }
					case -36:
						break;
					case 36:
						{ return new Symbol(TokenConstants.ELSE); }
					case -37:
						break;
					case 37:
						{ return new Symbol(TokenConstants.LOOP); }
					case -38:
						break;
					case 38:
						{ return new Symbol(TokenConstants.THEN); }
					case -39:
						break;
					case 39:
						{ return new Symbol(TokenConstants.POOL); }
					case -40:
						break;
					case 40:
						{ return new Symbol(TokenConstants.BOOL_CONST, Boolean.TRUE); }
					case -41:
						break;
					case 41:
						{ return new Symbol(TokenConstants.CLASS); }
					case -42:
						break;
					case 42:
						{ return new Symbol(TokenConstants.BOOL_CONST, Boolean.FALSE); }
					case -43:
						break;
					case 43:
						{ return new Symbol(TokenConstants.WHILE); }
					case -44:
						break;
					case 44:
						{ return new Symbol(TokenConstants.ISVOID); }
					case -45:
						break;
					case 45:
						{ return new Symbol(TokenConstants.INHERITS); }
					case -46:
						break;
					case 46:
						{ curr_lineno += 1; }
					case -47:
						break;
					case 47:
						{ }
					case -48:
						break;
					case 48:
						{ commentdepth += 1; }
					case -49:
						break;
					case 49:
						{ 
    commentdepth -= 1;
    if (commentdepth == 0) {
        yybegin(YYINITIAL);
    }    
}
					case -50:
						break;
					case 51:
						{ /*
                    *  This should be the very last rule and will match
                    *  everything not matched by other lexical rules.
                    */
                   System.err.println("LEXER BUG - UNMATCHED: " + yytext()); }
					case -51:
						break;
					case 52:
						{
    return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext()));
}
					case -52:
						break;
					case 53:
						{
    return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext()));
}
					case -53:
						break;
					case 54:
						{ return new Symbol(TokenConstants.FI); }
					case -54:
						break;
					case 55:
						{ return new Symbol(TokenConstants.IF); }
					case -55:
						break;
					case 56:
						{ return new Symbol(TokenConstants.IN); }
					case -56:
						break;
					case 57:
						{ return new Symbol(TokenConstants.OF); }
					case -57:
						break;
					case 58:
						{ return new Symbol(TokenConstants.LET); }
					case -58:
						break;
					case 59:
						{ return new Symbol(TokenConstants.NEW); }
					case -59:
						break;
					case 60:
						{ return new Symbol(TokenConstants.NOT); }
					case -60:
						break;
					case 61:
						{ return new Symbol(TokenConstants.CASE); }
					case -61:
						break;
					case 62:
						{ return new Symbol(TokenConstants.ESAC); }
					case -62:
						break;
					case 63:
						{ return new Symbol(TokenConstants.ELSE); }
					case -63:
						break;
					case 64:
						{ return new Symbol(TokenConstants.LOOP); }
					case -64:
						break;
					case 65:
						{ return new Symbol(TokenConstants.THEN); }
					case -65:
						break;
					case 66:
						{ return new Symbol(TokenConstants.POOL); }
					case -66:
						break;
					case 67:
						{ return new Symbol(TokenConstants.CLASS); }
					case -67:
						break;
					case 68:
						{ return new Symbol(TokenConstants.WHILE); }
					case -68:
						break;
					case 69:
						{ return new Symbol(TokenConstants.ISVOID); }
					case -69:
						break;
					case 70:
						{ return new Symbol(TokenConstants.INHERITS); }
					case -70:
						break;
					case 72:
						{ /*
                    *  This should be the very last rule and will match
                    *  everything not matched by other lexical rules.
                    */
                   System.err.println("LEXER BUG - UNMATCHED: " + yytext()); }
					case -71:
						break;
					case 73:
						{
    return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext()));
}
					case -72:
						break;
					case 74:
						{
    return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext()));
}
					case -73:
						break;
					case 76:
						{
    return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext()));
}
					case -74:
						break;
					case 77:
						{
    return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext()));
}
					case -75:
						break;
					case 78:
						{
    return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext()));
}
					case -76:
						break;
					case 79:
						{
    return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext()));
}
					case -77:
						break;
					case 80:
						{
    return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext()));
}
					case -78:
						break;
					case 81:
						{
    return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext()));
}
					case -79:
						break;
					case 82:
						{
    return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext()));
}
					case -80:
						break;
					case 83:
						{
    return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext()));
}
					case -81:
						break;
					case 84:
						{
    return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext()));
}
					case -82:
						break;
					case 85:
						{
    return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext()));
}
					case -83:
						break;
					case 86:
						{
    return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext()));
}
					case -84:
						break;
					case 87:
						{
    return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext()));
}
					case -85:
						break;
					case 88:
						{
    return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext()));
}
					case -86:
						break;
					case 89:
						{
    return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext()));
}
					case -87:
						break;
					case 90:
						{
    return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext()));
}
					case -88:
						break;
					case 91:
						{
    return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext()));
}
					case -89:
						break;
					case 92:
						{
    return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext()));
}
					case -90:
						break;
					case 93:
						{
    return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext()));
}
					case -91:
						break;
					case 94:
						{
    return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext()));
}
					case -92:
						break;
					case 95:
						{
    return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext()));
}
					case -93:
						break;
					case 96:
						{
    return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext()));
}
					case -94:
						break;
					case 97:
						{
    return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext()));
}
					case -95:
						break;
					case 98:
						{
    return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext()));
}
					case -96:
						break;
					case 99:
						{
    return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext()));
}
					case -97:
						break;
					case 100:
						{
    return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext()));
}
					case -98:
						break;
					case 101:
						{
    return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext()));
}
					case -99:
						break;
					case 102:
						{
    return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext()));
}
					case -100:
						break;
					case 103:
						{
    return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext()));
}
					case -101:
						break;
					case 104:
						{
    return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext()));
}
					case -102:
						break;
					case 105:
						{
    return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext()));
}
					case -103:
						break;
					case 106:
						{
    return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext()));
}
					case -104:
						break;
					case 107:
						{
    return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext()));
}
					case -105:
						break;
					case 108:
						{
    return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext()));
}
					case -106:
						break;
					case 109:
						{
    return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext()));
}
					case -107:
						break;
					case 110:
						{
    return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext()));
}
					case -108:
						break;
					case 111:
						{
    return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext()));
}
					case -109:
						break;
					case 112:
						{
    return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext()));
}
					case -110:
						break;
					case 113:
						{
    return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext()));
}
					case -111:
						break;
					case 114:
						{
    return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext()));
}
					case -112:
						break;
					case 115:
						{
    return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext()));
}
					case -113:
						break;
					case 116:
						{
    return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext()));
}
					case -114:
						break;
					case 117:
						{
    return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext()));
}
					case -115:
						break;
					case 118:
						{
    return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext()));
}
					case -116:
						break;
					case 119:
						{
    return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext()));
}
					case -117:
						break;
					case 120:
						{
    return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext()));
}
					case -118:
						break;
					case 121:
						{
    return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext()));
}
					case -119:
						break;
					case 122:
						{
    return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext()));
}
					case -120:
						break;
					case 123:
						{
    return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext()));
}
					case -121:
						break;
					case 124:
						{
    return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext()));
}
					case -122:
						break;
					case 125:
						{
    return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext()));
}
					case -123:
						break;
					case 126:
						{
    return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext()));
}
					case -124:
						break;
					case 127:
						{
    return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext()));
}
					case -125:
						break;
					case 128:
						{
    return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext()));
}
					case -126:
						break;
					case 129:
						{
    return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext()));
}
					case -127:
						break;
					case 130:
						{
    return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext()));
}
					case -128:
						break;
					case 131:
						{
    return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext()));
}
					case -129:
						break;
					case 132:
						{
    return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext()));
}
					case -130:
						break;
					case 133:
						{
    return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext()));
}
					case -131:
						break;
					case 134:
						{
    return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext()));
}
					case -132:
						break;
					case 135:
						{
    return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext()));
}
					case -133:
						break;
					case 136:
						{
    return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext()));
}
					case -134:
						break;
					case 137:
						{
    return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext()));
}
					case -135:
						break;
					case 138:
						{
    return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext()));
}
					case -136:
						break;
					case 139:
						{
    return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext()));
}
					case -137:
						break;
					case 140:
						{
    return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext()));
}
					case -138:
						break;
					case 141:
						{
    return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext()));
}
					case -139:
						break;
					case 142:
						{
    return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext()));
}
					case -140:
						break;
					case 143:
						{
    return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext()));
}
					case -141:
						break;
					case 144:
						{
    return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext()));
}
					case -142:
						break;
					case 145:
						{
    return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext()));
}
					case -143:
						break;
					case 146:
						{
    return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext()));
}
					case -144:
						break;
					case 147:
						{
    return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext()));
}
					case -145:
						break;
					case 148:
						{
    return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext()));
}
					case -146:
						break;
					case 149:
						{
    return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext()));
}
					case -147:
						break;
					case 150:
						{
    return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext()));
}
					case -148:
						break;
					case 151:
						{
    return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext()));
}
					case -149:
						break;
					case 152:
						{
    return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext()));
}
					case -150:
						break;
					case 153:
						{
    return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext()));
}
					case -151:
						break;
					case 154:
						{
    return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext()));
}
					case -152:
						break;
					case 155:
						{
    return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext()));
}
					case -153:
						break;
					case 156:
						{
    return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext()));
}
					case -154:
						break;
					default:
						yy_error(YY_E_INTERNAL,false);
					case -1:
					}
					yy_initial = true;
					yy_state = yy_state_dtrans[yy_lexical_state];
					yy_next_state = YY_NO_STATE;
					yy_last_accept_state = YY_NO_STATE;
					yy_mark_start();
					yy_this_accept = yy_acpt[yy_state];
					if (YY_NOT_ACCEPT != yy_this_accept) {
						yy_last_accept_state = yy_state;
						yy_mark_end();
					}
				}
			}
		}
	}
}
