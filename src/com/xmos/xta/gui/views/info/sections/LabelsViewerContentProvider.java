package com.xmos.xta.gui.views.info.sections;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.xmos.xta.engine.Engine;

class LabelsViewerContentProvider extends SectionsViewerContentProvider {
	public Object[] getElements(Object inputElement) {
		Map<String, Integer> labelsMap = ((Engine)inputElement).getLabels();
		
		List<LabelsContent> content = new ArrayList<LabelsContent>();
		for (String label : labelsMap.keySet()) {			
			content.add(new LabelsContent(label, labelsMap.get(label)));
		}		
		
		return content.toArray();
	}
}

class LabelsContent {
	String m_label;
	Integer m_pc;
	
	LabelsContent(String label, Integer pc) {
		m_label = label;
		m_pc = pc;
	}
}