package com.huyu.field.invoker;

public interface FieldReflectInvoker<T, R> {

  /**
   *
   *
   * @param target
   * @return
   */
  void set(T target, R arg);


  /**
   *
   *
   * @param target
   * @return
   */
  R get(T target);
}
