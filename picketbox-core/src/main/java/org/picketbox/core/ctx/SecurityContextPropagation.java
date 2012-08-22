/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2012, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.picketbox.core.ctx;

import org.picketbox.core.PicketBoxMessages;
import org.picketbox.core.exceptions.PicketBoxSessionException;
import org.picketbox.core.exceptions.ProcessingException;
import org.picketbox.core.session.PicketBoxSession;
import org.picketbox.core.session.PicketBoxSessionManager;

/**
 * Main class in propagation of {@link SecurityContext}
 *
 * @author anil saldhana
 * @since Aug 22, 2012
 */
public class SecurityContextPropagation {
    public enum LEVEL {
        THREAD, SESSION
    };

    private static ThreadLocal<SecurityContext> storage = new ThreadLocal<SecurityContext>();

    private static PicketBoxSession session = null;

    /**
     * Set the {@link SecurityContext}
     *
     * @param securityContext
     * @param level
     * @throws ProcessingException
     */
    public static void setContext(SecurityContext securityContext, LEVEL level) throws ProcessingException {
        if (level == LEVEL.THREAD) {
            storage.set(securityContext);
        } else {
            if (session == null) {
                session = PicketBoxSessionManager.create();
            }
            try {
                session.setAttribute("CONTEXT", securityContext);
            } catch (PicketBoxSessionException e) {
                throw PicketBoxMessages.MESSAGES.processingException(e);
            }
        }
    }

    /**
     * Set the {@link SecurityContext}
     *
     * @param securityContext
     * @param level
     * @param pboxSession Session that the integrating application owns
     * @throws ProcessingException
     */
    public static void setContext(SecurityContext securityContext, LEVEL level, PicketBoxSession pboxSession)
            throws ProcessingException {
        if (level == LEVEL.THREAD) {
            throw PicketBoxMessages.MESSAGES.invalidLevel(level.name());
        } else {
            try {
                pboxSession.setAttribute("CONTEXT", securityContext);
            } catch (PicketBoxSessionException e) {
                throw PicketBoxMessages.MESSAGES.processingException(e);
            }
        }
    }

    /**
     * Set the {@link SecurityContext}
     *
     * @param securityContext
     * @param level
     * @throws ProcessingException
     */
    public static SecurityContext getContext(LEVEL level) throws ProcessingException {
        if (level == LEVEL.THREAD) {
            return storage.get();
        } else {
            if (session == null) {
                return null;
            }
            try {
                return (SecurityContext) session.getAttribute("CONTEXT");
            } catch (PicketBoxSessionException e) {
                throw PicketBoxMessages.MESSAGES.processingException(e);
            }
        }
    }

    /**
     * Set the {@link SecurityContext}
     *
     * @param securityContext
     * @param level
     * @param pboxsession Integrating application owned session
     * @throws ProcessingException
     */
    public static SecurityContext getContext(LEVEL level, PicketBoxSession pboxsession) throws ProcessingException {
        if (level == LEVEL.THREAD) {
            throw PicketBoxMessages.MESSAGES.invalidLevel(level.name());
        } else {
            if (pboxsession == null) {
                return null;
            }
            try {
                return (SecurityContext) pboxsession.getAttribute("CONTEXT");
            } catch (PicketBoxSessionException e) {
                throw PicketBoxMessages.MESSAGES.processingException(e);
            }
        }
    }

    /**
     * Clear the existing security context
     *
     * @param level
     * @throws ProcessingException
     */
    public static void clear(LEVEL level) throws ProcessingException {
        if (level == LEVEL.THREAD) {
            storage.remove();
        } else {
            if (session != null) {
                try {
                    session.removeAttribute("CONTEXT");
                } catch (PicketBoxSessionException e) {
                    throw PicketBoxMessages.MESSAGES.processingException(e);
                }
            }
        }
    }

    /**
     * Clear the existing security context
     *
     * @param level
     * @throws ProcessingException
     */
    public static void clear(LEVEL level, PicketBoxSession theSession) throws ProcessingException {
        if (level == LEVEL.THREAD) {
            throw PicketBoxMessages.MESSAGES.invalidLevel(level.name());
        } else {
            if (theSession != null) {
                try {
                    theSession.removeAttribute("CONTEXT");
                } catch (PicketBoxSessionException e) {
                    throw PicketBoxMessages.MESSAGES.processingException(e);
                }
            }
        }
    }
}