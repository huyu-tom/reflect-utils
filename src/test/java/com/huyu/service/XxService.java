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
package com.huyu.service;

public interface XxService<T> {

  void remove(T t);

  long add(int a);

  void add(String a);

  void add();

  void remove();

  String add(String username, String password);

  long add(long size, long time);

  void remove(String username, String password);

  Long add(String username, String password, Long userId, Integer age, byte width, long height,
    short length, int weight);

  void remove(String username, String password, Long userId, Integer age);
}
