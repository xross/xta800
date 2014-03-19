package com.xmos.xta.core.engine.sections;

import java.util.ArrayList;
import java.util.Collection;

import com.xmos.libs.core.instructiondecoder.Instruction;
import com.xmos.libs.core.utils.ByteArrayManipulation;
import com.xmos.libs.core.utils.CoreId;
import com.xmos.libs.core.xeloader.XeLoader;
import com.xmos.xta.core.engine.CommandSource;
import com.xmos.xta.core.engine.Notifier;

public class SrcLevelLoops extends Section {
    private Collection<SrcLevelLoopInfo> m_loops;

    SrcLevelLoops(CoreId coreId, Notifier notifier) {
        super(coreId, notifier);
        m_loops = new ArrayList<SrcLevelLoopInfo>();
    }

    Collection<SrcLevelLoopInfo> get() {
        return m_loops;
    }

    boolean load(CommandSource src, XeLoader xeLoader) {
        byte[] data = xeLoader.getSectionData(".xtaloop", getCoreId());
        try {
            int byteIndex = 0;
            while (byteIndex < data.length) {
                long pc = Instruction.makeLongPc(ByteArrayManipulation.readInt(data, byteIndex));
                byteIndex += 4;

                int numIterations = ByteArrayManipulation.readInt(data, byteIndex);
                byteIndex += 4;

                m_loops.add(new SrcLevelLoopInfo(this, pc, numIterations));
            }
        } catch (Exception e) {
            getNotifier().notify(src, Notifier.NOTIFY_ERROR,
                    "problem reading .xtaloop section while loading executable");
            return false;
        }
        return true;
    }
}
