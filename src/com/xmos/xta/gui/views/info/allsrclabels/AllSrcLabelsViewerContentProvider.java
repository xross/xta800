package com.xmos.xta.gui.views.info.allsrclabels;

import java.util.Collection;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

import com.xmos.xta.engine.Engine;
import com.xmos.xta.engine.sections.SrcLabelTableInfo;

public class AllSrcLabelsViewerContentProvider implements IStructuredContentProvider {
	public Object[] getElements(Object inputElement) {
	    Collection<SrcLabelTableInfo> infos = ((Engine)inputElement).getSrcLabelTable().values();
		return infos.toArray();
	}

	public void dispose() {
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	}
}
