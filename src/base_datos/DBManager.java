package base_datos;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;

/**
 *
 * @author Antonio J. Gil
 * created on 10/05
 * @version 9.0
 */
public class DBManager {

	// Conexión a la base de datos
    private static Connection conn = null;

    // Configuración de la conexión a la base de datos
    private static final String DB_HOST = "localhost";
    private static final String DB_PORT = "3306";
    private static String DB_NAME = "tienda";
    private static String DB_URL = "jdbc:mysql://" + DB_HOST + ":" + DB_PORT + "/" + DB_NAME + "?serverTimezone=UTC";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "";

    // Configuración de la tabla Clientes
    private static String DB_TABLE = "clientes";

    //////////////////////////////////////////////////
    // MÉTODOS DE CONEXIÓN A LA BASE DE DATOS
    //////////////////////////////////////////////////
    
    /**
     * Cambia la base de datos a la que se accede
     * @param base Nombre de la Base de datos
     */
    public static void cambioBD(String base) {
    	DB_NAME = base;
    	
    	DB_URL = "jdbc:mysql://" + DB_HOST + ":" + DB_PORT + "/" + DB_NAME + "?serverTimezone=UTC";
    }

    /**
     * Intenta conectar con la base de datos.
     * @return true si pudo conectarse, false en caso contrario
     * @throws SQLException Lanza excepción si base de datos no existe.
     */
    public static boolean connect() throws SQLException{
            System.out.print("Conectando a la base de datos...");
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
            return true;
    }

    /**
     * Cierra la conexión con la base de datos.
     */
    public static void close() {
        try {
            System.out.print("Cerrando la conexión...");
            conn.close();
            System.out.println("OK!");
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
    
    //////////////////////////////////////////////////
    // SETTERS & GETTERS
    //////////////////////////////////////////////////

    /**
     * Pide la conexion
     * @return Devuelve la conexion
     */
	public static Connection getConn() {
		return conn;
	}

	/**
	 * Cambia la conexion
	 * @param conn Nueva conexion
	 */
	public static void setConn(Connection conn) {
		DBManager.conn = conn;
	}

	/**
	 * Pide el nombre de la BD
	 * @return devuelve el nombre de la BD
	 */
	public static String getDB_NAME() {
		return DB_NAME;
	}

	/**
	 * Cambia el nombre de la BD
	 * @param dB_NAME nuevo nombre
	 */
	public static void setDB_NAME(String dB_NAME) {
		DB_NAME = dB_NAME;
	}

	/**
	 * Pide el nombre de la tabla
	 * @return devuelve el nombre de la tabla
	 */
	public static String getDB_TABLE() {
		return DB_TABLE;
	}

	/**
	 * Cambia el nombre de la tabla
	 * @param dB_TABLE nuevo nombre de la tabla
	 */
	public static void setDB_TABLE(String dB_TABLE) {
		DB_TABLE = dB_TABLE;
	}
    
    
}
