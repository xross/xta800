package com.xmos.xta.ui.dialogs;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;

import com.xmos.libs.util.Conversion;
import com.xmos.xta.core.engine.Config;
import com.xmos.xta.core.engine.Engine;
import com.xmos.xta.core.engine.Notifier;
import com.xmos.xta.core.engine.analysis.structural.nodes.Node.Case;
import com.xmos.xta.ui.GuiEngine;

public class Settings extends Dialog {
	private static final int COLUMN_WIDTH = 60;
	
    private Button m_caseButton;
    private Button m_scaleButton;
    
    private SettingsLabelProvider m_labelProvider;
    private TableViewer m_viewer;

    private Map<Integer, Double> m_oldFrequencies;
    private Map<Integer, Map<Integer, Integer>> m_oldNumThreads;
    
    public Settings(Shell parent) {
    	super(parent);
    	logFrequencies();
    	logNumThreads();
    	open();
    }

	private void logFrequencies() {
		m_oldFrequencies = new TreeMap<Integer, Double>();
		Engine engine = GuiEngine.getEngine();
		Vector<Integer> nodeNumbers = engine.getXeLoader().getNodeNumbers();
		for (int nodeNumber : nodeNumbers) {
			m_oldFrequencies.put(nodeNumber, engine.getFrequency(nodeNumber));
		}
	}

	private void logNumThreads() {
		m_oldNumThreads = new TreeMap<Integer, Map<Integer, Integer>>();
		Engine engine = GuiEngine.getEngine();
		Vector<Integer> nodeNumbers = engine.getXeLoader().getNodeNumbers();
		for (int nodeNumber : nodeNumbers) {
			Map<Integer, Integer> threadNumbers = new TreeMap<Integer, Integer>();
			for (int coreNumber : engine.getXeLoader().getCoreNumbers(nodeNumber)) {
				threadNumbers.put(coreNumber, engine.getNumThreads(nodeNumber, coreNumber));
			}
			
			m_oldNumThreads.put(nodeNumber, threadNumbers);
		}
	}


	private void setFrequency(int nodeNumber, double frequency) {
		GuiEngine.getEngine().setFrequency(GuiEngine.s_guiSrc, nodeNumber, frequency);
	}

	private void setNumThreads(int nodeNumber, int coreNumber, int numThreads) {
		GuiEngine.getEngine().setNumThreads(GuiEngine.s_guiSrc, nodeNumber, coreNumber, numThreads);
	}	
    
    protected void configureShell(Shell newShell) {
    	super.configureShell(newShell);
    	XtaDialog.configureShell(newShell, "Xta Settings", 0, 0);
    }

