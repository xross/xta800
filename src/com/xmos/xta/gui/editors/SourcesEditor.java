package com.xmos.xta.gui.editors;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.MultiPageEditorPart;

import com.xmos.xta.engine.Notifier;
import com.xmos.xta.gui.Broadcaster;
import com.xmos.xta.gui.GuiEngine;
import com.xmos.xta.gui.icons.Icons;
import com.xmos.xta.sourcemapper.SourceFile;
import com.xmos.xta.sourcemapper.SourceMapper;

public class SourcesEditor extends MultiPageEditorPart implements IPropertyChangeListener {
    public static final boolean DEBUG = false;
    public static final String ID = "com.xmos.xta.gui.editors.Sources";
    
    private int m_disassemblyIndex;
    
	public SourcesEditor() {
		if (DEBUG) System.out.printf("SourcesEditor()\n");			

		GuiEngine.addBroadcastListener(this);
	}
	
	public void dispose() {
		if (DEBUG) System.out.printf("SourcesEditor: dispose\n");

		GuiEngine.removeBroadcastListener(this);
		super.dispose();
	}

	public void propertyChange(PropertyChangeEvent event) {
		if (DEBUG) System.out.printf("SourcesEditor: propertyChange: %s\n", event.getProperty());	
		
		if (getPageCount() > 0) {
			if (event.getProperty().equals(Broadcaster.BROADCAST_SHOW_CODE_POINT)) {
				setActivePage(m_disassemblyIndex);
				
			} else if (event.getProperty().equals(Broadcaster.BROADCAST_HIGHLIGHTS_UPDATED)) {
				for (int i = 0; i < getPageCount(); i++) {
					XtaEditor editor = (XtaEditor)getEditor(i);
					
					if (editor.hasHighlightedLines()) {
						setPageImage(i, Icons.getIcon("selected"));
					} else {
						setPageImage(i, null);
					}
				}
				
			} else if (event.getProperty().equals(Broadcaster.BROADCAST_SET_FOCUS)) {
				XtaEditor activeEditor = (XtaEditor)getEditor(getActivePage());
				if (activeEditor.hasHighlightedLines()) {
					return;
				}

				for (int i = 0; i < getPageCount(); i++) {
					XtaEditor editor = (XtaEditor)this.getEditor(i);
					if (editor.hasHighlightedLines() && (i != m_disassemblyIndex)) {
						setActivePage(i);
						return;
					}
				}
				setActivePage(m_disassemblyIndex);
				
			} else if (event.getProperty().equals(Notifier.NOTIFY_SET_NODE_CORE)) {
				while (getPageCount() > 0) {
					removePage(0);
				}
				createPages();
			}
		}
	}
	
	public void doSave(IProgressMonitor monitor) {
		if (DEBUG) System.out.printf("SourcesEditor: doSave\n");			
	}

	public void doSaveAs() {
		if (DEBUG) System.out.printf("SourcesEditor: doSaveAs\n");			
	}

	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
		if (DEBUG) System.out.printf("SourcesEditor: init\n");
		
		setSite(site);
		setInput(input);
	}

	public boolean isDirty() {
		if (DEBUG) System.out.printf("SourcesEditor: isDirty\n");			
		return false;
	}

	public boolean isSaveAsAllowed() {
		if (DEBUG) System.out.printf("SourcesEditor: isSaveAsAllowed\n");			
		return false;
	}

	public void setFocus() {
		if (DEBUG) System.out.printf("SourcesEditor: setFocus\n");			
	}

	protected void createPages() {		
		if (DEBUG) System.out.printf("SourcesEditor: createPages\n");	
        try {
        	XtaEditor disassemblyEditor = new XtaEditor(XtaDocumentPartitioner.DISASSEMBLY,
        			"Disassembly");
        	IEditorInput disassemblyEditorInput = new InputDisassembly();
			int index = addPage(disassemblyEditor, disassemblyEditorInput);
			setPageText(index, "Disassembly");
			m_disassemblyIndex = index;
        	
			SourceMapper xeObject = GuiEngine.getEngine().getSourceMapper();
	        for (SourceFile sourceFile : xeObject.getSourceFiles()) {       	        	
	        	XtaEditor sourceEditor = new XtaEditor(XtaDocumentPartitioner.SOURCE, sourceFile.getFileName());
	        	IEditorInput sourceEditorInput = new InputSource(sourceFile);
	    		index = addPage(sourceEditor, sourceEditorInput);
		    	setPageText(index, sourceFile.getShortFileName());
		    	
		    	Composite container = super.getContainer();
		    	if (container instanceof CTabFolder){
		    	    CTabItem tabItem = ((CTabFolder)container).getItem(index);
		    	    tabItem.setToolTipText(sourceFile.getFullFileName());
		    	}
	        }
	        
			String fileName = xeObject.getFunctionFileName("main");
			
			// If single core main doesn't exist, try multi-core main
			if ((fileName == null) || fileName.equals(""))
				fileName = xeObject.getFunctionFileName("__mcmain");

			if ((fileName == null) || fileName.equals("")) {
				setActivePage(0);
			} else {
				for (int i = 0; i < this.getPageCount(); i++) {
					if (fileName.equals(this.getPageText(i))) {
						setActivePage(i);
					}
				}
			}
		} catch (PartInitException e) {
			e.printStackTrace();
		}
	}
}
