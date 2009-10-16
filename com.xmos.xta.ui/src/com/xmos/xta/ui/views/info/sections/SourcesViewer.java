package com.xmos.xta.ui.views.info.sections;

import org.eclipse.jface.viewers.TableViewer;

public class SourcesViewer extends SectionsViewer {
	public SourcesViewer(TableViewer viewer) {
		super(viewer);
	}

	SectionsViewerContentProvider createContentProvider() {
		return new SourcesViewerContentProvider();
	}
	
	SectionsViewerLabelProvider createLabelProvider() {
		return new SourcesViewerLabelProvider();
	}

	protected long getPc(Object object) {
		return 0;
	}
}
