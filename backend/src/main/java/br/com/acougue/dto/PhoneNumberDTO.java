package br.com.acougue.dto;

import br.com.acougue.enums.PhoneType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class PhoneNumberDTO {

    @NotNull(message = "O tipo de telefone é obrigatório")
    private PhoneType type;

    @NotBlank(message = "O número é obrigatório")
    private String number;

    private boolean isPrimary;

    // Construtores
    public PhoneNumberDTO() {}
    public PhoneNumberDTO(PhoneType type, String number, boolean isPrimary) {
        this.type = type;
        this.number = number;
        this.isPrimary = isPrimary;
    }

    // Getters e Setters
    public PhoneType getType() { return type; }
    public void setType(PhoneType type) { this.type = type; }
    public String getNumber() { return number; }
    public void setNumber(String number) { this.number = number; }
    public boolean isPrimary() { return isPrimary; }
    public void setPrimary(boolean primary) { isPrimary = primary; }
}
