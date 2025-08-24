package com.huyu.method;

import static java.lang.invoke.MethodType.methodType;

import com.huyu.method.invoker.MethodReflectInvoker;
import com.huyu.method.invoker.impl.StaticFixedLambdaMethodReflectInvoker;
import java.lang.invoke.CallSite;
import java.lang.invoke.LambdaMetafactory;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.function.BiFunction;
import java.util.function.Function;


/**
 * 固定lambda反射工具,只支持无参数或者参数个数<={@code  MAX_SUPPORT_PARAMS_COUNT}的方法
 *
 * @author huyu
 */
public class FixedLambdaReflectUtils {


  /**
   * 最大支持的参数个数
   */
  public static final int MAX_SUPPORT_PARAMS_COUNT = 20;


  /**
   * 是否支持固定的lambda方法
   *
   * @param method
   * @return
   */
  public static boolean isSupportFixLambda(Method method) {
    return method.getParameterCount() <= MAX_SUPPORT_PARAMS_COUNT;
  }

  public interface BiConsumer0<T> extends MethodReflectInvoker<T, Object> {

    void accept(T t);

    void acceptStatic();

    @Override
    default Object invoke(T target, Object... args) {
      if (target == null) {
        acceptStatic();
      } else {
        accept(target);
      }
      return null;
    }
  }


  public interface BiConsumer1<T, U> extends MethodReflectInvoker<T, Object> {

    void accept(T t, U u);

    void acceptStatic(U u);


    @Override
    default Object invoke(T target, Object... args) {
      accept(target, (U) args[0]);
      return null;
    }
  }

  public interface BiConsumer2<T, U, U1> extends MethodReflectInvoker<T, Object> {

    void accept(T t, U u, U1 u1);

    void acceptStatic(U u, U1 u1);

    @Override
    default Object invoke(T target, Object... args) {
      accept(target, (U) args[0], (U1) args[1]);
      return null;
    }
  }


  public interface BiConsumer3<T, U, U1, U2> extends MethodReflectInvoker<T, Object> {

    void accept(T t, U u, U1 u1, U2 u2);

    void acceptStatic(U u, U1 u1, U2 u2);

    @Override
    default Object invoke(T target, Object... args) {
      if (target == null) {
        acceptStatic((U) args[0], (U1) args[1], (U2) args[2]);
      } else {
        accept(target, (U) args[0], (U1) args[1], (U2) args[2]);
      }
      return null;
    }
  }

  public interface BiConsumer4<T, U, U1, U2, U3> extends MethodReflectInvoker<T, Object> {

    void accept(T t, U u, U1 u1, U2 u2, U3 u3);

    void acceptStatic(U u, U1 u1, U2 u2, U3 u3);

    @Override
    default Object invoke(T target, Object... args) {
      if (target == null) {
        acceptStatic((U) args[0], (U1) args[1], (U2) args[2], (U3) args[3]);
      } else {
        accept(target, (U) args[0], (U1) args[1], (U2) args[2], (U3) args[3]);
      }
      return null;
    }
  }

  // 新增 BiConsumer5 - BiConsumer10
  public interface BiConsumer5<T, U, U1, U2, U3, U4> extends MethodReflectInvoker<T, Object> {

    void accept(T t, U u, U1 u1, U2 u2, U3 u3, U4 u4);

    void acceptStatic(U u, U1 u1, U2 u2, U3 u3, U4 u4);

    @Override
    default Object invoke(T target, Object... args) {
      if (target == null) {
        acceptStatic((U) args[0], (U1) args[1], (U2) args[2], (U3) args[3], (U4) args[4]);
      } else {
        accept(target, (U) args[0], (U1) args[1], (U2) args[2], (U3) args[3], (U4) args[4]);
      }
      return null;
    }
  }

  public interface BiConsumer6<T, U, U1, U2, U3, U4, U5> extends MethodReflectInvoker<T, Object> {

    void accept(T t, U u, U1 u1, U2 u2, U3 u3, U4 u4, U5 u5);

    void acceptStatic(U u, U1 u1, U2 u2, U3 u3, U4 u4, U5 u5);

    @Override
    default Object invoke(T target, Object... args) {
      if (target == null) {
        acceptStatic((U) args[0], (U1) args[1], (U2) args[2], (U3) args[3], (U4) args[4],
            (U5) args[5]);
      } else {
        accept(target, (U) args[0], (U1) args[1], (U2) args[2], (U3) args[3], (U4) args[4],
            (U5) args[5]);
      }
      return null;
    }
  }

  public interface BiConsumer7<T, U, U1, U2, U3, U4, U5, U6> extends
      MethodReflectInvoker<T, Object> {

    void accept(T t, U u, U1 u1, U2 u2, U3 u3, U4 u4, U5 u5, U6 u6);

    void acceptStatic(U u, U1 u1, U2 u2, U3 u3, U4 u4, U5 u5, U6 u6);

    @Override
    default Object invoke(T target, Object... args) {
      if (target == null) {
        acceptStatic((U) args[0], (U1) args[1], (U2) args[2], (U3) args[3], (U4) args[4],
            (U5) args[5], (U6) args[6]);
      } else {
        accept(target, (U) args[0], (U1) args[1], (U2) args[2], (U3) args[3], (U4) args[4],
            (U5) args[5], (U6) args[6]);
      }
      return null;
    }
  }

  public interface BiConsumer8<T, U, U1, U2, U3, U4, U5, U6, U7> extends
      MethodReflectInvoker<T, Object> {

    void accept(T t, U u, U1 u1, U2 u2, U3 u3, U4 u4, U5 u5, U6 u6, U7 u7);

    void acceptStatic(U u, U1 u1, U2 u2, U3 u3, U4 u4, U5 u5, U6 u6, U7 u7);

    @Override
    default Object invoke(T target, Object... args) {
      if (target == null) {
        acceptStatic((U) args[0], (U1) args[1], (U2) args[2], (U3) args[3], (U4) args[4],
            (U5) args[5], (U6) args[6], (U7) args[7]);
      } else {
        accept(target, (U) args[0], (U1) args[1], (U2) args[2], (U3) args[3], (U4) args[4],
            (U5) args[5], (U6) args[6], (U7) args[7]);
      }
      return null;
    }
  }

  public interface BiConsumer9<T, U, U1, U2, U3, U4, U5, U6, U7, U8> extends
      MethodReflectInvoker<T, Object> {

    void accept(T t, U u, U1 u1, U2 u2, U3 u3, U4 u4, U5 u5, U6 u6, U7 u7, U8 u8);

    void acceptStatic(U u, U1 u1, U2 u2, U3 u3, U4 u4, U5 u5, U6 u6, U7 u7, U8 u8);

    @Override
    default Object invoke(T target, Object... args) {
      if (target == null) {
        acceptStatic((U) args[0], (U1) args[1], (U2) args[2], (U3) args[3], (U4) args[4],
            (U5) args[5], (U6) args[6], (U7) args[7], (U8) args[8]);
      } else {
        accept(target, (U) args[0], (U1) args[1], (U2) args[2], (U3) args[3], (U4) args[4],
            (U5) args[5], (U6) args[6], (U7) args[7], (U8) args[8]);
      }
      return null;
    }
  }

  public interface BiConsumer10<T, U, U1, U2, U3, U4, U5, U6, U7, U8, U9> extends
      MethodReflectInvoker<T, Object> {

    void accept(T t, U u, U1 u1, U2 u2, U3 u3, U4 u4, U5 u5, U6 u6, U7 u7, U8 u8, U9 u9);

    void acceptStatic(U u, U1 u1, U2 u2, U3 u3, U4 u4, U5 u5, U6 u6, U7 u7, U8 u8, U9 u9);

    @Override
    default Object invoke(T target, Object... args) {
      if (target == null) {
        acceptStatic((U) args[0], (U1) args[1], (U2) args[2], (U3) args[3], (U4) args[4],
            (U5) args[5], (U6) args[6], (U7) args[7], (U8) args[8], (U9) args[9]);
      } else {
        accept(target, (U) args[0], (U1) args[1], (U2) args[2], (U3) args[3], (U4) args[4],
            (U5) args[5], (U6) args[6], (U7) args[7], (U8) args[8], (U9) args[9]);
      }
      return null;
    }
  }

