package com.xmos.xta.ui.views.info.defines;

import java.util.Set;
import java.util.TreeSet;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;

import com.xmos.xta.core.engine.Engine;
import com.xmos.xta.core.engine.defines.LoopInfo;
import com.xmos.xta.ui.GuiEngine;
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

	protected Set<Long> getPcs(Object object) {
		return new TreeSet<Long>();
	}

 	@Override
	protected void removeSelection() {
		ISelection selection = getViewer().getSelection(); 
		for (Object object : ((IStructuredSelection)selection).toList()) {
			LoopInfo info = (LoopInfo)object;
			GuiEngine.getEngine().removeLoop(GuiEngine.s_guiSrc,
					info.getReference().getRef());
		}
	}
}

class LoopTimeContentProvider extends BaseViewerContentProvider {
	public Object[] getElements(Object inputElement) {
		return ((Engine)inputElement).getLoopDefines().toArray();
	}
}
