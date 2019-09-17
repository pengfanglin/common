package com.fanglin.common.util;

/**
 * 内存计算工具类
 *
 * @author 彭方林
 * @version 1.0
 * @date 2019/8/29 17:55
 **/

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.*;

/**
 * 计算内存占用的工具类
 *
 * @author 彭方林
 * @version 1.0
 * @date 2019/8/29 18:22
 **/
public final class MemoryUtils {

    /**
     * 1K字节的大小
     */
    public static final long ONE_KB = 1024;

    /**
     * 1M字节的大小
     */
    public static final long ONE_MB = ONE_KB * ONE_KB;

    /**
     * 1G字节的大小
     */
    public static final long ONE_GB = ONE_KB * ONE_MB;

    private MemoryUtils() {
    }

    /**
     * 如果JVM启用了指针压缩引用则为true
     */
    public final static boolean COMPRESSED_REFS_ENABLED;

    /**
     * JVM用于表示对象引用的字节数
     */
    public final static int NUM_BYTES_OBJECT_REF;

    /**
     * 示对象标头的字节数(没有字段，没有对齐)
     */
    public final static int NUM_BYTES_OBJECT_HEADER;

    /**
     * 表示数组头的字节数(没有内容，但有对齐)
     */
    public final static int NUM_BYTES_ARRAY_HEADER;

    /**
     * 指定JVM内对象对齐边界的常量。对象将总是取这个常数的整数倍，可能会浪费一些空间
     */
    public final static int NUM_BYTES_OBJECT_ALIGNMENT;

    /**
     * 未知对象的近似内存占用量
     */
    public static final int UNKNOWN_DEFAULT_RAM_BYTES_USED = 256;

    /**
     * 原始类的大小
     */
    public static final Map<Class<?>, Integer> PRIMITIVE_SIZES;

    static {
        Map<Class<?>, Integer> primitiveSizesMap = new IdentityHashMap<>();
        primitiveSizesMap.put(boolean.class, 1);
        primitiveSizesMap.put(byte.class, 1);
        primitiveSizesMap.put(char.class, Character.BYTES);
        primitiveSizesMap.put(short.class, Short.BYTES);
        primitiveSizesMap.put(int.class, Integer.BYTES);
        primitiveSizesMap.put(float.class, Float.BYTES);
        primitiveSizesMap.put(double.class, Double.BYTES);
        primitiveSizesMap.put(long.class, Long.BYTES);
        PRIMITIVE_SIZES = Collections.unmodifiableMap(primitiveSizesMap);
    }

    /**
     * vm通常缓存小长段。它尝试找出值域是什么
     */
    static final long LONG_CACHE_MIN_VALUE, LONG_CACHE_MAX_VALUE;
    static final int LONG_SIZE, STRING_SIZE;


    static final String MANAGEMENT_FACTORY_CLASS = "java.lang.management.ManagementFactory";
    static final String HOT_SPOT_BEAN_CLASS = "com.sun.management.HotSpotDiagnosticMXBean";

