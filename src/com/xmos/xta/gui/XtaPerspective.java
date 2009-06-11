package com.xmos.xta.gui;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
import org.eclipse.ui.IFolderLayout;

import com.xmos.xta.gui.views.console.ConsoleView;
import com.xmos.xta.gui.views.control.ControlView;
import com.xmos.xta.gui.views.info.InfoView;
import com.xmos.xta.gui.views.log.LogView;
import com.xmos.xta.gui.views.tree.TreeView;
import com.xmos.xta.gui.views.routes.RoutesView;

public class XtaPerspective implements IPerspectiveFactory {
	public void createInitialLayout(IPageLayout layout) {
        IFolderLayout viewsFolder = layout.createFolder(
        		"views.folder", IPageLayout.BOTTOM, 0.7f, layout.getEditorArea());

        addView(layout, viewsFolder, ConsoleView.ID);
        addView(layout, viewsFolder, LogView.ID);
        addView(layout, viewsFolder, InfoView.ID);
        
        // TODO
        //addView(layout, viewsFolder, FunctionView.ID);
        //addView(layout, viewsFolder, StructureView.ID);

        IFolderLayout treeFolder = layout.createFolder(
        		"tree.folder", IPageLayout.LEFT, 0.3f, layout.getEditorArea());        
        addView(layout, treeFolder, TreeView.ID);
        addView(layout, treeFolder, RoutesView.ID);
        addView(layout, treeFolder, ControlView.ID);

        layout.setEditorAreaVisible(true);
	}
	
	private void addView(IPageLayout pageLayout, IFolderLayout folderLayout, String viewId) {
		folderLayout.addView(viewId); 
		pageLayout.addShowViewShortcut(viewId);
		pageLayout.getViewLayout(viewId).setCloseable(false);
	}
}
