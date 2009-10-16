package com.xmos.xta.ui.views.info.sections;

import org.eclipse.jface.viewers.TableViewer;

import com.xmos.xta.core.engine.sections.SrcLabelTableInfo;

public class AllSrcLabelsViewer extends SectionsViewer {
	public AllSrcLabelsViewer(TableViewer viewer) {
		super(viewer);
	}

	SectionsViewerContentProvider createContentProvider() {
		return new AllSrcLabelsViewerContentProvider();
	}
	
	SectionsViewerLabelProvider createLabelProvider() {
		return new AllSrcLabelsViewerLabelProvider();
	}

	@Override
	protected long getPc(Object object) {
		return ((SrcLabelTableInfo)object).getPc();
	}
}
