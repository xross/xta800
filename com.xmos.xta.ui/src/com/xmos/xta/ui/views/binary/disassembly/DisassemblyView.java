package com.xmos.xta.ui.views.binary.disassembly;

import java.util.Iterator;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.AnnotationModel;
import org.eclipse.jface.text.source.IAnnotationAccess;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.jface.text.source.IAnnotationModelExtension;
import org.eclipse.jface.text.source.IOverviewRuler;
import org.eclipse.jface.text.source.ISharedTextColors;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.IVerticalRuler;
import org.eclipse.jface.text.source.OverviewRuler;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.jface.text.source.VerticalRuler;
import org.eclipse.jface.text.source.projection.ProjectionAnnotationModel;
import org.eclipse.jface.text.source.projection.ProjectionSupport;
import org.eclipse.jface.text.source.projection.ProjectionViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.editors.text.EditorsUI;
import org.eclipse.ui.internal.editors.text.EditorsPlugin;
import org.eclipse.ui.texteditor.AbstractDecoratedTextEditorPreferenceConstants;
import org.eclipse.ui.texteditor.AnnotationPreference;
import org.eclipse.ui.texteditor.DefaultMarkerAnnotationAccess;
import org.eclipse.ui.texteditor.DefaultRangeIndicator;
import org.eclipse.ui.texteditor.MarkerAnnotationPreferences;
import org.eclipse.ui.texteditor.SourceViewerDecorationSupport;

import com.xmos.xta.ui.GuiEngine;
import com.xmos.xta.ui.actions.SourceActions;
import com.xmos.xta.ui.annotations.XtaAnnotatedObject;
import com.xmos.xta.ui.annotations.XtaAnnotations;
import com.xmos.xta.ui.annotations.XtaDisassemblyAnnotations;
import com.xmos.xta.ui.dialogs.Find;
import com.xmos.xta.ui.editors.XtaDocumentProvider;
import com.xmos.xta.ui.views.ControlMonitor;
import com.xmos.xta.ui.views.binary.BinaryView;

@SuppressWarnings("restriction")
public class DisassemblyView extends ControlMonitor implements XtaAnnotatedObject {
	public final static String ID = "com.xmos.xta.ui.views.binary.Binary";
	public final static String ViewContextViewID  = "#BinaryViewContext";
	public final static String RulerContextViewID = "#BinaryEditorRulerContext";

	private final static int VERTICAL_RULER_WIDTH = 12;
	private final static int LEFT_MOUSE_BUTTON  = 1;
	private final static String CURRENT_LINE = AbstractDecoratedTextEditorPreferenceConstants.EDITOR_CURRENT_LINE;
	private final static String CURRENT_LINE_COLOR = AbstractDecoratedTextEditorPreferenceConstants.EDITOR_CURRENT_LINE_COLOR;

	private IWorkbenchPartSite m_site;
	private SourceActions m_actions;
	private IEditorInput m_input;

    private XtaDocumentProvider m_documentProvider;
	private ISourceViewer m_viewer;
	private XtaAnnotations m_annotations;
	private IVerticalRuler m_verticalRuler;
	private IOverviewRuler m_overviewRuler;

	private Menu m_rulerContextMenu;
	private Menu m_textContextMenu;
	private IMenuListener m_menuListener;
	private MouseListener m_mouseListener;
	private IAnnotationAccess m_annotationAccess;
	private MarkerAnnotationPreferences m_annotationPreferences;
	private SourceViewerDecorationSupport m_sourceViewerDecorationSupport;

