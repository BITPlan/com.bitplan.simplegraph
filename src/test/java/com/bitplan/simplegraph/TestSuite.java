package com.bitplan.simplegraph;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import com.bitplan.simplegraph.TestFileSystem;

@RunWith(Suite.class)
@Suite.SuiteClasses({ TestFileSystem.class,TestTinkerPop3.class})
/**
 * TestSuite
 * @author wf
 *
 * no content necessary - annotation has info
 */
public class TestSuite {
}

