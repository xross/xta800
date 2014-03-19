package com.xmos.xta.core.engine.sections;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.xmos.libs.core.utils.ByteArrayManipulation;
import com.xmos.libs.core.utils.CoreId;
import com.xmos.libs.core.utils.IteratableSet;
import com.xmos.libs.core.xeloader.XeLoader;
import com.xmos.xta.core.engine.CommandSource;
import com.xmos.xta.core.engine.Notifier;

public class SrcLabelTable extends Section {
    protected IteratableSet<SrcLabelTableInfo> m_locations;
    private String m_sectionName;
    private static final boolean Debug = false;

    SrcLabelTable(CoreId coreId, String sectionName, Notifier notifier) {
        super(coreId, notifier);
        m_locations = new IteratableSet<SrcLabelTableInfo>();
        m_sectionName = sectionName;
    }

    IteratableSet<SrcLabelTableInfo> get() {
        return m_locations;
    }

    boolean load(CommandSource src, XeLoader xeLoader) {
        byte[] data = xeLoader.getSectionData(m_sectionName, getCoreId());
        if(Debug) System.out.println("m_sectionName " + m_sectionName);
        if(Debug) System.out.println("data.length " + data.length);
        if (data.length == 0) {
            return true;
        }
        try {
            int byteIndex = 0;
            while (byteIndex < data.length) {
                int base = byteIndex;

                int size = ByteArrayManipulation.readInt(data, byteIndex);
                byteIndex += 4;

                // Ignores the version field.
                   ByteArrayManipulation.readInt(data, byteIndex);
                   byteIndex += 4;

                   String compilationDir = ByteArrayManipulation.readString(data, byteIndex);
                   byteIndex += compilationDir.length() + 1;

                   while (byteIndex < (base + size)) {
                    String fileName = ByteArrayManipulation.readString(data, byteIndex);
                    byteIndex += fileName.length() + 1;

                    int lineNumberStart = ByteArrayManipulation.readInt(data, byteIndex);
                    byteIndex += 4;

                    int lineNumberEnd = ByteArrayManipulation.readInt(data, byteIndex);
                    byteIndex += 4;

                    long location = ByteArrayManipulation.readInt(data, byteIndex);
                    byteIndex += 4;

                    m_locations.add(new SrcLabelTableInfo(this, location, compilationDir,
                            fileName, lineNumberStart, lineNumberEnd));
                }
            }
        } catch (Exception e) {
            getNotifier().notify(src, Notifier.NOTIFY_ERROR, String.format(
                    "problem reading %s section while loading executable", m_sectionName));
            return false;
        }

        // BUG 6471: A compiler bug means that basic blocks overlap when they shouldn't
        // Here we ensure this overlapping is removed as the rest of the tool assumes the
        // blocks don't overlap.
        detectAndCorrectOverlap();

        return true;
    }

    protected void detectAndCorrectOverlap() {
        List<SrcLabelTableInfo> entries = new ArrayList<SrcLabelTableInfo>();
        //entries.addAll(m_locations.values());
        Collections.sort(entries);

        int index = 0;
        while (index < entries.size()) {
            index = detectForFile(entries, index);
        }
    }

    private int detectForFile(List<SrcLabelTableInfo> entries, int index) {
        SrcLabelTableInfo entry = entries.get(index);
        String thisFile = entry.getFileName();

        index++;
        while (index < entries.size()) {
            SrcLabelTableInfo nextEntry = entries.get(index);
            if (!nextEntry.getFileName().equals(thisFile)) {
                return index;
            } else {
                if (entry.getLineNumberEnd() > nextEntry.getLineNumberStart()) {
                    entry.setLineNumberEnd(nextEntry.getLineNumberStart() - 1);
                }
            }
            entry = nextEntry;
            index++;
        }
        return index;
    }
}
