package com.xmos.xta.core.xctestgen;

import java.io.BufferedWriter;
import java.io.IOException;

public class ReturnStatement extends CodeElement {

	public ReturnStatement(int id) {
		super(0, "r", id);
	}

	public void emitSource(BufferedWriter file, int depth) throws IOException  {
		emitSourceIndent(file, depth);
		file.write("if (( temp^ " + m_id +") == (x + " + m_id +")) {\n");
		emitSourceIndent(file, depth);
		file.write("	return x;\n");
		emitSourceIndent(file, depth);
		file.write("}\n");
	}

	public void emitExpect(int node, BufferedWriter file, int depth) throws IOException  {
		emitExpectIndent(file, depth);
		file.write("");
	}

	public int getNodeNumPaths() {
		return 1;
	}
}

