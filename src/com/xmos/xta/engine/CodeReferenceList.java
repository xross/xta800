package com.xmos.xta.engine;

import java.util.List;
import java.util.Vector;

import com.xmos.xta.engine.references.Reference;

public class CodeReferenceList {
    private int m_nodeId;
	private int m_coreId;
	private Vector<Reference> m_references;
		
	CodeReferenceList(int nodeId, int coreId) {
		m_nodeId = nodeId;
		m_coreId = coreId;
		m_references = new Vector<Reference>(); 
	}
		
	int getNodeId() {
		return m_nodeId;
	}

	int getCoreId() {
		return m_coreId;
	}
		
	List<Reference> getReferences() {
		return m_references;
	}
}
