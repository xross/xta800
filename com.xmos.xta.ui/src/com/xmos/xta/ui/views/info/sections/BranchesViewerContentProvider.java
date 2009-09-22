package com.xmos.xta.ui.views.info.sections;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import com.xmos.xta.core.engine.Engine;

class BranchesViewerContentProvider extends SectionsViewerContentProvider {
	public Object[] getElements(Object inputElement) {
		Map<Long, TreeSet<Long>> branchesMap = ((Engine)inputElement).getBranches();
		
		List<BranchesContent> content = new ArrayList<BranchesContent>();
		for (Long fromPc : branchesMap.keySet()) {			
			content.add(new BranchesContent(fromPc, branchesMap.get(fromPc)));
		}				
		return content.toArray();
	}
}

class BranchesContent {
	long m_fromPc;
	TreeSet<Long> m_toPcs;
	
	BranchesContent(long fromPc, TreeSet<Long> toPcs) {
		m_fromPc = fromPc;
		m_toPcs = toPcs;
	}
}