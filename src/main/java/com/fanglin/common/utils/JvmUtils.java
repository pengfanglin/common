package com.fanglin.common.utils;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.lang.management.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * jvm状态
 *
 * @author 彭方林
 * @version 1.0
 * @date 2019/8/29 19:31
 **/
public class JvmUtils {

    public OsInfo osInfo() {
        OperatingSystemMXBean osb = ManagementFactory.getOperatingSystemMXBean();
        return new OsInfo(
            osb.getName(),
            osb.getArch(),
            osb.getAvailableProcessors(),
            osb.getVersion(),
            osb.getSystemLoadAverage()
        );
    }

    public ProcessInfo processInfo() {
        RuntimeMXBean mxb = ManagementFactory.getRuntimeMXBean();
        return new ProcessInfo(
            mxb.getName(),
            mxb.getVmName(),
            mxb.getVmVendor(),
            mxb.getVmVersion(),
            mxb.getSpecName(),
            mxb.getSpecVendor(),
            mxb.getSpecVersion(),
            mxb.getManagementSpecVersion(),
            mxb.getClassPath(),
            mxb.getLibraryPath(),
            mxb.isBootClassPathSupported(),
            mxb.getBootClassPath(),
            mxb.getInputArguments(),
            mxb.getUptime() / 1000,
            TimeUtils.getSimpleDateFormat().format(new Date()),
            mxb.getSystemProperties()
        );
    }

    public MemoryInfo memoryInfo() {
        MemoryMXBean mxb = ManagementFactory.getMemoryMXBean();
        return new MemoryInfo(
            mxb.getObjectPendingFinalizationCount(),
            conversionMemoryUsage(mxb.getHeapMemoryUsage()),
            conversionMemoryUsage(mxb.getNonHeapMemoryUsage())
        );
    }

    private String formatMemorySize(long size) {
        return size / 1024 / 1024 + "MB";
    }

    private MemoryUsage conversionMemoryUsage(java.lang.management.MemoryUsage memoryUsage) {
        return new MemoryUsage(
            formatMemorySize(memoryUsage.getInit()),
            formatMemorySize(memoryUsage.getUsed()),
            formatMemorySize(memoryUsage.getCommitted()),
            formatMemorySize(memoryUsage.getMax())
        );
    }

    public List<MemoryPoolInfo> memoryPoolInfo() {
        List<MemoryPoolMXBean> pools = ManagementFactory.getMemoryPoolMXBeans();
        List<MemoryPoolInfo> memoryPoolInfos = new ArrayList<>(pools.size());
        for (MemoryPoolMXBean pool : pools) {
            memoryPoolInfos.add(
                new MemoryPoolInfo(
                    pool.getName(),
                    pool.getType().name(),
                    conversionMemoryUsage(pool.getUsage()),
                    conversionMemoryUsage(pool.getPeakUsage()),
                    pool.isValid(),
                    pool.getMemoryManagerNames(),
                    pool.getUsageThreshold(),
                    pool.isUsageThresholdExceeded(),
                    pool.getUsageThresholdCount(),
                    pool.isUsageThresholdSupported(),
                    pool.getCollectionUsageThreshold(),
                    pool.isCollectionUsageThresholdExceeded(),
                    pool.getCollectionUsageThresholdCount(),
                    conversionMemoryUsage(pool.getCollectionUsage()),
                    pool.isCollectionUsageThresholdSupported()
                )
            );
        }
        return memoryPoolInfos;
    }

