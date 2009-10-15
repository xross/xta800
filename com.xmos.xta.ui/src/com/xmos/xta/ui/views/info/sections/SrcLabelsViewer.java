package com.xmos.xta.ui.views.info.sections;

import org.eclipse.jface.viewers.TableViewer;

import com.xmos.xta.core.engine.sections.CodePointInfo;

public class SrcLabelsViewer extends SectionsViewer {
	public SrcLabelsViewer(TableViewer viewer) {
		super(viewer);
	}

	SectionsViewerContentProvider createContentProvider() {
		return new SrcLabelsViewerContentProvider();
	}
	
	SectionsViewerLabelProvider createLabelProvider() {
		return new SrcLabelsViewerLabelProvider();
	}

	@Override
	protected long getPc(Object object) {
		return ((CodePointInfo)object).getPc();
	}
}
