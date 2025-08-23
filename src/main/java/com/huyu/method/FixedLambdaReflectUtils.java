package com.huyu.method;

import static java.lang.invoke.MethodType.methodType;

import com.huyu.method.invoker.MethodReflectInvoker;
import java.lang.invoke.CallSite;
import java.lang.invoke.LambdaMetafactory;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.function.BiFunction;
import java.util.function.Function;


/**
 * 固定lambda反射工具,只支持无参数或者参数个数<=10的方法反射
 *
 * @author huyu
 */
public class FixedLambdaReflectUtils {


  @FunctionalInterface
  public interface BiConsumer0<T> extends MethodReflectInvoker<T, Object> {

    void accept(T t);

    @Override
    default Object invoke(T target, Object... args) {
      accept(target);
      return null;
    }
  }


  @FunctionalInterface
  public interface BiConsumer1<T, U> extends MethodReflectInvoker<T, Object> {

    void accept(T t, U u);

    @Override
    default Object invoke(T target, Object... args) {
      accept(target, (U) args[0]);
      return null;
    }
  }

  @FunctionalInterface
  public interface BiConsumer2<T, U, U1> extends MethodReflectInvoker<T, Object> {

    void accept(T t, U u, U1 u1);

    @Override
    default Object invoke(T target, Object... args) {
      accept(target, (U) args[0], (U1) args[1]);
      return null;
    }
  }

  @FunctionalInterface
  public interface BiConsumer3<T, U, U1, U2> extends MethodReflectInvoker<T, Object> {

    void accept(T t, U u, U1 u1, U2 u2);

    @Override
    default Object invoke(T target, Object... args) {
      accept(target, (U) args[0], (U1) args[1], (U2) args[2]);
      return null;
    }
  }

  @FunctionalInterface
  public interface BiConsumer4<T, U, U1, U2, U3> extends MethodReflectInvoker<T, Object> {

    void accept(T t, U u, U1 u1, U2 u2, U3 u3);

    @Override
    default Object invoke(T target, Object... args) {
      accept(target, (U) args[0], (U1) args[1], (U2) args[2], (U3) args[3]);
      return null;
    }
  }

  // 新增 BiConsumer5 - BiConsumer10
  @FunctionalInterface
  public interface BiConsumer5<T, U, U1, U2, U3, U4> extends MethodReflectInvoker<T, Object> {

    void accept(T t, U u, U1 u1, U2 u2, U3 u3, U4 u4);

    @Override
    default Object invoke(T target, Object... args) {
      accept(target, (U) args[0], (U1) args[1], (U2) args[2], (U3) args[3], (U4) args[4]);
      return null;
    }
  }

  @FunctionalInterface
  public interface BiConsumer6<T, U, U1, U2, U3, U4, U5> extends MethodReflectInvoker<T, Object> {

    void accept(T t, U u, U1 u1, U2 u2, U3 u3, U4 u4, U5 u5);

    @Override
    default Object invoke(T target, Object... args) {
      accept(target, (U) args[0], (U1) args[1], (U2) args[2], (U3) args[3], (U4) args[4],
          (U5) args[5]);
      return null;
    }
  }

  @FunctionalInterface
  public interface BiConsumer7<T, U, U1, U2, U3, U4, U5, U6> extends
      MethodReflectInvoker<T, Object> {

    void accept(T t, U u, U1 u1, U2 u2, U3 u3, U4 u4, U5 u5, U6 u6);

    @Override
    default Object invoke(T target, Object... args) {
      accept(target, (U) args[0], (U1) args[1], (U2) args[2], (U3) args[3], (U4) args[4],
          (U5) args[5], (U6) args[6]);
      return null;
    }
  }

