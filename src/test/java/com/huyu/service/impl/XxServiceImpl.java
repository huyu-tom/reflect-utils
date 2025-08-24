/*
 * Copyright 2010-2025 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.huyu.service.impl;

import com.huyu.service.XxService;

public class XxServiceImpl implements XxService<XxServiceImpl1> {

  public long cc;
  String aa;


  public static long staticAdd(long d) {
    int a = 1;
    int b = 2;
    int c = 8;
    return a + b + c;
  }

  static long staticAdd1() {
    return 100L;
  }

  static long staticAdd1(long a, Long b, Integer c, XxService xxService) {
    return a + b + c;
  }

  private static long privateStaticAdd1(long a, Long b, Integer c, XxService xxService) {
    return a + b + c;
  }

  private static long privateStaticAdd1() {
    return 100L;
  }

  long notPrivateAdd() {
    int a = 1;
    int b = 2;
    int c = 4;
    return a + b + c;
  }

  long notPrivateAdd(long a, Long b, Integer c, XxService xxService) {
    return a + b + c;
  }

  private long privateAdd() {
    int a = 1;
    int b = 2;
    int c = 4;
    return a + b + c;
  }

  private long privateAdd(long a, Long b, Integer c, XxService xxService) {
    return a + b + c;
  }

  @Override
  public void remove(XxServiceImpl1 object) {

  }

  protected void test() {
    int a = 1;
    int b = 2;
    int c = 3;
    cc = a + b + c;
  }

  public long add(int a) {
    cc = a;
    return cc;
  }

  public void add(String pass) {
    aa = pass;
  }

  /**
   * AOP方法
   */
  @Override
  public void add() {
    int a = 1;
    int b = 2;
    int c = 3;
    cc = a + b + c;
  }

  @Override
  public void remove() {
    int a = 1;
    int b = 2;
    int c = 3;
    cc = a + b + c;
  }

  @Override
  public String add(String username, String password) {
    int a = 1;
    int b = 2;
    int c = 3;
    cc = a + b + c;
    return username;
  }

  @Override
  public long add(long size, long time) {
    return size + time;
  }

  @Override
  public void remove(String username, String password) {
    int a = 1;
    int b = 2;
    int c = 3;
    cc = a + b + c;
  }

  /**
   * 设置装箱拆箱
   *
   * @param username
   * @param password
   * @param userId
   * @param age
   */
  @Override
  public Long add(String username, String password, Long userId, Integer age, byte width,
      long height, short length, int weight) {
    int a = 1;
    int b = 2;
    int c = 3;
    cc = a + b + c + userId + age + width + height + length + weight;
    return cc;
  }

  @Override
  public void remove(String username, String password, Long userId, Integer age) {
    int a = 1;
    int b = 2;
    int c = 3;
    cc = a + b + c;
  }
}
