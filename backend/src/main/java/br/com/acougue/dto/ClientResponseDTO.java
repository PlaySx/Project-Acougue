package br.com.acougue.dto;

import java.util.List;

public class ClientResponseDTO {

	private Long id;
	private String name;
    private List<PhoneNumberDTO> phoneNumbers;
	private String address;
	private String addressNeighborhood;
	private String observation;
	private Long establishmentId;
	private int orderCount;

	public ClientResponseDTO(Long id, String name, List<PhoneNumberDTO> phoneNumbers, String address, String addressNeighborhood, String observation, Long establishmentId, int orderCount) {
		this.id = id;
		this.name = name;
        this.phoneNumbers = phoneNumbers;
		this.address = address;
		this.addressNeighborhood = addressNeighborhood;
		this.observation = observation;
		this.establishmentId = establishmentId;
		this.orderCount = orderCount;
	}

	// Getters
	public Long getId() { return id; }
	public String getName() { return name; }
    public List<PhoneNumberDTO> getPhoneNumbers() { return phoneNumbers; }
	public String getAddress() { return address; }
	public String getAddressNeighborhood() { return addressNeighborhood; }
	public String getObservation() { return observation; }
	public Long getEstablishmentId() { return establishmentId; }
	public int getOrderCount() { return orderCount; }
}
