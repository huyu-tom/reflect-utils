package com.huyu.field.invoker;

public interface FieldReflectInvoker<T, R> {

  /**
   *
   * 给目标对象设置值
   *
   * @param target 目标对象
   * @param value  要被设置的值
   */
  void set(T target, R value);


  /**
   *
   * 获取目标对象所属的值
   *
   * @param target 目标
   * @return 获取到值
   */
  R get(T target);
}
