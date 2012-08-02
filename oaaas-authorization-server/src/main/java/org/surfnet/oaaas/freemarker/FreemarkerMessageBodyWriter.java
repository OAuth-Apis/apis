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
package org.surfnet.oaaas.freemarker;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.inject.Named;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateException;

/**
 * {@link MessageBodyWriter} capable of rendering {@link Model}
 * 
 */
@Named
@Produces(MediaType.TEXT_HTML)
@Provider
public class FreemarkerMessageBodyWriter implements MessageBodyWriter<Model> {

  private static Configuration configuration;

  /*
   * (non-Javadoc)
   * 
   * @see javax.ws.rs.ext.MessageBodyWriter#isWriteable(java.lang.Class,
   * java.lang.reflect.Type, java.lang.annotation.Annotation[],
   * javax.ws.rs.core.MediaType)
   */
  @Override
  public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
    return Model.class.isAssignableFrom(type);
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.ws.rs.ext.MessageBodyWriter#getSize(java.lang.Object,
   * java.lang.Class, java.lang.reflect.Type, java.lang.annotation.Annotation[],
   * javax.ws.rs.core.MediaType)
   */
  @Override
  public long getSize(Model t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
    return -1;
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.ws.rs.ext.MessageBodyWriter#writeTo(java.lang.Object,
   * java.lang.Class, java.lang.reflect.Type, java.lang.annotation.Annotation[],
   * javax.ws.rs.core.MediaType, javax.ws.rs.core.MultivaluedMap,
   * java.io.OutputStream)
   */
  @Override
  public void writeTo(Model model, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType,
      MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException,
      WebApplicationException {
    final Template template = getConfiguration().getTemplate(model.getView());

    try {
      template.process(model.getModel(), new OutputStreamWriter(entityStream));
    } catch (TemplateException e) {
      throw new RuntimeException(e);
    }
  }

  private Configuration getConfiguration() {
    if (configuration == null) {
      configuration = new Configuration();
      configuration.setClassForTemplateLoading(getClass(), null);
      configuration.setObjectWrapper(new DefaultObjectWrapper());
    }
    return configuration;
  }

}