  @FunctionalInterface
  public interface BiConsumer8<T, U, U1, U2, U3, U4, U5, U6, U7> extends
      MethodReflectInvoker<T, Object> {

    void accept(T t, U u, U1 u1, U2 u2, U3 u3, U4 u4, U5 u5, U6 u6, U7 u7);

    @Override
    default Object invoke(T target, Object... args) {
      accept(target, (U) args[0], (U1) args[1], (U2) args[2], (U3) args[3], (U4) args[4],
          (U5) args[5], (U6) args[6], (U7) args[7]);
      return null;
    }
  }

  @FunctionalInterface
  public interface BiConsumer9<T, U, U1, U2, U3, U4, U5, U6, U7, U8> extends
      MethodReflectInvoker<T, Object> {

    void accept(T t, U u, U1 u1, U2 u2, U3 u3, U4 u4, U5 u5, U6 u6, U7 u7, U8 u8);

    @Override
    default T invoke(T target, Object... args) {
      accept(target, (U) args[0], (U1) args[1], (U2) args[2], (U3) args[3], (U4) args[4],
          (U5) args[5], (U6) args[6], (U7) args[7], (U8) args[8]);
      return null;
    }
  }

  @FunctionalInterface
  public interface BiConsumer10<T, U, U1, U2, U3, U4, U5, U6, U7, U8, U9> extends
      MethodReflectInvoker<T, Object> {

    void accept(T t, U u, U1 u1, U2 u2, U3 u3, U4 u4, U5 u5, U6 u6, U7 u7, U8 u8, U9 u9);

    @Override
    default Object invoke(T target, Object... args) {
      accept(target, (U) args[0], (U1) args[1], (U2) args[2], (U3) args[3], (U4) args[4],
          (U5) args[5], (U6) args[6], (U7) args[7], (U8) args[8], (U9) args[9]);
      return null;
    }
  }

  @FunctionalInterface
  public interface BiFunction0<T, R> extends MethodReflectInvoker<T, R> {

    // T 执行的对象
    // P 参数类型
    // P2 参数2
    R apply(T t);

    default R invoke(T target, Object... args) {
      //传入target,通过ClassFile动态生成args的参数
      return apply(target);
    }
  }

  @FunctionalInterface
  public interface BiFunction1<T, P, R> extends MethodReflectInvoker<T, R> {

    // T 执行的对象
    // P 参数类型
    // P2 参数2
    R apply(T t, P p);

    default R invoke(T target, Object... args) {
      //传入target,通过ClassFile动态生成args的参数
      return apply(target, (P) args[0]);
    }
  }


  @FunctionalInterface
  public interface BiFunction2<T, P, P2, R> extends MethodReflectInvoker<T, R> {

    // T 执行的对象
    // P 参数类型
    // P2 参数2
    R apply(T t, P p, P2 p2);

    default R invoke(T target, Object... args) {
      //传入target,通过ClassFile动态生成args的参数
      return apply(target, (P) args[0], (P2) args[1]);
    }
  }

  @FunctionalInterface
  public interface BiFunction3<T, P, P2, P3, R> extends MethodReflectInvoker<T, R> {

    // T 执行的对象
    // P 参数类型
    // P2 参数2
    R apply(T t, P p, P2 p2, P3 p3);

    default R invoke(T target, Object... args) {
      //传入target,通过ClassFile动态生成args的参数
      return apply(target, (P) args[0], (P2) args[1], (P3) args[2]);
    }
  }

  @FunctionalInterface
  public interface BiFunction4<T, P, P2, P3, P4, R> extends MethodReflectInvoker<T, R> {

    // T 执行的对象
    // P 参数类型
    // P2 参数2
    R apply(T t, P p, P2 p2, P3 p3, P4 p4);

    default R invoke(T target, Object... args) {
      return apply(target, (P) args[0], (P2) args[1], (P3) args[2], (P4) args[3]);
    }
  }

  @FunctionalInterface
  public interface BiFunction5<T, P, P2, P3, P4, P5, R> extends MethodReflectInvoker<T, R> {

    // T 执行的对象
    // P 参数类型
    // P2 参数2
    R apply(T t, P p, P2 p2, P3 p3, P4 p4, P5 p5);