    static {
        if (Long.parseLong(System.getProperty("sun.arch.data.model")) == 64) {
            boolean compressedOops = false;
            int objectAlignment = 8;
            try {
                final Class<?> beanClazz = Class.forName(HOT_SPOT_BEAN_CLASS);
                final Object hotSpotBean = Class.forName(MANAGEMENT_FACTORY_CLASS)
                    .getMethod("getPlatformMXBean", Class.class)
                    .invoke(null, beanClazz);
                if (hotSpotBean != null) {
                    final Method getVmOptionMethod = beanClazz.getMethod("getVMOption", String.class);
                    try {
                        final Object vmOption = getVmOptionMethod.invoke(hotSpotBean, "UseCompressedOops");
                        compressedOops = Boolean.parseBoolean(
                            vmOption.getClass().getMethod("getValue").invoke(vmOption).toString()
                        );
                    } catch (ReflectiveOperationException | RuntimeException ignored) {
                    }
                    try {
                        final Object vmOption = getVmOptionMethod.invoke(hotSpotBean, "ObjectAlignmentInBytes");
                        objectAlignment = Integer.parseInt(
                            vmOption.getClass().getMethod("getValue").invoke(vmOption).toString()
                        );
                    } catch (ReflectiveOperationException | RuntimeException ignored) {
                    }
                }
            } catch (ReflectiveOperationException | RuntimeException ignored) {
            }
            COMPRESSED_REFS_ENABLED = compressedOops;
            NUM_BYTES_OBJECT_ALIGNMENT = objectAlignment;
            NUM_BYTES_OBJECT_REF = COMPRESSED_REFS_ENABLED ? 4 : 8;
            NUM_BYTES_OBJECT_HEADER = 8 + NUM_BYTES_OBJECT_REF;
            NUM_BYTES_ARRAY_HEADER = (int) alignObjectSize(NUM_BYTES_OBJECT_HEADER + Integer.BYTES);
        } else {
            COMPRESSED_REFS_ENABLED = false;
            NUM_BYTES_OBJECT_ALIGNMENT = 8;
            NUM_BYTES_OBJECT_REF = 4;
            NUM_BYTES_OBJECT_HEADER = 8;
            NUM_BYTES_ARRAY_HEADER = NUM_BYTES_OBJECT_HEADER + Integer.BYTES;
        }
        LONG_CACHE_MIN_VALUE = Long.MIN_VALUE;
        LONG_CACHE_MAX_VALUE = Long.MAX_VALUE;
        LONG_SIZE = (int) shallowSizeOfInstance(Long.class);
        STRING_SIZE = (int) shallowSizeOfInstance(String.class);
    }

    /**
     * 将内存大小对齐为 {@link #NUM_BYTES_OBJECT_ALIGNMENT}的整数倍.
     */
    public static long alignObjectSize(long size) {
        size += (long) NUM_BYTES_OBJECT_ALIGNMENT - 1L;
        return size - (size % NUM_BYTES_OBJECT_ALIGNMENT);
    }

    /**
     * 返回Long类型的内存大小
     */
    public static long sizeOf(Long value) {
        if (value >= LONG_CACHE_MIN_VALUE && value <= LONG_CACHE_MAX_VALUE) {
            return 0;
        }
        return LONG_SIZE;
    }

    /**
     * 返回byte数组的内存大小
     */
    public static long sizeOf(byte[] arr) {
        return alignObjectSize((long) NUM_BYTES_ARRAY_HEADER + arr.length);
    }

    /**
     * 返回boolean数组的内存大小
     */
    public static long sizeOf(boolean[] arr) {
        return alignObjectSize((long) NUM_BYTES_ARRAY_HEADER + arr.length);
    }

    /**
     * 返回char数组的内存大小
     */
    public static long sizeOf(char[] arr) {
        return alignObjectSize((long) NUM_BYTES_ARRAY_HEADER + (long) Character.BYTES * arr.length);
    }

    /**
     * 返回short数组的内存大小
     */
    public static long sizeOf(short[] arr) {
        return alignObjectSize((long) NUM_BYTES_ARRAY_HEADER + (long) Short.BYTES * arr.length);
    }

    /**
     * 返回int数组的内存大小
     */
    public static long sizeOf(int[] arr) {
        return alignObjectSize((long) NUM_BYTES_ARRAY_HEADER + (long) Integer.BYTES * arr.length);
    }

    /**
     * 返回float数组的内存大小
     */
    public static long sizeOf(float[] arr) {
        return alignObjectSize((long) NUM_BYTES_ARRAY_HEADER + (long) Float.BYTES * arr.length);
    }

    /**
     * 返回float数组的内存大小
     */
    public static long sizeOf(long[] arr) {
        return alignObjectSize((long) NUM_BYTES_ARRAY_HEADER + (long) Long.BYTES * arr.length);
    }

    /**
     * 返回double数组的内存大小
     */
    public static long sizeOf(double[] arr) {
        return alignObjectSize((long) NUM_BYTES_ARRAY_HEADER + (long) Double.BYTES * arr.length);
    }

