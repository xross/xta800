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

	// Emit the part of the switch which relates to the current path 
	public void emitExpect(int node, int pathNum, BufferedWriter file, int depth) throws IOException  {
		pathNum = pathNum % getNodeNumPaths();

		int numPaths = 0;
		for (ArrayList<CodeElement> children : m_children) {
			int childNumPaths = 1;
			for (CodeElement child : children) {
				childNumPaths *= child.getNodeNumPaths();
			}
			numPaths += childNumPaths;

			if (pathNum < numPaths) {
				for (CodeElement child : children) {
					child.emitExpect(node, pathNum, file, depth);
				}
				break;
			}
		}
	}

	public int getNodeNumPaths() {
		int numPaths = 1;
		for (ArrayList<CodeElement> children : m_children) {
			int childNumPaths = 1;
			for (CodeElement child : children) {
				childNumPaths *= child.getNodeNumPaths();
			}
			numPaths += childNumPaths;
		}
		return numPaths;
	}
}
