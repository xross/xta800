package com.xmos.xta.core.engine.utils;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryNotificationInfo;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.MemoryType;
import java.util.ArrayList;
import java.util.Collection;

import javax.management.Notification;
import javax.management.NotificationEmitter;
import javax.management.NotificationListener;

public class HeapUsageMonitor {
    private static final MemoryPoolMXBean tenuredGenPool = findTenuredGenPool();
    private static double m_usageThreashold;
    
    private final Collection<Listener> listeners = new ArrayList<Listener>();

    public static void setUsageThreshold(double threshold) throws Exception {
	    if (threshold <= 0.0 || threshold > 1.0) {
	      throw new Exception("threshold not in range (0.0 - 1.0)");
	    }
	    m_usageThreashold = threshold;
	    long maxMemory = tenuredGenPool.getUsage().getMax();
	    long warningThreshold = (long) (maxMemory * threshold);
	    tenuredGenPool.setUsageThreshold(warningThreshold);
    }

    public static double getUsageThreshold() {
    	return m_usageThreashold;
    }    
    
    public interface Listener {
        public void memoryUsageLow(long usedMemory, long maxMemory);
    }

    public HeapUsageMonitor() {
        MemoryMXBean mbean = ManagementFactory.getMemoryMXBean();
        NotificationEmitter emitter = (NotificationEmitter) mbean;
    
        emitter.addNotificationListener(new NotificationListener() {
            public void handleNotification(Notification n, Object hb) {    	  
                if (n.getType().equals(MemoryNotificationInfo.MEMORY_THRESHOLD_EXCEEDED)) {
                    long maxMemory = tenuredGenPool.getUsage().getMax();
                    long usedMemory = tenuredGenPool.getUsage().getUsed();
                    for (Listener listener : listeners) {
                        listener.memoryUsageLow(usedMemory, maxMemory);
                    }
                }
            }
        }, null, null);
    }

    public boolean addListener(Listener listener) {
        return listeners.add(listener);
    }

	public boolean removeListener(Listener listener) {
	    return listeners.remove(listener);
    }

    //
    // Tenured Space Pool can be determined by it being of type
    // HEAP and by it being possible to set the usage threshold.
    //
    private static MemoryPoolMXBean findTenuredGenPool() {
        for (MemoryPoolMXBean pool :
            ManagementFactory.getMemoryPoolMXBeans()) {

            // I don't know whether this approach is better, or whether
            // we should rather check for the pool name "Tenured Gen"?
            if (pool.getType() == MemoryType.HEAP &&
                pool.isUsageThresholdSupported()) {
                return pool;
            }
        }
        throw new AssertionError("Could not find tenured space");
    }
}
  