  public interface BiConsumer11<T, U, U1, U2, U3, U4, U5, U6, U7, U8, U9, U10> extends
      MethodReflectInvoker<T, Object> {

    void accept(T t, U u, U1 u1, U2 u2, U3 u3, U4 u4, U5 u5, U6 u6, U7 u7, U8 u8, U9 u9, U10 u10);

    void acceptStatic(U u, U1 u1, U2 u2, U3 u3, U4 u4, U5 u5, U6 u6, U7 u7, U8 u8, U9 u9, U10 u10);

    @Override
    default Object invoke(T target, Object... args) {
      if (target == null) {
        acceptStatic((U) args[0], (U1) args[1], (U2) args[2], (U3) args[3], (U4) args[4],
            (U5) args[5], (U6) args[6], (U7) args[7], (U8) args[8], (U9) args[9], (U10) args[10]);
      } else {
        accept(target, (U) args[0], (U1) args[1], (U2) args[2], (U3) args[3], (U4) args[4],
            (U5) args[5], (U6) args[6], (U7) args[7], (U8) args[8], (U9) args[9], (U10) args[10]);
      }
      return null;
    }
  }

  public interface BiConsumer12<T, U, U1, U2, U3, U4, U5, U6, U7, U8, U9, U10, U11> extends
      MethodReflectInvoker<T, Object> {

    void accept(T t, U u, U1 u1, U2 u2, U3 u3, U4 u4, U5 u5, U6 u6, U7 u7, U8 u8, U9 u9, U10 u10,
        U11 u11);

    void acceptStatic(U u, U1 u1, U2 u2, U3 u3, U4 u4, U5 u5, U6 u6, U7 u7, U8 u8, U9 u9, U10 u10,
        U11 u11);

    @Override
    default Object invoke(T target, Object... args) {
      if (target == null) {
        acceptStatic((U) args[0], (U1) args[1], (U2) args[2], (U3) args[3], (U4) args[4],
            (U5) args[5], (U6) args[6], (U7) args[7], (U8) args[8], (U9) args[9], (U10) args[10],
            (U11) args[11]);
      } else {
        accept(target, (U) args[0], (U1) args[1], (U2) args[2], (U3) args[3], (U4) args[4],
            (U5) args[5], (U6) args[6], (U7) args[7], (U8) args[8], (U9) args[9], (U10) args[10],
            (U11) args[11]);
      }
      return null;
    }
  }

  public interface BiConsumer13<T, U, U1, U2, U3, U4, U5, U6, U7, U8, U9, U10, U11, U12> extends
      MethodReflectInvoker<T, Object> {

    void accept(T t, U u, U1 u1, U2 u2, U3 u3, U4 u4, U5 u5, U6 u6, U7 u7, U8 u8, U9 u9, U10 u10,
        U11 u11, U12 u12);

    void acceptStatic(U u, U1 u1, U2 u2, U3 u3, U4 u4, U5 u5, U6 u6, U7 u7, U8 u8, U9 u9, U10 u10,
        U11 u11, U12 u12);

    @Override
    default Object invoke(T target, Object... args) {
      if (target == null) {
        acceptStatic((U) args[0], (U1) args[1], (U2) args[2], (U3) args[3], (U4) args[4],
            (U5) args[5], (U6) args[6], (U7) args[7], (U8) args[8], (U9) args[9], (U10) args[10],
            (U11) args[11], (U12) args[12]);
      } else {
        accept(target, (U) args[0], (U1) args[1], (U2) args[2], (U3) args[3], (U4) args[4],
            (U5) args[5], (U6) args[6], (U7) args[7], (U8) args[8], (U9) args[9], (U10) args[10],
            (U11) args[11], (U12) args[12]);
      }
      return null;
    }
  }

  public interface BiConsumer14<T, U, U1, U2, U3, U4, U5, U6, U7, U8, U9, U10, U11, U12, U13> extends
      MethodReflectInvoker<T, Object> {

    void accept(T t, U u, U1 u1, U2 u2, U3 u3, U4 u4, U5 u5, U6 u6, U7 u7, U8 u8, U9 u9, U10 u10,
        U11 u11, U12 u12, U13 u13);

    void acceptStatic(U u, U1 u1, U2 u2, U3 u3, U4 u4, U5 u5, U6 u6, U7 u7, U8 u8, U9 u9, U10 u10,
        U11 u11, U12 u12, U13 u13);

    @Override
    default Object invoke(T target, Object... args) {
      if (target == null) {
        acceptStatic((U) args[0], (U1) args[1], (U2) args[2], (U3) args[3], (U4) args[4],
            (U5) args[5], (U6) args[6], (U7) args[7], (U8) args[8], (U9) args[9], (U10) args[10],
            (U11) args[11], (U12) args[12], (U13) args[13]);
      } else {
        accept(target, (U) args[0], (U1) args[1], (U2) args[2], (U3) args[3], (U4) args[4],
            (U5) args[5], (U6) args[6], (U7) args[7], (U8) args[8], (U9) args[9], (U10) args[10],
            (U11) args[11], (U12) args[12], (U13) args[13]);
      }
      return null;
    }
  }

  public interface BiConsumer15<T, U, U1, U2, U3, U4, U5, U6, U7, U8, U9, U10, U11, U12, U13, U14> extends
      MethodReflectInvoker<T, Object> {

    void accept(T t, U u, U1 u1, U2 u2, U3 u3, U4 u4, U5 u5, U6 u6, U7 u7, U8 u8, U9 u9, U10 u10,
        U11 u11, U12 u12, U13 u13, U14 u14);

    void acceptStatic(U u, U1 u1, U2 u2, U3 u3, U4 u4, U5 u5, U6 u6, U7 u7, U8 u8, U9 u9, U10 u10,
        U11 u11, U12 u12, U13 u13, U14 u14);

    @Override
    default Object invoke(T target, Object... args) {
      if (target == null) {
        acceptStatic((U) args[0], (U1) args[1], (U2) args[2], (U3) args[3], (U4) args[4],
            (U5) args[5], (U6) args[6], (U7) args[7], (U8) args[8], (U9) args[9], (U10) args[10],
            (U11) args[11], (U12) args[12], (U13) args[13], (U14) args[14]);
      } else {
        accept(target, (U) args[0], (U1) args[1], (U2) args[2], (U3) args[3], (U4) args[4],
            (U5) args[5], (U6) args[6], (U7) args[7], (U8) args[8], (U9) args[9], (U10) args[10],
            (U11) args[11], (U12) args[12], (U13) args[13], (U14) args[14]);
      }
      return null;
    }
  }

  public interface BiConsumer16<T, U, U1, U2, U3, U4, U5, U6, U7, U8, U9, U10, U11, U12, U13, U14, U15> extends
      MethodReflectInvoker<T, Object> {

    void accept(T t, U u, U1 u1, U2 u2, U3 u3, U4 u4, U5 u5, U6 u6, U7 u7, U8 u8, U9 u9, U10 u10,
        U11 u11, U12 u12, U13 u13, U14 u14, U15 u15);

    void acceptStatic(U u, U1 u1, U2 u2, U3 u3, U4 u4, U5 u5, U6 u6, U7 u7, U8 u8, U9 u9, U10 u10,
        U11 u11, U12 u12, U13 u13, U14 u14, U15 u15);

    @Override
    default Object invoke(T target, Object... args) {
      if (target == null) {
        acceptStatic((U) args[0], (U1) args[1], (U2) args[2], (U3) args[3], (U4) args[4],
            (U5) args[5], (U6) args[6], (U7) args[7], (U8) args[8], (U9) args[9], (U10) args[10],
            (U11) args[11], (U12) args[12], (U13) args[13], (U14) args[14], (U15) args[15]);
      } else {
        accept(target, (U) args[0], (U1) args[1], (U2) args[2], (U3) args[3], (U4) args[4],
            (U5) args[5], (U6) args[6], (U7) args[7], (U8) args[8], (U9) args[9], (U10) args[10],
            (U11) args[11], (U12) args[12], (U13) args[13], (U14) args[14], (U15) args[15]);
      }
      return null;
    }
  }

