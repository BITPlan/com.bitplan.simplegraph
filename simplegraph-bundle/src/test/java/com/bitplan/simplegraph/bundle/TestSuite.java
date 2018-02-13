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
package com.bitplan.simplegraph.bundle;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import com.bitplan.powerpoint.TestPowerPoint;
import com.bitplan.simplegraph.core.TestTinkerPop3;
import com.bitplan.simplegraph.filesystem.TestFileSystem;
import com.bitplan.simplegraph.map.TestMapSystem;
import com.bitplan.simplegraph.mediawiki.TestMediaWiki;
import com.bitplan.simplegraph.smw.TestSMW;
import com.bitplan.simplegraph.sql.TestSQL;
import com.bitplan.simplegraph.triplestore.TestTripleStore;
import com.bitplan.simplegraph.wikidata.TestWikiData;

@RunWith(Suite.class)
@Suite.SuiteClasses({ TestFileSystem.class, 
    TestTinkerPop3.class,
    TestTripleStore.class, 
    TestPowerPoint.class, TestMediaWiki.class,
    TestMapSystem.class, TestSQL.class, TestSMW.class, TestWikiData.class,
    com.bitplan.simplegraph.core.TestRythm.class,
    com.bitplan.simplegraph.filesystem.TestRythm.class,
    com.bitplan.simplegraph.map.TestRythm.class
    })
/**
 * TestSuite
 * 
 * @author wf
 *
 *         no content necessary - annotation has info
 */
public class TestSuite {
}
