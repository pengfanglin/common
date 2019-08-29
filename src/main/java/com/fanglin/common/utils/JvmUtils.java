package com.fanglin.common.utils;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.lang.management.*;
import java.util.Date;
import java.util.List;

/**
 * jvm状态
 *
 * @author 彭方林
 * @version 1.0
 * @date 2019/8/29 19:31
 **/
public class JvmUtils {

    public static JvmInfo jvmInfo() {
        return new JvmInfo(
            osInfo(),
            memoryInfo(),
            memoryPoolInfo(),
            processInfo(),
            threadInfo()
        );
    }

    public static OsInfo osInfo() {
        OperatingSystemMXBean osb = ManagementFactory.getOperatingSystemMXBean();
        return new OsInfo(
            osb.getName(),
            osb.getArch(),
            osb.getAvailableProcessors(),
            osb.getVersion(),
            osb.getSystemLoadAverage()
        );
    }

    public static ProcessInfo processInfo() {
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
            buildUptime(mxb.getStartTime()),
            TimeUtils.getSimpleDateFormat().format(new Date(mxb.getStartTime()))
        );
    }

    public static MemoryInfo memoryInfo() {
        MemoryMXBean mxb = ManagementFactory.getMemoryMXBean();
        return new MemoryInfo(
            mxb.getObjectPendingFinalizationCount(),
            conversionMemoryUsage(mxb.getHeapMemoryUsage()),
            conversionMemoryUsage(mxb.getNonHeapMemoryUsage())
        );
    }

    private static String formatMemorySize(long size) {
        return size / 1024 / 1024 + "MB";
    }

    private static MemoryUsage conversionMemoryUsage(java.lang.management.MemoryUsage memoryUsage) {
        return new MemoryUsage(
            formatMemorySize(memoryUsage.getInit()),
            formatMemorySize(memoryUsage.getUsed()),
            formatMemorySize(memoryUsage.getCommitted()),
            formatMemorySize(memoryUsage.getMax())
        );
    }

    public static MemoryPoolInfo[] memoryPoolInfo() {
        List<MemoryPoolMXBean> pools = ManagementFactory.getMemoryPoolMXBeans();
        MemoryPoolInfo[] memoryPoolInfos = new MemoryPoolInfo[pools.size()];
        for (int i = 0; i < pools.size(); i++) {
            MemoryPoolMXBean pool = pools.get(i);
            boolean collectionUsageThresholdSupported = pool.isCollectionUsageThresholdSupported();
            memoryPoolInfos[i] = new MemoryPoolInfo(
                pool.getName(),
                pool.getType().name(),
                conversionMemoryUsage(pool.getUsage()),
                conversionMemoryUsage(pool.getPeakUsage()),
                pool.isValid(),
                pool.getMemoryManagerNames(),
                pool.isUsageThresholdSupported() ? pool.getUsageThreshold() : 0L,
                pool.isUsageThresholdSupported() && pool.isUsageThresholdExceeded(),
                pool.isUsageThresholdSupported() ? pool.getUsageThresholdCount() : 0,
                pool.isUsageThresholdSupported(),
                collectionUsageThresholdSupported ? pool.getCollectionUsageThreshold() : 0,
                collectionUsageThresholdSupported && pool.isCollectionUsageThresholdExceeded(),
                collectionUsageThresholdSupported ? pool.getCollectionUsageThresholdCount() : 0L,
                collectionUsageThresholdSupported ? conversionMemoryUsage(pool.getCollectionUsage()) : null,
                collectionUsageThresholdSupported
            );
        }
        return memoryPoolInfos;
    }

    public static ThreadInfo threadInfo() {
        ThreadMXBean tmx = ManagementFactory.getThreadMXBean();
        return new ThreadInfo(
            tmx.getThreadCount(),
            tmx.getPeakThreadCount(),
            tmx.getTotalStartedThreadCount(),
            tmx.getDaemonThreadCount(),
            tmx.getAllThreadIds(),
            tmx.isThreadContentionMonitoringSupported(),
            tmx.isThreadContentionMonitoringEnabled(),
            tmx.getCurrentThreadCpuTime(),
            tmx.getCurrentThreadUserTime(),
            tmx.isThreadCpuTimeSupported(),
            tmx.isCurrentThreadCpuTimeSupported(),
            tmx.isThreadCpuTimeEnabled(),
            tmx.findMonitorDeadlockedThreads(),
            tmx.findDeadlockedThreads(),
            tmx.isObjectMonitorUsageSupported(),
            tmx.isSynchronizerUsageSupported(),
            findThreadDetails(tmx, tmx.getAllThreadIds()),
            findThreadDetails(tmx, tmx.findDeadlockedThreads())
        );
    }

    private static ThreadDetail[] findThreadDetails(ThreadMXBean tmx, long[] ids) {
        if (ids == null || ids.length == 0) {
            return null;
        }
        ThreadDetail[] threadDetails = new ThreadDetail[ids.length];
        for (int i = 0; i < ids.length; i++) {
            long id = ids[i];
            java.lang.management.ThreadInfo threadInfo = tmx.getThreadInfo(id);
            ThreadDetail detail = new ThreadDetail(
                threadInfo.getThreadName(),
                threadInfo.getThreadId(),
                threadInfo.getBlockedTime(),
                threadInfo.getBlockedCount(),
                threadInfo.getWaitedTime(),
                threadInfo.getWaitedCount(),
                threadInfo.getLockInfo() != null ? new LockInfo(
                    threadInfo.getLockInfo().getClassName(),
                    threadInfo.getLockInfo().getIdentityHashCode()
                ) : null,
                threadInfo.getLockName(),
                threadInfo.getLockOwnerId(),
                threadInfo.getLockOwnerName(),
                threadInfo.isInNative(),
                threadInfo.isSuspended(),
                threadInfo.getThreadState().name(),
                buildMonitors(threadInfo.getLockedMonitors()),
                buildLockInfos(threadInfo.getLockedSynchronizers())
            );
            threadDetails[i] = detail;
        }
        return threadDetails;
    }

    private static MonitorInfo[] buildMonitors(java.lang.management.MonitorInfo[] monitors) {
        MonitorInfo[] monitorInfos = new MonitorInfo[monitors.length];
        for (int i = 0; i < monitors.length; i++) {
            java.lang.management.MonitorInfo monitor = monitors[i];
            monitorInfos[i] = new MonitorInfo(
                monitor.getLockedStackDepth(),
                buildStackTraceElement(monitor.getLockedStackFrame())
            );
        }
        return monitorInfos;
    }

    private static LockInfo[] buildLockInfos(java.lang.management.LockInfo[] locks) {
        LockInfo[] lockInfos = new LockInfo[locks.length];
        for (int i = 0; i < locks.length; i++) {
            java.lang.management.LockInfo lock = locks[i];
            lockInfos[i] = new LockInfo(
                lock.getClassName(),
                lock.getIdentityHashCode()
            );
        }
        return lockInfos;
    }

    private static StackTraceElement buildStackTraceElement(java.lang.StackTraceElement element) {
        return new StackTraceElement(
            element.getClassName(),
            element.getMethodName(),
            element.getFileName(),
            element.getLineNumber()
        );
    }

    private static String buildUptime(long time) {
        //两时间差,精确到毫秒
        long diff = System.currentTimeMillis() - time;
        long day = diff / 86400000;
        long hour = diff % 86400000 / 3600000;
        long min = diff % 86400000 % 3600000 / 60000;
        long seconds = diff % 86400000 % 3600000 % 60000 / 1000;
        return String.format("%s天%s小时%s分%s秒", day, hour, min, seconds);
    }

    @Setter
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @ApiModel("jvm信息")
    public static class JvmInfo {
        @ApiModelProperty("操作系统信息")
        OsInfo osInfo;
        @ApiModelProperty("内存信息")
        MemoryInfo memoryInfo;
        @ApiModelProperty("内存池信息")
        MemoryPoolInfo[] memoryPoolInfos;
        @ApiModelProperty("进程信息")
        ProcessInfo processInfo;
        @ApiModelProperty("线程信息")
        ThreadInfo threadInfo;
    }

    @Setter
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @ApiModel("线程信息")
    public static class ThreadInfo {
        @ApiModelProperty("线程数量")
        int threadCount;
        @ApiModelProperty("峰值线程数量")
        int peakThreadCount;
        @ApiModelProperty("总启动线程数")
        long totalStartedThreadCount;
        @ApiModelProperty("守护线程数")
        int daemonThreadCount;
        @ApiModelProperty("所有线程id")
        long[] allThreadIds;
        @ApiModelProperty("是否支持线程竞争监视")
        boolean threadContentionMonitoringSupported;
        @ApiModelProperty("是否启用线程争用监控")
        boolean threadContentionMonitoringEnabled;
        @ApiModelProperty("当前线程CPU时间")
        long currentThreadCpuTime;
        @ApiModelProperty("当前线程用户时间")
        long currentThreadUserTime;
        @ApiModelProperty("是否支持线程CPU时间")
        boolean threadCpuTimeSupported;
        @ApiModelProperty("是否支持当前线程CPU时间")
        boolean currentThreadCpuTimeSupported;
        @ApiModelProperty("是否启用线程CPU时间")
        boolean threadCpuTimeEnabled;
        @ApiModelProperty("死锁线程监视器")
        long[] monitorDeadlockedThreads;
        @ApiModelProperty("死锁线程")
        long[] deadlockedThreads;
        @ApiModelProperty("是否支持使用对象监视器")
        boolean objectMonitorUsageSupported;
        @ApiModelProperty("是否支持使用同步器")
        boolean synchronizerUsageSupported;
        @ApiModelProperty("线程详情")
        ThreadDetail[] threadDetails;
        @ApiModelProperty("死锁杀死线程详情")
        ThreadDetail[] deadLockedThreadDetails;
    }

    @Setter
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @ApiModel("线程详细信息")
    public static class ThreadDetail {
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
    public static class StackTraceElement {
        @ApiModelProperty("异常类")
        private String className;
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
    public static class LockInfo {
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
    public static class MonitorInfo {
        @ApiModelProperty("堆栈深度")
        private int stackDepth;
        @ApiModelProperty("堆栈帧")
        private StackTraceElement stackFrame;
    }

    @Setter
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @ApiModel("进程信息")
    public static class ProcessInfo {
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
        @ApiModelProperty("运行时间(秒)")
        private String uptime;
        @ApiModelProperty("启动时间")
        private String startTime;
    }

    @Setter
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @ApiModel("操作系统信息")
    public static class OsInfo {
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
    @ApiModel("内存信息")
    public static class MemoryInfo {
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
    public static class MemoryUsage {
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
    @ApiModel("内存池信息")
    public static class MemoryPoolInfo {
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