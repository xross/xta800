package com.xmos.xta.xctestgen;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;

public class IfStatement extends CodeElement {
	
	public IfStatement(int id) {
		super(10, "i", id);
	}
	
	public void emitSource(BufferedWriter file, int depth) throws IOException  {
		for (int i = 0; i < m_children.size(); i++) {
			if (i > 0) {
				file.write(" else ");
			} else {
				emitSourceIndent(file, depth);
			}
			
			file.write("if (x == " + m_id + " + temp) {\n");
			for (CodeElement child : getChildren(i)) {
				child.emitSource(file, depth + 1);
			}
			emitSourceIndent(file, depth);
			file.write("}");
		}
		file.write("\n");
	}

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
