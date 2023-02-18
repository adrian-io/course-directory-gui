package model;

import java.io.*;
import java.util.*;

import javax.swing.table.*;

import controller.*;
import view.*;

/**
 * Die Klasse stellt das Datenmodell für die VL_Suche zur Verfügung
 * 
 * @author Adrian Scholl
 * @version 04.06.2020
 *
 */
public class VL_Suche_Model {
	
	private VL_Suche_Controller controller;
	private VL_Suche_Frame view;
	
	private TreeSet<Vorlesung> vorlesungen;
	private DefaultTableModel tableModel;
	private TableRowSorter rowSorter;
	
	private BufferedReader reader;
	
	
	/**
	 * Standard Konstrukor der das Modell direkt mit Daten füllt.
	 */
	public VL_Suche_Model() {
		initTableModel();
	}
	
	/**
	 * initializes the tablemodel and the row sorter.
	 * Imports default lectures from res/vorlesungen.txt
	 */
	public void initTableModel(){
		tableModel = new DefaultTableModel();
		
		//Filling the table(model)
		tableModel.addColumn("Vorlesung");
		tableModel.addColumn("Dozent/in");
		tableModel.addColumn("Semester");
		/*
		//For testing purposes:
        Vorlesung rep1 = new Vorlesung("Proksy", "Ratz", "SoSe2020");
        Vorlesung rep2 = new Vorlesung("Ökonometrie", "Schienle", "SoSe2020");
        Vorlesung vl3 = new Vorlesung("gfewsa", "gfdews", "SoSe20");
        Vorlesung vl4 = new Vorlesung("Mathe", "Winter", "WiSe20");

        vorlesungen = new TreeSet<Vorlesung>();
        vorlesungen.add(rep1);
        vorlesungen.add(rep2);
        vorlesungen.add(vl3);
        vorlesungen.add(vl4);
        */
		
		//import lectures from file: vorlesungen.txt
		try {
			importLecturesFromFile(new File("res/vorlesungen.txt"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	   	//set filter with row sorter
        rowSorter = new TableRowSorter<DefaultTableModel>(tableModel); 
    }
	
	/**
	 * Imports data from given file to the vorlesungen treeSet and the tableModel
	 * 
	 * @param file file to be imported
	 * @throws IOException possibly occuring ioexception
	 * @throws Exception possibly occuring other exceptions
	 */
	public void importLecturesFromFile(File file) throws IOException, Exception{
		//file = new File("vorlesungen.txt");
		//neues tree set für vorlesungen
		vorlesungen = new TreeSet<Vorlesung>();
		//tablemodel leeren
		getTableModel().setRowCount(0);
		reader = new BufferedReader(new FileReader(file));
		//Ignore header = first row in file
		String line = reader.readLine();
		//Alle Zeilen auslesen
		while((line = reader.readLine()) != null) {
			String[] attr = line.split("\t");
			//tree set füllen
			Vorlesung vl = new Vorlesung(attr[0], attr[1], attr[2]);
			vorlesungen.add(vl);
			//table model füllen
			Vector<Object> row = new Vector<Object>();
            row.add(vl.getName());
            row.add(vl.getProf());
            row.add(vl.getSemester());
            getTableModel().addRow(row);
		}
	}

	/**
	 * @return the controller
	 */
	public VL_Suche_Controller getController() {
		return controller;
	}

	/**
	 * @param controller the controller to set
	 */
	public void setController(VL_Suche_Controller controller) {
		this.controller = controller;
	}

	/**
	 * @return the view
	 */
	public VL_Suche_Frame getView() {
		return view;
	}

	/**
	 * @param view the view to set
	 */
	public void setView(VL_Suche_Frame view) {
		this.view = view;
	}

	/**
	 * @return the vorlesungen
	 */
	public TreeSet<Vorlesung> getVorlesungen() {
		return vorlesungen;
	}

	/**
	 * @param vorlesungen the vorlesungen to set
	 */
	public void setVorlesungen(TreeSet<Vorlesung> vorlesungen) {
		this.vorlesungen = vorlesungen;
	}

	/**
	 * @return the tableModel
	 */
	public DefaultTableModel getTableModel() {
		return tableModel;
	}

	/**
	 * @param tableModel the tableModel to set
	 */
	public void setTableModel(DefaultTableModel tableModel) {
		this.tableModel = tableModel;
		view.getVorlesungsTabelle().setModel(tableModel);
	}

	/**
	 * @return the rowSorter
	 */
	public TableRowSorter getRowSorter() {
		return rowSorter;
	}

	/**
	 * @param rowSorter the rowSorter to set
	 */
	public void setRowSorter(TableRowSorter rowSorter) {
		this.rowSorter = rowSorter;
	}

}
