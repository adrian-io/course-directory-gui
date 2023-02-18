package controller;

import java.awt.Desktop;
import java.awt.event.*;
import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;
import java.util.Vector;
import java.util.regex.PatternSyntaxException;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.DefaultTableModel;

import model.VL_Suche_Model;
import model.Vorlesung;
import view.*;

/**
 * Stellt alle Listener Klassen für die VL_Suche bereit.
 * @author Adrian Scholl
 * @version 03.06.2020
 *
 */
public class VL_Suche_Controller {
	
	private VL_Suche_Frame view;
	private VL_Suche_Model model;

	private JFileChooser fileChooser;
	private FileWriter fileWriter;
	private BufferedReader reader;
	
	/**
	 * Spezifiziert ActionListener für alle Button im Frame 
	 * mit entprechender Funktionalität: Exportieren, Löschen, Hinzufügen
	 * 
	 * @author Adrian Scholl
	 * @version 03.06.2020
	 *
	 */
	class ButtonListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent ae) {
			//reaction depending on event source
			String actionCommand = ae.getActionCommand();
			System.out.println("Action com: "+actionCommand);
			switch(actionCommand) {
				case "export":
					fileChooser = new JFileChooser();
					int returnVal = fileChooser.showSaveDialog(view);
					//if save button was pressed:
					if(returnVal == JFileChooser.APPROVE_OPTION) {
						String fileName = fileChooser.getSelectedFile().getName();
						//prevent double file endings like: filename.txt.txt
						try {
							if(fileName.contains(".txt")) {
								fileWriter = new FileWriter(fileChooser.getSelectedFile());
							}
							else {
								fileWriter = new FileWriter(fileChooser.getSelectedFile() + ".txt");
							}
						}
						catch(IOException ioe) {
							JOptionPane.showMessageDialog(view, "Fehler beim Speichern. Wählen Sie einen anderen Speicherort.");
						}
						try(BufferedWriter bufferedWriter = new BufferedWriter(fileWriter)){
							bufferedWriter.write("NAME" + "\t" + "PROFESSOR" + "\t" + "SEMESTER" + "\n");
							for(Vorlesung vl : model.getVorlesungen()) {
								bufferedWriter.write(vl.getName() + "\t" + vl.getProf() + "\t" + vl.getSemester() +"\n");
							}
							bufferedWriter.flush();
							JOptionPane.showMessageDialog(view, "Vorlesungen erfolgreich exportiert.", "Export erfolgreich", JOptionPane.INFORMATION_MESSAGE);
						}
						catch(IOException ioe) {
							ioe.printStackTrace();
							JOptionPane.showMessageDialog(view, "Fehler beim Speichern. Wählen Sie einen anderen Speicherort.", "Fehler beim Speichern", JOptionPane.ERROR_MESSAGE);
						}
						catch(Exception e) {
							e.printStackTrace();
							JOptionPane.showMessageDialog(view, "Fehler beim Speichern. Wählen Sie einen anderen Speicherort.", "Fehler beim Speichern", JOptionPane.ERROR_MESSAGE);

						}
					}
					break;
				case "import":
						fileChooser = new JFileChooser();
						returnVal = fileChooser.showOpenDialog(view);
						if(returnVal == JFileChooser.APPROVE_OPTION) {
							try{
								model.importLecturesFromFile(fileChooser.getSelectedFile());
								JOptionPane.showMessageDialog(view, "Datei erfolgreich importiert", "Import erfolgreich", JOptionPane.INFORMATION_MESSAGE);
							}
							catch(IOException ioe) {
								JOptionPane.showMessageDialog(view, "Fehler beim Importieren. Wählen Sie eine passende Datei.", "Fehler beim Importieren", JOptionPane.ERROR_MESSAGE);
							}
							catch(Exception e) {
								JOptionPane.showMessageDialog(view, "Fehler beim Importieren. Wählen Sie eine passende Datei.", "Fehler beim Importieren", JOptionPane.ERROR_MESSAGE);
							}
						}
					break;
					
				case "new lecture":
					//get content of textfields
					String inputName = view.getNameInputField().getText();
					String inputProf = view.getDozentInputField().getText();
					String inputSemester = view.getSemesterInputField().getText();
					Vorlesung newLecture = new Vorlesung(inputName, inputProf, inputSemester);
					//check if lecture already exists
					if(!model.getVorlesungen().contains(newLecture)){
						//add to TreeSet
						model.getVorlesungen().add(newLecture);
						System.out.println(newLecture + " was added to treeSet");
						//Important: update FilterBoxes first, then adjust table model!
						//otherwise: concurrent threads --> data inconsistency
			            view.updateFilterBoxes();
						//add as new row to tableModel
			            Vector<String> row = new Vector<String>();
			            row.add(newLecture.getName());
			            row.add(newLecture.getProf());
			            row.add(newLecture.getSemester());
			            model.getTableModel().addRow(row);
			            System.out.println("Vorlesungen +1:"+model.getVorlesungen());
			            //model.setVorlesungen(view.getVorlesungen());
					}
					else {
			            JOptionPane.showMessageDialog(view, "Diese Vorlesung existiert bereits.", "Fehler beim Hinzufügen", JOptionPane.ERROR_MESSAGE);
					}
					break;
					
				case "delete lecture":
	               int rowNumber = view.getVorlesungsTabelle().getSelectedRow();
	               //coverts visible row index too actual row index in table mode
	               System.out.println("#################rowNumbe"+rowNumber);
	               int highestRow = view.getVorlesungsTabelle().getRowCount()-1;
	               //Auswahl unzulässig rowNumber = -1
		           if(rowNumber != -1) {
		        	   int modelRowNumber = view.getVorlesungsTabelle().convertRowIndexToModel(rowNumber);
		               System.out.println("#################modelRowNumber"+modelRowNumber);
		               //find deleted value in TreeSet vorlesungen
		               //System.out.println("-----------Search vl----------- row Number:"+rowNumber);
		               Iterator<Vorlesung> lectureIterator = model.getVorlesungen().iterator();
		               boolean loop = true;
		               while(lectureIterator.hasNext() && loop) {
			               Vorlesung vl = lectureIterator.next();
		            	   //System.out.println(vl);
			               String name = (String) model.getTableModel().getValueAt(modelRowNumber, 0);
			               String prof = (String) model.getTableModel().getValueAt(modelRowNumber, 1);
			               String semester = (String) model.getTableModel().getValueAt(modelRowNumber, 2);
			               //System.out.println(name+prof+semester);
			               if(vl.getName() == name && vl.getProf() == prof && vl.getSemester() == semester) {
				               //remove lecture from lecture TreeSet
			            	   model.getVorlesungen().remove(vl);
					           System.out.println("Vorlesungen -1:"+model.getVorlesungen());
			            	   System.out.println("Removed from TreeSet:"+vl);
			            	   //Important: update FilterBoxes first, then adjust table model!
			            	   //otherwise: concurrent threads --> data inconsistency
				               view.updateFilterBoxes();
				               //remove selected row from the model
				               model.getTableModel().removeRow(modelRowNumber);
				               JOptionPane.showMessageDialog(view, "Vorlesung \"" + vl.getName() + "\" 	wurde gelöscht.", "Löschen erfolgreich", JOptionPane.INFORMATION_MESSAGE);
			            	   //stop loop when lecture found and removed
			            	   loop = false;
			               }
		               }
		            }
					break;
				case "openKitWebsite":
					try {
				        Desktop.getDesktop().browse(new URL("https://campus.studium.kit.edu/events/catalog.php#!campus/all/fields.asp?group=Vorlesungsverzeichnis").toURI());
				    } catch (Exception e) {
				        e.printStackTrace();
				    }
					break;
				case "showTable":
					for(JPanel panel : view.getViewPanels()) {
						panel.setVisible(false);
					}
					view.getTablePanel().setVisible(true);
					System.out.println("table");
					break;
				case "showExport":
					for(JPanel panel : view.getViewPanels()) {
						panel.setVisible(false);
					}
					view.getExportPanel().setVisible(true);
					System.out.println("Export");
					break;
				case "showImport":
					for(JPanel panel : view.getViewPanels()) {
						panel.setVisible(false);
					}
					view.getImportPanel().setVisible(true);	
					System.out.println("Import");
					break;
				case "showHelp":
					for(JPanel panel : view.getViewPanels()) {
						panel.setVisible(false);
					}
					view.getHelpPanel().setVisible(true);
					System.out.println("Helpsdf");
					break;
				default:
					break;
			}
		}
	}
	/**
	 * Listener, der auf die Auswahl einer Reihe per Mausklick oder Tastatur in der Tabelle reagiert
	 * 
	 * @author Adrian Scholl
	 * @version 05.06.2020
	 *
	 */
	class RowSelectionListener implements ListSelectionListener{
        public void valueChanged(ListSelectionEvent event) {
        	int row = view.getVorlesungsTabelle().getSelectedRow();
        	//row = view.getVorlesungsTabelle().convertColumnIndexToModel(row);
        	if(row != -1) {
            	String name = view.getVorlesungsTabelle().getValueAt(row, 0).toString();
            	String prof = view.getVorlesungsTabelle().getValueAt(row, 1).toString();
            	String semester = view.getVorlesungsTabelle().getValueAt(row, 2).toString();
                view.getDeleteLabel().setText("<html><b>Gewählte Vorlesung:</b><br><br> [" + name + " | " + prof + " | "+ semester + "]</html>");
        	}
        	else {
                view.getDeleteLabel().setText("<html>Wählen Sie die zu löschende <br> Vorlesung in der Tabelle.</html>");
        	}
        }
    }
	
	/**
	 * Listener für die ComboBoxen, der den Filter der Tabelle auf die Auswahl in den ComboBoxen anpasst
	 * 
	 * @author Adrian Scholl
	 * @version 04.06.2020
	 * 
	 */
	class ComboBoxListener implements ItemListener{
		@Override
		public void itemStateChanged(ItemEvent e) {
			System.out.println("update");
			newFilter();
		}
	}
	
	/**
	 * Listener für das TextFeld zum Filtern der Tabelle
	 * 
	 * @author Adrian Scholl
	 * @version 04.06.2020
	 *
	 */
	class TextFieldListener implements DocumentListener{
   		@Override
		public void insertUpdate(DocumentEvent e) {
			// TODO Auto-generated method stub
			newFilter();
			System.out.println("update");
		}
		@Override
		public void removeUpdate(DocumentEvent e) {
			// TODO Auto-generated method stub
			newFilter();
			System.out.println("update");
		}
		@Override
		public void changedUpdate(DocumentEvent e) {
			// TODO Auto-generated method stub
			newFilter();
			System.out.println("update");
		}
	}
	/**
	 * 
	 * 
	 * @author Adrian Scholl
	 * @version 05.06.2020
	 *
	 */
	class FocusListener extends WindowAdapter{
	    public void windowGainedFocus(WindowEvent e) {
	        view.getSearchField().requestFocusInWindow();
	    }
	}

	/**
	 * Fragt das Filter-Textfeld und die Filter-CheckBoxen ab und 
	 * erzeugt dementsprechend 0-3 neue Filter für die Vorlesungstabelle
	 */
	private void newFilter() {
		String textFilter = view.getSearchField().getText();
		String profFilter = (String) view.getProfBox().getSelectedItem();
		String semesterFilter = (String) view.getSemesterBox().getSelectedItem();
		
		System.out.println("------------------NEW FILTER----------------");
	    if (textFilter.length() == 0 && profFilter == "Alle Dozenten" && semesterFilter == "Alle Semester") {
	    	System.out.println("KEIN FILTER");
	    	model.getRowSorter().setRowFilter(null);
	    }
	    else {
	        try {
	        	List<RowFilter<Object,Object>> filters = new ArrayList<RowFilter<Object,Object>>();
				if(textFilter.length() != 0){
	        		filters.add(RowFilter.regexFilter("(?i)" + textFilter));
	        		System.out.println("TextFilter: (?i)"+textFilter);
	        	}
				if(profFilter != "Alle Dozenten" && profFilter != null){
					filters.add(RowFilter.regexFilter(profFilter));
	        		System.out.println("profFilter: "+profFilter);
				}
				if(semesterFilter != "Alle Semester"  && semesterFilter != null){
					filters.add(RowFilter.regexFilter(semesterFilter));
	        		System.out.println("SemsterFilter: "+semesterFilter);
				}
				model.getRowSorter().setRowFilter(RowFilter.andFilter(filters));
		        view.getVorlesungsTabelle().setRowSorter(model.getRowSorter());
	        }
	        catch (PatternSyntaxException pse) {
	            JOptionPane.showMessageDialog(view, "Kein gültiger regulärer Ausdruck!", "Fehler im Suchfeld", JOptionPane.ERROR_MESSAGE);
	        }
	        System.out.println("----------------------------------------");
	    }
	}

	/**
	 * @return the model
	 */
	public VL_Suche_Model getModel() {
		return model;
	}

	/**
	 * @param model the model to set
	 */
	public void setModel(VL_Suche_Model model) {
		this.model = model;
	}
	
	/**
	 * @param frame frame to set
	 */
	public void setView(VL_Suche_Frame frame) {
		this.view = frame;
	}

	/**
	 * Gibt einen RowSelectionListener zurück
	 * @return ButtonListener RowSelectionListener für die Tabelle
	 */
	public RowSelectionListener getRowSelectionListener() {
		return new RowSelectionListener();
	}
	
	/**
	 * Gibt einen ButtonListener zurück
	 * @return ButtonListener ActionListener für alle JButtons im Frame
	 */
	public ButtonListener getButtonListener() {
		return new ButtonListener();
	}
	
	/**
	 * Gibt einen neu erzeugten ButtonListener zurück
	 * @return ButtonListener ActionListener für alle JButtons im Frame
	 */
	public TextFieldListener getTextFieldListener() {
		return new TextFieldListener();
	}
	
	/**
	 * Gibt einen neu erzeugten ComboBoxListener zurück
	 * @return ComboBoxListener für alle ComboBoxes im Frame
	 */
	public ComboBoxListener getComboBoxListener() {
		return new ComboBoxListener();
	}
	

	/**
	 * Gibt einen neu erzeugten FocusListener zurück
	 * @return FocusListener FocusListener für den Hauptframe
	 */
	public FocusListener getFocusListener() {
		return new FocusListener();
	}
	

}
