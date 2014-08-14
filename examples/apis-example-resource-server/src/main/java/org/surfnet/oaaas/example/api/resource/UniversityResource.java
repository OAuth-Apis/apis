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
package org.surfnet.oaaas.example.api.resource;

import java.security.Principal;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.surfnet.oaaas.example.api.domain.Course;
import org.surfnet.oaaas.example.api.domain.Student;
import org.surfnet.oaaas.example.api.domain.University;

import com.yammer.dropwizard.auth.Auth;
import com.yammer.metrics.annotation.Timed;

/**
 * Main resource
 * 
 */

@Path("/v1/api")
@Produces(MediaType.APPLICATION_JSON)
public class UniversityResource {

  private static final String UNIVERSITY_FOO_JSON = "university-foo-data.json";

  private final ObjectMapper objectMapper = new ObjectMapper().enable(
      DeserializationConfig.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY).setSerializationInclusion(
      JsonSerialize.Inclusion.NON_NULL);

  private University university;

  public UniversityResource() {
    super();
    init();
  }

  private void init() {
    try {
      university = getObjectMapper().readValue(
          Thread.currentThread().getContextClassLoader().getResourceAsStream(UNIVERSITY_FOO_JSON), University.class);
    } catch (Exception e) {
      throw new RuntimeException(String.format("Unable to parse %s", UNIVERSITY_FOO_JSON), e);
    }
  }

  protected ObjectMapper getObjectMapper() {
    return objectMapper;
  }

  @GET
  @Timed
  @Path("/student")
  public Response getAllStudents(@Auth
  Principal principal) {
    return Response.ok(university.getStudents()).build();
  }

  @GET
  @Timed
  @Path("/student/{studentId}")
  public Response getStudentById(@Auth
  Principal principal, @PathParam("studentId")
  String id) {
    List<Student> students = university.getStudents();
    for (Student student : students) {
      if (student.getId().equals(id)) {
        return Response.ok(student).build();
      }
    }
    return Response.status(Response.Status.NOT_FOUND).build();
  }

  @GET
  @Timed
  @Path("/course")
  public Response getAllCourses(@Auth
  Principal principal) {
    return Response.ok(university.getCourses()).build();
  }

  @GET
  @Timed
  @Path("/course/{courseId}")
  public Response getCourseById(@Auth
  Principal principal, @PathParam("courseId")
  String id) {
    List<Course> courses = university.getCourses();
    for (Course course : courses) {
      if (course.getId().equals(id)) {
        return Response.ok(course).build();
      }
    }
    return Response.status(Response.Status.NOT_FOUND).build();
  }

}
