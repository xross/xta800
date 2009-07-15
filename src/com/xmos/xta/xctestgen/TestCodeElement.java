package com.xmos.xta.xctestgen;

import java.io.BufferedWriter;
import java.io.IOException;

public class TestCodeElement extends CodeElement {
	
	public TestCodeElement() {
		super(1, "test_", 0);
	}
	
	public void emitSource(BufferedWriter file, int depth) throws IOException  {
		
		file.write("#include <xs1.h>\n");
		file.write("port c = XS1_PORT_1A;\n");
		file.write("int temp = 1;\n");
		file.write("int test(int x, port c) {\n");

		for (CodeElement child : getChildren()) {
			child.emitSource(file, depth + 1);
		}
		file.write("}\n");
		file.write("\n");
		file.write("int main() {\n");
		file.write("  int returnVal = 0;\n");
		file.write("  #pragma xta endpoint \"start\"\n");
		file.write("  c<: returnVal;\n");
		file.write("  returnVal = test(1, c);\n");
		file.write("  #pragma xta endpoint \"end\"\n");
		file.write("  c<: returnVal;\n");
		file.write("  return returnVal;\n");
		file.write("}\n");
	}

	public void emitExpect(int node, int pathNum, BufferedWriter file, int depth) throws IOException  {
		int numPaths = getTotalNumPaths();

		file.write("Route\\(0\\) function: test\n");
		if (numPaths == 1)
		    file.write("Complete \\[" + numPaths + " path\\]\n");
		else
		    file.write("Complete \\[" + numPaths + " paths\\]\n");
		
		super.emitExpect(node, pathNum, file, depth + 1);
		file.write("\n");
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
