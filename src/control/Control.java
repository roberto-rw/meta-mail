
package control;

import objetosNegocio.*;
import interfaces.*;
import interfazUsuario.ConstantesGUI;
import interfazUsuario.DlgCorreo;
import interfazUsuario.DlgUsuario;
import interfazUsuario.FrmEmail;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import persistencia.PersistenciaListas;
import java.net.URL;
import java.applet.Applet;
import java.applet.AudioClip;
import java.awt.Color;
import java.awt.Component;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import static java.util.Collections.list;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import static javax.swing.JOptionPane.INFORMATION_MESSAGE;
import javax.swing.UIManager;
import javax.swing.plaf.ColorUIResource;

public class Control implements Runnable{
    IPersistencia persistencia;
    Conversiones conversiones;
    FrmEmail frame;
    URL direccionNotificacion;
    AudioClip notificacion;
    int id;
    Usuario myUsr=null;
    
    public Control(FrmEmail frame) {
        this.frame=frame;
        persistencia = new PersistenciaListas();
        conversiones = new Conversiones();
        Thread mihilo = new Thread(this);
        mihilo.start();
        
        direccionNotificacion = getClass().getResource("/recursos/etc/notificación.wav");
        notificacion = Applet.newAudioClip(direccionNotificacion);
        
//        Correo c = new Correo(777);
//        c.setAsunto("relaciones sexuales.");
//        Usuario u = new Usuario("juan","juan@juan.com","27.27.27.27");
//        c.setDestinatario(u);
//        c.setFecha("El 27 del 27");
//        c.setMensaje("Pasame la juca");
//        Usuario us = new Usuario("DEL Pardo","2","2");
//        c.setRemitente(us);
//        persistencia.agregar(c);
//        
//        Correo bookert = new Correo(27);
//        bookert.setRemitente(new Usuario("Bad bunny","itachi@gmail.com","69.420.27.27"));
//        bookert.setAsunto("El disco mas vendido de este puto año");
//        bookert.setFecha("29 de ayer");
//        bookert.setMensaje("ya quedon't");
//        
//        persistencia.agregar(bookert);
//        
//        Correo reces = new Correo(420);
//        reces.setRemitente(new Usuario("Sie","sieToys@gmail.com","27.27.27.27"));
//        reces.setAsunto("Las reces we, las reces");
//        reces.setFecha("ayer");
//        reces.setMensaje("mañana we, mañana");
//        persistencia.agregar(reces);
        
        
    }
    
    public boolean enviarCorreo(JFrame frame, String nomUsuario,String ipUsuario,String correoUsuario, int op, String ipDest) throws Exception{
        Usuario remitente = new Usuario(nomUsuario,correoUsuario,ipUsuario);
        Correo correo = new Correo();
        StringBuffer respuesta = new StringBuffer("");
        int operacion = -1;
        Usuario dest = null;

        if(op == 0){
            operacion = ConstantesGUI.AGREGAR;
        }
        else if(op == 1){
            operacion = ConstantesGUI.ACTUALIZAR;
        }
        
        if(ipDest != null){
            dest = new Usuario(ipDest);
        
            try{
                dest = persistencia.obten(dest);
            }
            catch(Exception e){
                JOptionPane.showMessageDialog(frame, e.getMessage(), "Error!!.", JOptionPane.ERROR_MESSAGE);       
                return false;
            }
        }
        
        correo.setRemitente(remitente); 
        correo.setDestinatario(dest);

        DlgCorreo dlgCorreo = new DlgCorreo(frame, "Enviar Correo", true, correo, operacion, respuesta);
        
        if (respuesta.substring(0).equals(ConstantesGUI.CANCELAR)) return false;
     
        try{
            String ip=correo.getDestinatario().getIp();
            Socket misocket = new Socket(ip,8080);
            ObjectOutputStream paqueteCorreo = new ObjectOutputStream(misocket.getOutputStream());
            paqueteCorreo.writeObject(correo);
            misocket.close();
        }catch(Exception e){
            e.printStackTrace();         
            System.out.println("Error al enviar el correo  " + e.getMessage());
        }
        return true;
    }
    
    public boolean eliminarCorreo(String idD){
        Correo correo;
        int uId = Integer.parseInt(idD);        
        correo = new Correo(uId);
        
        try{
            correo = persistencia.obten(correo);
        }
        catch(Exception e){
            JOptionPane.showMessageDialog(frame, e.getMessage(), "Error!!.", JOptionPane.ERROR_MESSAGE);       
            return false;
        }
        
        try{
            persistencia.eliminar(correo);
        }
        catch(Exception e){
            JOptionPane.showMessageDialog(frame, e.getMessage(), "Error!!.", JOptionPane.ERROR_MESSAGE);       
            return false;
        }
        
        return true;
    }
    
