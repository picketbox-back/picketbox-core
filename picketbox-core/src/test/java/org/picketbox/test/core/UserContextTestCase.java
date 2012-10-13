package org.picketbox.test.core;

import org.junit.Before;
import org.junit.Test;
import org.picketbox.core.UserContext;
import org.picketlink.idm.model.Role;
import org.picketlink.idm.model.SimpleRole;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * <p>
 * Tests the {@link org.picketbox.core.UserContext}.
 * </p>
 *
 * @author <a href="mailto:abstractj@redhat.com">Bruno Oliveira</a>
 *
 */
public class UserContextTestCase {

    private UserContext userContext = new UserContext();

    private List<String> roleNames = Arrays.asList("manager", "developer");

    @Before
    public void setUp() throws Exception {
        userContext.setRoles(buildRoles(roleNames));
    }

    private Collection<Role> buildRoles(List<String> roleNames) {

        Collection<Role> roles = new ArrayList<Role>();
        for (String roleName : roleNames) {
            roles.add(new SimpleRole(roleName));
        }

        return roles;
    }

    @Test
    public void testGetRoleNames() throws Exception {
        assertTrue(userContext.getRoleNames().containsAll(roleNames));
    }

    @Test
    public void testNonExistentRole() throws Exception {
        assertFalse(userContext.getRoleNames().contains("guest"));
    }
}
