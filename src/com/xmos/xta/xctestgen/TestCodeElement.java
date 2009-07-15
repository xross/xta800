package com.xmos.xta.xctestgen;

import java.io.BufferedWriter;
import java.io.IOException;

public class TestCodeElement extends CodeElement {
	private String m_description; 
	public TestCodeElement(String desc) {
		super(1, "test_", 0);
		m_description = desc;
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
		
		file.write("function: "+ m_description + "\n");
		for (CodeElement child : m_children.get(node)) {
			child.emitExpect(node, file, depth+1);
		}
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
