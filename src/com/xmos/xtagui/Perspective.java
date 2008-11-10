package com.xmos.xtagui;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
import org.eclipse.ui.IFolderLayout;

import com.xmos.xtagui.views.ConsoleView;
import com.xmos.xtagui.views.ControlView;
import com.xmos.xtagui.views.ErrorsView;
import com.xmos.xtagui.views.FlowView;
import com.xmos.xtagui.views.FunctionView;
import com.xmos.xtagui.views.LoopView;
import com.xmos.xtagui.views.StructureView;
import com.xmos.xtagui.views.TreeView;

public class Perspective implements IPerspectiveFactory {
    public static final boolean DEBUG = true;

	public void createInitialLayout(IPageLayout layout) {
        if (DEBUG) System.out.printf("Perspective: createInitialLayout\n");        
				
        IFolderLayout viewsFolder = layout.createFolder(
        		"views.folder", IPageLayout.BOTTOM, 0.5f, layout.getEditorArea());        
        viewsFolder.addView(ConsoleView.ID);	
        viewsFolder.addView(ErrorsView.ID);	
        viewsFolder.addView(LoopView.ID);	
        viewsFolder.addView(FunctionView.ID);	
        viewsFolder.addView(StructureView.ID);	
        viewsFolder.addView(FlowView.ID);	
		
        IFolderLayout treeFolder = layout.createFolder(
        		"tree.folder", IPageLayout.LEFT, 0.5f, layout.getEditorArea());        
        treeFolder.addView(ControlView.ID);
        treeFolder.addView(TreeView.ID);

        layout.setEditorAreaVisible(true);
	}
}
