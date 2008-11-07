package com.xmos.xtagui.views;

import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;

import com.xmos.xeobject.Function;
import com.xmos.xeobject.XeObject;
import com.xmos.xta.engine.Engine;
import com.xmos.xtagui.XtaDisplayer;
import com.xmos.xtagui.XtaEngine;
import com.xmos.xtagui.editors.Sources;
import com.xmos.xtagui.editors.SourcesInput;

public class Control extends ViewPart implements IPropertyChangeListener {
    public static final boolean DEBUG = true;
    public static final String ID = "com.xmos.xtagui.views.Control";

    private Combo m_combo;
    
	public Control() {
		if (DEBUG) System.out.printf("Control()\n");			
		XtaDisplayer.getInstance().addPropertyChangeListener(this);
	}

	public void createPartControl(Composite parent) {
		if (DEBUG) System.out.printf("Control: createPartControl\n");	

		Composite composite = new Composite(parent, SWT.NONE);
	    GridLayout gridLayout = new GridLayout();
	    composite.setLayout(gridLayout);
	    
		createFunctionComposite(composite);
	}

	public void setFocus() {
		if (DEBUG) System.out.printf("Control: setFocus\n");			
	}

	public void propertyChange(PropertyChangeEvent event) {
		if (DEBUG) System.out.printf("Control: propertyChange: %s\n", event.getProperty());			
		
		if (event.getProperty().equals("LoadExecutable")) {
			XeObject xeObject = XtaEngine.getEngine().getXeObject();                    
	        for (Function function : xeObject.getFunctions()) {
	        	m_combo.add(function.getText());
	        }			
		}		
	}    		
		
	public void executeLoad() {		
		IWorkbenchPage page = getSite().getWorkbenchWindow().getActivePage();
		try {
			IEditorInput sourcesInput = new SourcesInput();
			page.openEditor(sourcesInput, Sources.ID);
		} catch (PartInitException e) {
			e.printStackTrace();
		}
		
        FileDialog fileDialog = new FileDialog(getSite().getWorkbenchWindow().getShell(), SWT.OPEN);
        fileDialog.setText("Open Xe File");
        String[] filterExt = { "*.xe" };
        fileDialog.setFilterExtensions(filterExt);
        String xeFileName = fileDialog.open();
        
        if (xeFileName != null) {
        	if (DEBUG) {
        		System.out.printf("OpenHandler: File open: %s\n", xeFileName);
        	}
        	Engine engine = XtaEngine.getEngine();
        	boolean success = engine.loadExecutable(xeFileName);
        	if (DEBUG) {
        		if (success) {
                    System.out.printf("OpenHandler: Successfully loaded executable: %s\n", xeFileName);
        		} else {
                    System.out.printf("OpenHandler: Failed to open file: %s\n", xeFileName);
        		}
        	}
        } else {
        	if (DEBUG) {
        		System.out.printf("OpenHandler: File open: null\n");
        	}
        }		
	}

	public void executeRun() {
		boolean fullExecution = false;		
		String functionName = m_combo.getText();
		if (!functionName.equals("")) {
			RunEngineMethodFunc run = new RunEngineMethodFunc(functionName, fullExecution);
			run.start();
		} 				
	}
	
	public String getCurrentFunctionSelection() {
		return m_combo.getText();
	}
		
	private void createFunctionComposite(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
	    GridLayout gridLayout = new GridLayout();
	    gridLayout.numColumns = 2;
	    composite.setLayout(gridLayout);

		Label label = new Label(composite, SWT.NONE);
        label.setText("Functions:");
		m_combo = new Combo(composite, SWT.DROP_DOWN);
	}

    class RunEngineMethodFunc extends Thread {
        private String m_functionName;
        private boolean m_full;

        RunEngineMethodFunc(String functionName, boolean full) {
            m_functionName = functionName;
            m_full = full;
        }

        public void run() {
            if (!XtaEngine.getEngine().analyseRoute(m_functionName, m_full))
                System.err.println(XtaEngine.getEngine().getErrorString());
        }
    }

    class RunEngineMethodRef extends Thread {
        private String m_startRef;
        private String m_endRef;
        private boolean m_full;

        RunEngineMethodRef(String startRef, String endRef, boolean full) {
            m_startRef = startRef;
            m_endRef = endRef;
            m_full = full;
        }

        public void run() {        	
            if (!XtaEngine.getEngine().analyseRoute(m_startRef, m_endRef, m_full))
                System.err.println(XtaEngine.getEngine().getErrorString());
        }
    }
}