    public boolean guardarCorreo(String idD){
        Correo correo;
        int uId = Integer.parseInt(idD);        
        correo = new Correo(uId);
        
        try{
            correo = persistencia.obten(correo);
        }
        catch(Exception e){
            JOptionPane.showMessageDialog(frame, e.getMessage(), "Error!!.", JOptionPane.ERROR_MESSAGE);       
            return false;
        }
        
        if(correo==null)return false;
        
        JFileChooser file = new JFileChooser();
        file.addChoosableFileFilter(new FiltroTexto());
        
        int respuesta = file.showOpenDialog(file);
        if(respuesta == JFileChooser.APPROVE_OPTION){
            File archivo= file.getSelectedFile();
            archivo.setWritable(true);
            try{
               FileWriter f = new FileWriter(archivo); 
               f.write(correo.toString());
               f.close();
            }
            catch(IOException e){
                JOptionPane.showMessageDialog(frame,"Error al guardar","error", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        }
        return true;
    }
    
    public boolean abrirCorreo(String idD){
        Correo correo;
        int uId = Integer.parseInt(idD);        
        correo = new Correo(uId);
        StringBuffer respuesta = new StringBuffer("");
        
        try{
            correo = persistencia.obten(correo);
        }
        catch(Exception e){
            JOptionPane.showMessageDialog(frame, e.getMessage(), "Error!!.", JOptionPane.ERROR_MESSAGE);       
            return false;
        }
        
        DlgCorreo dlgCorreo = new DlgCorreo(frame, "Abrir Correo", true, correo, ConstantesGUI.DESPLEGAR, respuesta);
        
        if(correo == null)return false;
        
        if(respuesta.substring(0).equals(ConstantesGUI.AÑADIR_C)){
            Usuario u;
            u = correo.getRemitente(); 
            
            añadirUsuario(u);
        }
        
        return true;
    }
    
    public boolean añadirUsuario(Usuario usuario){
        Usuario usr;
        
        try{
            usr = persistencia.obten(usuario);
        }
        catch(Exception e){
            JOptionPane.showMessageDialog(frame, e.getMessage(), "Error!!.", JOptionPane.ERROR_MESSAGE);       
            return false;
        }
        
        if(usr != null){
            JOptionPane.showMessageDialog(frame, "El usuario ya se encuentra en contactos", "Error!!.", JOptionPane.ERROR_MESSAGE);       
            return false;
        }
        
        try{
            persistencia.agregar(usuario);
        }
        catch(Exception e){
            JOptionPane.showMessageDialog(frame, e.getMessage(), "Error!!.", JOptionPane.ERROR_MESSAGE);       
            return false;
        }
        
        return true;
    }
    
    public boolean eliminarUsuario(String uIP){
        Usuario usuario;  
        usuario = new Usuario(uIP);
        
        try{
            usuario = persistencia.obten(usuario);
        }
        catch(Exception e){
            JOptionPane.showMessageDialog(frame, e.getMessage(), "Error!!.", JOptionPane.ERROR_MESSAGE);       
            return false;
        }
        
        try{
            persistencia.eliminar(usuario);
        }
        catch(Exception e){
            JOptionPane.showMessageDialog(frame, e.getMessage(), "Error!!.", JOptionPane.ERROR_MESSAGE);       
            return false;
        }
        
        return true;
    }
    
    public boolean abrirUsuario(String nUip){
        StringBuffer respuesta = new StringBuffer("");
        Usuario usuario;  
        usuario = new Usuario(nUip);
        
        try{
            usuario = persistencia.obten(usuario);
        }
        catch(Exception e){
            JOptionPane.showMessageDialog(frame, e.getMessage(), "Error!!.", JOptionPane.ERROR_MESSAGE);       
            return false;
        }
        
        DlgUsuario dlg = new DlgUsuario(frame, "Abrir Correo", true, usuario, ConstantesGUI.DESPLEGAR, respuesta);
        
        if(respuesta.substring(0).equals(ConstantesGUI.AÑADIR_C)){
            try {
                String nombre = getMyUsr().getNombre() ;
                String ip = getMyUsr().getIp();
                String correo = getMyUsr().getDireccionCorreo();
                
                enviarCorreo(null, nombre, ip, correo, 1,usuario.getIp());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, ex.getMessage(), "Error!!.", JOptionPane.ERROR_MESSAGE);       
                return false;
            }
        }
        
        return true;
    }
    
    public boolean añadirUsuarioBinario(Usuario usuario){
        persistencia.agregar(usuario);
        return true;
    }
    
     public boolean añadirCorreoBinario(Correo correo){
        persistencia.agregar(correo);
        return true;
    }
    
    public void setMyUsr(Usuario usr){
        this.myUsr = usr;
    } 
    
    public Usuario getMyUsr(){
        return this.myUsr;
    } 
    
    public void setID(int id){
        this.id = id;
    }
    
    public int getIDCorreo(){
        return this.id;
    }
    
    public List<Usuario> listaUsers(){
        List<Usuario> listaUsuarios;
        try {
            listaUsuarios = persistencia.consultarUsuarios();
            System.out.println("Obtuvo la lista de usuarios");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame, e.getMessage(), "Error!!.", JOptionPane.ERROR_MESSAGE);
            System.out.println("Retornó null");
            return null;
        }
        return listaUsuarios;
    }
    
