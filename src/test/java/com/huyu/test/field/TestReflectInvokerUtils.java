package com.huyu.test.field;

import com.huyu.field.ReflectFieldInvokerUtils;
import com.huyu.method.ReflectMethodInvokerUtils;
import com.huyu.method.invoker.MethodReflectInvoker;
import com.huyu.service.impl.XxServiceImpl;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class TestReflectInvokerUtils {

  public static void main(String[] args) throws Throwable {
    XxServiceImpl service = new XxServiceImpl();

    //属性反射和varHandler哪个性能较高
    Field field = service.getClass().getDeclaredField("cc");
    field.setAccessible(true);

    var unsafeInvoker = ReflectFieldInvokerUtils.createUnsafeInvoker(field);
    var varHandleInvoker = ReflectFieldInvokerUtils.createVarHandleInvoker(field);
    var directInvoker = ReflectFieldInvokerUtils.createDirectInvoker(field);
    var fieldInvoker = ReflectFieldInvokerUtils.createDefaultInvoker(field);

    unsafeInvoker.set(service, 100L);
    System.out.println("预期结果是: 100 , 真实结果是: " + unsafeInvoker.get(service));

    varHandleInvoker.set(service, 200L);
    System.out.println("预期结果是: 200 , 真实结果是: " + varHandleInvoker.get(service));

    directInvoker.set(service, 300L);
    System.out.println("预期结果是: 300 , 真实结果是: " + directInvoker.get(service));

    fieldInvoker.set(service, 400L);
    System.out.println("预期结果是: 400 , 真实结果是: " + fieldInvoker.get(service));

    //调用非私有方法
    Method notPrivateAdd = service.getClass().getDeclaredMethod("notPrivateAdd");
    notPrivateAdd.setAccessible(true);
    MethodReflectInvoker notPrivateMethodInvoker = ReflectMethodInvokerUtils.createMethodInvoker(
        notPrivateAdd);
    System.out.println(notPrivateMethodInvoker.invoke(service));

    //调用非私有静态方法
    Method testStatic = service.getClass().getDeclaredMethod("staticAdd", long.class);
    testStatic.setAccessible(true);
    MethodReflectInvoker staticMethodInvoker = ReflectMethodInvokerUtils.createMethodInvoker(
        testStatic);
    System.out.println(staticMethodInvoker.invoke(service, 10L));

    MethodReflectInvoker lambdaMethodInvoker = ReflectMethodInvokerUtils.createDynasticLambdaInvoker(
        testStatic);
    lambdaMethodInvoker.invoke(service, 10L);
  }
}
