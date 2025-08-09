package br.com.acougue.dto;

import java.time.LocalDateTime;
import java.util.List;

public class OrderRequestDTO {

	private LocalDateTime datahora;
	private String paymentMethod;
	private String observacao;
	private Long clientId;
	private Long establishmentId;
	private List<Long> productIds;

	public OrderRequestDTO() {
	}

	public OrderRequestDTO(LocalDateTime datahora, String paymentMethod, String observacao, Long clientId,
			Long establishmentId, List<Long> productIds) {
		this.datahora = datahora;
		this.paymentMethod = paymentMethod;
		this.observacao = observacao;
		this.clientId = clientId;
		this.establishmentId = establishmentId;
		this.productIds = productIds;
	}

	public LocalDateTime getDatahora() {
		return datahora;
	}

	public void setDatahora(LocalDateTime datahora) {
		this.datahora = datahora;
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

	public Long getClientId() {
		return clientId;
	}

	public void setClientId(Long clientId) {
		this.clientId = clientId;
	}

	public Long getEstablishmentId() {
		return establishmentId;
	}

	public void setEstablishmentId(Long establishmentId) {
		this.establishmentId = establishmentId;
	}

	public List<Long> getProductIds() {
		return productIds;
	}

	public void setProductIds(List<Long> productIds) {
		this.productIds = productIds;
	}
}
