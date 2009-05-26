package com.xmos.xta.gui.actions;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import com.xmos.xta.engine.Engine;
import com.xmos.xta.engine.Notifier;
import com.xmos.xta.gui.XtaPlugin;
import com.xmos.xta.gui.GuiEngine;
import com.xmos.xta.gui.GuiFileFinder;
import com.xmos.xta.gui.editors.SourcesEditor;
import com.xmos.xta.gui.editors.SourcesEditorInput;
import com.xmos.xta.gui.icons.Icons;
import com.xmos.xta.gui.views.console.ConsoleView;

public class ActionLoad extends Action implements IPropertyChangeListener {

	public ActionLoad() {
		GuiEngine.addBroadcastListener(this);
		setImageDescriptor(Icons.getIconDescriptor("load"));
		setToolTipText("Open Xe File");
	}

	public void propertyChange(PropertyChangeEvent event) {
		if (event.getProperty().equals(Notifier.NOTIFY_LOAD_EXECUTABLE)) {			
	        openEditors();
	        runSrcLevelCommands();
		} 
	}
	
	public void run() {
		GuiEngine.broadcast(Notifier.NOTIFY_SET_GUI_COMMAND, "");

		FileDialog fileDialog = new FileDialog(Display.getCurrent().getActiveShell(), SWT.OPEN);
        fileDialog.setText("Open Xe File");
        String[] filterExt = { "*.xe" };
        fileDialog.setFilterExtensions(filterExt);
        
        XtaPlugin plugin = XtaPlugin.getDefault();        
        String lastDir = plugin.getPreferenceStore().getString("LastDir");
        
        fileDialog.setFilterPath(lastDir);        
        String xeFileName = fileDialog.open();
        
        if (xeFileName == null)
        	return;
        
        String path = new File(xeFileName).getParent();
        plugin.getPreferenceStore().setValue("LastDir", path);

        if (xeFileName != null) {
        	File xeFile = new File(xeFileName);
        	
        	try {
				xeFile = xeFile.getCanonicalFile();
			} catch (IOException e) {
				GuiEngine.broadcast(Notifier.NOTIFY_ERROR, 
						String.format("Cannot resolve file: %s", xeFileName));
				return;
			}
			
			final File file = xeFile;
			Job job = new Job("Load: " + xeFileName) {
				protected IStatus run(IProgressMonitor monitor) {
					Engine engine = GuiEngine.getEngine();
					engine.setMonitor(monitor);
					boolean success = engine.loadExecutable(file, new GuiFileFinder());					
		        	if (success) {
		                setToolTipText("Currently loaded " + file.getAbsolutePath());
		        	}		        	
					return Status.OK_STATUS;
				}
			};
			job.schedule();        	        	
        }
	}
	
    private void openEditors() {
		final IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		page.closeAllEditors(false);
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
    	
    	Collection<String> srcLevelCommands = GuiEngine.getEngine().getSrcLevelCommands();    	
    	if (srcLevelCommands.size() > 0) {    	    
			try {
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().
				    getActivePage().showView(ConsoleView.ID);
			} catch (PartInitException e) {}    	    

        	for (String command : GuiEngine.getEngine().getSrcLevelCommands()) {    	    				
	    	    console.runCommand(command);
    	    }
    	}    	
    }    
}
