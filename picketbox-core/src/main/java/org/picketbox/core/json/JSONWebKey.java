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
package org.picketbox.core.json;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.picketbox.core.PicketBoxMessages;

/**
 * Represents a JSON Web Key
 *
 * @author anil saldhana
 * @since Jul 24, 2012
 */
public class JSONWebKey {
    protected JSONArray keys = null;

    public JSONWebKey() {
    }

    /**
     * Set the Keys
     *
     * @param arr
     */
    public void setKeys(JSONArray arr) {
        this.keys = arr;
    }

    /**
     * Get a public key given its kid
     *
     * @param id
     * @return
     * @throws JSONException
     */
    public JSONObject getKey(String id) throws JSONException {
        if (keys == null) {
            throw PicketBoxMessages.MESSAGES.jsonWebKeysMissing();
        }
        int length = keys.length();
        for (int i = 0; i < length; i++) {
            JSONObject json = (JSONObject) keys.get(i);
            if (id.equals(json.get(PicketBoxJSONConstants.KID))) {
                return json;
            }
        }
        return null;
    }

    /**
     * Get the keys
     *
     * @return
     */
    public JSONArray getKeys() {
        return keys;
    }

    /**
     * Parse a {@link JSONObject} into {@link JSONWebKey}
     *
     * @param jsonObj
     * @throws JSONException
     */
    public void parse(JSONObject jsonObj) throws JSONException {
        if (jsonObj == null) {
            throw PicketBoxMessages.MESSAGES.invalidNullArgument("obj");
        }
        keys = jsonObj.getJSONArray(PicketBoxJSONConstants.KEYS);
    }
}