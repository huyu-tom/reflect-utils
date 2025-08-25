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
   * 调用方法(静态方法,实例方法,构造方法)
   * <pre>
   * 1. 实例方法跟Method反射的invoke方法一样
   * 2. 构造方法第一个参数传null即可
   * 3. 静态方法第一个参数传null即可
   * </pre>
   *
   * @param target
   * @param args
   * @return
   */
  R invoke(T target, Object... args);

}
