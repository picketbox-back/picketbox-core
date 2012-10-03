package org.picketbox.test.session;

import org.junit.Before;
import org.junit.Test;
import org.picketbox.core.PicketBoxSubject;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class PicketBoxSubjectTest extends PicketBoxSubject {

    private PicketBoxSubjectTest picketBoxSubject;

    @Before
    public void setUp() {
        picketBoxSubject = new PicketBoxSubjectTest();
        picketBoxSubject.setRoleNames(buildRoles());
        picketBoxSubject.setAuthenticated(true);
    }

    private List<String> buildRoles() {
        return Arrays.asList("manager", "developer");
    }

    @Test
    public void testHasRole() throws Exception {
        assertTrue(picketBoxSubject.hasRole("manager"));
    }

    @Test
    public void testNonExistentRole() throws Exception {
        assertFalse(picketBoxSubject.hasRole("guest"));
    }
}