  public interface BiConsumer17<T, U, U1, U2, U3, U4, U5, U6, U7, U8, U9, U10, U11, U12, U13, U14, U15, U16> extends
      MethodReflectInvoker<T, Object> {

    void accept(T t, U u, U1 u1, U2 u2, U3 u3, U4 u4, U5 u5, U6 u6, U7 u7, U8 u8, U9 u9, U10 u10,
        U11 u11, U12 u12, U13 u13, U14 u14, U15 u15, U16 u16);

    void acceptStatic(U u, U1 u1, U2 u2, U3 u3, U4 u4, U5 u5, U6 u6, U7 u7, U8 u8, U9 u9, U10 u10,
        U11 u11, U12 u12, U13 u13, U14 u14, U15 u15, U16 u16);

    @Override
    default Object invoke(T target, Object... args) {
      if (target == null) {
        acceptStatic((U) args[0], (U1) args[1], (U2) args[2], (U3) args[3], (U4) args[4],
            (U5) args[5], (U6) args[6], (U7) args[7], (U8) args[8], (U9) args[9], (U10) args[10],
            (U11) args[11], (U12) args[12], (U13) args[13], (U14) args[14], (U15) args[15],
            (U16) args[16]);
      } else {
        accept(target, (U) args[0], (U1) args[1], (U2) args[2], (U3) args[3], (U4) args[4],
            (U5) args[5], (U6) args[6], (U7) args[7], (U8) args[8], (U9) args[9], (U10) args[10],
            (U11) args[11], (U12) args[12], (U13) args[13], (U14) args[14], (U15) args[15],
            (U16) args[16]);
      }
      return null;
    }
  }

  public interface BiConsumer18<T, U, U1, U2, U3, U4, U5, U6, U7, U8, U9, U10, U11, U12, U13, U14, U15, U16, U17> extends
      MethodReflectInvoker<T, Object> {

    void accept(T t, U u, U1 u1, U2 u2, U3 u3, U4 u4, U5 u5, U6 u6, U7 u7, U8 u8, U9 u9, U10 u10,
        U11 u11, U12 u12, U13 u13, U14 u14, U15 u15, U16 u16, U17 u17);

    void acceptStatic(U u, U1 u1, U2 u2, U3 u3, U4 u4, U5 u5, U6 u6, U7 u7, U8 u8, U9 u9, U10 u10,
        U11 u11, U12 u12, U13 u13, U14 u14, U15 u15, U16 u16, U17 u17);

    @Override
    default Object invoke(T target, Object... args) {
      if (target == null) {
        acceptStatic((U) args[0], (U1) args[1], (U2) args[2], (U3) args[3], (U4) args[4],
            (U5) args[5], (U6) args[6], (U7) args[7], (U8) args[8], (U9) args[9], (U10) args[10],
            (U11) args[11], (U12) args[12], (U13) args[13], (U14) args[14], (U15) args[15],
            (U16) args[16], (U17) args[17]);
      } else {
        accept(target, (U) args[0], (U1) args[1], (U2) args[2], (U3) args[3], (U4) args[4],
            (U5) args[5], (U6) args[6], (U7) args[7], (U8) args[8], (U9) args[9], (U10) args[10],
            (U11) args[11], (U12) args[12], (U13) args[13], (U14) args[14], (U15) args[15],
            (U16) args[16], (U17) args[17]);
      }
      return null;
    }
  }

  public interface BiConsumer19<T, U, U1, U2, U3, U4, U5, U6, U7, U8, U9, U10, U11, U12, U13, U14, U15, U16, U17, U18> extends
      MethodReflectInvoker<T, Object> {

    void accept(T t, U u, U1 u1, U2 u2, U3 u3, U4 u4, U5 u5, U6 u6, U7 u7, U8 u8, U9 u9, U10 u10,
        U11 u11, U12 u12, U13 u13, U14 u14, U15 u15, U16 u16, U17 u17, U18 u18);

    void acceptStatic(U u, U1 u1, U2 u2, U3 u3, U4 u4, U5 u5, U6 u6, U7 u7, U8 u8, U9 u9, U10 u10,
        U11 u11, U12 u12, U13 u13, U14 u14, U15 u15, U16 u16, U17 u17, U18 u18);

    @Override
    default Object invoke(T target, Object... args) {
      if (target == null) {
        acceptStatic((U) args[0], (U1) args[1], (U2) args[2], (U3) args[3], (U4) args[4],
            (U5) args[5], (U6) args[6], (U7) args[7], (U8) args[8], (U9) args[9], (U10) args[10],
            (U11) args[11], (U12) args[12], (U13) args[13], (U14) args[14], (U15) args[15],
            (U16) args[16], (U17) args[17], (U18) args[18]);
      } else {
        accept(target, (U) args[0], (U1) args[1], (U2) args[2], (U3) args[3], (U4) args[4],
            (U5) args[5], (U6) args[6], (U7) args[7], (U8) args[8], (U9) args[9], (U10) args[10],
            (U11) args[11], (U12) args[12], (U13) args[13], (U14) args[14], (U15) args[15],
            (U16) args[16], (U17) args[17], (U18) args[18]);
      }
      return null;
    }
  }

  public interface BiConsumer20<T, U, U1, U2, U3, U4, U5, U6, U7, U8, U9, U10, U11, U12, U13, U14, U15, U16, U17, U18, U19> extends
      MethodReflectInvoker<T, Object> {

    void accept(T t, U u, U1 u1, U2 u2, U3 u3, U4 u4, U5 u5, U6 u6, U7 u7, U8 u8, U9 u9, U10 u10,
        U11 u11, U12 u12, U13 u13, U14 u14, U15 u15, U16 u16, U17 u17, U18 u18, U19 u19);

    void acceptStatic(U u, U1 u1, U2 u2, U3 u3, U4 u4, U5 u5, U6 u6, U7 u7, U8 u8, U9 u9, U10 u10,
        U11 u11, U12 u12, U13 u13, U14 u14, U15 u15, U16 u16, U17 u17, U18 u18, U19 u19);

    @Override
    default Object invoke(T target, Object... args) {
      if (target == null) {
        acceptStatic((U) args[0], (U1) args[1], (U2) args[2], (U3) args[3], (U4) args[4],
            (U5) args[5], (U6) args[6], (U7) args[7], (U8) args[8], (U9) args[9], (U10) args[10],
            (U11) args[11], (U12) args[12], (U13) args[13], (U14) args[14], (U15) args[15],
            (U16) args[16], (U17) args[17], (U18) args[18], (U19) args[19]);
      } else {
        accept(target, (U) args[0], (U1) args[1], (U2) args[2], (U3) args[3], (U4) args[4],
            (U5) args[5], (U6) args[6], (U7) args[7], (U8) args[8], (U9) args[9], (U10) args[10],
            (U11) args[11], (U12) args[12], (U13) args[13], (U14) args[14], (U15) args[15],
            (U16) args[16], (U17) args[17], (U18) args[18], (U19) args[19]);
      }
      return null;
    }
  }


  public interface BiFunction0<T, R> extends MethodReflectInvoker<T, R> {

    R apply(T t);

    R applyStatic();

    default R invoke(T target, Object... args) {
      //传入target,通过ClassFile动态生成args的参数
      if (target == null) {
        return applyStatic();
      } else {
        return apply(target);
      }
    }
  }

  public interface BiFunction1<T, P, R> extends MethodReflectInvoker<T, R> {

    // T 执行的对象
    // P 参数类型
    // P2 参数2
    R apply(T t, P p);

    R applyStatic(P p);

    default R invoke(T target, Object... args) {
      //传入target,通过ClassFile动态生成args的参数
      if (target == null) {
        return applyStatic((P) args[0]);
      } else {
        return apply(target, (P) args[0]);
      }
    }
  }


  public interface BiFunction2<T, P, P2, R> extends MethodReflectInvoker<T, R> {

    // T 执行的对象
    // P 参数类型
    // P2 参数2
    R apply(T t, P p, P2 p2);

    R applyStatic(P p, P2 p2);

    default R invoke(T target, Object... args) {
      //传入target,通过ClassFile动态生成args的参数
      if (target == null) {
        return applyStatic((P) args[0], (P2) args[1]);
      } else {
        return apply(target, (P) args[0], (P2) args[1]);
      }
    }
  }

  public interface BiFunction3<T, P, P2, P3, R> extends MethodReflectInvoker<T, R> {

