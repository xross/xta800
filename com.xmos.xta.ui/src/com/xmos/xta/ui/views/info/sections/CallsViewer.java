package com.xmos.xta.ui.views.info.sections;

import java.util.Set;
import java.util.TreeSet;

import org.eclipse.jface.viewers.TableViewer;

import com.xmos.xta.core.engine.sections.CodePointInfo;
import com.xmos.xta.ui.views.info.BaseViewer;
import com.xmos.xta.ui.views.info.BaseViewerContentProvider;
import com.xmos.xta.ui.views.info.BaseViewerLabelProvider;

public class CallsViewer extends BaseViewer {
	public CallsViewer(TableViewer viewer) {
		super(viewer);
	}

	protected BaseViewerContentProvider createContentProvider() {
		return new CallsViewerContentProvider();
	}
	
	protected BaseViewerLabelProvider createLabelProvider() {
		return new CallsViewerLabelProvider();
	}

	@Override
	protected Set<Long> getPcs(Object object) {
		Set<Long> pcs = new TreeSet<Long>();
		pcs.add(((CodePointInfo)object).getPc());
		return pcs;
	}
}
