/**
 * Copyright (c) 2018 BITPlan GmbH
 *
 * http://www.bitplan.com
 *
 * This file is part of the Opensource project at:
 * https://github.com/BITPlan/com.bitplan.simplegraph
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.bitplan.simplegraph.impl;

import java.util.ArrayList;
import java.util.List;

/**
 * generic holder to be use in e.g. in lambdas to access outer scope variables which need to be final
 * @author wf
 *
 * @param <T>
 */
public class Holder<T> {
  private List<T> values=new ArrayList<T>();
  private Class<T> clazz;
  
  public Holder() {
  }
  
  public boolean isPresent() {
    return values.size()>0;
  }
  
  /**
   * construct this holder from a value
   * @param value
   */
  @SuppressWarnings("unchecked")
  public Holder(T value) {
    values.add(value);
    clazz=(Class<T>) value.getClass();
  }
  
  /**
   * get the first held value or null if it is not present
   * @return the first value
   */
  public T getFirstValue() {
    if (isPresent())
      return values.get(0);
    else
      return null;
  }
  
  public List<T> getValues() {
    return values;
  }
  
  public void setValue(T value) {
    values.clear();
    add(value);
  }

  public void add(T value) {
    values.add(value);
  }

  /**
   * add all holded values from the other holder
   * @param other
   */
  public void addAll(Holder<T> other) {
    other.getValues().forEach(otherT->this.add(otherT));
  }
  public static <T> T convertInstanceOfObject(Object o, Class<T> clazz) {
    try {
      return clazz.cast(o);
  } catch(ClassCastException e) {
      return null;
  }
  }

  public void increment() {
    if (this.isPresent()) {
      Object o=this.getFirstValue();
      if (o instanceof Integer) {
        int i=(Integer)o;
        Integer i1 = i+1;
        this.setValue(convertInstanceOfObject(i1,clazz));
      }
    }
  }
}
