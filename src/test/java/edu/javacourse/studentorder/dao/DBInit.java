package edu.javacourse.studentorder.dao;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.Statement;
import java.util.List;
import java.util.stream.Collectors;

public class DBInit {
    public static void startUp() throws Exception {
    //У каждого класса в java есть описатель класса. У каждого описателя есть класслоадер, который его загружает.
        //при помощи получаемого url мы можем обращаться и к внешним файлам, которые будут лежать в target. Корнем для поиска
        //файлов в данном случае является target/
        //важно, что файлы он будет доставать из целевой папки, в которой будет храниться сборка. Т.к. мы положили
        //все ресурсы в одно место в пакете main, то и все остальные будут храниться там где нужно. Поэтому такая подгрузка
        //через класс лоудер работает (файлы будут искаться в target/classes/
        URL url1 = DictionaryDaoImplTest.class.getClassLoader().getResource("student_project.sql");
        URL url2 = DictionaryDaoImplTest.class.getClassLoader().getResource("student_data.sql");

        List<String> str1 = Files.readAllLines(Path.of(url1.toURI()));
        String sql1 = str1.stream().collect(Collectors.joining());

        List<String> str2 = Files.readAllLines(Path.of(url2.toURI()));
        String sql2 = str2.stream().collect(Collectors.joining());

        try (Connection conn = ConnectionBuilder.getConnection(); Statement stmt = conn.createStatement()) {
            stmt.execute(sql1);
            stmt.execute(sql2);
        }
    }

}