    /**
     * 返回String数组的内存大小
     */
    public static long sizeOf(String[] arr) {
        long size = shallowSizeOf(arr);
        for (String s : arr) {
            if (s == null) {
                continue;
            }
            size += sizeOf(s);
        }
        return size;
    }

    /**
     * 只递归到直系后代
     */
    public static final int MAX_DEPTH = 1;

    /**
     * 返回Map的内存大小
     */
    public static long sizeOfMap(Map<?, ?> map) {
        return sizeOfMap(map, 0, UNKNOWN_DEFAULT_RAM_BYTES_USED);
    }

    /**
     * 返回Map的内存大小
     */
    public static long sizeOfMap(Map<?, ?> map, long defSize) {
        return sizeOfMap(map, 0, defSize);
    }

    private static long sizeOfMap(Map<?, ?> map, int depth, long defSize) {
        if (map == null) {
            return 0;
        }
        long size = shallowSizeOf(map);
        if (depth > MAX_DEPTH) {
            return size;
        }
        long sizeOfEntry = -1;
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            if (sizeOfEntry == -1) {
                sizeOfEntry = shallowSizeOf(entry);
            }
            size += sizeOfEntry;
            size += sizeOfObject(entry.getKey(), depth, defSize);
            size += sizeOfObject(entry.getValue(), depth, defSize);
        }
        return alignObjectSize(size);
    }

    /**
     * 返回集合的内存大小
     */
    public static long sizeOfCollection(Collection<?> collection) {
        return sizeOfCollection(collection, 0, UNKNOWN_DEFAULT_RAM_BYTES_USED);
    }

    /**
     * 返回集合的内存大小
     */
    public static long sizeOfCollection(Collection<?> collection, long defSize) {
        return sizeOfCollection(collection, 0, defSize);
    }

    private static long sizeOfCollection(Collection<?> collection, int depth, long defSize) {
        if (collection == null) {
            return 0;
        }
        long size = shallowSizeOf(collection);
        if (depth > MAX_DEPTH) {
            return size;
        }
        size += NUM_BYTES_ARRAY_HEADER + collection.size() * NUM_BYTES_OBJECT_REF;
        for (Object o : collection) {
            size += sizeOfObject(o, depth, defSize);
        }
        return alignObjectSize(size);
    }


    /**
     * Best effort attempt to estimate the size in bytes of an undetermined object. Known types
     * will be estimated according to their formulas, and all other object sizes will be estimated
     * as {@link #UNKNOWN_DEFAULT_RAM_BYTES_USED}.
     */
    public static long sizeOfObject(Object o) {
        return sizeOfObject(o, 0, UNKNOWN_DEFAULT_RAM_BYTES_USED);
    }

    /**
     * 对象的内存大小
     */
    public static long sizeOfObject(Object o, long defSize) {
        return sizeOfObject(o, 0, defSize);
    }

    /**
     * 对象的内存大小
     */
    private static long sizeOfObject(Object o, int depth, long defSize) {
        if (o == null) {
            return 0;
        }
        long size;
        if (o instanceof String) {
            size = sizeOf((String) o);
        } else if (o instanceof boolean[]) {
            size = sizeOf((boolean[]) o);
        } else if (o instanceof byte[]) {
            size = sizeOf((byte[]) o);
        } else if (o instanceof char[]) {
            size = sizeOf((char[]) o);
        } else if (o instanceof double[]) {
            size = sizeOf((double[]) o);
        } else if (o instanceof float[]) {
            size = sizeOf((float[]) o);
        } else if (o instanceof int[]) {
            size = sizeOf((int[]) o);
        } else if (o instanceof Long) {
            size = sizeOf((Long) o);
        } else if (o instanceof long[]) {
            size = sizeOf((long[]) o);
        } else if (o instanceof short[]) {
            size = sizeOf((short[]) o);
        } else if (o instanceof String[]) {
            size = sizeOf((String[]) o);
        } else if (o instanceof Map) {
            size = sizeOfMap((Map) o, ++depth, defSize);
        } else if (o instanceof Collection) {
            size = sizeOfCollection((Collection) o, ++depth, defSize);
        } else {
            if (defSize > 0) {
                size = defSize;
            } else {
                size = shallowSizeOf(o);
            }
        }
        return size;
    }

    /**
     * 字符串的内存大小
     */
    public static long sizeOf(String s) {
        if (s == null) {
            return 0;
        }
        long size = STRING_SIZE + (long) NUM_BYTES_ARRAY_HEADER + (long) Character.BYTES * s.length();
        return alignObjectSize(size);
    }

    /**
     * 对象数组的浅内存大小
     */
    public static long shallowSizeOf(Object[] arr) {
        return alignObjectSize((long) NUM_BYTES_ARRAY_HEADER + (long) NUM_BYTES_OBJECT_REF * arr.length);
    }

    /**
     * 返回对象的浅内存大小
     */
    public static long shallowSizeOf(Object obj) {
        if (obj == null) {
            return 0;
        }
        final Class<?> clz = obj.getClass();
        if (clz.isArray()) {
            return shallowSizeOfArray(obj);
        } else {
            return shallowSizeOfInstance(clz);
        }
    }

    /**
     * 返回给定类的实例将占用的实例大小(以字节为单位)
     * 这适用于所有传统类和基本类型，但不适用于数组
     * 然后大小取决于元素的数量，并且对象之间有所不同
     */
    public static long shallowSizeOfInstance(Class<?> clazz) {
        if (clazz.isArray()) {
            throw new IllegalArgumentException("该方法不能计算数组的大小");
        }
        if (clazz.isPrimitive()) {
            return PRIMITIVE_SIZES.get(clazz);
        }
        long size = NUM_BYTES_OBJECT_HEADER;
        for (; clazz != null; clazz = clazz.getSuperclass()) {
            final Field[] fields = AccessController.doPrivileged((PrivilegedAction<Field[]>) clazz::getDeclaredFields);
            for (Field f : fields) {
                if (!Modifier.isStatic(f.getModifiers())) {
                    size = adjustForField(size, f);
                }
            }
        }
        return alignObjectSize(size);
    }

    /**
     * 数组对象的内存大小
     */
    private static long shallowSizeOfArray(Object array) {
        long size = NUM_BYTES_ARRAY_HEADER;
        final int len = Array.getLength(array);
        if (len > 0) {
            Class<?> arrayElementClazz = array.getClass().getComponentType();
            if (arrayElementClazz.isPrimitive()) {
                size += (long) len * PRIMITIVE_SIZES.get(arrayElementClazz);
            } else {
                size += (long) NUM_BYTES_OBJECT_REF * len;
            }
        }
        return alignObjectSize(size);
    }

    /**
     * 回对象的最大表示大小,是目前测量到的物体尺寸,返回的偏移量将是迄今为止测量到的最大偏移量
     */
    static long adjustForField(long sizeSoFar, final Field f) {
        final Class<?> type = f.getType();
        final int size = type.isPrimitive() ? PRIMITIVE_SIZES.get(type) : NUM_BYTES_OBJECT_REF;
        return sizeSoFar + size;
    }

    /**
     * 返回格式化的大小(GB, MB, KB or bytes)
     */
    public static String format(long bytes) {
        return format(bytes, new DecimalFormat("0.#", DecimalFormatSymbols.getInstance(Locale.ROOT)));
    }

    /**
     * 返回格式化的大小(GB, MB, KB or bytes)
     */
    public static String format(long bytes, DecimalFormat df) {
        if (bytes / ONE_GB > 0) {
            return df.format((float) bytes / ONE_GB) + "GB";
        } else if (bytes / ONE_MB > 0) {
            return df.format((float) bytes / ONE_MB) + "MB";
        } else if (bytes / ONE_KB > 0) {
            return df.format((float) bytes / ONE_KB) + "KB";
        } else {
            return bytes + "bytes";
        }
    }

    public static void main(String[] args) {
        System.out.println(format(sizeOfObject(new PageUtils())));
    }
}
