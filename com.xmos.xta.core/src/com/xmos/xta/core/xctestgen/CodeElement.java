package com.xmos.xta.core.xctestgen;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;

public abstract class CodeElement {

	protected ArrayList<ArrayList<CodeElement>> m_children;
	protected String m_namePart;
	protected int m_id;
	protected int m_maxNodes;

	public CodeElement(int maxNodes, String namePart, int id) {
		m_id = id;
		m_children = new ArrayList<ArrayList<CodeElement>>();
		m_namePart = namePart;
		m_maxNodes = maxNodes;
	}
	
	public int getChildCount(){
		int childCount = 0;
		for(ArrayList<CodeElement> child : m_children){
			childCount+=child.size();
		}
		return childCount;
	}
	
	public void addNode() {
		assert(m_children.size() < m_maxNodes);
		m_children.add(new ArrayList<CodeElement>());
	}
	
	public void addChild(CodeElement element) {
		assert(m_children.size() > 0);
		addChild(m_children.size() - 1, element);
	}

	public void addChild(int node, CodeElement element) {
		assert(node < m_children.size());
		m_children.get(node).add(element);
	}

	public ArrayList<CodeElement> getChildren() {
		return getChildren(0);
	}

	public ArrayList<CodeElement> getChildren(int node) {
		assert(node < m_children.size());
		return m_children.get(node);
	}

	public void emitSourceIndent(BufferedWriter file, int depth) throws IOException  {
		for (int i = 0; i < depth; i++)
			file.write("  ");
	}

	public void emitExpectIndent(BufferedWriter file, int depth) throws IOException  {
		for (int i = 0; i < depth; i++)
			file.write("   ");
	}

	public abstract void emitSource(BufferedWriter file, int depth) throws IOException ;

	public abstract void emitExpect(int node, BufferedWriter file, int depth) throws IOException ;


	public String toString(){
		return getName();
	}
	public String getName() {
		if (m_children.size() == 0) 
			return m_namePart;

		String name = m_namePart;
		if (m_children.get(0).size() > 0)
			name += "-";

		for (ArrayList<CodeElement> children : m_children) {
			for (CodeElement child : children) {
				name += child.getName();
			}
		}

		if (m_children.get(0).size() > 0)
			name += "_";
		return name;
	}
}
