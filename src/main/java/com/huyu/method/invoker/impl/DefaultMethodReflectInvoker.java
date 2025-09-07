package com.huyu.method.invoker.impl;

import com.huyu.method.invoker.MethodReflectInvoker;
import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public final class DefaultMethodReflectInvoker implements MethodReflectInvoker<Object, Object> {

  private final Constructor constructor;

  private final Method method;

  private final boolean isConstructor;

  public DefaultMethodReflectInvoker(Executable method) {
    method.setAccessible(true);
    if (method instanceof Constructor constructor) {
      this.constructor = constructor;
      this.method = null;
      isConstructor = true;
    } else {
      this.method = (Method) method;
      this.constructor = null;
      this.isConstructor = false;
    }
  }

  @Override
  public Object invoke(Object target, Object... args) {
    try {
      if (isConstructor) {
        return constructor.newInstance(args);
      } else {
        return method.invoke(target, args);
      }
    } catch (IllegalAccessException e) {
      throw new RuntimeException(e);
    } catch (InvocationTargetException e) {
      throw new RuntimeException(e);
    } catch (InstantiationException e) {
      throw new RuntimeException(e);
    }
  }
}
