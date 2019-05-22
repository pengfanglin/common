package com.fanglin.common.utils;



import com.fanglin.common.core.others.ValidateException;
import lombok.extern.slf4j.Slf4j;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Twitter_Snowflake<br>
 * SnowFlake的结构如下(每部分用-分开):<br>
 * 0 - 0000000000 0000000000 0000000000 0000000000 0 - 00000 - 00000 - 000000000000 <br>
 * 1位标识，由于long基本类型在Java中是带符号的，最高位是符号位，正数是0，负数是1，所以id一般是正数，最高位是0<br>
 * 41位时间截(毫秒级)，注意，41位时间截不是存储当前时间的时间截，而是存储时间截的差值（当前时间截 - 开始时间截)
 * 得到的值），这里的的开始时间截，一般是我们的id生成器开始使用的时间，由我们程序来指定的（如下下面程序IdWorker类的startTime属性）。41位的时间截，可以使用69年，年T = (1L << 41) / (1000L * 60 * 60 * 24 * 365) = 69<br>
 * 10位的数据机器位，可以部署在1024个节点，包括5位dataCenterId和5位workerId<br>
 * 12位序列，毫秒内的计数，12位的计数顺序号支持每个节点每毫秒(同一机器，同一时间截)产生4096个ID序号<br>
 * 加起来刚好64位，为一个Long型。<br>
 * SnowFlake的优点是，整体上按照时间自增排序，并且整个分布式系统内不会产生ID碰撞(由数据中心ID和机器ID作区分)，并且效率较高，经测试，SnowFlake每秒能够产生26万ID左右。
 *
 * @author 彭方林
 * @version 1.0
 * @date 2019/4/2 17:56
 **/
@Slf4j
public class UUIDUtils {

    // ==============================Fields===========================================
    /**
     * 开始时间截 (2015-01-01)
     */
    private static final long START_TIME = 1420041600000L;

    /**
     * 机器id所占的位数
     */
    private static final int WORKER_ID_BITS = 5;

    /**
     * 数据标识id所占的位数
     */
    private static final int DATA_CENTER_ID_BITS = 5;

    /**
     * 支持的最大机器id，结果是31 (这个移位算法可以很快的计算出几位二进制数所能表示的最大十进制数)
     */
    private static final int MAX_WORKER_ID = ~(-1 << WORKER_ID_BITS);

    /**
     * 支持的最大数据标识id，结果是31
     */
    private static final int MAX_DATA_CENTER_ID = ~(-1 << DATA_CENTER_ID_BITS);

    /**
     * 序列在id中占的位数
     */
    private static final int SEQUENCE_BITS = 12;

    /**
     * 机器ID向左移12位
     */
    private static final int WORKER_ID_SHIFT = SEQUENCE_BITS;

    /**
     * 数据标识id向左移17位(12+5)
     */
    private static final int DATA_CENTER_ID_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS;

    /**
     * 时间截向左移22位(5+5+12)
     */
    private static final int TIME_STAMP_LEFT_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS + DATA_CENTER_ID_BITS;

    /**
     * 生成序列的掩码，这里为4095 (0b111111111111=0xfff=4095)
     */
    private static final int SEQUENCE_MASK = ~(-1 << SEQUENCE_BITS);

    /**
     * 工作机器ID(0~31)
     */
    private static int WORKER_ID = 0;

    /**
     * 数据中心ID(0~31)
     */
    private static int DATA_CENTER_ID = 0;

    /**
     * 毫秒内序列(0~4095)
     */
    private static int SEQUENCE = 0;

    /**
     * 上次生成ID的时间截
     */
    private static long LAST_TIMESTAMP = -1L;

    /**
     * SimpleDateFormat对象不是线程安全的，需要放到ThreadLocal中
     */
    private static ThreadLocal<SimpleDateFormat> threadLocal = ThreadLocal.withInitial(() -> new SimpleDateFormat("yyyyMMdd"));

