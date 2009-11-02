package com.xmos.xta.ui.views.info.sections;

import org.eclipse.jface.viewers.TableViewer;

import com.xmos.xta.core.engine.sections.CodePointInfo;
import com.xmos.xta.ui.views.info.BaseViewer;
import com.xmos.xta.ui.views.info.BaseViewerContentProvider;
import com.xmos.xta.ui.views.info.BaseViewerLabelProvider;

public class EndpointsViewer extends BaseViewer {
	public EndpointsViewer(TableViewer viewer) {
		super(viewer);
	}

	protected BaseViewerContentProvider createContentProvider() {
		return new EndpointsViewerContentProvider();
	}
	
	protected BaseViewerLabelProvider createLabelProvider() {
		return new EndpointsViewerLabelProvider();
	}

	@Override
	protected long getPc(Object object) {
		return ((CodePointInfo)object).getPc();
	}
}
