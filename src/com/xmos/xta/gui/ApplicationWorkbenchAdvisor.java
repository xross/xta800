package com.xmos.xta.gui;

import org.eclipse.core.runtime.Platform;
import org.eclipse.ui.application.IWorkbenchConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchAdvisor;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;

public class ApplicationWorkbenchAdvisor extends WorkbenchAdvisor {
	private static final String PERSPECTIVE_ID = "com.xmos.xta.gui.XtaPerspective";
	
	public void initialize(IWorkbenchConfigurer configurer) {
		configurer.setSaveAndRestore(true);
				
		String[] args = Platform.getApplicationArgs();

        // Takes the filename as being the first argument found
        // that doesn't start with a '-'.
        for (String arg : args) {
            if (!arg.matches("-.*")) {
            	GuiEngine.setCommandLineXeFile(arg);
                break;
            }
		}
	}
		
    public WorkbenchWindowAdvisor createWorkbenchWindowAdvisor(IWorkbenchWindowConfigurer configurer) {
        return new ApplicationWorkbenchWindowAdvisor(configurer);
    }

	public String getInitialWindowPerspectiveId() {
		return PERSPECTIVE_ID;
	}
}
