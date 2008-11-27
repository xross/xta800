package com.xmos.xta.rcp;

import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.ContributionItemFactory;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;

public class ApplicationActionBarAdvisor extends ActionBarAdvisor {
	private MenuManager showViewMenuMgr;
	private IContributionItem showViewItem;
	//private StatusLineContributionItem m_statusItem;
	
    public ApplicationActionBarAdvisor(IActionBarConfigurer configurer) {
        super(configurer);
    }

    protected void makeActions(IWorkbenchWindow window) {
    	showViewMenuMgr = new MenuManager("Show View", "showView");
    	showViewItem = ContributionItemFactory.VIEWS_SHORTLIST.create(window);
    }

    protected void fillMenuBar(IMenuManager menuBar) {
    	MenuManager windowMenu = new MenuManager("&Window",
    			IWorkbenchActionConstants.M_WINDOW);
    	showViewMenuMgr.add(showViewItem);
    	windowMenu.add(showViewMenuMgr);
    	menuBar.add(windowMenu);
    }
    
    protected void fillStatusLine(IStatusLineManager statusLine) {
    	//m_statusItem = new StatusLineContributionItem("LoggedInStatus");
    	//m_statusItem.setText("Logged in");
        //statusLine.add(m_statusItem);
    	//statusLine.setMessage("Hello there from me");
    }    
}