    // T 执行的对象
    // P 参数类型
    // P2 参数2
    R apply(T t, P p, P2 p2, P3 p3);

    R applyStatic(P p, P2 p2, P3 p3);

    default R invoke(T target, Object... args) {
      //传入target,通过ClassFile动态生成args的参数
      if (target == null) {
        return applyStatic((P) args[0], (P2) args[1], (P3) args[2]);
      } else {
        return apply(target, (P) args[0], (P2) args[1], (P3) args[2]);
      }
    }
  }

  public interface BiFunction4<T, P, P2, P3, P4, R> extends MethodReflectInvoker<T, R> {

    // T 执行的对象
    // P 参数类型
    // P2 参数2
    R apply(T t, P p, P2 p2, P3 p3, P4 p4);

    R applyStatic(P p, P2 p2, P3 p3, P4 p4);

    default R invoke(T target, Object... args) {
      if (target == null) {
        return applyStatic((P) args[0], (P2) args[1], (P3) args[2], (P4) args[3]);
      } else {
        return apply(target, (P) args[0], (P2) args[1], (P3) args[2], (P4) args[3]);
      }
    }
  }

  public interface BiFunction5<T, P, P2, P3, P4, P5, R> extends MethodReflectInvoker<T, R> {

    // T 执行的对象
    // P 参数类型
    // P2 参��2
    R apply(T t, P p, P2 p2, P3 p3, P4 p4, P5 p5);

    R applyStatic(P p, P2 p2, P3 p3, P4 p4, P5 p5);

    default R invoke(T target, Object... args) {
      if (target == null) {
        return applyStatic((P) args[0], (P2) args[1], (P3) args[2], (P4) args[3], (P5) args[4]);
      } else {
        return apply(target, (P) args[0], (P2) args[1], (P3) args[2], (P4) args[3], (P5) args[4]);
      }
    }
  }

  // 新增 BiFunction6 - BiFunction10
  public interface BiFunction6<T, P, P2, P3, P4, P5, P6, R> extends MethodReflectInvoker<T, R> {

    R apply(T t, P p, P2 p2, P3 p3, P4 p4, P5 p5, P6 p6);

    R applyStatic(P p, P2 p2, P3 p3, P4 p4, P5 p5, P6 p6);

    default R invoke(T target, Object... args) {
      if (target == null) {
        return applyStatic((P) args[0], (P2) args[1], (P3) args[2], (P4) args[3], (P5) args[4],
            (P6) args[5]);
      } else {
        return apply(target, (P) args[0], (P2) args[1], (P3) args[2], (P4) args[3], (P5) args[4],
            (P6) args[5]);
      }
    }
  }

  public interface BiFunction7<T, P, P2, P3, P4, P5, P6, P7, R> extends MethodReflectInvoker<T, R> {

    R apply(T t, P p, P2 p2, P3 p3, P4 p4, P5 p5, P6 p6, P7 p7);

    R applyStatic(P p, P2 p2, P3 p3, P4 p4, P5 p5, P6 p6, P7 p7);

    default R invoke(T target, Object... args) {
      if (target == null) {
        return applyStatic((P) args[0], (P2) args[1], (P3) args[2], (P4) args[3], (P5) args[4],
            (P6) args[5], (P7) args[6]);
      } else {
        return apply(target, (P) args[0], (P2) args[1], (P3) args[2], (P4) args[3], (P5) args[4],
            (P6) args[5], (P7) args[6]);
      }
    }
  }

  public interface BiFunction8<T, P, P2, P3, P4, P5, P6, P7, P8, R> extends
      MethodReflectInvoker<T, R> {

    R apply(T t, P p, P2 p2, P3 p3, P4 p4, P5 p5, P6 p6, P7 p7, P8 p8);

    R applyStatic(P p, P2 p2, P3 p3, P4 p4, P5 p5, P6 p6, P7 p7, P8 p8);

    default R invoke(T target, Object... args) {
      if (target == null) {
        return applyStatic((P) args[0], (P2) args[1], (P3) args[2], (P4) args[3], (P5) args[4],
            (P6) args[5], (P7) args[6], (P8) args[7]);
      } else {
        return apply(target, (P) args[0], (P2) args[1], (P3) args[2], (P4) args[3], (P5) args[4],
            (P6) args[5], (P7) args[6], (P8) args[7]);
      }
    }
  }

  public interface BiFunction9<T, P, P2, P3, P4, P5, P6, P7, P8, P9, R> extends
      MethodReflectInvoker<T, R> {

    R apply(T t, P p, P2 p2, P3 p3, P4 p4, P5 p5, P6 p6, P7 p7, P8 p8, P9 p9);

    R applyStatic(P p, P2 p2, P3 p3, P4 p4, P5 p5, P6 p6, P7 p7, P8 p8, P9 p9);

    default R invoke(T target, Object... args) {
      if (target == null) {
        return applyStatic((P) args[0], (P2) args[1], (P3) args[2], (P4) args[3], (P5) args[4],
            (P6) args[5], (P7) args[6], (P8) args[7], (P9) args[8]);
      } else {
        return apply(target, (P) args[0], (P2) args[1], (P3) args[2], (P4) args[3], (P5) args[4],
            (P6) args[5], (P7) args[6], (P8) args[7], (P9) args[8]);
      }
    }
  }

  public interface BiFunction10<T, P, P2, P3, P4, P5, P6, P7, P8, P9, P10, R> extends
      MethodReflectInvoker<T, R> {

    R apply(T t, P p, P2 p2, P3 p3, P4 p4, P5 p5, P6 p6, P7 p7, P8 p8, P9 p9, P10 p10);

    R applyStatic(P p, P2 p2, P3 p3, P4 p4, P5 p5, P6 p6, P7 p7, P8 p8, P9 p9, P10 p10);

    default R invoke(T target, Object... args) {
      if (target == null) {
        return applyStatic((P) args[0], (P2) args[1], (P3) args[2], (P4) args[3], (P5) args[4],
            (P6) args[5], (P7) args[6], (P8) args[7], (P9) args[8], (P10) args[9]);
      } else {
        return apply(target, (P) args[0], (P2) args[1], (P3) args[2], (P4) args[3], (P5) args[4],
            (P6) args[5], (P7) args[6], (P8) args[7], (P9) args[8], (P10) args[9]);
      }
    }
  }

  public interface BiFunction11<T, P, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, R> extends
      MethodReflectInvoker<T, R> {

    R apply(T t, P p, P2 p2, P3 p3, P4 p4, P5 p5, P6 p6, P7 p7, P8 p8, P9 p9, P10 p10, P11 p11);

    R applyStatic(P p, P2 p2, P3 p3, P4 p4, P5 p5, P6 p6, P7 p7, P8 p8, P9 p9, P10 p10, P11 p11);

    default R invoke(T target, Object... args) {
      if (target == null) {
        return applyStatic((P) args[0], (P2) args[1], (P3) args[2], (P4) args[3], (P5) args[4],
            (P6) args[5], (P7) args[6], (P8) args[7], (P9) args[8], (P10) args[9], (P11) args[10]);
      } else {
        return apply(target, (P) args[0], (P2) args[1], (P3) args[2], (P4) args[3], (P5) args[4],
            (P6) args[5], (P7) args[6], (P8) args[7], (P9) args[8], (P10) args[9], (P11) args[10]);
      }
    }
  }

  public interface BiFunction12<T, P, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, R> extends
      MethodReflectInvoker<T, R> {

    R apply(T t, P p, P2 p2, P3 p3, P4 p4, P5 p5, P6 p6, P7 p7, P8 p8, P9 p9, P10 p10, P11 p11,
        P12 p12);

    R applyStatic(P p, P2 p2, P3 p3, P4 p4, P5 p5, P6 p6, P7 p7, P8 p8, P9 p9, P10 p10, P11 p11,
        P12 p12);

    default R invoke(T target, Object... args) {
      if (target == null) {
        return applyStatic((P) args[0], (P2) args[1], (P3) args[2], (P4) args[3], (P5) args[4],
            (P6) args[5], (P7) args[6], (P8) args[7], (P9) args[8], (P10) args[9], (P11) args[10],
            (P12) args[11]);
      } else {
        return apply(target, (P) args[0], (P2) args[1], (P3) args[2], (P4) args[3], (P5) args[4],
            (P6) args[5], (P7) args[6], (P8) args[7], (P9) args[8], (P10) args[9], (P11) args[10],
            (P12) args[11]);
      }
    }
  }