	public DisassemblyView(Composite parent, IWorkbenchPartSite site) {
		super(BinaryView.ID, false);
		m_site = site;
		
		m_annotationPreferences = new MarkerAnnotationPreferences();
		m_annotationAccess = new DefaultMarkerAnnotationAccess();

		m_menuListener = new IMenuListener() {
			public void menuAboutToShow(IMenuManager menu) {
				String id = menu.getId();
				if (id.equals(RulerContextViewID)) {
					rulerContextMenuAboutToShow(menu);
				}
				else if (id.equals(ViewContextViewID)) {
					viewContextMenuAboutToShow(menu);
				}
			}
		};

		m_mouseListener = new MouseAdapter() {
			public void mouseUp(MouseEvent e) {
				if (e.x < VERTICAL_RULER_WIDTH) {
					long clickLineIndex = m_verticalRuler.getLineOfLastMouseButtonActivity();
					if (e.button == LEFT_MOUSE_BUTTON) {
						foldUnfold(clickLineIndex);
					}
				}
			}

			public void mouseDown(MouseEvent e) {
				StyledText text = m_viewer.getTextWidget();
				if (text != null && !text.isDisposed()) {
					Display display = text.getDisplay();
					Point location = display.getCursorLocation();
					m_rulerContextMenu.setLocation(location.x, location.y);
				}
			}
		};
		
		m_actions = new SourceActions(parent);

		createViewer(parent);
		createViewContextMenu();
		createRulerContextMenu();
		if (m_sourceViewerDecorationSupport != null) {
			m_sourceViewerDecorationSupport.install(getEditorPreferenceStore());
		}

		ProjectionSupport projectionSupport = new ProjectionSupport((ProjectionViewer) m_viewer,
				m_annotationAccess, getSharedColors());
        projectionSupport.install();

        ((SourceViewer) m_viewer).doOperation(ProjectionViewer.TOGGLE);
        m_annotations.connectToFile();		
	}

	public void dispose() {
		m_annotations.dispose();
		if (m_sourceViewerDecorationSupport != null) {
			m_sourceViewerDecorationSupport.dispose();
			m_sourceViewerDecorationSupport = null;
		}
	}

	public IEditorInput getInput() {
		return m_input;
	}

	public XtaDocumentProvider getDocumentProvider() {
		if (m_documentProvider == null)
			m_documentProvider = new XtaDocumentProvider();
		return m_documentProvider;
	}
	
	public IAnnotationModelExtension getAnnotationModel() {
		ProjectionViewer viewer = (ProjectionViewer)m_viewer;
        return (IAnnotationModelExtension)viewer.getAnnotationModel();
	}

	public IDocument getDocument() {
		return m_viewer.getDocument();
	}

	public IEditorInput getEditorInput() {
		return GuiEngine.getDisassemblyInput();
	}

	public String getFileName() {
		return "DISASSEMBLY";
	}

	public ProjectionAnnotationModel getProjectionAnnotationModel() {
		ProjectionViewer viewer = (ProjectionViewer)m_viewer;
		return viewer.getProjectionAnnotationModel();
	}

	public ISourceViewer getViewer() {
		return m_viewer;
	}

	public void setReadOnly(boolean readOnly) {
		// Ignore - already is readOnly...
	}

	public boolean isDirty() {
		return false;
	}

	public void refreshInput(IEditorInput input) {
		m_input = input;
		try {
			getDocumentProvider().connect(input);
		} catch (CoreException e) {
			assert(false);
		}
		IAnnotationModel model = getDocumentProvider().getAnnotationModel(input);
		m_viewer.setDocument(getDocumentProvider().getDocument(input), model);
	}

	protected SourceViewerDecorationSupport getSourceViewerDecorationSupport(ISourceViewer viewer) {
		if (m_sourceViewerDecorationSupport == null) {
			m_sourceViewerDecorationSupport = new SourceViewerDecorationSupport(viewer, getOverviewRuler(),
					m_annotationAccess, getSharedColors());
			configureSourceViewerDecorationSupport(m_sourceViewerDecorationSupport);
		}
		return m_sourceViewerDecorationSupport;
	}
	
	protected void setInput(IEditorInput input) {
		m_input = input;
	}
	
