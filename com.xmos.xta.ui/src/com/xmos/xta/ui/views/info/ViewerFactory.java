package com.xmos.xta.ui.views.info;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IWorkbenchPartSite;

import com.xmos.xta.ui.views.info.assertions.AssertionsViewer;
import com.xmos.xta.ui.views.info.exclusions.ExclusionsViewer;
import com.xmos.xta.ui.views.info.log.LogViewer;
import com.xmos.xta.ui.views.info.sections.AllCallsViewer;
import com.xmos.xta.ui.views.info.sections.AllEndpointsViewer;
import com.xmos.xta.ui.views.info.sections.AllSrcLabelsViewer;
import com.xmos.xta.ui.views.info.sections.BranchesViewer;
import com.xmos.xta.ui.views.info.sections.CallsViewer;
import com.xmos.xta.ui.views.info.sections.EndpointsViewer;
import com.xmos.xta.ui.views.info.sections.LabelsViewer;
import com.xmos.xta.ui.views.info.sections.SourcesViewer;
import com.xmos.xta.ui.views.info.sections.SrcCommandsViewer;
import com.xmos.xta.ui.views.info.sections.SrcLabelsViewer;
import com.xmos.xta.ui.views.info.sections.SrcLoopsViewer;
import com.xmos.xta.ui.views.info.sections.SystemsViewer;
import com.xmos.xta.ui.views.info.sections.ThreadStartPointsViewer;
import com.xmos.xta.ui.views.info.sections.ThreadStopPointsViewer;
import com.xmos.xta.ui.views.info.unknowns.KnownsViewer;
import com.xmos.xta.ui.views.info.unknowns.UnknownsViewer;

public class ViewerFactory {
	private static final String LOG                 = "Log";
	private static final String UNKNOWNS            = "Unknowns";	
	private static final String KNOWNS              = "Knowns";		
	private static final String EXCLUSIONS          = "Exclusions";
	private static final String ASSERTIONS          = "Assertions";

	private static final String BINARY_INFO         = "Binary Info";
	private static final String ENDPOINTS           = "Endpoints";				
	private static final String ALL_ENDPOINTS       = "All Endpoints";		
	private static final String SRC_LABELS          = "SrcLabels";		
	private static final String ALL_SRC_LABELS      = "All SrcLabels";		
	private static final String CALLS               = "Calls";		
	private static final String ALL_CALLS           = "All Calls";		
	private static final String BRANCHES            = "Branches";	
	private static final String LABELS              = "Labels";	
	private static final String THREAD_START_POINTS = "Thread Start Points";	
	private static final String THREAD_STOP_POINTS  = "Thread Stop Points";
	private static final String SRC_LEVEL_COMMANDS  = "Src Level Commands";
	private static final String SRC_LEVEL_LOOPS     = "Src Level Loops";
	private static final String SOURCE_FILES        = "Source files";
	private static final String SYSTEM_FUNCTIONS    = "System functions";

	Viewer createDefaultViewer(Composite composite) {
    	return new LogViewer(createNewViewer(LOG, composite));
	}
	
