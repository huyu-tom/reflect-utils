package com.huyu.field.invoker.impl;

import com.huyu.field.invoker.FieldReflectInvoker;
import java.lang.reflect.Field;

public class DefaultReflectFieldInvoker implements FieldReflectInvoker<Object, Object> {

  private final Field field;

  public DefaultReflectFieldInvoker(Field field) {
    this.field = field;
    field.setAccessible(true);
  }

  @Override
  public void set(Object target, Object arg) {
    try {
      field.set(target, arg);
    } catch (IllegalAccessException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public Object get(Object target) {
    try {
      return field.get(target);
    } catch (IllegalAccessException e) {
      throw new RuntimeException(e);
    }
  }
}
