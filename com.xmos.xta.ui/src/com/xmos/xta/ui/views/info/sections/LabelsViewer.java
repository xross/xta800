package com.xmos.xta.ui.views.info.sections;

import org.eclipse.jface.viewers.TableViewer;

public class LabelsViewer extends SectionsViewer {
	public LabelsViewer(TableViewer viewer) {
		super(viewer);
	}

	SectionsViewerContentProvider createContentProvider() {
		return new LabelsViewerContentProvider();
	}
	
	SectionsViewerLabelProvider createLabelProvider() {
		return new LabelsViewerLabelProvider();
	}

	protected long getPc(Object object) {
		return ((LabelsContent)object).m_pc;
	}
}
