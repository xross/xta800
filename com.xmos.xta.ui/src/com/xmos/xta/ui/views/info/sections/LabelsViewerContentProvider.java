package com.xmos.xta.ui.views.info.sections;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.xmos.xta.core.engine.Engine;
import com.xmos.xta.ui.views.info.BaseViewerContentProvider;

class LabelsViewerContentProvider extends BaseViewerContentProvider {
	public Object[] getElements(Object inputElement) {
		Map<String, Set<Long>> labelsMap = ((Engine)inputElement).getLabels();
		
		List<LabelsContent> content = new ArrayList<LabelsContent>();
		for (String label : labelsMap.keySet()) {			
			content.add(new LabelsContent(label, labelsMap.get(label)));
		}		
		
		return content.toArray();
	}
}

class LabelsContent {
	String m_label;
	Set<Long> m_pcs;
	
	LabelsContent(String label, Set<Long> pcs) {
		m_label = label;
		m_pcs = pcs;
	}
}