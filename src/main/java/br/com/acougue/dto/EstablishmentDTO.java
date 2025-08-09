package br.com.acougue.dto;

public class EstablishmentDTO {

	private Long id;
	private String name;
	private Long cnpj;
	private String address;
	private Integer totalClients;
	private Integer totalProducts;
	private Integer totalOrders;

	public EstablishmentDTO() {
	}

	public EstablishmentDTO(Long id, String name, Long cnpj, String address, Integer totalClients,
			Integer totalProducts, Integer totalOrders) {
		this.id = id;
		this.name = name;
		this.cnpj = cnpj;
		this.address = address;
		this.totalClients = totalClients;
		this.totalProducts = totalProducts;
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

	public Long getCnpj() {
		return cnpj;
	}

	public void setCnpj(Long cnpj) {
		this.cnpj = cnpj;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public Integer getTotalClients() {
		return totalClients;
	}

	public void setTotalClients(Integer totalClients) {
		this.totalClients = totalClients;
	}

	public Integer getTotalProducts() {
		return totalProducts;
	}

	public void setTotalProducts(Integer totalProducts) {
		this.totalProducts = totalProducts;
	}

	public Integer getTotalOrders() {
		return totalOrders;
	}

	public void setTotalOrders(Integer totalOrders) {
		this.totalOrders = totalOrders;
	}
}