    public ThreadInfo threadInfo() {
        ThreadMXBean tmx = ManagementFactory.getThreadMXBean();
        System.out.println("thread count:" + tmx.getThreadCount());
        for (long id : tmx.getAllThreadIds()) {
            ThreadInfo ti = tmx.getThreadInfo(id);
            System.out.println(ti.toString().trim());
            System.out.println("cpu time:" + tmx.getThreadCpuTime(id));
            System.out.println("user time:" + tmx.getThreadUserTime(id));
            System.out.println("-----------------");
        }
        System.out.println("findDeadlockedThreads:");
        if (tmx.findDeadlockedThreads() != null)
            for (long id : tmx.findDeadlockedThreads()) {
                ThreadInfo ti = tmx.getThreadInfo(id);
                System.out.println(ti.toString().trim());
            }
        return new ThreadInfo(
            tmx.getThreadCount()
        );
    }
}

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ApiModel("进程信息")
class ProcessInfo {
    @ApiModelProperty("进程号")
    private String name;
    @ApiModelProperty("虚拟机名称")
    private String vmName;
    @ApiModelProperty("虚拟机提供商")
    private String vmVendor;
    @ApiModelProperty("虚拟机版本")
    private String vmVersion;
    @ApiModelProperty("虚拟机规范")
    private String specName;
    @ApiModelProperty("虚拟机规范提供商")
    private String specVendor;
    @ApiModelProperty("虚拟机规范版本")
    private String specVersion;
    @ApiModelProperty("管理器规范版本")
    private String managementSpecVersion;
    @ApiModelProperty("类路径")
    private String classPath;
    @ApiModelProperty("库文件路径")
    private String libraryPath;
    @ApiModelProperty("是否支持引导类路径")
    private boolean bootClassPathSupported;
    @ApiModelProperty("引导类路径")
    private String bootClassPath;
    @ApiModelProperty("输入参数")
    private List<String> inputArguments;
    @ApiModelProperty("运行时间(秒)")
    private long uptime;
    @ApiModelProperty("启动时间")
    private String startTime;
    @ApiModelProperty("系统参数")
    private Map<String, String> systemProperties;
}

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ApiModel("操作系统信息")
class OsInfo {
    @ApiModelProperty("操作系统")
    private String name;
    @ApiModelProperty("体系结构")
    private String arch;
    @ApiModelProperty("处理器数目")
    private int availableProcessors;
    @ApiModelProperty("版本号")
    private String version;
    @ApiModelProperty("平均负载")
    private double systemLoadAverage;
}

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ApiModel("操作系统信息")
class MemoryInfo {
    @ApiModelProperty("对象挂起的终结计数")
    int objectPendingFinalizationCount;
    @ApiModelProperty("堆内存使用量")
    MemoryUsage heap;
    @ApiModelProperty("非堆内存使用量")
    MemoryUsage nonHeap;
}

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ApiModel("内存使用情况")
class MemoryUsage {
    @ApiModelProperty("初始化")
    private String init;
    @ApiModelProperty("已使用")
    private String used;
    @ApiModelProperty("已提交")
    private String committed;
    @ApiModelProperty("最大内存")
    private String max;
}

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ApiModel("线程信息")
class ThreadInfo {
    @ApiModelProperty("线程数量")
    int getThreadCount();

    @ApiModelProperty("峰值线程数量")
    int getPeakThreadCount();

    @ApiModelProperty("总启动线程数")
    long getTotalStartedThreadCount();

    @ApiModelProperty("守护线程数")
    int getDaemonThreadCount();

    @ApiModelProperty("所有线程id")
    long[] getAllThreadIds();

    @ApiModelProperty("最大内存")
    java.lang.management.ThreadInfo getThreadInfo(long var1);

    @ApiModelProperty("最大内存")
    java.lang.management.ThreadInfo[] getThreadInfo(long[] var1);

    @ApiModelProperty("最大内存")
    java.lang.management.ThreadInfo getThreadInfo(long var1, int var3);

    @ApiModelProperty("最大内存")
    java.lang.management.ThreadInfo[] getThreadInfo(long[] var1, int var2);

    @ApiModelProperty("最大内存")
    boolean isThreadContentionMonitoringSupported();

    @ApiModelProperty("最大内存")
    boolean isThreadContentionMonitoringEnabled();

    @ApiModelProperty("最大内存")
    void setThreadContentionMonitoringEnabled(boolean var1);

    @ApiModelProperty("最大内存")
    long getCurrentThreadCpuTime();

    @ApiModelProperty("最大内存")
    long getCurrentThreadUserTime();

    @ApiModelProperty("最大内存")
    long getThreadCpuTime(long var1);

    @ApiModelProperty("最大内存")
    long getThreadUserTime(long var1);

    @ApiModelProperty("最大内存")
    boolean isThreadCpuTimeSupported();

    @ApiModelProperty("最大内存")
    boolean isCurrentThreadCpuTimeSupported();

