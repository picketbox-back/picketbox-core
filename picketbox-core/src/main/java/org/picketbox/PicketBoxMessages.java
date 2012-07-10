package org.picketbox;

import java.io.IOException;

import javax.servlet.ServletException;

import org.jboss.logging.Cause;
import org.jboss.logging.Message;
import org.jboss.logging.MessageBundle;
import org.jboss.logging.Messages;
import org.picketbox.exceptions.AuthenticationException;

@MessageBundle(projectCode = "PBOX")
public interface PicketBoxMessages {

    PicketBoxMessages MESSAGES = Messages.getBundle(PicketBoxMessages.class);

    @Message(id = 1, value = "Unable to proceed: AuthenticationManager has not been injected")
    AuthenticationException invalidNullAuthenticationManager();

    @Message(id = 2, value = "Unable to proceed: ServletContext has not been injected")
    AuthenticationException invalidNullServletContext();

    @Message(id = 3, value = "Unable to identify certificate principal")
    AuthenticationException unableToIdentifyCertPrincipal();

    @Message(id = 4, value = "Unable to forward to cached request")
    AuthenticationException unableToForwardToCachedRequest();

    @Message(id = 5, value = "RequestDispatcher could not be found")
    AuthenticationException unableToFindRequestDispatcher();

    @Message(id = 6, value = "Required init parameter %s is missing")
    ServletException missingRequiredInitParameter(String paramName);

    @Message(id = 7, value = "The argument %s cannot be null")
    IllegalArgumentException invalidNullArgument(String argName);

    @Message(id = 8, value = "Error encoding from file %s")
    IllegalStateException errorEncodingFromFile(String fileName, @Cause Throwable throwable);

    @Message(id = 9, value = "Error decoding from file %s")
    IllegalStateException errorDecodingFromFile(String fileName, @Cause Throwable throwable);

    @Message(id = 10, value = "Error decoding from file %s: file is too big (%s bytes)")
    IllegalStateException errorDecodingFromBigInputFile(String fileName, long fileSize);

    @Message(id = 11, value = "Base64 input not properly padded")
    IOException invalidBase64Padding();

    @Message(id = 12, value = "Invalid Base64 character found: %s")
    String invalidBase64CharacterMessage(byte character);

    @Message(id = 13, value = "Error reading Base64 stream: nothing to read")
    IOException errorReadingBase64Stream();

    @Message(id = 14, value = "Unable to find properties file: %s")
    RuntimeException unableToFindPropertiesFile(String fileName);


}
