package org.picketbox;

import org.jboss.logging.BasicLogger;
import org.jboss.logging.Logger;
import org.jboss.logging.MessageLogger;

@MessageLogger(projectCode = "PBOX")
public interface PicketBoxLogger extends BasicLogger {

    PicketBoxLogger LOGGER = Logger.getMessageLogger(PicketBoxLogger.class, PicketBoxLogger.class.getPackage().getName());

}
