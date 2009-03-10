package com.xmos.xta.xctestgen;

import java.io.BufferedWriter;
import java.io.IOException;

public class DoWhileStatement extends CodeElement {
	
	public DoWhileStatement(int id) {
		super(1, "d", id);
	}
	
	public void emitSource(BufferedWriter file, int depth) throws IOException  {
		emitSourceIndent(file, depth);
		file.write("do {\n");
		for (CodeElement child : getChildren()) {
			child.emitSource(file, depth + 1);
		}
		emitSourceIndent(file, depth + 1);
		file.write("x--;\n");
		emitSourceIndent(file, depth);
		file.write("} while (x < " + m_id + ");\n");
	}

	public void emitExpect(int node, int pathNum, BufferedWriter file, int depth) throws IOException  {
		int numPaths = getTotalNumPaths();

		emitExpectIndent(file, depth);
		
		if (numPaths == 1)
		    file.write("loop \\[" + numPaths + " path, 0 iterations\\]\n");
		else
		    file.write("loop \\[" + numPaths + " paths, 0 iterations\\]\n");
		
		super.emitExpect(node, pathNum, file, depth + 1);
	}

	public int getNodeNumPaths() {
		return 1;
	}
}

