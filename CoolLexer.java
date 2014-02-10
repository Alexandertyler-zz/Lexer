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
		76,
		51
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
		/* 50 */ YY_NO_ANCHOR,
		/* 51 */ YY_NO_ANCHOR,
		/* 52 */ YY_NO_ANCHOR,
		/* 53 */ YY_NO_ANCHOR,
		/* 54 */ YY_NOT_ACCEPT,
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
		/* 71 */ YY_NO_ANCHOR,
		/* 72 */ YY_NO_ANCHOR,
		/* 73 */ YY_NO_ANCHOR,
		/* 74 */ YY_NO_ANCHOR,
		/* 75 */ YY_NO_ANCHOR,
		/* 76 */ YY_NOT_ACCEPT,
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
		/* 156 */ YY_NO_ANCHOR,
		/* 157 */ YY_NO_ANCHOR,
		/* 158 */ YY_NO_ANCHOR,
		/* 159 */ YY_NO_ANCHOR,
		/* 160 */ YY_NO_ANCHOR
	};
	private int yy_cmap[] = unpackFromString(1,130,
"10:8,3:2,1,3:3,10:18,3,10,8,10:5,6,9,7,51,56,4,54,52,13:10,58,57,53,11,12,1" +
"0,59,15,50,14,28,17,20,50,23,21,50:2,18,50,22,27,29,50,24,16,25,32,26,30,50" +
":3,10:3,5,49,10,33,34,35,36,37,19,34,38,39,34:2,40,34,41,42,43,34,44,45,31," +
"46,47,48,34:3,61,2,60,55,10,0:2")[0];

	private int yy_rmap[] = unpackFromString(1,161,
"0,1:2,2,3,1,4,1:3,5,6,7,8,1:13,9,10,11,10,1,10:9,9,10,9,10:3,1:4,12,1:2,13," +
"14,15,16,10,9,17,9:14,18,19,20,21,22,23,24,25,26,27,28,29,30,31,32,33,34,35" +
",36,37,38,39,40,41,42,43,44,45,46,47,48,49,50,51,52,53,54,55,56,57,58,59,60" +
",61,62,63,64,65,66,67,68,69,70,71,72,73,74,75,76,77,78,79,80,81,82,83,84,85" +
",86,87,88,89,90,91,92,10,9,93,94,95,96,97,98,99,100,101")[0];

	private int yy_nxt[][] = unpackFromString(102,62,
"1,2,3:2,4,5,6,7,8,9,5,10,5,11,12,150:2,152,110,13,56,78,112,150:2,154,150,8" +
"0,150,156,158,57,150,151:2,153,151,155,151,79,111,113,81,157,151:4,159,5,15" +
"0,14,15,16,17,18,19,20,21,22,23,24,-1:64,3:2,-1:62,54,-1:64,25,-1:66,26,-1:" +
"62,11,-1:61,150:2,114,150:2,116,150:14,114,150:6,116,150:10,-1:24,151:2,160" +
",151:5,27,151:11,160,151:5,27,151:11,-1:24,151:38,-1:24,150:38,-1:24,150:10" +
",136,150:14,136,150:12,-1:11,1,52,5,75:2,5,75:2,53,75:53,-1,31,54:60,-1:7,4" +
"9,-1:67,150:8,58,150:17,58,150:11,-1:24,151:10,115,117,151:13,115,151:5,117" +
",151:6,-1:24,151:10,141,151:14,141,151:12,-1:14,75:2,-1,75:2,-1,75:53,1,47," +
"5,48:2,5,55,77,48,5,48:52,-1:9,50,-1:65,150:3,124,150:2,28:2,150,29,150:18," +
"29,150:3,124,150:5,-1:24,151:3,127,151:2,59:2,151,60,151:18,60,151:3,127,15" +
"1:5,-1:24,150:6,30:2,150:30,-1:24,151:6,61:2,151:30,-1:24,150:12,32,150:5,3" +
"2,150:19,-1:24,151:12,62,151:5,62,151:19,-1:24,150:17,33,150:17,33,150:2,-1" +
":24,151:17,63,151:17,63,151:2,-1:24,150:12,34,150:5,34,150:19,-1:24,151:12," +
"64,151:5,64,151:19,-1:24,150:4,35,150:19,35,150:13,-1:24,151:9,69,151:18,69" +
",151:9,-1:24,150,36,150:20,36,150:15,-1:24,151:4,41,151:19,41,151:13,-1:24," +
"150:4,37,150:19,37,150:13,-1:24,151:4,65,151:19,65,151:13,-1:24,150:16,38,1" +
"50:13,38,150:7,-1:24,151,66,151:20,66,151:15,-1:24,150:9,39,150:18,39,150:9" +
",-1:24,151:4,67,151:19,67,151:13,-1:24,150:5,40,150:21,40,150:10,-1:24,151:" +
"16,68,151:13,68,151:7,-1:24,150:3,42,150:28,42,150:5,-1:24,151:5,70,151:21," +
"70,151:10,-1:24,150:4,44,150:19,44,150:13,-1:24,151:4,43,151:19,43,151:13,-" +
"1:24,150:15,45,150:7,45,150:14,-1:24,151:3,71,151:28,71,151:5,-1:24,150:3,4" +
"6,150:28,46,150:5,-1:24,151:4,72,151:19,72,151:13,-1:24,151:15,73,151:7,73," +
"151:14,-1:24,151:3,74,151:28,74,151:5,-1:24,150:4,82,150:9,122,150:9,82,150" +
":4,122,150:8,-1:24,151:4,83,151:9,129,151:9,83,151:4,129,151:8,-1:24,150:4," +
"84,150:9,86,150:9,84,150:4,86,150:8,-1:24,151:4,85,151:9,87,151:9,85,151:4," +
"87,151:8,-1:24,150:3,88,150:28,88,150:5,-1:24,151:4,89,151:19,89,151:13,-1:" +
"24,150:2,132,150:17,132,150:17,-1:24,151:19,91,151:13,91,151:4,-1:24,150:2," +
"90,150:17,90,150:17,-1:24,151:3,93,151:28,93,151:5,-1:24,150:3,92,150:28,92" +
",150:5,-1:24,151:2,137,151:17,137,151:17,-1:24,150:14,94,150:14,94,150:8,-1" +
":24,151:2,95,151:17,95,151:17,-1:24,150:13,134,150:20,134,150:3,-1:24,151:3" +
",97,151:28,97,151:5,-1:24,150:4,96,150:19,96,150:13,-1:24,151:13,139,151:20" +
",139,151:3,-1:24,150:14,98,150:14,98,150:8,-1:24,151:14,99,151:14,99,151:8," +
"-1:24,150:8,138,150:17,138,150:11,-1:24,151:14,101,151:14,101,151:8,-1:24,1" +
"50:3,100,150:28,100,150:5,-1:24,151:8,143,151:17,143,151:11,-1:24,150:14,14" +
"0,150:14,140,150:8,-1:24,151:3,103,151:28,103,151:5,-1:24,150:4,142,150:19," +
"142,150:13,-1:24,151:3,105,151:28,105,151:5,-1:24,150:5,102,150:21,102,150:" +
"10,-1:24,151:14,145,151:14,145,151:8,-1:24,150:8,104,150:17,104,150:11,-1:2" +
"4,151:4,147,151:19,147,151:13,-1:24,150:11,144,150:19,144,150:6,-1:24,151:5" +
",107,151:21,107,151:10,-1:24,150:8,146,150:17,146,150:11,-1:24,151:8,108,15" +
"1:17,108,151:11,-1:24,150:12,106,150:5,106,150:19,-1:24,151:11,148,151:19,1" +
"48,151:6,-1:24,151:8,149,151:17,149,151:11,-1:24,151:12,109,151:5,109,151:1" +
"9,-1:24,150:3,118,150,120,150:21,120,150:4,118,150:5,-1:24,151:2,119,151:2," +
"121,151:14,119,151:6,121,151:10,-1:24,150:10,126,150:14,126,150:12,-1:24,15" +
"1:3,123,151,125,151:21,125,151:4,123,151:5,-1:24,150:14,128,150:14,128,150:" +
"8,-1:24,151:14,131,151:14,131,151:8,-1:24,150:10,130,150:14,130,150:12,-1:2" +
"4,151:10,133,151:14,133,151:12,-1:24,151:5,135,151:21,135,151:10,-1:11");

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
						{
    yybegin(STRING);
}
					case -9:
						break;
					case 9:
						{ return new Symbol(TokenConstants.RPAREN); }
					case -10:
						break;
					case 10:
						{ return new Symbol(TokenConstants.EQ); }
					case -11:
						break;
					case 11:
						{ /* Integers */
                          return new Symbol(TokenConstants.INT_CONST,
					    AbstractTable.inttable.addString(yytext())); }
					case -12:
						break;
					case 12:
						{
    return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext()));
}
					case -13:
						break;
					case 13:
						{
    return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext()));
}
					case -14:
						break;
					case 14:
						{ return new Symbol(TokenConstants.PLUS); }
					case -15:
						break;
					case 15:
						{ return new Symbol(TokenConstants.DIV); }
					case -16:
						break;
					case 16:
						{ return new Symbol(TokenConstants.LT); }
					case -17:
						break;
					case 17:
						{ return new Symbol(TokenConstants.DOT); }
					case -18:
						break;
					case 18:
						{ return new Symbol(TokenConstants.NEG); }
					case -19:
						break;
					case 19:
						{ return new Symbol(TokenConstants.COMMA); }
					case -20:
						break;
					case 20:
						{ return new Symbol(TokenConstants.SEMI); }
					case -21:
						break;
					case 21:
						{ return new Symbol(TokenConstants.COLON); }
					case -22:
						break;
					case 22:
						{ return new Symbol(TokenConstants.AT); }
					case -23:
						break;
					case 23:
						{ return new Symbol(TokenConstants.RBRACE); }
					case -24:
						break;
					case 24:
						{ return new Symbol(TokenConstants.LBRACE); }
					case -25:
						break;
					case 25:
						{
    commentdepth += 1; 
    yybegin(COMMENT);
}
					case -26:
						break;
					case 26:
						{ return new Symbol(TokenConstants.DARROW); }
					case -27:
						break;
					case 27:
						{ return new Symbol(TokenConstants.FI); }
					case -28:
						break;
					case 28:
						{ return new Symbol(TokenConstants.IF); }
					case -29:
						break;
					case 29:
						{ return new Symbol(TokenConstants.IN); }
					case -30:
						break;
					case 30:
						{ return new Symbol(TokenConstants.OF); }
					case -31:
						break;
					case 31:
						{  
    curr_lineno += 1; 
}
					case -32:
						break;
					case 32:
						{ return new Symbol(TokenConstants.LET); }
					case -33:
						break;
					case 33:
						{ return new Symbol(TokenConstants.NEW); }
					case -34:
						break;
					case 34:
						{ return new Symbol(TokenConstants.NOT); }
					case -35:
						break;
					case 35:
						{ return new Symbol(TokenConstants.CASE); }
					case -36:
						break;
					case 36:
						{ return new Symbol(TokenConstants.ESAC); }
					case -37:
						break;
					case 37:
						{ return new Symbol(TokenConstants.ELSE); }
					case -38:
						break;
					case 38:
						{ return new Symbol(TokenConstants.LOOP); }
					case -39:
						break;
					case 39:
						{ return new Symbol(TokenConstants.THEN); }
					case -40:
						break;
					case 40:
						{ return new Symbol(TokenConstants.POOL); }
					case -41:
						break;
					case 41:
						{ return new Symbol(TokenConstants.BOOL_CONST, Boolean.TRUE); }
					case -42:
						break;
					case 42:
						{ return new Symbol(TokenConstants.CLASS); }
					case -43:
						break;
					case 43:
						{ return new Symbol(TokenConstants.BOOL_CONST, Boolean.FALSE); }
					case -44:
						break;
					case 44:
						{ return new Symbol(TokenConstants.WHILE); }
					case -45:
						break;
					case 45:
						{ return new Symbol(TokenConstants.ISVOID); }
					case -46:
						break;
					case 46:
						{ return new Symbol(TokenConstants.INHERITS); }
					case -47:
						break;
					case 47:
						{ curr_lineno += 1; }
					case -48:
						break;
					case 48:
						{ }
					case -49:
						break;
					case 49:
						{ commentdepth += 1; }
					case -50:
						break;
					case 50:
						{ 
    commentdepth -= 1;
    if (commentdepth == 0) {
        yybegin(YYINITIAL);
    }    
}
					case -51:
						break;
					case 51:
						{ }
					case -52:
						break;
					case 52:
						{ curr_lineno += 1; }
					case -53:
						break;
					case 53:
						{ yybegin(YYINITIAL); }
					case -54:
						break;
					case 55:
						{ /*
                    *  This should be the very last rule and will match
                    *  everything not matched by other lexical rules.
                    */
                   System.err.println("LEXER BUG - UNMATCHED: " + yytext()); }
					case -55:
						break;
					case 56:
						{
    return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext()));
}
					case -56:
						break;
					case 57:
						{
    return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext()));
}
					case -57:
						break;
					case 58:
						{ return new Symbol(TokenConstants.FI); }
					case -58:
						break;
					case 59:
						{ return new Symbol(TokenConstants.IF); }
					case -59:
						break;
					case 60:
						{ return new Symbol(TokenConstants.IN); }
					case -60:
						break;
					case 61:
						{ return new Symbol(TokenConstants.OF); }
					case -61:
						break;
					case 62:
						{ return new Symbol(TokenConstants.LET); }
					case -62:
						break;
					case 63:
						{ return new Symbol(TokenConstants.NEW); }
					case -63:
						break;
					case 64:
						{ return new Symbol(TokenConstants.NOT); }
					case -64:
						break;
					case 65:
						{ return new Symbol(TokenConstants.CASE); }
					case -65:
						break;
					case 66:
						{ return new Symbol(TokenConstants.ESAC); }
					case -66:
						break;
					case 67:
						{ return new Symbol(TokenConstants.ELSE); }
					case -67:
						break;
					case 68:
						{ return new Symbol(TokenConstants.LOOP); }
					case -68:
						break;
					case 69:
						{ return new Symbol(TokenConstants.THEN); }
					case -69:
						break;
					case 70:
						{ return new Symbol(TokenConstants.POOL); }
					case -70:
						break;
					case 71:
						{ return new Symbol(TokenConstants.CLASS); }
					case -71:
						break;
					case 72:
						{ return new Symbol(TokenConstants.WHILE); }
					case -72:
						break;
					case 73:
						{ return new Symbol(TokenConstants.ISVOID); }
					case -73:
						break;
					case 74:
						{ return new Symbol(TokenConstants.INHERITS); }
					case -74:
						break;
					case 75:
						{ }
					case -75:
						break;
					case 77:
						{ /*
                    *  This should be the very last rule and will match
                    *  everything not matched by other lexical rules.
                    */
                   System.err.println("LEXER BUG - UNMATCHED: " + yytext()); }
					case -76:
						break;
					case 78:
						{
    return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext()));
}
					case -77:
						break;
					case 79:
						{
    return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext()));
}
					case -78:
						break;
					case 80:
						{
    return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext()));
}
					case -79:
						break;
					case 81:
						{
    return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext()));
}
					case -80:
						break;
					case 82:
						{
    return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext()));
}
					case -81:
						break;
					case 83:
						{
    return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext()));
}
					case -82:
						break;
					case 84:
						{
    return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext()));
}
					case -83:
						break;
					case 85:
						{
    return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext()));
}
					case -84:
						break;
					case 86:
						{
    return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext()));
}
					case -85:
						break;
					case 87:
						{
    return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext()));
}
					case -86:
						break;
					case 88:
						{
    return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext()));
}
					case -87:
						break;
					case 89:
						{
    return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext()));
}
					case -88:
						break;
					case 90:
						{
    return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext()));
}
					case -89:
						break;
					case 91:
						{
    return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext()));
}
					case -90:
						break;
					case 92:
						{
    return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext()));
}
					case -91:
						break;
					case 93:
						{
    return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext()));
}
					case -92:
						break;
					case 94:
						{
    return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext()));
}
					case -93:
						break;
					case 95:
						{
    return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext()));
}
					case -94:
						break;
					case 96:
						{
    return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext()));
}
					case -95:
						break;
					case 97:
						{
    return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext()));
}
					case -96:
						break;
					case 98:
						{
    return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext()));
}
					case -97:
						break;
					case 99:
						{
    return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext()));
}
					case -98:
						break;
					case 100:
						{
    return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext()));
}
					case -99:
						break;
					case 101:
						{
    return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext()));
}
					case -100:
						break;
					case 102:
						{
    return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext()));
}
					case -101:
						break;
					case 103:
						{
    return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext()));
}
					case -102:
						break;
					case 104:
						{
    return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext()));
}
					case -103:
						break;
					case 105:
						{
    return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext()));
}
					case -104:
						break;
					case 106:
						{
    return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext()));
}
					case -105:
						break;
					case 107:
						{
    return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext()));
}
					case -106:
						break;
					case 108:
						{
    return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext()));
}
					case -107:
						break;
					case 109:
						{
    return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext()));
}
					case -108:
						break;
					case 110:
						{
    return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext()));
}
					case -109:
						break;
					case 111:
						{
    return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext()));
}
					case -110:
						break;
					case 112:
						{
    return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext()));
}
					case -111:
						break;
					case 113:
						{
    return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext()));
}
					case -112:
						break;
					case 114:
						{
    return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext()));
}
					case -113:
						break;
					case 115:
						{
    return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext()));
}
					case -114:
						break;
					case 116:
						{
    return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext()));
}
					case -115:
						break;
					case 117:
						{
    return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext()));
}
					case -116:
						break;
					case 118:
						{
    return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext()));
}
					case -117:
						break;
					case 119:
						{
    return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext()));
}
					case -118:
						break;
					case 120:
						{
    return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext()));
}
					case -119:
						break;
					case 121:
						{
    return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext()));
}
					case -120:
						break;
					case 122:
						{
    return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext()));
}
					case -121:
						break;
					case 123:
						{
    return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext()));
}
					case -122:
						break;
					case 124:
						{
    return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext()));
}
					case -123:
						break;
					case 125:
						{
    return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext()));
}
					case -124:
						break;
					case 126:
						{
    return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext()));
}
					case -125:
						break;
					case 127:
						{
    return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext()));
}
					case -126:
						break;
					case 128:
						{
    return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext()));
}
					case -127:
						break;
					case 129:
						{
    return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext()));
}
					case -128:
						break;
					case 130:
						{
    return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext()));
}
					case -129:
						break;
					case 131:
						{
    return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext()));
}
					case -130:
						break;
					case 132:
						{
    return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext()));
}
					case -131:
						break;
					case 133:
						{
    return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext()));
}
					case -132:
						break;
					case 134:
						{
    return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext()));
}
					case -133:
						break;
					case 135:
						{
    return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext()));
}
					case -134:
						break;
					case 136:
						{
    return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext()));
}
					case -135:
						break;
					case 137:
						{
    return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext()));
}
					case -136:
						break;
					case 138:
						{
    return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext()));
}
					case -137:
						break;
					case 139:
						{
    return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext()));
}
					case -138:
						break;
					case 140:
						{
    return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext()));
}
					case -139:
						break;
					case 141:
						{
    return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext()));
}
					case -140:
						break;
					case 142:
						{
    return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext()));
}
					case -141:
						break;
					case 143:
						{
    return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext()));
}
					case -142:
						break;
					case 144:
						{
    return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext()));
}
					case -143:
						break;
					case 145:
						{
    return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext()));
}
					case -144:
						break;
					case 146:
						{
    return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext()));
}
					case -145:
						break;
					case 147:
						{
    return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext()));
}
					case -146:
						break;
					case 148:
						{
    return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext()));
}
					case -147:
						break;
					case 149:
						{
    return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext()));
}
					case -148:
						break;
					case 150:
						{
    return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext()));
}
					case -149:
						break;
					case 151:
						{
    return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext()));
}
					case -150:
						break;
					case 152:
						{
    return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext()));
}
					case -151:
						break;
					case 153:
						{
    return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext()));
}
					case -152:
						break;
					case 154:
						{
    return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext()));
}
					case -153:
						break;
					case 155:
						{
    return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext()));
}
					case -154:
						break;
					case 156:
						{
    return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext()));
}
					case -155:
						break;
					case 157:
						{
    return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext()));
}
					case -156:
						break;
					case 158:
						{
    return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext()));
}
					case -157:
						break;
					case 159:
						{
    return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext()));
}
					case -158:
						break;
					case 160:
						{
    return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext()));
}
					case -159:
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
