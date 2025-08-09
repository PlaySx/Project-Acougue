package br.com.acougue.mapper;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import br.com.acougue.dto.OrderRequestDTO;
import br.com.acougue.dto.OrderResponseDTO;
import br.com.acougue.entities.Client;
import br.com.acougue.entities.Establishment;
import br.com.acougue.entities.Order;
import br.com.acougue.entities.Products;
import br.com.acougue.repository.ClientRepository;
import br.com.acougue.repository.EstablishmentRepository;
import br.com.acougue.repository.ProductsRepository;

@Component
public class OrderMapper {

	@Autowired
	private ClientRepository clientRepository;

	@Autowired
	private EstablishmentRepository establishmentRepository;

	@Autowired
	private ProductsRepository productsRepository;

	@Autowired
	private ProductMapper productMapper;

	// Converte DTO de request para entidade
	public Order toEntity(OrderRequestDTO dto) {
		if (dto == null)
			return null;

		Order order = new Order();
		order.setDatahora(dto.getDatahora());
		order.setPaymentMethod(dto.getPaymentMethod());
		order.setObservação(dto.getObservacao());

		// Busca o cliente pelo ID
		if (dto.getClientId() != null) {
			Client client = clientRepository.findById(dto.getClientId())
					.orElseThrow(() -> new RuntimeException("Client not found with id: " + dto.getClientId()));
			order.setClient(client);
		}

		// Busca o estabelecimento pelo ID
		if (dto.getEstablishmentId() != null) {
			Establishment establishment = establishmentRepository.findById(dto.getEstablishmentId()).orElseThrow(
					() -> new RuntimeException("Establishment not found with id: " + dto.getEstablishmentId()));
			order.setEstablishment(establishment);
		}

		// Busca os produtos pelos IDs
		if (dto.getProductIds() != null && !dto.getProductIds().isEmpty()) {
			List<Products> products = new ArrayList<>();
			for (Long productId : dto.getProductIds()) {
				Products product = productsRepository.findById(productId)
						.orElseThrow(() -> new RuntimeException("Product not found with id: " + productId));
				products.add(product);
			}
			order.setProducts(products);
		}

		return order;
	}

	// Converte entidade para DTO de response
	public OrderResponseDTO toResponseDTO(Order entity) {
		if (entity == null)
			return null;

		// Calcula o valor total do pedido
		Double totalValue = 0.0;
		if (entity.getProducts() != null) {
			totalValue = entity.getProducts().stream()
					.mapToDouble(product -> product.getValue() != null ? product.getValue() : 0.0).sum();
		}

		return new OrderResponseDTO(entity.getId(), entity.getDatahora(), entity.getStatus(), entity.getPaymentMethod(),
				entity.getObservação(), entity.getClient() != null ? entity.getClient().getName() : null,
				entity.getEstablishment() != null ? entity.getEstablishment().getName() : null,
				entity.getProducts() != null ? productMapper.toResponseDTOList(entity.getProducts()) : null,
				totalValue);
	}

	// Atualiza entidade existente com dados do DTO
	public void updateEntityFromDTO(Order entity, OrderRequestDTO dto) {
		if (entity == null || dto == null)
			return;

		entity.setDatahora(dto.getDatahora());
		entity.setPaymentMethod(dto.getPaymentMethod());
		entity.setObservação(dto.getObservacao());

		if (dto.getClientId() != null) {
			Client client = clientRepository.findById(dto.getClientId())
					.orElseThrow(() -> new RuntimeException("Client not found with id: " + dto.getClientId()));
			entity.setClient(client);
		}

		if (dto.getEstablishmentId() != null) {
			Establishment establishment = establishmentRepository.findById(dto.getEstablishmentId()).orElseThrow(
					() -> new RuntimeException("Establishment not found with id: " + dto.getEstablishmentId()));
			entity.setEstablishment(establishment);
		}

		if (dto.getProductIds() != null && !dto.getProductIds().isEmpty()) {
			List<Products> products = new ArrayList<>();
			for (Long productId : dto.getProductIds()) {
				Products product = productsRepository.findById(productId)
						.orElseThrow(() -> new RuntimeException("Product not found with id: " + productId));
				products.add(product);
			}
			entity.setProducts(products);
		}
	}

	// Converte lista de entidades para lista de DTOs de response
	public List<OrderResponseDTO> toResponseDTOList(List<Order> entities) {
		if (entities == null)
			return null;

		return entities.stream().map(this::toResponseDTO).collect(Collectors.toList());
	}
}
