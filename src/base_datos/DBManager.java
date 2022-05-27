package base_datos;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Scanner;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;

/**
 *
 * @author Antonio J. Gil
 * created on 10/0
 * @version 8.0
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
    public static void cambioBaseNueva(String base) {
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
    
    /**
     * Lista las tablas disponibles de la BD
     */
    public static void listarTablas() {
    	try {
			DatabaseMetaData metaDatos = conn.getMetaData();
			
			ResultSet rs = metaDatos.getTables(DB_NAME, null, "%", null);
			
			while (rs.next()) {
				   String tabla = rs.getString(3);
				   System.out.println(" > " + tabla);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
    }
    
    //////////////////////////////////////////////////
    // MÉTODOS DE PROCEDIMIENTOS ALMACENADOS
    //////////////////////////////////////////////////
    
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
    		  stmt = conn.prepareStatement(sql);
    		  
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
			metaDatos = conn.getMetaData();
			ResultSet rs = metaDatos.getProcedures(DB_NAME, null, null);
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
			metaDatos = conn.getMetaData();
			ResultSet rs = metaDatos.getProcedureColumns(DB_NAME, null, nombre, null);
			
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
    		CallableStatement cStmt = conn.prepareCall(sql);
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
			metaDatos = conn.getMetaData();
			ResultSet rs = metaDatos.getProcedureColumns(DB_NAME, null, nombre, null);
			
			while(rs.next()) {
				lista.add(rs.getString("COLUMN_NAME"));
			}
				
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return lista;
    }

    //////////////////////////////////////////////////
    // MÉTODOS DE TABLA 
    //////////////////////////////////////////////////
    
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
        	
            String sql = "SELECT * FROM " + DB_TABLE + " WHERE " + columna + "= ?;";
            PreparedStatement stmt = conn.prepareStatement(sql,ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
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
    	DB_TABLE = tabla;
    }
    
    /**
     * Obtiene toda la tabla clientes de la base de datos
     * @param resultSetType Tipo de ResultSet
     * @param resultSetConcurrency Concurrencia del ResultSet
     * @return ResultSet (del tipo indicado) con la tabla, null en caso de error
     */
    private static ResultSet getTabla(int resultSetType, int resultSetConcurrency) {
    	String sql = "select * from " + DB_TABLE + ";";
        try {
            PreparedStatement stmt = conn.prepareStatement(sql, resultSetType, resultSetConcurrency);
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

    //////////////////////////////////////////////////
    // MÉTODOS DE UN SOLO CLIENTE
    //////////////////////////////////////////////////
    
    /**
     * Solicita a la BD el cliente con id indicado
     * @param id id del cliente
     * @return ResultSet con el resultado de la consulta, null en caso de error
     */
    public static ResultSet getCliente(int id) {
        try {
        	
            // Realizamos la consulta SQL
            String sql = "SELECT * FROM " + DB_TABLE + " WHERE " + getPK() + "= ?;";
            PreparedStatement stmt = conn.prepareStatement(sql,ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
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
    		DatabaseMetaData metaDatos = conn.getMetaData();
    		ResultSet rs = metaDatos.getPrimaryKeys(DB_NAME, null, DB_TABLE);
    	
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
    	
    	ResultSet rs = getTabla(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
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
            ResultSet rs = getTabla(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);

            // Insertamos el nuevo registro
            rs.moveToInsertRow();
            
            for(int i = 1; i <= numeroColumnas(); i++) {
            	rs.updateString(nombreColumna(i), lista.get(i-1));
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
    
    //////////////////////////////////////////////////
    // FICHEROS
    //////////////////////////////////////////////////
    
    /**
     * Imprime en un fichero lista de clientes.
     * @param ruta cadena de texto con la ruta del fichero a generar
     * @return devuelve true si se recogieron los clientes en un fichero satisfactoriamente
     */
    public static boolean printClientesFichero(String ruta) {
    	
    	try {
    		File fichero = new File(ruta);
    		fichero.createNewFile();
    		ResultSet rs = getTabla(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
            ResultSetMetaData rsmd = rs.getMetaData();
            FileWriter writer = new FileWriter(fichero);
            
            writer.write("DB Name: " + DB_NAME + "   -   Table Name: " + DB_TABLE + "\n");
			
			for(int i = 1; i <= rsmd.getColumnCount(); i++) {
				writer.write(rsmd.getColumnName(i) + "\t");
			}
			
			writer.write("\n");
            
            while (rs.next()) {
            	
            	for(int i = 1; i <= rsmd.getColumnCount(); i++) {
            		if(rsmd.getColumnTypeName(i).equals("VARCHAR")) {
                		String texto = rs.getString(rsmd.getColumnName(i));
                		writer.write(texto + "\t");
                	}
                	
                	if(rsmd.getColumnTypeName(i).equals("INT")) {
                		int entero = rs.getInt(rsmd.getColumnName(i));
                		writer.write(entero + "\t");
                	}
                }
            	writer.write("\n");
            }
			
			writer.close();
			
			return true;
		} catch (IOException e) {
			System.out.println("[" + e + "] - No se puedo crear el fichero");
			return false;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		} catch (NullPointerException ex) {
        	System.out.println("[" + ex + "]");
        	return false;
        }
            	   	
    }
    
    /**
     * Elimina fila/s de una BD a través de un fichero
     * @param ruta la ruta del fichero
     */
    public static void eliminarFichero(String ruta) {
    	File archivo = new File(ruta);
    	String pk = "";
    	try {
			Scanner input = new Scanner(archivo);
			
			DB_NAME = input.nextLine();
			DB_TABLE = input.nextLine();
			pk = input.nextLine();
			
			while(pk.length()!=0) {
				deleteFila(Integer.parseInt(pk.substring(0, pk.indexOf(","))));
				pk = pk.substring(pk.indexOf(",")+1);
			}
			
			input.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (StringIndexOutOfBoundsException e) {
			deleteFila(Integer.parseInt(pk.substring(0)));
		}
    	
    	
    }
    
}
