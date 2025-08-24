package com.huyu.field.invoker.impl;

import com.huyu.field.invoker.FieldReflectInvoker;
import com.huyu.utils.UnsafeUtils;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;


public class UnsafeReflectFieldInvoker implements FieldReflectInvoker<Object, Object> {


  private final long FIELD_OFFSET;

  private final Object base;

  private final boolean staticField;

  private final boolean isPrimitive;

  private final Class<?> type;

  public UnsafeReflectFieldInvoker(Field field) {
    if (!UnsafeUtils.isSupportUnsafe()) {
      throw new UnsupportedOperationException("Unsafe is not supported on this platform.");
    }
    if (staticField = Modifier.isStatic(field.getModifiers())) {
      FIELD_OFFSET = UnsafeUtils.UNSAFE.staticFieldOffset(field);
      base = UnsafeUtils.UNSAFE.staticFieldBase(field);
    } else {
      FIELD_OFFSET = UnsafeUtils.UNSAFE.objectFieldOffset(field);
      base = null;
    }
    type = field.getType();
    isPrimitive = type.isPrimitive();
  }

  @Override
  public void set(Object target, Object arg) {
    final Object baseTarget = staticField ? base : target;
    if (isPrimitive) {
      if (type == byte.class) {
        UnsafeUtils.UNSAFE.putByte(baseTarget, FIELD_OFFSET, (byte) arg);
      } else if (type == short.class) {
        UnsafeUtils.UNSAFE.putShort(baseTarget, FIELD_OFFSET, (short) arg);
      } else if (type == char.class) {
        UnsafeUtils.UNSAFE.putChar(baseTarget, FIELD_OFFSET, (char) arg);
      } else if (type == int.class) {
        UnsafeUtils.UNSAFE.putInt(baseTarget, FIELD_OFFSET, (int) arg);
      } else if (type == long.class) {
        UnsafeUtils.UNSAFE.putLong(baseTarget, FIELD_OFFSET, (long) arg);
      } else if (type == float.class) {
        UnsafeUtils.UNSAFE.putFloat(baseTarget, FIELD_OFFSET, (float) arg);
      } else if (type == double.class) {
        UnsafeUtils.UNSAFE.putDouble(baseTarget, FIELD_OFFSET, (double) arg);
      } else {
        throw new IllegalArgumentException("Unsupported primitive type: " + type);
      }
    } else {
      UnsafeUtils.UNSAFE.putObject(baseTarget, FIELD_OFFSET, arg);
    }
  }

  @Override
  public Object get(Object target) {
    final Object baseTarget = staticField ? base : target;
    if (isPrimitive) {
      if (type == byte.class) {
        return UnsafeUtils.UNSAFE.getByte(baseTarget, FIELD_OFFSET);
      } else if (type == short.class) {
        return UnsafeUtils.UNSAFE.getShort(baseTarget, FIELD_OFFSET);
      } else if (type == char.class) {
        return UnsafeUtils.UNSAFE.getChar(baseTarget, FIELD_OFFSET);
      } else if (type == int.class) {
        return UnsafeUtils.UNSAFE.getInt(baseTarget, FIELD_OFFSET);
      } else if (type == long.class) {
        return UnsafeUtils.UNSAFE.getLong(baseTarget, FIELD_OFFSET);
      } else if (type == float.class) {
        return UnsafeUtils.UNSAFE.getFloat(baseTarget, FIELD_OFFSET);
      } else if (type == double.class) {
        return UnsafeUtils.UNSAFE.getDouble(baseTarget, FIELD_OFFSET);
      } else {
        throw new IllegalArgumentException("Unsupported primitive type: " + type);
      }
    } else {
      return UnsafeUtils.UNSAFE.getObject(baseTarget, FIELD_OFFSET);
    }
  }
}
