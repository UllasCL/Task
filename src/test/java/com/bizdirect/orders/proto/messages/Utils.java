package com.bizdirect.orders.proto.messages;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ThreadLocalRandom;

public class Utils {

    private static final String ALPHA_NUMERIC_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    public Properties fetchFromPropertiesFile() {
        Properties properties = new Properties();
        try {
            File file = new File("src/main/resources/config.properties");
            FileInputStream fileInput = new FileInputStream(file);
            properties.load(fileInput);
            fileInput.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return properties;
    }

    public String randomAlphaNumeric(int count) {
        StringBuilder builder = new StringBuilder();
        while (count-- != 0) {
            int character = (int) (Math.random() * ALPHA_NUMERIC_STRING.length());
            builder.append(ALPHA_NUMERIC_STRING.charAt(character));
        }
        return builder.toString();
    }

    public int randomInt(int max) {
        int randomNum = 0;
        randomNum = ThreadLocalRandom.current().nextInt(4000, max + 1);
        return randomNum;
    }


    public String getValueFromDB(Connection con, String sql) throws Exception {
        String value = "";
        Statement stmt;
        stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery(sql);
        while (rs.next()) {
            value = rs.getString(1);
            break;
//                logger.info("ResultSet:" + value);
        }
        //System.out.println("Value is " + value);
        return value;
    }


    public List<HashMap<String, Object>> fetchEntry(Connection connection, String query) throws SQLException {
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(query);

        ResultSetMetaData metaData = resultSet.getMetaData();
        int columns = metaData.getColumnCount();
        List<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();
        while (resultSet.next()) {
            HashMap<String, Object> row = new HashMap<>();
            for (int i = 0; i < columns; i++) {
                row.put(resultSet.getString(i), resultSet.getObject(i));
            }
            list.add(row);
        }
        return list;
    }

}
