package com.xmos.xta.xctestgen;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;

public class IfStatement extends CodeElement {
	
	public IfStatement(int id) {
		super(10, "i", id);
	}

	public void emitSource(BufferedWriter file, int depth) throws IOException  {
		if(m_children.size() == 1){
			emitSourceIndent(file, depth);
			file.write("if (x == " + m_id + " + temp) {\n");
			for (CodeElement child : getChildren(0)) {
				child.emitSource(file, depth + 1);
			}
			emitSourceIndent(file, depth);
			file.write("}\n");
		} else if(m_children.size() == 2){
			emitSourceIndent(file, depth);
			file.write("if (x == " + m_id + " + temp) {\n");
			for (CodeElement child : getChildren(0)) {
				child.emitSource(file, depth + 1);
			}
			emitSourceIndent(file, depth);
			file.write("} else {\n");
			for (CodeElement child : getChildren(1)) {
				child.emitSource(file, depth + 1);
			}
			emitSourceIndent(file, depth);
			file.write("}\n");
		} else if(m_children.size() > 2){
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
	}

	public void emitExpect(int node, BufferedWriter file, int depth) throws IOException  {
		
		emitExpectIndent(file, depth);
		file.write("cond\n");
		
		if(m_children.size() == 1 ) { 
			for (ArrayList<CodeElement> children : m_children) {
				for (CodeElement child : children) {
					child.emitExpect(node, file, depth+1);
				}
			}
		} else if (m_children.size() == 2){
			for (ArrayList<CodeElement> children : m_children) {
				for (CodeElement child : children) {
					child.emitExpect(node, file, depth+1);
				}
			}
		} else if (m_children.size() > 2){
			//TODO - ill do this later as its hard but not nessessary
			assert(false);
		}
		
		
		
	}

	
}
