
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Scanner;
import base_datos.DBManager;

/**
 *
 * @author Antonio J. Gil
 * created on 10/05
 * @version 7.0
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
    	
    	boolean salir = false;
    	
    	do {
    		System.out.println(" ¿Que quiere hacer?");
    		System.out.println(" 0. Salir.");	//DONE
    		System.out.println(" 1. Abrir una tabla y realizar acciones con ella");	//DONE
    		System.out.println(" 2. Crear una nueva tabla en la BD");	//DONE
    		System.out.println(" 3. Abrir un documento de texto y realizar una acción");	//TODO
    		System.out.println(" 4. Procedimientos Almacenados");	//DONE
    		System.out.println(" 5. Cambiar la BBDD");	//DONE
    		int opcion = pideInt("Elige una opción: ");
    		
    		switch (opcion) {
    		case 0:
    			salir = true;
    			break;
    		case 1:
    			eligeTabla();
    			do {
    	        	salir = menuTabla();
    	        } while (!salir);
    			salir = false;
    			break;
    		case 2:
    			opcionNuevaTabla();
    			break;
    		case 3:
    			do {
    				System.out.println("0. Salir");
    				System.out.println("1. Insertar a partir de fichero.");	//TODO
    				System.out.println("1. Modificar a partir de fichero.");	//TODO
    				System.out.println("3. Borrar a partir de fichero.");	//DONE
    				int opcionFich = pideInt("Elige una opcioón: ");
    				
    				switch (opcionFich) {
    				case 0:
    					salir = true;
    					break;
    				case 1:
    					break;
    				case 2:
    					break;
    				case 3:
    					DBManager.eliminarFichero(pideLinea("Introduce ruta de archivo: "));
    					break;
    				}
    			}while(!salir);
    			salir = false;
    			
    			break;
    		case 4:
    			opcionProcAlmacenados();
    			break;
    		case 5:
    			pideBase();
            	try {
            		DBManager.connect();
            		System.out.println("OK!");
            	} catch (SQLException e) {
            		System.out.println("No se pudo conectar a la BBDD");
            	}
    			break;
    		default:
    			System.out.println("Ha introducido un valor erróneo");
    			break;
    		}
    	} while (!salir);
    	
        DBManager.close();	

    }

    /**
     * Imprime menú y solicita elegir opción
     * @return devuelve false cuando el usuario sale del menú principal
     */
    public static boolean menuTabla() {
        System.out.println("");
        System.out.println("MENU PRINCIPAL");
        System.out.println("0. Salir");
        System.out.println("1. Listar filas");
        System.out.println("2. Nueva fila");
        System.out.println("3. Modificar fila");
        System.out.println("4. Eliminar fila");
        System.out.println("5. Imprimir tabla en un fichero");
        System.out.println("6. Filtrar tabla");	//TODO
        
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
        boolean res = DBManager.deleteFila(id);

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
    
    /**
     * Permite utilizar procedimientos almacenados de la BBDD
     */
    public static void opcionProcAlmacenados() {
    	DBManager.getProc();
    	String nombreProc = pideLinea("Introduce uno de los procedimientos almacenados de arriba: ");
    	ArrayList<String> lista = DBManager.infoColumna(nombreProc);
    	ArrayList<String> resultado = new ArrayList<String>();
    	
    	for(int i = 0; i < lista.size(); i++) {
    		System.out.print("Introduce el siguiente campo: " + lista.get(i));
    		System.out.println();
    		resultado.add(pideLinea("Introduce: "));
    	}
    	
    	DBManager.procedimientoAlmac(nombreProc, resultado);
    }
    
    /**
     * Pide la información necesaria para crear una tabla nueva
     */
    public static void opcionNuevaTabla() {
    	String nombre = pideLinea("Intrdouce el nombre de la tabla: ");
    	ArrayList<String> lista = new ArrayList<String>();
    	
    	lista.add(pideLinea("Introduce nombre de la primary key: "));
    	lista.add(pideLinea("Introduce tipo de dato de la primary key -> String o Int: "));
    	lista.add(pideLinea("Alguna etiqueta mas que añadir como auto_increment: ") + " primary key");
    	String campo;
    	do {
    		campo = pideLinea("Introduce nuevo campo, introduzca 0 para concluir el añadido de campos: ");
    		if(!campo.equals("0")) {
    			lista.add(campo);
    			lista.add(pideLinea("Introduce tipo de dato de " + campo + ": "));
    			lista.add(pideLinea("Alguna etiqueta mas que añadir como not null: "));
    		}
    		
    	} while(!campo.equals("0"));
    	
    	DBManager.crearTabla(nombre, lista);
    }
    
}
