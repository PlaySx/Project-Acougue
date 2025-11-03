package br.com.acougue.entities;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ProductTest {

    @Test
    void testProductCreationAndGettersSetters() {
        Product product = new Product();
        product.setId(1L);
        product.setName("Picanha");
        product.setDescription("Corte nobre");
        product.setValue(59.99);
        product.setEstablishment(null); // Dependencies tested separately

        assertEquals(1L, product.getId());
        assertEquals("Picanha", product.getName());
        assertEquals("Corte nobre", product.getDescription());
        assertEquals(59.99, product.getValue());
        assertNull(product.getEstablishment());
    }

    @Test
    void testEquals() {
        Product product1 = new Product();
        product1.setId(1L);

        Product product2 = new Product();
        product2.setId(1L);

        Product product3 = new Product();
        product3.setId(2L);

        assertEquals(product1, product2, "Dois produtos com o mesmo ID devem ser iguais.");
        assertNotEquals(product1, product3, "Produtos com IDs diferentes não devem ser iguais.");
        assertNotEquals(product1, null, "Um produto não deve ser igual a nulo.");
        assertNotEquals(product1, new Object(), "Um produto não deve ser igual a um objeto de outra classe.");
    }

    @Test
    void testHashCode() {
        Product product1 = new Product();
        product1.setId(1L);

        Product product2 = new Product();
        product2.setId(1L);

        assertEquals(product1.hashCode(), product2.hashCode(), "Hashcodes de dois produtos com o mesmo ID devem ser iguais.");
    }

    @Test
    void testToString() {
        Product product = new Product();
        product.setId(1L);
        product.setName("Picanha");
        product.setValue(59.99);

        String expected = "Product{id=1, name='Picanha', value=59.99}";
        assertEquals(expected, product.toString());
    }
}