    /**
     * 构造函数
     *
     * @param workerId     工作ID (0~31)
     * @param dataCenterId 数据中心ID (0~31)
     */
    public UUIDUtils(int workerId, int dataCenterId) {
        // 如果超出范围就抛出异常
        if (workerId > MAX_WORKER_ID || workerId < 0) {
            throw new IllegalArgumentException("机器Id不能大于最大机器Id或者小于0");
        }
        if (dataCenterId > MAX_DATA_CENTER_ID || dataCenterId < 0) {
            throw new IllegalArgumentException("区域Id不能大于最大区域Id或者小于0");
        }
        UUIDUtils.WORKER_ID = workerId;
        UUIDUtils.DATA_CENTER_ID = dataCenterId;
    }

    /**
     * 构造函数
     */
    public UUIDUtils() {
        UUIDUtils.WORKER_ID = 0;
        UUIDUtils.DATA_CENTER_ID = 0;
    }

    /**
     * 获得下一个ID (该方法是线程安全的)
     *
     * @return SnowflakeId
     */
    public synchronized static long nextId() {
        long timestamp = System.currentTimeMillis();
        //如果当前时间小于上一次ID生成的时间戳，说明系统时钟回退过这个时候应当抛出异常
        if (timestamp < LAST_TIMESTAMP) {
            log.warn("时钟倒退");
            throw new ValidateException(
                String.format("Clock moved backwards.  Refusing to generate id for %d milliseconds", LAST_TIMESTAMP - timestamp));
        }

        //如果是同一时间生成的，则进行毫秒内序列
        if (LAST_TIMESTAMP == timestamp) {
            SEQUENCE = (SEQUENCE + 1) & SEQUENCE_MASK;
            //毫秒内序列溢出
            if (SEQUENCE == 0) {
                //阻塞到下一个毫秒,获得新的时间戳
                timestamp = tilNextMillis(LAST_TIMESTAMP);
            }
        }
        //时间戳改变，毫秒内序列重置
        else {
            SEQUENCE = 0;
        }
        //上次生成ID的时间截
        LAST_TIMESTAMP = timestamp;
        //移位并通过或运算拼到一起组成64位的ID
        return ((timestamp - START_TIME) << TIME_STAMP_LEFT_SHIFT)
            | (DATA_CENTER_ID << DATA_CENTER_ID_SHIFT)
            | (WORKER_ID << WORKER_ID_SHIFT)
            | SEQUENCE;
    }

    /**
     * 阻塞到下一个毫秒，直到获得新的时间戳
     *
     * @param lastTimestamp 上次生成ID的时间截
     * @return 当前时间戳
     */
    protected static long tilNextMillis(long lastTimestamp) {
        long timestamp = System.currentTimeMillis();
        while (timestamp <= lastTimestamp) {
            timestamp = System.currentTimeMillis();
        }
        return timestamp;
    }

    /**
     * 从uuid中提取时间
     *
     * @param longId uuid
     * @return
     */
    public static String[] getTimeFromUUID(long longId) {
        String id = Long.toBinaryString(longId);
        int len = id.length();
        int sequenceStart = len < WORKER_ID_SHIFT ? 0 : len - WORKER_ID_SHIFT;
        int workerStart = len < DATA_CENTER_ID_SHIFT ? 0 : len - DATA_CENTER_ID_SHIFT;
        int timeStart = len < TIME_STAMP_LEFT_SHIFT ? 0 : len - TIME_STAMP_LEFT_SHIFT;
        String sequence = id.substring(sequenceStart, len);
        String workerId = sequenceStart == 0 ? "0" : id.substring(workerStart, sequenceStart);
        String dataCenterId = workerStart == 0 ? "0" : id.substring(timeStart, workerStart);
        String time = timeStart == 0 ? "0" : id.substring(0, timeStart);
        long diffTime = Long.parseLong(time, 2);
        long timeLong = diffTime + START_TIME;
        String stringTime = threadLocal.get().format(new Date(timeLong));
        return new String[]{stringTime.substring(0, 4), stringTime.substring(4, 6), stringTime.substring(6), sequence, dataCenterId, workerId};
    }
}