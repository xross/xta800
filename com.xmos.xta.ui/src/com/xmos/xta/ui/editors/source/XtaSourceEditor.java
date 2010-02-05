package com.xmos.xta.ui.editors.source;

import java.io.IOException;

import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.widgets.Composite;

import com.xmos.xta.ui.annotations.XtaSourceAnnotations;
import com.xmos.xta.ui.editors.XtaEditor;
import com.xmos.xta.ui.input.InputSource;


public class XtaSourceEditor extends XtaEditor {
    public static final String ID = "com.xmos.xta.ui.editors.XtaSourceEditor";

    public XtaSourceEditor() {
    	setSourceViewerConfiguration(new XtaSourceConfiguration(this));
    	m_annotations = new XtaSourceAnnotations(this);
    }

    public void createPartControl(Composite parent) {
		if (DEBUG) System.out.printf("XtaSourceEditor: createPartControl\n"); 
    	super.createPartControl(parent);

    	StyledText text = getSourceViewer().getTextWidget();
    	text.addMouseListener(new MouseListener() {

			public void mouseDoubleClick(MouseEvent e) {
				m_annotations.updateSourcePcs(true);
			}

			public void mouseDown(MouseEvent e) {}

			public void mouseUp(MouseEvent e) {
				m_annotations.updateSourcePcs(false);
			}
    	});

		text.addKeyListener(new KeyListener() {

			public void keyPressed(KeyEvent e) {}

			public void keyReleased(KeyEvent e) {
				m_annotations.updateSourcePcs(false);
			}
		});

		m_annotations.connectToFile();
    }

	public String getFileName() {
	    String fileName = "";
	    try {
			fileName = ((InputSource)getEditorInput()).getSourceFile().getFile().getCanonicalPath();
		} catch (IOException e) {
		}
		return fileName;
	}

	public void setReadOnly(boolean readOnly) {
		// Ignore - already is readOnly...
	}
}
