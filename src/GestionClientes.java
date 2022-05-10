
import java.util.Scanner;
import base_datos.DBManager;

/**
 *
 * @author Antonio J. Gil
 * created on 10/05
 * @version 2.0
 */
public class GestionClientes {

	/**
	 * Main
	 * @param args Argumentos
	 */
    public static void main(String[] args) {

        DBManager.connect();

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
        System.out.println("1. Listar clientes");
        System.out.println("2. Nuevo cliente");
        System.out.println("3. Modificar cliente");
        System.out.println("4. Eliminar cliente");
        System.out.println("5. Imprimir tabla en un fichero");
        System.out.println("6. Salir");
        
            
        int opcion = pideInt("Elige una opción: ");
        
        switch (opcion) {
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
                return true;
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
     * Imprime listado de clientes
     */
    public static void opcionMostrarClientes() {
        System.out.println("Listado de Clientes:");
        DBManager.printTablaClientes();
    }
    
    /**
     * Introducir nuevo cliente a la BBDD
     */
    public static void opcionNuevoCliente() {

        System.out.println("Introduce los datos del nuevo cliente:");
        String nombre = pideLinea("Nombre: ");
        String direccion = pideLinea("Dirección: ");

        boolean res = DBManager.insertCliente(nombre, direccion);

        if (res) {
            System.out.println("Cliente registrado correctamente");
        } else {
            System.out.println("Error :(");
        }
    }

    /**
     * Modifica los datos de un cliente de una BBDD
     */
    public static void opcionModificarCliente() {

        int id = pideInt("Indica el id del cliente a modificar: ");

        // Comprobamos si existe el cliente
        if (!DBManager.existsCliente(id)) {
            System.out.println("El cliente " + id + " no existe.");
            return;
        }

        // Mostramos datos del cliente a modificar
        DBManager.printCliente(id);

        // Solicitamos los nuevos datos
        String nombre = pideLinea("Nuevo nombre: ");
        String direccion = pideLinea("Nueva dirección: ");

        // Registramos los cambios
        boolean res = DBManager.updateCliente(id, nombre, direccion);

        if (res) {
            System.out.println("Cliente modificado correctamente");
        } else {
            System.out.println("Error :(");
        }
    }

    /**
     * Eliminar un cliente de la BBDD
     */
    public static void opcionEliminarCliente() {

        int id = pideInt("Indica el id del cliente a eliminar: ");

        // Comprobamos si existe el cliente
        if (!DBManager.existsCliente(id)) {
            System.out.println("El cliente " + id + " no existe.");
            return;
        }

        // Eliminamos el cliente
        boolean res = DBManager.deleteCliente(id);

        if (res) {
            System.out.println("Cliente eliminado correctamente");
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
