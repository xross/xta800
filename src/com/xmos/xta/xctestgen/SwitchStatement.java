package com.xmos.xta.xctestgen;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;

public class SwitchStatement extends CodeElement {
	
	public SwitchStatement(int id) {
		super(10, "s", id);
	}
	
	public void emitSource(BufferedWriter file, int depth) throws IOException {
		emitSourceIndent(file, depth);
		file.write("switch (x) {\n");
		for (int i = 0; i < m_children.size(); i++) {
			emitSourceIndent(file, depth);
			int caseId = m_id + i;
			file.write("case " + caseId +":\n");
			for (CodeElement child : getChildren(i)) {
				child.emitSource(file, depth + 1);
			}
			emitSourceIndent(file, depth + 1);
			file.write("break;\n");
		}
		emitSourceIndent(file, depth);
		file.write("}\n");
	}

	public void emitExpect(int node, BufferedWriter file, int depth) throws IOException  {
		// Do nothing
	}

	public int getNodeNumPaths() {
		int numPaths = 1;
		for (ArrayList<CodeElement> children : m_children) {
			for (CodeElement child : children) {
				numPaths *= child.getNodeNumPaths();
			}
		}
		return numPaths + m_children.size();
	}
}
