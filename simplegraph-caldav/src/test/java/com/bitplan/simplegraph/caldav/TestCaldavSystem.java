package com.bitplan.simplegraph.caldav;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.InputStream;
import java.net.URL;

import org.junit.Test;

import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.ComponentList;
import net.fortuna.ical4j.model.component.CalendarComponent;
import net.fortuna.ical4j.util.CompatibilityHints;

public class TestCaldavSystem {

  @Test
  public void testCaldavaAccess() throws Exception {
    String icsUrl = "https://www.schulferien.eu/downloads/ical4.php?land=10&type=1&year=2018";
    InputStream urlStream = new URL(icsUrl).openStream();
    // we are not having ical4j.properties yet
     // ical4j.parsing.relaxed=true;
    CompatibilityHints.setHintEnabled(CompatibilityHints.KEY_RELAXED_UNFOLDING, true);
    CompatibilityHints.setHintEnabled(CompatibilityHints.KEY_RELAXED_PARSING,true);
    CalendarBuilder builder = new CalendarBuilder();
    Calendar calendar = builder.build(urlStream);
    ComponentList<CalendarComponent> components = calendar.getComponents();
    assertEquals(5,components.size());
    fail("Not yet implemented");
  }

}
