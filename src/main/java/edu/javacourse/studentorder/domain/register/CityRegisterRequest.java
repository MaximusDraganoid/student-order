package edu.javacourse.studentorder.domain.register;

import edu.javacourse.studentorder.domain.Address;
import edu.javacourse.studentorder.domain.Person;
import edu.javacourse.studentorder.util.LocalDateAdapter;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.time.LocalDate;

public class CityRegisterRequest {
    private String surName;
    private String givenName;
    private String patronymicName;
    @XmlJavaTypeAdapter(value = LocalDateAdapter.class)
    private LocalDate dateOfBirthday;
    private String building;
    private String extension;
    private String apartment;
    private Integer streetCode;

    public CityRegisterRequest() {}

    public CityRegisterRequest(Person person) {
        surName = person.getSurName();
        givenName = person.getGivenName();
        patronymicName = person.getPatronymic();
        dateOfBirthday = person.getDateOfBirth();
        Address adr = person.getAddress();
        streetCode = person.getAddress().getStreet().getStreetCode().intValue();
        building = adr.getBuilding();
        extension = adr.getExtension();
        apartment = adr.getApartment();
    }

    @Override
    public String toString() {
        return "CityRegisterRequest{" +
                "surName='" + surName + '\'' +
                ", givenName='" + givenName + '\'' +
                ", patronymicName='" + patronymicName + '\'' +
                ", dateOfBirthday=" + dateOfBirthday +
                ", building='" + building + '\'' +
                ", extension='" + extension + '\'' +
                ", apartment='" + apartment + '\'' +
                '}';
    }

    public String getSurName() {
        return surName;
    }

    public void setSurName(String surName) {
        this.surName = surName;
    }

    public String getGivenName() {
        return givenName;
    }

    public void setGivenName(String givenName) {
        this.givenName = givenName;
    }

    public String getPatronymicName() {
        return patronymicName;
    }

    public void setPatronymicName(String patronymicName) {
        this.patronymicName = patronymicName;
    }

    public LocalDate getDateOfBirthday() {
        return dateOfBirthday;
    }

    public void setDateOfBirthday(LocalDate dateOfBirthday) {
        this.dateOfBirthday = dateOfBirthday;
    }

    public String getBuilding() {
        return building;
    }

    public void setBuilding(String building) {
        this.building = building;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public String getApartment() {
        return apartment;
    }

    public void setApartment(String apartment) {
        this.apartment = apartment;
    }

    public Integer getStreetCode() {
        return streetCode;
    }

    public void setStreetCode(Integer streetCode) {
        this.streetCode = streetCode;
    }
}
