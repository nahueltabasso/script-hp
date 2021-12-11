package script.hp.data;

import java.sql.*;

public class DatabaseConnection {

    // Valores de conexion a la base de datos (en este caso MySql)
    private static String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    private static String JDBC_URL = "jdbc:mysql://localhost:3306/hospital_local?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC&zerodatetimebehavior=Converttonull";
    private static String JDBC_USER = "root";
    private static String JDBC_PASS = "root123-";
    private static Driver driver = null;

    // Usamos la palabra reservada synchronized para evitar fallos en  la conexion
    public static synchronized Connection getConnection() throws SQLException {
        if (driver == null) {
            try {
                // Registramos el DRIVER
                Class jdbcDriverClass = Class.forName(JDBC_DRIVER);
                driver = (Driver) jdbcDriverClass.newInstance();
                DriverManager.registerDriver(driver);
            } catch (Exception e) {
                System.out.println("Ocurrio un error al cargar el driver JDBC");
                e.printStackTrace();
            }
        }
        return DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASS);
    }

    // Cierre del resultSet
    public static void closeResultSet(ResultSet rs) {
        try {
            if (rs != null) {
                rs.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Cierre del preparedStatement
    public static void closePreparedStatement(PreparedStatement statement) {
        try {
            if (statement != null) {
                statement.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Cierre de la conexion
    public static void closeConnection(Connection connection) {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
