package com.xmos.xta.rcp.editors;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.MultiPageEditorPart;

import com.xmos.xeobject.SourceFile;
import com.xmos.xeobject.XeObject;
import com.xmos.xta.engine.Notifier;
import com.xmos.xta.rcp.XtaNotifier;
import com.xmos.xta.rcp.XtaEngine;
import com.xmos.xta.rcp.icons.Icons;

public class SourcesEditor extends MultiPageEditorPart implements IPropertyChangeListener {
    public static final boolean DEBUG = false;
    public static final String ID = "com.xmos.xta.rcp.editors.Sources";
    
    private int m_disassemblyIndex;
    
	public SourcesEditor() {
		if (DEBUG) System.out.printf("Sources()\n");			

		XtaNotifier.getInstance().addPropertyChangeListener(this);
	}

	public void propertyChange(PropertyChangeEvent event) {
		if (DEBUG) System.out.printf("SourcesEditor: propertyChange: %s\n", event.getProperty());	
		
		if (getPageCount() > 0) {
			if (event.getProperty().equals(Notifier.NOTIFY_SHOW_CODE_POINT)) {
				setActivePage(m_disassemblyIndex);
			} else if (event.getProperty().equals(Notifier.NOTIFY_UPDATE_HIGHLIGHTS)) {
				for (int i = 0; i < getPageCount(); i++) {
					XtaEditor editor = (XtaEditor)this.getEditor(i);
					
					if (editor.numHighlights() > 0) {
						setPageImage(i, Icons.getIcon("selected"));
					} else {
						setPageImage(i, null);
					}
				}
			}
		}
	}
	
	public void doSave(IProgressMonitor monitor) {
		if (DEBUG) System.out.printf("Sources: doSave\n");			
	}

	public void doSaveAs() {
		if (DEBUG) System.out.printf("Sources: doSaveAs\n");			
	}

	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {
		if (DEBUG) System.out.printf("Sources: init\n");
		
		setSite(site);
		setInput(input);
	}

	public boolean isDirty() {
		if (DEBUG) System.out.printf("Sources: isDirty\n");			
		return false;
	}

	public boolean isSaveAsAllowed() {
		if (DEBUG) System.out.printf("Sources: isSaveAsAllowed\n");			
		return false;
	}

	public void setFocus() {
		if (DEBUG) System.out.printf("Sources: setFocus\n");			
	}

	protected void createPages() {		
		if (DEBUG) System.out.printf("Sources: createPages\n");	
        try {
        	XtaEditor disassemblyEditor = new XtaEditor(XtaDocumentPartitioner.DISASSEMBLY,
        			"Disassembly");
        	IEditorInput disassemblyEditorInput = new InputDisassembly();
			int index = addPage(disassemblyEditor, disassemblyEditorInput);
			setPageText(index, "Disassembly");
			m_disassemblyIndex = index;
        	
			XeObject xeObject = XtaEngine.getEngine().getXeObject();
	        for (SourceFile sourceFile : xeObject.getSourceFiles()) {       	        	
	        	XtaEditor sourceEditor = new XtaEditor(XtaDocumentPartitioner.SOURCE,
	        			sourceFile.getFile().getName());
	        	IEditorInput sourceEditorInput = new InputSource(sourceFile);
	    		index = addPage(sourceEditor, sourceEditorInput);
		    	setPageText(index, sourceFile.getFile().getName());
	        }					
		} catch (PartInitException e) {
			e.printStackTrace();
		}       
	}
}
