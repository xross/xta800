package com.xmos.xta.core.engine.utils;

import com.xmos.libs.core.xeloader.XeLoader.Architecture;
import com.xmos.libs.core.xnloader.XmlHandler;
import com.xmos.xta.core.engine.Config.ConfigTaskPriority;

public class CoreConfig {
    public int m_numThreads;
    public int m_numHighPriorityTasks;
    public double m_frequency;
    public int m_pipeLine;
    public ConfigTaskPriority m_taskPriority;

    public static final int MAX_THREADS = 8;
    public static final int DEFAULT_THREADS = 8;
    public static final int DEFAULT_HIGHPRIORITYTASKS = 0;
    public static final ConfigTaskPriority DEFAULT_TASKPRIORITY = ConfigTaskPriority.LOW;
    public static final double DEFAULT_FREQUENCY_MHZ = XmlHandler.DEFAULT_FREQUENCY_HZ / 1000000;

    public CoreConfig(int numThreads, int numHighPriorityTasks, double frequency, Architecture arch, ConfigTaskPriority taskPriority) {
        m_numThreads = numThreads;
        m_numHighPriorityTasks = numHighPriorityTasks;
        m_frequency = frequency;
        m_pipeLine = (arch == Architecture.XS2A)? 5 : 4;
        m_taskPriority = taskPriority;
    }
}
