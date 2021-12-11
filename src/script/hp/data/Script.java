package script.hp.data;

import script.hp.models.dto.LocalidadResponseDTO;
import script.hp.models.entity.Provincia;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Logger;

public class Script {

    private static final Logger logger = Logger.getLogger(String.valueOf(Script.class));

    /**
     * Metodo que inserta un registro en la base de datos en la tabla "provincia"
     * @param provincia
     * @return
     * @throws SQLException
     */
    public Provincia insertarRegistroTablaProvincia(Provincia provincia) throws SQLException {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            connection = DatabaseConnection.getConnection();
            statement = connection.prepareStatement("insert into provincia(nombre) "
                    + "values (?)", PreparedStatement.RETURN_GENERATED_KEYS);
            // Pasamos los parametros para el insert
            statement.setString(1, provincia.getNombre());

            statement.executeUpdate();
            resultSet = statement.getGeneratedKeys();

            // Validamos que nos devuelva el id para retornar la provinca
            if (resultSet != null && resultSet.next()) {
                provincia.setId(resultSet.getLong(1));
            }
        } catch (SQLException e) {
            logger.info("Ocurrio un error en la conexion a la base de datos MySQL");
            e.printStackTrace();
        } finally {
            DatabaseConnection.closeResultSet(resultSet);
            DatabaseConnection.closePreparedStatement(statement);
            DatabaseConnection.closeConnection(connection);
        }
        return provincia;
    }

    /**
     * Metodo que inserta multiples registros en la base de datos en la tabla "localidad"
     * @param localidades, idProvincia
     * @return
     * @throws SQLException
     */
    public boolean insertarMultiplesRegistrosTablaLocalidad(List<LocalidadResponseDTO> localidades, Long idProvincia) throws SQLException {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        int[] cantidadRegistrosInsertadors;
        try {
            connection = DatabaseConnection.getConnection();
            statement = connection.prepareStatement("insert into localidad(codigo_postal, nombre, id_fk_provincia) "
                    + "values (?, ?, ?) ", PreparedStatement.RETURN_GENERATED_KEYS);

            for (LocalidadResponseDTO l : localidades) {
                statement.setString(1, "1111");
                statement.setString(2, l.getNombre());
                statement.setLong(3, idProvincia);
                statement.addBatch();
            }
            cantidadRegistrosInsertadors = statement.executeBatch();
            if (cantidadRegistrosInsertadors.length != localidades.size()) {
                return false;
            }
        } catch (SQLException e) {
            logger.info("Ocurrio un error en la conexion a la base de datos MySQL");
            e.printStackTrace();
        } finally {
            DatabaseConnection.closeResultSet(resultSet);
            DatabaseConnection.closePreparedStatement(statement);
            DatabaseConnection.closeConnection(connection);
        }
        return true;
    }

    /**
     * Metodo que inserta multiples registros en la base de datos en la tabla "tipo_persona"
     * @param roles
     * @return
     * @throws SQLException
     */
    public boolean insertarMultiplesRegistrosTablaTipoPersona(List<String> roles) throws SQLException {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        int[] cantidadRegistrosInsertadors;
        try {
            connection = DatabaseConnection.getConnection();
            statement = connection.prepareStatement("insert into tipo_persona(descripcion) "
                    + "values (?) ", PreparedStatement.RETURN_GENERATED_KEYS);

            for (String r : roles) {
                statement.setString(1, r);
                statement.addBatch();
            }

            cantidadRegistrosInsertadors = statement.executeBatch();
            if (cantidadRegistrosInsertadors.length != roles.size()) {
                return false;
            }
        } catch (SQLException e) {
            logger.info("Ocurrio un error en la conexion a la base de datos MySQL");
            e.printStackTrace();
        } finally {
            DatabaseConnection.closeResultSet(resultSet);
            DatabaseConnection.closePreparedStatement(statement);
            DatabaseConnection.closeConnection(connection);
        }
        return true;
    }

}
