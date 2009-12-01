package com.xmos.xta.ui.views.info.sections;

import java.util.Set;
import java.util.TreeSet;

import org.eclipse.jface.viewers.TableViewer;

import com.xmos.xta.core.engine.sections.SrcLevelLoopInfo;
import com.xmos.xta.ui.views.info.BaseViewer;
import com.xmos.xta.ui.views.info.BaseViewerContentProvider;
import com.xmos.xta.ui.views.info.BaseViewerLabelProvider;

public class SrcLoopsViewer extends BaseViewer {
	public SrcLoopsViewer(TableViewer viewer) {
		super(viewer);
	}

	protected BaseViewerContentProvider createContentProvider() {
		return new SrcLoopsViewerContentProvider();
	}
	
	protected BaseViewerLabelProvider createLabelProvider() {
		return new SrcLoopsViewerLabelProvider();
	}

	@Override
	protected Set<Long> getPcs(Object object) {
		Set<Long> pcs = new TreeSet<Long>();
		pcs.add(((SrcLevelLoopInfo)object).getPc());
		return pcs;
	}
}
