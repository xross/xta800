package com.xmos.xtagui;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
import org.eclipse.ui.IFolderLayout;

import com.xmos.xtagui.views.Console;
import com.xmos.xtagui.views.Control;
import com.xmos.xtagui.views.Errors;
import com.xmos.xtagui.views.Flow;
import com.xmos.xtagui.views.Function;
import com.xmos.xtagui.views.Loop;
import com.xmos.xtagui.views.Structure;
import com.xmos.xtagui.views.Tree;

public class Perspective implements IPerspectiveFactory {
    public static final boolean DEBUG = true;

	public void createInitialLayout(IPageLayout layout) {
        if (DEBUG) System.out.printf("Perspective: createInitialLayout\n");        
				
        IFolderLayout viewsFolder = layout.createFolder(
        		"views.folder", IPageLayout.BOTTOM, 0.5f, layout.getEditorArea());        
        viewsFolder.addView(Console.ID);	
        viewsFolder.addView(Errors.ID);	
        viewsFolder.addView(Loop.ID);	
        viewsFolder.addView(Function.ID);	
        viewsFolder.addView(Structure.ID);	
        viewsFolder.addView(Flow.ID);	
		
        IFolderLayout treeFolder = layout.createFolder(
        		"tree.folder", IPageLayout.LEFT, 0.5f, layout.getEditorArea());        
        treeFolder.addView(Control.ID);
        treeFolder.addView(Tree.ID);

        layout.setEditorAreaVisible(true);
	}
}
