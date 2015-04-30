package com.xmos.xta.core.engine.utils;

import com.xmos.libs.core.xeloader.XeLoader.Architecture;
import com.xmos.libs.core.xnloader.XmlHandler;
import com.xmos.xta.core.engine.Config.ConfigThreadPriority;

public class CoreConfig {
    public int m_numThreads;
    public int m_numHighPriorityThreads;
    public double m_frequency;
    public int m_pipeLine;
    public ConfigThreadPriority m_threadPriority;

    public static final int MAX_THREADS = 8;
    public static final int DEFAULT_THREADS = 8;
    public static final int DEFAULT_HIGHPRIORITYTHREADS = 0;
    public static final ConfigThreadPriority DEFAULT_THREADPRIORITY = ConfigThreadPriority.LOW;
    public static final double DEFAULT_FREQUENCY_MHZ = XmlHandler.DEFAULT_FREQUENCY_HZ / 1000000;

    public CoreConfig(int numThreads, int numHighPriorityThreads, double frequency, Architecture arch, ConfigThreadPriority threadPriority) {
        m_numThreads = numThreads;
        m_numHighPriorityThreads = numHighPriorityThreads;
        m_frequency = frequency;
        m_pipeLine = (arch == Architecture.XS2A)? 5 : 4;
        m_threadPriority = threadPriority;
    }
}