  public interface BiFunction13<T, P, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, R> extends
      MethodReflectInvoker<T, R> {

    R apply(T t, P p, P2 p2, P3 p3, P4 p4, P5 p5, P6 p6, P7 p7, P8 p8, P9 p9, P10 p10, P11 p11,
        P12 p12, P13 p13);

    R applyStatic(P p, P2 p2, P3 p3, P4 p4, P5 p5, P6 p6, P7 p7, P8 p8, P9 p9, P10 p10, P11 p11,
        P12 p12, P13 p13);

    default R invoke(T target, Object... args) {
      if (target == null) {
        return applyStatic((P) args[0], (P2) args[1], (P3) args[2], (P4) args[3], (P5) args[4],
            (P6) args[5], (P7) args[6], (P8) args[7], (P9) args[8], (P10) args[9], (P11) args[10],
            (P12) args[11], (P13) args[12]);
      } else {
        return apply(target, (P) args[0], (P2) args[1], (P3) args[2], (P4) args[3], (P5) args[4],
            (P6) args[5], (P7) args[6], (P8) args[7], (P9) args[8], (P10) args[9], (P11) args[10],
            (P12) args[11], (P13) args[12]);
      }
    }
  }

  public interface BiFunction14<T, P, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, R> extends
      MethodReflectInvoker<T, R> {

    R apply(T t, P p, P2 p2, P3 p3, P4 p4, P5 p5, P6 p6, P7 p7, P8 p8, P9 p9, P10 p10, P11 p11,
        P12 p12, P13 p13, P14 p14);

    R applyStatic(P p, P2 p2, P3 p3, P4 p4, P5 p5, P6 p6, P7 p7, P8 p8, P9 p9, P10 p10, P11 p11,
        P12 p12, P13 p13, P14 p14);

    default R invoke(T target, Object... args) {
      if (target == null) {
        return applyStatic((P) args[0], (P2) args[1], (P3) args[2], (P4) args[3], (P5) args[4],
            (P6) args[5], (P7) args[6], (P8) args[7], (P9) args[8], (P10) args[9], (P11) args[10],
            (P12) args[11], (P13) args[12], (P14) args[13]);
      } else {
        return apply(target, (P) args[0], (P2) args[1], (P3) args[2], (P4) args[3], (P5) args[4],
            (P6) args[5], (P7) args[6], (P8) args[7], (P9) args[8], (P10) args[9], (P11) args[10],
            (P12) args[11], (P13) args[12], (P14) args[13]);
      }
    }
  }

  public interface BiFunction15<T, P, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, R> extends
      MethodReflectInvoker<T, R> {

    R apply(T t, P p, P2 p2, P3 p3, P4 p4, P5 p5, P6 p6, P7 p7, P8 p8, P9 p9, P10 p10, P11 p11,
        P12 p12, P13 p13, P14 p14, P15 p15);

    R applyStatic(P p, P2 p2, P3 p3, P4 p4, P5 p5, P6 p6, P7 p7, P8 p8, P9 p9, P10 p10, P11 p11,
        P12 p12, P13 p13, P14 p14, P15 p15);

    default R invoke(T target, Object... args) {
      if (target == null) {
        return applyStatic((P) args[0], (P2) args[1], (P3) args[2], (P4) args[3], (P5) args[4],
            (P6) args[5], (P7) args[6], (P8) args[7], (P9) args[8], (P10) args[9], (P11) args[10],
            (P12) args[11], (P13) args[12], (P14) args[13], (P15) args[14]);
      } else {
        return apply(target, (P) args[0], (P2) args[1], (P3) args[2], (P4) args[3], (P5) args[4],
            (P6) args[5], (P7) args[6], (P8) args[7], (P9) args[8], (P10) args[9], (P11) args[10],
            (P12) args[11], (P13) args[12], (P14) args[13], (P15) args[14]);
      }
    }
  }

  public interface BiFunction16<T, P, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, R> extends
      MethodReflectInvoker<T, R> {

    R apply(T t, P p, P2 p2, P3 p3, P4 p4, P5 p5, P6 p6, P7 p7, P8 p8, P9 p9, P10 p10, P11 p11,
        P12 p12, P13 p13, P14 p14, P15 p15, P16 p16);

    R applyStatic(P p, P2 p2, P3 p3, P4 p4, P5 p5, P6 p6, P7 p7, P8 p8, P9 p9, P10 p10, P11 p11,
        P12 p12, P13 p13, P14 p14, P15 p15, P16 p16);

    default R invoke(T target, Object... args) {
      if (target == null) {
        return applyStatic((P) args[0], (P2) args[1], (P3) args[2], (P4) args[3], (P5) args[4],
            (P6) args[5], (P7) args[6], (P8) args[7], (P9) args[8], (P10) args[9], (P11) args[10],
            (P12) args[11], (P13) args[12], (P14) args[13], (P15) args[14], (P16) args[15]);
      } else {
        return apply(target, (P) args[0], (P2) args[1], (P3) args[2], (P4) args[3], (P5) args[4],
            (P6) args[5], (P7) args[6], (P8) args[7], (P9) args[8], (P10) args[9], (P11) args[10],
            (P12) args[11], (P13) args[12], (P14) args[13], (P15) args[14], (P16) args[15]);
      }
    }
  }

  public interface BiFunction17<T, P, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, R> extends
      MethodReflectInvoker<T, R> {

    R apply(T t, P p, P2 p2, P3 p3, P4 p4, P5 p5, P6 p6, P7 p7, P8 p8, P9 p9, P10 p10, P11 p11,
        P12 p12, P13 p13, P14 p14, P15 p15, P16 p16, P17 p17);

    R applyStatic(P p, P2 p2, P3 p3, P4 p4, P5 p5, P6 p6, P7 p7, P8 p8, P9 p9, P10 p10, P11 p11,
        P12 p12, P13 p13, P14 p14, P15 p15, P16 p16, P17 p17);

    default R invoke(T target, Object... args) {
      if (target == null) {
        return applyStatic((P) args[0], (P2) args[1], (P3) args[2], (P4) args[3], (P5) args[4],
            (P6) args[5], (P7) args[6], (P8) args[7], (P9) args[8], (P10) args[9], (P11) args[10],
            (P12) args[11], (P13) args[12], (P14) args[13], (P15) args[14], (P16) args[15],
            (P17) args[16]);
      } else {
        return apply(target, (P) args[0], (P2) args[1], (P3) args[2], (P4) args[3], (P5) args[4],
            (P6) args[5], (P7) args[6], (P8) args[7], (P9) args[8], (P10) args[9], (P11) args[10],
            (P12) args[11], (P13) args[12], (P14) args[13], (P15) args[14], (P16) args[15],
            (P17) args[16]);
      }
    }
  }

  public interface BiFunction18<T, P, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, R> extends
      MethodReflectInvoker<T, R> {

    R apply(T t, P p, P2 p2, P3 p3, P4 p4, P5 p5, P6 p6, P7 p7, P8 p8, P9 p9, P10 p10, P11 p11,
        P12 p12, P13 p13, P14 p14, P15 p15, P16 p16, P17 p17, P18 p18);

    R applyStatic(P p, P2 p2, P3 p3, P4 p4, P5 p5, P6 p6, P7 p7, P8 p8, P9 p9, P10 p10, P11 p11,
        P12 p12, P13 p13, P14 p14, P15 p15, P16 p16, P17 p17, P18 p18);

    default R invoke(T target, Object... args) {
      if (target == null) {
        return applyStatic((P) args[0], (P2) args[1], (P3) args[2], (P4) args[3], (P5) args[4],
            (P6) args[5], (P7) args[6], (P8) args[7], (P9) args[8], (P10) args[9], (P11) args[10],
            (P12) args[11], (P13) args[12], (P14) args[13], (P15) args[14], (P16) args[15],
            (P17) args[16], (P18) args[17]);
      } else {
        return apply(target, (P) args[0], (P2) args[1], (P3) args[2], (P4) args[3], (P5) args[4],
            (P6) args[5], (P7) args[6], (P8) args[7], (P9) args[8], (P10) args[9], (P11) args[10],
            (P12) args[11], (P13) args[12], (P14) args[13], (P15) args[14], (P16) args[15],
            (P17) args[16], (P18) args[17]);
      }
    }
  }

