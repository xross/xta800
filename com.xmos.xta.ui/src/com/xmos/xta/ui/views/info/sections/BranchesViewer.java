package com.xmos.xta.ui.views.info.sections;

import org.eclipse.jface.viewers.TableViewer;

import com.xmos.xta.ui.views.info.BaseViewer;
import com.xmos.xta.ui.views.info.BaseViewerContentProvider;
import com.xmos.xta.ui.views.info.BaseViewerLabelProvider;

public class BranchesViewer extends BaseViewer {
	public BranchesViewer(TableViewer viewer) {
		super(viewer);
	}

	protected BaseViewerContentProvider createContentProvider() {
		return new BranchesViewerContentProvider();
	}
	
	protected BaseViewerLabelProvider createLabelProvider() {
		return new BranchesViewerLabelProvider();
	}

	@Override
	protected long getPc(Object object) {
		return ((BranchesContent)object).m_fromRef.getPc();
	}
}
