package com.xmos.xta.ui.views.info.sections;

import org.eclipse.jface.viewers.TableViewer;

import com.xmos.xta.ui.views.info.BaseViewer;
import com.xmos.xta.ui.views.info.BaseViewerContentProvider;
import com.xmos.xta.ui.views.info.BaseViewerLabelProvider;

public class LabelsViewer extends BaseViewer {
	public LabelsViewer(TableViewer viewer) {
		super(viewer);
	}

	protected BaseViewerContentProvider createContentProvider() {
		return new LabelsViewerContentProvider();
	}
	
	protected BaseViewerLabelProvider createLabelProvider() {
		return new LabelsViewerLabelProvider();
	}

	protected long getPc(Object object) {
		return ((LabelsContent)object).m_pc;
	}
}
