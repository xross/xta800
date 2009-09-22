package com.xmos.xta.ui.editors;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.Vector;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.VerifyKeyListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.texteditor.ITextEditorActionConstants;

import com.xmos.xta.core.engine.Notifier;
import com.xmos.xta.core.engine.analysis.structural.nodes.InstructionNode;
import com.xmos.xta.core.engine.analysis.structural.nodes.Node;
import com.xmos.xta.core.engine.analysis.structural.nodes.traversers.DefaultNodeTraverser;
import com.xmos.xta.core.engine.instructions.XtaInstruction;
import com.xmos.xta.core.engine.references.Reference;
import com.xmos.xta.core.engine.sourcemapper.AssemblyLine;
import com.xmos.xta.ui.Broadcaster;
import com.xmos.xta.ui.GuiEngine;

public class XtaDisassemblyEditor extends XtaEditor {
    public static final String ID = "com.xmos.xta.gui.editors.XtaDisassemblyEditor";
    public static final String EDITORNAME = "Disassembly";

    public static final String ExpandAllID           = "com.xmos.xta.gui.action.ExpandAll";
    public static final String CollapseAllID         = "com.xmos.xta.gui.action.CollapseAll";
    
    protected static final String fnopString = AssemblyLine.getLeadingWhiteSpace() + " FNOP\n";
    
    public XtaDisassemblyEditor() {
    	super(XtaDocumentPartitioner.DISASSEMBLY);
    	setSourceViewerConfiguration(new XtaDisassemblyConfiguration(this));
    }

    public void createPartControl(Composite parent) {
		if (DEBUG) System.out.printf("XtaDisassemblyEditor: createPartControl\n"); 
    	super.createPartControl(parent);
    
        getSourceViewer().getTextWidget().addVerifyKeyListener(new VerifyKeyListener() {
			public void verifyKey(VerifyEvent event) {
				ISelection selection = getSelectionProvider().getSelection();

				if (selection instanceof ITextSelection) {
					ITextSelection textSelection = (ITextSelection)selection;

					if ((event.keyCode == SWT.CR) || (event.keyCode == SWT.KEYPAD_CR)) {
						try {
							IRegion line = getDocument().getLineInformationOfOffset(textSelection.getOffset());
							m_projectionAnnotationModel.expandAll(line.getOffset() + line.getLength(), 1);
						} catch (BadLocationException e) {}
	
					} else if (event.keyCode == SWT.BS) {
						try {
							IRegion line = getDocument().getLineInformationOfOffset(textSelection.getOffset());
							m_projectionAnnotationModel.collapseAll(line.getOffset() + line.getLength(), 1);
						} catch (BadLocationException e) {}
					}
				}
			}
        });
    }

	public void propertyChange(PropertyChangeEvent event) {
		if (DEBUG) System.out.printf("XtaEditor: propertyChange: %s\n", event.getProperty());	

		if (event.getProperty().equals(Notifier.NOTIFY_LOAD_EXECUTABLE)) {
			updateAll();

		} else if (event.getProperty().equals(Notifier.NOTIFY_SELECT_ROUTE)) {
	        updateStartPoint();
	        updateEndPoint();
			updateAll();

		} else if (event.getProperty().equals(Notifier.NOTIFY_SET_START_POINT) ||
				   event.getProperty().equals(Broadcaster.BROADCAST_SET_START_POINT)) {
			updateStartPoint();
			updateHighlighting();

		} else if (event.getProperty().equals(Notifier.NOTIFY_SET_END_POINT) ||
				   event.getProperty().equals(Broadcaster.BROADCAST_SET_END_POINT)) {
			updateEndPoint();
			updateHighlighting();

		} else if (event.getProperty().equals(Broadcaster.BROADCAST_SHOW_CODE_POINT)) {
			Reference codePoint = GuiEngine.getCodePoint();
	    	if (codePoint == null || !codePoint.getResolved()) {
	    		return;
	    	}
			showCodePoint(codePoint.getPc());
			
		} else if (event.getProperty().equals(Notifier.NOTIFY_UPDATE_UNKNOWNS)) {
			updateUnknowns();

		} else if (event.getProperty().equals(Notifier.NOTIFY_UPDATE_EXCLUSIONS)) {
			updateExcluded();
			updateHighlighting();

		} else if (event.getProperty().equals(Notifier.NOTIFY_SET_NODE_CORE)) {
	        updateStartPoint();
	        updateEndPoint();
	        
			Input input = getInput();
	    	if (input == null)
	    		return;	        

		} else if (event.getProperty().equals(Broadcaster.BROADCAST_SELECTION_CHANGED)) {
			updateSelection(GuiEngine.getSelectedNodes());
			
		} else if (event.getProperty().equals(Broadcaster.BROADCAST_SET_FOCUS)) {
			Node focusNode = GuiEngine.getFocus();
			if (focusNode != null) {
				for (XtaInstruction instruction : focusNode.getInstructions()) {
					boolean hit = showCodePoint(instruction.getPc());
					if (hit) {
						return;
					}
				}
			}

		}
	}

	void updateAll() {
		updateFnops();
		updateExcluded();
		updateUnknowns();
		updateHighlighting();
	}

	protected void createActions() {
		if (DEBUG) System.out.printf("XtaDisassemblyEditor: createActions\n"); 
     	super.createActions();
     	
     	setAction(ExpandAllID, new Action("Expand All") {
     		public void run() {
     			IDocument document = getSourceViewer().getDocument();
     			m_projectionAnnotationModel.expandAll(0, document.getLength());
     		}
     	});
     	
     	setAction(CollapseAllID, new Action("Collapse All") {
     		public void run() {
     			IDocument document = getSourceViewer().getDocument();
     			m_projectionAnnotationModel.collapseAll(0, document.getLength());
     		}
     	});
    }    

