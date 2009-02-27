package com.xmos.xta.xctestgen;

import java.io.BufferedWriter;
import java.io.IOException;

public class TestCodeElement extends CodeElement {
	
	public TestCodeElement() {
		super(1, "test_", 0);
	}
	
	public void emitSource(BufferedWriter file, int depth) throws IOException  {
		file.write("void test(int x) {\n");

		for (CodeElement child : getChildren()) {
			child.emitSource(file, depth + 1);
		}
		file.write("}\n");
		file.write("\n");
		file.write("int main() {\n");
		file.write("  test(1);\n");
		file.write("  return 0;\n");
		file.write("}\n");
	}

	public void emitExpect(int node, BufferedWriter file, int depth) throws IOException  {
		int numPaths = getTotalNumPaths();

		file.write("Path\\(id\\): Timing\\(ns\\)/Thread cycles/Instrs/Fnops/Pausable/Executions/Absolute\n");
		file.write("\\+COMPLETE \\[" + numPaths + " path\\(s\\)\\]\n");
		super.emitExpect(node, file, depth + 1);
	}

	public int getNodeNumPaths() {
		return 1;
	}

	public String getName() {
		String name = m_namePart;
		for (CodeElement child : getChildren()) {
			name += child.getName();
		}
		return name;
	}
}
