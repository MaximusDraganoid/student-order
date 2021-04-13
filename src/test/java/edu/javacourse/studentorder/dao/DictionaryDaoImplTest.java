package edu.javacourse.studentorder.dao;

import edu.javacourse.studentorder.domain.CountryArea;
import edu.javacourse.studentorder.domain.PassportOffice;
import edu.javacourse.studentorder.domain.RegisterOffice;
import edu.javacourse.studentorder.domain.Street;
import edu.javacourse.studentorder.exception.DaoException;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;

public class DictionaryDaoImplTest {
    private static final Logger logger = LoggerFactory.getLogger(DictionaryDaoImplTest.class);
    @BeforeClass //помечает метод, который будет выполнять при запуске всех тестов, т.е. он первый раз запустится, а потом
    //будут выполняться все остальные тесты, т.е. он выполнится 1 раз. Если этот метод не запустится, все тесты будут
    //проигнорированы
    public static void startUp() throws Exception {
        DBInit.startUp();
    }

    //@Before //будет выполняться перед каждым тестом
    //есть аналогичне аннотации @After и @AfterClass

    @Test
    public void testStreet() throws DaoException {
        LocalDateTime dt = LocalDateTime.now();
        logger.info("TEST {}", dt);
        List<Street> d = new DictionaryDaoImpl().findStreets("про");
        Assert.assertTrue(d.size() == 2);
    }

    @Test
//    @Ignore
    public void testPassportOffice() throws DaoException {
        List<PassportOffice> po = new DictionaryDaoImpl().findPassportOffices("010020000000");
        Assert.assertTrue(po.size() == 2);
    }

    @Test
    public void testRegisterOffice() throws DaoException {
        List<RegisterOffice> ro = new DictionaryDaoImpl().findRegisterOffices("010010000000");
        Assert.assertTrue(ro.size() == 2);
    }

    @Test
    public void testArea() throws DaoException {

        List<CountryArea> countryAreas1 = new DictionaryDaoImpl().findAreas("");
        Assert.assertTrue(countryAreas1.size() == 2);

        List<CountryArea> countryAreas2 = new DictionaryDaoImpl().findAreas("020000000000");
        Assert.assertTrue(countryAreas1.size() == 2);

        List<CountryArea> countryAreas3 = new DictionaryDaoImpl().findAreas("020010000000");
        Assert.assertTrue(countryAreas1.size() == 2);

        List<CountryArea> countryAreas4 = new DictionaryDaoImpl().findAreas("020010010000");
        Assert.assertTrue(countryAreas1.size() == 2);
    }
}