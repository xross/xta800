package com.xmos.xta.ui.extn.views.files;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import com.xmos.libs.dwarfhandler.DebugFile;
import com.xmos.libs.xeloader.DebugInfo;
import com.xmos.xta.core.engine.Engine;
import com.xmos.xta.ui.GuiEngine;
import com.xmos.xta.ui.editors.XtaDisassemblyEditor;

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

	    DebugInfo debugInfo = engine.getXeLoader().getDebugInfo(engine.getNodeId(), engine.getCoreId());
	    Collection<DebugFile> files = new ArrayList<DebugFile>(debugInfo.getDebugEngine().getDebugFiles());
	    files.add(new DebugFile("", XtaDisassemblyEditor.EDITORNAME, XtaDisassemblyEditor.EDITORNAME));
	    return files.toArray();
	}

	public void dispose() {
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	}
}
