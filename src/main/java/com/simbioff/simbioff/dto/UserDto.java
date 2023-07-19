package com.simbioff.simbioff.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.br.CPF;
import javax.validation.constraints.*;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {

    @NotBlank(message = "Nome completo é obrigatório")
    @Size(min=5, max=30, message = "Nome completo deve ter entre 5 a 30 caracteres")
    private String fullName;

    @NotBlank(message = "E-mail é obrigatório")
    @Size(min=5, max=50, message = "E-mail deve ter entre 5 a 30 caracteres")
    @Email(message = "E-mail inválido", regexp = "^[A-Za-z0-9+_.-]+@(.+)$")
    private String email;

    private String password;

    @CPF(message = "CPF inválido")
    private String cpf ;

    @NotNull(message = "Data de Nascimento é obrigatório")
    private LocalDate birthDate;

    @Size(min=5, max=20, message = "CEP deve ter entre 5 a 30 caracteres")
    private String zip;

    @NotBlank (message = "Rua é obrigatório")
    @Size(min=2, max=250, message = "Rua deve ter entre 2 a 250 caracteres")
    private String street;

    @NotBlank(message = "Bairro é obrigatório")
    @Size(min = 2, max = 250, message = "Bairro deve ter entre 2 a 250 caracteres")
    private String district;

    @NotBlank(message = "Cidade é obrigatório")
    @Size(min=2, max=20, message = "Cidade deve ter entre 2 a 20 caracteres")
    private String city;

    @NotBlank(message = "Estado é obrigatório")
    @Size(min = 2, max = 20, message = "Estado deve ter entre 2 a 20 caracteres")
    private String state;

    @NotBlank(message = "País é obrigatório")
    @Size(min = 2, max = 20, message = "País deve ter entre 5 a 20 caracteres")
    private String country;

    @Min(value = 1, message = "Número deve ser maior que 0")
    private String number;

    @NotNull(message = "Possui filhos é obrigatório")
    private Boolean hasChildren;

    private int childrenQty;

    @NotBlank(message = "Aeroporto mais próximo é obrigatório")
    @Size(min = 3, max = 240, message = "Aeroporto mais próximo deve ter entre 3 a 240 caracteres")
    private String nearbyAirport;

    @NotBlank(message = "Chave Pix é obrigatório")
    private String pixKey;

    @NotBlank(message = "Número de celular é obrigatório")
    @Size(min = 5, max = 20, message = "Número de celular deve ter entre 5 a 20 caracteres")
    private String phone;

    @NotBlank(message = "Estado civil é obrigatório")
    @Size(min = 5, max = 20, message = "Estado civil deve ter entre 5 a 20 caracteres")
    private String maritalState;

    @NotNull(message = "Data de entrada na Simbiose é obrigatório")
    private LocalDate startOnTeam;

    private String childrenNames;

    // Other Information

    private String functionAtWork;

    private Boolean ped;

    private String responsiblePed;

    private Boolean english;

    private String englishTeacher;

    private Boolean therapy;

    private String responsibleTherapist;

    private Boolean undergraduate;

    private String undergraduateCourseName;

    private Boolean graduate;

    private String graduateCourseName;

    // Personal Information

    private String shirtSize;

    private String shoeSize;

    private String favouriteColor;

    private String favouriteFood;

    private String beachOrCamp;

    private String pets;

    private String hobbies;

    private Double daysOffWithdrawn;

    private Boolean enabled;

}
