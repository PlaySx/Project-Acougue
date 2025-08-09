package br.com.acougue.dto;

public class ClientResponseDTO {

	private Long id;
	private String name;
	private Long numberPhone;
	private String address;
	private String addressNeighborhood;
	private String observation;
	private Long establishmentId;
	private Integer totalOrders;

	public ClientResponseDTO() {
	}

	public ClientResponseDTO(Long id, String name, Long numberPhone, String address, String addressNeighborhood,
			String observation, Long establishmentId, Integer totalOrders) {
		this.id = id;
		this.name = name;
		this.numberPhone = numberPhone;
		this.address = address;
		this.addressNeighborhood = addressNeighborhood;
		this.observation = observation;
		this.establishmentId = establishmentId;
		this.totalOrders = totalOrders;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Long getNumberPhone() {
		return numberPhone;
	}

	public void setNumberPhone(Long numberPhone) {
		this.numberPhone = numberPhone;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getAddressNeighborhood() {
		return addressNeighborhood;
	}

	public void setAddressNeighborhood(String addressNeighborhood) {
		this.addressNeighborhood = addressNeighborhood;
	}

	public String getObservation() {
		return observation;
	}

	public void setObservation(String observation) {
		this.observation = observation;
	}

	public Long getEstablishmentId() {
		return establishmentId;
	}

	public void setEstablishmentId(Long establishmentId) {
		this.establishmentId = establishmentId;
	}

	public Integer getTotalOrders() {
		return totalOrders;
	}

	public void setTotalOrders(Integer totalOrders) {
		this.totalOrders = totalOrders;
	}
}
