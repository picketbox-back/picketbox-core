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
package org.picketbox.core.session;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map;

import org.picketbox.core.PicketBoxMessages;
import org.picketbox.core.util.StreamUtil;

/**
 * A {@link SessionStore} that serializes/deserializes sessions from a file
 *
 * @author anil saldhana
 * @since Aug 22, 2012
 */
public class FileSessionStore extends AbstractSessionStore {
    private String sessionFileName = "PBOXSESSION.DAT";

    public FileSessionStore() {
        loadFromFile();
    }

    public FileSessionStore(String sessionFileName) {
        if (sessionFileName == null) {
            throw PicketBoxMessages.MESSAGES.invalidNullArgument("sessionFileName");
        }
        this.sessionFileName = sessionFileName;
        loadFromFile();
    }

    @Override
    protected void doStop() {
        super.doStop();
        storeToFile();
    }

    @SuppressWarnings("unchecked")
    protected void loadFromFile() {
        ObjectInputStream ois = null;
        try {
            File file = new File(sessionFileName);
            if (file.exists() == false) {
                return; // If there is no session file, return as the store will create one
            }
            ois = new ObjectInputStream(new FileInputStream(file));
            this.sessions.clear();
            this.sessions.putAll((Map<? extends Serializable, ? extends PicketBoxSession>) ois.readObject());
        } catch (IOException e) {
            throw PicketBoxMessages.MESSAGES.unableToLoadFromFile(sessionFileName, e);
        } catch (ClassNotFoundException e) {
            throw PicketBoxMessages.MESSAGES.unableToLoadFromFile(sessionFileName, e);
        } finally {
            StreamUtil.safeClose(ois);
        }
    }

    protected void storeToFile() {
        ObjectOutputStream oos = null;
        try {
            oos = new ObjectOutputStream(new FileOutputStream(sessionFileName));
            oos.writeObject(this.sessions);
        } catch (IOException e) {
            throw PicketBoxMessages.MESSAGES.unableToStoreToFile(sessionFileName, e);
        } finally {
            StreamUtil.safeClose(oos);
        }
    }
}