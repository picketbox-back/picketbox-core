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
package org.picketbox.authentication;

/**
 * <p>
 * Holder for HTTP/Digest Authentication
 * </p>
 * <p>
 * Refer to RFC 2069 and 2617 for the meaning.
 * </p>
 *  
 * @author anil saldhana
 * @since July 6, 2012
 */
public class DigestHolder {
    private String username, realm, nonce, uri, qop, nc, cnonce, clientResponse, opaque, domain, stale, requestMethod;
    
    public DigestHolder setUsername(String username) {
        this.username = username;
        return this;
    }

    public DigestHolder setRealm(String realm) {
        this.realm = realm;
        return this;
    }

    public DigestHolder setNonce(String nonce) {
        this.nonce = nonce;
        return this;
    }

    public DigestHolder setUri(String uri) {
        this.uri = uri;
        return this;
    }

    public DigestHolder setQop(String qop) {
        this.qop = qop;
        return this;
    }

    public DigestHolder setNc(String nc) {
        this.nc = nc;
        return this;
    }

    public DigestHolder setCnonce(String cnonce) {
        this.cnonce = cnonce;
        return this;
    }

    public DigestHolder setClientResponse(String clientResponse) {
        this.clientResponse = clientResponse;
        return this;
    }

    public DigestHolder setOpaque(String opaque) {
        this.opaque = opaque;
        return this;
    }
    public String getUsername() {
        return username;
    }

    public String getRealm() {
        return realm;
    }
    public String getNonce() {
        return nonce;
    }
    public String getUri() {
        return uri;
    }
    public String getQop() {
        return qop;
    }
    public String getNc() {
        return nc;
    }
    public String getCnonce() {
        return cnonce;
    }
    public String getClientResponse() {
        return clientResponse;
    }
    public String getOpaque() {
        return opaque;
    }

    public String getDomain() {
        return domain;
    }

    public DigestHolder setDomain(String domain) {
        this.domain = domain;
        return this;
    }

    public String getStale() {
        return stale;
    }

    public DigestHolder setStale(String stale) {
        this.stale = stale;
        return this;
    }

    public String getRequestMethod() {
        return requestMethod;
    }

    public DigestHolder setRequestMethod(String requestMethod) {
        this.requestMethod = requestMethod;
        return this;
    } 
}