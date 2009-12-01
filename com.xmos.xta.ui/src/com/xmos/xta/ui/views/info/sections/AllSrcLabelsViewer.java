package com.xmos.xta.ui.views.info.sections;

import java.util.Set;
import java.util.TreeSet;

import org.eclipse.jface.viewers.TableViewer;

import com.xmos.xta.core.engine.sections.SrcLabelTableInfo;
import com.xmos.xta.ui.views.info.BaseViewer;
import com.xmos.xta.ui.views.info.BaseViewerContentProvider;
import com.xmos.xta.ui.views.info.BaseViewerLabelProvider;

public class AllSrcLabelsViewer extends BaseViewer {
	public AllSrcLabelsViewer(TableViewer viewer) {
		super(viewer);
	}

	protected BaseViewerContentProvider createContentProvider() {
		return new AllSrcLabelsViewerContentProvider();
	}
	
	protected BaseViewerLabelProvider createLabelProvider() {
		return new AllSrcLabelsViewerLabelProvider();
	}

	@Override
	protected Set<Long> getPcs(Object object) {
		Set<Long> pcs = new TreeSet<Long>();
		pcs.add(((SrcLabelTableInfo)object).getPc());
		return pcs;
	}
}
