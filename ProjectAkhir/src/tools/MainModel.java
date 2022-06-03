/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tools;

import View.ViewAnalysis;
import View.ViewLogin;
import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.*;
import javax.swing.JOptionPane;
import View.ViewUpload;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author muhammad fajar andikha
 */
public class MainModel {
    Update update;
    String file;
    
    public Connection koneksi;
    public Statement statement;
    
    private String username;
    private String password;
    static private String ID;

    static final String DBurl = "jdbc:mysql://localhost/projectakhir";
    static final String DBusername = "root";
    static final String DBpassword = "";
    private String filepath = "";
    
    int batchSize = 20;
    
    
    public MainModel(String file) {
        this.file = file;
    }

    public MainModel() {
        
    }
    
    public void setUpdate(Update up){
        this.update = up;
    }
    
    public String getUsername() {
        return this.username;
    }

    public String getPassword() {
        return this.password;
    }

    public String getId() {
        return ID;
    }

    public void setId(String id) {
        this.ID = id;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    
    public void clearUsername(String username) {
        this.username = username;
        update.clearUser(this);
    }

    public void clearPassword(String password) {
        this.password = password;
        update.clearPass(this);
    }
    
    public void clearUser(){
        clearUsername("");
    }
    
    public void clearPass(){
        clearPassword("");
    }
    
    public void setPath(){
        try {
            JFileChooser chooser = new JFileChooser();
            chooser.showOpenDialog(null);
            File f = chooser.getSelectedFile();        
            this.filepath = f.getAbsolutePath();
            inputData();
        }catch(Exception ex){
            JOptionPane.showMessageDialog(null,  "Cancel Select", "Message", JOptionPane.WARNING_MESSAGE);
        }
        
    }

    public void Connector() {
        try{
            Class.forName("com.mysql.jdbc.Driver");
            koneksi = (Connection) DriverManager.getConnection(DBurl,DBusername,DBpassword);
            System.out.println("Koneksi Berhasil");
        }catch(Exception ex){
            System.out.println("Koneksi gagal");
        }
    }
    
    public void loginData(Controller cl,ViewLogin lg){
        if(this.username.isEmpty() || this.password.isEmpty()){
            JOptionPane.showMessageDialog(null,  "Anda belum memasukkan Input!", "Empty Field", JOptionPane.WARNING_MESSAGE);
        } else if(this.username.isBlank() || this.password.isBlank()){
            JOptionPane.showMessageDialog(null,  "Input tidak boleh blank (Space)!", "Blank Field", JOptionPane.WARNING_MESSAGE);
        } else {
            try {
                String pass="";
                String user="";
                String query = "SELECT * FROM `users` WHERE `username` = '"+this.username+"'";
                Connector();
                statement = koneksi.createStatement();
                ResultSet resultSet  = statement.executeQuery(query);
                while (resultSet.next()){
                    user = resultSet.getString("username");
                    pass = resultSet.getString("password");
                    String id = resultSet.getString("id");
                    setId(id);
                }

                if (!user.equals("")){
                    if(this.password.equals(pass)){
                        JOptionPane.showMessageDialog(null, "Login Successful");
                        lg.setVisible(false);
                        ViewUpload h = new ViewUpload();
                        h.setVisible(true);
                        h.pack();
                        h.setLocationRelativeTo(null);
                        h.setDefaultCloseOperation(ViewUpload.EXIT_ON_CLOSE);
                    } else  {
                        JOptionPane.showMessageDialog(null,  "Password Salah!", "Warning", JOptionPane.WARNING_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(null,  "Username Tidak Ditemukan", "Warning", JOptionPane.WARNING_MESSAGE);
                }
            } catch (SQLException ex){
                JOptionPane.showMessageDialog(null,ex,"Warning!", JOptionPane.WARNING_MESSAGE);

            } catch (NullPointerException ex){
                JOptionPane.showMessageDialog(null,  "Input yang dimasukkan salah!", "Input Error", JOptionPane.WARNING_MESSAGE);

            }catch (Exception ex){
                JOptionPane.showMessageDialog(null,ex, "Warning", JOptionPane.WARNING_MESSAGE);
            }finally {
                cl.clearInput();
            }
        }
    }
    
    
    
    public void inputData(){
        try{
            this.Connector();
            koneksi.setAutoCommit(false);
            String sql = "INSERT INTO `datapenjualan`(`tanggal`, `kode_bln`, `user_id`, `NoInvoice`, `NoDeliveryOrder`, `Income`, `PPN`) VALUES (?,?,?,?,?,?,?);";
            PreparedStatement statement = koneksi.prepareStatement(sql);
            
            statement = koneksi.prepareStatement(sql);
            
            BufferedReader lineReader = new BufferedReader(new FileReader(this.filepath));
            String lineText = null;
            int count = 0;
            
            lineReader.readLine();
            while((lineText = lineReader.readLine()) != null){
                String[] data = lineText.split(",");
                String tanggal = data[0];
                String kode_bln = data[1];
                String NoInvoice = data[2];
                String NoDeliveryOrder = data[3];
                String Income = data[4];
                String PPN = data[5];
                
                statement.setString(1,tanggal);
                statement.setString(2,kode_bln);
                statement.setString(3,MainModel.ID);
                statement.setString(4,NoInvoice);
                statement.setString(5,NoDeliveryOrder);
                statement.setString(6,Income);
                statement.setString(7,PPN);

                statement.addBatch();
                if(count%batchSize==0){
                    statement.executeBatch();
                }
            }
            lineReader.close();
            statement.executeBatch();
            koneksi.commit();
            JOptionPane.showMessageDialog(null,  "Input Successful");
            
        }catch(Exception ex){
            JOptionPane.showMessageDialog(null,ex, "Warning", JOptionPane.WARNING_MESSAGE);
        }
    }

    public void registerData(Controller cl){
        try {
            if(this.username.isEmpty() || this.password.isEmpty()){
                JOptionPane.showMessageDialog(null,  "Anda belum memasukkan Input!", "Empty Field", JOptionPane.WARNING_MESSAGE);
            } else if(this.username.isBlank() || this.password.isBlank()){
                JOptionPane.showMessageDialog(null,  "Input tidak boleh blank (Space)!", "Blank Field", JOptionPane.WARNING_MESSAGE);
            } else {
                String query = "INSERT INTO `users`(`id`, `username`, `password`) VALUES (NULL, '"+this.username+"', '"+this.password+"')";
                this.Connector();
                statement = koneksi.createStatement();
                statement.executeUpdate(query);
                JOptionPane.showMessageDialog(null,"Registration Successful");}
        } catch(SQLIntegrityConstraintViolationException ex){
            JOptionPane.showMessageDialog(null,"Username telah digunakan!", "Warning", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex){
            JOptionPane.showMessageDialog(null,ex, "Warning", JOptionPane.WARNING_MESSAGE);
        } finally {
            cl.clearInput();
        }   
    }    
    
 
    
    public void table(ViewUpload vu){
        try{
            this.Connector();
            statement = koneksi.createStatement();
            String query = "SELECT * FROM `datapenjualan` WHERE `user_id` = '"+this.ID+"'";
            ResultSet resultSet = statement.executeQuery(query);
            DefaultTableModel model = (DefaultTableModel) ViewUpload.tabel.getModel();
            model.setRowCount(0);
            while (resultSet.next()){
                model.addRow(new String[]{resultSet.getString(1),resultSet.getString(4),resultSet.getString(6),resultSet.getString(7)});

            }
            
        }catch(SQLException e){
            JOptionPane.showMessageDialog(null,e, "Message", JOptionPane.WARNING_MESSAGE);
        }
    }
    
        
public void deleteRow(ViewUpload vu){
        try {
            int count = vu.tabel.getRowCount();
            if (count == 0){
                JOptionPane.showMessageDialog(null,"Data Kosong!", "Message", JOptionPane.WARNING_MESSAGE);
            } else {
                int row = vu.tabel.getSelectedRow();
                String cell = vu.tabel.getModel().getValueAt(row,1).toString();
                String sql = "DELETE FROM `datapenjualan` WHERE `NoInvoice` LIKE '"+cell+"' " ;
                this.Connector();
                PreparedStatement statement = koneksi.prepareStatement(sql);
                statement = koneksi.prepareStatement(sql);
                int ans = JOptionPane.showConfirmDialog(null,"Apakah anda yakin menghapus data yang dipilih?","Confirmation?", JOptionPane.OK_CANCEL_OPTION);
                if (ans == JOptionPane.YES_OPTION){
                    statement.execute();
                    JOptionPane.showMessageDialog(null,"Data Berhasil Dihapus!");
                    DefaultTableModel table1 = (DefaultTableModel) vu.tabel.getModel();
                    table1.removeRow(vu.tabel.getSelectedRow());
                } else {
                    JOptionPane.showMessageDialog(null,"Data Batal Dihapus");
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(ViewUpload.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception e){
            JOptionPane.showMessageDialog(null,"Anda belum memilih data yang akan dihapus !", "Message", JOptionPane.WARNING_MESSAGE);
        } 
        
        
}
    
    public void clearTab(){
        try{
            this.Connector();
            statement = koneksi.createStatement();
            String query = "DELETE FROM `datapenjualan`";
            int ans = JOptionPane.showConfirmDialog(null,"Apakah anda yakin menghapus seluruh data?","Confirmation?", JOptionPane.OK_CANCEL_OPTION);
            if (ans == JOptionPane.YES_OPTION){
                    statement.execute(query);
                    JOptionPane.showMessageDialog(null,"Data Tabel Berhasil Dihapus!");
                } else {
                    JOptionPane.showMessageDialog(null,"Data Batal Dihapus");
                }
            
        }catch(SQLException e){
            JOptionPane.showMessageDialog(null,e, "Message", JOptionPane.WARNING_MESSAGE);
        }
    }   
    public void bar(ViewAnalysis va){
        try{
            this.Connector();
            statement = koneksi.createStatement();
            String query = "SELECT * FROM `sales_monthly`";
            ResultSet resultSet = statement.executeQuery(query);
            String label[];
            int profit[];
            
            int count = 0;
            label = new String[12];
            profit = new int[12];
            while (resultSet.next()){
                profit[count] = resultSet.getInt("profit") / 1000000;
                label[count] = String.valueOf((profit[count]));
                count++;
            }
            
            ViewAnalysis.Jan.setValue(profit[0]);
            ViewAnalysis.JanLab.setText(label[0] + "%");
            
            ViewAnalysis.Feb.setValue(profit[1]);
            ViewAnalysis.FebLab.setText(label[1] + "%");
            
            ViewAnalysis.Mar.setValue(profit[2]);
            ViewAnalysis.MarLab.setText(label[2] + "%");
            
            ViewAnalysis.Apr.setValue(profit[3]);
            ViewAnalysis.AprLab.setText(label[3] + "%");
            
            ViewAnalysis.Mei.setValue(profit[4]);
            ViewAnalysis.MayLab.setText(label[4] + "%");
            
            ViewAnalysis.Jun.setValue(profit[5]);
            ViewAnalysis.JunLab.setText(label[5] + "%");
            
            ViewAnalysis.Jul.setValue(profit[6]);
            ViewAnalysis.JulLab.setText(label[6] + "%");
            
            ViewAnalysis.Aug.setValue(profit[7]);
            ViewAnalysis.AugLab.setText(label[7] + "%");
            
            
            ViewAnalysis.Sep.setValue(profit[8]);
            ViewAnalysis.SepLab.setText(label[8] + "%");
                      
            ViewAnalysis.Okt.setValue(profit[9]);
            ViewAnalysis.OktLab.setText(label[9] + "%");
           
            ViewAnalysis.Nov.setValue(profit[10]);
            ViewAnalysis.NovLab.setText(label[10] + "%");
            
            ViewAnalysis.Des.setValue(profit[11]);
            ViewAnalysis.DesLab.setText(label[11] + "%");
            
        }catch(SQLException e){
            JOptionPane.showMessageDialog(null,e, "Message", JOptionPane.WARNING_MESSAGE);
        }
//
    }

    public void analyize(){
        try {
            String query = "INSERT INTO sales_monthly (`month_id`, `expense`, `income`, `profit`) SELECT `kode_bln` , SUM(PPN), SUM(Income), SUM(Income) - SUM(PPN) FROM datapenjualan GROUP BY kode_bln";
            this.Connector();
            statement = koneksi.createStatement();
            statement.executeUpdate(query);
            JOptionPane.showMessageDialog(null,"Analyize Successful");
        } catch (Exception ex){
            JOptionPane.showMessageDialog(null,ex, "Warning", JOptionPane.WARNING_MESSAGE);
        }
    }
}
