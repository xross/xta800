package com.xmos.xta.ui.extn.views.files;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.window.ToolTip;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.FilteredTree;
import org.eclipse.ui.dialogs.PatternFilter;
import org.eclipse.ui.part.ViewPart;

import com.xmos.libs.dwarfhandler.DebugFile;
import com.xmos.xta.core.engine.Notifier;
import com.xmos.xta.core.engine.sourcemapper.SourceFile;
import com.xmos.xta.core.engine.sourcemapper.SourceMapper;
import com.xmos.xta.ui.Broadcaster;
import com.xmos.xta.ui.GuiEngine;
import com.xmos.xta.ui.editors.InputDisassembly;
import com.xmos.xta.ui.editors.InputSource;
import com.xmos.xta.ui.editors.XtaDisassemblyEditor;
import com.xmos.xta.ui.editors.XtaEditor;
import com.xmos.xta.ui.editors.XtaSourceEditor;

public class FilesView extends ViewPart implements IPropertyChangeListener {
    public static final boolean DEBUG = false;
    public static final String ID = "com.xmos.xta.ui.views.files.Files";
    
    private FilteredTree m_filesViewer;
    
    // Keep a map to ensure source files can't be opened twice
    private Map<String, IEditorInput> m_editorInputs;
    
	public FilesView() {
		if (DEBUG) System.out.printf("FilesView()\n");			
		GuiEngine.addBroadcastListener(this);
		m_editorInputs = new HashMap<String, IEditorInput>();
	}

	public void createPartControl(final Composite parent) {
	    if (DEBUG) System.out.printf("FilesView: createPartControl\n");
		
	    m_filesViewer = new FilteredTree(parent, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL, new PatternFilter());
	    final TreeViewer viewer = m_filesViewer.getViewer();
		viewer.getTree().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));

	    viewer.setContentProvider(new FilesViewContentProvider());			
	    viewer.setLabelProvider(new FilesViewLabelProvider());
		ColumnViewerToolTipSupport.enableFor(viewer, ToolTip.NO_RECREATE);

	    m_filesViewer.getViewer().addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				for (Object object : ((IStructuredSelection)viewer.getSelection()).toArray()) {
					if (object instanceof DebugFile) {
						openEditor((DebugFile)object);
					}
				}
			}
		});

	    viewer.setInput(new DebugFile("", "", ""));
	    
	    m_filesViewer.setInitialText("file name filter");
	}

	public void setFocus() {
		if (DEBUG) System.out.printf("FilesView: setFocus\n");			
	}

	public void propertyChange(PropertyChangeEvent event) {
		if (DEBUG) System.out.printf("FilesView: propertyChange: %s\n", event.getProperty());	
		
		if (event.getProperty().equals(Notifier.NOTIFY_LOAD_EXECUTABLE)) {
			m_editorInputs.clear();
			m_filesViewer.getViewer().refresh();
		}
	}

	private void openEditor(DebugFile debugFile) {
        try {
    		final IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();

			IEditorInput input = getInput(debugFile.getFileName());
			if (debugFile.getFileName().equals(XtaDisassemblyEditor.EDITORNAME)) {
	        	XtaEditor editor = (XtaEditor)page.openEditor(input, XtaDisassemblyEditor.ID);
	        	editor.setNames(XtaDisassemblyEditor.EDITORNAME, XtaDisassemblyEditor.EDITORNAME);

        	} else {
	        	XtaEditor editor = (XtaEditor)page.openEditor(input, XtaSourceEditor.ID);
	        	editor.setNames(debugFile.getShortFileName(), debugFile.getFileName());

        	}
			// Cause an update in the new editor which will do the highlighting
			GuiEngine.broadcast(Broadcaster.BROADCAST_SELECTION_CHANGED, "");
		} catch (PartInitException e) {
			e.printStackTrace();
		}
	}

	private IEditorInput getInput(String fileName) {
		if (m_editorInputs.containsKey(fileName)) {
			return m_editorInputs.get(fileName);
		} else {
			IEditorInput input;
			if (fileName.equals(XtaDisassemblyEditor.EDITORNAME)) {
	        	input = new InputDisassembly();
			} else {
				input = new InputSource(getSourceFile(fileName)); 
			}
	        m_editorInputs.put(fileName, input);
	        return input;
		}
	}

	private SourceFile getSourceFile(String fileName) {
		SourceMapper sourceMapper = GuiEngine.getEngine().getSourceMapper();
	    for (SourceFile sourceFile : sourceMapper.getSourceFiles()) {
	    	if (sourceFile.getFileName().equals(fileName)) {
	    		return sourceFile;
	    	}
	    }
	    assert(false);
		return null;
	}
}
