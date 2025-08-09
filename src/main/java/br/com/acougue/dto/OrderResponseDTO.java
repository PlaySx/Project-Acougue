package br.com.acougue.dto;

import java.time.LocalDateTime;
import java.util.List;

import br.com.acougue.enums.OrderStatus;

public class OrderResponseDTO {

	private Long id;
	private LocalDateTime datahora;
	private OrderStatus status;
	private String paymentMethod;
	private String observacao;
	private String clientName;
	private String establishmentName;
	private List<ProductResponseDTO> products;
	private Double totalValue;

	public OrderResponseDTO() {
	}

	public OrderResponseDTO(Long id, LocalDateTime datahora, OrderStatus status, String paymentMethod,
			String observacao, String clientName, String establishmentName, List<ProductResponseDTO> products,
			Double totalValue) {
		this.id = id;
		this.datahora = datahora;
		this.status = status;
		this.paymentMethod = paymentMethod;
		this.observacao = observacao;
		this.clientName = clientName;
		this.establishmentName = establishmentName;
		this.products = products;
		this.totalValue = totalValue;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public LocalDateTime getDatahora() {
		return datahora;
	}

	public void setDatahora(LocalDateTime datahora) {
		this.datahora = datahora;
	}

	public OrderStatus getStatus() {
		return status;
	}

	public void setStatus(OrderStatus status) {
		this.status = status;
	}

	public String getPaymentMethod() {
		return paymentMethod;
	}

	public void setPaymentMethod(String paymentMethod) {
		this.paymentMethod = paymentMethod;
	}

	public String getObservacao() {
		return observacao;
	}

	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}

	public String getClientName() {
		return clientName;
	}

	public void setClientName(String clientName) {
		this.clientName = clientName;
	}

	public String getEstablishmentName() {
		return establishmentName;
	}

	public void setEstablishmentName(String establishmentName) {
		this.establishmentName = establishmentName;
	}

	public List<ProductResponseDTO> getProducts() {
		return products;
	}

	public void setProducts(List<ProductResponseDTO> products) {
		this.products = products;
	}

	public Double getTotalValue() {
		return totalValue;
	}

	public void setTotalValue(Double totalValue) {
		this.totalValue = totalValue;
	}

}
