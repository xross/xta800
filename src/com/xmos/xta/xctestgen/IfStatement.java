package com.xmos.xta.xctestgen;

import java.io.BufferedWriter;
import java.io.IOException;

public class IfStatement extends CodeElement {
	
	public IfStatement(int id) {
		super(10, "i", id);
	}
	
	public void emitSource(BufferedWriter file, int depth) throws IOException  {
		emitSourceIndent(file, depth);
		file.write("if (x == " + m_id + ") {\n");
		for (CodeElement child : getChildren()) {
			child.emitSource(file, depth + 1);
		}
		emitSourceIndent(file, depth);
		file.write("}\n");
	}

	public void emitExpect(int node, BufferedWriter file, int depth) throws IOException  {
		// Do nothing
	}

	public int getNodeNumPaths() {
		int numPaths = 1;
		for (CodeElement child : getChildren()) {
			numPaths *= child.getNodeNumPaths();
		}
		return numPaths + 1;
	}
}
