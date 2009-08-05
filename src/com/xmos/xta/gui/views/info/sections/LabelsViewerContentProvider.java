package com.xmos.xta.gui.views.info.sections;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.xmos.xta.engine.Engine;

class LabelsViewerContentProvider extends SectionsViewerContentProvider {
	public Object[] getElements(Object inputElement) {
		Map<String, Long> labelsMap = ((Engine)inputElement).getLabels();
		
		List<LabelsContent> content = new ArrayList<LabelsContent>();
		for (String label : labelsMap.keySet()) {			
			content.add(new LabelsContent(label, labelsMap.get(label)));
		}		
		
		return content.toArray();
	}
}

class LabelsContent {
	String m_label;
	Long m_pc;
	
	LabelsContent(String label, Long pc) {
		m_label = label;
		m_pc = pc;
	}
}