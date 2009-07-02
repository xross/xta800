package com.xmos.xta.gui.views.info.sections;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import com.xmos.xta.engine.Engine;

class BranchesViewerContentProvider extends SectionsViewerContentProvider {
	public Object[] getElements(Object inputElement) {
		Map<Integer, HashSet<Integer>> branchesMap = ((Engine)inputElement).getBranches();
		
		List<BranchesContent> content = new ArrayList<BranchesContent>();
		for (Integer fromPc : branchesMap.keySet()) {			
			content.add(new BranchesContent(fromPc, branchesMap.get(fromPc)));
		}				
		return content.toArray();
	}
}

class BranchesContent {
	Integer m_fromPc;
	HashSet<Integer> m_toPcs;
	
	BranchesContent(Integer fromPc, HashSet<Integer> toPcs) {
		m_fromPc = fromPc;
		m_toPcs = toPcs;
	}
}