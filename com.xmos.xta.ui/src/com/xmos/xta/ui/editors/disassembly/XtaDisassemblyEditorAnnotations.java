package com.xmos.xta.ui.editors.disassembly;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.TreeSet;
import java.util.Vector;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.BadPositionCategoryException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.projection.ProjectionAnnotation;
import org.eclipse.jface.text.source.projection.ProjectionAnnotationModel;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.ui.IEditorInput;

import com.xmos.xta.core.engine.Notifier;
import com.xmos.xta.core.engine.analysis.structural.nodes.InstructionNode;
import com.xmos.xta.core.engine.analysis.structural.nodes.Node;
import com.xmos.xta.core.engine.analysis.structural.nodes.traversers.DefaultNodeTraverser;
import com.xmos.xta.core.engine.instructions.XtaInstruction;
import com.xmos.xta.core.engine.references.Reference;
import com.xmos.xta.core.engine.sourcemapper.AssemblyLine;
import com.xmos.xta.ui.Broadcaster;
import com.xmos.xta.ui.GuiEngine;
import com.xmos.xta.ui.editors.Input;
import com.xmos.xta.ui.editors.InputLine;
import com.xmos.xta.ui.editors.InputSection;
import com.xmos.xta.ui.editors.XtaAnnotatedEditor;
import com.xmos.xta.ui.editors.XtaDocumentPartitioner;
import com.xmos.xta.ui.editors.XtaEditorAnnotations;

public class XtaDisassemblyEditorAnnotations extends XtaEditorAnnotations {
    protected static final String fnopString = AssemblyLine.getLeadingWhiteSpace() + " FNOP\n";

    private Annotation[] m_previousProjectionAnnotations;
	protected Set<XtaInstruction> m_previousFnops;
    
    public XtaDisassemblyEditorAnnotations(XtaAnnotatedEditor editor) {
    	super(editor);
        m_previousFnops = new TreeSet<XtaInstruction>();
    }

    public void dispose() {
	    GuiEngine.closeDisassembly();
    	super.dispose();
    }
    
	public void propertyChange(PropertyChangeEvent event) {
		if (DEBUG) System.out.printf("XtaDisassemblyEditorAnnotations: propertyChange: %s\n", event.getProperty());	
		if (m_isDisposed)
			return;

		if (event.getProperty().equals(Broadcaster.BROADCAST_SHOW_CODE_POINT)) {
			Reference codePoint = GuiEngine.getCodePoint();
	    	if (codePoint == null || !codePoint.getResolved()) {
	    		return;
	    	}
			showCodePoint(codePoint.getPc());
			
		}  else if (event.getProperty().equals(Notifier.NOTIFY_SET_NODE_CORE)) {
			refreshInput();

		}  else if (event.getProperty().equals(Broadcaster.BROADCAST_SET_SOURCE_PCS)) {
			updateHighlighting();
			if (GuiEngine.showSourcePcsPosition()) {
				List<Long> pcs = GuiEngine.getSourcePcs();
				if (pcs.size() > 0) {
					showCodePoint(pcs.get(0));
				}
			}
		}  else if (event.getProperty().equals(Broadcaster.BROADCAST_OPEN_DISASSEMBLY)) {
			m_selectedNodes = GuiEngine.getSelectedNodes();
			updateFoldingStructure();
			updateAll();
	        collapseFolding();
		}
		
		super.propertyChange(event);
	}

	public void updateAll() {
		if (DEBUG) System.out.printf("XtaDisassemblyEditorAnnotations: updateAll\n");
		updateFnops();
		updateUnknowns();
		updateHighlighting();
	}
    
	public String getLineDescription(long index) {
		return lineIndexToString(index);
	}

	public boolean isValidEndPoint(long lineNumber) {
		if (isSection(lineNumber - 1))
			return false;
		else
			return true;
	}
		
