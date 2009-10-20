package com.xmos.xta.ui;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IPerspectiveFactory;
import org.eclipse.ui.IPerspectiveListener;
import org.eclipse.ui.IStartup;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

public class GuiPerspectiveMonitor implements  IStartup, IPerspectiveFactory, IPerspectiveListener {

	public void earlyStartup() {
		final GuiPerspectiveMonitor guiPerspectiveMonitor = this;
		PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {
    		public void run() {
    			IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
    			window.addPerspectiveListener(guiPerspectiveMonitor);
		    }
    	});		
	}
	
	public void createInitialLayout(IPageLayout layout) {
	}

	public void perspectiveActivated(IWorkbenchPage page, IPerspectiveDescriptor perspective) {
	    GuiEngine.broadcast(Broadcaster.BROADCAST_PERSPECTIVE_CHANGED, "");
	}

	public void perspectiveChanged(IWorkbenchPage page, IPerspectiveDescriptor perspective, String changeId) {
	}
}

