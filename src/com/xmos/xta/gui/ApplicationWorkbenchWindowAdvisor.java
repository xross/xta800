package com.xmos.xta.gui;

import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.ToolBarContributionItem;
import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;
import org.eclipse.ui.part.EditorInputTransfer;

import com.xmos.xta.gui.editors.EditorAreaDropAdapter;

public class ApplicationWorkbenchWindowAdvisor extends WorkbenchWindowAdvisor {

    public ApplicationWorkbenchWindowAdvisor(IWorkbenchWindowConfigurer configurer) {
        super(configurer);
    }

    public ActionBarAdvisor createActionBarAdvisor(IActionBarConfigurer configurer) {
        return new ApplicationActionBarAdvisor(configurer);
    }

    public void preWindowOpen() {    	
        IWorkbenchWindowConfigurer configurer = getWindowConfigurer();
        configurer.setInitialSize(new Point(800, 800));
        configurer.setShowCoolBar(true);
        configurer.setShowStatusLine(true);
        configurer.setShowProgressIndicator(true);
        configurer.setTitle("XMOS Timing Analysis");
		configurer.addEditorAreaTransfer(EditorInputTransfer.getInstance());
		configurer.configureEditorAreaDropListener(new EditorAreaDropAdapter());
    }
    
    public void postWindowOpen() {
        super.postWindowOpen();

        IActionBarConfigurer configurer = getWindowConfigurer().getActionBarConfigurer();
        
        // Removes unwanted menu items.
        IMenuManager menuManager = configurer.getMenuManager();        	
        for (IContributionItem contributionItem : menuManager.getItems()) {
        	if (contributionItem.getId().equals("file")) {
        		removeAllSubItems(contributionItem, new String[] {"quit", "open"});

        	} else if (contributionItem.getId().equals("help")) {
        		removeAllSubItems(contributionItem, new String[] {"about"});
        		
        	} else {
            	menuManager.remove(contributionItem.getId());
        	}
        }
                        
        // Removes unwanted tool bar items.
        IContributionItem[] coolItems = configurer.getCoolBarManager().getItems();
        for (int i = 0; i < coolItems.length; i++) {
        	if (coolItems[i] instanceof ToolBarContributionItem) {
        		ToolBarContributionItem toolbarItem = (ToolBarContributionItem) coolItems[i];
        		String id = toolbarItem.getId();
				if (id != null && (
						id.equals("org.eclipse.search.searchActionSet") ||
						id.equals("org.eclipse.debug.ui.launchActionSet") ||
						id.equals("org.eclipse.ui.edit.text.actionSet.annotationNavigation") ||
						id.equals("org.eclipse.ui.edit.text.actionSet.navigation"))) {
        			toolbarItem.getToolBarManager().removeAll();
        		}
        	}
        }

        configurer.getCoolBarManager().update(true);
        configurer.getMenuManager().update(true);
	}
    
    private void removeAllSubItems(IContributionItem contributionItem, String[] exclusions) {
		if (contributionItem instanceof MenuManager) {
			MenuManager subMenuManager = (MenuManager)contributionItem;
	        for (IContributionItem subItem : subMenuManager.getItems()) {
	        	boolean excluded = false;
	        	for (String exclusion : exclusions) {
		        	if (subItem.getId().equals(exclusion))
		        		excluded = true;        		
	        	}
	        	if (!excluded)
	        	    subMenuManager.remove(subItem);	        			        	
	        }
		}    	
    }    
}
