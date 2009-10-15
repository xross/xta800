package com.xmos.xta.ui.views.info.sections;

import org.eclipse.jface.viewers.TableViewer;

public class SystemsViewer extends SectionsViewer {
	public SystemsViewer(TableViewer viewer) {
		super(viewer);
	}

	SectionsViewerContentProvider createContentProvider() {
		return new SystemsViewerContentProvider();
	}
	
	SectionsViewerLabelProvider createLabelProvider() {
		return new SystemsViewerLabelProvider();
	}

	protected long getPc(Object object) {
		return 0;
	}
}
