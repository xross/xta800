package com.xmos.xta.ui.extn.views.files;

import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ViewerCell;

import com.xmos.libs.dwarfhandler.DebugFile;

public class FilesViewLabelProvider extends CellLabelProvider {

	public void update(ViewerCell cell) {
		DebugFile debugFile = (DebugFile)cell.getElement();
		if (debugFile.getFile() != null)
			cell.setText(debugFile.getShortFileName());
		else
			cell.setText(debugFile.getFileName());
	}

	public String getToolTipText(Object element) {
		return ((DebugFile)element).getFullFileName();
	}

}
