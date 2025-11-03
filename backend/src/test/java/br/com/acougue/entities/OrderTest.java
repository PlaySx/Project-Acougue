package br.com.acougue.entities;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import br.com.acougue.enums.OrderStatus;

class OrderTest {

    @Test
    void testOrderCreationAndGettersSetters() {
        Order order = new Order();
        LocalDateTime now = LocalDateTime.now();

        order.setId(1L);
        order.setDatahora(now);
        order.setStatus(OrderStatus.PENDENTE);
        order.setPaymentMethod("Credit Card");
        order.setObservation("Handle with care");
        order.setClient(null); // Dependencies tested separately
        order.setEstablishment(null);

        assertEquals(1L, order.getId());
        assertEquals(now, order.getDatahora());
        assertEquals(OrderStatus.PENDENTE, order.getStatus());
        assertEquals("Credit Card", order.getPaymentMethod());
        assertEquals("Handle with care", order.getObservation());
        assertNull(order.getClient());
        assertNull(order.getEstablishment());
    }

    @Test
    void testEquals() {
        Order order1 = new Order();
        order1.setId(1L);

        Order order2 = new Order();
        order2.setId(1L);

        Order order3 = new Order();
        order3.setId(2L);

        assertEquals(order1, order2, "Duas ordens com o mesmo ID devem ser iguais.");
        assertNotEquals(order1, order3, "Ordens com IDs diferentes não devem ser iguais.");
        assertNotEquals(order1, null, "Uma ordem não deve ser igual a nulo.");
        assertNotEquals(order1, new Object(), "Uma ordem não deve ser igual a um objeto de outra classe.");
    }

    @Test
    void testHashCode() {
        Order order1 = new Order();
        order1.setId(1L);

        Order order2 = new Order();
        order2.setId(1L);

        assertEquals(order1.hashCode(), order2.hashCode(), "Hashcodes de duas ordens com o mesmo ID devem ser iguais.");
    }

    @Test
    void testToString() {
        Order order = new Order();
        LocalDateTime now = LocalDateTime.now();
        order.setId(1L);
        order.setDatahora(now);
        order.setStatus(OrderStatus.ENTREGUE);

        // Corrigido para usar o valor real do enum.toString()
        String expected = "Order{id=1, datahora=" + now + ", status=" + OrderStatus.ENTREGUE + "}";
        assertEquals(expected, order.toString());
    }
}
