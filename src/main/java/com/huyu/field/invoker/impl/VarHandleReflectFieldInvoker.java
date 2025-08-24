package com.huyu.field.invoker.impl;

import com.huyu.field.invoker.FieldReflectInvoker;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.lang.reflect.AccessFlag;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class VarHandleReflectFieldInvoker implements FieldReflectInvoker<Object, Object> {

  private static final MethodHandles.Lookup SHARED_LOOKUP = MethodHandles.lookup();

  private final VarHandle varHandle;

  private final boolean staticField;

  public VarHandleReflectFieldInvoker(Field field) {
    try {
      MethodHandles.Lookup lookup;
      if (isPublicField(field)) {
        lookup = SHARED_LOOKUP;
      } else {
        lookup = MethodHandles.privateLookupIn(field.getDeclaringClass(), SHARED_LOOKUP);
      }

      if (staticField = Modifier.isStatic(field.getModifiers())) {
        varHandle = lookup.findStaticVarHandle(field.getDeclaringClass(), field.getName(),
            field.getType());
      } else {
        varHandle = lookup.findVarHandle(field.getDeclaringClass(), field.getName(),
            field.getType());
      }
    } catch (IllegalAccessException e) {
      throw new RuntimeException("Unable to get VarHandle", e);
    } catch (NoSuchFieldException e) {
      throw new RuntimeException("Field not found: " + field.getName(), e);
    }
  }

  private boolean isPublicField(Field field) {
    return field.accessFlags().contains(AccessFlag.PUBLIC);
  }

  @Override
  public void set(Object target, Object arg) {
    if (staticField) {
      varHandle.set(arg);
    } else {
      varHandle.set(target, arg);
    }
  }

  @Override
  public Object get(Object target) {
    if (staticField) {
      return varHandle.get();
    } else {
      return varHandle.get(target);
    }
  }
}