	protected void createViewer(final Composite parent) {
		m_verticalRuler = createVerticalRuler();
		m_overviewRuler = createOverviewRuler(getSharedColors());
		
		m_viewer = createSourceViewer(parent, m_verticalRuler, getOverviewRuler());
		
		m_annotations = new XtaDisassemblyAnnotations(this);
		m_viewer.configure(new XtaDisassemblyConfiguration(m_annotations));
		getSourceViewerDecorationSupport(m_viewer);
		
		m_viewer.setDocument(new Document(), new AnnotationModel());

		refreshInput(GuiEngine.getDisassemblyInput()); 
		
		m_viewer.getTextWidget().addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				if (((e.stateMask & SWT.CTRL) != 0) &&
    				(e.character == eventChar('f'))) {
					new Find(parent.getShell(), m_viewer);
				}
			}
		});
	}

	protected IVerticalRuler createVerticalRuler() {
		IVerticalRuler ruler = new VerticalRuler(VERTICAL_RULER_WIDTH, m_annotationAccess);
		return ruler;
	}

	protected void rulerContextMenuAboutToShow(IMenuManager menu) {
		long clickLineIndex = m_verticalRuler.getLineOfLastMouseButtonActivity();
		m_actions.createRulerContextMenu(menu, m_annotations, clickLineIndex);
	}

	protected void viewContextMenuAboutToShow(IMenuManager menu) {
     	menu.add(new Action("Expand All") {
     		public void run() {
     			IDocument document = m_viewer.getDocument();
     			getProjectionAnnotationModel().expandAll(0, document.getLength());
     		}
     	});
     	
     	menu.add(new Action("Collapse All") {
     		public void run() {
     			IDocument document = m_viewer.getDocument();
     			getProjectionAnnotationModel().collapseAll(0, document.getLength());
     		}
     	});
	}
	
	@SuppressWarnings("unchecked")
	private void foldUnfold(long clickLineIndex) {
		if (!m_annotations.isSection(clickLineIndex)) {
			return;
		}
		try {
			IDocument document = getDocument();
			int charOffset = document.getLineOffset((int)clickLineIndex);

			ProjectionAnnotationModel model = getProjectionAnnotationModel();
			Iterator<Annotation> iter = model.getAnnotationIterator(charOffset, 1, false, true);
			while (iter.hasNext()) {
				Annotation annotation = iter.next();
				model.toggleExpansionState(annotation);
			}
		} catch (BadLocationException e) {}
	}
	
	@SuppressWarnings("unchecked")
	private IOverviewRuler createOverviewRuler(ISharedTextColors sharedColors) {
		IOverviewRuler ruler = new OverviewRuler(m_annotationAccess, VERTICAL_RULER_WIDTH, sharedColors);
		Iterator e = m_annotationPreferences.getAnnotationPreferences().iterator();
		while(e.hasNext()) {
			AnnotationPreference preference = (AnnotationPreference)e.next();
			if (preference.contributesToHeader())
				ruler.addHeaderAnnotationType(preference.getAnnotationType());
		}
		return ruler;
	}

	private SourceViewer createSourceViewer(Composite parent, IVerticalRuler vertRuler, IOverviewRuler ovRuler) {
		DisassemblyViewer viewer = new DisassemblyViewer(parent, vertRuler, ovRuler);
		viewer.setRangeIndicator(new DefaultRangeIndicator());

		Font font = JFaceResources.getTextFont();
		viewer.getTextWidget().setFont(font);
		return viewer;
	}

	@SuppressWarnings("unchecked")
	private void configureSourceViewerDecorationSupport(SourceViewerDecorationSupport support) {
		Iterator e = m_annotationPreferences.getAnnotationPreferences().iterator();
		while(e.hasNext()) {
			support.setAnnotationPreference((AnnotationPreference)e.next());
		}
		support.setCursorLinePainterPreferenceKeys(CURRENT_LINE, CURRENT_LINE_COLOR);
	}

	private ISharedTextColors getSharedColors() {
		ISharedTextColors sharedColors = EditorsPlugin.getDefault().getSharedTextColors();
		return sharedColors;
	}

	private IPreferenceStore getEditorPreferenceStore() {
		return EditorsUI.getPreferenceStore();
	}

	private IOverviewRuler getOverviewRuler() {
		if (m_overviewRuler == null)
			m_overviewRuler = createOverviewRuler(getSharedColors());
		return m_overviewRuler;
	}

	private void createViewContextMenu() {
		MenuManager manager = new MenuManager(ViewContextViewID, ViewContextViewID);
		manager.setRemoveAllWhenShown(true);
		manager.addMenuListener(m_menuListener);
		StyledText styledText = m_viewer.getTextWidget();
		m_textContextMenu = manager.createContextMenu(styledText);
		styledText.setMenu(m_textContextMenu);

		if (m_site != null) {
			m_site.registerContextMenu(ViewContextViewID, manager, (SourceViewer)m_viewer);
		}
	}

	private void createRulerContextMenu() {
		MenuManager manager = new MenuManager(RulerContextViewID, RulerContextViewID);
		manager.setRemoveAllWhenShown(true);
		manager.addMenuListener(m_menuListener);
		Control rulerControl = m_verticalRuler.getControl();
		m_rulerContextMenu = manager.createContextMenu(rulerControl);
		rulerControl.setMenu(m_rulerContextMenu);
		rulerControl.addMouseListener(m_mouseListener);
	}

	private int eventChar(char c) {
		return (c - 'a' + 1);
	}
}