  public interface BiFunction19<T, P, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, R> extends
      MethodReflectInvoker<T, R> {

    R apply(T t, P p, P2 p2, P3 p3, P4 p4, P5 p5, P6 p6, P7 p7, P8 p8, P9 p9, P10 p10, P11 p11,
        P12 p12, P13 p13, P14 p14, P15 p15, P16 p16, P17 p17, P18 p18, P19 p19);

    R applyStatic(P p, P2 p2, P3 p3, P4 p4, P5 p5, P6 p6, P7 p7, P8 p8, P9 p9, P10 p10, P11 p11,
        P12 p12, P13 p13, P14 p14, P15 p15, P16 p16, P17 p17, P18 p18, P19 p19);

    default R invoke(T target, Object... args) {
      if (target == null) {
        return applyStatic((P) args[0], (P2) args[1], (P3) args[2], (P4) args[3], (P5) args[4],
            (P6) args[5], (P7) args[6], (P8) args[7], (P9) args[8], (P10) args[9], (P11) args[10],
            (P12) args[11], (P13) args[12], (P14) args[13], (P15) args[14], (P16) args[15],
            (P17) args[16], (P18) args[17], (P19) args[18]);
      } else {
        return apply(target, (P) args[0], (P2) args[1], (P3) args[2], (P4) args[3], (P5) args[4],
            (P6) args[5], (P7) args[6], (P8) args[7], (P9) args[8], (P10) args[9], (P11) args[10],
            (P12) args[11], (P13) args[12], (P14) args[13], (P15) args[14], (P16) args[15],
            (P17) args[16], (P18) args[17], (P19) args[18]);
      }
    }
  }

  public interface BiFunction20<T, P, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, R> extends
      MethodReflectInvoker<T, R> {

    R apply(T t, P p, P2 p2, P3 p3, P4 p4, P5 p5, P6 p6, P7 p7, P8 p8, P9 p9, P10 p10, P11 p11,
        P12 p12, P13 p13, P14 p14, P15 p15, P16 p16, P17 p17, P18 p18, P19 p19, P20 p20);

    R applyStatic(P p, P2 p2, P3 p3, P4 p4, P5 p5, P6 p6, P7 p7, P8 p8, P9 p9, P10 p10, P11 p11,
        P12 p12, P13 p13, P14 p14, P15 p15, P16 p16, P17 p17, P18 p18, P19 p19, P20 p20);

    default R invoke(T target, Object... args) {
      if (target == null) {
        return applyStatic((P) args[0], (P2) args[1], (P3) args[2], (P4) args[3], (P5) args[4],
            (P6) args[5], (P7) args[6], (P8) args[7], (P9) args[8], (P10) args[9], (P11) args[10],
            (P12) args[11], (P13) args[12], (P14) args[13], (P15) args[14], (P16) args[15],
            (P17) args[16], (P18) args[17], (P19) args[18], (P20) args[19]);
      } else {
        return apply(target, (P) args[0], (P2) args[1], (P3) args[2], (P4) args[3], (P5) args[4],
            (P6) args[5], (P7) args[6], (P8) args[7], (P9) args[8], (P10) args[9], (P11) args[10],
            (P12) args[11], (P13) args[12], (P14) args[13], (P15) args[14], (P16) args[15],
            (P17) args[16], (P18) args[17], (P19) args[18], (P20) args[19]);
      }
    }
  }


  private record LambdaWrapper(Class<?> classz, Method method, Method staticMethod) {

  }

  private static final LambdaWrapper[] LAMBDA_CONSUMER_WRAPPERS;

  private static final LambdaWrapper[] LAMBDA_FUNCTION_WRAPPERS;

