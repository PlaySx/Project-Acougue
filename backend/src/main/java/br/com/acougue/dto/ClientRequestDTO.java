package br.com.acougue.dto;

public class ClientRequestDTO {

	private String name;
	private Long numberPhone;
	private String address;
	private String addressNeighborhood;
	private String observation;
	private Long establishmentId;

	public ClientRequestDTO() {
	}

	public ClientRequestDTO(String name, Long numberPhone, String address, String addressNeighborhood,
			String observation, Long establishmentId) {
		this.name = name;
		this.numberPhone = numberPhone;
		this.address = address;
		this.addressNeighborhood = addressNeighborhood;
		this.observation = observation;
		this.establishmentId = establishmentId;
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
}
