package br.com.acougue.entities;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;

class ClientTest {

    @Test
    void testClientCreationAndGettersSetters() {
        Client client = new Client();
        client.setId(1L);
        client.setName("John Doe");
        client.setNumberPhone(123456789L);
        client.setAddress("123 Main St");
        client.setAddressNeighborhood("Downtown");
        client.setObservation("VIP Client");
        client.setEstablishment(null); // Assuming establishment can be tested separately
        client.setOrders(new ArrayList<>());
        client.setProducts(new ArrayList<>());

        assertEquals(1L, client.getId());
        assertEquals("John Doe", client.getName());
        assertEquals(123456789L, client.getNumberPhone());
        assertEquals("123 Main St", client.getAddress());
        assertEquals("Downtown", client.getAddressNeighborhood());
        assertEquals("VIP Client", client.getObservation());
        assertNull(client.getEstablishment());
        assertTrue(client.getOrders().isEmpty());
        assertTrue(client.getProducts().isEmpty());
    }

    @Test
    void testEquals() {
        Client client1 = new Client();
        client1.setId(1L);
        client1.setName("Client A");

        Client client2 = new Client();
        client2.setId(1L);
        client2.setName("Client B"); // Different name, but same ID

        Client client3 = new Client();
        client3.setId(2L);

        assertEquals(client1, client2, "Dois clientes com o mesmo ID devem ser iguais.");
        assertNotEquals(client1, client3, "Clientes com IDs diferentes não devem ser iguais.");
        assertNotEquals(client1, null, "Um cliente não deve ser igual a nulo.");
        assertNotEquals(client1, new Object(), "Um cliente não deve ser igual a um objeto de outra classe.");
    }

    @Test
    void testHashCode() {
        Client client1 = new Client();
        client1.setId(1L);

        Client client2 = new Client();
        client2.setId(1L);

        assertEquals(client1.hashCode(), client2.hashCode(), "Hashcodes de dois clientes com o mesmo ID devem ser iguais.");
    }

    @Test
    void testToString() {
        Client client = new Client();
        client.setId(1L);
        client.setName("John Doe");
        client.setNumberPhone(123456789L);

        String expected = "Client{id=1, name='John Doe', numberPhone=123456789}";
        assertEquals(expected, client.toString());
    }
}