    protected Control createDialogArea(Composite parent) {
    	Composite container = (Composite) super.createDialogArea(parent);

		Composite controls = new Composite(parent, SWT.NONE);
	    GridLayout layout = new GridLayout();
	    layout.numColumns = 2;	    	    
	    controls.setLayout(layout);
	    controls.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));
	    
	    createSettingsBar(controls);
    	return container;
    }

    protected void buttonPressed(int id) {
    	if (id == IDialogConstants.CANCEL_ID) {
    		Engine engine = GuiEngine.getEngine();
    		Vector<Integer> nodeNumbers = engine.getXeLoader().getNodeNumbers();
    		for (int nodeNumber : nodeNumbers) {
    			engine.setFrequency(GuiEngine.s_guiSrc, nodeNumber, m_oldFrequencies.get(nodeNumber));

    			Map<Integer, Integer> threadNumbers = m_oldNumThreads.get(nodeNumber);
    			for (int coreNumber : engine.getXeLoader().getCoreNumbers(nodeNumber)) {
    				setNumThreads(nodeNumber, coreNumber, threadNumbers.get(coreNumber));
    			}
    		}
    	}
    	super.buttonPressed(id);
    }
    
    void checkButtonStatus() {
    }    
    
	private void createSettingsBar(Composite controls) {
		Group caseGroup = XtaDialog.createGroup(controls, "Print worst case", 1, 1);
		m_caseButton = new Button(caseGroup, SWT.CHECK);
		m_caseButton.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false));
		m_caseButton.setSelection(isWorstCase());
		m_caseButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				setCase(m_caseButton.getSelection());
			}
		});

		Group scaleGroup = XtaDialog.createGroup(controls, "Scale results", 1, 1);
		m_scaleButton = new Button(scaleGroup, SWT.CHECK);
		m_scaleButton.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false));
		m_scaleButton.setSelection(Conversion.getScale());
		m_scaleButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				setScale(m_scaleButton.getSelection());
			}
		});

		createNodeSettings(controls);
	}
		
	private void createNodeSettings(Composite controls) {
		Engine engine = GuiEngine.getEngine();
		Vector<Integer> nodeNumbers = engine.getXeLoader().getNodeNumbers();
		if (nodeNumbers.size() == 0) {
			return;
		}
	
		Group freqGroup = XtaDialog.createGroup(controls, "Frequency/Threads", 2, 2);
		m_viewer = new TableViewer(freqGroup);
		Table table = m_viewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		IStructuredContentProvider contentProvider = new SettingsContentProvider();
		m_labelProvider = new SettingsLabelProvider(engine);

		int i = 0;
		List<Integer> alignments = m_labelProvider.getColumnAlignments();
		List<String> columnNames = m_labelProvider.getColumnNames();
		for (String label : columnNames) {
			TableColumn tableColumn = new TableColumn(table, alignments.get(i));
			tableColumn.setText(label);
			tableColumn.setWidth(COLUMN_WIDTH);
			i++;
		}
		m_viewer.setContentProvider(contentProvider);
		m_viewer.setLabelProvider(m_labelProvider);

		String names[] = new String[columnNames.size()];
		columnNames.toArray(names);
		m_viewer.setColumnProperties(names);
		
		m_viewer.setInput(engine);
		addCellEditors(nodeNumbers);
	}

	private void addCellEditors(Vector<Integer> nodeNumbers) {
		CellEditor[] editors = new CellEditor[nodeNumbers.size() + 1];

		Table table = m_viewer.getTable();
		TextCellEditor readOnly = new TextCellEditor(table);
        ((Text) readOnly.getControl()).addVerifyListener(new VerifyListener() {
    		public void verifyText(VerifyEvent e) {
    			e.doit = false;  
    		}
        });
        editors[0] = readOnly;

		for (final int nodeNumber : nodeNumbers) {
			TextCellEditor textEditor = new TextCellEditor(table);
			final Text text = ((Text) textEditor.getControl());
			text.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					try {
						int rowNumber = m_viewer.getTable().getSelectionIndex();
						if (rowNumber == 0) {
							double frequency = Double.parseDouble(text.getText());
							if (frequency > 0) {
								setFrequency(nodeNumber, frequency);
							}
						} else {
							int numThreads = Integer.parseInt(text.getText());
							if ((numThreads > 0) && (numThreads <= Config.MAX_THREADS)) {
								int coreNumber = rowNumber - 1;
								setNumThreads(nodeNumber, coreNumber, numThreads);
							}
						}
					} catch(Exception ex) { }
				}
			});
	        editors[nodeNumber + 1] = textEditor;
		}

		m_viewer.setCellEditors(editors);
		m_viewer.setCellModifier(new SettingsCellModifier(m_viewer, m_labelProvider));
	}

	private boolean isWorstCase() {
		return GuiEngine.getEngine().getCase().equals(Case.WORST);
	}

	private void setCase(boolean isWorstCase) {
		GuiEngine.getEngine().setCase(GuiEngine.s_guiSrc, isWorstCase ? Case.WORST : Case.BEST);
	}

	private void setScale(boolean scale) {
		if (scale != Conversion.getScale()) {
			Conversion.setScale(scale);
			GuiEngine.getEngine().notify(GuiEngine.s_guiSrc, Notifier.NOTIFY_UPDATE_STATE, Notifier.STATE_SCALE);
		}
	}
}

