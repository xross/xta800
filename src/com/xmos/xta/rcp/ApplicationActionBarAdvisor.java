package com.xmos.xta.rcp;

import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.ContributionItemFactory;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;

public class ApplicationActionBarAdvisor extends ActionBarAdvisor {
	private MenuManager m_showViewMenuMgr;
	private IContributionItem m_showViewItem;
	
    public ApplicationActionBarAdvisor(IActionBarConfigurer configurer) {
        super(configurer);
    }

    protected void makeActions(IWorkbenchWindow window) {
    	m_showViewMenuMgr = new MenuManager("Show View", "showView");
    	m_showViewItem = ContributionItemFactory.VIEWS_SHORTLIST.create(window);
    }

    protected void fillMenuBar(IMenuManager menuBar) {
    	MenuManager windowMenu = new MenuManager("&Window",
    			IWorkbenchActionConstants.M_WINDOW);
    	m_showViewMenuMgr.add(m_showViewItem);
    	windowMenu.add(m_showViewMenuMgr);
    	menuBar.add(windowMenu);
    }
}
