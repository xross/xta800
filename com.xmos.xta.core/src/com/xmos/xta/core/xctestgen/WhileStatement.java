package com.xmos.xta.core.xctestgen;

import java.io.BufferedWriter;
import java.io.IOException;

public class WhileStatement extends CodeElement {

	public WhileStatement(int id) {
		super(1, "w", id);
	}

	public void emitSource(BufferedWriter file, int depth) throws IOException  {
		emitSourceIndent(file, depth);
		file.write("while (x < " + m_id + " + temp) {\n");
		for (CodeElement child : getChildren()) {
			child.emitSource(file, depth + 1);
		}
		emitSourceIndent(file, depth + 1);
		file.write("x--;\n");
		emitSourceIndent(file, depth);
		file.write("}\n");
	}

	public void emitExpect(int node, BufferedWriter file, int depth) throws IOException  {
		emitExpectIndent(file, depth);
		file.write("loop \n");
		for (CodeElement child : m_children.get(node)) {
			child.emitExpect(node, file, depth+1);
		}
	}

}

