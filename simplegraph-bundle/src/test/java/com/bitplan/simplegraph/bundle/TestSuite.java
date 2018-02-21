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

import com.bitplan.simplegraph.core.TestTinkerPop3;
import com.bitplan.simplegraph.excel.TestExcelSystem;
import com.bitplan.simplegraph.filesystem.TestFileSystem;
import com.bitplan.simplegraph.json.TestJsonSystem;
import com.bitplan.simplegraph.map.TestMapSystem;
import com.bitplan.simplegraph.mediawiki.TestMediaWikiSystem;
import com.bitplan.simplegraph.pdf.TestPdfSystem;
import com.bitplan.simplegraph.powerpoint.TestPowerPointSystem;
import com.bitplan.simplegraph.smw.TestSMW;
import com.bitplan.simplegraph.sql.TestSQLSystem;
import com.bitplan.simplegraph.triplestore.TestTripleStore;
import com.bitplan.simplegraph.wikidata.TestWikiData;
import com.bitplan.simplegraph.word.TestWordSystem;

@RunWith(Suite.class)
@Suite.SuiteClasses({ TestExcelSystem.class, TestFileSystem.class,
    TestJsonSystem.class, TestTinkerPop3.class, TestTripleStore.class,
    TestPowerPointSystem.class, TestMediaWikiSystem.class, TestMapSystem.class,
    TestPdfSystem.class, TestPDFFiles.class, TestSQLSystem.class, TestSMW.class,
    TestWikiData.class, TestWordSystem.class,
    com.bitplan.simplegraph.core.TestRythm.class,
    com.bitplan.simplegraph.filesystem.TestRythm.class,
    com.bitplan.simplegraph.map.TestRythm.class })
/**
 * TestSuite
 * 
 * @author wf
 *
 *         no content necessary - annotation has info
 */
public class TestSuite {
}
