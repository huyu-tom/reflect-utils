package com.huyu.method.invoker.impl;

import com.huyu.method.invoker.MethodReflectInvoker;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;


/**
 * <pre>
 * 目的是为了解决静态方法第一个参数传递非null情况导致的bug问题
 * 其实也不用担心性能问题,因为jit会进行内联优化
 * </pre>
 *
 * @author huyu
 */
public class StaticFixedLambdaMethodReflectInvoker implements MethodReflectInvoker<Object, Object> {


  private final boolean isStaticMethod;

  private final MethodReflectInvoker<Object, Object> methodReflectInvoker;


  public StaticFixedLambdaMethodReflectInvoker(Method method,
      MethodReflectInvoker methodReflectInvoker) {
    this.isStaticMethod = Modifier.isStatic(method.getModifiers());
    this.methodReflectInvoker = methodReflectInvoker;
  }


  @Override
  public Object invoke(Object target, Object... args) {
    if (isStaticMethod) {
      //忽略第一个参数,直接设置为null
      return methodReflectInvoker.invoke(null, args);
    }
    return methodReflectInvoker.invoke(target, args);
  }
}
