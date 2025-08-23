package com.huyu.field.invoker.impl;

import com.huyu.field.invoker.FieldReflectInvoker;
import com.huyu.utils.UnsafeUtils;
import java.lang.reflect.Field;


public class UnsafeReflectFieldInvoker implements FieldReflectInvoker<Object, Object> {


  private final long FIELD_OFFSET;

  private final boolean isPrimitive;

  private final Class<?> type;

  public UnsafeReflectFieldInvoker(Field field) {
    if (!UnsafeUtils.isSupportUnsafe()) {
      throw new UnsupportedOperationException("Unsafe is not supported on this platform.");
    }
    FIELD_OFFSET = UnsafeUtils.UNSAFE.objectFieldOffset(field);
    type = field.getType();
    isPrimitive = type.isPrimitive();
  }

  @Override
  public void set(Object target, Object arg) {
    if (isPrimitive) {
      if (type == byte.class) {
        UnsafeUtils.UNSAFE.putByte(target, FIELD_OFFSET, (byte) arg);
      } else if (type == short.class) {
        UnsafeUtils.UNSAFE.putShort(target, FIELD_OFFSET, (short) arg);
      } else if (type == char.class) {
        UnsafeUtils.UNSAFE.putChar(target, FIELD_OFFSET, (char) arg);
      } else if (type == int.class) {
        UnsafeUtils.UNSAFE.putInt(target, FIELD_OFFSET, (int) arg);
      } else if (type == long.class) {
        UnsafeUtils.UNSAFE.putLong(target, FIELD_OFFSET, (long) arg);
      } else if (type == float.class) {
        UnsafeUtils.UNSAFE.putFloat(target, FIELD_OFFSET, (float) arg);
      } else if (type == double.class) {
        UnsafeUtils.UNSAFE.putDouble(target, FIELD_OFFSET, (double) arg);
      } else {
        throw new IllegalArgumentException("Unsupported primitive type: " + type);
      }
    } else {
      UnsafeUtils.UNSAFE.putObject(target, FIELD_OFFSET, arg);
    }
  }

  @Override
  public Object get(Object target) {
    if (isPrimitive) {
      if (type == byte.class) {
        return UnsafeUtils.UNSAFE.getByte(target, FIELD_OFFSET);
      } else if (type == short.class) {
        return UnsafeUtils.UNSAFE.getShort(target, FIELD_OFFSET);
      } else if (type == char.class) {
        return UnsafeUtils.UNSAFE.getChar(target, FIELD_OFFSET);
      } else if (type == int.class) {
        return UnsafeUtils.UNSAFE.getInt(target, FIELD_OFFSET);
      } else if (type == long.class) {
        return UnsafeUtils.UNSAFE.getLong(target, FIELD_OFFSET);
      } else if (type == float.class) {
        return UnsafeUtils.UNSAFE.getFloat(target, FIELD_OFFSET);
      } else if (type == double.class) {
        return UnsafeUtils.UNSAFE.getDouble(target, FIELD_OFFSET);
      } else {
        throw new IllegalArgumentException("Unsupported primitive type: " + type);
      }
    } else {
      return UnsafeUtils.UNSAFE.getObject(target, FIELD_OFFSET);
    }
  }
}
