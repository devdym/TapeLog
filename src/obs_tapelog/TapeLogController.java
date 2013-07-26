/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package obs_tapelog;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.print.Book;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.Printable;
import static java.awt.print.Printable.PAGE_EXISTS;
import java.awt.print.PrinterJob;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.URL;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.property.FloatProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuButton;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 *
 * @author chobs_sym
 */
public class TapeLogController implements Initializable {
    private static final Image ICON_S = new Image(TapeLogController.class.getResourceAsStream("settings.png"));
   
    //Button Panel
    @FXML Label StatusMassagelabel;
   
    @FXML Button ImportBtn;
    @FXML MenuButton TapeLogBtn;
    @FXML MenuButton BoxLabelBtn;
    @FXML MenuButton TapeLabelBtn;
    @FXML Button SettingsBtn; 
    
    //BoxList
    @FXML ListView boxListView;
    //tape log
    @FXML TableView TapeLogView;
    @FXML private TableColumn Tape_c;
    @FXML private TableColumn LineName_c;
    @FXML private TableColumn Seq_c;
    @FXML private TableColumn head_c;
    @FXML private TableColumn date_c;
    @FXML private TableColumn FF_c;
    @FXML private TableColumn LF_c;
    @FXML private TableColumn FSP_c;
    @FXML private TableColumn LSP_c;
    @FXML private TableColumn Comm_c;
    
    //tape info
    @FXML TextField lineName;
    @FXML TextField SEQ;
    @FXML TextField tape;
    @FXML TextField date;
    @FXML TextField heading;
    @FXML TextField FF;
    @FXML TextField LF;
    @FXML TextField FSP;
    @FXML TextField LSP;
    @FXML TextArea Comments_;
    @FXML CheckBox NTBP;
    @FXML ComboBox boxNb_CB;
    
    @FXML TextField Vessel_TF;
    @FXML TextField Company_TF;
    @FXML TextField Rec_TF;
    @FXML TextField Client_TF;
    @FXML TextField printerip_TF;
    @FXML TextField Job_nb_TF;
    @FXML TextField Sample_TF;
    @FXML TextField Area_TF;
    @FXML TextField Heading_i_TF;
    @FXML TextField Heading_d_TF;
    @FXML TextField Format_TF;
    @FXML TextField Copy_nb_TF;
    @FXML TextField Master_nb_TF;
    @FXML GridPane info;
    @FXML AnchorPane comments;
    @FXML GridPane files_sp;
      
    //settings  
    @FXML StackPane spane;
    @FXML Region Dilog;
    @FXML Region DilogSet;
    @FXML Pane DilogBox;
    @FXML Pane DilogBoxSet;
    @FXML Label Box_info;
    @FXML StackPane settingsWindow;
    @FXML SplitPane mainWindow;
    
    int BoxNb, p, copy_nb, master_nb, id;;
    File file;
    String filePath, job_nb, company, client, printer_ip, rec, sample, heading_i, heading_d, area, format, vessel;
       
    ObservableList<tapeLogUI> tLog = FXCollections.observableArrayList();
    ObservableList<tapeLogUI> selTape = FXCollections.observableArrayList();
    ObservableList boxList = FXCollections.observableArrayList();
    
