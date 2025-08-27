package br.com.acougue.entities;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class EstablishmentTest {

    @Test
    void testEstablishmentCreation() {
        Establishment establishment = new Establishment("testuser", "password", "Test Shop", 12345678901234L, "123 Test St");
        establishment.setId(1L);

        assertEquals(1L, establishment.getId());
        assertEquals("testuser", establishment.getUsername());
        assertEquals("password", establishment.getPassword());
        assertEquals("Test Shop", establishment.getName());
        assertEquals(12345678901234L, establishment.getCnpj());
        assertEquals("123 Test St", establishment.getAddress());
        assertEquals("ROLE_ESTABLISHMENT", establishment.getRole());
        assertTrue(establishment.getUsers().isEmpty());
        assertTrue(establishment.getClients().isEmpty());
        assertTrue(establishment.getProducts().isEmpty());
        assertTrue(establishment.getOrders().isEmpty());
    }

    @Test
    void testEquals() {
        // A implementação atual de equals requer que id, name, cnpj e username sejam iguais.
        Establishment est1 = new Establishment("user1", "pass", "Shop A", 111L, "Addr A");
        est1.setId(10L);

        Establishment est2 = new Establishment("user1", "pass", "Shop A", 111L, "Addr A");
        est2.setId(10L);

        Establishment est3_different_id = new Establishment("user1", "pass", "Shop A", 111L, "Addr A");
        est3_different_id.setId(99L); // ID diferente

        Establishment est4_different_name = new Establishment("user1", "pass", "Shop B", 111L, "Addr A");
        est4_different_name.setId(10L); // Nome diferente

        assertEquals(est1, est2, "Dois estabelecimentos com os mesmos 4 campos (id, name, cnpj, username) devem ser iguais.");
        assertNotEquals(est1, est3_different_id, "Estabelecimentos com IDs diferentes não devem ser iguais.");
        assertNotEquals(est1, est4_different_name, "Estabelecimentos com nomes diferentes não devem ser iguais.");
        assertNotEquals(est1, null, "Um estabelecimento não deve ser igual a nulo.");
        assertNotEquals(est1, new Object(), "Um estabelecimento não deve ser igual a um objeto de outra classe.");
    }

    @Test
    void testHashCode() {
        // A implementação atual de hashCode usa id, name, cnpj e username.
        Establishment est1 = new Establishment("user1", "pass", "Shop A", 111L, "Addr A");
        est1.setId(10L);

        Establishment est2 = new Establishment("user1", "pass", "Shop A", 111L, "Addr A");
        est2.setId(10L);

        assertEquals(est1.hashCode(), est2.hashCode(), "Hashcodes devem ser iguais para objetos iguais.");
    }

    @Test
    void testToString() {
        Establishment establishment = new Establishment("testuser", "password", "Test Shop", 12345678901234L, "123 Test St");
        establishment.setId(1L);
        String expected = "Establishment{id=1, name='Test Shop', cnpj=12345678901234, address='123 Test St', username='testuser', role='ROLE_ESTABLISHMENT'}";
        assertEquals(expected, establishment.toString());
    }
}
