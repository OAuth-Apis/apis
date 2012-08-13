/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.surfnet.oaaas.example.api.domain;

import java.util.List;

/**
 * Root domain object for our dummy API
 * 
 */
public class University {
  private String name;
  private List<Student> students;
  private List<Course> courses;

  /**
   * @return the name
   */
  public String getName() {
    return name;
  }

  /**
   * @param name
   *          the name to set
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * @return the students
   */
  public List<Student> getStudents() {
    return students;
  }

  /**
   * @param students
   *          the students to set
   */
  public void setStudents(List<Student> students) {
    this.students = students;
  }

  /**
   * @return the courses
   */
  public List<Course> getCourses() {
    return courses;
  }

  /**
   * @param courses
   *          the courses to set
   */
  public void setCourses(List<Course> courses) {
    this.courses = courses;
  }

}
