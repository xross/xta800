package com.xmos.xta.ui.views.info.sections;

import org.eclipse.jface.viewers.TableViewer;

import com.xmos.xta.core.engine.sections.CodePointTableInfo;
import com.xmos.xta.ui.views.info.BaseViewer;
import com.xmos.xta.ui.views.info.BaseViewerContentProvider;
import com.xmos.xta.ui.views.info.BaseViewerLabelProvider;

public class AllEndpointsViewer extends BaseViewer {
	public AllEndpointsViewer( TableViewer viewer) {
		super(viewer);
	}

	protected BaseViewerContentProvider createContentProvider() {
		return new AllEndpointsViewerContentProvider();
	}
	
	protected BaseViewerLabelProvider createLabelProvider() {
		return new AllEndpointsViewerLabelProvider();
	}

	@Override
	protected long getPc(Object object) {
		return ((CodePointTableInfo)object).getPc();
	}
}
