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
package com.bitplan.json;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.math.BigDecimal;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

/**
 * Json Pretty printer based on Googles gson
 * https://sites.google.com/site/gson/streaming
 * @author wf
 *
 */
public class JsonPrettyPrinter {
  
  /**
   * pretty print the given json string
   * @param json
   * @return
   * @throws Exception
   */
  public static String prettyPrint(String json) throws Exception {
    InputStream in = new ByteArrayInputStream( json.getBytes( "UTF-8" ) );
    OutputStream out=new ByteArrayOutputStream();
    prettyPrint(in,out,false,true);
    return out.toString();
  }

  /**
   * pretty print
   * @param in
   * @param out
   * @param compact
   * @param lenient
   * @throws Exception
   */
  public static void prettyPrint(InputStream in, OutputStream out,
      boolean compact, boolean lenient) throws Exception {
    JsonWriter writer = new JsonWriter(new OutputStreamWriter(out));
    JsonReader reader = new JsonReader(new InputStreamReader(in));
    if (!compact) {
      writer.setIndent("    ");
    }
    reader.setLenient(lenient);
    prettyprint(reader, writer);
    writer.close();
    reader.close();
  }

  static void prettyprint(JsonReader reader, JsonWriter writer)
      throws IOException {
    while (true) {
      JsonToken token = reader.peek();
      switch (token) {
      case BEGIN_ARRAY:
        reader.beginArray();
        writer.beginArray();
        break;
      case END_ARRAY:
        reader.endArray();
        writer.endArray();
        break;
      case BEGIN_OBJECT:
        reader.beginObject();
        writer.beginObject();
        break;
      case END_OBJECT:
        reader.endObject();
        writer.endObject();
        break;
      case NAME:
        String name = reader.nextName();
        writer.name(name);
        break;
      case STRING:
        String s = reader.nextString();
        writer.value(s);
        break;
      case NUMBER:
        String n = reader.nextString();
        writer.value(new BigDecimal(n));
        break;
      case BOOLEAN:
        boolean b = reader.nextBoolean();
        writer.value(b);
        break;
      case NULL:
        reader.nextNull();
        writer.nullValue();
        break;
      case END_DOCUMENT:
        return;
      }
    }
  }

}
