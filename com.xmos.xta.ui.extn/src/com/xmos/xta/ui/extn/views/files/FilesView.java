package com.xmos.xta.ui.extn.views.files;

import java.io.File;
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
import com.xmos.xta.core.engine.Engine;
import com.xmos.xta.core.engine.Notifier;
import com.xmos.xta.core.engine.sourcemapper.SourceFile;
import com.xmos.xta.core.engine.sourcemapper.SourceMapper;
import com.xmos.xta.ui.Broadcaster;
import com.xmos.xta.ui.GuiEngine;
import com.xmos.xta.ui.GuiFileFinder;
import com.xmos.xta.ui.editors.XtaEditor;
import com.xmos.xta.ui.editors.disassembly.XtaDisassemblyEditor;
import com.xmos.xta.ui.editors.source.InputSource;
import com.xmos.xta.ui.editors.source.XtaSourceEditor;

public class FilesView extends ViewPart implements IPropertyChangeListener {
    public static final boolean DEBUG = false;
    public static final String ID = "com.xmos.xta.ui.extn.views.files.Files";
    
    private FilteredTree m_filesViewer;

    // Keep a map to ensure source files can't be opened twice
    private Map<String, IEditorInput> m_editorInputs;
        
	public FilesView() {
		if (DEBUG) System.out.printf("FilesView()\n");			
		GuiEngine.addBroadcastListener(this);
		m_editorInputs = new HashMap<String, IEditorInput>();
	}
	
	public void dispose() {
		GuiEngine.removeBroadcastListener(this);
		super.dispose();
	}

	public void createPartControl(final Composite parent) {
	    if (DEBUG) System.out.printf("FilesView: createPartControl\n");
		
	    m_filesViewer = new FilteredTree(parent, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL, new PatternFilter());
	    final TreeViewer viewer = m_filesViewer.getViewer();
		viewer.getTree().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));

	    viewer.setContentProvider(new FilesViewContentProvider());			
	    viewer.setLabelProvider(new FilesViewLabelProvider());
		ColumnViewerToolTipSupport.enableFor(viewer, ToolTip.NO_RECREATE);

		viewer.addDoubleClickListener(new IDoubleClickListener() {
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
			if (!GuiEngine.getEngine().wasReload()) {
				m_editorInputs.clear();
				m_filesViewer.getViewer().refresh();
			}
		}
	}

	private void openEditor(DebugFile debugFile) {
        try {
    		final IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();

    		if (debugFile.getFileName().equals(XtaDisassemblyEditor.EDITORNAME)) {
    			GuiEngine.openDisassembly();
    			return;
    		}
    			
			IEditorInput input = getInput(debugFile.getFullFileName());
			if (input == null)
				return;
			
        	XtaEditor editor = (XtaEditor)page.openEditor(input, XtaSourceEditor.ID);
        	editor.setName(debugFile.getShortFileName());

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
			SourceFile sourceFile = getSourceFile(fileName);
			if (sourceFile != null) {
				IEditorInput input = new InputSource(sourceFile); 
				m_editorInputs.put(fileName, input);
				return input;
			} else {
				return null;
			}
		}
	}

	private SourceFile getSourceFile(String fileName) {
		Engine engine = GuiEngine.getEngine();
		SourceMapper sourceMapper = engine.getSourceMapper();
		SourceFile sourceFile = sourceMapper.getSourceFile(fileName);
		if (sourceFile.getDebugFile().getFile() == null) {
			File file = sourceMapper.findFileForSourceFile(sourceFile, new GuiFileFinder());
			if (file == null)
				return null;

			engine.updateSourceMappers(fileName, file);
			m_filesViewer.getViewer().refresh();
		}
		return sourceFile;
	}
}
