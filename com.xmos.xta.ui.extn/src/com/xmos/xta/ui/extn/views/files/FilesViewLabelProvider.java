package com.xmos.xta.ui.extn.views.files;

import java.io.File;

import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;

import com.xmos.libs.dwarfhandler.DebugFile;
import com.xmos.xta.ui.icons.Icons;

public class FilesViewLabelProvider extends CellLabelProvider implements ILabelProvider {

	public void update(ViewerCell cell) {
		DebugFile debugFile = (DebugFile)cell.getElement();
		cell.setText(getText(debugFile));

		String iconName = "file";
		if (debugFile.getFile() == null) {
			iconName += "_notfound";
		} else if (editorOpen(debugFile)) {
			iconName += "_open";
		}
		cell.setImage(Icons.getIcon(iconName));
	}

	private boolean editorOpen(DebugFile debugFile) {
		File file = new File(debugFile.getFullFileName());
		String fileName = file.getName();
		
		final IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		if (page == null) {
			return false;
		}
		IEditorReference[] editorReferences = page.getEditorReferences();
		for (IEditorReference editorRef : editorReferences) {
			if (editorRef.getPartName().equals(fileName)) {
				return true;
			}
		}
		return false;
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
		if (debugFile.getFile() != null) {
			return debugFile.getShortFileName();
		} else {
			return fileName + " (File not found)";
		}
	}
}
