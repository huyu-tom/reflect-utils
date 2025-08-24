package com.huyu.method.invoker;


/**
 *
 * 反射统一调用接口
 *
 * @param <T> 目标对象
 * @param <R> 返回值 如果调用方法无返回值的话,返回的是null
 * @author huyu
 */
public interface MethodReflectInvoker<T, R> {

  /**
   *
   * 调用方法(静态方法,实例方法)
   * <p>
   * 使用方式和Method的invoke方法一样
   * </p>
   *
   * @param target
   * @param args
   * @return
   */
  R invoke(T target, Object... args);

}
