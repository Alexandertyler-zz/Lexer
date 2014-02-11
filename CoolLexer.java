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
		86,
		56
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
		/* 57 */ YY_NO_ANCHOR,
		/* 58 */ YY_NO_ANCHOR,
		/* 59 */ YY_NO_ANCHOR,
		/* 60 */ YY_NO_ANCHOR,
		/* 61 */ YY_NO_ANCHOR,
		/* 62 */ YY_NO_ANCHOR,
		/* 63 */ YY_NOT_ACCEPT,
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
		/* 79 */ YY_NO_ANCHOR,
		/* 80 */ YY_NO_ANCHOR,
		/* 81 */ YY_NO_ANCHOR,
		/* 82 */ YY_NO_ANCHOR,
		/* 83 */ YY_NO_ANCHOR,
		/* 84 */ YY_NO_ANCHOR,
		/* 85 */ YY_NO_ANCHOR,
		/* 86 */ YY_NOT_ACCEPT,
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
		/* 163 */ YY_NO_ANCHOR,
		/* 164 */ YY_NO_ANCHOR,
		/* 165 */ YY_NO_ANCHOR,
		/* 166 */ YY_NO_ANCHOR,
		/* 167 */ YY_NO_ANCHOR,
		/* 168 */ YY_NO_ANCHOR,
		/* 169 */ YY_NO_ANCHOR,
		/* 170 */ YY_NO_ANCHOR
	};
	private int yy_cmap[] = unpackFromString(1,130,
"12,10:7,15:2,1,3,15,11,10:18,15,10,9,10:5,6,8,7,56,61,4,59,57,14,18:9,63,62" +
",58,16,17,10,64,20,55,19,33,22,25,55,28,26,55:2,23,55,27,32,34,55,29,21,30," +
"37,31,35,55:3,10,13,10,5,54,10,38,39,40,41,42,24,39,43,44,39:2,45,39,46,47," +
"48,39,49,50,36,51,52,53,39:3,66,2,65,60,10,0:2")[0];

	private int yy_rmap[] = unpackFromString(1,171,
"0,1:2,2,1,3,1,4,5,1:2,6,7,8,9,1:2,10,1:11,11,12,13,12,1:3,12:9,11,12,11,12:" +
"3,1:5,14,1:6,15,16,17,12,11,18,11:14,19,20,21,22,23,24,25,26,27,28,29,30,31" +
",32,33,34,35,36,37,38,39,40,41,42,43,44,45,46,47,48,49,50,51,52,53,54,55,56" +
",57,58,59,60,61,62,63,64,65,66,67,68,69,70,71,72,73,74,75,76,77,78,79,80,81" +
",82,83,84,85,86,87,88,89,90,91,92,93,94,95,12,11,96,97,98,99,100,101,102,10" +
"3,104")[0];

	private int yy_nxt[][] = unpackFromString(105,67,
"1,2,3,4,5,6,7,8,9,10,6,3,6:2,11,3,12,6,11,13,160:2,162,120,14,64,87,122,160" +
":2,164,160,90,160,166,168,65,160,161:2,163,161,165,161,88,121,123,91,167,16" +
"1:4,169,6,160,15,16,17,18,19,20,21,22,23,24,25,-1:69,3,-1:8,3,-1:3,3,-1:55," +
"63,-1:69,26,-1:67,27,-1:72,11,-1:3,11,-1:65,28,-1:63,160,-1:3,160:2,124,160" +
":2,126,160:14,124,160:6,126,160:10,-1:25,161,-1:3,161:2,170,161:5,29,161:11" +
",170,161:5,29,161:11,-1:15,33,-1:11,34,-1:64,161,-1:3,161:38,-1:25,160,-1:3" +
",160:38,-1:25,160,-1:3,160:10,146,160:14,146,160:12,-1:11,1,57,52,85:2,52,8" +
"5:3,58,85:2,59,83,52,85:52,-1,35,63:65,-1:14,160,-1:3,160:8,66,160:17,66,16" +
"0:11,-1:25,161,-1:3,161:10,125,127,161:13,125,161:5,127,161:6,-1:25,161,-1:" +
"3,161:10,151,161:14,151,161:12,-1:12,60,61:9,-1,61:2,62,61:52,-1:7,54,-1:62" +
",85:2,-1,85:3,-1,85:2,-1:3,85:52,1,51,52,53:2,52,84,89,53,52,53:2,52:3,53:5" +
"2,-1:14,160,-1:3,160:3,134,160:2,30:2,160,31,160:18,31,160:3,134,160:5,-1:2" +
"5,161,-1:3,161:3,137,161:2,67:2,161,68,161:18,68,161:3,137,161:5,-1:19,55,-" +
"1:72,160,-1:3,160:6,32:2,160:30,-1:25,161,-1:3,161:6,69:2,161:30,-1:25,160," +
"-1:3,160:12,36,160:5,36,160:19,-1:25,161,-1:3,161:12,70,161:5,70,161:19,-1:" +
"25,160,-1:3,160:17,37,160:17,37,160:2,-1:25,161,-1:3,161:17,71,161:17,71,16" +
"1:2,-1:25,160,-1:3,160:12,38,160:5,38,160:19,-1:25,161,-1:3,161:12,72,161:5" +
",72,161:19,-1:25,160,-1:3,160:4,39,160:19,39,160:13,-1:25,161,-1:3,161:9,77" +
",161:18,77,161:9,-1:25,160,-1:3,160,40,160:20,40,160:15,-1:25,161,-1:3,161:" +
"4,45,161:19,45,161:13,-1:25,160,-1:3,160:4,41,160:19,41,160:13,-1:25,161,-1" +
":3,161:4,73,161:19,73,161:13,-1:25,160,-1:3,160:16,42,160:13,42,160:7,-1:25" +
",161,-1:3,161,74,161:20,74,161:15,-1:25,160,-1:3,160:9,43,160:18,43,160:9,-" +
"1:25,161,-1:3,161:4,75,161:19,75,161:13,-1:25,160,-1:3,160:5,44,160:21,44,1" +
"60:10,-1:25,161,-1:3,161:16,76,161:13,76,161:7,-1:25,160,-1:3,160:3,46,160:" +
"28,46,160:5,-1:25,161,-1:3,161:5,78,161:21,78,161:10,-1:25,160,-1:3,160:4,4" +
"8,160:19,48,160:13,-1:25,161,-1:3,161:4,47,161:19,47,161:13,-1:25,160,-1:3," +
"160:15,49,160:7,49,160:14,-1:25,161,-1:3,161:3,79,161:28,79,161:5,-1:25,160" +
",-1:3,160:3,50,160:28,50,160:5,-1:25,161,-1:3,161:4,80,161:19,80,161:13,-1:" +
"25,161,-1:3,161:15,81,161:7,81,161:14,-1:25,161,-1:3,161:3,82,161:28,82,161" +
":5,-1:25,160,-1:3,160:4,92,160:9,132,160:9,92,160:4,132,160:8,-1:25,161,-1:" +
"3,161:4,93,161:9,139,161:9,93,161:4,139,161:8,-1:25,160,-1:3,160:4,94,160:9" +
",96,160:9,94,160:4,96,160:8,-1:25,161,-1:3,161:4,95,161:9,97,161:9,95,161:4" +
",97,161:8,-1:25,160,-1:3,160:3,98,160:28,98,160:5,-1:25,161,-1:3,161:4,99,1" +
"61:19,99,161:13,-1:25,160,-1:3,160:2,142,160:17,142,160:17,-1:25,161,-1:3,1" +
"61:19,101,161:13,101,161:4,-1:25,160,-1:3,160:2,100,160:17,100,160:17,-1:25" +
",161,-1:3,161:3,103,161:28,103,161:5,-1:25,160,-1:3,160:3,102,160:28,102,16" +
"0:5,-1:25,161,-1:3,161:2,147,161:17,147,161:17,-1:25,160,-1:3,160:14,104,16" +
"0:14,104,160:8,-1:25,161,-1:3,161:2,105,161:17,105,161:17,-1:25,160,-1:3,16" +
"0:13,144,160:20,144,160:3,-1:25,161,-1:3,161:3,107,161:28,107,161:5,-1:25,1" +
"60,-1:3,160:4,106,160:19,106,160:13,-1:25,161,-1:3,161:13,149,161:20,149,16" +
"1:3,-1:25,160,-1:3,160:14,108,160:14,108,160:8,-1:25,161,-1:3,161:14,109,16" +
"1:14,109,161:8,-1:25,160,-1:3,160:8,148,160:17,148,160:11,-1:25,161,-1:3,16" +
"1:14,111,161:14,111,161:8,-1:25,160,-1:3,160:3,110,160:28,110,160:5,-1:25,1" +
"61,-1:3,161:8,153,161:17,153,161:11,-1:25,160,-1:3,160:14,150,160:14,150,16" +
"0:8,-1:25,161,-1:3,161:3,113,161:28,113,161:5,-1:25,160,-1:3,160:4,152,160:" +
"19,152,160:13,-1:25,161,-1:3,161:3,115,161:28,115,161:5,-1:25,160,-1:3,160:" +
"5,112,160:21,112,160:10,-1:25,161,-1:3,161:14,155,161:14,155,161:8,-1:25,16" +
"0,-1:3,160:8,114,160:17,114,160:11,-1:25,161,-1:3,161:4,157,161:19,157,161:" +
"13,-1:25,160,-1:3,160:11,154,160:19,154,160:6,-1:25,161,-1:3,161:5,117,161:" +
"21,117,161:10,-1:25,160,-1:3,160:8,156,160:17,156,160:11,-1:25,161,-1:3,161" +
":8,118,161:17,118,161:11,-1:25,160,-1:3,160:12,116,160:5,116,160:19,-1:25,1" +
"61,-1:3,161:11,158,161:19,158,161:6,-1:25,161,-1:3,161:8,159,161:17,159,161" +
":11,-1:25,161,-1:3,161:12,119,161:5,119,161:19,-1:25,160,-1:3,160:3,128,160" +
",130,160:21,130,160:4,128,160:5,-1:25,161,-1:3,161:2,129,161:2,131,161:14,1" +
"29,161:6,131,161:10,-1:25,160,-1:3,160:10,136,160:14,136,160:12,-1:25,161,-" +
"1:3,161:3,133,161,135,161:21,135,161:4,133,161:5,-1:25,160,-1:3,160:14,138," +
"160:14,138,160:8,-1:25,161,-1:3,161:14,141,161:14,141,161:8,-1:25,160,-1:3," +
"160:10,140,160:14,140,160:12,-1:25,161,-1:3,161:10,143,161:14,143,161:12,-1" +
":25,161,-1:3,161:5,145,161:21,145,161:10,-1:11");

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
            return new Symbol(TokenConstants.ERROR, "EOF in comment");
        } else break;
    case STRING:
        if (!eof_reached) {
            eof_reached = true;
            return new Symbol(TokenConstants.ERROR, "EOF in string constant");
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
						{ /* Special case for VTAB, skip */ }
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
						{ return new Symbol(TokenConstants.RPAREN); }
					case -10:
						break;
					case 10:
						{ /* Start string state */
    string_buf = new StringBuffer();
    yybegin(STRING);
}
					case -11:
						break;
					case 11:
						{ /* Integers */
                          return new Symbol(TokenConstants.INT_CONST,
					    AbstractTable.inttable.addString(yytext())); }
					case -12:
						break;
					case 12:
						{ return new Symbol(TokenConstants.EQ); }
					case -13:
						break;
					case 13:
						{
    return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext()));
}
					case -14:
						break;
					case 14:
						{ /* It's an object or type if undetected after the constants */
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
						{ /* Starts comment state */
    commentdepth += 1; 
    yybegin(COMMENT);
}
					case -27:
						break;
					case 27:
						{
    return new Symbol(TokenConstants.ERROR, "Unmatched *)");
}
					case -28:
						break;
					case 28:
						{ return new Symbol(TokenConstants.DARROW); }
					case -29:
						break;
					case 29:
						{ return new Symbol(TokenConstants.FI); }
					case -30:
						break;
					case 30:
						{ return new Symbol(TokenConstants.IF); }
					case -31:
						break;
					case 31:
						{ return new Symbol(TokenConstants.IN); }
					case -32:
						break;
					case 32:
						{ return new Symbol(TokenConstants.OF); }
					case -33:
						break;
					case 33:
						{ return new Symbol(TokenConstants.ASSIGN); }
					case -34:
						break;
					case 34:
						{ return new Symbol(TokenConstants.LE); }
					case -35:
						break;
					case 35:
						{  
    curr_lineno += 1; 
}
					case -36:
						break;
					case 36:
						{ return new Symbol(TokenConstants.LET); }
					case -37:
						break;
					case 37:
						{ return new Symbol(TokenConstants.NEW); }
					case -38:
						break;
					case 38:
						{ return new Symbol(TokenConstants.NOT); }
					case -39:
						break;
					case 39:
						{ return new Symbol(TokenConstants.CASE); }
					case -40:
						break;
					case 40:
						{ return new Symbol(TokenConstants.ESAC); }
					case -41:
						break;
					case 41:
						{ return new Symbol(TokenConstants.ELSE); }
					case -42:
						break;
					case 42:
						{ return new Symbol(TokenConstants.LOOP); }
					case -43:
						break;
					case 43:
						{ return new Symbol(TokenConstants.THEN); }
					case -44:
						break;
					case 44:
						{ return new Symbol(TokenConstants.POOL); }
					case -45:
						break;
					case 45:
						{ return new Symbol(TokenConstants.BOOL_CONST, Boolean.TRUE); }
					case -46:
						break;
					case 46:
						{ return new Symbol(TokenConstants.CLASS); }
					case -47:
						break;
					case 47:
						{ return new Symbol(TokenConstants.BOOL_CONST, Boolean.FALSE); }
					case -48:
						break;
					case 48:
						{ return new Symbol(TokenConstants.WHILE); }
					case -49:
						break;
					case 49:
						{ return new Symbol(TokenConstants.ISVOID); }
					case -50:
						break;
					case 50:
						{ return new Symbol(TokenConstants.INHERITS); }
					case -51:
						break;
					case 51:
						{ curr_lineno += 1; }
					case -52:
						break;
					case 52:
						{ /*
                    *  This should be the very last rule and will match
                    *  everything not matched by other lexical rules.
                    */
                   System.err.println("LEXER BUG - UNMATCHED: " + yytext()); }
					case -53:
						break;
					case 53:
						{ /* Skip comment content */ }
					case -54:
						break;
					case 54:
						{ /* Allows for comment nesting */ commentdepth += 1; }
					case -55:
						break;
					case 55:
						{ /* Checks if all comments are closed, if so, jump to yyinitial */
    commentdepth -= 1;
    if (commentdepth == 0) {
        yybegin(YYINITIAL);
    }    
}
					case -56:
						break;
					case 56:
						{ string_buf.append(yytext()); }
					case -57:
						break;
					case 57:
						{ /* Error case where there's a newline in a string without a / */
    curr_lineno += 1; 
    yybegin(YYINITIAL);
    return new Symbol(TokenConstants.ERROR, "Unterminated string constant.");
}
					case -58:
						break;
					case 58:
						{ /* Returns String, checks to make sure string isn't too long */
    yybegin(YYINITIAL); 
    String output = string_buf.toString();
    if (output.length() >= MAX_STR_CONST) 
        return new Symbol(TokenConstants.ERROR, "String constant too long.");
    else 
        return new Symbol(TokenConstants.STR_CONST, AbstractTable.stringtable.addString(string_buf.toString())); 
}
					case -59:
						break;
					case 59:
						{ /* Error if null character is in a string */
    curr_lineno += 1; 
    yybegin(YYINITIAL);
    return new Symbol(TokenConstants.ERROR, "String contains null character."); 
}
					case -60:
						break;
					case 60:
						{ /* Properly formatted newline in a string */
    curr_lineno += 1; 
    string_buf.append("\n"); 
}
					case -61:
						break;
					case 61:
						{ /* Processes special escaped characters */ 
    String output = yytext();
    if (output.equals("\\t")) string_buf.append("\t");
    else if (output.equals("\\b")) string_buf.append("\b");
    else if (output.equals("\\f")) string_buf.append("\f");
    else if (output.equals("\\n")) string_buf.append("\n");
    else string_buf.append(output.substring(1,2));
}
					case -62:
						break;
					case 62:
						{ /* Processes /0, not the null character */ string_buf.append("0"); }
					case -63:
						break;
					case 64:
						{
    return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext()));
}
					case -64:
						break;
					case 65:
						{ /* It's an object or type if undetected after the constants */
    return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext()));
}
					case -65:
						break;
					case 66:
						{ return new Symbol(TokenConstants.FI); }
					case -66:
						break;
					case 67:
						{ return new Symbol(TokenConstants.IF); }
					case -67:
						break;
					case 68:
						{ return new Symbol(TokenConstants.IN); }
					case -68:
						break;
					case 69:
						{ return new Symbol(TokenConstants.OF); }
					case -69:
						break;
					case 70:
						{ return new Symbol(TokenConstants.LET); }
					case -70:
						break;
					case 71:
						{ return new Symbol(TokenConstants.NEW); }
					case -71:
						break;
					case 72:
						{ return new Symbol(TokenConstants.NOT); }
					case -72:
						break;
					case 73:
						{ return new Symbol(TokenConstants.CASE); }
					case -73:
						break;
					case 74:
						{ return new Symbol(TokenConstants.ESAC); }
					case -74:
						break;
					case 75:
						{ return new Symbol(TokenConstants.ELSE); }
					case -75:
						break;
					case 76:
						{ return new Symbol(TokenConstants.LOOP); }
					case -76:
						break;
					case 77:
						{ return new Symbol(TokenConstants.THEN); }
					case -77:
						break;
					case 78:
						{ return new Symbol(TokenConstants.POOL); }
					case -78:
						break;
					case 79:
						{ return new Symbol(TokenConstants.CLASS); }
					case -79:
						break;
					case 80:
						{ return new Symbol(TokenConstants.WHILE); }
					case -80:
						break;
					case 81:
						{ return new Symbol(TokenConstants.ISVOID); }
					case -81:
						break;
					case 82:
						{ return new Symbol(TokenConstants.INHERITS); }
					case -82:
						break;
					case 83:
						{ /*
                    *  This should be the very last rule and will match
                    *  everything not matched by other lexical rules.
                    */
                   System.err.println("LEXER BUG - UNMATCHED: " + yytext()); }
					case -83:
						break;
					case 84:
						{ /* Skip comment content */ }
					case -84:
						break;
					case 85:
						{ string_buf.append(yytext()); }
					case -85:
						break;
					case 87:
						{
    return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext()));
}
					case -86:
						break;
					case 88:
						{ /* It's an object or type if undetected after the constants */
    return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext()));
}
					case -87:
						break;
					case 89:
						{ /* Skip comment content */ }
					case -88:
						break;
					case 90:
						{
    return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext()));
}
					case -89:
						break;
					case 91:
						{ /* It's an object or type if undetected after the constants */
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
						{ /* It's an object or type if undetected after the constants */
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
						{ /* It's an object or type if undetected after the constants */
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
						{ /* It's an object or type if undetected after the constants */
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
						{ /* It's an object or type if undetected after the constants */
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
						{ /* It's an object or type if undetected after the constants */
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
						{ /* It's an object or type if undetected after the constants */
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
						{ /* It's an object or type if undetected after the constants */
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
						{ /* It's an object or type if undetected after the constants */
    return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext()));
}
					case -106:
						break;
					case 108:
						{
    return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext()));
}
					case -107:
						break;
					case 109:
						{ /* It's an object or type if undetected after the constants */
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
						{ /* It's an object or type if undetected after the constants */
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
						{ /* It's an object or type if undetected after the constants */
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
						{ /* It's an object or type if undetected after the constants */
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
						{ /* It's an object or type if undetected after the constants */
    return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext()));
}
					case -116:
						break;
					case 118:
						{ /* It's an object or type if undetected after the constants */
    return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext()));
}
					case -117:
						break;
					case 119:
						{ /* It's an object or type if undetected after the constants */
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
						{ /* It's an object or type if undetected after the constants */
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
						{ /* It's an object or type if undetected after the constants */
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
						{ /* It's an object or type if undetected after the constants */
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
						{ /* It's an object or type if undetected after the constants */
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
						{ /* It's an object or type if undetected after the constants */
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
						{ /* It's an object or type if undetected after the constants */
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
						{ /* It's an object or type if undetected after the constants */
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
						{ /* It's an object or type if undetected after the constants */
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
						{ /* It's an object or type if undetected after the constants */
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
						{ /* It's an object or type if undetected after the constants */
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
						{ /* It's an object or type if undetected after the constants */
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
						{ /* It's an object or type if undetected after the constants */
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
						{ /* It's an object or type if undetected after the constants */
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
						{ /* It's an object or type if undetected after the constants */
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
						{ /* It's an object or type if undetected after the constants */
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
						{ /* It's an object or type if undetected after the constants */
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
						{ /* It's an object or type if undetected after the constants */
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
						{ /* It's an object or type if undetected after the constants */
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
						{ /* It's an object or type if undetected after the constants */
    return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext()));
}
					case -156:
						break;
					case 158:
						{ /* It's an object or type if undetected after the constants */
    return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext()));
}
					case -157:
						break;
					case 159:
						{ /* It's an object or type if undetected after the constants */
    return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext()));
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
						{ /* It's an object or type if undetected after the constants */
    return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext()));
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
						{ /* It's an object or type if undetected after the constants */
    return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext()));
}
					case -162:
						break;
					case 164:
						{
    return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext()));
}
					case -163:
						break;
					case 165:
						{ /* It's an object or type if undetected after the constants */
    return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext()));
}
					case -164:
						break;
					case 166:
						{
    return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext()));
}
					case -165:
						break;
					case 167:
						{ /* It's an object or type if undetected after the constants */
    return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext()));
}
					case -166:
						break;
					case 168:
						{
    return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext()));
}
					case -167:
						break;
					case 169:
						{ /* It's an object or type if undetected after the constants */
    return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext()));
}
					case -168:
						break;
					case 170:
						{ /* It's an object or type if undetected after the constants */
    return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext()));
}
					case -169:
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
