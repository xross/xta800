package com.xmos.xta.rcp;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
import org.eclipse.ui.IFolderLayout;

import com.xmos.xta.rcp.views.ConsoleView;
import com.xmos.xta.rcp.views.ErrorsView;
import com.xmos.xta.rcp.views.FlowView;
import com.xmos.xta.rcp.views.FunctionView;
import com.xmos.xta.rcp.views.StructureView;
import com.xmos.xta.rcp.views.TreeView;

public class XtaPerspective implements IPerspectiveFactory {
    public static final boolean DEBUG = false;

	public void createInitialLayout(IPageLayout layout) {
        if (DEBUG) System.out.printf("XtaPerspective: createInitialLayout\n");
				
        IFolderLayout viewsFolder = layout.createFolder(
        		"views.folder", IPageLayout.BOTTOM, 0.5f, layout.getEditorArea());        
        viewsFolder.addView(ConsoleView.ID);
        viewsFolder.addView(ErrorsView.ID);
        viewsFolder.addView(FunctionView.ID);
        viewsFolder.addView(StructureView.ID);
        viewsFolder.addView(FlowView.ID);
		
        IFolderLayout treeFolder = layout.createFolder(
        		"tree.folder", IPageLayout.LEFT, 0.5f, layout.getEditorArea());        
        treeFolder.addView(TreeView.ID);

        layout.setEditorAreaVisible(true);
	}
}
