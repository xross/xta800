package com.xmos.xta.ui.views.info.sections;

import org.eclipse.jface.viewers.TableViewer;

import com.xmos.xta.core.engine.sections.CodePointInfo;
import com.xmos.xta.ui.views.info.BaseViewer;
import com.xmos.xta.ui.views.info.BaseViewerContentProvider;
import com.xmos.xta.ui.views.info.BaseViewerLabelProvider;

public class SrcLabelsViewer extends BaseViewer {
	public SrcLabelsViewer(TableViewer viewer) {
		super(viewer);
	}

	protected BaseViewerContentProvider createContentProvider() {
		return new SrcLabelsViewerContentProvider();
	}
	
	protected BaseViewerLabelProvider createLabelProvider() {
		return new SrcLabelsViewerLabelProvider();
	}

	@Override
	protected long getPc(Object object) {
		return ((CodePointInfo)object).getPc();
	}
}
