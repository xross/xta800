package com.xmos.xta.ui.extn.views.files;

import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.graphics.Image;

import com.xmos.libs.dwarfhandler.DebugFile;
import com.xmos.xta.ui.editors.XtaDisassemblyEditor;

public class FilesViewLabelProvider extends CellLabelProvider implements ILabelProvider {

	public void update(ViewerCell cell) {
		cell.setText(getText((DebugFile)cell.getElement()));
	}

	public String getToolTipText(Object element) {
		return ((DebugFile)element).getFullFileName();
	}

	public Image getImage(Object element) {
		return null;
	}

	public String getText(Object element) {
		return getText((DebugFile)element);
	}

	public String getText(DebugFile element) {
		DebugFile debugFile = (DebugFile)element;
		
		String fileName = debugFile.getFileName();
		if (fileName.equals(XtaDisassemblyEditor.EDITORNAME))
			return fileName;
		
		if (debugFile.getFile() != null)
			return debugFile.getShortFileName();
		else
			return fileName + " (File not found)"; 	    		
	}
}
