package com.xmos.xtagui.views;

import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;

import com.xmos.xeobject.Function;
import com.xmos.xeobject.XeObject;
import com.xmos.xta.engine.Engine;
import com.xmos.xtagui.XtaDisplayer;
import com.xmos.xtagui.XtaEngine;
import com.xmos.xtagui.XtaFileFinder;
import com.xmos.xtagui.editors.Sources;
import com.xmos.xtagui.editors.SourcesInput;

public class ControlView extends ViewPart implements IPropertyChangeListener {
    public static final boolean DEBUG = true;
    public static final String ID = "com.xmos.xtagui.views.Control";

    private Combo m_combo;
    private Button m_exhustive;
    private Button m_fnops;
    private Spinner m_frequency;
    private Spinner m_numThreads;
    
	public ControlView() {
		if (DEBUG) System.out.printf("Control()\n");			
		XtaDisplayer.getInstance().addPropertyChangeListener(this);
	}

	public void createPartControl(Composite parent) {
		if (DEBUG) System.out.printf("Control: createPartControl\n");	

		Composite composite = new Composite(parent, SWT.NONE);
	    GridLayout gridLayout = new GridLayout();
	    gridLayout.numColumns = 2;
	    composite.setLayout(gridLayout);
	    
		createFunctionControl(composite);
		createExhustiveControl(composite);
		createFNopsControl(composite);
		createFrequencyControl(composite);
		createNumThreadsControl(composite);
	}

	public void setFocus() {
		if (DEBUG) System.out.printf("Control: setFocus\n");			
	}

	public void propertyChange(PropertyChangeEvent event) {
		if (DEBUG) System.out.printf("Control: propertyChange: %s\n", event.getProperty());			
		
		if (event.getProperty().equals("LoadExecutable")) {
			createXeObject();
            populateFunctionList();			
	        openEditors();
		}		
	}    		
		
	public void executeLoad() {				
        FileDialog fileDialog = new FileDialog(getSite().getWorkbenchWindow().getShell(), SWT.OPEN);
        fileDialog.setText("Open Xe File");
        String[] filterExt = { "*.xe" };
        fileDialog.setFilterExtensions(filterExt);
        String xeFileName = fileDialog.open();
        
        if (xeFileName != null) {
        	if (DEBUG) {
        		System.out.printf("Control: File open: %s\n", xeFileName);
        	}
        	Engine engine = XtaEngine.getEngine();
        	boolean success = engine.loadExecutable(xeFileName);
        	if (DEBUG) {
        		if (success) {
                    System.out.printf("Control: Successfully loaded executable: %s\n", xeFileName);
        		} else {
                    System.out.printf("Control: Failed to open file: %s\n", xeFileName);
        		}
        	}
        } else {
        	if (DEBUG) {
        		System.out.printf("Control: File open: null\n");
        	}
        }	                
	}

	public void executeRun() {
		Views.showView(TreeView.ID);
		
		boolean fullExecution = false;		
		String functionName = m_combo.getText();
		if (!functionName.equals("")) {
			RunEngineMethodFunc run = new RunEngineMethodFunc(functionName, fullExecution);
			run.start();
		} else {
			XtaDisplayer.getInstance().broadcastPropertyChange(
				new PropertyChangeEvent(this, "Error", null, "No function/path specified"));
		}
	}
	
	public String getFunctionSelection() {
		return m_combo.getText();
	}

	public boolean getExhustiveSelection() {
		return m_exhustive.getSelection();
	}

	public boolean getFnopsSelection() {
		return m_fnops.getSelection();
	}

	public int getFrequencySelection() {
		return m_frequency.getSelection();
	}

	public int getNumThreadsSelection() {
		return m_numThreads.getSelection();
	}
	
	private void createControlLabel(Composite parent, String str) {
		Label label = new Label(parent, SWT.NONE);		        
        label.setText(str);
	}
	
	private void createFunctionControl(Composite parent) {
		createControlLabel(parent, "Functions:");
		m_combo = new Combo(parent, SWT.DROP_DOWN);
	}

	private void createExhustiveControl(Composite parent) {
		createControlLabel(parent, "Exhustive:");
        m_exhustive = new Button(parent, SWT.CHECK);
	}	    
		
    private void createFNopsControl(Composite parent) {
		createControlLabel(parent, "Show Fnops:");
        m_fnops = new Button(parent, SWT.CHECK);
    }

    private void createFrequencyControl(Composite parent) {
		createControlLabel(parent, "Frequency:");
        m_frequency = new Spinner(parent, SWT.BORDER);
        m_frequency.setMinimum(0);
        m_frequency.setMaximum(1000);
        m_frequency.setSelection(400);
        m_frequency.setIncrement(1);
        m_frequency.setPageIncrement(10);
    }

    private void createNumThreadsControl(Composite parent) {
		createControlLabel(parent, "Num Threads:");
        m_numThreads = new Spinner(parent, SWT.BORDER);
        m_numThreads.setMinimum(0);
        m_numThreads.setMaximum(8);
        m_numThreads.setSelection(8);
        m_numThreads.setIncrement(1);
        m_numThreads.setPageIncrement(8);
    }
    
    private void createXeObject() {
		Engine engine = XtaEngine.getEngine();
		XeObject xeObject = new XeObject(engine.getXeLoader(), new XtaFileFinder());
		XtaEngine.getEngine().setXeObject(xeObject);                    
    }
    
    private void populateFunctionList() {
    	XeObject xeObject = XtaEngine.getEngine().getXeObject();                    
        for (Function function : xeObject.getFunctions()) {
        	m_combo.add(function.getText());
        }
    }
    
    private void openEditors() {
		IWorkbenchPage page = getSite().getWorkbenchWindow().getActivePage();
		try {
			IEditorInput sourcesInput = new SourcesInput();
			page.openEditor(sourcesInput, Sources.ID);
		} catch (PartInitException e) {
			e.printStackTrace();
		}    	
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
