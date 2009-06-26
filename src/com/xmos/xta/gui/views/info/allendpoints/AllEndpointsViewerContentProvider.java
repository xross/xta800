package com.xmos.xta.gui.views.info.allendpoints;

import java.util.Collection;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

import com.xmos.xta.engine.Engine;
import com.xmos.xta.engine.sections.CodePointTableInfo;

public class AllEndpointsViewerContentProvider implements IStructuredContentProvider {
	public Object[] getElements(Object inputElement) {
	    Collection<CodePointTableInfo> infos = ((Engine)inputElement).getEndpointTable().values();
		return infos.toArray();
	}

	public void dispose() {
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	}
}
