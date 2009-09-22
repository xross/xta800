package com.xmos.xta.ui.editors;

import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.hyperlink.IHyperlink;

import com.xmos.xta.core.engine.Engine;
import com.xmos.xta.core.engine.references.Reference;
import com.xmos.xta.core.engine.references.ReferenceFactory;
import com.xmos.xta.core.engine.references.types.ReferenceTypeFactory.ReferenceClass;
import com.xmos.xta.ui.Broadcaster;
import com.xmos.xta.ui.GuiEngine;

class BranchTargetHyperlink implements IHyperlink {
	private String m_pcString;
	private IRegion m_region;

	public BranchTargetHyperlink(IRegion region, String pcString) {
		m_region = region;
		m_pcString = pcString;
	}
	
	public IRegion getHyperlinkRegion() {
		return m_region;
	}
	
	public void open() {
		Reference ref = ReferenceFactory.createReference(m_pcString, ReferenceClass.LABEL_REF);
		
		Engine engine = GuiEngine.getEngine();
		boolean resolved = ref.resolve(engine, engine.getNodeId(), engine.getCoreId());
		if (resolved) {
			GuiEngine.setCodePoint(ref);
			GuiEngine.broadcast(Broadcaster.BROADCAST_SHOW_CODE_POINT, "");
		}
	}
	
	public String getTypeLabel() {
		return null;
	}
	
	public String getHyperlinkText() {
		return null;
	}
}
