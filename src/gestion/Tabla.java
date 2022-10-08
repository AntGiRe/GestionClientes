package gestion;

import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import base_datos.DBManager;

/**
*
* @author Antonio J. Gil
* created on 10/05
* @version 9.0
*/
public class Tabla {

	/**
     * Muestra nombre de cada columna de una tabla
     */
    public static void infoColumnaTabla() {
    	try {
    		ResultSet rs = getTabla(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
            ResultSetMetaData rsmd = rs.getMetaData();
            
            for(int i = 0; i < rsmd.getColumnCount(); i++) {
            	System.out.print(rsmd.getColumnName(i+1) + "  ");
            }
    	} catch (SQLException e) {
    		System.out.println(e);
    	}
		
    }
    
    /**
     * Busca las filas que cumplan la condición propuesta
     * @param columna nombre de la columna
     * @param texto texto el cual se busca
     */
    public static void getColumnasFiltradas(String columna, String texto) {
    	try {
        	
            String sql = "SELECT * FROM " + DBManager.getDB_TABLE() + " WHERE " + columna + "= ?;";
            PreparedStatement stmt = DBManager.getConn().prepareStatement(sql,ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
            stmt.setString(1, texto);
            ResultSet rs = stmt.executeQuery();
            ResultSetMetaData rsmd = rs.getMetaData();
            
            while (rs.next()) {
            	
            	for(int i = 1; i <= rsmd.getColumnCount(); i++) {
            		if(rsmd.getColumnTypeName(i).equals("VARCHAR")) {
                		String cadena = rs.getString(rsmd.getColumnName(i));
                		System.out.print(cadena + "\t");
                	}
                	
                	if(rsmd.getColumnTypeName(i).equals("INT")) {
                		int entero = rs.getInt(rsmd.getColumnName(i));
                		System.out.print(entero + "\t");
                	}
                }
                System.out.println();
            }
            rs.close();

        } catch (SQLException ex) {
            System.out.println("[" + ex + "]");
        }
    }
    
    /**
     * Se introduce una nueva tabla
     * @param tabla cadena de texto nombre de la tabla
     */
    public static void setTabla(String tabla) {
    	DBManager.setDB_TABLE(tabla);
    }
    
    /**
     * Obtiene toda la tabla clientes de la base de datos
     * @param resultSetType Tipo de ResultSet
     * @param resultSetConcurrency Concurrencia del ResultSet
     * @return ResultSet (del tipo indicado) con la tabla, null en caso de error
     */
    public static ResultSet getTabla(int resultSetType, int resultSetConcurrency) {
    	String sql = "select * from " + DBManager.getDB_TABLE() + ";";
        try {
            PreparedStatement stmt = DBManager.getConn().prepareStatement(sql, resultSetType, resultSetConcurrency);
            ResultSet rs = stmt.executeQuery();
            return rs;
        } catch (SQLException ex) {
            System.out.println("[" + ex + "] - Pruebe con otra tabla");
            return null;
        }

    }
    
    /**
     * Pide el numero de columnas de una tabla
     * @return Devuelve el numero de columnas de una tabla
     */
    public static int numeroColumnas() {
    	
    	try {
    		ResultSet rs = getTabla(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
            ResultSetMetaData rsmd = rs.getMetaData();
            
            return rsmd.getColumnCount();
    	} catch (SQLException e) {
    		System.out.println(e);
    	}
    	return 0;
    }
    
    /**
     * Pide el nombre de la columna a partir de su posición
     * @param numColumna posicion de la columna
     * @return devuelve una cadena de texto con el nombre de la columna
     */
    public static String nombreColumna(int numColumna) {
    	try {
        	
            ResultSet rs = getTabla(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
            ResultSetMetaData rsmd = rs.getMetaData();
            
            return rsmd.getColumnName(numColumna);

        } catch (SQLException ex) {
            ex.printStackTrace();
        } catch (NullPointerException ex) {
        	System.out.println("[" + ex + "]");
        }
    	
    	return null;
    }

    /**
     * Imprime por pantalla el contenido de la tabla clientes
     */
    public static void printTablaClientes() {
    	
        try {
        	
            ResultSet rs = getTabla(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
            ResultSetMetaData rsmd = rs.getMetaData();
            
            
            while (rs.next()) {
            	
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
            }
            rs.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        } catch (NullPointerException ex) {
        	System.out.println("[" + ex + "]");
        }
    }
    
    /**
     * Lista las tablas disponibles de la BD
     */
    public static void listarTablas() {
    	try {
			DatabaseMetaData metaDatos = DBManager.getConn().getMetaData();
			
			ResultSet rs = metaDatos.getTables(DBManager.getDB_NAME(), null, "%", null);
			
			while (rs.next()) {
				   String tabla = rs.getString(3);
				   System.out.println(" > " + tabla);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
    }
}
