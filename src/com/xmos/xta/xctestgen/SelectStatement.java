package com.xmos.xta.xctestgen;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;

public class SelectStatement extends CodeElement {
	
	public SelectStatement(int id) {
		super(10, "e", id);
	}
	
	public void emitSource(BufferedWriter file, int depth) throws IOException {
		// Need to create a basic block for the timer declarations
		emitSourceIndent(file, depth);
		file.write("{\n");
		depth++;

		for (int i = 0; i < m_children.size(); i++) {
			emitSourceIndent(file, depth);
			file.write("timer tmr" + i + ";\n");
		}

		emitSourceIndent(file, depth);
		file.write("select {\n");
		for (int i = 0; i < m_children.size(); i++) {
			emitSourceIndent(file, depth);
			file.write("case tmr" + i +" :> int tmp" + i + ":\n");
			for (CodeElement child : getChildren(i)) {
				child.emitSource(file, depth + 1);
			}
			emitSourceIndent(file, depth + 1);
			file.write("break;\n");
		}
		emitSourceIndent(file, depth);
		file.write("}\n");

		depth--;
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
		int numPaths = 0;
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