    private void updateFnops() {
		if (DEBUG) System.out.printf("XtaEditor: updateFnops\n"); 
		
    	TreeSet<XtaInstruction> toAdd = new TreeSet<XtaInstruction>();
    	TreeSet<XtaInstruction> toRemove;
    	TreeSet<XtaInstruction> toKeep  = new TreeSet<XtaInstruction>();
    	
   		final Collection<XtaInstruction> fnops = new Vector<XtaInstruction>();
   		for (Node node : m_selectedNodes) {
       		node.traverse(0, new DefaultNodeTraverser() {
       		    public boolean foundInstructionNode(Node node, int depth) {
    				XtaInstruction instruction = ((InstructionNode)node).getXtaInstruction();				
       		    	if (instruction.isFnop()) {   		    		
       	   		    	fnops.add(instruction);
       		    	}
       		    	return true;
       		    }   			
       		});    		
    	}
   		
   		// Makes a list of all the fnops to add.
   		for (XtaInstruction inst: fnops){
   			if (m_previousFnops.contains(inst)){
   				toKeep.add(inst);
   			} else {
   				toAdd.add(inst);
   			}
   		}
    
    	// Makes a list of old fnops to remove
    	toRemove = m_previousFnops;
    	toRemove.removeAll(toKeep);
    	
    	// Removes the old fnops
    	for (XtaInstruction inst : toRemove) {
    		removeFnop(inst.getPc());
		}

    	// Adds the new fnops
    	for (XtaInstruction inst : toAdd) {
			addFnop(inst.getPc());
		}
    	
    	m_previousFnops = toKeep;
    	m_previousFnops.addAll(toAdd);
    	
	    ((InputDisassembly)getEditorInput()).updateLineNumberMapping();
	}
        
    private void removeFnop(long pc) {
		int fnopsRemoved = 0;
		IDocument document = getDocument();
    	for (InputSection inputSection : getSections()) {
    		inputSection.setIndex(inputSection.getIndex() - fnopsRemoved);
    		
    		InputLine fnopLine = inputSection.removeFnop(pc);
    		while (fnopLine != null) {
    			fnopsRemoved++;
    			long index = fnopLine.getIndex();
				try {
					IRegion lineInfo = document.getLineInformation((int)index);
		            document.replace(lineInfo.getOffset(), fnopString.length(), "");
				} catch (BadLocationException e) {
					e.printStackTrace();
				}
	    		fnopLine = inputSection.removeFnop(pc);
			}
    	}	
	}
    
    private void addFnop(long pc) {
    	boolean fnopsAdded = false;

    	IDocument document = getDocument();    	
    	for (InputSection inputSection : getSections()) {
    		if (fnopsAdded) {
    			inputSection.incrementIndex();
    		} else {
	    		long lineNum = 0;
	    		while (!fnopsAdded && (lineNum < inputSection.getNumLines())) {
	    			InputLine line = inputSection.getLine(lineNum);
	    			long index = line.getIndex();
					if (line.getPc() == pc) {
						try {
							IRegion lineInfo = document.getLineInformation((int)index);
							
				            document.replace(lineInfo.getOffset(), 0, fnopString);
							inputSection.addFnop(fnopString, pc, index, getInput());
							fnopsAdded = true;
						} catch (BadLocationException e) {
							e.printStackTrace();
						}
					} else {
						lineNum++;
					}
				}
    		}
		}
	}
    
	protected void editorContextMenuAboutToShow(IMenuManager menu) {
		if (DEBUG) System.out.printf("XtaDisassemblyEditor: editorContextMenuAboutToShow\n"); 
     	super.editorContextMenuAboutToShow(menu);

     	menu.add(new Separator(ITextEditorActionConstants.GROUP_REST));     	
    	addAction(menu, ExpandAllID);
    	addAction(menu, CollapseAllID);
    }
	
	boolean showCodePoint(long pc) {
		IDocument document = getDocument();
		InputLine line = getInput().getPcsToLines().get(pc);
		if (line != null) {
			try {
				int charOffset = document.getLineOffset((int)line.getIndex());
				showLocation(charOffset, line.getIndex(), true);
				return true;
			} catch (BadLocationException e) {}
		}
		return false;
	}

    boolean updateAnnotations(long pc, String type, List<Annotation> annotations, 
    		Map<Annotation, Position> annotationPositions, boolean excludeFnops, boolean excludeSections) {
    	
    	boolean hitSection = false;
    	
    	InputLine line = getInput().getPcsToLines().get(pc);
    	if (line == null) 
    		return hitSection;
    	
    	if (!excludeSections) {
			InputSection inputSection = getInput().getPcsToSection().get(pc);
			if (inputSection != null) {
				addAnnotation(inputSection.getIndex(), type, annotations, annotationPositions);
				hitSection = true;
			}
    	}
    	
		InputLine inputLine = getInput().getPcsToLines().get(pc);
		if (inputLine != null) {
			addAnnotation(line.getIndex(), type, annotations, annotationPositions);
		}
		
		if (!excludeFnops){
			for (InputSection section : getSections()){
				for (InputLine fnop : section.getFnops()){
					if (fnop.getPc() == pc) { 
						addAnnotation(fnop.getIndex(), type, annotations, annotationPositions);
					}
				}
			}
		}
		return hitSection;
    }
}
