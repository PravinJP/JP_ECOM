package com.ecommerce.project.model;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.w3c.dom.stylesheets.LinkStyle;

import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
@Data
@AllArgsConstructor
@Table(name = "addresses")
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long addressId;


    @NotBlank
    @Size(min=5,message = "street name should be atleast 5 characters")
    private String street;

    @NotBlank
    @Size(min=5,message = "Building name should be atleast 5 characters")
    private String buildingName;

    @NotBlank
    @Size(min=5,message = "city name should be atleast 5 characters")
    private String city;


    @NotBlank
    @Size(min=5,message = "Country name should be atleast 5 characters")
    private String country;

    @NotBlank
    @Size(min=5,message = "pincode should be atleast 6 characters")
    private String pincode;

    @ToString.Exclude
    @ManyToMany(mappedBy = "addresses")
    private List<User>users=new ArrayList<>();

    public Address(String buildingName, String city, String country, String pincode, String street ) {
        this.buildingName = buildingName;
        this.city = city;
        this.country = country;
        this.pincode = pincode;
        this.street = street;

    }
}