    default R invoke(T target, Object... args) {
      return apply(target, (P) args[0], (P2) args[1], (P3) args[2], (P4) args[3], (P5) args[4]);
    }
  }

  // 新增 BiFunction6 - BiFunction10
  @FunctionalInterface
  public interface BiFunction6<T, P, P2, P3, P4, P5, P6, R> extends MethodReflectInvoker<T, R> {

    R apply(T t, P p, P2 p2, P3 p3, P4 p4, P5 p5, P6 p6);

    default R invoke(T target, Object... args) {
      return apply((T) target, (P) args[0], (P2) args[1], (P3) args[2], (P4) args[3], (P5) args[4],
          (P6) args[5]);
    }
  }

  @FunctionalInterface
  public interface BiFunction7<T, P, P2, P3, P4, P5, P6, P7, R> extends MethodReflectInvoker<T, R> {

    R apply(T t, P p, P2 p2, P3 p3, P4 p4, P5 p5, P6 p6, P7 p7);

    default R invoke(T target, Object... args) {
      return apply(target, (P) args[0], (P2) args[1], (P3) args[2], (P4) args[3], (P5) args[4],
          (P6) args[5], (P7) args[6]);
    }
  }

  @FunctionalInterface
  public interface BiFunction8<T, P, P2, P3, P4, P5, P6, P7, P8, R> extends
      MethodReflectInvoker<T, R> {

    R apply(T t, P p, P2 p2, P3 p3, P4 p4, P5 p5, P6 p6, P7 p7, P8 p8);

    default R invoke(T target, Object... args) {
      return apply((T) target, (P) args[0], (P2) args[1], (P3) args[2], (P4) args[3], (P5) args[4],
          (P6) args[5], (P7) args[6], (P8) args[7]);
    }
  }

  @FunctionalInterface
  public interface BiFunction9<T, P, P2, P3, P4, P5, P6, P7, P8, P9, R> extends
      MethodReflectInvoker<T, R> {

    R apply(T t, P p, P2 p2, P3 p3, P4 p4, P5 p5, P6 p6, P7 p7, P8 p8, P9 p9);

    default R invoke(T target, Object... args) {
      return apply(target, (P) args[0], (P2) args[1], (P3) args[2], (P4) args[3], (P5) args[4],
          (P6) args[5], (P7) args[6], (P8) args[7], (P9) args[8]);
    }
  }

  @FunctionalInterface
  public interface BiFunction10<T, P, P2, P3, P4, P5, P6, P7, P8, P9, P10, R> extends
      MethodReflectInvoker<T, R> {

    R apply(T t, P p, P2 p2, P3 p3, P4 p4, P5 p5, P6 p6, P7 p7, P8 p8, P9 p9, P10 p10);

    default R invoke(T target, Object... args) {
      return apply(target, (P) args[0], (P2) args[1], (P3) args[2], (P4) args[3], (P5) args[4],
          (P6) args[5], (P7) args[6], (P8) args[7], (P9) args[8], (P10) args[9]);
    }
  }

  private record LambdaWrapper(Class<?> classz, Method method) {

  }

  private static final LambdaWrapper[] LAMBDA_CONSUMER_WRAPPERS;

  private static final LambdaWrapper[] LAMBDA_FUNCTION_WRAPPERS;

