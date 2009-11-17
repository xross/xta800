package com.xmos.xta.ui.views.info.sections;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.xmos.xta.core.engine.Engine;
import com.xmos.xta.core.engine.sections.BranchInfo;
import com.xmos.xta.ui.views.info.BaseViewerContentProvider;

class BranchesViewerContentProvider extends BaseViewerContentProvider {
	public Object[] getElements(Object inputElement) {
		Map<Long, Set<BranchInfo>> branchesMap = ((Engine)inputElement).getBranches();
		
		List<BranchesContent> content = new ArrayList<BranchesContent>();
		for (Long fromPc : branchesMap.keySet()) {			
			content.add(new BranchesContent(fromPc, branchesMap.get(fromPc)));
		}				
		return content.toArray();
	}
}

class BranchesContent {
	long m_fromPc;
	Set<BranchInfo> m_toPcs;
	
	BranchesContent(long fromPc, Set<BranchInfo> toPcs) {
		m_fromPc = fromPc;
		m_toPcs = toPcs;
	}
}