    public List<Correo> listaCorreos(){
        List<Correo> listaCorreos;
        try {
            listaCorreos = persistencia.consultarCorreos();
            System.out.println("Obtuvo la lista de usuarios");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame, e.getMessage(), "Error!!.", JOptionPane.ERROR_MESSAGE);
            System.out.println("Retornó null");
            return null;
        }
        return listaCorreos;
    }
    
    /**
     * Regresa un objeto Tabla con todos los correos
     *
     * @param frame Ventana sobre la que se despliega el mensaje de error
     * @return Objeto Tabla con todos los correos, null si hay un error
     */
    public Tabla getTablaCorreos() {
        List<Correo> listaCorreos;
        try {
            listaCorreos = persistencia.consultarCorreos();
            System.out.println("Obtuvo la lista de correos");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame, e.getMessage(), "Error!!.", JOptionPane.ERROR_MESSAGE);
            System.out.println("Retornó null");
            return null;
        }
        System.out.println("Regresó la tabla de correos");
        return new Tabla("Lista de Correos", conversiones.correosRecibidosTableModel(listaCorreos));
    }
    
    public Tabla getTablaCorreosUsuario(String usr) {
        List<Correo> listaCorreos;
        try {
            listaCorreos = persistencia.consultarCorreosUsuario(usr);
            System.out.println("Obtuvo la lista de correos");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame, e.getMessage(), "Error!!.", JOptionPane.ERROR_MESSAGE);
            System.out.println("Retornó null");
            return null;
        }
        System.out.println("Regresó la tabla de correos");
        return new Tabla("Lista de Correos", conversiones.correosRecibidosTableModel(listaCorreos));
    }
    
    public Tabla getTablaUsuarios() {
        List<Usuario> listaUsuarios;
        try {
            listaUsuarios = persistencia.consultarUsuarios();
            System.out.println("Obtuvo la lista de usuarios");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame, e.getMessage(), "Error!!.", JOptionPane.ERROR_MESSAGE);
            System.out.println("Retornó null");
            return null;
        }
        System.out.println("Regresó la tabla de Usuarios");
        return new Tabla("Lista de Usuarios", conversiones.usuariosTableModel(listaUsuarios));
    }

    @Override
    public void run() {
        try {
            System.out.println("Estoy a la escucha ");
            ServerSocket servidor = new ServerSocket(8080);
            Socket misocket;
            Correo correo;
            

            while (true) {
                misocket = servidor.accept();
                ObjectInputStream flujoEntrada = new ObjectInputStream(misocket.getInputStream());
                correo = (Correo)flujoEntrada.readObject();
                
                correo.setIdCorreo(id);
                id++;
                
                Calendar c1 = GregorianCalendar.getInstance();
                correo.setFecha(c1.getTime().toLocaleString());
                
                notificacion.play();
                persistencia.agregar(correo);

                Tabla tabla = getTablaCorreos();
                frame.despliegaTablaCorreos(tabla);
                
                JOptionPane.showMessageDialog(null, "Nuevo mensaje de: \n\n\n" + correo.getRemitente().getNombre(), "Nuevo mensaje", -1);
                
                System.out.println("Demonios, se agregó");
                misocket.close();
            }
        } 
        catch (Exception ex) {
            System.out.println("OOO" + ex.getMessage());
        }
    }
}