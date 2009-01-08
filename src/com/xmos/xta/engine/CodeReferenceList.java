package com.xmos.xta.engine;

import java.util.List;
import java.util.Vector;

public class CodeReferenceList {
    private int m_nodeId;
	private int m_coreId;
	private Vector<CodeReference> m_references;
		
	CodeReferenceList(int nodeId, int coreId) {
		m_nodeId = nodeId;
		m_coreId = coreId;
		m_references = new Vector<CodeReference>(); 
	}
		
	int getNodeId() {
		return m_nodeId;
	}

	int getCoreId() {
		return m_coreId;
	}
		
	List<CodeReference> getReferences() {
		return m_references;
	}
}
