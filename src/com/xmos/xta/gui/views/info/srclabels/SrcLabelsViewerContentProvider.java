package com.xmos.xta.gui.views.info.srclabels;

import java.util.Collection;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

import com.xmos.xta.engine.Engine;
import com.xmos.xta.engine.sections.CodePointInfo;

public class SrcLabelsViewerContentProvider implements IStructuredContentProvider {
	public Object[] getElements(Object inputElement) {
	    Collection<CodePointInfo> infos = ((Engine)inputElement).getSrcLabels().values();
		return infos.toArray();
	}

	public void dispose() {
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	}
}
