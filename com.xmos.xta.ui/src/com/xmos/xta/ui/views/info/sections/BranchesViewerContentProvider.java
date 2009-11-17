package com.xmos.xta.ui.views.info.sections;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.xmos.xta.core.engine.Engine;
import com.xmos.xta.core.engine.references.Reference;
import com.xmos.xta.core.engine.sections.BranchInfo;
import com.xmos.xta.ui.views.info.BaseViewerContentProvider;

class BranchesViewerContentProvider extends BaseViewerContentProvider {
	public Object[] getElements(Object inputElement) {
		Map<Reference, Set<BranchInfo>> branchesMap = ((Engine)inputElement).getBranches();
		
		List<BranchesContent> content = new ArrayList<BranchesContent>();
		for (Reference fromRef : branchesMap.keySet()) {			
			content.add(new BranchesContent(fromRef, branchesMap.get(fromRef)));
		}				
		return content.toArray();
	}
}

class BranchesContent {
	Reference m_fromRef;
	Set<BranchInfo> m_toRefs;
	
	BranchesContent(Reference fromRef, Set<BranchInfo> toRefs) {
		m_fromRef = fromRef;
		m_toRefs = toRefs;
	}
}