package gestion;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Scanner;

import base_datos.DBManager;

/**
*
* @author Antonio J. Gil
* created on 10/05
* @version 9.0
*/
public class Fichero {

	/**
     * Imprime en un fichero lista de clientes.
     * @param ruta cadena de texto con la ruta del fichero a generar
     * @return devuelve true si se recogieron los clientes en un fichero satisfactoriamente
     */
    public static boolean printClientesFichero(String ruta) {
    	
    	try {
    		File fichero = new File(ruta);
    		fichero.createNewFile();
    		ResultSet rs = Tabla.getTabla(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
            ResultSetMetaData rsmd = rs.getMetaData();
            FileWriter writer = new FileWriter(fichero);
            
            writer.write("DB Name: " + DBManager.getDB_NAME() + "   -   Table Name: " + DBManager.getDB_TABLE() + "\n");
			
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
			
			DBManager.setDB_NAME(input.nextLine());
			DBManager.setDB_TABLE(input.nextLine());
			pk = input.nextLine();
			
			while(pk.length()!=0) {
				Cliente.deleteFila(Integer.parseInt(pk.substring(0, pk.indexOf(","))));
				pk = pk.substring(pk.indexOf(",")+1);
			}
			
			input.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (StringIndexOutOfBoundsException e) {
			Cliente.deleteFila(Integer.parseInt(pk.substring(0)));
		}
    	
    }
    
    /**
     * Añade fila/s a una tabla de una BD a través de un fichero
     * @param ruta la ruta del fichero
     */
    public static void addFilaFichero(String ruta) {
    	File archivo = new File(ruta);
    	ArrayList<String> camposTB = new ArrayList<String>();
    	
    	try {
			Scanner input = new Scanner(archivo);
			
			DBManager.setDB_NAME(input.nextLine());
			DBManager.setDB_TABLE(input.nextLine());
			String campos = input.nextLine();
			
			//Se guardan los nombres de los campos en camposTB
			while(campos.length()!=0) {
				try {
					camposTB.add(campos.substring(0, campos.indexOf(",")));
					campos = campos.substring(campos.indexOf(",")+1);
				} catch (StringIndexOutOfBoundsException e) {
					camposTB.add(campos.substring(0));
					campos = "";DBManager.setDB_NAME(input.nextLine());
					DBManager.setDB_TABLE(input.nextLine());
				}
			}
			
			ResultSet rs = Tabla.getTabla(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
			
			try {
				while(input.hasNext()) {
					String texto = input.nextLine();
					
					while(texto.length()!=0) {
						
						rs.moveToInsertRow();
						
						try {
							for(int i = 0; i < camposTB.size(); i++) {
				            	rs.updateString(camposTB.get(i), texto.substring(0, texto.indexOf(",")));
				            	
				            	texto = texto.substring(texto.indexOf(",")+1);
				            }
							
						} catch (StringIndexOutOfBoundsException e) {
							rs.updateString(camposTB.get(camposTB.size()-1), texto.substring(0));
							texto = "";
						}
						
						rs.insertRow();
						
					}
				}
				rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
			input.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
    }
    
    /**
     * Modificar tabla a partir de fichero
     * @param ruta Ruta del fichero a consultar
     */
    public static void modFilaFichero(String ruta) {
    	File archivo = new File(ruta);
    	ArrayList<String> camposMD = new ArrayList<String>();
    	int pk = 1;
    	boolean fin = false;
    	
    	try {
			Scanner input = new Scanner(archivo);
			
			DBManager.setDB_NAME(input.nextLine());
			DBManager.setDB_TABLE(input.nextLine());
			String campos = input.nextLine();
			String sql = "update " + DBManager.getDB_TABLE() + " set ";
			
			//Recogemos los campos de la tabla para completar el sql y guardamos en pk la posicion de la primary key
			while(campos.length()!=0) {
				try {
					String campo = campos.substring(0, campos.indexOf(","));
					campos = campos.substring(campos.indexOf(",")+1);
					if(!Cliente.getPK().equals(campo)) {
						sql += campo + " = ?, ";
						fin = true;
					} else if (!Cliente.getPK().equals(campo) && !fin) {
						pk++;
					}
					
				} catch (StringIndexOutOfBoundsException e) {
					sql += campos + " = ? WHERE " + Cliente.getPK() + " = ?";
					campos = "";
				}
			}
			
			//Recogemos cada lina de modificacion
			while(input.hasNext()) {
				String texto = input.nextLine();
				String key = "";
				int contador = 1;
				//Guardamos cada modificacion en el arraylist camposMD
				while(texto.length()!=0) {
					try {
						if(contador==pk) {
				            key = texto.substring(0, texto.indexOf(","));
				            camposMD.add(key);
				        } else {
				            camposMD.add(texto.substring(0, texto.indexOf(",")));
				        }
				        contador++;
				        texto = texto.substring(texto.indexOf(",")+1);
					} catch (StringIndexOutOfBoundsException e) {
						camposMD.add(texto.substring(0));
						texto = "";
					}
				}
					
				//Realizamos la actualización
				updateTablaFichero(sql,key,camposMD);
					
			}
			
			input.close();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
    }
    
    /**
     * Actualiza la tabla con los datos del fichero
     * @param sql SQL necesario
     * @param key PrimaryKey
     * @param camposMD Texto de los campos
     */
    private static void updateTablaFichero(String sql, String key, ArrayList<String> camposMD) {
    	int contador = 1;
		String txtKey = "";
		
		try {
			PreparedStatement stmt = DBManager.getConn().prepareStatement(sql);
			
			while(camposMD.size()!=0) {
				if(camposMD.get(0).equals(key)) {
					txtKey = key;
					camposMD.remove(0);
				} else {
					stmt.setString(contador, camposMD.get(0));
					camposMD.remove(0);
					contador++;
				}
			}
			
			stmt.setString(contador, txtKey);
			
			stmt.executeUpdate();
			
			System.out.println("Actualizado con éxito... " + key);
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
    }
}
