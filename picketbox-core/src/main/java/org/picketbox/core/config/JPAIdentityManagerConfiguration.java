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

package org.picketbox.core.config;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.picketbox.core.identity.impl.EntityManagerContext;
import org.picketlink.idm.internal.JPAIdentityStore;
import org.picketlink.idm.internal.jpa.JPACallback;
import org.picketlink.idm.internal.jpa.JPATemplate;
import org.picketlink.idm.spi.IdentityStore;


/**
 * @author <a href="mailto:psilva@redhat.com">Pedro Silva</a>
 *
 */
public class JPAIdentityManagerConfiguration implements IdentityManagerConfiguration {

    private EntityManagerFactory entityManagerFactory;

    public void setEntityManagerFactory(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    public EntityManagerFactory getEntityManagerFactory() {
        return this.entityManagerFactory;
    }

    @Override
    public IdentityStore getIdentityStore() {
        JPAIdentityStore store = new JPAIdentityStore();

        JPATemplate jpaTemplate = new JPATemplate() {
            @Override
            public Object execute(JPACallback callback) {
                EntityManager entityManager = EntityManagerContext.get();
                return callback.execute(entityManager);
            }
        };

        store.setJpaTemplate(jpaTemplate);

        return store;
    }

}
