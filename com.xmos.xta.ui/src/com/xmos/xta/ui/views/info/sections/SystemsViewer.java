package com.xmos.xta.ui.views.info.sections;

import org.eclipse.jface.viewers.TableViewer;

import com.xmos.xta.ui.views.info.BaseViewer;
import com.xmos.xta.ui.views.info.BaseViewerContentProvider;
import com.xmos.xta.ui.views.info.BaseViewerLabelProvider;

public class SystemsViewer extends BaseViewer {
	public SystemsViewer(TableViewer viewer) {
		super(viewer);
	}

	protected BaseViewerContentProvider createContentProvider() {
		return new SystemsViewerContentProvider();
	}
	
	protected BaseViewerLabelProvider createLabelProvider() {
		return new SystemsViewerLabelProvider();
	}

	protected long getPc(Object object) {
		return 0;
	}
}
