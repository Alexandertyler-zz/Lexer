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
		79,
		55
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
		/* 54 */ YY_NO_ANCHOR,
		/* 55 */ YY_NO_ANCHOR,
		/* 56 */ YY_NO_ANCHOR,
		/* 57 */ YY_NOT_ACCEPT,
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
		/* 76 */ YY_NO_ANCHOR,
		/* 77 */ YY_NO_ANCHOR,
		/* 78 */ YY_NO_ANCHOR,
		/* 79 */ YY_NOT_ACCEPT,
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
		/* 160 */ YY_NO_ANCHOR,
		/* 161 */ YY_NO_ANCHOR,
		/* 162 */ YY_NO_ANCHOR,
		/* 163 */ YY_NO_ANCHOR
	};
	private int yy_cmap[] = unpackFromString(1,130,
"12:8,11:2,1,3,11:2,12:18,11,12,8,12:5,6,9,7,55,60,4,58,56,15,16:9,62,61,57," +
"13,14,12,63,18,54,17,32,20,23,54,27,24,54:2,21,54,25,31,33,54,28,19,29,36,3" +
"0,34,54:3,12,10,12,5,53,12,37,38,39,40,41,22,38,42,43,38:2,44,38,26,45,46,3" +
"8,47,48,35,49,50,51,52,38:2,65,2,64,59,12,0:2")[0];

	private int yy_rmap[] = unpackFromString(1,164,
"0,1:2,2,1,3,1,4,1:3,5,6,7,8,1:2,9,1:10,10,11,12,11,1:3,11:9,10,11,10,11:3,1" +
":5,13,1,14,15,16,11,10,17,10:14,18,19,20,21,22,23,24,25,26,27,28,29,30,31,3" +
"2,33,34,35,36,37,38,39,40,41,42,43,44,45,46,47,48,49,50,51,52,53,54,55,56,5" +
"7,58,59,60,61,62,63,64,65,66,67,68,69,70,71,72,73,10,74,75,76,77,78,79,80,8" +
"1,82,83,84,85,86,87,88,89,90,91,92,93,94,95,96,97,98,11,99,100,101,102")[0];

	private int yy_nxt[][] = unpackFromString(103,66,
"1,2,3,4,5,6,7,8,9,10,6,3,6,11,6,12:2,13,159:2,160,127,14,58,80,132,59,159:2" +
",161,159,83,159,162,163,128,159,133:2,137,133,139,133,81,141,84,143,133:4,1" +
"45,133,6,159,15,16,17,18,19,20,21,22,23,24,25,-1:68,3,-1:8,3,-1:58,57,-1:68" +
",26,-1:72,27,-1:66,12:2,-1:64,159:3,136,159:2,138,159:15,136,159:6,138,159:" +
"10,-1:26,133:3,147,133:5,28,133:12,147,133:5,28,133:11,-1:15,32,-1:8,33,-1:" +
"67,133:40,-1:26,159:40,-1:26,159:12,153,159:14,153,159:12,-1:11,1,78,51,78:" +
"2,51,78:2,56,78,51,78:4,51,78:10,51,78:25,51,78:13,-1,34,57:64,-1:15,159:9," +
"60,159:18,60,159:11,-1:26,133:5,86,133:10,88,133:9,86,133:3,88,133:9,-1:26," +
"133:12,114,133:14,114,133:12,-1:18,53,-1:59,78,-1,78:2,-1,78:2,-1,78,-1,78:" +
"4,-1,78:10,-1,78:25,-1,78:13,1,50,51,52:2,51,77,82,52,51,52:56,-1:15,159:4," +
"146,159:2,29:2,159,30:2,159:21,146,159:6,-1:26,133:4,98,133:2,61:2,133,62:2" +
",133:21,98,133:6,-1:20,54,-1:71,159:7,31:2,159:31,-1:26,133:7,63:2,133:31,-" +
"1:26,159:14,35,159:5,35,159:19,-1:26,133:19,65,133:16,65,133:3,-1:26,159:19" +
",36,159:16,36,159:3,-1:26,133:14,66,133:5,66,133:19,-1:26,159:14,37,159:5,3" +
"7,159:19,-1:26,133:5,106,133:20,106,133:13,-1:26,159:5,38,159:20,38,159:13," +
"-1:26,133:21,108,133:12,108,133:5,-1:26,159:2,39,159:21,39,159:15,-1:26,133" +
":4,110,133:28,110,133:6,-1:26,159:5,40,159:20,40,159:13,-1:26,133:3,111,133" +
":18,111,133:17,-1:26,159:18,41,159:12,41,159:8,-1:26,133:15,135,133:19,135," +
"133:4,-1:26,159:10,42:2,159:28,-1:26,133:14,64,133:5,64,133:19,-1:26,159:6," +
"43,159:22,43,159:10,-1:26,133:16,115,133:13,115,133:9,-1:26,159:4,45,159:28" +
",45,159:6,-1:26,133:9,117,133:18,117,133:11,-1:26,159:5,47,159:20,47,159:13" +
",-1:26,133:10,71:2,133:28,-1:26,159:17,48,159:7,48,159:14,-1:26,133:5,44,13" +
"3:20,44,133:13,-1:26,159:4,49,159:28,49,159:6,-1:26,133:5,67,133:20,67,133:" +
"13,-1:26,133:4,119,133:28,119,133:6,-1:26,133:2,68,133:21,68,133:15,-1:26,1" +
"33:5,69,133:20,69,133:13,-1:26,133:5,121,133:20,121,133:13,-1:26,133:18,70," +
"133:12,70,133:8,-1:26,133:6,72,133:22,72,133:10,-1:26,133:6,122,133:22,122," +
"133:10,-1:26,133:5,46,133:20,46,133:13,-1:26,133:4,73,133:28,73,133:6,-1:26" +
",133:9,123,133:18,123,133:11,-1:26,133:13,124,133:18,124,133:7,-1:26,133:5," +
"74,133:20,74,133:13,-1:26,133:17,75,133:7,75,133:14,-1:26,133:9,125,133:18," +
"125,133:11,-1:26,133:14,126,133:5,126,133:19,-1:26,133:4,76,133:28,76,133:6" +
",-1:26,159:5,85,159:10,144,159:9,85,159:3,144,159:9,-1:26,133:12,90,92,133:" +
"13,90,133:4,92,133:7,-1:26,133:4,113,133:28,113,133:6,-1:26,133:3,112,133:1" +
"8,112,133:17,-1:26,133:16,116,133:13,116,133:9,-1:26,159:5,87,159:10,89,159" +
":9,87,159:3,89,159:9,-1:26,133:4,118,133:28,118,133:6,-1:26,133:16,120,133:" +
"13,120,133:9,-1:26,159:4,91,159:28,91,159:6,-1:26,133:3,94,133:2,96,133:15," +
"94,133:6,96,133:10,-1:26,159:3,151,159:18,151,159:17,-1:26,133:4,130,133,12" +
"9,133:22,129,133:3,130,133:6,-1:26,159:3,93,159:18,93,159:17,-1:26,133:5,10" +
"0,133:10,102,133:9,100,133:3,102,133:9,-1:26,159:4,95,159:28,95,159:6,-1:26" +
",133:16,131,133:13,131,133:9,-1:26,159:16,97,159:13,97,159:9,-1:26,133:12,1" +
"04,133:14,104,133:12,-1:26,159:15,152,159:19,152,159:4,-1:26,133:6,134,133:" +
"22,134,133:10,-1:26,159:5,99,159:20,99,159:13,-1:26,159:16,101,159:13,101,1" +
"59:9,-1:26,159:9,154,159:18,154,159:11,-1:26,159:4,103,159:28,103,159:6,-1:" +
"26,159:16,155,159:13,155,159:9,-1:26,159:5,156,159:20,156,159:13,-1:26,159:" +
"6,105,159:22,105,159:10,-1:26,159:9,107,159:18,107,159:11,-1:26,159:13,157," +
"159:18,157,159:7,-1:26,159:9,158,159:18,158,159:11,-1:26,159:14,109,159:5,1" +
"09,159:19,-1:26,159:4,140,159,142,159:22,142,159:3,140,159:6,-1:26,159:12,1" +
"48,159:14,148,159:12,-1:26,159:16,149,159:13,149,159:9,-1:26,159:12,150,159" +
":14,150,159:12,-1:11");

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
						{ curr_lineno += 1; }
					case -5:
						break;
					case 5:
						{ return new Symbol(TokenConstants.MINUS); }
					case -6:
						break;
					case 6:
						{ return new Symbol(TokenConstants.ERROR, yytext()); }
					case -7:
						break;
					case 7:
						{ return new Symbol(TokenConstants.LPAREN); }
					case -8:
						break;
					case 8:
						{ return new Symbol(TokenConstants.MULT); }
					case -9:
						break;
					case 9:
						{
    string_buf = new StringBuffer();
    yybegin(STRING);
}
					case -10:
						break;
					case 10:
						{ return new Symbol(TokenConstants.RPAREN); }
					case -11:
						break;
					case 11:
						{ return new Symbol(TokenConstants.EQ); }
					case -12:
						break;
					case 12:
						{ /* Integers */
                          return new Symbol(TokenConstants.INT_CONST,
					    AbstractTable.inttable.addString(yytext())); }
					case -13:
						break;
					case 13:
						{
    return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext()));
}
					case -14:
						break;
					case 14:
						{
    return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext()));
}
					case -15:
						break;
					case 15:
						{ return new Symbol(TokenConstants.PLUS); }
					case -16:
						break;
					case 16:
						{ return new Symbol(TokenConstants.DIV); }
					case -17:
						break;
					case 17:
						{ return new Symbol(TokenConstants.LT); }
					case -18:
						break;
					case 18:
						{ return new Symbol(TokenConstants.DOT); }
					case -19:
						break;
					case 19:
						{ return new Symbol(TokenConstants.NEG); }
					case -20:
						break;
					case 20:
						{ return new Symbol(TokenConstants.COMMA); }
					case -21:
						break;
					case 21:
						{ return new Symbol(TokenConstants.SEMI); }
					case -22:
						break;
					case 22:
						{ return new Symbol(TokenConstants.COLON); }
					case -23:
						break;
					case 23:
						{ return new Symbol(TokenConstants.AT); }
					case -24:
						break;
					case 24:
						{ return new Symbol(TokenConstants.RBRACE); }
					case -25:
						break;
					case 25:
						{ return new Symbol(TokenConstants.LBRACE); }
					case -26:
						break;
					case 26:
						{
    commentdepth += 1; 
    yybegin(COMMENT);
}
					case -27:
						break;
					case 27:
						{ return new Symbol(TokenConstants.DARROW); }
					case -28:
						break;
					case 28:
						{ return new Symbol(TokenConstants.FI); }
					case -29:
						break;
					case 29:
						{ return new Symbol(TokenConstants.IF); }
					case -30:
						break;
					case 30:
						{ return new Symbol(TokenConstants.IN); }
					case -31:
						break;
					case 31:
						{ return new Symbol(TokenConstants.OF); }
					case -32:
						break;
					case 32:
						{ return new Symbol(TokenConstants.ASSIGN); }
					case -33:
						break;
					case 33:
						{ return new Symbol(TokenConstants.LE); }
					case -34:
						break;
					case 34:
						{  
    curr_lineno += 1; 
}
					case -35:
						break;
					case 35:
						{ return new Symbol(TokenConstants.LET); }
					case -36:
						break;
					case 36:
						{ return new Symbol(TokenConstants.NEW); }
					case -37:
						break;
					case 37:
						{ return new Symbol(TokenConstants.NOT); }
					case -38:
						break;
					case 38:
						{ return new Symbol(TokenConstants.CASE); }
					case -39:
						break;
					case 39:
						{ return new Symbol(TokenConstants.ESAC); }
					case -40:
						break;
					case 40:
						{ return new Symbol(TokenConstants.ELSE); }
					case -41:
						break;
					case 41:
						{ return new Symbol(TokenConstants.LOOP); }
					case -42:
						break;
					case 42:
						{ return new Symbol(TokenConstants.THEN); }
					case -43:
						break;
					case 43:
						{ return new Symbol(TokenConstants.POOL); }
					case -44:
						break;
					case 44:
						{ return new Symbol(TokenConstants.BOOL_CONST, Boolean.TRUE); }
					case -45:
						break;
					case 45:
						{ return new Symbol(TokenConstants.CLASS); }
					case -46:
						break;
					case 46:
						{ return new Symbol(TokenConstants.BOOL_CONST, Boolean.FALSE); }
					case -47:
						break;
					case 47:
						{ return new Symbol(TokenConstants.WHILE); }
					case -48:
						break;
					case 48:
						{ return new Symbol(TokenConstants.ISVOID); }
					case -49:
						break;
					case 49:
						{ return new Symbol(TokenConstants.INHERITS); }
					case -50:
						break;
					case 50:
						{ curr_lineno += 1; }
					case -51:
						break;
					case 51:
						{ /*
                    *  This should be the very last rule and will match
                    *  everything not matched by other lexical rules.
                    */
                   System.err.println("LEXER BUG - UNMATCHED: " + yytext()); }
					case -52:
						break;
					case 52:
						{ }
					case -53:
						break;
					case 53:
						{ commentdepth += 1; }
					case -54:
						break;
					case 54:
						{ 
    commentdepth -= 1;
    if (commentdepth == 0) {
        yybegin(YYINITIAL);
    }    
}
					case -55:
						break;
					case 55:
						{ string_buf.append(yytext()); }
					case -56:
						break;
					case 56:
						{ yybegin(YYINITIAL); return new Symbol(TokenConstants.STR_CONST,
                        AbstractTable.stringtable.addString(string_buf.toString())); }
					case -57:
						break;
					case 58:
						{
    return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext()));
}
					case -58:
						break;
					case 59:
						{
    return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext()));
}
					case -59:
						break;
					case 60:
						{ return new Symbol(TokenConstants.FI); }
					case -60:
						break;
					case 61:
						{ return new Symbol(TokenConstants.IF); }
					case -61:
						break;
					case 62:
						{ return new Symbol(TokenConstants.IN); }
					case -62:
						break;
					case 63:
						{ return new Symbol(TokenConstants.OF); }
					case -63:
						break;
					case 64:
						{ return new Symbol(TokenConstants.LET); }
					case -64:
						break;
					case 65:
						{ return new Symbol(TokenConstants.NEW); }
					case -65:
						break;
					case 66:
						{ return new Symbol(TokenConstants.NOT); }
					case -66:
						break;
					case 67:
						{ return new Symbol(TokenConstants.CASE); }
					case -67:
						break;
					case 68:
						{ return new Symbol(TokenConstants.ESAC); }
					case -68:
						break;
					case 69:
						{ return new Symbol(TokenConstants.ELSE); }
					case -69:
						break;
					case 70:
						{ return new Symbol(TokenConstants.LOOP); }
					case -70:
						break;
					case 71:
						{ return new Symbol(TokenConstants.THEN); }
					case -71:
						break;
					case 72:
						{ return new Symbol(TokenConstants.POOL); }
					case -72:
						break;
					case 73:
						{ return new Symbol(TokenConstants.CLASS); }
					case -73:
						break;
					case 74:
						{ return new Symbol(TokenConstants.WHILE); }
					case -74:
						break;
					case 75:
						{ return new Symbol(TokenConstants.ISVOID); }
					case -75:
						break;
					case 76:
						{ return new Symbol(TokenConstants.INHERITS); }
					case -76:
						break;
					case 77:
						{ /*
                    *  This should be the very last rule and will match
                    *  everything not matched by other lexical rules.
                    */
                   System.err.println("LEXER BUG - UNMATCHED: " + yytext()); }
					case -77:
						break;
					case 78:
						{ string_buf.append(yytext()); }
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
						{ /*
                    *  This should be the very last rule and will match
                    *  everything not matched by other lexical rules.
                    */
                   System.err.println("LEXER BUG - UNMATCHED: " + yytext()); }
					case -81:
						break;
					case 83:
						{
    return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext()));
}
					case -82:
						break;
					case 84:
						{
    return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext()));
}
					case -83:
						break;
					case 85:
						{
    return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext()));
}
					case -84:
						break;
					case 86:
						{
    return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext()));
}
					case -85:
						break;
					case 87:
						{
    return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext()));
}
					case -86:
						break;
					case 88:
						{
    return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext()));
}
					case -87:
						break;
					case 89:
						{
    return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext()));
}
					case -88:
						break;
					case 90:
						{
    return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext()));
}
					case -89:
						break;
					case 91:
						{
    return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext()));
}
					case -90:
						break;
					case 92:
						{
    return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext()));
}
					case -91:
						break;
					case 93:
						{
    return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext()));
}
					case -92:
						break;
					case 94:
						{
    return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext()));
}
					case -93:
						break;
					case 95:
						{
    return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext()));
}
					case -94:
						break;
					case 96:
						{
    return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext()));
}
					case -95:
						break;
					case 97:
						{
    return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext()));
}
					case -96:
						break;
					case 98:
						{
    return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext()));
}
					case -97:
						break;
					case 99:
						{
    return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext()));
}
					case -98:
						break;
					case 100:
						{
    return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext()));
}
					case -99:
						break;
					case 101:
						{
    return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext()));
}
					case -100:
						break;
					case 102:
						{
    return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext()));
}
					case -101:
						break;
					case 103:
						{
    return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext()));
}
					case -102:
						break;
					case 104:
						{
    return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext()));
}
					case -103:
						break;
					case 105:
						{
    return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext()));
}
					case -104:
						break;
					case 106:
						{
    return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext()));
}
					case -105:
						break;
					case 107:
						{
    return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext()));
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
    return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext()));
}
					case -108:
						break;
					case 110:
						{
    return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext()));
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
    return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext()));
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
    return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext()));
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
    return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext()));
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
    return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext()));
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
    return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext()));
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
    return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext()));
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
    return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext()));
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
    return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext()));
}
					case -125:
						break;
					case 127:
						{
    return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext()));
}
					case -126:
						break;
					case 128:
						{
    return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext()));
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
    return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext()));
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
    return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext()));
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
    return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext()));
}
					case -147:
						break;
					case 149:
						{
    return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext()));
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
    return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext()));
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
    return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext()));
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
    return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext()));
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
    return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext()));
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
    return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext()));
}
					case -158:
						break;
					case 160:
						{
    return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext()));
}
					case -159:
						break;
					case 161:
						{
    return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext()));
}
					case -160:
						break;
					case 162:
						{
    return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext()));
}
					case -161:
						break;
					case 163:
						{
    return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext()));
}
					case -162:
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
