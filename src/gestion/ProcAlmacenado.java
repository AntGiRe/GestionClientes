package gestion;

import java.sql.CallableStatement;
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
public class ProcAlmacenado {

	/**
     * Crea una tabla nueva en la BD
     * @param nombre nombre de la nueva tabla
     * @param lista lista con toda la información necesaria
     */
    public static void crearTabla(String nombre, ArrayList<String> lista) {
    	String sql = "create table " + nombre + " (\n";
    	PreparedStatement stmt = null;
    	
    	for(int i = 0; i < lista.size(); i++) {
    		
    		if(lista.get(i).equalsIgnoreCase("String")) {
    			sql += " varchar(50)";
    		} else if(lista.get(i).equalsIgnoreCase("Int")) {
    			sql += " int";
    		} else {
    			sql += " " + lista.get(i);
    		}
			  
			  if((i+1)%3==0 && i!=(lista.size()-1)) {
				  sql += ",\n";
			  }
			  
		  }
    	
    	sql += "\n);";
    	
    	System.out.println(sql);
    	try {
    		  stmt = DBManager.getConn().prepareStatement(sql);
    		  
    		  stmt.execute(); 
    		  stmt.close();	           
    	} catch (SQLException sqle) { 
    		  System.out.println(sqle);
    	}
    }
    
    /**
     * Pide a la BD el nombre de todos los procedimientos almacenados
     */
    public static void getProc(){
    	DatabaseMetaData metaDatos;
    	
    	try {
			metaDatos = DBManager.getConn().getMetaData();
			ResultSet rs = metaDatos.getProcedures(DBManager.getDB_NAME(), null, null);
			ResultSetMetaData rsmd = rs.getMetaData();
			
			System.out.println("Procedimientos Almacenados:");
			
			while(rs.next()) {
				for(int i = 0; i < rsmd.getColumnCount()/8; i++){
					System.out.println(rs.getString(3+(i*8)));
				}
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
    }
    
    /**
     * Solicita el numero de columnas de un procedimiento
     * @param nombre nombre del procedimiento
     * @return devuelve el numero de columnas de un procedimiento
     */
    public static int numeroColumnas(String nombre) {
    	ArrayList<String> lista = new ArrayList<String>();
    	DatabaseMetaData metaDatos;
		try {
			metaDatos = DBManager.getConn().getMetaData();
			ResultSet rs = metaDatos.getProcedureColumns(DBManager.getDB_NAME(), null, nombre, null);
			
			while(rs.next()) {
				lista.add(rs.getString("COLUMN_NAME"));
			}
				
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return lista.size();
    }
    
    /**
     * Solicita a la BD un procedimiento almacenado
     * @param nombre nombre del procedimiento
     * @param lista cadenas de texto necesarias para realizar el procedimiento
     */
    public static void procedimientoAlmac(String nombre, ArrayList<String> lista) {
    	String sql = "{call " + nombre + "(";
    	
    	
    	for(int i = 0; i < numeroColumnas(nombre); i++) {
    		sql = sql + "?";
    		if(i!=numeroColumnas(nombre)-1) {
    			sql = sql + ",";
    		}
    	}
    	
    	sql = sql + ")}";
    	
    	try {
    		CallableStatement cStmt = DBManager.getConn().prepareCall(sql);
    		for(int i = 0; i < lista.size(); i++) {
    			cStmt.setString((i+1), lista.get(i));
    		}
    		
    		cStmt.execute();    
    	    final ResultSet rs = cStmt.getResultSet();
			
    	    while (rs.next()) {  
    	          System.out.println(rs.getString(1));
    	    } 
    	    
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
    }
    
    /**
     * Pide a la BD el nombre de las columnas de los procedimientos almacenados
     * @param nombre del procedimiento almacenado
     * @return devuelve un arraylist con los nombres de las columnas
     */
    public static ArrayList<String> infoColumna(String nombre) {
    	ArrayList<String> lista = new ArrayList<String>();
    	DatabaseMetaData metaDatos;
		try {
			metaDatos = DBManager.getConn().getMetaData();
			ResultSet rs = metaDatos.getProcedureColumns(DBManager.getDB_NAME(), null, nombre, null);
			
			while(rs.next()) {
				lista.add(rs.getString("COLUMN_NAME"));
			}
				
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return lista;
    }
}
