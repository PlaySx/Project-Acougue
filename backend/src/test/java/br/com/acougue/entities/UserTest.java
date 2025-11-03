package br.com.acougue.entities;

import br.com.acougue.enums.Role;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    void testUserCreation() {
        User user = new User(1L, "testuser", "password", Role.ROLE_EMPLOYEE, null);
        assertEquals(1L, user.getId());
        assertEquals("testuser", user.getUsername());
        assertEquals("password", user.getPassword());
        assertEquals(Role.ROLE_EMPLOYEE, user.getRole());
        assertNull(user.getEstablishment());
    }

    @Test
    void testEquals() {
        User user1 = new User(1L, "user1", "pass1", Role.ROLE_OWNER, null);
        User user2 = new User(1L, "user2", "pass2", Role.ROLE_EMPLOYEE, null);
        User user3 = new User(2L, "user3", "pass3", Role.ROLE_EMPLOYEE, null);

        assertEquals(user1, user2);
        assertNotEquals(user1, user3);
    }

    @Test
    void testHashCode() {
        User user1 = new User(1L, "user1", "pass1", Role.ROLE_OWNER, null);
        User user2 = new User(1L, "user2", "pass2", Role.ROLE_EMPLOYEE, null);

        assertEquals(user1.hashCode(), user2.hashCode());
    }

    @Test
    void testToString() {
        User user = new User(1L, "testuser", "password", Role.ROLE_EMPLOYEE, null);
        String expected = "User{id=1, username='testuser', role=ROLE_EMPLOYEE}";
        assertEquals(expected, user.toString());
    }
}
