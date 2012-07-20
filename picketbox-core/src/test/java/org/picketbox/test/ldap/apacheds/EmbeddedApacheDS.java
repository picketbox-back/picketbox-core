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
package org.picketbox.test.ldap.apacheds;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

import org.apache.directory.server.constants.ServerDNConstants;
import org.apache.directory.server.core.CoreSession;
import org.apache.directory.server.core.DefaultDirectoryService;
import org.apache.directory.server.core.DirectoryService;
import org.apache.directory.server.core.partition.Partition;
import org.apache.directory.server.core.partition.impl.btree.jdbm.JdbmPartition;
import org.apache.directory.server.core.partition.ldif.LdifPartition;
import org.apache.directory.server.core.schema.SchemaPartition;
import org.apache.directory.server.ldap.LdapServer;
import org.apache.directory.server.protocol.shared.transport.TcpTransport;
import org.apache.directory.shared.ldap.entry.DefaultServerEntry;
import org.apache.directory.shared.ldap.ldif.LdifEntry;
import org.apache.directory.shared.ldap.ldif.LdifReader;
import org.apache.directory.shared.ldap.schema.SchemaManager;
import org.apache.directory.shared.ldap.schema.loader.ldif.LdifSchemaLoader;
import org.apache.directory.shared.ldap.schema.manager.impl.DefaultSchemaManager;
import org.apache.directory.shared.ldap.schema.registries.SchemaLoader;

/**
 * Embedded Apache Directory Service
 *
 * Based on EmbeddedADSVer157 class from Embedded Samples of Apache DS for v1.5.7 (ASL)
 *
 * @author anil saldhana
 * @since Jul 20, 2012
 */
public class EmbeddedApacheDS {
    /** The directory service */
    private DirectoryService service;

    /** The LDAP server */
    private LdapServer server;

    public EmbeddedApacheDS(File workDir) throws Exception {
        // Initialize the LDAP service
        service = new DefaultDirectoryService();
        service.setWorkingDirectory(workDir);

        // first load the schema
        initSchemaPartition();

        // then the system partition
        // this is a MANDATORY partition
        Partition systemPartition = addPartition("system", ServerDNConstants.SYSTEM_DN);
        service.setSystemPartition(systemPartition);

        // Disable the ChangeLog system
        service.getChangeLog().setEnabled(false);
        service.setDenormalizeOpAttrsEnabled(true);    
    }

    @SuppressWarnings("unused")
    public void createBaseDN(String partitionName, String baseDN) throws Exception{
        Partition partition = addPartition(partitionName, baseDN);

        // And start the service
        service.startup();

        /*// Inject the apache root entry
        if (!service.getAdminSession().exists(partition.getSuffixDn())) {
            DN dnApache = new DN(baseDN);
            ServerEntry entryApache = service.newEntry(dnApache);
            entryApache.add("objectClass", "top", "domain", "extensibleObject");
            entryApache.add("dc", "Apache");
            service.getAdminSession().add(entryApache);
        }*/
    }

    /**
     * Add a new partition to the server
     *
     * @param partitionId The partition Id
     * @param partitionDn The partition DN
     * @return The newly added partition
     * @throws Exception If the partition can't be added
     */
    private Partition addPartition(String partitionId, String partitionDn) throws Exception { 
        JdbmPartition partition = new JdbmPartition();
        partition.setId(partitionId);
        partition.setPartitionDir(new File(service.getWorkingDirectory(), partitionId));
        partition.setSuffix(partitionDn);
        service.addPartition(partition);

        return partition;
    } 
    
    /**
     * initialize the schema manager and add the schema partition to diectory service
     *
     * @throws Exception if the schema LDIF files are not found on the classpath
     */
    private void initSchemaPartition() throws Exception {
        SchemaPartition schemaPartition = service.getSchemaService().getSchemaPartition();

        // Init the LdifPartition
        LdifPartition ldifPartition = new LdifPartition();
        String workingDirectory = service.getWorkingDirectory().getPath();
        ldifPartition.setWorkingDirectory(workingDirectory + "/schema");

        // Extract the schema on disk (a brand new one) and load the registries
        /*File schemaRepository = new File(workingDirectory, "schema");
        SchemaLdifExtractor extractor = new DefaultSchemaLdifExtractor(new File(workingDirectory));
        extractor.extractOrCopy(true);
        if(extractor.isExtracted() == false)
            throw new RuntimeException("Schema not extracted");*/

        schemaPartition.setWrappedPartition(ldifPartition);

        URL schemaDir = getClass().getClassLoader().getResource("apacheds/schema");
        File schemaRepository = new File(schemaDir.getPath());
        
        SchemaLoader loader = new LdifSchemaLoader(schemaRepository);
        SchemaManager schemaManager = new DefaultSchemaManager(loader);
        service.setSchemaManager(schemaManager);

        // We have to load the schema now, otherwise we won't be able
        // to initialize the Partitions, as we won't be able to parse
        // and normalize their suffix DN
        schemaManager.loadAllEnabled();

        schemaPartition.setSchemaManager(schemaManager);

        List<Throwable> errors = schemaManager.getErrors();

        if (errors.size() != 0) {
            throw new Exception("Schema load failed : " + errors);
        }
    }

    public void importLdif( InputStream in ) throws Exception
    { 
        /** the context root for the rootDSE */
        CoreSession rootDSE = service.getAdminSession();
        for ( LdifEntry ldifEntry:new LdifReader( in ) )
        {
            DefaultServerEntry entry = new DefaultServerEntry( 
                    rootDSE.getDirectoryService().getSchemaManager(), ldifEntry.getEntry() );

            if(!rootDSE.exists(entry.getDn())){ 
                rootDSE.add( entry);   
            } 
        }
    }


    /**
     * starts the LdapServer
     *
     * @throws Exception
     */
    public void startServer() throws Exception
    {
        server = new LdapServer();
        int serverPort = 10389;
        server.setTransports( new TcpTransport( serverPort ) );
        server.setDirectoryService( service );

        server.start();
    }

    public void stopServer() throws Exception
    {
        if(server != null){
            if(server.isStarted()){
                server.stop();
            }
        }
    }
}