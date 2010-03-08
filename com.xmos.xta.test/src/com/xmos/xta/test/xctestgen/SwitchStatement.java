package com.xmos.xta.test.xctestgen;

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
	public void emitExpect(int node, BufferedWriter file, int depth) throws IOException  {
//TODO
		for (ArrayList<CodeElement> children : m_children) {
				for (CodeElement child : children) {
					child.emitExpect(node, file, depth);
				}
		}
	}

}
