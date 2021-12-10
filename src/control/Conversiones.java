
package control;

import objetosNegocio.*;
import java.util.List;
import javax.swing.table.DefaultTableModel;

public class Conversiones {
    
    String nombresColumnasTablaCorreosRecibidos[] = {"ID Correo","Nombre del remitente", "Asunto", "Mensaje", "Fecha"};
    String nombresColumnasTablasUsuarios[] = {"Nombre","IP"}; 
    
    public DefaultTableModel correosRecibidosTableModel(List<Correo> listaCorreos) {
        Object tabla[][];
        if (listaCorreos != null) {
            tabla = new Object[listaCorreos.size()][5];
            for (int i = 0; i < listaCorreos.size(); i++) {
                Correo correo = listaCorreos.get(i);
                tabla[i][0] = correo.getIdCorreo();
                tabla[i][1] = correo.getRemitente().getNombre();
                tabla[i][2] = correo.getAsunto();
                tabla[i][3] = correo.getMensaje();
                tabla[i][4] = correo.getFecha();

            }
            return new DefaultTableModel(tabla, nombresColumnasTablaCorreosRecibidos);
        }
        return null;
    }
    
    public DefaultTableModel usuariosTableModel(List<Usuario> listaUsuarios){
        Object tabla[][];
        
        if (listaUsuarios != null) {     
            tabla = new Object[listaUsuarios.size()][2];
            for (int i = 0; i < listaUsuarios.size(); i++) {
                Usuario usuario = listaUsuarios.get(i);        
                tabla[i][0] = usuario.getNombre();         
                tabla[i][1] = usuario.getIp(); 
            }  
            return new DefaultTableModel(tabla, nombresColumnasTablasUsuarios);
        }
        return null;
    }
    
}
