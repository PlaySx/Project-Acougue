package br.com.acougue.dto;

public class ProductRequestDTO {

	private String name;
	private String description;
	private Double value;
	private Long establishmentId;

	public ProductRequestDTO() {
	}

	public ProductRequestDTO(String name, String description, Double value, Long establishmentId) {
		this.name = name;
		this.description = description;
		this.value = value;
		this.establishmentId = establishmentId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Double getValue() {
		return value;
	}

	public void setValue(Double value) {
		this.value = value;
	}

	public Long getEstablishmentId() {
		return establishmentId;
	}

	public void setEstablishmentId(Long establishmentId) {
		this.establishmentId = establishmentId;
	}
}
