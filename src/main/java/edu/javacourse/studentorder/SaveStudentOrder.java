package edu.javacourse.studentorder;

import edu.javacourse.studentorder.dao.StudentOrderDaoImpl;
import edu.javacourse.studentorder.dao.StudentOrderDao;
import edu.javacourse.studentorder.domain.*;

import java.time.LocalDate;
import java.util.List;

public class SaveStudentOrder
{
    public static void main(String[] args) throws Exception{
//        StudentOrder s = buildStudentOrder(10);
        StudentOrderDao dao = new StudentOrderDaoImpl();
//        long id = dao.saveStudentOrder(s);
//        System.out.println(id);
        
        List<StudentOrder> so = dao.getStudentOrders();
        for (StudentOrder elem : so) {
            System.out.println(elem.getStudentOrderId());
        }
//        StudentOrder so = new StudentOrder();
//        long ans = saveStudentOrder(so);
//        System.out.println(ans);
    }

    static long saveStudentOrder(StudentOrder studentOrder) {
        long answer = 199;
        System.out.println("saveStudentOrder");

        return answer;
    }


}