  static {
    try {
      LAMBDA_CONSUMER_WRAPPERS = new LambdaWrapper[21];
      // 0-10 参数的 Consumer
      LAMBDA_CONSUMER_WRAPPERS[0] = new LambdaWrapper(BiConsumer0.class,
          BiConsumer0.class.getMethod("accept", Object.class),
          BiConsumer0.class.getMethod("acceptStatic"));
      LAMBDA_CONSUMER_WRAPPERS[1] = new LambdaWrapper(BiConsumer1.class,
          BiConsumer1.class.getMethod("accept", Object.class, Object.class),
          BiConsumer1.class.getMethod("acceptStatic", Object.class));
      LAMBDA_CONSUMER_WRAPPERS[2] = new LambdaWrapper(BiConsumer2.class,
          BiConsumer2.class.getMethod("accept", Object.class, Object.class, Object.class),
          BiConsumer2.class.getMethod("acceptStatic", Object.class, Object.class));
      LAMBDA_CONSUMER_WRAPPERS[3] = new LambdaWrapper(BiConsumer3.class,
          BiConsumer3.class.getMethod("accept", Object.class, Object.class, Object.class,
              Object.class),
          BiConsumer3.class.getMethod("acceptStatic", Object.class, Object.class, Object.class));
      LAMBDA_CONSUMER_WRAPPERS[4] = new LambdaWrapper(BiConsumer4.class,
          BiConsumer4.class.getMethod("accept", Object.class, Object.class, Object.class,
              Object.class, Object.class),
          BiConsumer4.class.getMethod("acceptStatic", Object.class, Object.class, Object.class,
              Object.class));
      LAMBDA_CONSUMER_WRAPPERS[5] = new LambdaWrapper(BiConsumer5.class,
          BiConsumer5.class.getMethod("accept", Object.class, Object.class, Object.class,
              Object.class, Object.class, Object.class),
          BiConsumer5.class.getMethod("acceptStatic", Object.class, Object.class, Object.class,
              Object.class, Object.class));
      LAMBDA_CONSUMER_WRAPPERS[6] = new LambdaWrapper(BiConsumer6.class,
          BiConsumer6.class.getMethod("accept", Object.class, Object.class, Object.class,
              Object.class, Object.class, Object.class, Object.class),
          BiConsumer6.class.getMethod("acceptStatic", Object.class, Object.class, Object.class,
              Object.class, Object.class, Object.class));
      LAMBDA_CONSUMER_WRAPPERS[7] = new LambdaWrapper(BiConsumer7.class,
          BiConsumer7.class.getMethod("accept", Object.class, Object.class, Object.class,
              Object.class, Object.class, Object.class, Object.class, Object.class),
          BiConsumer7.class.getMethod("acceptStatic", Object.class, Object.class, Object.class,
              Object.class, Object.class, Object.class, Object.class));
      LAMBDA_CONSUMER_WRAPPERS[8] = new LambdaWrapper(BiConsumer8.class,
          BiConsumer8.class.getMethod("accept", Object.class, Object.class, Object.class,
              Object.class, Object.class, Object.class, Object.class, Object.class, Object.class),
          BiConsumer8.class.getMethod("acceptStatic", Object.class, Object.class, Object.class,
              Object.class, Object.class, Object.class, Object.class, Object.class));
      LAMBDA_CONSUMER_WRAPPERS[9] = new LambdaWrapper(BiConsumer9.class,
          BiConsumer9.class.getMethod("accept", Object.class, Object.class, Object.class,
              Object.class, Object.class, Object.class, Object.class, Object.class, Object.class,
              Object.class),
          BiConsumer9.class.getMethod("acceptStatic", Object.class, Object.class, Object.class,
              Object.class, Object.class, Object.class, Object.class, Object.class, Object.class));
      LAMBDA_CONSUMER_WRAPPERS[10] = new LambdaWrapper(BiConsumer10.class,
          BiConsumer10.class.getMethod("accept", Object.class, Object.class, Object.class,
              Object.class, Object.class, Object.class, Object.class, Object.class, Object.class,
              Object.class, Object.class),
          BiConsumer10.class.getMethod("acceptStatic", Object.class, Object.class, Object.class,
              Object.class, Object.class, Object.class, Object.class, Object.class, Object.class,
              Object.class));

      // 11-20 参数的 Consumer
      Class<?>[] params11 = new Class<?>[12];
      Arrays.fill(params11, Object.class);
      Class<?>[] staticParams11 = new Class<?>[11];
      Arrays.fill(staticParams11, Object.class);
      LAMBDA_CONSUMER_WRAPPERS[11] = new LambdaWrapper(BiConsumer11.class,
          BiConsumer11.class.getMethod("accept", params11),
          BiConsumer11.class.getMethod("acceptStatic", staticParams11));

      Class<?>[] params12 = new Class<?>[13];
      Arrays.fill(params12, Object.class);
      Class<?>[] staticParams12 = new Class<?>[12];
      Arrays.fill(staticParams12, Object.class);
      LAMBDA_CONSUMER_WRAPPERS[12] = new LambdaWrapper(BiConsumer12.class,
          BiConsumer12.class.getMethod("accept", params12),
          BiConsumer12.class.getMethod("acceptStatic", staticParams12));

      Class<?>[] params13 = new Class<?>[14];
      Arrays.fill(params13, Object.class);
      Class<?>[] staticParams13 = new Class<?>[13];
      Arrays.fill(staticParams13, Object.class);
      LAMBDA_CONSUMER_WRAPPERS[13] = new LambdaWrapper(BiConsumer13.class,
          BiConsumer13.class.getMethod("accept", params13),
          BiConsumer13.class.getMethod("acceptStatic", staticParams13));

      Class<?>[] params14 = new Class<?>[15];
      Arrays.fill(params14, Object.class);
      Class<?>[] staticParams14 = new Class<?>[14];
      Arrays.fill(staticParams14, Object.class);
      LAMBDA_CONSUMER_WRAPPERS[14] = new LambdaWrapper(BiConsumer14.class,
          BiConsumer14.class.getMethod("accept", params14),
          BiConsumer14.class.getMethod("acceptStatic", staticParams14));

      Class<?>[] params15 = new Class<?>[16];
      Arrays.fill(params15, Object.class);
      Class<?>[] staticParams15 = new Class<?>[15];
      Arrays.fill(staticParams15, Object.class);
      LAMBDA_CONSUMER_WRAPPERS[15] = new LambdaWrapper(BiConsumer15.class,
          BiConsumer15.class.getMethod("accept", params15),
          BiConsumer15.class.getMethod("acceptStatic", staticParams15));

      Class<?>[] params16 = new Class<?>[17];
      Arrays.fill(params16, Object.class);
      Class<?>[] staticParams16 = new Class<?>[16];
      Arrays.fill(staticParams16, Object.class);
      LAMBDA_CONSUMER_WRAPPERS[16] = new LambdaWrapper(BiConsumer16.class,
          BiConsumer16.class.getMethod("accept", params16),
          BiConsumer16.class.getMethod("acceptStatic", staticParams16));

      Class<?>[] params17 = new Class<?>[18];
      Arrays.fill(params17, Object.class);
      Class<?>[] staticParams17 = new Class<?>[17];
      Arrays.fill(staticParams17, Object.class);
      LAMBDA_CONSUMER_WRAPPERS[17] = new LambdaWrapper(BiConsumer17.class,
          BiConsumer17.class.getMethod("accept", params17),
          BiConsumer17.class.getMethod("acceptStatic", staticParams17));

      Class<?>[] params18 = new Class<?>[19];
      Arrays.fill(params18, Object.class);
      Class<?>[] staticParams18 = new Class<?>[18];
      Arrays.fill(staticParams18, Object.class);
      LAMBDA_CONSUMER_WRAPPERS[18] = new LambdaWrapper(BiConsumer18.class,
          BiConsumer18.class.getMethod("accept", params18),
          BiConsumer18.class.getMethod("acceptStatic", staticParams18));

      Class<?>[] params19 = new Class<?>[20];
      Arrays.fill(params19, Object.class);
      Class<?>[] staticParams19 = new Class<?>[19];
      Arrays.fill(staticParams19, Object.class);
      LAMBDA_CONSUMER_WRAPPERS[19] = new LambdaWrapper(BiConsumer19.class,
          BiConsumer19.class.getMethod("accept", params19),
          BiConsumer19.class.getMethod("acceptStatic", staticParams19));

      Class<?>[] params20 = new Class<?>[21];
      Arrays.fill(params20, Object.class);
      Class<?>[] staticParams20 = new Class<?>[20];
      Arrays.fill(staticParams20, Object.class);
      LAMBDA_CONSUMER_WRAPPERS[20] = new LambdaWrapper(BiConsumer20.class,
          BiConsumer20.class.getMethod("accept", params20),
          BiConsumer20.class.getMethod("acceptStatic", staticParams20));
    } catch (Throwable e) {
      throw new RuntimeException(e);
    }

    try {
      LAMBDA_FUNCTION_WRAPPERS = new LambdaWrapper[21];
      // 0-10 参数的 Function
      LAMBDA_FUNCTION_WRAPPERS[0] = new LambdaWrapper(BiFunction0.class,
          Function.class.getMethod("apply", Object.class),
          BiFunction0.class.getMethod("applyStatic"));
      LAMBDA_FUNCTION_WRAPPERS[1] = new LambdaWrapper(BiFunction1.class,
          BiFunction.class.getMethod("apply", Object.class, Object.class),
          BiFunction1.class.getMethod("applyStatic", Object.class));
      LAMBDA_FUNCTION_WRAPPERS[2] = new LambdaWrapper(BiFunction2.class,
          BiFunction2.class.getMethod("apply", Object.class, Object.class, Object.class),
          BiFunction2.class.getMethod("applyStatic", Object.class, Object.class));
      LAMBDA_FUNCTION_WRAPPERS[3] = new LambdaWrapper(BiFunction3.class,
          BiFunction3.class.getMethod("apply", Object.class, Object.class, Object.class,
              Object.class),
          BiFunction3.class.getMethod("applyStatic", Object.class, Object.class, Object.class));
      LAMBDA_FUNCTION_WRAPPERS[4] = new LambdaWrapper(BiFunction4.class,
          BiFunction4.class.getMethod("apply", Object.class, Object.class, Object.class,
              Object.class, Object.class),
          BiFunction4.class.getMethod("applyStatic", Object.class, Object.class, Object.class,
              Object.class));
      LAMBDA_FUNCTION_WRAPPERS[5] = new LambdaWrapper(BiFunction5.class,
          BiFunction5.class.getMethod("apply", Object.class, Object.class, Object.class,
              Object.class, Object.class, Object.class),
          BiFunction5.class.getMethod("applyStatic", Object.class, Object.class, Object.class,
              Object.class, Object.class));
      LAMBDA_FUNCTION_WRAPPERS[6] = new LambdaWrapper(BiFunction6.class,
          BiFunction6.class.getMethod("apply", Object.class, Object.class, Object.class,
              Object.class, Object.class, Object.class, Object.class),
          BiFunction6.class.getMethod("applyStatic", Object.class, Object.class, Object.class,
              Object.class, Object.class, Object.class));
      LAMBDA_FUNCTION_WRAPPERS[7] = new LambdaWrapper(BiFunction7.class,
          BiFunction7.class.getMethod("apply", Object.class, Object.class, Object.class,
              Object.class, Object.class, Object.class, Object.class, Object.class),
          BiFunction7.class.getMethod("applyStatic", Object.class, Object.class, Object.class,
              Object.class, Object.class, Object.class, Object.class));
      LAMBDA_FUNCTION_WRAPPERS[8] = new LambdaWrapper(BiFunction8.class,
          BiFunction8.class.getMethod("apply", Object.class, Object.class, Object.class,
              Object.class, Object.class, Object.class, Object.class, Object.class, Object.class),
          BiFunction8.class.getMethod("applyStatic", Object.class, Object.class, Object.class,
              Object.class, Object.class, Object.class, Object.class, Object.class));
      LAMBDA_FUNCTION_WRAPPERS[9] = new LambdaWrapper(BiFunction9.class,
          BiFunction9.class.getMethod("apply", Object.class, Object.class, Object.class,
              Object.class, Object.class, Object.class, Object.class, Object.class, Object.class,
              Object.class),
          BiFunction9.class.getMethod("applyStatic", Object.class, Object.class, Object.class,
              Object.class, Object.class, Object.class, Object.class, Object.class, Object.class));
      LAMBDA_FUNCTION_WRAPPERS[10] = new LambdaWrapper(BiFunction10.class,
          BiFunction10.class.getMethod("apply", Object.class, Object.class, Object.class,
              Object.class, Object.class, Object.class, Object.class, Object.class, Object.class,
              Object.class, Object.class),
          BiFunction10.class.getMethod("applyStatic", Object.class, Object.class, Object.class,
              Object.class, Object.class, Object.class, Object.class, Object.class, Object.class,
              Object.class));

      // 11-20 参数的 Function
      Class<?>[] fParams11 = new Class<?>[12];
      Arrays.fill(fParams11, Object.class);
      Class<?>[] fStaticParams11 = new Class<?>[11];
      Arrays.fill(fStaticParams11, Object.class);
      LAMBDA_FUNCTION_WRAPPERS[11] = new LambdaWrapper(BiFunction11.class,
          BiFunction11.class.getMethod("apply", fParams11),
          BiFunction11.class.getMethod("applyStatic", fStaticParams11));

      Class<?>[] fParams12 = new Class<?>[13];
      Arrays.fill(fParams12, Object.class);
      Class<?>[] fStaticParams12 = new Class<?>[12];
      Arrays.fill(fStaticParams12, Object.class);
      LAMBDA_FUNCTION_WRAPPERS[12] = new LambdaWrapper(BiFunction12.class,
          BiFunction12.class.getMethod("apply", fParams12),
          BiFunction12.class.getMethod("applyStatic", fStaticParams12));

      Class<?>[] fParams13 = new Class<?>[14];
      Arrays.fill(fParams13, Object.class);
      Class<?>[] fStaticParams13 = new Class<?>[13];
      Arrays.fill(fStaticParams13, Object.class);
      LAMBDA_FUNCTION_WRAPPERS[13] = new LambdaWrapper(BiFunction13.class,
          BiFunction13.class.getMethod("apply", fParams13),
          BiFunction13.class.getMethod("applyStatic", fStaticParams13));

      Class<?>[] fParams14 = new Class<?>[15];
      Arrays.fill(fParams14, Object.class);
      Class<?>[] fStaticParams14 = new Class<?>[14];
      Arrays.fill(fStaticParams14, Object.class);
      LAMBDA_FUNCTION_WRAPPERS[14] = new LambdaWrapper(BiFunction14.class,
          BiFunction14.class.getMethod("apply", fParams14),
          BiFunction14.class.getMethod("applyStatic", fStaticParams14));

      Class<?>[] fParams15 = new Class<?>[16];
      Arrays.fill(fParams15, Object.class);
      Class<?>[] fStaticParams15 = new Class<?>[15];
      Arrays.fill(fStaticParams15, Object.class);
      LAMBDA_FUNCTION_WRAPPERS[15] = new LambdaWrapper(BiFunction15.class,
          BiFunction15.class.getMethod("apply", fParams15),
          BiFunction15.class.getMethod("applyStatic", fStaticParams15));

      Class<?>[] fParams16 = new Class<?>[17];
      Arrays.fill(fParams16, Object.class);
      Class<?>[] fStaticParams16 = new Class<?>[16];
      Arrays.fill(fStaticParams16, Object.class);
      LAMBDA_FUNCTION_WRAPPERS[16] = new LambdaWrapper(BiFunction16.class,
          BiFunction16.class.getMethod("apply", fParams16),
          BiFunction16.class.getMethod("applyStatic", fStaticParams16));

      Class<?>[] fParams17 = new Class<?>[18];
      Arrays.fill(fParams17, Object.class);
      Class<?>[] fStaticParams17 = new Class<?>[17];
      Arrays.fill(fStaticParams17, Object.class);
      LAMBDA_FUNCTION_WRAPPERS[17] = new LambdaWrapper(BiFunction17.class,
          BiFunction17.class.getMethod("apply", fParams17),
          BiFunction17.class.getMethod("applyStatic", fStaticParams17));

      Class<?>[] fParams18 = new Class<?>[19];
      Arrays.fill(fParams18, Object.class);
      Class<?>[] fStaticParams18 = new Class<?>[18];
      Arrays.fill(fStaticParams18, Object.class);
      LAMBDA_FUNCTION_WRAPPERS[18] = new LambdaWrapper(BiFunction18.class,
          BiFunction18.class.getMethod("apply", fParams18),
          BiFunction18.class.getMethod("applyStatic", fStaticParams18));

      Class<?>[] fParams19 = new Class<?>[20];
      Arrays.fill(fParams19, Object.class);
      Class<?>[] fStaticParams19 = new Class<?>[19];
      Arrays.fill(fStaticParams19, Object.class);
      LAMBDA_FUNCTION_WRAPPERS[19] = new LambdaWrapper(BiFunction19.class,
          BiFunction19.class.getMethod("apply", fParams19),
          BiFunction19.class.getMethod("applyStatic", fStaticParams19));

      Class<?>[] fParams20 = new Class<?>[21];
      Arrays.fill(fParams20, Object.class);
      Class<?>[] fStaticParams20 = new Class<?>[20];
      Arrays.fill(fStaticParams20, Object.class);
      LAMBDA_FUNCTION_WRAPPERS[20] = new LambdaWrapper(BiFunction20.class,
          BiFunction20.class.getMethod("apply", fParams20),
          BiFunction20.class.getMethod("applyStatic", fStaticParams20));
    } catch (Throwable e) {
      throw new RuntimeException(e);
    }
  }

