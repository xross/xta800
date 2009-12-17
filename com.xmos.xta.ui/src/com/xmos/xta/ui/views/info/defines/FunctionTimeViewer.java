package com.xmos.xta.ui.views.info.defines;

import java.util.Set;
import java.util.TreeSet;

import org.eclipse.jface.viewers.TableViewer;

import com.xmos.xta.core.engine.Engine;
import com.xmos.xta.ui.views.info.BaseViewer;
import com.xmos.xta.ui.views.info.BaseViewerContentProvider;
import com.xmos.xta.ui.views.info.BaseViewerLabelProvider;

public class FunctionTimeViewer extends BaseViewer {
	public FunctionTimeViewer(TableViewer viewer) {
		super(viewer);
	}

	protected BaseViewerContentProvider createContentProvider() {
		return new FunctionTimeContentProvider();
	}
	
	protected BaseViewerLabelProvider createLabelProvider() {
		return new LabelProvider();
	}

	protected Set<Long> getPcs(Object object) {
		return new TreeSet<Long>();
	}
}

class FunctionTimeContentProvider extends BaseViewerContentProvider {
	public Object[] getElements(Object inputElement) {
		return ((Engine)inputElement).getFunctionTimeDefines().toArray();
	}
}
