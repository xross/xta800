package com.xmos.xta.ui.views.info.sections;

import org.eclipse.jface.viewers.TableViewer;

import com.xmos.xta.core.engine.sections.CodePointTableInfo;
import com.xmos.xta.ui.views.info.BaseViewer;
import com.xmos.xta.ui.views.info.BaseViewerContentProvider;
import com.xmos.xta.ui.views.info.BaseViewerLabelProvider;

public class AllCallsViewer extends BaseViewer {
	public AllCallsViewer(TableViewer viewer) {
		super(viewer);
	}

	protected BaseViewerContentProvider createContentProvider() {
		return new AllCallsViewerContentProvider();
	}
	
	protected BaseViewerLabelProvider createLabelProvider() {
		return new AllCallsViewerLabelProvider();
	}

	@Override
	protected long getPc(Object object) {
		return ((CodePointTableInfo)object).getPc();
	}
}
