package com.huyu.test.field;

import com.huyu.field.ReflectFieldInvokerUtils;
import com.huyu.field.invoker.FieldReflectInvoker;
import com.huyu.method.ReflectMethodInvokerUtils;
import com.huyu.method.invoker.MethodReflectInvoker;
import com.huyu.service.impl.XxServiceImpl;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class TestReflectInvokerUtils {

  public static void main(String[] args) throws Throwable {
    XxServiceImpl service = new XxServiceImpl();

    //调用非私有方法
    Method notPrivateAdd = service.getClass().getDeclaredMethod("notPrivateAdd");
    notPrivateAdd.setAccessible(true);
    MethodReflectInvoker notPrivateMethodInvoker = ReflectMethodInvokerUtils.createMethodInvoker(
        notPrivateAdd);
    System.out.println(notPrivateMethodInvoker.invoke(service));

    //调用非私有静态方法
    Method testStatic = service.getClass().getDeclaredMethod("staticAdd");
    testStatic.setAccessible(true);
    MethodReflectInvoker staticMethodInvoker = ReflectMethodInvokerUtils.createMethodInvoker(
        testStatic);
    System.out.println(staticMethodInvoker.invoke(service));

    System.out.println("===================开始测试属性==============");

    //测试对象属性
    testField(service);

    //测试静态属性
    testStaticField();
  }

  private static void testField(XxServiceImpl service) throws NoSuchFieldException {
    testNoPrivateFiled(service);

    testPrivateFiled(service);
  }

  private static void testPrivateFiled(XxServiceImpl service) throws NoSuchFieldException {

    System.out.println("=================测试私有属性===================");
//属性反射和varHandler哪个性能较高
    Field field = service.getClass().getDeclaredField("privateFiled");
    field.setAccessible(true);

    var unsafeInvoker = ReflectFieldInvokerUtils.createUnsafeInvoker(field);
    unsafeInvoker.set(service, 600L);
    System.out.println("预期结果是: 600 , 真实结果是: " + unsafeInvoker.get(service));

    var varHandleInvoker = ReflectFieldInvokerUtils.createVarHandleInvoker(field);
    varHandleInvoker.set(service, 200L);
    System.out.println("预期结果是: 200 , 真实结果是: " + varHandleInvoker.get(service));

    try {
      var directInvoker = ReflectFieldInvokerUtils.createDirectInvoker(field);
      directInvoker.set(service, 300L);
      System.out.println("非预期结果是: 300 , 真实结果是: " + directInvoker.get(service));
    } catch (Throwable e) {
      System.out.println("预期结果私有属性无法直接调用, 真实结果是: " + e.getMessage());
    }

    var fieldInvoker = ReflectFieldInvokerUtils.createDefaultInvoker(field);
    fieldInvoker.set(service, 400L);
    System.out.println("预期结果是: 400 , 真实结果是: " + fieldInvoker.get(service));
  }

  private static void testNoPrivateFiled(XxServiceImpl service) throws NoSuchFieldException {
    System.out.println("=================测试非私有属性===================");

    //属性反射和varHandler哪个性能较高
    Field field = service.getClass().getDeclaredField("noPrivateFiled");
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
  }


  private static void testStaticField() throws Throwable {

    System.out.println("=================测试非私有静态属性===================");

    testNoPrivateStaticField();

    System.out.println("=================测试私有静态属性===================");

    testPrivateStaticField();
  }

  private static void testPrivateStaticField() throws NoSuchFieldException {
    //公共属性
    Field privateStaticField = XxServiceImpl.class.getDeclaredField("privateStaticField");

    //测试直接调用
    try {
      FieldReflectInvoker directInvoker = ReflectFieldInvokerUtils.createDirectInvoker(
          privateStaticField);
      directInvoker.set(null, 20L);
      System.out.println("预期结果是: 20 ,  真实结果是: " + directInvoker.get(null));
      directInvoker.set(null, 10L);
    } catch (Exception e) {
      System.out.println("预期私有属性直接调用失败, 真实结果是: " + e.getMessage());
    }

    FieldReflectInvoker unsafeInvoker = ReflectFieldInvokerUtils.createUnsafeInvoker(
        privateStaticField);
    unsafeInvoker.set(null, 30L);
    System.out.println("预期结果是: 30 ,  真实结果是: " + unsafeInvoker.get(null));
    //重置
    unsafeInvoker.set(null, 10L);

    FieldReflectInvoker varHandleInvoker = ReflectFieldInvokerUtils.createVarHandleInvoker(
        privateStaticField);
    varHandleInvoker.set(null, 40L);
    System.out.println("预期结果是: 40 ,  真实结果是: " + varHandleInvoker.get(null));
    //重置
    varHandleInvoker.set(null, 10L);

    FieldReflectInvoker defaultInvoker = ReflectFieldInvokerUtils.createDefaultInvoker(
        privateStaticField);
    defaultInvoker.set(null, 50L);
    System.out.println("预期结果是: 50 ,  真实结果是: " + defaultInvoker.get(null));
    //重置
    defaultInvoker.set(null, 10L);
  }

  private static void testNoPrivateStaticField() throws NoSuchFieldException {
    //公共属性
    Field field = XxServiceImpl.class.getDeclaredField("publicStaticField");

    //测试直接调用
    FieldReflectInvoker directInvoker = ReflectFieldInvokerUtils.createDirectInvoker(field);
    directInvoker.set(null, 20L);
    System.out.println("预期结果是: 20 ,  真实结果是: " + directInvoker.get(null));
    //重置
    directInvoker.set(null, 10L);

    FieldReflectInvoker unsafeInvoker = ReflectFieldInvokerUtils.createUnsafeInvoker(field);
    unsafeInvoker.set(null, 30L);
    System.out.println("预期结果是: 30 ,  真实结果是: " + unsafeInvoker.get(null));
    //重置
    unsafeInvoker.set(null, 10L);

    FieldReflectInvoker varHandleInvoker = ReflectFieldInvokerUtils.createVarHandleInvoker(field);
    varHandleInvoker.set(null, 40L);
    System.out.println("预期结果是: 40 ,  真实结果是: " + varHandleInvoker.get(null));
    //重置
    varHandleInvoker.set(null, 10L);

    FieldReflectInvoker defaultInvoker = ReflectFieldInvokerUtils.createDefaultInvoker(field);
    defaultInvoker.set(null, 50L);
    System.out.println("预期结果是: 50 ,  真实结果是: " + defaultInvoker.get(null));

    //必须重置
    //重置
    defaultInvoker.set(null, 10L);

    return;
  }
}
