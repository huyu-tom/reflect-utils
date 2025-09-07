package com.huyu.utils;

import java.lang.reflect.Field;
import sun.misc.Unsafe;

public class UnsafeUtils {

  public static final Unsafe UNSAFE;

  public static final boolean IS_SUPPORTED;

  static {
    boolean isSupported = false;
    Unsafe unsafe = null;
    try {
      Field f = Unsafe.class.getDeclaredField("theUnsafe");
      f.setAccessible(true);
      unsafe = (Unsafe) f.get(null);
      isSupported = true;
    } catch (Exception e) {
      //忽略错误
    }
    IS_SUPPORTED = isSupported;
    UNSAFE = unsafe;
  }

  public static Unsafe getUnsafe() {
    if (isSupportUnsafe()) {
      return UNSAFE;
    }
    throw new UnsupportedOperationException("Unsafe is not supported on this platform.");
  }

  public static boolean isSupportUnsafe() {
    return IS_SUPPORTED;
  }


  /**
   * 注意: 分配一个实例，但不运行任何构造函数。如果还没有初始化课程
   *
   * @param clazz
   * @return
   * @throws InstantiationException
   */
  public static Object newInstance(Class<?> clazz) throws InstantiationException {
    return getUnsafe().allocateInstance(clazz);
  }
}
