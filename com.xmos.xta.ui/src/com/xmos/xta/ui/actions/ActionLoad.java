package com.xmos.xta.ui.actions;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowPulldownDelegate;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionDelegate;

import com.xmos.xta.core.engine.Engine;
import com.xmos.xta.core.engine.Notifier;
import com.xmos.xta.core.engine.sections.SrcStringInfo;
import com.xmos.xta.ui.GuiProgressMonitor;
import com.xmos.xta.ui.XtaPlugin;
import com.xmos.xta.ui.GuiEngine;
import com.xmos.xta.ui.GuiFileFinder;
import com.xmos.xta.ui.editors.SourcesEditor;
import com.xmos.xta.ui.editors.SourcesEditorInput;
import com.xmos.xta.ui.views.console.ConsoleView;

public class ActionLoad extends ActionDelegate implements IPropertyChangeListener, 
										IWorkbenchWindowPulldownDelegate {
	private IAction m_action;
	private SelectionListener m_listListener;
	
	public ActionLoad() {
		GuiEngine.addBroadcastListener(this);
		m_listListener = new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
				assert(false);
			}

			public void widgetSelected(SelectionEvent e) {
				String selectedFile = ((MenuItem)e.getSource()).getText();
				loadFile(selectedFile);
			}
		};
	}

	public void init(IAction action) {
		m_action = action;
		
		String xeFileName = GuiEngine.getCommandLineXeFile();
		if (xeFileName != null) {
			loadFile(xeFileName);
		}
	}

	public void init(IWorkbenchWindow window) {
	}

	public void run(IAction action) {
		m_action = action;
		
		FileDialog fileDialog = new FileDialog(Display.getCurrent().getActiveShell(), SWT.OPEN);
        fileDialog.setText("Open Xe File");
        String[] filterExt = { "*.xe", "*.*" };
        fileDialog.setFilterExtensions(filterExt);
        
        XtaPlugin plugin = XtaPlugin.getDefault();        
        String lastDir = plugin.getPreferenceStore().getString("LastDir");
        
        fileDialog.setFilterPath(lastDir);        
        String xeFileName = fileDialog.open();
        
        if (xeFileName == null)
        	return;
                
        String path = new File(xeFileName).getParent();
        plugin.getPreferenceStore().setValue("LastDir", path);
        loadFile(xeFileName);
	}
	
	public void loadFile(String xeFileName) {
       	final File xeFile = new File(xeFileName);
        Job job = new Job("Load: " + xeFileName) {
			protected IStatus run(IProgressMonitor monitor) {
				Engine engine = GuiEngine.getEngine();
				engine.setMonitor(new GuiProgressMonitor(monitor));
				boolean success = engine.loadExecutable(GuiEngine.s_guiCommandSrc, xeFile, new GuiFileFinder());					
				engine.setMonitor(null);
	        	if (success && (m_action != null)) {	        		
	        		m_action.setToolTipText("Currently loaded " + xeFile.getAbsolutePath());
	        	} 	        		        	
	        	
	        	XtaPlugin plugin = XtaPlugin.getDefault(); 
	        	String fileHistory = plugin.getPreferenceStore().getString("FileHistory");
	        	String[] files = fileHistory.split(";");
	        	List<String> fileList = new LinkedList<String>();
	        	for(String file : files){
	        		fileList.add(file);
	        	}
	        		        	
	        	fileList.remove(xeFile.toString());
	        	if(success)
	        		fileList.add(xeFile.toString());
	        	StringBuffer sb = new StringBuffer();
	        	int count = 0;
	        	for(String file : fileList){
	        		sb.append(file +";");
	        		count++;
	        		if(count > 10)
	        			break;
	        	}
	        	
	        	plugin.getPreferenceStore().setValue("FileHistory", sb.toString()  );
				return Status.OK_STATUS;
			}
		};
		job.setUser(true);
		job.schedule();		        		
	}
	
	public void propertyChange(PropertyChangeEvent event) {
		if (event.getProperty().equals(Notifier.NOTIFY_LOAD_EXECUTABLE)) {			
	        openEditors();
	        runSrcLevelCommands();
		}
	}

    private void openEditors() {
		final IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		
		// Need to close any xta editors already open..
		for (IEditorReference editorReference : page.getEditorReferences()) {
			if (editorReference.getId().equals(SourcesEditor.ID)) {
				page.closeEditor(editorReference.getEditor(false), false);
			}
		}
		
		// Opens a new xta editor.. 
		IEditorInput sourcesInput = new SourcesEditorInput();
		try {
		    page.openEditor(sourcesInput, SourcesEditor.ID);
		} catch (PartInitException e) {
			e.printStackTrace();
		}
    }
    
    private void runSrcLevelCommands() {  
	    ConsoleView console = (ConsoleView)PlatformUI.getWorkbench().
            getActiveWorkbenchWindow().getActivePage().findView(ConsoleView.ID);
    	
    	Collection<SrcStringInfo> srcLevelCommands = GuiEngine.getEngine().getSrcLevelCommands();    	
    	if (srcLevelCommands.size() > 0) {    	    
			try {
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().
				    getActivePage().showView(ConsoleView.ID);
			} catch (PartInitException e) {}    	    

        	for (SrcStringInfo command : srcLevelCommands) {    	    				
	    	    console.runCommand(command.getString());
    	    }
    	}    	
    }

	public Menu getMenu(Control parent) {
		Menu menu = new Menu(parent);
		
		XtaPlugin plugin = XtaPlugin.getDefault();        
     	String fileHistory = plugin.getPreferenceStore().getString("FileHistory");
     	
     	String[] files = fileHistory.split(";");
     	
     	List<String> fileStrings = new ArrayList<String>();
     	
     	for(String file : files){
     		fileStrings.add(file);
     	}
     	
     	Collections.reverse(fileStrings);
     	
    	for(String file : fileStrings){
    		if(file.length() > 0 ){
	        	MenuItem item = new MenuItem(menu, SWT.NONE);
	    		item.setText(file);
	    		item.addSelectionListener(m_listListener);
    		}
        }
		
		return menu;
	}
}
