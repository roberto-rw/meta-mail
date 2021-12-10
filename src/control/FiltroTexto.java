/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package control;

import java.io.File;

/**
 * Clase de filtro de texto
 * @author PC
 */
public class FiltroTexto extends javax.swing.filechooser.FileFilter{

    /**
     * Selecciona el tipo de archivo con terminacion .txt
     * @param file Archivo a aceptar
     * @return true si lo acepta, false si no
     */
    @Override
    public boolean accept(File file) {
        return file.isDirectory() || file.getAbsolutePath().endsWith(".txt");
    }

    /**
     * Descripcion del tipo de archivo filtrado
     * @return La descripcion del filtro
     */
    @Override
    public String getDescription() {
        return "Documento de texto (*.txt)";
    }
    
}