    @Override public void initialize(URL url, ResourceBundle rb) {
        //create DB
        DBjob();
        getSettings();
        getBoxes();
        setTapeLogTable();        
        try{
            Class.forName("org.apache.derby.jdbc.EmbeddedDriver");  
        }catch(ClassNotFoundException e) {
            System.out.println(e);
        }
        ImageView settingImg = new ImageView(ICON_S);
        SettingsBtn.setGraphic(settingImg);
        FF.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent t) {
                if(FF.getText().matches("0")){
                   FF.setText("");
                }
            }  
        }); 
        FF.setOnKeyReleased(new EventHandler<KeyEvent>() {
           @Override
           public void handle(KeyEvent ke) {
                if(FF.getText().isEmpty()){
                    FF.setText("0");
                    saveLabel();
                }else{
                    saveLabel();
                }
            }
        }); 
        LF.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent t) {
                if(LF.getText().matches("0")){
                   LF.setText("");
                }
            }  
        }); 
        LF.setOnKeyReleased(new EventHandler<KeyEvent>() {
           @Override
           public void handle(KeyEvent ke) {
                if(LF.getText().isEmpty()){
                    LF.setText("0");
                    saveLabel();
                }else{
                    saveLabel();
                }
            }
        });      
        FSP.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent t) {
                if(FSP.getText().matches("0")){
                   FSP.setText("");
                }
            }  
        }); 
        FSP.setOnKeyReleased(new EventHandler<KeyEvent>() {
           @Override
           public void handle(KeyEvent ke) {
                if(FSP.getText().isEmpty()){
                    FSP.setText("0");
                    saveLabel();
                }else{
                    saveLabel();
                }
            }
        });
        LSP.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent t) {
                if(LSP.getText().matches("0")){
                   LSP.setText("");
                }
            }  
        }); 
        LSP.setOnKeyReleased(new EventHandler<KeyEvent>() {
           @Override
           public void handle(KeyEvent ke) {
                if(LSP.getText().isEmpty()){
                    LSP.setText("0");
                    saveLabel();
                }else{
                    saveLabel();
                }
            }
        });
        lineName.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent t) {
                if(lineName.getText().matches("0")){
                   lineName.setText("");
                }
            }  
        }); 
        lineName.setOnKeyReleased(new EventHandler<KeyEvent>() {
           @Override
           public void handle(KeyEvent ke) {
                if(lineName.getText().isEmpty()){
                    lineName.setText("0");
                    saveLabel();
                }else{
                    saveLabel();
                }
            }
        });
        SEQ.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent t) {
                if(SEQ.getText().matches("0")){
                   SEQ.setText("");
                }
            }  
        }); 
        SEQ.setOnKeyReleased(new EventHandler<KeyEvent>() {
           @Override
           public void handle(KeyEvent ke) {
                if(SEQ.getText().isEmpty()){
                    SEQ.setText("0");
                    saveLabel();
                }else{
                    saveLabel();
                }
            }
         });
        tape.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent t) {
                if(tape.getText().matches("0")){
                   tape.setText("");
                }
            }  
        }); 
        tape.setOnKeyReleased(new EventHandler<KeyEvent>() {
           @Override
           public void handle(KeyEvent ke) {
                if(tape.getText().isEmpty()){
                    //tape.setText("9999");
                   // saveLabel();
                    
                }else{
                    saveLabel();
                    getBoxes();
                }
            }
         });       
        Comments_.setOnKeyReleased(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent ke) {
                    saveLabel();
            }
        }); 
        date.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent t) {
                if(date.getText().matches("0")){
                   date.setText("");
                }
            }  
        }); 
        date.setOnKeyReleased(new EventHandler<KeyEvent>() {
           @Override
           public void handle(KeyEvent ke) {
                if(date.getText().isEmpty()){
                    date.setText("0");
                    saveLabel();
                }else{
                    saveLabel();
                }
            }
        });
        heading.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent t) {
                if(heading.getText().matches("0")){
                   heading.setText("");
                }
            }  
        }); 
        heading.setOnKeyReleased(new EventHandler<KeyEvent>() {
           @Override
           public void handle(KeyEvent ke) {
                if(heading.getText().isEmpty()){
                    heading.setText("0");
                    saveLabel();
                }else{
                    saveLabel();
                }
            }
        });
        boxListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener(){
            @Override
            public void changed(ObservableValue ov, Object oldValue, Object newValue) {
                System.out.println("newValue " + newValue);
                if(newValue != null){
                     comments.visibleProperty().setValue(Boolean.FALSE);
                     TapeLogView.visibleProperty().setValue(Boolean.TRUE);
                     String boxSel = boxListView.getSelectionModel().getSelectedItem().toString();
                     boxSel = boxSel.substring(boxSel.lastIndexOf("x") +1, boxSel.length()).trim();
                     getBox(boxSel);
                     configureButonsBox();
                }
            }
        });       
        TapeLogView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                System.out.println("NewVAlue: " + newValue);
                if(newValue != null){
                comments.visibleProperty().setValue(Boolean.TRUE);
                tapeLogUI selectedUser = (tapeLogUI)newValue;
                System.out.println(selectedUser.tapeProperty().toString());
                String tapeSel = selectedUser.tapeProperty().toString();
                tapeSel = tapeSel.substring(tapeSel.lastIndexOf(":")+1,tapeSel.length()).trim();
                getTape(tapeSel);
                TapeLabelBtn.setDisable(false);
                }
            }
         });    
        Dilog.setStyle("-fx-background-color: rgba(0, 0, 0, 0.4)");
        DilogSet.setStyle("-fx-background-color: rgba(0, 0, 0, 0.4)");
        DilogBox.setStyle("-fx-background-color: white");
        DilogBoxSet.setStyle("-fx-background-color: white");
        spane.visibleProperty().setValue(Boolean.FALSE);
        settingsWindow.visibleProperty().setValue(Boolean.FALSE);
    }       
    
    @FXML private void NewButtonAction(ActionEvent event) {
        System.out.println("NewButtonAction");
        int lb = getLastboxNum();
        createNewTape(String.valueOf(lb), "9999", "LINENAME", "0", "0", "0", "0", "0000-00-00", "0", "0");
        getBoxes();
        boxListView.getSelectionModel().selectLast();
        getBox(String.valueOf(getLastboxNum()));
    }
    
    @FXML private void ImportButtonAction(ActionEvent event) {
        File filePathAbs;
        System.out.println("ImportButtonAction");
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("labels","*.xml");
        fileChooser.getExtensionFilters().add(extFilter);
        getSettings();
        
        if (filePath != "null" || filePath != ""){
            filePathAbs = new File(filePath.substring(0, filePath.lastIndexOf("\\")));
            if(filePathAbs.exists()){
                fileChooser.setInitialDirectory(filePathAbs);
            }
        } 
        List<File> file = fileChooser.showOpenMultipleDialog(null);
        for(int i=0; i<file.size(); i++){
            processFile(file.get(i));
        }
        
        orgTape(0);
        getBox(String.valueOf(0));
        getBoxes();
        TapeLogView.getSelectionModel().selectLast();
    }
    
    @FXML private void PrintTapeLogButtonAction(ActionEvent event) {
        System.out.println("PrintTapeLogButtonAction");
        PrinterJob printJob = PrinterJob.getPrinterJob();
        Book book = new Book();
        PageFormat documentPageFormat = new PageFormat();
        Paper paper = new Paper();
        paper.setSize(595, 842);
        paper.setImageableArea(20, 20, 575, 822);
        documentPageFormat.setPaper(paper);
        documentPageFormat.setOrientation(PageFormat.LANDSCAPE);
        for( p=1; p<=getLastboxNum(); p++){
            BoxNb = p;
            book.append(new Document2(), documentPageFormat);
        }
        printJob.setPageable(book);
        if(printJob.printDialog()){
            try{
                if(BoxNb == 0){
                StatusMassagelabel.setText("No Data!");
                } else {
                    printJob.print();
                }
            } catch (Exception PrintException) {
                PrintException.printStackTrace();
            }
        }
    }
    
    @FXML private void ExportToCSV(ActionEvent event){
        System.out.println("Export to CSV");
        String ex = "";
        
        //header
        ex = "# Vessel:\t" + vessel + "\n" +
                "# Company:\t" + company + "\n" +
                "# Job Number:\t" + job_nb + "\n" +
                "# Client:\t" + client + "\n" +
                "# Area:\t" + area + "\n\n" +
                "Tape number\tLine Name\tSeq\tFF\tLF\tFSP\tLSP\tDate\tHeading\tBox Number\tComments\tNTBP\n";           
        try{
            Connection con = DriverManager.getConnection("jdbc:derby:DataBase;create=true;");
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM LABEL");
            while (rs.next()){
                ex = ex + rs.getInt("TNUMBER") + "\t " + 
                          rs.getString("LINE")+ "\t " + rs.getInt("SEQ")+ "\t " + 
                          rs.getInt("FF")+ "\t " + rs.getInt("LF") + "\t " + 
                          rs.getInt("FSP")+ "\t " + rs.getInt("LSP") + "\t " + 
                          rs.getString("DATE_")+ "\t " + rs.getFloat("HEADING") + "\t " + 
                          rs.getInt("BOX")+ "\t " + rs.getString("COMMENTS") + "\t " + 
                          rs.getInt("NTBP")+ "\n";
            }
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } 
        saveExport(ex);
    }
    
    private void saveExport(String tx){
        String fileSavePath = "C:";
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Excel file (*.xls)","*.xls");
        fileChooser.getExtensionFilters().add(extFilter);
        File userSelection = fileChooser.showSaveDialog(null);
        if(userSelection != null){
            try {
                FileWriter fileWriter = null;
                fileWriter = new FileWriter(userSelection);
                fileWriter.write(tx);
                fileWriter.close();
            }
            catch (IOException ex) {
               ex.printStackTrace();
            }
        }
    }
    
    @FXML private void BoxLabelsButtonAction(ActionEvent event) {
        System.out.println("BoxLabelsButtonAction");
        String t = boxListView.getSelectionModel().getSelectedItem().toString();
        System.out.println("boxPR: " + t);
        t = t.substring(t.lastIndexOf(":")+1, t.length());
        System.out.println("boxPR1: " + t);
        t = t.substring(t.lastIndexOf("x")+1, t.length()).trim();
        System.out.println("boxPR2: " + t);
        int ftape=0, ltape=0;
        try {
            Connection con = DriverManager.getConnection("jdbc:derby:DataBase;create=true;"); // The database URL may not be same for you, lookup the "Services" side-bar.
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT MIN(TNUMBER) from LABEL WHERE BOX = "+t+"");
            System.out.println("SELECT MIN(TNUMBER) from LABEL WHERE BOX = "+t+"");
            while(rs.next()){
                ftape = rs.getInt(1);         
            }
            con.close();
        } catch(SQLException e){
            System.err.println(e);
        } 
        System.out.println("ftape" + ftape);
        //------------------
        try {
            Connection con = DriverManager.getConnection("jdbc:derby:DataBase;create=true;"); // The database URL may not be same for you, lookup the "Services" side-bar.
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT MAX(TNUMBER) from LABEL WHERE BOX = "+t+"");
            System.out.println("SELECT MAX(TNUMBER) from LABEL WHERE BOX = "+t+"");
            
            while(rs.next()){
                ltape = rs.getInt(1);
        
            }
            con.close();
        } catch(SQLException e){
            System.err.println(e);
        } 
        System.out.println("ltape" + ltape);
        PrintBox(t, String.valueOf(ftape), String.valueOf(ltape));
    }
    
    @FXML private void BoxLabelsPageButtonAction(ActionEvent event){
        PrinterJob printJob = PrinterJob.getPrinterJob();
        Book book = new Book();
        PageFormat documentPageFormat = new PageFormat();
        Paper paper = new Paper();
        paper.setSize(285, 585);
        paper.setImageableArea(27, 45, 285, 585);
        documentPageFormat.setPaper(paper);
        documentPageFormat.setOrientation(PageFormat.PORTRAIT);
        book.append(new BoxlabelPrinter(), documentPageFormat);
        book.append(new BoxlabelPrinter(), documentPageFormat);
        book.append(new BoxlabelPrinterC(), documentPageFormat);
        book.append(new BoxlabelPrinterC(), documentPageFormat);
        book.append(new BoxlabelPrinterD(), documentPageFormat);
        book.append(new BoxlabelPrinterD(), documentPageFormat);
        printJob.setPageable(book);
        if(printJob.printDialog()){
            try{
                printJob.print();
            } catch (Exception PrintException) {
                PrintException.printStackTrace();
            }
            StatusMassagelabel.setText("Labels are printed.");
        }
    }
    
    @FXML private void PrintTape(ActionEvent event) {
        saveLabel();
        if(tape.getText().isEmpty() || lineName.getText().isEmpty() || FF.getText().isEmpty() ||
                 LF.getText().isEmpty() || FSP.getText().isEmpty() || LSP.getText().isEmpty() || 
                 date.getText().isEmpty() || SEQ.getText().isEmpty()){
            StatusMassagelabel.setText("No Data!");
        } else {
            String print_name, label, label_c, label_type;
            print_name = printer_ip;
            String NTBP_label = ""; 
            label_type = "newlogo_up.lbx";

            if (NTBP.isSelected()){
                NTBP_label = " NTBP ";
            }
            label = "<labels _FORMAT=\"" + label_type + "\" _QUANTITY=\"1\"> " + "\n" +
                    "  <label>" +  "\n" +
                    "    <variable name=\"VTAPE\">"+ tape.getText() +"</variable>" + "\n" +
                    "    <variable name=\"TYPE\">MASTER</variable>" + "\n" +
                    "    <variable name=\"VLINE\">"+ lineName.getText() +"</variable>" + "\n" +
                    "    <variable name=\"FF\">"+ FF.getText() +"</variable>" + "\n" +
                    "    <variable name=\"LF\">"+ LF.getText() +"</variable>" + "\n" +
                    "    <variable name=\"FSP\">"+ FSP.getText() +"</variable>" + "\n" +
                    "    <variable name=\"LSP\">"+ LSP.getText() +"</variable>" + "\n" +
                    "    <variable name=\"VDATE\">"+ date.getText() +"</variable>" + "\n" +
                    "    <variable name=\"VREC\">"+ rec +"</variable>" + "\n" +
                    "    <variable name=\"VSAMPLE\">"+ sample +"</variable>" + "\n" +
                    "    <variable name=\"VFORMAT\">"+ format +"</variable>" + "\n" +
                    "    <variable name=\"HEADING\">"+ heading.getText() +"</variable>" + "\n" +
                    "    <variable name=\"VCLIENT\">"+ client +"</variable>" + "\n" +
                    "    <variable name=\"AREA\">"+ area +"</variable>" + "\n" +
                    "    <variable name=\"VCOMPANY\">"+ company +"</variable>" + "\n" +
                    "    <variable name=\"VSHIP\">"+ vessel +"</variable>" + "\n" +
                    "    <variable name=\"VSEQ\">"+ SEQ.getText() +"</variable>" + "\n" +
                    "    <variable name=\"NTBP\">"+ NTBP_label +"</variable>" + "\n" +
                    "  </label>" + "\n" +
                    "</labels>";

            label_c = "<labels _FORMAT=\"" + label_type + "\" _QUANTITY=\"1\"> " + "\n" +
                    "  <label>" +  "\n" +
                    "    <variable name=\"VTAPE\">"+ tape.getText() +"</variable>" + "\n" +
                    "    <variable name=\"TYPE\">COPY</variable>" + "\n" +
                    "    <variable name=\"VLINE\">"+ lineName.getText() +"</variable>" + "\n" +
                    "    <variable name=\"FF\">"+ FF.getText() +"</variable>" + "\n" +
                    "    <variable name=\"LF\">"+ LF.getText() +"</variable>" + "\n" +
                    "    <variable name=\"FSP\">"+ FSP.getText() +"</variable>" + "\n" +
                    "    <variable name=\"LSP\">"+ LSP.getText() +"</variable>" + "\n" +
                    "    <variable name=\"VDATE\">"+ date.getText() +"</variable>" + "\n" +
                    "    <variable name=\"VREC\">"+ rec +"</variable>" + "\n" +
                    "    <variable name=\"VSAMPLE\">"+ sample +"</variable>" + "\n" +
                    "    <variable name=\"VFORMAT\">"+ format +"</variable>" + "\n" +
                    "    <variable name=\"HEADING\">"+ heading.getText() +"</variable>" + "\n" +
                    "    <variable name=\"VCLIENT\">"+ client +"</variable>" + "\n" +
                    "    <variable name=\"AREA\">"+ area +"</variable>" + "\n" +
                    "    <variable name=\"VCOMPANY\">"+ company +"</variable>" + "\n" +
                    "    <variable name=\"VSHIP\">"+ vessel +"</variable>" + "\n" +
                    "    <variable name=\"VSEQ\">"+ SEQ.getText() +"</variable>" + "\n" +
                    "    <variable name=\"NTBP\">"+ NTBP_label +"</variable>" + "\n" +
                    "  </label>" + "\n" +
                    "</labels>";
            int qu = master_nb;
            int qu_c = copy_nb;
            try {
                Socket sock = new Socket(print_name, 9200);
                PrintWriter os = new PrintWriter(sock.getOutputStream( ), true);
                for (int i=1; i<=qu; i++) {
                    os.print(label);
                }
                for (int j=1; j<=qu_c; j++) {
                    os.print(label_c);
                }
                os.flush( );
                StatusMassagelabel.setText("Labels are printed.");
            } catch (java.io.IOException e) {
                StatusMassagelabel.setText("error connecting to " + print_name + e);
            }
         }
    }
    
    @FXML private void PrintLabelButtonAction(ActionEvent event) {
        saveLabel();
        PrinterJob printJob = PrinterJob.getPrinterJob();
        Book book = new Book();
        PageFormat documentPageFormat = new PageFormat();
        Paper paper = new Paper();
        paper.setSize(285, 585);
        paper.setImageableArea(27, 45, 285, 585);
        documentPageFormat.setPaper(paper);
        documentPageFormat.setOrientation(PageFormat.PORTRAIT);
        book.append(new Document1(), documentPageFormat);
        printJob.setPageable(book);
        if(printJob.printDialog()){
            try{
                if(tape.getText().isEmpty() || lineName.getText().isEmpty() || FF.getText().isEmpty() ||
                LF.getText().isEmpty() || FSP.getText().isEmpty() || LSP.getText().isEmpty() || date.getText().isEmpty() ||
                SEQ.getText().isEmpty()){
                StatusMassagelabel.setText("No Data!");
                } else {
                    printJob.print();
                }
            } catch (Exception PrintException) {
                PrintException.printStackTrace();
            }
            StatusMassagelabel.setText("Labels are printed.");
        }
    }
    
    private void processFile(File file){
        System.out.println("processFile");
        String dFF, dLF, dFSP, dLSP, dSeq, dLine, dDate, dHed;  
        try{     
            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            Document doc = docBuilder.parse(file);
            
            try {
                Connection con = DriverManager.getConnection("jdbc:derby:DataBase;create=true;"); 
                Statement stmt = con.createStatement();
                String q = "UPDATE SETTINGS SET PATH = '" + file.getCanonicalPath() + "' "
                        + "WHERE SERVER = 'localhost'";
                stmt.executeUpdate(q);
                System.out.println(q);
                con.close(); 
            } catch(SQLException e){
                System.err.println(e);
            } 

            doc.getDocumentElement().normalize();

            NodeList tlabel = doc.getElementsByTagName("header");
            
            Node label1 = tlabel.item(0);
            if (label1.getNodeType() == Node.ELEMENT_NODE){
                Element fstElemnt = (Element)label1;
                
                NodeList tape = fstElemnt.getElementsByTagName("tape_number");
                Element tape_ = (Element) tape.item(0);
                NodeList tape_num = tape_.getChildNodes();
                
                NodeList fFile = fstElemnt.getElementsByTagName("first_file");
                Element FF_ = (Element) fFile.item(0);
                NodeList ff_num = FF_.getChildNodes();
                dFF = ff_num.item(0).getNodeValue();                
                
                NodeList lFile = fstElemnt.getElementsByTagName("last_file");
                Element LF_ = (Element) lFile.item(0);
                NodeList lf_num = LF_.getChildNodes();
                dLF = lf_num.item(0).getNodeValue();
                
                NodeList fShot = fstElemnt.getElementsByTagName("first_shot_point");
                Element FS_ = (Element) fShot.item(0);
                NodeList fs_num = FS_.getChildNodes();
                dFSP = fs_num.item(0).getNodeValue();                
                
                NodeList lShot = fstElemnt.getElementsByTagName("last_shot_point");
                Element LS_ = (Element) lShot.item(0);
                NodeList ls_num = LS_.getChildNodes();
                dLSP = ls_num.item(0).getNodeValue();
                
                NodeList lseq = fstElemnt.getElementsByTagName("first_sequence");
                Element seq_ = (Element) lseq.item(0);
                NodeList seq_num = seq_.getChildNodes();
                dSeq = seq_num.item(0).getNodeValue();   
                
                NodeList lline = fstElemnt.getElementsByTagName("first_line");
                Element ln_ = (Element) lline.item(0);
                NodeList ln_nm = ln_.getChildNodes();
                dLine = ln_nm.item(0).getNodeValue();  
                
                NodeList ldate = fstElemnt.getElementsByTagName("start_date");
                Element dt_ = (Element) ldate.item(0);
                NodeList dt_c = dt_.getChildNodes();
                dDate = dt_c.item(0).getNodeValue(); 

                if (Integer.parseInt(dFSP) < Integer.parseInt(dLSP)){
                    dHed = heading_i; 
                }
                else {
                    dHed = heading_d;
                }
                createNewTapeWObox( String.valueOf(tape_num.item(0).getNodeValue()), dLine, dFF, dLF, dFSP, dLSP, dDate, dHed, dSeq);
            }
            
       }catch (SAXParseException err) {
            System.out.println ("** Parsing error" + ", line " + err.getLineNumber () + ", uri " + err.getSystemId ());
            System.out.println(" " + err.getMessage ());
            }catch (SAXException e) {
                Exception x = e.getException ();
       }catch (Throwable t) {        } 
    }
    
    private int getLastboxNum() {
        System.out.println("getLastboxNum");
        int boxn = 10, boxC =0;
        try {
            Connection con = DriverManager.getConnection("jdbc:derby:DataBase;create=true;"); // The database URL may not be same for you, lookup the "Services" side-bar.
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT MAX(BOX) from LABEL");
            
            while(rs.next()){
                if(rs.getInt(1) == 0){
                    boxn = 1;
                    System.out.println("\n box 1 " + boxn);
                }else {
                    boxn = rs.getInt(1);
                    System.out.println("\n box " + boxn);
                }
            }
            con.close();
        } catch(SQLException e){
            System.err.println(e);
        } 
        //-----------------------
        try {
            Connection con = DriverManager.getConnection("jdbc:derby:DataBase;create=true;"); // The database URL may not be same for you, lookup the "Services" side-bar.
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT COUNT(BOX) from LABEL where BOX = "+ boxn +"");
            
            while(rs.next()){
                if(rs.getInt(1) == 0){
                    boxC = 0;
                    System.out.println("box count 0" + boxC);
                }else{
                    boxC = rs.getInt(1);
                    System.out.println("box count = " + boxC);
                }
            }
            con.close();
        } catch(SQLException e){
            System.err.println(e);
        } 
        
        if(boxC > 19){
            boxn = boxn + 1;
            return boxn;
        }else{
            return boxn;
        }
    } 
    
    private void createNewTape(String boxn, String tnum, String line, String ff, String lf, String fsp, String lsp, String date_, String hed, String seq){
        System.out.println("createNewTape");
        try {
            Connection con = DriverManager.getConnection("jdbc:derby:DataBase;create=true;"); 
            Statement stmt = con.createStatement();
            String q = "INSERT INTO LABEL (TNUMBER, BOX, LINE, SEQ, FF, LF, FSP, LSP, DATE_, HEADING, COMMENTS) "+ 
	    "VALUES (" + tnum + 
                    ", " + Integer.valueOf(boxn) + 
                    ", '" + line +
                    "', " + seq +
                    ", " + Integer.valueOf(ff) +
                    ", " + Integer.valueOf(lf) +
                    ", " + Integer.valueOf(fsp) +
                    ", " + Integer.valueOf(lsp) +
                    ", '" + date_ +
                    "', " + hed +
                    ", ''"+
                    ")";
            System.out.println(q);
            stmt.execute(q);
            con.close(); 
        } catch(SQLException e){
            System.err.println(e);
        } 
        getBox(boxn);
    }
    
    private void createNewTapeWObox(String tnum, String line, String ff, String lf, String fsp, String lsp, String date_, String hed, String seq){
        System.out.println("createNewTapeWObox");
        try {
            Connection con = DriverManager.getConnection("jdbc:derby:DataBase;create=true;"); 
            Statement stmt = con.createStatement();
            String q = "INSERT INTO LABEL (TNUMBER, BOX, LINE, SEQ, FF, LF, FSP, LSP, DATE_, HEADING, COMMENTS) "+ 
	    "VALUES (" + tnum + 
                    ", " + 0 + 
                    ", '" + line +
                    "', " + seq +
                    ", " + Integer.valueOf(ff) +
                    ", " + Integer.valueOf(lf) +
                    ", " + Integer.valueOf(fsp) +
                    ", " + Integer.valueOf(lsp) +
                    ", '" + date_ +
                    "', " + hed +
                    ", ''"+
                    ")";
            System.out.println(q);
            stmt.execute(q);
            con.close(); 
        } catch(SQLException e){
            System.err.println(e);
        }    
    }
    
    @FXML private void SaveTapeNbAction() {
        if(tape.getText().isEmpty()){  
        }else{
            saveLabel();
            getBoxes();
        }
    }
    
    @FXML private void SaveTapeButtonAction() {
        saveLabel();
    }
    
    public void saveLabel(){
        System.out.println("SaveTapeButtonAction");
        int ntbp = 0;
        String comm = "";
        String sbox;
        if(boxNb_CB.getSelectionModel().isEmpty()){
            sbox = boxNb_CB.getPromptText();
            System.out.println("==null " + boxNb_CB.getPromptText());
        } else {
            sbox = boxNb_CB.getSelectionModel().getSelectedItem().toString();
            System.out.println("!=null else " + boxNb_CB.getSelectionModel().getSelectedItem().toString());
        }
        
        if(NTBP.isSelected()){
            ntbp = 1;
        } 
        try {
            Connection con = DriverManager.getConnection("jdbc:derby:DataBase;create=true;"); 
            Statement stmt = con.createStatement();
            String q = "UPDATE LABEL SET LINE = '" + lineName.getText() + "', "
                    + "SEQ = " + SEQ.getText() + ", FF = " + FF.getText() + ", "
                    + "LF = " + LF.getText() + ", FSP = " + FSP.getText() + ", "
                    + "LSP = " + LSP.getText() + ", DATE_ = '" + date.getText() + "', "
                    + "HEADING = " + heading.getText() + ", "
                    + "COMMENTS = '" + Comments_.getText() + " " + comm + "', NTBP = "+ ntbp + ", "
                    + "TNUMBER = " + tape.getText() + ", BOX = "+ sbox +" WHERE ID = " + id;
	    System.out.println(q);
            stmt.executeUpdate(q);
            con.close(); 
        } catch(SQLException e){
            System.err.println(e);
        } 
        String boxSel = boxListView.getSelectionModel().getSelectedItem().toString();
        System.out.println("boxSel before: " + boxSel);
        boxSel = boxSel.substring(boxSel.lastIndexOf("x") +1, boxSel.length()).trim();
        System.out.println("boxSel" + boxSel);
        getBox(boxSel);
    }
    
    @FXML private void SaveSettingsButton(ActionEvent event){
        System.out.println("SaveSettingsButton");
        settingsWindow.visibleProperty().setValue(Boolean.FALSE);
        mainWindow.visibleProperty().setValue(Boolean.TRUE);
        try {
            Connection con = DriverManager.getConnection("jdbc:derby:DataBase;create=true;"); 
            Statement stmt = con.createStatement();
            String q = "UPDATE SETTINGS SET PRINTER_IP = '" + printerip_TF.getText() + "', "
                    + "MASTER_NB = " + Integer.valueOf(Master_nb_TF.getText()) + ", COPY_NB = " + Integer.valueOf(Copy_nb_TF.getText()) + ", "
                    + "COMPANY = '" + Company_TF.getText() + "', CLIENT = '" + Client_TF.getText() + "', "
                    + "JOB = '" + Job_nb_TF.getText() + "', AREA = '" + Area_TF.getText() + "', "
                    + "FORMAT = '" + Format_TF.getText() + "', "
                    + "REC = '" + Rec_TF.getText() + "', SAMPLE = '"+ Sample_TF.getText() + "', "
                    + "HED_I = '" + Heading_i_TF.getText() + "', HED_D = '" + Heading_d_TF.getText() + "', VESSEL = '" + Vessel_TF.getText() + "' WHERE SERVER = 'localhost'";   
            stmt.executeUpdate(q);
            con.close(); 
        } catch(SQLException e){
            System.err.println(e);
        } 
        getSettings();
        configureButonsBox();
        getBoxes();
    }
    
    @FXML private void QdeleteBox(ActionEvent event){
        spane.visibleProperty().setValue(Boolean.TRUE);
    }
    
    @FXML private void QdeleteBoxClose(ActionEvent event){
        spane.visibleProperty().setValue(Boolean.FALSE);
    }
    
    @FXML private void DeleteBoxButtonAction(ActionEvent event) {
        System.out.println("DeleteBoxButtonAction");        
        String Sel = boxListView.getSelectionModel().getSelectedItem().toString();
        try {
            Connection con = DriverManager.getConnection("jdbc:derby:DataBase;create=true;"); 
            Statement stmt = con.createStatement();
            stmt.execute("DELETE FROM  LABEL WHERE BOX = " + (Sel.substring(Sel.lastIndexOf("x")+1, Sel.length())).trim());
            con.close(); 
        } catch(SQLException e){
            System.err.println(e);
        } 
        getBoxes();
        comments.visibleProperty().setValue(Boolean.FALSE);
        TapeLogView.visibleProperty().setValue(Boolean.FALSE);
        spane.visibleProperty().setValue(Boolean.FALSE);
    }
    
    @FXML private void DeleteTapeButtonAction(ActionEvent event) {
        System.out.println("DeleteTapeButtonAction");       
        try {
            Connection con = DriverManager.getConnection("jdbc:derby:DataBase;create=true;"); 
            Statement stmt = con.createStatement();
            String q = "DELETE FROM  LABEL WHERE TNUMBER = " + tape.getText();
            System.out.println(q);
            stmt.execute(q);
            con.close(); 
        } catch(SQLException e){
            System.err.println(e);
        } 
        String boxSel = boxListView.getSelectionModel().getSelectedItem().toString();
        System.out.println("boxSel before: " + boxSel);
        boxSel = boxSel.substring(boxSel.lastIndexOf("x") +1, boxSel.length()).trim();
        System.out.println("boxSel" + boxSel);
        getBox(boxSel);
        comments.visibleProperty().setValue(Boolean.FALSE);
    }
    
    public void configureButonsSettings(){
        ImportBtn.setDisable(true);
        TapeLogBtn.setDisable(true);
        BoxLabelBtn.setDisable(true);
        TapeLabelBtn.setDisable(true);
        SettingsBtn.setDisable(true);
    }
    
    public void configureButonsBox(){
        ImportBtn.setDisable(false);
        TapeLogBtn.setDisable(false);
        BoxLabelBtn.setDisable(false);
        TapeLabelBtn.setDisable(true);
        SettingsBtn.setDisable(false);
    }
    
    public void configureButonsTape(){
        ImportBtn.setDisable(false);
        TapeLogBtn.setDisable(true);
        BoxLabelBtn.setDisable(true);
        TapeLabelBtn.setDisable(false);
        SettingsBtn.setDisable(false); 
    }
    
    public void PrintBox(String box, String fTape, String lTape){ 
        if(fTape == null || lTape == null){
            StatusMassagelabel.setText("No Data!");
        } else {
        String label_type = "boxlabel_up.lbx", label, label_SC, label_SD;
        int qu = 4;

        label = "<labels _FORMAT=\""+ label_type +"\" _QUANTITY=\"1\"> " + "\n" +
                "  <label>" +  "\n" +
                "    <variable name=\"VBOX\">"+ box +"</variable>" + "\n" +
                "    <variable name=\"VCODE\">SR</variable>" + "\n" +
                "    <variable name=\"TYPE\"> PRIMARY </variable>" + "\n" +
                "    <variable name=\"VFR\">"+ fTape +"</variable>" + "\n" +
                "    <variable name=\"VLR\">"+ lTape +"</variable>" + "\n" +
                "    <variable name=\"VCLIENT\">"+ client +"</variable>" + "\n" +
                "    <variable name=\"AREA\">"+ area +"</variable>" + "\n" +
                "    <variable name=\"VSHIP\">"+ vessel +"</variable>" + "\n" +
                "    <variable name=\"VJOB\">"+ job_nb +"</variable>" + "\n" +
                "  </label>" + "\n" +
                "</labels>";

        label_SC = "<labels _FORMAT=\""+ label_type +"\" _QUANTITY=\"1\"> " + "\n" +
                "  <label>" +  "\n" +
                "    <variable name=\"VBOX\">"+ box +"</variable>" + "\n" +
                "    <variable name=\"VCODE\">SC</variable>" + "\n" +
                "    <variable name=\"TYPE\">  COPY  </variable>" + "\n" +
                "    <variable name=\"VFR\">"+ fTape +"</variable>" + "\n" +
                "    <variable name=\"VLR\">"+ lTape +"</variable>" + "\n" +
                "    <variable name=\"VCLIENT\">"+ client +"</variable>" + "\n" +
                "    <variable name=\"AREA\">"+ area +"</variable>" + "\n" +
                "    <variable name=\"VSHIP\">"+ vessel +"</variable>" + "\n" +
                "    <variable name=\"VJOB\">"+ job_nb +"</variable>" + "\n" +
                "  </label>" + "\n" +
                "</labels>";
        
        label_SD = "<labels _FORMAT=\""+ label_type +"\" _QUANTITY=\"1\"> " + "\n" +
                "  <label>" +  "\n" +
                "    <variable name=\"VBOX\">"+ box +"</variable>" + "\n" +
                "    <variable name=\"VCODE\">SD</variable>" + "\n" +
                "    <variable name=\"TYPE\">  COPY  </variable>" + "\n" +
                "    <variable name=\"VFR\">"+ fTape +"</variable>" + "\n" +
                "    <variable name=\"VLR\">"+ lTape +"</variable>" + "\n" +
                "    <variable name=\"VCLIENT\">"+ client +"</variable>" + "\n" +
                "    <variable name=\"AREA\">"+ area +"</variable>" + "\n" +
                "    <variable name=\"VSHIP\">"+ vessel +"</variable>" + "\n" +
                "    <variable name=\"VJOB\">"+ job_nb +"</variable>" + "\n" +
                "  </label>" + "\n" +
                "</labels>";

            try {
                Socket sock = new Socket(printer_ip, 9200);
                PrintWriter os = new PrintWriter(sock.getOutputStream( ), true);
                for (int i=1; i<=qu; i++) {
                    os.print(label);
                     System.out.println("label");
                }
                for (int i=1; i<=qu; i++) {
                    os.print(label_SC);
                    System.out.println("label_SC");
                }
                if(copy_nb>=2){
                    for (int i=1; i<=qu; i++) {
                        os.print(label_SD);
                        System.out.println("label_SD");
                    }
                }
                os.flush( );
                StatusMassagelabel.setText("Labels are printed.");
            } catch (java.io.IOException e) {
                StatusMassagelabel.setText("error connecting to " + printer_ip + e);
            }
        }
    }
    
    private void orgTape(int sBox) {
        System.out.println("orgTape");
       
        tLog.removeAll(tLog);
        System.out.println(sBox);
        try {
            Connection con = DriverManager.getConnection("jdbc:derby:DataBase;create=true;"); 
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * from LABEL WHERE BOX = "+sBox+" ORDER BY TNUMBER");
            System.out.println("SELECT * from LABEL WHERE BOX = "+sBox+"");
            
            while(rs.next()){
                 try {
                    Connection conu = DriverManager.getConnection("jdbc:derby:DataBase;create=true;"); 
                    Statement stmtu = conu.createStatement();
                    String q = "UPDATE LABEL SET BOX = " + getLastboxNum() 
                           + " WHERE TNUMBER = "+ rs.getInt("TNUMBER") +""; 
                    System.out.println(q);
                    stmtu.executeUpdate(q);
                    conu.close(); 
                } catch(SQLException e){
                    System.err.println(e);
                }     
            }
            con.close();
        } catch(SQLException e){
            System.err.println(e);
        } 
    }
    
    private class Document1 implements Printable {
        public int print(Graphics g, PageFormat pageFormat, int page){
            Graphics2D g2d = (Graphics2D) g; 
            g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
            g2d.setPaint(Color.black);
            g2d.setStroke(new BasicStroke(1));
            Font titleFont = new Font("Monospac821 BT", Font.BOLD, 12);
            Font titleFontN = new Font("Monospac821 BT", Font.PLAIN, 10);
            int spl=13, sp = 38;
            g2d.setFont(titleFont);
            g2d.drawString("TAPE # " + tape.getText() + "   MASTER", 10, sp);
            g2d.drawString("LINE # " + lineName.getText(), 10, sp = spl + sp);
            g2d.drawString("FILE from " + FF.getText() +" to " + LF.getText(), 10, sp = spl + sp);
            g2d.drawString("SP   from " + FSP.getText() +" to " + LSP.getText(), 10, sp = spl + sp);           
            g2d.setFont(titleFontN);
            g2d.drawString("DATE: " + date.getText(), 10, sp = spl + sp);
            g2d.drawString("REC LENGTH: " + rec + " MS", 10, sp = spl + sp);
            g2d.drawString("SAMPLE RATE: " + sample + " MS", 10, sp = spl + sp);
            g2d.drawString("FORMAT: " + format, 10, sp = spl + sp);
            g2d.drawString("HEADING: " + heading.getText(), 10, sp = spl + sp);
            g2d.drawString("CLIENT: " + client, 10, sp = spl + sp);
            g2d.drawString("AREA: " + area, 10, sp = spl + sp);
            g2d.drawString("VESSEL: " + vessel, 10, sp = spl + sp);
            g2d.drawString("SEQ: " + SEQ.getText(), 10, sp = spl + sp);           
            g2d.rotate(Math.toRadians(180), -100, 20 );
            g2d.setFont(titleFont);
            sp = sp - 595;
            g2d.drawString("TAPE # " + tape.getText() + "   COPY", -412, sp = spl + sp);
            g2d.drawString("LINE # " + lineName.getText(), -412, sp = spl + sp);
            g2d.drawString("FILE from " + FF.getText() +" to " + LF.getText(), -412, sp = spl + sp);
            g2d.drawString("SP   from " + FSP.getText() +" to " + LSP.getText(), -412, sp = spl + sp);
            g2d.setFont(titleFontN);
            g2d.drawString("DATE: " + date.getText(), -412, sp = spl + sp);
            g2d.drawString("REC LENGTH: " + rec + " MS", -412, sp = spl + sp);
            g2d.drawString("SAMPLE RATE: " + sample + " MS", -412, sp = spl + sp);
            g2d.drawString("FORMAT: " + format, -412, sp = spl + sp);
            g2d.drawString("HEADING: " + heading.getText(), -412, sp = spl + sp);
            g2d.drawString("CLIENT: " + client, -412, sp = spl + sp);
            g2d.drawString("AREA: " + area, -412, sp = spl + sp);
            g2d.drawString("VESSEL: " + vessel, -412, sp = spl + sp);
            g2d.drawString("SEQ: " + SEQ.getText(), -412, sp = spl + sp);
            return (PAGE_EXISTS);
        }
    } 
    
    private class BoxlabelPrinter implements Printable {
        public int print(Graphics g, PageFormat pageFormat, int page){
            String boxSel = boxListView.getSelectionModel().getSelectedItem().toString();
                     System.out.println("boxSel before: " + boxSel);
                     boxSel = boxSel.substring(boxSel.lastIndexOf("x") +1, boxSel.length()).trim();
                     System.out.println("boxSel" + boxSel);      
            int ftape=0, ltape=0;
        try {
            Connection con = DriverManager.getConnection("jdbc:derby:DataBase;create=true;"); // The database URL may not be same for you, lookup the "Services" side-bar.
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT MIN(TNUMBER) from LABEL WHERE BOX = "+boxSel+"");
            System.out.println("SELECT MIN(TNUMBER) from LABEL WHERE BOX = "+boxSel+"");
            
            while(rs.next()){
                ftape = rs.getInt(1);          
            }
            con.close();
        } catch(SQLException e){
            System.err.println(e);
        } 
        System.out.println("ftape" + ftape);
        //------------------
        try {
            Connection con = DriverManager.getConnection("jdbc:derby:DataBase;create=true;"); // The database URL may not be same for you, lookup the "Services" side-bar.
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT MAX(TNUMBER) from LABEL WHERE BOX = "+boxSel+"");
            System.out.println("SELECT MAX(TNUMBER) from LABEL WHERE BOX = "+boxSel+"");
            
            while(rs.next()){
                ltape = rs.getInt(1);
            }
            con.close();
        } catch(SQLException e){
            System.err.println(e);
        } 
        System.out.println("ltape" + ltape);
            Graphics2D g2d = (Graphics2D) g;
            g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
            g2d.setPaint(Color.black);
            g2d.setStroke(new BasicStroke(1));
            Font titleFont = new Font("Arial", Font.BOLD, 14);
            Font titleFontN = new Font("Arial", Font.PLAIN, 14);
            Font titleFontB = new Font("Arial", Font.BOLD, 21);
            int spl=13, sp = 38;
            g2d.setFont(titleFontN);
            g2d.drawString("               " + vessel, 10, sp);
            g2d.setFont(titleFontB);
            g2d.drawString("BOX " + boxSel + "   PRIMARY", 10, sp = spl + sp + 15);
            g2d.setFont(titleFont);
            g2d.drawString("                         SR     SR", 10, sp = spl + sp + 10);
            g2d.drawString("TAPE REELS: " + ftape + " : " + ltape, 10, sp = spl + sp + 5);          
            g2d.setFont(titleFontN);
            g2d.drawString("CLIENT: " + client, 10, sp = spl + sp + 15);
            g2d.drawString("JOB NUMBER: " + job_nb, 10, sp = spl + sp + 5);
            g2d.drawString("AREA: " + area, 10, sp = spl + sp + 5);
            g2d.drawString("FORMAT: RAW SEISMIC DATA", 10, sp = spl + sp + 5);
            g2d.drawString(format, 10, sp = spl + sp + 5);
            g2d.rotate(Math.toRadians(180), -100, 20 );
            sp = sp - 640;
            g2d.setFont(titleFontN);
            g2d.drawString("               " + vessel, -440, sp);
            g2d.setFont(titleFontB);
            g2d.drawString("BOX " + boxSel + "   PRIMARY", -440, sp = spl + sp + 15);
            g2d.setFont(titleFont);
            g2d.drawString("                         SR     SR", -440, sp = spl + sp + 10);
            g2d.drawString("TAPE REELS: " + ftape + " : " + ltape, -440, sp = spl + sp + 5);          
            g2d.setFont(titleFontN);
            g2d.drawString("CLIENT: " + client, -440, sp = spl + sp + 15);
            g2d.drawString("JOB NUMBER: " + job_nb, -440, sp = spl + sp + 5);
            g2d.drawString("AREA: " + area, -440, sp = spl + sp + 5);
            g2d.drawString("FORMAT: RAW SEISMIC DATA", -440, sp = spl + sp +5);
            g2d.drawString(format, -440, sp = spl + sp +5);
            return (PAGE_EXISTS);
        }
    } 
    
    private class BoxlabelPrinterC implements Printable {
        public int print(Graphics g, PageFormat pageFormat, int page){
            String boxSel = boxListView.getSelectionModel().getSelectedItem().toString();
                     System.out.println("boxSel before: " + boxSel);
                     boxSel = boxSel.substring(boxSel.lastIndexOf("x") +1, boxSel.length()).trim();
                     System.out.println("boxSel" + boxSel);  
            int ftape=0, ltape=0;
        try {
            Connection con = DriverManager.getConnection("jdbc:derby:DataBase;create=true;"); // The database URL may not be same for you, lookup the "Services" side-bar.
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT MIN(TNUMBER) from LABEL WHERE BOX = "+boxSel+"");
            System.out.println("SELECT MIN(TNUMBER) from LABEL WHERE BOX = "+boxSel+"");
            while(rs.next()){
                ftape = rs.getInt(1);          
            }
            con.close();
        } catch(SQLException e){
            System.err.println(e);
        } 
        System.out.println("ftape" + ftape);
        //------------------
        try {
            Connection con = DriverManager.getConnection("jdbc:derby:DataBase;create=true;"); // The database URL may not be same for you, lookup the "Services" side-bar.
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT MAX(TNUMBER) from LABEL WHERE BOX = "+boxSel+"");
            System.out.println("SELECT MAX(TNUMBER) from LABEL WHERE BOX = "+boxSel+"");
            
            while(rs.next()){
                ltape = rs.getInt(1);
            }
            con.close();
        } catch(SQLException e){
            System.err.println(e);
        } 
        System.out.println("ltape" + ltape);
            Graphics2D g2d = (Graphics2D) g;
            g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
            g2d.setPaint(Color.black);
            g2d.setStroke(new BasicStroke(1));
            Font titleFont = new Font("Arial", Font.BOLD, 14);
            Font titleFontN = new Font("Arial", Font.PLAIN, 14);
            Font titleFontB = new Font("Arial", Font.BOLD, 21);
            int spl=13, sp = 38;
            g2d.setFont(titleFontN);
            g2d.drawString("               " + vessel, 10, sp);
            g2d.setFont(titleFontB);
            g2d.drawString("BOX " + boxSel + "   COPY", 10, sp = spl + sp + 15);
            g2d.setFont(titleFont);
            g2d.drawString("                         SC     SC", 10, sp = spl + sp + 10);
            g2d.drawString("TAPE REELS: " + ftape + " : " + ltape, 10, sp = spl + sp + 5);          
            g2d.setFont(titleFontN);
            g2d.drawString("CLIENT: " + client, 10, sp = spl + sp + 15);
            g2d.drawString("JOB NUMBER: " + job_nb, 10, sp = spl + sp + 5);
            g2d.drawString("AREA: " + area, 10, sp = spl + sp + 5);
            g2d.drawString("FORMAT: RAW SEISMIC DATA", 10, sp = spl + sp + 5);
            g2d.drawString(format, 10, sp = spl + sp + 5);
            g2d.rotate(Math.toRadians(180), -100, 20 );
            sp = sp - 640;
            g2d.setFont(titleFontN);
            g2d.drawString("               " + vessel, -440, sp);
            g2d.setFont(titleFontB);
            g2d.drawString("BOX " + boxSel + "   COPY", -440, sp = spl + sp + 15);
            g2d.setFont(titleFont);
            g2d.drawString("                         SC     SC", -440, sp = spl + sp + 10);
            g2d.drawString("TAPE REELS: " + ftape + " : " + ltape, -440, sp = spl + sp + 5);          
            g2d.setFont(titleFontN);
            g2d.drawString("CLIENT: " + client, -440, sp = spl + sp + 15);
            g2d.drawString("JOB NUMBER: " + job_nb, -440, sp = spl + sp + 5);
            g2d.drawString("AREA: " + area, -440, sp = spl + sp + 5);
            g2d.drawString("FORMAT: RAW SEISMIC DATA", -440, sp = spl + sp +5);
            g2d.drawString(format, -440, sp = spl + sp +5);
            return (PAGE_EXISTS);
        }
    } 
    
    private class BoxlabelPrinterD implements Printable {
        public int print(Graphics g, PageFormat pageFormat, int page){
            String boxSel = boxListView.getSelectionModel().getSelectedItem().toString();
            System.out.println("boxSel before: " + boxSel);
            boxSel = boxSel.substring(boxSel.lastIndexOf("x") +1, boxSel.length()).trim();
            System.out.println("boxSel" + boxSel);     
            int ftape=0, ltape=0;
        try {
            Connection con = DriverManager.getConnection("jdbc:derby:DataBase;create=true;"); // The database URL may not be same for you, lookup the "Services" side-bar.
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT MIN(TNUMBER) from LABEL WHERE BOX = "+boxSel+"");
            System.out.println("SELECT MIN(TNUMBER) from LABEL WHERE BOX = "+boxSel+"");
            while(rs.next()){
                ftape = rs.getInt(1);
            }
            con.close();
        } catch(SQLException e){
            System.err.println(e);
        } 
        System.out.println("ftape" + ftape);
        //------------------
        try {
            Connection con = DriverManager.getConnection("jdbc:derby:DataBase;create=true;"); // The database URL may not be same for you, lookup the "Services" side-bar.
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT MAX(TNUMBER) from LABEL WHERE BOX = "+boxSel+"");
            System.out.println("SELECT MAX(TNUMBER) from LABEL WHERE BOX = "+boxSel+"");
            
            while(rs.next()){
                ltape = rs.getInt(1);
            }
            con.close();
        } catch(SQLException e){
            System.err.println(e);
        } 
        System.out.println("ltape" + ltape);
            Graphics2D g2d = (Graphics2D) g;
            g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
            g2d.setPaint(Color.black);
            g2d.setStroke(new BasicStroke(1));
            Font titleFont = new Font("Arial", Font.BOLD, 14);
            Font titleFontN = new Font("Arial", Font.PLAIN, 14);
            Font titleFontB = new Font("Arial", Font.BOLD, 21);
            int spl=13, sp = 38;
            g2d.setFont(titleFontN);
            g2d.drawString("               " + vessel, 10, sp);
            g2d.setFont(titleFontB);
            g2d.drawString("BOX " + boxSel + "   COPY", 10, sp = spl + sp + 15);
            g2d.setFont(titleFont);
            g2d.drawString("                         SD     SD", 10, sp = spl + sp + 10);
            g2d.drawString("TAPE REELS: " + ftape + " : " + ltape, 10, sp = spl + sp + 5);           
            g2d.setFont(titleFontN);
            g2d.drawString("CLIENT: " + client, 10, sp = spl + sp + 15);
            g2d.drawString("JOB NUMBER: " + job_nb, 10, sp = spl + sp + 5);
            g2d.drawString("AREA: " + area, 10, sp = spl + sp + 5);
            g2d.drawString("FORMAT:\nRAW SEISMIC DATA", 10, sp = spl + sp + 5);
            g2d.drawString(format, 10, sp = spl + sp + 5);
            g2d.rotate(Math.toRadians(180), -100, 20 );
            sp = sp - 640;
            g2d.setFont(titleFontN);
            g2d.drawString("               " + vessel, -440, sp);
            g2d.setFont(titleFontB);
            g2d.drawString("BOX " + boxSel + "   COPY", -440, sp = spl + sp + 15);
            g2d.setFont(titleFont);
            g2d.drawString("                         SD     SD", -440, sp = spl + sp + 10);
            g2d.drawString("TAPE REELS: " + ftape + " : " + ltape, -440, sp = spl + sp + 5);           
            g2d.setFont(titleFontN);
            g2d.drawString("CLIENT: " + client, -440, sp = spl + sp + 15);
            g2d.drawString("JOB NUMBER: " + job_nb, -440, sp = spl + sp + 5);
            g2d.drawString("AREA: " + area, -440, sp = spl + sp + 5);
            g2d.drawString("FORMAT: RAW SEISMIC DATA", -440, sp = spl + sp +5);
            g2d.drawString(format, -440, sp = spl + sp +5);
            return (PAGE_EXISTS);
        }
    } 
    
    private class Document2 implements Printable {
        @Override
        public int print(Graphics g, PageFormat pageFormat, int page){
            int pageL = page + 1;
            Graphics2D g2d = (Graphics2D) g;
            g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
            g2d.setPaint(Color.black);
            g2d.setStroke(new BasicStroke(1));
            Font titleFont = new Font("Arial", Font.PLAIN, 11);
            Font titleFontTb = new Font("Arial", Font.BOLD, 11);
            Font titleFontT = new Font("Arial", Font.BOLD, 20);
            int spl=20, sp = 38;
            g2d.setFont(titleFont);
            //header
            g2d.setPaint(new Color(0, 146, 207));
            g2d.fill(new Rectangle2D.Double(10, 0, 267, 10));
            g2d.setPaint(new Color(140, 198, 63));
            g2d.fill(new Rectangle2D.Double(267, 0, 267, 10));
            g2d.setPaint(new Color(251, 176, 52));
            g2d.fill(new Rectangle2D.Double(534, 0, 267, 10));
            //Fotter---
            g2d.setPaint(new Color(0, 146, 207));
            g2d.fill(new Rectangle2D.Double(10, 550, 267, 10));
            g2d.setPaint(new Color(140, 198, 63));
            g2d.fill(new Rectangle2D.Double(267, 550, 267, 10));
            g2d.setPaint(new Color(251, 176, 52));
            g2d.fill(new Rectangle2D.Double(534, 550, 267, 10));
            g2d.setPaint(Color.BLACK);
            g2d.setFont(titleFontT);
            g2d.drawString("Observer's Tape Log ", 180, sp);
            g2d.drawString("Box " + pageL, 590, sp );
            g2d.setFont(titleFont);
            g2d.drawString("Company: " + company, 30, sp = spl + sp);
            g2d.drawString("Client: " + client, 280, sp );
            g2d.drawString("Job Number: " + job_nb, 30, sp = spl + sp);
            g2d.drawString("Area: " + area, 280, sp );
            sp = spl + sp;
            try{
            Connection con = DriverManager.getConnection("jdbc:derby:DataBase;create=true;"); // The database URL may not be same for you, lookup the "Services" side-bar.
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * from LABEL where BOX="+ pageL + "ORDER BY TNUMBER");
            g2d.setFont(titleFontTb);
            g2d.drawString("Tape Number", 30, sp );
            g2d.drawString("Line Nmae", 115, sp );
            g2d.drawString("SEQ", 220, sp );
            g2d.drawString("DIR ", 280, sp );
            g2d.drawString("FSP", 335, sp );
            g2d.drawString("LSP", 385, sp );
            g2d.drawString("FF", 445, sp );
            g2d.drawString("LF", 485, sp );
            g2d.drawString("Comments", 560, sp );
            g2d.setFont(titleFont);
            sp = spl + sp;
            while(rs.next()){
                g2d.drawString(String.valueOf(rs.getInt("TNUMBER")), 50, sp );
                g2d.drawString(String.valueOf(rs.getString("LINE")), 105, sp );
                g2d.drawString(String.valueOf(rs.getInt("SEQ")), 220, sp );
                g2d.drawString(String.valueOf(rs.getInt("HEADING")), 280, sp );
                g2d.drawString(String.valueOf(rs.getInt("FSP")), 330, sp );
                g2d.drawString(String.valueOf(rs.getInt("LSP")), 380, sp );
                g2d.drawString(String.valueOf(rs.getInt("FF")),440, sp );
                g2d.drawString(String.valueOf(rs.getInt("LF")), 480, sp );
                g2d.drawString(String.valueOf(rs.getString("COMMENTS")),560, sp );
                sp = spl + sp;
            }
            con.close(); 
        } catch(SQLException e){
            System.err.println(e);
        } 
            return (PAGE_EXISTS);
        }
    }
  
    @FXML private void SettingsButtonAction(ActionEvent event) {
        settingsWindow.visibleProperty().setValue(Boolean.TRUE);
        mainWindow.visibleProperty().setValue(Boolean.FALSE);
        Vessel_TF.setText(vessel);
        Master_nb_TF.setText(String.valueOf(master_nb));
        Copy_nb_TF.setText(String.valueOf(copy_nb));
        Format_TF.setText(format);
        Job_nb_TF.setText(job_nb);
        Company_TF.setText(company);
        Client_TF.setText(client);
        printerip_TF.setText(printer_ip);
        Rec_TF.setText(rec);
        Sample_TF.setText(sample);
        Heading_i_TF.setText(heading_i);
        Heading_d_TF.setText(heading_d);
        Area_TF.setText(area);
        configureButonsSettings();
    }
        
    public void createSettingsTable(){
        try {
            Connection con = DriverManager.getConnection("jdbc:derby:DataBase;create=true;"); 
            Statement stmt = con.createStatement();
            stmt.execute( "CREATE TABLE SETTINGS (SERVER VARCHAR(50),"
                    + "PATH VARCHAR(200),"
                    + "CLIENT VARCHAR(50),"
                    + "PRINTER_IP VARCHAR(20),"
                    + "SAMPLE VARCHAR(50),"
                    + "COPY_NB SMALLINT,"
                    + "JOB VARCHAR(50),"
                    + "MASTER_NB SMALLINT,"
                    + "COMPANY VARCHAR(50),"
                    + "REC VARCHAR(50),"
                    + "AREA VARCHAR(50),"
                    + "FORMAT VARCHAR(50),"
                    + "VESSEL VARCHAR(50),"
                    + "HED_I VARCHAR(50),"
                    + "HED_D VARCHAR(50))");
            con.close(); 
        } catch(SQLException e){
            System.err.println(e);
        } 
        addline();
    }
    
    public void createLabelTable(){
        try {
            Connection con = DriverManager.getConnection("jdbc:derby:DataBase;create=true;"); 
            Statement stmt = con.createStatement();
            stmt.execute("CREATE TABLE Label (ID INT NOT NULL GENERATED ALWAYS AS IDENTITY,"
                    + "TNUMBER INTEGER,"
                    + "LINE VARCHAR(50),"
                    + "SEQ SMALLINT,"
                    + "FF INTEGER,"
                    + "LF INTEGER,"
                    + "FSP INTEGER,"
                    + "LSP INTEGER,"
                    + "DATE_ VARCHAR(50),"
                    + "HEADING FLOAT,"
                    + "BOX SMALLINT,"
                    + "COMMENTS VARCHAR(200),"
                    + "NTBP SMALLINT)");
           con.close(); 
        } catch(SQLException e){
            System.err.println(e);
        } 
    }
    
    public void addline(){
        try {
            Connection con = DriverManager.getConnection("jdbc:derby:DataBase;create=true;"); 
            Statement stmt = con.createStatement();
            stmt.execute("INSERT INTO SETTINGS (SERVER, PATH) VALUES ('localhost', 'C:\\')");
            con.close(); 
        }catch(SQLException e){
            System.err.println(e);
        } 
   }
    
    public void DBjob(){
        try {
            Connection con = DriverManager.getConnection("jdbc:derby:DataBase;create=true;"); 
            DatabaseMetaData dbm = con.getMetaData();
            ResultSet tables = dbm.getTables(null, null, "SETTINGS", null);
            if(tables.next()){
                System.out.println("Tables are present");
            }else{
                createSettingsTable();
                createLabelTable();
                System.out.println("Tables are created");
            }
            con.close(); 
        } catch(SQLException e){
            System.err.println(e);
        } 
    }
    
    private void getBox(String sBox) {
        System.out.println("getBox");
        sBox = sBox.substring(sBox.lastIndexOf("x")+1, sBox.length()).trim();
        tLog.removeAll(tLog);
        System.out.println(sBox);
        try {
            Connection con = DriverManager.getConnection("jdbc:derby:DataBase;create=true;"); // The database URL may not be same for you, lookup the "Services" side-bar.
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * from LABEL WHERE BOX = "+sBox+" ORDER BY TNUMBER");
            System.out.println("SELECT * from LABEL WHERE BOX = "+sBox+"");
            
            while(rs.next()){
                tLog.add(new tapeLogUI(rs.getInt("TNUMBER"), rs.getString("LINE"),
                        rs.getInt("SEQ"), rs.getInt("FF"), rs.getInt("LF"), rs.getInt("FSP"), rs.getInt("LSP"),
                        rs.getString("DATE_"), rs.getFloat("HEADING"), rs.getInt("BOX"), rs.getString("COMMENTS")));
            }
            con.close();
        } catch(SQLException e){
            System.err.println(e);
        } 
        Box_info.setText("Box " + sBox); 
    }
    
    private void getBoxes(){
        System.out.println("getBoxes");
        boxListView.getItems().clear();
        boxList.removeAll();
        boxNb_CB.getItems().clear();
        try {
            Connection con = DriverManager.getConnection("jdbc:derby:DataBase;create=true;"); // The database URL may not be same for you, lookup the "Services" side-bar.
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT BOX from LABEL GROUP BY BOX ORDER by BOX");
            while(rs.next()){
                    boxList.add("Box " + rs.getInt("BOX"));                
            }         
            con.close();
        } catch(SQLException e){
            System.err.println(e);
        } 
       
        boxNb_CB.setItems(boxList);
        boxListView.setItems(boxList);
        boxListView.getSelectionModel().selectLast();
        getBox(String.valueOf(getLastboxNum()));
    }
    
    private void getTape(String sTape){
    System.out.println("getTape");
        sTape = sTape.substring(sTape.lastIndexOf("e")+1, sTape.length()-1).trim();
        lineName.setText("");
        SEQ.setText("");
        tape.setText("");
        date.setText("");
        heading.setText("");
        FF.setText("");
        LF.setText("");
        FSP.setText("");
        LSP.setText("");
        Comments_.setText("");
        NTBP.setSelected(false);
        
        try {
            Connection con = DriverManager.getConnection("jdbc:derby:DataBase;create=true;");
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * from LABEL WHERE TNUMBER = "+sTape+"");
            System.out.println("SELECT * from LABEL WHERE TNUMBER = "+sTape+"");
            
            while(rs.next()){
                id = rs.getInt("ID");
                System.out.println("ID: " + id);
                lineName.setText(rs.getString("LINE"));
                SEQ.setText(String.valueOf(rs.getInt("SEQ")));
                tape.setText(String.valueOf(rs.getInt("TNUMBER")));
                date.setText(rs.getString("DATE_"));
                heading.setText(String.valueOf(rs.getFloat("HEADING")));
                FF.setText(String.valueOf(rs.getInt("FF")));
                LF.setText(String.valueOf(rs.getInt("LF")));
                FSP.setText(String.valueOf(rs.getInt("FSP")));
                LSP.setText(String.valueOf(rs.getInt("LSP")));
                Comments_.setText(rs.getString("COMMENTS"));
                boxNb_CB.setPromptText(String.valueOf(rs.getInt("BOX")));
                if(rs.getInt("NTBP")==1){
                    NTBP.setSelected(true);
                }else{
                    NTBP.setSelected(false);
                }
            }
            con.close();
        } catch(SQLException e){
            System.err.println(e);
        } 
    }
    
    private void getSettings() {
        try {
            Connection con = DriverManager.getConnection("jdbc:derby:DataBase;create=true;");
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * from SETTINGS");

            while(rs.next()){
                filePath = rs.getString("PATH");
                job_nb = rs.getString("JOB");
                company = rs.getString("COMPANY");
                client = rs.getString("CLIENT");
                printer_ip = rs.getString("PRINTER_IP");
                rec = rs.getString("REC");
                sample = rs.getString("SAMPLE");
                heading_i = rs.getString("HED_I");
                heading_d = rs.getString("HED_D");
                area = rs.getString("AREA");
                format  = rs.getString("FORMAT");
                copy_nb = rs.getInt("COPY_NB");
                master_nb = rs.getInt("MASTER_NB");
                vessel = rs.getString("VESSEL");
            }
            con.close();
        } catch(SQLException e){
            System.err.println(e);
        }  
    }

    private void setTapeLogTable() {
        Tape_c.setCellValueFactory(new PropertyValueFactory("tape"));
        LineName_c.setCellValueFactory(new PropertyValueFactory("lineName"));
        Seq_c.setCellValueFactory(new PropertyValueFactory("Seq"));
        head_c.setCellValueFactory(new PropertyValueFactory("heading"));
        FF_c.setCellValueFactory(new PropertyValueFactory("FF"));
        LF_c.setCellValueFactory(new PropertyValueFactory("LF"));
        FSP_c.setCellValueFactory(new PropertyValueFactory("FSP"));
        LSP_c.setCellValueFactory(new PropertyValueFactory("LSP"));
        date_c.setCellValueFactory(new PropertyValueFactory("Date_"));
        Comm_c.setCellValueFactory(new PropertyValueFactory("Comm"));
        TapeLogView.setItems(tLog);
    }
    
    public static class tapeLogUI {
        private  IntegerProperty tape;
        private  StringProperty lineName;
        private  IntegerProperty Seq;
        private  FloatProperty heading;
        private  StringProperty Date_;
        private  IntegerProperty FF;
        private  IntegerProperty LF;
        private  IntegerProperty FSP;
        private  IntegerProperty LSP;
        private  IntegerProperty box;
        private  StringProperty Comm;
        
        private tapeLogUI(int tape, String lineName, int seq, int FF, int LF,
                    int FSP, int LSP, String Date, float head, int box, String Comm) {
            this.tape = new SimpleIntegerProperty(tape);  
            this.lineName = new SimpleStringProperty(lineName);
            this.Seq = new SimpleIntegerProperty(seq);
            this.heading = new SimpleFloatProperty(head);
            this.Date_ = new SimpleStringProperty(Date);
            this.FF = new SimpleIntegerProperty(FF);
            this.LF = new SimpleIntegerProperty(LF);
            this.FSP = new SimpleIntegerProperty(FSP);
            this.LSP = new SimpleIntegerProperty(LSP);
            this.box = new SimpleIntegerProperty(box);
            this.Comm =new SimpleStringProperty(Comm);
        }
        public IntegerProperty tapeProperty(){return tape;}
        public StringProperty lineNameProperty(){return lineName;}
        public IntegerProperty SeqProperty(){return Seq;}
        public IntegerProperty FFProperty(){return FF;}  
        public IntegerProperty LFProperty(){return LF;}  
        public IntegerProperty FSPProperty(){return FSP;}  
        public IntegerProperty LSPProperty(){return LSP;}  
        public StringProperty Date_Property(){return Date_;}
        public FloatProperty headingProperty(){return heading;}
        public IntegerProperty boxProperty(){return box;}
        public StringProperty CommProperty(){return Comm;}
    }
}