  static {
    try {
      LAMBDA_CONSUMER_WRAPPERS = new LambdaWrapper[]{
          new LambdaWrapper(BiConsumer0.class, BiConsumer0.class.getMethod("accept", Object.class)),
          // 0
          new LambdaWrapper(BiConsumer1.class,
              BiConsumer1.class.getMethod("accept", Object.class, Object.class)), // 1
          // 显式获取 accept，避免默认方法影响顺序
          new LambdaWrapper(BiConsumer2.class,
              BiConsumer2.class.getMethod("accept", Object.class, Object.class, Object.class)), // 2
          new LambdaWrapper(BiConsumer3.class,
              BiConsumer3.class.getMethod("accept", Object.class, Object.class, Object.class,
                  Object.class)), // 3
          new LambdaWrapper(BiConsumer4.class,
              BiConsumer4.class.getMethod("accept", Object.class, Object.class, Object.class,
                  Object.class, Object.class)), // 4
          new LambdaWrapper(BiConsumer5.class,
              BiConsumer5.class.getMethod("accept", Object.class, Object.class, Object.class,
                  Object.class, Object.class, Object.class)), // 5
          new LambdaWrapper(BiConsumer6.class,
              BiConsumer6.class.getMethod("accept", Object.class, Object.class, Object.class,
                  Object.class, Object.class, Object.class, Object.class)), // 6
          new LambdaWrapper(BiConsumer7.class,
              BiConsumer7.class.getMethod("accept", Object.class, Object.class, Object.class,
                  Object.class, Object.class, Object.class, Object.class, Object.class)), // 7
          new LambdaWrapper(BiConsumer8.class,
              BiConsumer8.class.getMethod("accept", Object.class, Object.class, Object.class,
                  Object.class, Object.class, Object.class, Object.class, Object.class,
                  Object.class)),
          // 8
          new LambdaWrapper(BiConsumer9.class,
              BiConsumer9.class.getMethod("accept", Object.class, Object.class, Object.class,
                  Object.class, Object.class, Object.class, Object.class, Object.class,
                  Object.class, Object.class)), // 9
          new LambdaWrapper(BiConsumer10.class,
              BiConsumer10.class.getMethod("accept", Object.class, Object.class, Object.class,
                  Object.class, Object.class, Object.class, Object.class, Object.class,
                  Object.class, Object.class, Object.class)) // 10
      };
    } catch (Throwable e) {
      throw new RuntimeException(e);
    }

    try {
      LAMBDA_FUNCTION_WRAPPERS = new LambdaWrapper[]{
          new LambdaWrapper(BiFunction0.class, Function.class.getMethod("apply", Object.class)),
          // 0
          new LambdaWrapper(BiFunction1.class,
              BiFunction.class.getMethod("apply", Object.class, Object.class)), // 1
          // 显式获取 apply，避免默认方法影响顺序
          new LambdaWrapper(BiFunction2.class,
              BiFunction2.class.getMethod("apply", Object.class, Object.class, Object.class)), // 2
          new LambdaWrapper(BiFunction3.class,
              BiFunction3.class.getMethod("apply", Object.class, Object.class, Object.class,
                  Object.class)), // 3
          new LambdaWrapper(BiFunction4.class,
              BiFunction4.class.getMethod("apply", Object.class, Object.class, Object.class,
                  Object.class, Object.class)), // 4
          new LambdaWrapper(BiFunction5.class,
              BiFunction5.class.getMethod("apply", Object.class, Object.class, Object.class,
                  Object.class, Object.class, Object.class)), // 5
          new LambdaWrapper(BiFunction6.class,
              BiFunction6.class.getMethod("apply", Object.class, Object.class, Object.class,
                  Object.class, Object.class, Object.class, Object.class)), // 6
          new LambdaWrapper(BiFunction7.class,
              BiFunction7.class.getMethod("apply", Object.class, Object.class, Object.class,
                  Object.class, Object.class, Object.class, Object.class, Object.class)), // 7
          new LambdaWrapper(BiFunction8.class,
              BiFunction8.class.getMethod("apply", Object.class, Object.class, Object.class,
                  Object.class, Object.class, Object.class, Object.class, Object.class,
                  Object.class)),
          // 8
          new LambdaWrapper(BiFunction9.class,
              BiFunction9.class.getMethod("apply", Object.class, Object.class, Object.class,
                  Object.class, Object.class, Object.class, Object.class, Object.class,
                  Object.class, Object.class)),
          // 9
          new LambdaWrapper(BiFunction10.class,
              BiFunction10.class.getMethod("apply", Object.class, Object.class, Object.class,
                  Object.class, Object.class, Object.class, Object.class, Object.class,
                  Object.class, Object.class, Object.class)) // 10
      };
    } catch (Throwable e) {
      throw new RuntimeException(e);
    }
  }

