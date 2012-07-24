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
package org.picketbox.core.util;

import java.nio.charset.Charset;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.picketbox.core.PicketBoxMessages;
import org.picketbox.core.exceptions.ProcessingException;

/**
 * HMAC SHA 256 Algorithm
 *
 * @author anil saldhana
 * @since Jul 24, 2012
 */
public class HmacSha256Util {

    /**
     * Encode a payload using HMAC SHA 256 algorithm
     *
     * @param payload
     * @return
     * @throws ProcessingException
     */
    public static String encode(String payload) throws ProcessingException {
        final Charset charSet = Charset.forName("UTF-8");
        Mac sha256_HMAC = null;
        try {
            sha256_HMAC = Mac.getInstance("HmacSHA256");
        } catch (NoSuchAlgorithmException e1) {
            throw PicketBoxMessages.MESSAGES.noSuchAlgorithm(e1);
        }

        final SecretKeySpec secret_key = new javax.crypto.spec.SecretKeySpec(charSet.encode("key").array(), "HmacSHA256");
        try {
            sha256_HMAC.init(secret_key);
        } catch (InvalidKeyException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        final byte[] mac_data = sha256_HMAC.doFinal(charSet.encode(payload).array());
        String result = "";
        for (final byte element : mac_data) {
            result += Integer.toString((element & 0xff) + 0x100, 16).substring(1);
        }
        return result;
    }
}