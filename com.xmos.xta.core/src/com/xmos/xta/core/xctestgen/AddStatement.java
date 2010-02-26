package com.xmos.xta.core.xctestgen;

import java.io.BufferedWriter;
import java.io.IOException;

public class AddStatement extends CodeElement {

	public AddStatement(int id) {
		super(0, "a", id);
	}

	public void emitSource(BufferedWriter file, int depth) throws IOException  {
		emitSourceIndent(file, depth);
		file.write("x = " + m_id + " ^ temp;\n");
	}

	public void emitExpect(int node, BufferedWriter file, int depth) throws IOException  {
		emitExpectIndent(file, depth);
		//.  * block(1368)    : 80.0ns (x 1) / 80.0ns (x 1)
		file.write("block\\(\\d+\\) +: \\d+.\\d+ns \\(x \\d+\\)/ \\d+.\\d+ns \\(x \\d+\\)\n");
	}

	public int getNodeNumPaths() {
		return 1;
	}
}

