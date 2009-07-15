package com.xmos.xta.xctestgen;

import java.io.BufferedWriter;
import java.io.IOException;

public class EndPointStatement extends CodeElement{
	
	public EndPointStatement(int id) {
		super(0, "p", id);
	}
	
	public void emitSource(BufferedWriter file, int depth) throws IOException  {
		emitSourceIndent(file, depth);
		file.write("#pragma xta endpoint \"endpoint" + m_id + "\"\n");
		file.write("c <: x;\n");
	}

	public void emitExpect(int node, int pathNum, BufferedWriter file, int depth) throws IOException  {
		// Do nothing
	}

	public int getNodeNumPaths() {
		return 1;
	}
}
