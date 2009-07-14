package com.xmos.xta.xctestgen;

import java.io.BufferedWriter;
import java.io.IOException;

public class ForStatement extends CodeElement {
	
	public ForStatement(int id) {
		super(1, "f", id);
	}
	
	public void emitSource(BufferedWriter file, int depth) throws IOException  {
		emitSourceIndent(file, depth);
		file.write("for (int i" + m_id + " = 0; i" + m_id + " < temp; i" + m_id + "++) {\n");
		for (CodeElement child : getChildren()) {
			child.emitSource(file, depth + 1);
		}
		emitSourceIndent(file, depth);
		file.write("}\n");
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

