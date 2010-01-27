package com.xmos.xta.ui.dialogs;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.FindReplaceDocumentAdapter;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.projection.ProjectionAnnotationModel;
import org.eclipse.jface.text.source.projection.ProjectionViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class Find extends Dialog {
	private static final int BUTTON_FIND_ID  = 0;
	private static final int BUTTON_CLOSE_ID = 1;
	
	private ISourceViewer m_viewer;
	private Text m_text;
	private static String m_searchString = "";
	private static boolean m_forwardSearch = true;
	private static boolean m_caseSensitive = false;
	private static boolean m_wholeWord = false;
	private static boolean m_regexSearch = false;
	private static boolean m_wrapSearch = true;
	
	private Button m_findButton;
	
    public Find(Shell parent, ISourceViewer viewer) {
    	super(parent);
    	m_viewer = viewer;
    	open();
    }

    protected void configureShell(Shell newShell) {
    	super.configureShell(newShell);
    	newShell.setText("Find");
    }

    protected Control createDialogArea(Composite parent) {
    	Composite container = (Composite) super.createDialogArea(parent);    
    	
	    GridLayout gridLayout = new GridLayout();
	    gridLayout.numColumns = 1;
	    container.setLayout(gridLayout);		

		Group signalGroup = createGroup(container, "Find");				
    	m_text = new Text(signalGroup, SWT.BORDER | SWT.SINGLE); 
    	m_text.setLayoutData(new GridData(GridData.FILL, SWT.TOP, true, false, 2, 1));
    	m_text.setText(m_searchString);
    	m_text.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				m_searchString = m_text.getText();
				m_findButton.setEnabled(m_searchString.length() > 0);
			}
    	});
    	
    	createDirectionOptions(container);
    	createSearchOptions(container);
    	
        m_text.addKeyListener(new KeyListener() {
        	public void keyPressed(KeyEvent event) {
        		if ((event.keyCode == SWT.CR) || 
        			(event.keyCode == SWT.KEYPAD_CR)) {
        			handleSearch(m_text.getText());
        		}
        	}
			public void keyReleased(KeyEvent e) {
			}
        });
                
    	return container;
    }
    
    protected void createButtonsForButtonBar(Composite parent) {
    	m_findButton = createButton(parent, BUTTON_FIND_ID, "Find", false);
    	m_findButton.setEnabled(m_searchString.length() > 0);
    	createButton(parent, BUTTON_CLOSE_ID, "Close", false);    	
    }

    protected void buttonPressed(int buttonId) {
    	switch (buttonId) {
    	case BUTTON_FIND_ID:
    		handleSearch(m_text.getText());
    		break;

    	case BUTTON_CLOSE_ID:
    		close();
    		break;

    	default:
    		assert(false);
    	    break;
    	}
    }
	
	protected Group createGroup(Composite parent, String description) {
		Group group = new Group(parent, SWT.SHADOW_ETCHED_IN);
	    GridLayout layout = new GridLayout();
	    layout.numColumns = 2;
	    group.setLayout(layout);
	    group.setText(description);
	    group.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
	    return group;
	}	
	
    void createDirectionOptions(Composite container) {
		Group directionGroup = createGroup(container, "Direction");
		final Button forwardButton = new Button(directionGroup, SWT.RADIO);
		forwardButton.setText("Forward");
		forwardButton.setSelection(m_forwardSearch);
		forwardButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				m_forwardSearch = forwardButton.getSelection();
			}
		});
		
		Button backward = new Button(directionGroup, SWT.RADIO);
		backward.setText("Backward");
		backward.setSelection(!m_forwardSearch);
    }

    void createSearchOptions(Composite container) {
		Group optionsGroup = createGroup(container, "Options");
		
		final Button caseButton = new Button(optionsGroup, SWT.CHECK);
		caseButton.setText("Case sensitive");
		caseButton.setSelection(m_caseSensitive);
		caseButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				m_caseSensitive = caseButton.getSelection();
			}
		});

		final Button wrapSearchButton = new Button(optionsGroup, SWT.CHECK);
		wrapSearchButton.setText("Wrap search");
		wrapSearchButton.setSelection(m_wrapSearch);
		wrapSearchButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				m_wrapSearch = wrapSearchButton.getSelection();
			}
		});

		final Button wholeWordButton = new Button(optionsGroup, SWT.CHECK);
		wholeWordButton.setText("Whole word");
		wholeWordButton.setSelection(m_wholeWord);
		wholeWordButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				m_wholeWord = wholeWordButton.getSelection();
			}
		});

		final Button regExpButton = new Button(optionsGroup, SWT.CHECK);
		regExpButton.setText("Regular expressions");
		regExpButton.setSelection(m_regexSearch);
		regExpButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				m_regexSearch = regExpButton.getSelection();
			}
		});
    }

	void handleSearch(String findString) {
		int startIndex = m_viewer.getSelectedRange().x;
		
		// Ensure the current search result is not found again
		if (m_forwardSearch) {
			startIndex += 1;
		} else {
			startIndex -= 1;
		}
		
		IRegion result = null;
		try {
			IDocument document = m_viewer.getDocument();
			FindReplaceDocumentAdapter findReplaceDocumentAdapter = new FindReplaceDocumentAdapter(document);

			result = findReplaceDocumentAdapter.find(startIndex, findString,
					m_forwardSearch, m_caseSensitive, m_wholeWord, m_regexSearch);
		
			if (result == null && m_wrapSearch) {
				startIndex = m_forwardSearch ? 0 : document.getLength() - 1;
				result = findReplaceDocumentAdapter.find(startIndex, findString,
						m_forwardSearch, m_caseSensitive, m_wholeWord, m_regexSearch);
			}
		} catch (BadLocationException e) {
			// Do nothing
		}

		if (result != null) {
			showRegion(result);
		}
	}

	protected void showRegion(IRegion result) {
		int offset = result.getOffset();
		int length = result.getLength();
		
		ProjectionAnnotationModel model = ((ProjectionViewer)m_viewer).getProjectionAnnotationModel();
		if (model != null) {
			model.expandAll(offset, 1);
			m_viewer.setRangeIndication(offset, 0, true);
			m_viewer.setSelectedRange(offset, length);
		}
	}
}
