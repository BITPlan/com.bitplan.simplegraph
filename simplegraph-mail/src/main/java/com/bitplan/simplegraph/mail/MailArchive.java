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
package com.bitplan.simplegraph.mail;

import java.util.List;

import org.apache.james.mime4j.dom.Entity;
import org.apache.james.mime4j.dom.Message;

/**
 * generic MailArchive Interface
 * 
 * @author wf
 *
 */
public interface MailArchive {
  /**
   * return the content of a mail by it's id
   * 
   * @param id
   *          - the id to look for
   * @return - the message
   * @throws Exception
   *           - if getting the mail by it's id fails
   */
  public Message getMailById(String id) throws Exception;

  /**
   * get the list of messages with the given subject
   * 
   * @param subject
   *          - the subject to search for
   * @return - the list of mails having such a subject
   * @throws Exception
   *           - if there is an issue
   */
  public List<Message> getMailBySubject(String subject) throws Exception;

  /**
   * get the attachments for the given message
   * 
   * @param msg
   *          - the message to get the attachments for
   * @return - the list of attachments
   */
  public List<Entity> getAttachments(Message msg);

  /**
   * force reloading the mailarchive from the backing storage
   * 
   * @throws Exception
   */
  public void reload() throws Exception;
}