	public void updateAnnotations(List<Annotation> annotations, HashMap<Annotation, Position> positions) {
		ArrayList<Annotation> allAnnotations = new ArrayList<Annotation>(annotations);
		final HashMap<Annotation, Position> allPositions = new HashMap<Annotation, Position>(positions);
		
		Set<Long> pcs = new HashSet<Long>();
		pcs.addAll(GuiEngine.getSourcePcs());
        for (long pc : pcs) {
        	addAnnotationsForPc(pc, SourcePcsAnnotationID, allAnnotations, allPositions);
        } 

        super.updateAnnotations(allAnnotations, allPositions);
	}

    public boolean addAnnotationsForPc(long pc, String type, List<Annotation> annotations, 
    		Map<Annotation, Position> annotationPositions) {
    	
    	InputLine line = getInput().getPcsToLines().get(pc);
    	if (line == null) 
    		return false;
    	
		addAnnotationForIndex(line.getIndex(), type, annotations, annotationPositions);
		
		// Don't apply source highlighting to sections
		if (!type.equals(SourcePcsAnnotationID)) {
			InputSection inputSection = getInput().getPcsToSection().get(pc);
			if (inputSection != null) {
				addAnnotationForIndex(inputSection.getIndex(), type, annotations, annotationPositions);
			}
		}
    	
		// Only annotate FNOPs with highlights
		if (type.equals(HighlightAnnotationID)) {
			for (InputSection section : getSections()) {
				for (InputLine fnop : section.getFnops()) {
					if (fnop.getPc() == pc) { 
						addAnnotationForIndex(fnop.getIndex(), type, annotations, annotationPositions);
					}
				}
			}
		}
		return true;
    }
    
	public long getIndexForRef(Reference ref) {
		if (ref != null) {
			if (ref.getResolved()) {
			    long pc = ref.getPc();
			    return getIndexForPc(pc);
			}			
		}
		return -1;
	}

    public long getIndexForPc(long pc) {
		InputLine line = getInput().getPcsToLines().get(pc);
		if (line != null) {
			return line.getIndex();
		}
		return -1;
	}

    public String lineIndexToString(long index) {
    	for (InputSection inputSection : getSections()) {
    		long startLine = inputSection.getIndex();
    		long endLine = startLine + inputSection.getLines().size();
    		long offset = index - startLine;

    		// Don't map to the header
    		if ((index > startLine) && (index <= endLine)) {
    			if (!inputSection.isFnop(offset - 1)) {
    				return inputSection.getLine(offset - 1).getCodeReference();
    			} else {
    				return null;
    			}
			}
    	}
		return null;
	}

	public Collection<Long> getPcsForLine(long lineNumber) {
		TreeSet<Long> pcs = new TreeSet<Long>();
		HashMap<Long, InputLine> lineMap = getInput().getPcsToLines();
		Iterator<Map.Entry<Long, InputLine>> lineIter = lineMap.entrySet().iterator();
		while (lineIter.hasNext()) {
    	    Map.Entry<Long, InputLine> pair = lineIter.next();
    	    if (pair.getValue().getIndex() == lineNumber) {
    	    	pcs.add(pair.getKey());
    	    }                           
    	}      

		HashMap<Long, InputSection> sectionMap = getInput().getPcsToSection();
		Iterator<Map.Entry<Long, InputSection>> sectionIter = sectionMap.entrySet().iterator();
		while (sectionIter.hasNext()) {
    	    Map.Entry<Long, InputSection> pair = sectionIter.next();
    	    if (pair.getValue().getIndex() == lineNumber) {
    	    	pcs.add(pair.getKey());
    	    }                           
    	}      
		return pcs;
	}

    public boolean canAnnotate() {
		return true;
	}
	
	private void refreshInput() {
		if (DEBUG) System.out.printf("XtaDisassemblyEditorAnnotations: refreshInput\n");
		IEditorInput input = GuiEngine.getDisassemblyInput();
		m_editor.refreshInput(input);
        m_previousProjectionAnnotations = new Annotation[0];
        updateFoldingStructure();
        collapseFolding();
	}
	