  public static MethodReflectInvoker createLambda(Method findMethod) throws Throwable {

    if (!isSupportFixLambda(findMethod)) {
      throw new IllegalArgumentException(
          "Method " + findMethod + " param count > " + MAX_SUPPORT_PARAMS_COUNT);
    }

    // step1: method => methodHandle
    Lookup lookup = MethodHandles.privateLookupIn(findMethod.getDeclaringClass(),
        MethodHandles.lookup());
    MethodHandle handle = lookup.unreflect(findMethod);

    // step2: 获取方法对应的Lambda的包装类和方法
    final LambdaWrapper lambdaWrapper = getLambdaWrapper(findMethod);

    // step3: 构造CallSite生成lambda
    final CallSite callSite = createCallSite(findMethod, lookup, lambdaWrapper, handle);

    MethodReflectInvoker invoker;
    try {
      //严格数据类型,性能最高,动态性的很难达到,尝试一下(try)
      invoker = (MethodReflectInvoker) callSite.getTarget().invokeExact();
    } catch (Throwable e) {
      //大部分走得还是这个
      invoker = (MethodReflectInvoker) callSite.getTarget().invoke();
    }

    if (invoker == null) {
      throw new IllegalArgumentException(
          "MethodReflectInvoker create fail for method: " + findMethod);
    }

    if (Modifier.isStatic(findMethod.getModifiers())) {
      //做适配,防止调用静态方法的时候第第一个参数用户乱传导致异常问题
      return new StaticFixedLambdaMethodReflectInvoker(findMethod, invoker);
    }

    return invoker;
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
      //无适配���参数
      throw new IllegalArgumentException(
          "Method " + method.getName() + " has too many parameters: " + parameterCount
              + ". Maximum supported is " + MAX_SUPPORT_PARAMS_COUNT + ".");
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
      final Class<?> lambdaClass = lambdaWrapper.classz;
      final Method lambdaMethod;
      final MethodType lambdaMethodType;
      if (Modifier.isStatic(method.getModifiers())) {
        lambdaMethod = lambdaWrapper.staticMethod;
        lambdaMethodType = methodType(lambdaMethod.getReturnType(),
            boxPrimitiveTypes(method.getParameterTypes()));
      } else {
        lambdaMethod = lambdaWrapper.method;
        lambdaMethodType = methodType(method.getReturnType(), method.getDeclaringClass(),
            boxPrimitiveTypes(method.getParameterTypes()));
      }
      return LambdaMetafactory.metafactory(lookup, lambdaMethod.getName(), methodType(lambdaClass),
          // 返回值,参数 (lambda函数)
          methodType(lambdaMethod.getReturnType(), lambdaMethod.getParameterTypes()),
          // handle
          handle,
          // 返回值 类型，参数 (lambda实现方法参数)
          lambdaMethodType);
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
