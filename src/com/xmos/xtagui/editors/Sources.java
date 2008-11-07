package com.xmos.xtagui.editors;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.MultiPageEditorPart;

import com.xmos.xeloader.DebugInfo;
import com.xmos.xeloader.MemoryImage;
import com.xmos.xeloader.SymbolTable;
import com.xmos.xeobject.Function;
import com.xmos.xeobject.Line;
import com.xmos.xeobject.SourceFile;
import com.xmos.xeobject.SourceLine;
import com.xmos.xeobject.XeObject;
import com.xmos.xta.engine.Displayer;
import com.xmos.xta.engine.Engine;
import com.xmos.xta.engine.Route;
import com.xmos.xtagui.XtaEngine;
import com.xmos.xtagui.XtaFileFinder;

public class Sources extends MultiPageEditorPart implements Displayer {
    public static final boolean DEBUG = true;
    public static final String ID = "com.xmos.xtagui.editors.Sources";

    private int m_disassemblyIndex;
    
	public Sources() {
		if (DEBUG) System.out.printf("Sources()\n");			
		XtaEngine.getEngine().registerDisplayer(this);
		m_disassemblyIndex = 0;
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
		
		m_disassemblyIndex = CreatePage("Disassembly");
	}

	public void displayRoute(Route route, DebugInfo debugInfo,
			MemoryImage memoryImage, SymbolTable table) {
		if (DEBUG) System.out.printf("Sources: displayRoute\n");			
	}

	public void notifyAnalysePaths() {
		if (DEBUG) System.out.printf("Sources: notifyAnalysePaths\n");			
	}

	public void notifyLoadExecutable() {
		if (DEBUG) System.out.printf("Sources: notifyLoadExecutable\n");	

		Engine engine = XtaEngine.getEngine();
		XeObject xeObject = new XeObject(engine.getXeLoader(), new XtaFileFinder());
		XtaEngine.getEngine().setXeObject(xeObject);                    
		
		StyledText text = (StyledText)getControl(m_disassemblyIndex);		
        for (Function function : xeObject.getFunctions()) {
        	text.append(function.getText() + ":\n");
        	for (Line line : function.getChildern()) {
        		text.append(String.format("  0x%05x %s\n", 
            		line.getLineNumber(), line.getText()));        		
        	}
        	text.append("\n");
        }		
		
        for (SourceFile sourceFile : xeObject.getSourceFiles()) {
        	int pageIndex = CreatePage(sourceFile.getFile().getName());
        	text = (StyledText)getControl(pageIndex);		
        	
            for (SourceLine sourceLine : sourceFile.getSourceLines()){
            	text.append(sourceLine.getText() + "\n");
                for (Line line : sourceLine.getChildern()) {
                	text.append("  - " + line.getText() + "\n");                	
                }
            }
        }	               
	}

	public void notifyUpdateState() {
		if (DEBUG) System.out.printf("Sources: notifyUpdateState\n");			
	}	
		
	private	int CreatePage(String pageName) {
	    int index = addPage(new StyledText(getContainer(), SWT.BORDER));
	    setPageText(index, pageName);
	    return index;
	}
}
