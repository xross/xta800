package com.xmos.xta.ui.views.info.sections;

import org.eclipse.jface.viewers.TableViewer;

import com.xmos.xta.core.engine.sections.SrcLevelLoopInfo;

public class SrcLoopsViewer extends SectionsViewer {
	public SrcLoopsViewer(TableViewer viewer) {
		super(viewer);
	}

	SectionsViewerContentProvider createContentProvider() {
		return new SrcLoopsViewerContentProvider();
	}
	
	SectionsViewerLabelProvider createLabelProvider() {
		return new SrcLoopsViewerLabelProvider();
	}

	@Override
	protected long getPc(Object object) {
		return ((SrcLevelLoopInfo)object).getPc();
	}
}
