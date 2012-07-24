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

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.RSAPublicKeySpec;

import org.json.JSONException;
import org.json.JSONObject;
import org.picketbox.core.PicketBoxMessages;
import org.picketbox.core.exceptions.ProcessingException;
import org.picketbox.core.util.Base64;

/**
 * RSA based public key JSON representation
 *
 * @author anil saldhana
 * @since Jul 24, 2012
 */
public class RSAKey implements JSONKey {
    protected String kid;
    protected String mod;
    protected String exp;
    private KeyUse keyUse;

    /**
     * Get the Algorithm
     */
    @Override
    public String getAlg() {
        return PicketBoxJSONConstants.RSA;
    }

    /**
     * Get the K-ID
     */
    @Override
    public String getKid() {
        return kid;
    }

    /**
     * Set the K-ID
     *
     * @param kid
     */
    public void setKid(String kid) {
        this.kid = kid;
    }

    public String getMod() {
        return mod;
    }

    public void setMod(String mod) {
        this.mod = mod;
    }

    public String getExp() {
        return exp;
    }

    public void setExp(String exp) {
        this.exp = exp;
    }

    /**
     * Parse a {@link JSONObject} into a {@link RSAKey}
     *
     * @param json
     * @throws JSONException
     */
    public void parse(JSONObject json) throws JSONException {
        String alg = json.getString(PicketBoxJSONConstants.ALG);
        if (PicketBoxJSONConstants.RSA.equals(alg) == false) {
            throw PicketBoxMessages.MESSAGES.wrongJsonKey();
        }
        kid = json.getString(PicketBoxJSONConstants.KID);
        mod = json.getString(PicketBoxJSONConstants.MOD);
        exp = json.getString(PicketBoxJSONConstants.EXP);
    }

    /**
     * Convert into a {@link JSONObject}
     *
     * @return
     * @throws JSONException
     */
    public JSONObject convert() throws JSONException {
        JSONObject json = new JSONObject();
        json.put(PicketBoxJSONConstants.ALG, getAlg());
        json.put(PicketBoxJSONConstants.EXP, exp);
        json.put(PicketBoxJSONConstants.MOD, mod);
        json.put(PicketBoxJSONConstants.KID, kid);
        return json;
    }

    @Override
    public KeyUse getUse() {
        return keyUse;
    }

    /**
     * Set the Key Use
     *
     * @param ku
     */
    public void setUse(KeyUse ku) {
        this.keyUse = ku;
    }

    public static RSAKey convert(RSAPublicKey publicKey) throws ProcessingException {
        BigInteger modulus = publicKey.getModulus();
        BigInteger exponent = publicKey.getPublicExponent();

        RSAKey rsaKey = new RSAKey();
        rsaKey.setMod(Base64.encodeBytes(modulus.toByteArray()));
        rsaKey.setExp(Base64.encodeBytes(exponent.toByteArray()));
        return rsaKey;
    }

    /**
     * Convert to the JDK representation of a RSA Public Key
     *
     * @return
     * @throws ProcessingException
     */
    public RSAPublicKey convertToPublicKey() throws ProcessingException {
        BigInteger bigModulus = new BigInteger(1, massage(Base64.decode(mod)));
        BigInteger bigEx = new BigInteger(1, massage(Base64.decode(exp)));

        try {
            KeyFactory rsaKeyFactory = KeyFactory.getInstance("rsa");
            RSAPublicKeySpec kspec = new RSAPublicKeySpec(bigModulus, bigEx);
            return (RSAPublicKey) rsaKeyFactory.generatePublic(kspec);
        } catch (Exception e) {
            throw PicketBoxMessages.MESSAGES.processingException(e);
        }
    }

    private byte[] massage(byte[] byteArray) {
        if (byteArray[0] == 0) {
            byte[] substring = new byte[byteArray.length - 1];
            System.arraycopy(byteArray, 1, substring, 0, byteArray.length - 1);
            return substring;
        }
        return byteArray;
    }
}