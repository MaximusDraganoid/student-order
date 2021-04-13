package edu.javacourse.studentorder.dao;

import edu.javacourse.studentorder.config.Config;
import edu.javacourse.studentorder.domain.*;
import edu.javacourse.studentorder.exception.DaoException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class StudentOrderDaoImpl implements StudentOrderDao{

    private static final Logger logger = LoggerFactory.getLogger(StudentOrderDaoImpl.class);

    private static final String INSERT_ORDER =
            "INSERT INTO jc_student_order(" +
                    " student_order_status, student_order_date, h_sur_name, " +
                    " h_given_name, h_patronymic, h_date_of_birth, h_passport_seria, " +
                    " h_passport_number, h_passport_date, h_passport_office_id, h_post_index, " +
                    " h_street_code, h_building, h_extension, h_apartment, h_university_id, h_student_number, " +
                    " w_sur_name, w_given_name, w_patronymic, w_date_of_birth, w_passport_seria, " +
                    " w_passport_number, w_passport_date, w_passport_office_id, w_post_index, " +
                    " w_street_code, w_building, w_extension, w_apartment, w_university_id, w_student_number,  " +
                    " certificate_id, register_office_id, marriage_date)" +
                    " VALUES (?, ?, ?, " +
                    " ?, ?, ?, ?, " +
                    " ?, ?, ?, ?, " +
                    " ?, ?, ?, ?, ?, ?, " +
                    " ?, ?, ?, ?, ?, " +
                    " ?, ?, ?, ?," +
                    " ?, ?, ?, ?, ?, ?," +
                    "?, ?, ?);";
    private static final String INSERT_CHILD =
            "INSERT INTO public.jc_student_child(" +
                    " student_order_id, c_sur_name, c_given_name," +
                    " c_patronymic, c_date_of_birth, c_certificate_number, c_certificate_date, " +
                    "c_register_office_id, c_post_index, c_street_code, c_building, c_extension, c_apartment)" +
                    "VALUES (?, ?, ?," +
                    " ?, ?, ?, ?, " +
                    "?, ?, ?, ?, " +
                    "?, ?);";
    private static final String SELECT_ORDERS =
            "SELECT so.*, ro.r_office_area_id, ro.r_office_name, \n" +
            "po_h.p_office_area_id as h_p_office_area_id, \n" +
            "po_h.p_office_name as h_p_office_name,\n" +
            "po_w.p_office_area_id as w_p_office_area_id, \n" +
            "po_w.p_office_name as w_p_office_name\n" +
            "FROM jc_student_order so\n" +
            "INNER JOIN jc_register_office ro ON ro.r_office_id = so.register_office_id\n" +
            "INNER JOIN jc_passport_office po_h ON po_h.p_office_id = so.h_passport_office_id\n" +
            "INNER JOIN jc_passport_office po_w ON po_w.p_office_id = so.w_passport_office_id\n" +
            "WHERE student_order_status = ? ORDER BY student_order_date LIMIT ?;\n";

    private static final String SELECT_CHILD = "SELECT soc.*, ro.r_office_area_id, ro.r_office_name\n" +
            "FROM jc_student_child soc \n" +
            "INNER JOIN jc_register_office ro ON ro.r_office_id = soc.c_register_office_id\n" +
            "WHERE student_order_id IN ";

    private static final String SELECT_ORDERS_FULL =
            "SELECT so.*, ro.r_office_area_id, ro.r_office_name, \n" +
                    "po_h.p_office_area_id as h_p_office_area_id, \n" +
                    "po_h.p_office_name as h_p_office_name,\n" +
                    "po_w.p_office_area_id as w_p_office_area_id, \n" +
                    "po_w.p_office_name as w_p_office_name, " +
                    "soc.*, ro_c.r_office_area_id, ro_c.r_office_name\n" +
                    "FROM jc_student_order so\n" +
                    "INNER JOIN jc_register_office ro ON ro.r_office_id = so.register_office_id\n" +
                    "INNER JOIN jc_passport_office po_h ON po_h.p_office_id = so.h_passport_office_id\n" +
                    "INNER JOIN jc_passport_office po_w ON po_w.p_office_id = so.w_passport_office_id\n" +
                    "INNER JOIN jc_student_child soc  ON soc.student_order_id = so.student_order_id\n" +
                    "INNER JOIN jc_register_office ro_c ON ro_c.r_office_id = soc.c_register_office_id\n" +
                    "WHERE student_order_status = ? ORDER BY so.student_order_id LIMIT ?;\n";

    private Connection getConnection() throws SQLException {
        return ConnectionBuilder.getConnection();
    }

    @Override
    public Long saveStudentOrder(StudentOrder so) throws DaoException {
        Long result = -1L;

        logger.debug("SO: {}", so);

        try (Connection conn = getConnection();
             PreparedStatement statement = conn.prepareStatement(INSERT_ORDER, new String[]{"student_order_id"})) {

            //начинаем транзакцию
            conn.setAutoCommit(false);//отключаем автоматическое управление транзакциями. До этого отключения, каждая операция
            //с бд выполнялась как самостоятельная отдельная транзакция

            //ест 2 основные операции работы с транзакцией. С момента отключения автокоммита, мы управляем
            //транзакциями исключительно вручную. Есть 2 операции - принятие изменений (conn.commit();) и откат до инструкции
            //conn.setAutoCommit(false);  - conn.rollback(); Для работы можно обернуть их в такой блок:
            try {
                //header
                statement.setInt(1, StudentOrderStatus.START.ordinal());
                statement.setTimestamp(2, java.sql.Timestamp.valueOf(LocalDateTime.now()));
                //husband and wife
                setParamForAdult(statement, 3, so.getHusband());
                setParamForAdult(statement, 18, so.getWife());

                //order data
                statement.setString(33, so.getMarriageCertificateId());
                statement.setLong(34, so.getMarriageOffice().getOfficeId());
                statement.setDate(35, java.sql.Date.valueOf(so.getMarriageDate()));

                statement.executeUpdate();
                ResultSet gkRs = statement.getGeneratedKeys();
                if (gkRs.next()) {
                    result = gkRs.getLong(1);
                }
                gkRs.close();

                saveChildren(conn, so, result);

                conn.commit();//принятие изменений в бд
            } catch (SQLException ex) {
                logger.error(ex.getMessage(), ex);
                conn.rollback(); //откатываем изменения бд в случае если что то пошло не так
                throw ex;//пробрасывае возникшее исключени на уровень выше, чтобы оно было обнаружено и обработано
            }

        } catch (SQLException ex) {
            throw new DaoException(ex);
        }

        return result;
    }

    private void saveChildren(Connection conn, StudentOrder so, Long soId) throws SQLException{
        try(PreparedStatement statement = conn.prepareStatement(INSERT_CHILD)) {
            for (Child child : so.getChildren()) {
                statement.setLong(1, soId);
                setParamsForChild(child, statement);
                statement.addBatch();
            }
            statement.executeBatch();
        }
    }

    private void setParamForAdult(PreparedStatement statement, int start, Adult adult ) throws SQLException{
        setParamsForPerson(statement, start, adult);
        statement.setString(start + 4, adult.getPassportSeria());
        statement.setString(start + 5, adult.getPassportNumber());
        statement.setDate(start + 6, java.sql.Date.valueOf(adult.getIssueDate()));
        statement.setLong(start + 7, adult.getIssueDepartment().getOfficeId());
        setParamsForAddress(statement, start + 8, adult);
        statement.setLong(start + 13, adult.getUnivesity().getUniversityId());
        statement.setString(start + 14, adult.getStudentId());
    }

    private void setParamsForChild(Child child, PreparedStatement statement) throws SQLException {
        setParamsForPerson(statement, 2, child);
        statement.setString(6, child.getCertificateNumber());
        statement.setDate(7, java.sql.Date.valueOf(child.getIssueDate()));
        statement.setLong(8, child.getIssueDepartment().getOfficeId());
        setParamsForAddress(statement, 9, child );
    }

    private void setParamsForPerson(PreparedStatement statement, int start, Person person) throws SQLException {
        statement.setString(start, person.getSurName());
        statement.setString(start + 1, person.getGivenName());
        statement.setString(start + 2, person.getPatronymic());
        statement.setDate(start + 3, java.sql.Date.valueOf(person.getDateOfBirth()));
    }

    private void setParamsForAddress (PreparedStatement statement, int start, Person person ) throws SQLException {
        Address address = person.getAddress();
        statement.setString(start, address.getPostCode());
        statement.setLong(start + 1, address.getStreet().getStreetCode());
        statement.setString(start + 2, address.getBuilding());
        statement.setString(start + 3, address.getExtension());
        statement.setString(start + 4, address.getApartment());
    }

    @Override
    public List<StudentOrder> getStudentOrders() throws DaoException {
//        return getStudentOrdersTwoSelect();
        return getStudentOrdersOneSelect();
    }

    //в двух следующих методах мапа вводится для того, чтобы увеличить скорость обращения к конкретной заявке и не
    //нужно было бы обходить весь массив в поисках какой то конкретной
    private List<StudentOrder> getStudentOrdersTwoSelect() throws DaoException {
        List<StudentOrder> result = new LinkedList<>();

        try (Connection conn = getConnection();
             PreparedStatement statement = conn.prepareStatement(SELECT_ORDERS)) {
            statement.setInt(1, StudentOrderStatus.START.ordinal());
            statement.setInt(2, Integer.parseInt(Config.getProperties(Config.DB_LIMIT)));
            ResultSet rs = statement.executeQuery();

            while (rs.next()) {
                StudentOrder so = getFullStudentOrder(rs);

                result.add(so);
            }
            findChildren(conn, result);

            rs.close();
        } catch (SQLException ex) {
            logger.error(ex.getMessage(), ex);
            throw new DaoException(ex);
        }

        return result;
    }

    private List<StudentOrder> getStudentOrdersOneSelect() throws DaoException {
        List<StudentOrder> result = new LinkedList<>();

        try (Connection conn = getConnection();
             PreparedStatement statement = conn.prepareStatement(SELECT_ORDERS_FULL)) {

            statement.setInt(1, StudentOrderStatus.START.ordinal());
            int limit = Integer.parseInt(Config.getProperties(Config.DB_LIMIT));
            statement.setInt(2, limit);

            Map<Long, StudentOrder> maps = new HashMap<>();

            ResultSet rs = statement.executeQuery();
            int counter = 0;
            while (rs.next()) {
                Long soId = rs.getLong("student_order_id");

                if (!maps.containsKey(soId)) {
                    StudentOrder so = getFullStudentOrder(rs);

                    result.add(so);
                    maps.put(soId, so);
                }
                StudentOrder so = maps.get(soId);
                so.addChild(fillChild(rs));
                counter++;
            }
            //в ситуации, когда мы поулчаем все limit записей, может получиться так, что для последней семьи мы
            //получаем не всех ее детей (см. как работает запрос). Поэтому мы просто ее отбрасываем из рассмотрения
            if (counter >= limit) {
                result.remove(result.size() - 1);
            }
            rs.close();
        } catch (SQLException ex) {
            logger.error(ex.getMessage(), ex);
            throw new DaoException(ex);
        }

        return result;
    }

    private void findChildren(Connection conn, List<StudentOrder> result) throws SQLException{
        String cl = "(" + result.stream().map(so-> String.valueOf(so.getStudentOrderId()))
            .collect(Collectors.joining(",")) + ")";

        Map<Long, StudentOrder> maps = result.stream().collect(Collectors.toMap(so -> so.getStudentOrderId(), so -> so));

        try (PreparedStatement statement = conn.prepareStatement(SELECT_CHILD + cl)) {
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                Child ch = fillChild(rs);
                StudentOrder so = maps.get(rs.getLong("student_order_id"));
                so.addChild(ch);
            }
        }
    }

    private Adult fillAdult(ResultSet rs, String pref) throws SQLException {
        Adult adult = new Adult();

        adult.setSurName(rs.getString(pref + "sur_name"));
        adult.setGivenName(rs.getString(pref + "given_name"));
        adult.setPatronymic(rs.getString(pref + "patronymic"));
        adult.setDateOfBirth(rs.getDate(pref + "date_of_birth").toLocalDate());
        adult.setPassportSeria(rs.getString(pref + "passport_seria"));
        adult.setPassportNumber(rs.getString(pref + "passport_number"));
        adult.setIssueDate(rs.getDate(pref + "passport_date").toLocalDate());

        Long poId = rs.getLong(pref + "passport_office_id");
        String poArea = rs.getString(pref + "p_office_area_id");
        String poName = rs.getString(pref + "p_office_name");
        PassportOffice po = new PassportOffice(poId, poArea, poName); // мы умышленно
        adult.setIssueDepartment(po);
        //осталвяем часть полей пустыми, т.к. на данный момент в них нет необходимости. Как только нам
        //понадобится вытащить более подробную информацию, мы сможем это сделать при помощи идентефикатора

        Address adr = new Address();
        adr.setPostCode(rs.getString(pref + "post_index"));
        adr.setBuilding(rs.getString(pref + "building"));
        adr.setExtension(rs.getString(pref + "extension"));
        adr.setApartment(rs.getString(pref + "apartment"));
        Street street = new Street(rs.getLong(pref + "street_code"), "");
        adr.setStreet(street);
        adult.setAddress(adr);

        University uni = new University(rs.getLong(pref + "university_id"), "");
        adult.setUnivesity(uni);
        adult.setStudentId(rs.getString(pref + "student_number"));

        return adult;
    }

    private void fillStudentOrder(ResultSet rs, StudentOrder so) throws SQLException{
        so.setStudentOrderId(rs.getLong("student_order_id"));
        so.setStudentOrderDate(rs.getTimestamp("student_order_date").toLocalDateTime());
        so.setStudentOrderStatus(StudentOrderStatus.fromValue(rs.getInt("student_order_status")));
    }

    private void fillMarriage(ResultSet rs, StudentOrder so) throws SQLException{
        so.setMarriageCertificateId(rs.getString("certificate_id"));
        so.setMarriageDate(rs.getDate("marriage_date").toLocalDate());
        Long id = rs.getLong("register_office_id");
        String areaId = rs.getString("r_office_area_id");
        String name = rs.getString("r_office_name");
        RegisterOffice ro = new RegisterOffice(id, areaId, name);
        so.setMarriageOffice(ro);
    }

    private Child fillChild(ResultSet rs) throws SQLException {
        String surname = rs.getString("c_sur_name");
        String givenName = rs.getString("c_given_name");
        String patronymicName = rs.getString("c_patronymic");
        LocalDate dateOfBirth = rs.getDate("c_date_of_birth").toLocalDate();

        Child child = new Child(surname, givenName, patronymicName, dateOfBirth);

        child.setCertificateNumber(rs.getString("c_certificate_number"));
        child.setIssueDate(rs.getDate("c_certificate_date").toLocalDate());

        Long roId = rs.getLong("c_register_office_id");
        String roArea = rs.getString("r_office_area_id");
        String roName = rs.getString("r_office_name");
        RegisterOffice ro = new RegisterOffice(roId, roArea, roName);
        child.setIssueDepartment(ro);

        Address adr = new Address();
        adr.setPostCode(rs.getString("c_post_index"));
        adr.setBuilding(rs.getString("c_building"));
        adr.setExtension(rs.getString("c_extension"));
        adr.setApartment(rs.getString("c_apartment"));
        Street street = new Street(rs.getLong("c_street_code"), "");
        adr.setStreet(street);
        child.setAddress(adr);

        return child;
    }

    private StudentOrder getFullStudentOrder(ResultSet rs) throws SQLException {
        StudentOrder so = new StudentOrder();
        fillStudentOrder(rs, so);
        fillMarriage(rs, so);

        so.setHusband(fillAdult(rs, "h_"));
        so.setWife(fillAdult(rs, "w_"));
        return so;
    }

}
