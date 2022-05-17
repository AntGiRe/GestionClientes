
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Scanner;
import base_datos.DBManager;

/**
 *
 * @author Antonio J. Gil
 * created on 10/05
 * @version 3.0
 */
public class GestionClientes {

	/**
	 * Main
	 * @param args Argumentos
	 */
    public static void main(String[] args) {
    	boolean baseCorrecta = false;
    	
    	do {
    		pideBase();
    		
    		try {
    			baseCorrecta = DBManager.connect();
    		} catch (SQLException e) {
    			System.out.println(" [" + e + "] - No se pudo conectar con la BBDD, intentelo de nuevo.\n");
    		}
    		
    	}while(!baseCorrecta);
    	
    	System.out.println("OK!");	
    	
    	eligeTabla();
    		
    	boolean salir = false;
        do {
        	salir = menuPrincipal();
        } while (!salir);
            
        DBManager.close();	

    }

    /**
     * Imprime menú y solicita elegir opción
     * @return devuelve false cuando el usuario sale del menú principal
     */
    public static boolean menuPrincipal() {
        System.out.println("");
        System.out.println("MENU PRINCIPAL");
        System.out.println("0. Salir");	//DONE
        System.out.println("1. Listar filas");	//DONE
        System.out.println("2. Nueva fila");	//DONE
        System.out.println("3. Modificar fila"); //DONE
        System.out.println("4. Eliminar fila");	//DONE
        System.out.println("5. Imprimir tabla en un fichero");	//DONE
        System.out.println("6. Cambiar la BBDD");	//DONE
        
        int opcion = pideInt("Elige una opción: ");
        
        switch (opcion) {
        	case 0:
        		return true;
            case 1:
                opcionMostrarClientes();
                return false;
            case 2:
                opcionNuevoCliente();
                return false;
            case 3:
                opcionModificarCliente();
                return false;
            case 4:
                opcionEliminarCliente();
                return false;
            case 5:
            	opcionImprimirFichero();
            	return false;
            case 6:
            	pideBase();
            	eligeTabla();
            	try {
            		DBManager.connect();
            		System.out.println("OK!");
            	} catch (SQLException e) {
            		System.out.println("No se pudo conectar a la BBDD");
            	}
            	return false;
            default:
                System.out.println("Opción elegida incorrecta");
                return false;
        }
        
    }
    
    /**
     * Solicita un entero
     * @param mensaje Cadena de texto que pide un valor determinado al usuario
     * @return devuelve un entero
     */
    public static int pideInt(String mensaje){
    	Scanner in = new Scanner(System.in);
    	
        while(true) {
            try {
                System.out.print(mensaje);
                int valor = in.nextInt();
                return valor;
            } catch (Exception e) {
            	in.nextLine();
                System.out.println("No has introducido un número entero. Vuelve a intentarlo.");
            }
        }
        
    }
    
    /**
     * Solicita una cadena de texto
     * @param mensaje Cadena de texto que pide un valor determinado al usuario
     * @return devuelve una cadena de texto
     */
    public static String pideLinea(String mensaje){
    	Scanner in = new Scanner(System.in);
        while(true) {
            try {
                System.out.print(mensaje);
                String linea = in.nextLine();
                return linea;
            } catch (Exception e) {
                System.out.println("No has introducido una cadena de texto. Vuelve a intentarlo.");
            }
        }
    }
    
    /**
     * Solicita nueva BBDD, tabla y campos a las que se acceden
     */
    public static void pideBase(){
    	
    	String base = pideLinea("Introduce el nombre de la base de datos a la que acceder: ");
    	
    	DBManager.cambioBaseNueva(base);
    	
    }
    
    /**
     * Permite elegir una tabla de la BD
     */
    public static void eligeTabla(){
    	
    	System.out.println(" ** TABLAS **");
    	DBManager.listarTablas();
    	DBManager.setTabla(pideLinea("Elige una tabla: "));
    	
    }
    
    

    /**
     * Imprime listado de clientes
     */
    public static void opcionMostrarClientes() {
        System.out.println("Listado de tabla:");
        DBManager.printTablaClientes();
    }
    
    /**
     * Introducir nuevo cliente a la BBDD
     */
    public static void opcionNuevoCliente() {
    	ArrayList<String> lista = new ArrayList<String>();
    	
    	for(int i = 1; i <= DBManager.numeroColumnas(); i++) {
    		if(DBManager.nombreColumna(i).equals(DBManager.getPK())) {
    			lista.add(null);
    		} else {
    			lista.add(pideLinea("Introduce el " + DBManager.nombreColumna(i) + ": "));
    		}
    		
    	}

        boolean res = DBManager.insertCliente(lista);

        if (res) {
            System.out.println("Fila registrada correctamente");
        } else {
            System.out.println("Error :(");
        }
    }

    /**
     * Modifica los datos de un cliente de una BBDD
     */
    public static void opcionModificarCliente() {

        int id = pideInt("Indica el id de la fila a modificar: ");

        // Comprobamos si existe el cliente
        if (!DBManager.existsCliente(id)) {
            System.out.println("El cliente " + id + " no existe.");
            return;
        }

        // Mostramos datos del cliente a modificar
        DBManager.printCliente(id);

        // Solicitamos los nuevos datos
        String aModificar = pideLinea("Que campo quieres modificar: ");
        String nuevo = pideLinea("Nueva entrada de " + aModificar + ": ");

        boolean res = DBManager.updateCliente(id,aModificar, nuevo);

        if (res) {
            System.out.println("Fila modificada correctamente");
        } else {
            System.out.println("Error :(");
        }
    }

    /**
     * Eliminar un cliente de la BBDD
     */
    public static void opcionEliminarCliente() {

        int id = pideInt("Indica el id de la fila a eliminar: ");

        // Comprobamos si existe el cliente
        if (!DBManager.existsCliente(id)) {
            System.out.println("La fila " + id + " no existe.");
            return;
        }

        // Eliminamos el cliente
        boolean res = DBManager.deleteCliente(id);

        if (res) {
            System.out.println("Fila eliminada correctamente");
        } else {
            System.out.println("Error :(");
        }
    }
    
    /**
     * Solicita ruta donde generar fichero con clientes
     */
    public static void opcionImprimirFichero() {

        String ruta = pideLinea("Indica la ruta o nombre (si quiere que se genere en la carpeta de proyecto) del fichero: ");

        // Comprobamos si existe el cliente
        if (DBManager.printClientesFichero(ruta)) {
            System.out.println("El fichero se ha generado correctamente");
            return;
        } else {
        	System.out.println("El fichero no se ha podido generar, revise lo introducido.");
        	return;
        }
    }
    
    
}
