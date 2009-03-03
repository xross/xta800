package com.xmos.xta.xctestgen;

import java.io.BufferedWriter;
import java.io.IOException;

public class WhileStatement extends CodeElement {
	
	public WhileStatement(int id) {
		super(1, "w", id);
	}
	
	public void emitSource(BufferedWriter file, int depth) throws IOException  {
		emitSourceIndent(file, depth);
		file.write("while (x < " + m_id + ") {\n");
		for (CodeElement child : getChildren()) {
			child.emitSource(file, depth + 1);
		}
		emitSourceIndent(file, depth + 1);
		file.write("x--;\n");
		emitSourceIndent(file, depth);
		file.write("}\n");
	}

	public void emitExpect(int node, int pathNum, BufferedWriter file, int depth) throws IOException  {
		int numPaths = getTotalNumPaths();

		emitExpectIndent(file, depth);
		file.write("\\+loop \\[" + numPaths + " path\\(s\\), 0 iteration\\(s\\)\\]\n");
		
		super.emitExpect(node, pathNum, file, depth + 1);
	}

	public int getNodeNumPaths() {
		return 1;
	}
}