    private void updateFnops() {
		if (DEBUG) System.out.printf("XtaDisassemblyEditorAnnotations: updateFnops\n"); 
		
    	Set<XtaInstruction> toAdd = new TreeSet<XtaInstruction>();
    	Set<XtaInstruction> toRemove;
    	Set<XtaInstruction> toKeep  = new TreeSet<XtaInstruction>();
    	
   		final Collection<XtaInstruction> fnops = new Vector<XtaInstruction>();
   		for (Node node : m_selectedNodes) {
       		node.traverse(new Stack<Node>(), new DefaultNodeTraverser() {
       		    public TraverseStatus foundInstructionNode(Node node, Stack<Node> ancestors) {
    				XtaInstruction instruction = ((InstructionNode)node).getXtaInstruction();				
       		    	if (instruction.isFnop()) {   		    		
       	   		    	fnops.add(instruction);
       		    	}
       		    	return TraverseStatus.CONTINUE;
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
    	
	    ((InputDisassembly)m_editor.getEditorInput()).updateLineNumberMapping();
	}
        
    private void removeFnop(long pc) {
		int fnopsRemoved = 0;
		IDocument document = m_editor.getDocument();
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

    	IDocument document = m_editor.getDocument();    	
    	for (InputSection inputSection : getSections()) {
    		if (fnopsAdded) {
    			inputSection.incrementIndex();
    		} else {
	    		for (InputLine inputLine : inputSection.getLines()) {
					if (inputLine.getPc() == pc) {
						try {
			    			long index = inputLine.getIndex();
							IRegion lineInfo = document.getLineInformation((int)index);
				            document.replace(lineInfo.getOffset(), 0, fnopString);
							inputSection.addFnop(fnopString, pc, index);
							fnopsAdded = true;
							break;
						} catch (BadLocationException e) {
							e.printStackTrace();
						}
					}
	    		}
    		}
		}
	}
	
	private Input getInput() {
		IDocument document = m_editor.getDocument();
		if (document == null) {
			return null;
		}
		
		XtaDocumentPartitioner xtaDocumentPartitioner =
			(XtaDocumentPartitioner)document.getDocumentPartitioner();

		return xtaDocumentPartitioner.getInput();		
	}
	
	private ArrayList<InputSection> getSections() {
		IDocument document = m_editor.getDocument();
		if (document == null)
			return new ArrayList<InputSection>();
		
		XtaDocumentPartitioner xtaDocumentPartitioner =
			(XtaDocumentPartitioner)document.getDocumentPartitioner();

		return xtaDocumentPartitioner.getInput().getSections();
	}

    private boolean isSection(long index) {
    	for (InputSection inputSection : getSections()) {
    		long sectionIndex = inputSection.getIndex();
			if (sectionIndex == index) {
    			return true;
    		} else if (sectionIndex > index) {
    			return false;
    		}
    	}
		return false;
	}

	private ProjectionAnnotationModel updateFoldingStructure() {
	    if (DEBUG) System.out.printf("XtaDisassemblyEditorAnnotations: updateFoldingStructure\n"); 

	    IDocument document = m_editor.getDocument();
	    ArrayList<Position> positions = new ArrayList<Position>();
		try {
			for (Position position : document.getPositions(XtaDocumentPartitioner.SECTION)) {
				positions.add(position);
			}
		} catch (BadPositionCategoryException e) {
			e.printStackTrace();
		}

	    Annotation[] annotations = new Annotation[positions.size()];
        HashMap<ProjectionAnnotation, Position> annotationPositions = 
    	    new HashMap<ProjectionAnnotation, Position>();

        for (int i = 0; i < positions.size(); i++) {
            ProjectionAnnotation annotation = new ProjectionAnnotation();
            annotationPositions.put(annotation, positions.get(i));
            annotations[i] = annotation;
        }
       
        ProjectionAnnotationModel model = m_editor.getProjectionAnnotationModel();
        model.modifyAnnotations(m_previousProjectionAnnotations, annotationPositions, null);
        m_previousProjectionAnnotations = annotations;        
        return model;
    }

	private void collapseFolding() {
        ProjectionAnnotationModel model = m_editor.getProjectionAnnotationModel();
        model.collapseAll(0, m_editor.getDocument().getLength());
	}
}
