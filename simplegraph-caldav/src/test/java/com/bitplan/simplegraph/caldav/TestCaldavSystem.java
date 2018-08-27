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
package com.bitplan.simplegraph.caldav;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.InputStream;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.junit.Test;

import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.ComponentList;
import net.fortuna.ical4j.model.component.CalendarComponent;

/**
 * test the Caldav System
 * 
 * @author wf
 *
 */
public class TestCaldavSystem {

  /**
   * disable SSL
   */
  private void disableSslVerification() {
    try {
      // Create a trust manager that does not validate certificate chains
      TrustManager[] trustAllCerts = new TrustManager[] {
          new X509TrustManager() {
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
              return null;
            }

            public void checkClientTrusted(X509Certificate[] certs,
                String authType) {
            }

            public void checkServerTrusted(X509Certificate[] certs,
                String authType) {
            }
          } };

      // Install the all-trusting trust manager
      SSLContext sc = SSLContext.getInstance("SSL");
      sc.init(null, trustAllCerts, new java.security.SecureRandom());
      HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

      // Create all-trusting host name verifier
      HostnameVerifier allHostsValid = new HostnameVerifier() {
        public boolean verify(String hostname, SSLSession session) {
          return true;
        }
      };

      // Install the all-trusting host verifier
      HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
    } catch (KeyManagementException e) {
      e.printStackTrace();
    }
  }

  @Test
  public void testCaldavAccess() throws Exception {
    this.disableSslVerification();
    String icsUrl = "https://www.schulferien.eu/downloads/ical4.php?land=10&type=1&year=2018";
    InputStream urlStream = new URL(icsUrl).openStream();
    // we are having ical4j.properties in src/main/resources know
    // ical4j.parsing.relaxed=true;
    // CompatibilityHints.setHintEnabled(CompatibilityHints.KEY_RELAXED_UNFOLDING,
    // true);
    // CompatibilityHints.setHintEnabled(CompatibilityHints.KEY_RELAXED_PARSING,true);
    CalendarBuilder builder = new CalendarBuilder();
    Calendar calendar = builder.build(urlStream);
    System.out.println(calendar.getProperties());
    ComponentList<CalendarComponent> components = calendar.getComponents();
    assertEquals(5, components.size());
    // fail("Not yet implemented");
  }

}
