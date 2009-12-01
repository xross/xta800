package com.xmos.xta.ui.views.info.exclusions;

import java.util.Set;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPartSite;

import com.xmos.xta.core.engine.references.Reference;
import com.xmos.xta.ui.GuiEngine;
import com.xmos.xta.ui.views.info.BaseViewer;
import com.xmos.xta.ui.views.info.BaseViewerContentProvider;
import com.xmos.xta.ui.views.info.BaseViewerLabelProvider;

public class ExclusionsViewer extends BaseViewer {
	private Action m_removeExclusionAction;

	public ExclusionsViewer(TableViewer viewer, IWorkbenchPartSite site) {
		super(viewer);
		
		getViewer().addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				ISelection selection = event.getSelection();

				if (selection.isEmpty()) {
					m_removeExclusionAction.setEnabled(false);
				} else {
					m_removeExclusionAction.setEnabled(true);
				}
			}			
		});
		
		getViewer().getTable().addKeyListener(new KeyListener() {
			public void keyPressed(KeyEvent e) {
				if (e.character == SWT.DEL) {
					removeSelection();
				}
			}

			public void keyReleased(KeyEvent e) {
			}
		});

		// Create the context menu
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(false);
		Menu menu = menuMgr.createContextMenu(getViewer().getControl());
		getViewer().getControl().setMenu(menu);
		site.registerContextMenu(menuMgr, getViewer());		
		createContextMenuActions(menuMgr);	
		menuMgr.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}

	protected BaseViewerContentProvider createContentProvider() {
		return new ExclusionsViewerContentProvider();
	}
	
	protected BaseViewerLabelProvider createLabelProvider() {
		return new ExclusionsViewerLabelProvider();
	}

 	void createContextMenuActions(MenuManager menuMgr) {
		// Create the 'Set Instruction Time' action
 		m_removeExclusionAction = new Action() {
			public void run() {
				removeSelection();
			}
		}; 
		m_removeExclusionAction.setText("Remove");
		menuMgr.add(m_removeExclusionAction);
		m_removeExclusionAction.setEnabled(false);
	} 		

 	private void removeSelection() {
		ISelection selection = getViewer().getSelection(); 
		for (Object object : ((IStructuredSelection)selection).toList()) {
			Reference ref = (Reference)object;			
			GuiEngine.getEngine().removeExclusion(GuiEngine.s_guiSrc, ref.getRef());
		}
	}

 	@Override
	protected Set<Long> getPcs(Object object) {
		return ((Reference)object).getPcs();
	}
}