	Viewer createViewer(String name, Composite composite, IWorkbenchPartSite site) {
		// Top level
	    if (name.equals(LOG) || name.equals("")) {
	    	return new LogViewer(createNewViewer(LOG, composite));				    	
	    } else if (name.equals(UNKNOWNS)) {
	    	return new UnknownsViewer(createNewViewer(UNKNOWNS, composite), composite, site);						
	    } else if (name.equals(KNOWNS)) {
	    	return new KnownsViewer(createNewViewer(KNOWNS, composite), composite, site);		
	    } else if (name.equals(EXCLUSIONS)) {
	    	return new ExclusionsViewer(createNewViewer(EXCLUSIONS, composite), site);		    
	    } else if (name.equals(ASSERTIONS)) {
	    	return new AssertionsViewer(createNewViewer(EXCLUSIONS, composite));
	    	
	    // Binary info
	    } else if (name.equals(BINARY_INFO)) {
	    	return new NullViewer(composite);	    	
	    } else if (name.equals(ENDPOINTS)) {
	    	return new EndpointsViewer(createNewViewer(ENDPOINTS, composite));	
	    } else if (name.equals(ALL_ENDPOINTS)) {
	    	return new AllEndpointsViewer(createNewViewer(ALL_ENDPOINTS, composite));
	    } else if (name.equals(SRC_LABELS)) {
	    	return new SrcLabelsViewer(createNewViewer(SRC_LABELS, composite));		
	    } else if (name.equals(ALL_SRC_LABELS)) {
	    	return new AllSrcLabelsViewer(createNewViewer(ALL_SRC_LABELS, composite));
	    } else if (name.equals(CALLS)) {
	    	return new CallsViewer(createNewViewer(CALLS, composite));		
	    } else if (name.equals(ALL_CALLS)) {
	    	return new AllCallsViewer(createNewViewer(ALL_CALLS, composite));	
	    } else if (name.equals(BRANCHES)) {
	    	return new BranchesViewer(createNewViewer(BRANCHES, composite));	
	    } else if (name.equals(LABELS)) {
	    	return new LabelsViewer(createNewViewer(LABELS, composite));	
	    } else if (name.equals(THREAD_START_POINTS)) {
	    	return new ThreadStartPointsViewer(createNewViewer(THREAD_START_POINTS, composite));	
	    } else if (name.equals(THREAD_STOP_POINTS)) {
	    	return new ThreadStopPointsViewer(createNewViewer(THREAD_STOP_POINTS, composite));
	    } else if (name.equals(SRC_LEVEL_COMMANDS)) {
	    	return new SrcCommandsViewer(createNewViewer(SRC_LEVEL_COMMANDS, composite));
	    } else if (name.equals(SRC_LEVEL_LOOPS)) {
	    	return new SrcLoopsViewer(createNewViewer(SRC_LEVEL_LOOPS, composite));
	    } else if (name.equals(SOURCE_FILES)) {
	    	return new SourcesViewer(createNewViewer(SOURCE_FILES, composite));
	    } else if (name.equals(SYSTEM_FUNCTIONS)) {
	    	return new SystemsViewer(createNewViewer(SYSTEM_FUNCTIONS, composite));
	    }
	    assert(false);
	    return null;
	}

	TreeItem initialiseTree(Tree tree) {
		TreeItem log = new TreeItem(tree, 0);
		log.setText(LOG);
		
		new TreeItem(tree, 0).setText(UNKNOWNS);
		new TreeItem(tree, 0).setText(KNOWNS);		
		new TreeItem(tree, 0).setText(EXCLUSIONS);
		new TreeItem(tree, 0).setText(ASSERTIONS);				
		
		TreeItem binaryInfo = new TreeItem(tree, 0);
		binaryInfo.setText(BINARY_INFO);		
		new TreeItem(binaryInfo, 0).setText(ENDPOINTS);				
		new TreeItem(binaryInfo, 0).setText(ALL_ENDPOINTS);		
		new TreeItem(binaryInfo, 0).setText(SRC_LABELS);		
		new TreeItem(binaryInfo, 0).setText(ALL_SRC_LABELS);		
		new TreeItem(binaryInfo, 0).setText(CALLS);		
		new TreeItem(binaryInfo, 0).setText(ALL_CALLS);		
		new TreeItem(binaryInfo, 0).setText(BRANCHES);	
		new TreeItem(binaryInfo, 0).setText(LABELS);	
		new TreeItem(binaryInfo, 0).setText(THREAD_START_POINTS);	
		new TreeItem(binaryInfo, 0).setText(THREAD_STOP_POINTS);
		new TreeItem(binaryInfo, 0).setText(SRC_LEVEL_COMMANDS);
		new TreeItem(binaryInfo, 0).setText(SRC_LEVEL_LOOPS);
		new TreeItem(binaryInfo, 0).setText(SOURCE_FILES);
		new TreeItem(binaryInfo, 0).setText(SYSTEM_FUNCTIONS);		

		return log;
	}

	private TableViewer createNewViewer(String displayText, Composite composite) {
		TableViewer viewer = new TableViewer(composite);
		Table table = viewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);					
		return viewer;
	}	
}