    @ApiModelProperty("最大内存")
    boolean isThreadCpuTimeEnabled();

    @ApiModelProperty("最大内存")
    void setThreadCpuTimeEnabled(boolean var1);

    @ApiModelProperty("最大内存")
    long[] findMonitorDeadlockedThreads();

    @ApiModelProperty("最大内存")
    void resetPeakThreadCount();

    @ApiModelProperty("最大内存")
    long[] findDeadlockedThreads();

    @ApiModelProperty("最大内存")
    boolean isObjectMonitorUsageSupported();

    @ApiModelProperty("最大内存")
    boolean isSynchronizerUsageSupported();

    @ApiModelProperty("最大内存")
    java.lang.management.ThreadInfo[] getThreadInfo(long[] var1, boolean var2, boolean var3);

    @ApiModelProperty("最大内存")
    java.lang.management.ThreadInfo[] dumpAllThreads(boolean var1, boolean var2);
}

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ApiModel("线程详细信息")
class ThreadDetail {
    @ApiModelProperty("线程名")
    private String threadName;
    @ApiModelProperty("线程id")
    private long threadId;
    @ApiModelProperty("阻塞时间")
    private long blockedTime;
    @ApiModelProperty("阻塞次数")
    private long blockedCount;
    @ApiModelProperty("等待时间")
    private long waitedTime;
    @ApiModelProperty("等待次数")
    private long waitedCount;
    @ApiModelProperty("锁信息")
    private LockInfo lock;
    @ApiModelProperty("锁名称")
    private String lockName;
    @ApiModelProperty("锁的持有者Id")
    private long lockOwnerId;
    @ApiModelProperty("锁的持有者名称")
    private String lockOwnerName;
    @ApiModelProperty("是否天生")
    private boolean inNative;
    @ApiModelProperty("是否暂停过")
    private boolean suspended;
    @ApiModelProperty("线程状态")
    private String threadState;
    @ApiModelProperty("锁监控器")
    private MonitorInfo[] lockedMonitors;
    @ApiModelProperty("锁同步器")
    private LockInfo[] lockedSynchronizers;
}

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ApiModel("堆栈跟踪元素")
class StackTraceElement {
    @ApiModelProperty("声明类")
    private String declaringClass;
    @ApiModelProperty("方法名")
    private String methodName;
    @ApiModelProperty("文件名")
    private String fileName;
    @ApiModelProperty("行号")
    private int lineNumber;
}

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ApiModel("锁信息")
class LockInfo {
    @ApiModelProperty("类名称")
    private String className;
    @ApiModelProperty("身份哈希码")
    private int identityHashCode;
}

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ApiModel("监视器信息")
class MonitorInfo {
    @ApiModelProperty("堆栈深度")
    private int stackDepth;
    @ApiModelProperty("堆栈帧")
    private StackTraceElement stackFrame;
}

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ApiModel("内存池信息")
class MemoryPoolInfo {
    @ApiModelProperty("内存池名称")
    private String name;
    @ApiModelProperty("初始化")
    private String type;
    @ApiModelProperty("内存使用量")
    private MemoryUsage usage;
    @ApiModelProperty("峰值内存使用量")
    private MemoryUsage peakUsage;
    @ApiModelProperty("是否有效")
    private boolean valid;
    @ApiModelProperty("内存管理器名称")
    private String[] memoryManagerNames;
    @ApiModelProperty("使用阈值")
    private long usageThreshold;
    @ApiModelProperty("是否超过使用阈值")
    private boolean usageThresholdExceeded;
    @ApiModelProperty("使用阈值计数")
    private long usageThresholdCount;
    @ApiModelProperty("是否支持使用阈值")
    private boolean usageThresholdSupported;
    @ApiModelProperty("集合使用阈值")
    private long collectionUsageThreshold;
    @ApiModelProperty("是否超过集合使用阈值")
    private boolean collectionUsageThresholdExceeded;
    @ApiModelProperty("集合使用阈值计数")
    private long collectionUsageThresholdCount;
    @ApiModelProperty("集合内存使用情况")
    private MemoryUsage collectionUsage;
    @ApiModelProperty("是否支持集合使用阈值")
    private boolean collectionUsageThresholdSupported;
}
}
