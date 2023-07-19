package com.simbioff.simbioff.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "users")
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Data
public class UserModel implements UserDetails, Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id_user")
    private UUID idUser;

    @Column(name="user_name",length = 250, unique = true)
    private String userName;

    @Column(name="full_name")
    private String fullName;

    @Column(name="email", unique = true)
    private String email;

    @Column
    private String cpf ;

    @Column
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthDate;

    @Column
    private String zip;

    @Column
    private String street;

    @Column
    private String district;

    @Column
    private String city;

    @Column
    private String state;

    @Column
    private String country;

    @Column
    private String number;

    @Column
    private String phone;

    @Column(name="has_children")
    private Boolean hasChildren;

    @Column(name="nearby_airport")
    private String nearbyAirport;
    
    @Column(name="day_offs_available")
    private Double dayOffsAvailable;

   

    @Column(name="children_qty")
    private Integer childrenQty;

    @Column(name="pix_key")
    private String pixKey;

    @Column(name="children_names")
    private String childrenNames;

    @Column
    @JsonIgnore
    private String password;

    @Column
    @JsonIgnore
    private Boolean accountNonExpired;

    @Column
    @JsonIgnore
    private Boolean accountNonLocked;

    @Column
    @JsonIgnore
    private Boolean credentialsNonExpired;

    @Column
    private Boolean enabled;

    @Column
    private String token;

    @Column(name = "created_at")
    @CreationTimestamp
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate createdAt;

    @Column(name = "marital_state")
    private String maritalState;

    @Column(name = "start_on_team")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate startOnTeam;

    @Column(columnDefinition = "TIMESTAMP")
    private LocalDateTime tokenCreationDate;

    // Other Information

    @Column(name="function_at_work")
    private String functionAtWork;

    @Column(name="ped")
    private Boolean ped;

    @Column(name="responsible_ped")
    private String responsiblePed;

    @Column(name="english")
    private Boolean english;

    @Column(name="english_teacher")
    private String englishTeacher;

    @Column(name="therapy")
    private Boolean therapy;

    @Column(name="responsible_therapist")
    private String responsibleTherapist;

    // Graduação
    @Column(name="undergraduate")
    private Boolean undergraduate;

    @Column(name="undergraduate_course_name")
    private String undergraduateCourseName;

    // Pós-graduação
    @Column(name="graduate")
    private Boolean graduate;

    @Column(name="graduate_course_name")
    private String graduateCourseName;

    // Personal Information

    @Column(name="shirt_size")
    private String shirtSize;

    @Column(name="shoe_size")
    private String shoeSize;

    @Column(name="favourite_color")
    private String favouriteColor;

    @Column(name="favourite_food")
    private String favouriteFood;

    @Column(name = "beach_or_camp")
    private String beachOrCamp;

    @Column(name = "pets")
    private String pets;

    @Column(name = "hobbies")
    private String hobbies;

    @Column(name = "days_off_withdrawn")
    private Double daysOffWithdrawn;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "users_permission", joinColumns = {@JoinColumn(name = "id_user")},
            inverseJoinColumns = {@JoinColumn(name = "id_permission")})
    private List<PermissionModel> roles;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.roles;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.userName;
    }

    @Override
    public boolean isAccountNonExpired() {
        return this.accountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return this.accountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return this.credentialsNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return this.enabled;
    }


    public Boolean getAccountNonExpired() {
        return accountNonExpired;
    }

    public void setAccountNonExpired(Boolean accountNonExpired) {
        this.accountNonExpired = accountNonExpired;
    }

    public Boolean getAccountNonLocked() {
        return accountNonLocked;
    }

    public void setAccountNonLocked(Boolean accountNonLocked) {
        this.accountNonLocked = accountNonLocked;
    }

    public Boolean getCredentialsNonExpired() {
        return credentialsNonExpired;
    }

    public void setCredentialsNonExpired(Boolean credentialsNonExpired) {
        this.credentialsNonExpired = credentialsNonExpired;
    }
}
