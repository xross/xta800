package com.xmos.xta.gui.views.info.sections;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import com.xmos.xta.engine.Engine;

class BranchesViewerContentProvider extends SectionsViewerContentProvider {
	public Object[] getElements(Object inputElement) {
		Map<Integer, TreeSet<Integer>> branchesMap = ((Engine)inputElement).getBranches();
		
		List<BranchesContent> content = new ArrayList<BranchesContent>();
		for (Integer fromPc : branchesMap.keySet()) {			
			content.add(new BranchesContent(fromPc, branchesMap.get(fromPc)));
		}				
		return content.toArray();
	}
}

class BranchesContent {
	Integer m_fromPc;
	TreeSet<Integer> m_toPcs;
	
	BranchesContent(Integer fromPc, TreeSet<Integer> toPcs) {
		m_fromPc = fromPc;
		m_toPcs = toPcs;
	}
}