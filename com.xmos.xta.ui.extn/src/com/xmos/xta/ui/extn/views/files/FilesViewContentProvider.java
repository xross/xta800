package com.xmos.xta.ui.extn.views.files;

import java.io.File;
import java.util.Collections;
import java.util.LinkedList;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import com.xmos.libs.dwarfhandler.DebugFile;
import com.xmos.libs.xeloader.DebugInfo;
import com.xmos.libs.xeloader.XeLoader;
import com.xmos.xta.core.engine.Engine;
import com.xmos.xta.ui.GuiEngine;

public class FilesViewContentProvider implements ITreeContentProvider  {
	private static Object[] EMPTY_ARRAY = new Object[]{};

	public Object[] getChildren(Object parentElement) {
		return EMPTY_ARRAY;
	}

	public Object getParent(Object element) {
		return null;
	}

	public boolean hasChildren(Object element) {
		return false;
	}

	public Object[] getElements(Object inputElement) {
		Engine engine = GuiEngine.getEngine();

		File executable = engine.getExecutable();
    	if (executable == null) {
	        return EMPTY_ARRAY;
    	}

	    XeLoader xeLoader = engine.getXeLoader();
	    int nodeNumber = engine.getNodeNumber();
		int coreNumber = engine.getCoreNumber();
		if (!xeLoader.isLoaded(nodeNumber, coreNumber)) {
			return new LinkedList<DebugFile>().toArray();
		}
		
		DebugInfo debugInfo = xeLoader.getDebugInfo(nodeNumber, coreNumber);
	    LinkedList<DebugFile> files = new LinkedList<DebugFile>(debugInfo.getDebugEngine().getDebugFiles());
	    Collections.sort(files);
	    return files.toArray();
	}

	public void dispose() {
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	}
}
