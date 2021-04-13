package edu.javacourse.studentorder.validator.register;

import edu.javacourse.studentorder.config.Config;
import edu.javacourse.studentorder.domain.register.CityRegisterRequest;
import edu.javacourse.studentorder.domain.register.CityRegisterResponse;
import edu.javacourse.studentorder.domain.Person;
import edu.javacourse.studentorder.exception.CityRegisterException;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class RealCityRegisterChecker implements CityRegisterChecker
{
    public CityRegisterResponse checkPerson(Person person)
            throws CityRegisterException {
        try {

            CityRegisterRequest request = new CityRegisterRequest(person);
//        request.setStreetCode(person.getAddress().getStreet().getStreetCode().intValue());//костыль
            Client client = ClientBuilder.newClient();
            CityRegisterResponse response = client.target(Config.getProperties(Config.CR_URL))
                    .request(MediaType.APPLICATION_JSON)
                    .post(Entity.entity(request, MediaType.APPLICATION_JSON))
                    .readEntity(CityRegisterResponse.class);

            return response;
        } catch (Exception e) {
            throw new CityRegisterException("", e.getMessage(), e);
        }
    }
}