  public static MethodReflectInvoker createLambda(Method findMethod) throws Throwable {
    // step1: method => methodHandle

    Lookup lookup;
    if (Modifier.isStatic(findMethod.getModifiers())) {
      // 静态方法
      lookup = MethodHandles.lookup();
    } else {
      // 实例方法
      lookup = MethodHandles.privateLookupIn(findMethod.getDeclaringClass(),
          MethodHandles.lookup());
    }

    MethodHandle handle = lookup.unreflect(findMethod);

    // step2: 获取方法对应的Lambda的包装类和方法
    final LambdaWrapper lambdaWrapper = getLambdaWrapper(findMethod);

    // step3: 构造CallSite生成lambda
    final CallSite callSite = createCallSite(findMethod, lookup, lambdaWrapper, handle);
    try {
      //严格数据类型,性能最高,动态性的很难达到,尝试一下(try)
      return (MethodReflectInvoker) callSite.getTarget().invokeExact();
    } catch (Throwable e) {
      //大部分走得还是这个
      return (MethodReflectInvoker) callSite.getTarget().invoke();
    }
  }

  private static LambdaWrapper getLambdaWrapper(Method method) {
    Class<?> returnType = method.getReturnType();
    int parameterCount = method.getParameterCount();
    LambdaWrapper lambdaWrapper;
    try {
      if (returnType == void.class) {
        // 返回void类型，(有参数和无参数),采用 BiConsumer 模式
        lambdaWrapper = LAMBDA_CONSUMER_WRAPPERS[parameterCount];
      } else {
        // 返回object类型,(有参数和无参数) Function
        lambdaWrapper = LAMBDA_FUNCTION_WRAPPERS[parameterCount];
      }
    } catch (Throwable e) {
      //无适配的参数
      throw new IllegalArgumentException(
          "Method " + method.getName() + " has too many parameters: " + parameterCount
              + ". Maximum supported is 10.");
    }

    if (lambdaWrapper == null) {
      //初始化有问题
      throw new IllegalArgumentException("Method " + method.getName() + " is not a valid lambda.");
    }

    return lambdaWrapper;
  }

  private static CallSite createCallSite(Method method, Lookup lookup, LambdaWrapper lambdaWrapper,
      MethodHandle handle) {
    try {
      final Method lambdaMethod = lambdaWrapper.method;
      final Class<?> lambdaClass = lambdaWrapper.classz;
      return LambdaMetafactory.metafactory(lookup, lambdaMethod.getName(), methodType(lambdaClass),
          // 返回值,参数 (lambda函数)
          methodType(lambdaMethod.getReturnType(), lambdaMethod.getParameterTypes()),
          // handle
          handle,
          // 返回值 类型，参数
          methodType(method.getReturnType(), method.getDeclaringClass(),
              boxPrimitiveTypes(method.getParameterTypes())));
    } catch (Throwable e) {
      throw new IllegalArgumentException(e);
    }
  }


  public static Class<?>[] boxPrimitiveTypes(Class<?>[] classz) {
    return Arrays.stream(classz).map(FixedLambdaReflectUtils::boxPrimitiveType)
        .toArray(Class<?>[]::new);
  }


  public static Class<?> boxPrimitiveType(Class<?> type) {
    if (!type.isPrimitive()) {
      return type;
    }
    if (type == byte.class) {
      return Byte.class;
    } else if (type == short.class) {
      return Short.class;
    } else if (type == int.class) {
      return Integer.class;
    } else if (type == long.class) {
      return Long.class;
    } else if (type == float.class) {
      return Float.class;
    } else if (type == double.class) {
      return Double.class;
    } else if (type == boolean.class) {
      return Boolean.class;
    } else if (type == char.class) {
      return Character.class;
    } else if (type == void.class) {
      return Void.class;
    }
    return type;
  }
}
