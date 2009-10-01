package com.xmos.xta.ui;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

import com.xmos.xta.ui.views.console.ConsoleView;
import com.xmos.xta.ui.views.function.FunctionView;
import com.xmos.xta.ui.views.info.InfoView;
import com.xmos.xta.ui.views.routes.RoutesView;
import com.xmos.xta.ui.views.structure.StructureView;
import com.xmos.xta.ui.views.timedistribution.TimeDistributionView;

public class XtaPerspective implements IPerspectiveFactory {
	public static final String ID = "com.xmos.xta.ui.XtaPerspective";
	
	public void createInitialLayout(IPageLayout layout) {
        IFolderLayout viewsFolder = layout.createFolder(
        		"views.folder", IPageLayout.BOTTOM, 0.7f, layout.getEditorArea());

        addView(layout, viewsFolder, ConsoleView.ID);
        addView(layout, viewsFolder, InfoView.ID);
        addView(layout, viewsFolder, FunctionView.ID);
        addView(layout, viewsFolder, StructureView.ID);
        addView(layout, viewsFolder, TimeDistributionView.ID);

        IFolderLayout treeFolder = layout.createFolder(
        		"tree.folder", IPageLayout.LEFT, 0.3f, layout.getEditorArea());        
        addView(layout, treeFolder, RoutesView.ID);

        layout.setEditorAreaVisible(true);
	}
	
	private void addView(IPageLayout pageLayout, IFolderLayout folderLayout, String viewId) {
		folderLayout.addView(viewId); 
		pageLayout.addShowViewShortcut(viewId);
	}
}
