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
   * 反射调用
   *
   * @param target
   * @param args
   * @return
   */
  R invoke(T target, Object... args);
}
