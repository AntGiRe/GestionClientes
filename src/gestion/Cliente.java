package gestion;

import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;

import base_datos.DBManager;

/**
*
* @author Antonio J. Gil
* created on 10/05
* @version 9.0
*/
public class Cliente {

	/**
     * Solicita a la BD el cliente con id indicado
     * @param id id del cliente
     * @return ResultSet con el resultado de la consulta, null en caso de error
     */
    public static ResultSet getCliente(int id) {
        try {
        	
            // Realizamos la consulta SQL
            String sql = "SELECT * FROM " + DBManager.getDB_TABLE() + " WHERE " + getPK() + "= ?;";
            PreparedStatement stmt = DBManager.getConn().prepareStatement(sql,ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
            stmt.setString(1, id + "");
            ResultSet rs = stmt.executeQuery();
            
            // Si no hay primer registro entonces no existe el cliente
            if (!rs.first()) {
                return null;
            }

            // Todo bien, devolvemos el cliente
            return rs;

        } catch (SQLException ex) {
            System.out.println("[" + ex + "]");
            return null;
        }
    }
    
    /**
     * Solicita a la BD el nombre del campo de la PK
     * @return Devuelve el nombre del campo de la primary key
     */
    public static String getPK() {
    	try {
    		DatabaseMetaData metaDatos = DBManager.getConn().getMetaData();
    		ResultSet rs = metaDatos.getPrimaryKeys(DBManager.getDB_NAME(), null, DBManager.getDB_TABLE());
    	
    		while(rs.next()) {
    			return rs.getString("COLUMN_NAME");
    		}
    	} catch (SQLException ex) {
            System.out.println("[" + ex + "]");
        }
    	return null;
    }

    /**
     * Comprueba si en la BD existe el cliente con id indicado
     *
     * @param id id del cliente
     * @return verdadero si existe, false en caso contrario
     */
    public static boolean existsCliente(int id) {
        try {
            // Obtenemos el cliente
            ResultSet rs = getCliente(id);

            // Si rs es null, se ha producido un error
            if (rs == null) {
                return false;
            }

            // Si no existe primer registro
            if (!rs.first()) {
                rs.close();
                return false;
            }

            // Todo bien, existe el cliente
            rs.close();
            return true;

        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    /**
     * Imprime los datos del cliente con id indicado
     *
     * @param id id del cliente
     */
    public static void printCliente(int id) {
        try {
            // Obtenemos el cliente
            ResultSet rs = getCliente(id);
            ResultSetMetaData rsmd = rs.getMetaData();
            
            if (rs == null || !rs.first()) {
                System.out.println("Cliente " + id + " NO EXISTE");
                return;
            }
            
            imprimeColumnas();
            
            // Imprimimos su información por pantalla
            for(int i = 1; i <= rsmd.getColumnCount(); i++) {
        		if(rsmd.getColumnTypeName(i).equals("VARCHAR")) {
            		String texto = rs.getString(rsmd.getColumnName(i));
            		System.out.print(texto + "\t");
            	}
            	
            	if(rsmd.getColumnTypeName(i).equals("INT")) {
            		int entero = rs.getInt(rsmd.getColumnName(i));
            		System.out.print(entero + "\t");
            	}
            }
            System.out.println();

        } catch (SQLException ex) {
            System.out.println("Error al solicitar cliente " + id);
            ex.printStackTrace();
        }
    }
    
    /**
     * Imprime el nombre de las columnas de la tabla
     */
    public static void imprimeColumnas() {
    	
    	ResultSet rs = Tabla.getTabla(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
        ResultSetMetaData rsmd;
		try {
			rsmd = rs.getMetaData();
			
			for(int i = 1; i <= rsmd.getColumnCount(); i++) {
				System.out.print(rsmd.getColumnName(i) + "\t");
			}
			
			System.out.println();
		} catch (SQLException e) {
			e.printStackTrace();
		}
        
    	
    }

    /**
     * Solicita a la BD insertar un nuevo registro cliente
     *
     * @param lista Lista con las cadenas de texto a insertar
     * @return verdadero si pudo insertarlo, false en caso contrario
     */
    public static boolean insertCliente(ArrayList<String> lista) {
        try {
            // Obtenemos la tabla clientes
            System.out.print("Insertando fila...");
            ResultSet rs = Tabla.getTabla(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);

            // Insertamos el nuevo registro
            rs.moveToInsertRow();
            
            for(int i = 1; i <= Tabla.numeroColumnas(); i++) {
            	rs.updateString(Tabla.nombreColumna(i), lista.get(i-1));
            }
            
            
            rs.insertRow();

            // Todo bien, cerramos ResultSet y devolvemos true
            rs.close();
            System.out.println("OK!");
            return true;

        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        } catch (NullPointerException ex) {
        	System.out.println("[" + ex + "]");
        	return false;
        }
    }

    /**
     * Solicita a la BD modificar los datos de un cliente
     *
     * @param id id del cliente a modificar
     * @param aModificar cadena de texto del campo a modificar
     * @param nuevo nueva cadena de texto
     * @return verdadero si pudo modificarlo, false en caso contrario
     */
    public static boolean updateCliente(int id, String aModificar, String nuevo) {
        try {
            // Obtenemos el cliente
            System.out.print("Actualizando cliente " + id + "... ");
            ResultSet rs = getCliente(id);

            // Si no existe el Resultset
            if (rs == null) {
                System.out.println("Error. ResultSet null.");
                return false;
            }

            // Si tiene un primer registro, lo eliminamos
            if (rs.first()) {
                rs.updateString(aModificar, nuevo);
                rs.updateRow();
                rs.close();
                System.out.println("OK!");
                return true;
            } else {
                System.out.println("ERROR. ResultSet vacío.");
                return false;
            }
        } catch (SQLException ex) {
            System.out.println(ex);
            return false;
        }
    }

    /**
     * Solicita a la BD eliminar una fila
     *
     * @param id id de la fila a eliminar
     * @return verdadero si pudo eliminarlo, false en caso contrario
     */
    public static boolean deleteFila(int id) {
        try {
            System.out.print("Eliminando fila " + id + "... ");

            // Obtenemos el cliente
            ResultSet rs = getCliente(id);

            // Si no existe el Resultset
            if (rs == null) {
                System.out.println("ERROR. ResultSet null.");
                return false;
            }

            // Si existe y tiene primer registro, lo eliminamos
            if (rs.first()) {
                rs.deleteRow();
                rs.close();
                System.out.println("OK!");
                return true;
            } else {
                System.out.println("ERROR. ResultSet vacío.");
                return false;
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }
}
