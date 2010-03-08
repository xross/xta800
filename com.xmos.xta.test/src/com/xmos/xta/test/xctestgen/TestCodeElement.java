package com.xmos.xta.test.xctestgen;

import java.io.BufferedWriter;
import java.io.IOException;

public class TestCodeElement extends CodeElement {
	public TestCodeElement(String desc) {
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

	public void emitExpect(int node, BufferedWriter file, int depth) throws IOException  {
		//function(1363) : 520.0ns / 460.0ns test


		file.write("function\\(\\d+\\) +: \\d+.\\d+ns / \\d+.\\d+ns test\n");
		for (CodeElement child : m_children.get(node)) {
			child.emitExpect(node, file, depth+1);
		}
		file.write("\n");
	}

	public int getNodeNumPaths() {
		return 1;
	}

	public String getName() {
		StringBuffer sb = new StringBuffer(m_namePart);
		for (CodeElement child : getChildren()) {
			sb.append(child.getName());
		}
		return sb.toString();
	}
}
