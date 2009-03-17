package com.xmos.xta.rcp;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
import org.eclipse.ui.IFolderLayout;

import com.xmos.xta.rcp.views.ConsoleView.ConsoleView;
import com.xmos.xta.rcp.views.ControlView.ControlView;
import com.xmos.xta.rcp.views.FunctionView.FunctionView;
import com.xmos.xta.rcp.views.LogView.LogView;
import com.xmos.xta.rcp.views.StatusView.StatusView;
import com.xmos.xta.rcp.views.StructureView.StructureView;
import com.xmos.xta.rcp.views.TreeView.TreeView;
import com.xmos.xta.rcp.views.UnknownsView.UnknownsView;

public class XtaPerspective implements IPerspectiveFactory {
    public static final boolean DEBUG = false;

	public void createInitialLayout(IPageLayout layout) {
        if (DEBUG) System.out.printf("XtaPerspective: createInitialLayout\n");

        IFolderLayout viewsFolder = layout.createFolder(
        		"views.folder", IPageLayout.BOTTOM, 0.8f, layout.getEditorArea());
        
        addView(layout, viewsFolder, ConsoleView.ID);
        addView(layout, viewsFolder, LogView.ID);
        addView(layout, viewsFolder, UnknownsView.ID);
        addView(layout, viewsFolder, FunctionView.ID);
        addView(layout, viewsFolder, StructureView.ID);
				
        IFolderLayout treeFolder = layout.createFolder(
        		"tree.folder", IPageLayout.LEFT, 0.3f, layout.getEditorArea());        
        addView(layout, treeFolder, TreeView.ID);
        addView(layout, treeFolder, StatusView.ID);
        addView(layout, treeFolder, ControlView.ID);

        layout.setEditorAreaVisible(true);
	}
	
	private void addView(IPageLayout pageLayout, IFolderLayout folderLayout, String viewId) {
		folderLayout.addView(viewId); 
		pageLayout.addShowViewShortcut(viewId);
		pageLayout.getViewLayout(viewId).setCloseable(false);
	}
}
