package com.xmos.xta.ui.views.info.sections;

import java.util.Set;
import java.util.TreeSet;

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
	protected Set<Long> getPcs(Object object) {
		Set<Long> pcs = new TreeSet<Long>();
		pcs.add(((CodePointTableInfo)object).getPc());
		return pcs;
	}
}
