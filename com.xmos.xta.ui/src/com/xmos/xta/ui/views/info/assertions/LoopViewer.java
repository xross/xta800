package com.xmos.xta.ui.views.info.assertions;

import org.eclipse.jface.viewers.TableViewer;

import com.xmos.xta.core.engine.Engine;
import com.xmos.xta.ui.views.info.BaseViewer;
import com.xmos.xta.ui.views.info.BaseViewerContentProvider;
import com.xmos.xta.ui.views.info.BaseViewerLabelProvider;

public class LoopViewer extends BaseViewer {
	public LoopViewer(TableViewer viewer) {
		super(viewer);
	}

	protected BaseViewerContentProvider createContentProvider() {
		return new LoopTimeContentProvider();
	}
	
	protected BaseViewerLabelProvider createLabelProvider() {
		return new LabelProvider();
	}

	protected long getPc(Object object) {
		return 0;
	}
}

class LoopTimeContentProvider extends BaseViewerContentProvider {
	public Object[] getElements(Object inputElement) {
		return ((Engine)inputElement).getLoopAssertions().toArray();
	}
}
