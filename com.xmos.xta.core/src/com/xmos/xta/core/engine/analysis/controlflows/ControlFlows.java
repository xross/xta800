package com.xmos.xta.core.engine.analysis.controlflows;

import com.xmos.libs.xeloader.XeLoader;
import com.xmos.xta.core.engine.CommandSource;
import com.xmos.xta.core.engine.Notifier;
import com.xmos.xta.core.engine.analysis.binary.InstructionDecoder;
import com.xmos.xta.core.engine.sections.Sections;
import com.xmos.xta.core.engine.utils.CoreCollection;
import com.xmos.xta.core.engine.utils.CoreId;

public class ControlFlows {
	private CoreCollection<ControlFlow> m_controlFlows;

	public ControlFlows() {
		m_controlFlows = new CoreCollection<ControlFlow>();
	}
	
	public ControlFlow getControlFlow(CoreId coreId) {
		return m_controlFlows.get(coreId);
	}
	
	public boolean initialise(CommandSource src, Notifier notifier, XeLoader xeLoader, 
			Sections sections, CoreCollection<InstructionDecoder> instructionDecoders) {
		
		notifier.notify(src, Notifier.HOLD_MESSAGES, "");
		
		CoreCollection<ControlFlow> controlFlows = new CoreCollection<ControlFlow>();
		
		for (int nodeChainIndex : xeLoader.getNodeChainIndexes()) {
			for (int coreNumber : xeLoader.getCoreNumbers(nodeChainIndex)) {
				CoreId coreId = new CoreId(xeLoader.getXnLoader(), nodeChainIndex, coreNumber);

				ControlFlow controlFlow = new ControlFlow();
				if (!controlFlow.initialise(src, notifier, coreId, xeLoader, sections, instructionDecoders.get(coreId))) {
					return false;
				}
				controlFlows.add(coreId, controlFlow);
			}
		}
		m_controlFlows = controlFlows;
		
		notifier.notify(src, Notifier.RELEASE_MESSAGES, "");
		return true;
	}		
}
