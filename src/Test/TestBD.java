package Test;

import static org.junit.Assert.*;

import java.sql.SQLException;

import org.junit.Test;

import base_datos.DBManager;
import gestion.Tabla;

public class TestBD {

	@Test
	public void testNumColumnas() {
		int num = 0, esperado = 3;
		
		DBManager.cambioBD("tienda");
		Tabla.setTabla("clientes");
		
		try {
			DBManager.connect();
			System.out.println("OK");
			num = Tabla.numeroColumnas();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		assertEquals(num,esperado);
	}

}
