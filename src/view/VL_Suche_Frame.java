package view;

import model.*;

import javax.imageio.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.table.*;

import com.formdev.flatlaf.*;

import controller.VL_Suche_Controller;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.regex.PatternSyntaxException;


/**
 * Spezifiziert das Fenster in dem die VL_Suche angezeigt wird.
 * 
 * @author Adrian Scholl
 * @version 01.06.2020
 *
 */
public class VL_Suche_Frame extends JFrame{

	private VL_Suche_Controller controller;
	private VL_Suche_Model model;

	//Frame
	private int windowHeight, windowWidth;
	private BufferedImage kitIcon;
	private BufferedImage kitButtonIcon;
	private Container contentPane;
	
	private TreeSet<Vorlesung> vorlesungen;
	
	//Side menu
	private JPanel sideMenu;
	private JButton kitLogoButton; 
	private JButton menuButtonVorlesungen;
	private JButton menuButtonExport;
	private JButton menuButtonImport; 
	private JButton menuButtonHelp;
	
	//Content Panel
	private ArrayList<JPanel> viewPanels;
	private JPanel content;
	
	//Table Panel
	private JPanel tablePanel;
	private JLabel tableHeader;
	private JSeparator tableSeparator;
		//addLecturePanel
		private JPanel addLecturePanel;
		private JLabel nameInputLabel;
		private JLabel dozentInputLabel;
		private JLabel semesterInputLabel;
		private JTextField nameInputField;
		private JTextField dozentInputField;
		private JTextField semesterInputField;
		private JButton addLectureButton;
		//deleteLecturePanel
		private JPanel deleteLecturePanel;	
		private JButton deleteButton;
		private JLabel deleteLabel;
		//searchPanel
		private JPanel searchPanel;
		private JLabel searchFieldLabel;
		private JTextField searchField;
		private JComboBox<String> semesterBox;
		private JComboBox<String> profBox;
		//TableScrollPane
		private JScrollPane tableScrollPane;
		private JTable vorlesungsTabelle ;
	
	//Export panel
	private JPanel exportPanel;
	private JSeparator exportSeparator;
	private JButton exportButton;
	private JLabel exportHeader;
	
	//Import Panel
	private JPanel importPanel;	
	private JSeparator importSeparator;
	private JLabel importHeader;
	private JButton importButton;
	
	//Help Panel
	private JPanel helpPanel;
	private JSeparator helpSeparator;
	private JLabel helpHeader;
	private JLabel helpLabel;

	
	/**
	 * Standardkonstruktor für den VL_Suche_Frame
	 */
	public VL_Suche_Frame() {
		//Set Theme and Icon
		try {
			UIManager.setLookAndFeel(new FlatDarkLaf());
			kitIcon = ImageIO.read(new File("res/kit_logo_white_on_black.png"));
			setIconImage(kitIcon);
		} 
		catch(Exception ex) {
		    System.err.println("Laden von FlatDark Theme oder KIT Logo fehlgeschlagen.");
		}
		
		//Frame settings
		windowHeight = 650;
		windowWidth = 800;
		setSize(windowWidth, windowHeight);
		setLocationRelativeTo(null);
		setTitle("Vorlesungsverzeichnis");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		contentPane = this.getContentPane();
		contentPane.setLayout(new BorderLayout());
		
		//Init components
		initMenu();
		
		//Panel für Content auf rechter Seite
		content = new JPanel(new FlowLayout());
		viewPanels = new ArrayList<JPanel>();

		initTablePanel();
		initExportPanel();
		initImportPanel();
		initHelpPanel();
		
		//Add all panels to view panels 
		for(JPanel panel : viewPanels) {
			content.add(panel);
		}
		contentPane.add(content);
	}
	
	private void initMenu() {
		//Panel für side menu auf linker Seite
		sideMenu = new JPanel();
		sideMenu.setLayout(new FlowLayout());
		sideMenu.setBorder(new EmptyBorder(5, 5, 5, 5));
		sideMenu.setPreferredSize(new Dimension(200, 100));
			
		kitLogoButton = new JButton("KIT");
		try {
			kitButtonIcon = ImageIO.read(new File("res/kit_logo_V2_de.png"));
			kitLogoButton = new JButton(new ImageIcon(kitButtonIcon.getScaledInstance(180, 90, Image.SCALE_SMOOTH)));
			kitLogoButton.setBackground(Color.WHITE);
			kitLogoButton.setOpaque(false);
		} 
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		kitLogoButton.setActionCommand("openKitWebsite");
		menuButtonVorlesungen = new JButton("Vorlesungen");
		menuButtonVorlesungen.setActionCommand("showTable");
		menuButtonExport = new JButton("Import");
		menuButtonExport.setActionCommand("showImport");
		menuButtonImport = new JButton("Export");
		menuButtonImport.setActionCommand("showExport");
		menuButtonHelp = new JButton("Hilfe");
		menuButtonHelp.setActionCommand("showHelp");
		
		//menu button fix size
		kitLogoButton.setPreferredSize(new Dimension(190, 95));
		menuButtonVorlesungen.setPreferredSize(new Dimension(190, 50));
		menuButtonExport.setPreferredSize(new Dimension(190, 50));
		menuButtonImport.setPreferredSize(new Dimension(190, 50));
		menuButtonHelp.setPreferredSize(new Dimension(190, 50));

		sideMenu.add(kitLogoButton);
		sideMenu.add(menuButtonVorlesungen);
		sideMenu.add(menuButtonExport);
		sideMenu.add(menuButtonImport);
		sideMenu.add(menuButtonHelp);
		contentPane.add(sideMenu, BorderLayout.WEST);
	}
	
	private void initTablePanel() {
		//tableHeader
		tableHeader = new JLabel("Vorlesungen");
		tableHeader.setHorizontalAlignment(SwingConstants.LEFT);
		tableHeader.setFont(new Font("Verdana", Font.ITALIC, 40));
		
		//Separator
		tableSeparator = new JSeparator(SwingConstants.HORIZONTAL);
		
		//addLecturePanel
		addLecturePanel = new JPanel(new GridBagLayout());
		//GridLayout(4, 2, 5, 10));
		addLecturePanel.setBorder(new EmptyBorder(10, 10, 10, 10));
		//addLecturePanel.setBackground(Color.CYAN);
		addLecturePanel.setBorder(new TitledBorder(null, "Vorlesung hinzufügen", TitledBorder.LEADING, TitledBorder.TOP,
				new Font("Verdana", Font.BOLD , 12)));
		nameInputLabel = new JLabel("Name:");
		nameInputLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		dozentInputLabel = new JLabel("Dozent/in:");
		dozentInputLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		semesterInputLabel = new JLabel("Semester:");
		semesterInputLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		nameInputField = new JTextField(15);
		dozentInputField = new JTextField(15);
		semesterInputField = new JTextField(15);
		addLectureButton = new JButton("Hinzufügen");
		addLectureButton.setActionCommand("new lecture");
		
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.BOTH;
		gbc.insets = new Insets(5, 5, 5, 5);
		gbc.gridx = 0;
		gbc.gridy = 0;
		addLecturePanel.add(nameInputLabel, gbc);	
		gbc.gridx = 1;
		gbc.gridy = 0;
		addLecturePanel.add(nameInputField, gbc);
		gbc.gridx = 0;
		gbc.gridy = 1;	
		addLecturePanel.add(dozentInputLabel, gbc);
		gbc.gridx = 1;
		gbc.gridy = 1;
		addLecturePanel.add(dozentInputField, gbc);
		gbc.gridx = 0;
		gbc.gridy = 2;
		addLecturePanel.add(semesterInputLabel, gbc);
		gbc.gridx = 1;
		gbc.gridy = 2;
		addLecturePanel.add(semesterInputField, gbc);
		gbc.gridx = 0;
		gbc.gridy = 3;		
		gbc.gridwidth = 2;
		addLecturePanel.add(addLectureButton, gbc);
		
		//SearchPanel
		searchPanel = new JPanel(new GridBagLayout());
		//searchPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
		//searchPanel.setBackground(Color.GREEN);
		searchPanel.setBorder(new TitledBorder(null, "Vorlesung suchen", TitledBorder.LEADING, TitledBorder.TOP, 
				new Font("Verdana", Font.BOLD , 12)));
		searchFieldLabel = new JLabel("Suchbegriff:");
		searchFieldLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		searchField = new JTextField(12);
		profBox = new JComboBox<String>();
		semesterBox = new JComboBox<String>();

		gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.BOTH;
		gbc.insets = new Insets(5, 5, 5, 5);	
		gbc.gridwidth = 1;
		gbc.gridx = 0;
		gbc.gridy = 0;
		searchPanel.add(searchFieldLabel, gbc);
		gbc.gridx = 1;
		gbc.gridy = 0;
		searchPanel.add(searchField, gbc);
		gbc.gridx = 2;
		gbc.gridy = 0;
		searchPanel.add(profBox, gbc);
		gbc.gridx = 3;
		gbc.gridy = 0;
		searchPanel.add(semesterBox, gbc);
		
		//deletePanel
		deleteLecturePanel = new JPanel(new GridBagLayout());	
		//deleteLecturePanel.setBackground(Color.blue);
		deleteLecturePanel.setBorder(new TitledBorder(null, "Vorlesung löschen", TitledBorder.LEADING, TitledBorder.TOP,
				new Font("Verdana", Font.BOLD , 12)));
		deleteLabel = new JLabel("<html>Wählen Sie die zu löschende <br> Vorlesung in der Tabelle.</html>");
		deleteLabel.setHorizontalAlignment(SwingConstants.CENTER);
		deleteButton = new JButton("Entfernen");
		deleteButton.setActionCommand("delete lecture");
		
		gbc = new GridBagConstraints();
		gbc.insets = new Insets(5, 5, 5, 5);
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 1;
		gbc.weighty = 1;
		deleteLecturePanel.add(deleteLabel, gbc);
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.weightx = 1;
		gbc.weighty = 0;
		deleteLecturePanel.add(deleteButton, gbc);

		//Tabelle
	   	vorlesungsTabelle = new JTable();	
	   	//disable mutiple selection
	   	vorlesungsTabelle.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	   	//enable row selection
	   	vorlesungsTabelle.setRowSelectionAllowed(true);
	   	//disable cell editing
	   	vorlesungsTabelle.setDefaultEditor(Object.class, null);
	   	//set tool tip for table header
	   	vorlesungsTabelle.getTableHeader().setToolTipText("Zum Sortieren klicken");
	   	//Enable auto sorting
		vorlesungsTabelle.setAutoCreateRowSorter(true);

		//vorlesungsTabelle.setBackground(new Color(100, 100, 100));
		//vorlesungsTabelle.setForeground(Color.WHITE);

		//Scroll Pane für Tabelle
		tableScrollPane = new JScrollPane(vorlesungsTabelle);
		tableScrollPane.setPreferredSize(new Dimension(557, 285));
		
		//Table Panel mit GridBagLayout
		tablePanel = new JPanel(new GridBagLayout());
		gbc = new GridBagConstraints();
		tablePanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		gbc.fill = GridBagConstraints.BOTH;
		gbc.insets = new Insets(5, 5, 5, 5);
		gbc.gridx = 0; 
		gbc.gridy = 0;
		gbc.gridwidth = 2;
		tablePanel.add(tableHeader, gbc);
		gbc.gridx = 0; 
		gbc.gridy = 1;
		gbc.gridwidth = 2;
		gbc.weighty = 1;
		tablePanel.add(tableSeparator, gbc);
		gbc.gridx = 0; 
		gbc.gridy = 2;
		gbc.gridwidth = 2;
		tablePanel.add(searchPanel, gbc);
		gbc.gridx = 0; 
		gbc.gridy = 3;
		gbc.gridwidth = 1;
		gbc.weighty = 1;
		tablePanel.add(addLecturePanel, gbc);
		gbc.gridx = 1;
		gbc.gridy = 3;
		gbc.gridwidth = 1;
		tablePanel.add(deleteLecturePanel, gbc);	
		gbc.gridx = 0; 
		gbc.gridy = 4;
		gbc.gridwidth = 2;
		tablePanel.add(tableScrollPane, gbc);
		
		viewPanels.add(tablePanel);
	}	
	
	private void initExportPanel() {
		
		exportHeader = new JLabel("Datei exportieren");
		exportHeader.setFont(new Font("Verdana", Font.ITALIC, 40));	
		exportHeader.setHorizontalAlignment(SwingConstants.LEFT);
		exportHeader.setPreferredSize(new Dimension(550, 40));

		//Separator
		exportSeparator = new JSeparator(SwingConstants.HORIZONTAL);
				
		exportButton = new JButton("Speicherort auswählen");
		exportButton.setActionCommand("export");
		
		exportPanel = new JPanel(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		exportPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		gbc.fill = GridBagConstraints.BOTH;
		gbc.insets = new Insets(5, 5, 5, 5);
		gbc.gridx = 0; 
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.weightx = 1;
		exportPanel.add(exportHeader, gbc);
		gbc.gridx = 0; 
		gbc.gridy = 1;
		gbc.gridwidth = 1;
		exportPanel.add(exportSeparator, gbc);
		gbc.gridx = 0; 
		gbc.gridy = 2;
		gbc.gridwidth = 1;
		exportPanel.add(exportButton, gbc);
		//exportPanel.setPreferredSize(new Dimension(500, 300));
		//exportPanel.setBackground(Color.red);
		
		//exportPanel.add(exportFileChooser);
		
		//Add export panel to Panel with content
		exportPanel.setVisible(false);
		viewPanels.add(exportPanel);
	}
	
	private void initImportPanel() {
		//Import Panel mit Überschrift
		importPanel = new JPanel(new GridBagLayout());
		importPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
		
		importHeader = new JLabel("Datei importieren");
		importHeader.setHorizontalAlignment(SwingConstants.LEFT);
		importHeader.setFont(new Font("Verdana", Font.ITALIC, 40));	
		importHeader.setPreferredSize(new Dimension(550, 40));
		
		//Separator
		importSeparator = new JSeparator(SwingConstants.HORIZONTAL);
				
		importButton = new JButton("Datei auswählen");
		importButton.setActionCommand("import");

		importPanel = new JPanel(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		importPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		gbc.fill = GridBagConstraints.BOTH;
		gbc.insets = new Insets(5, 5, 5, 5);
		gbc.gridx = 0; 
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.weightx = 1;
		importPanel.add(importHeader, gbc);
		gbc.gridx = 0; 
		gbc.gridy = 1;
		gbc.gridwidth = 1;
		importPanel.add(importSeparator, gbc);
		gbc.gridx = 0; 
		gbc.gridy = 2;
		gbc.gridwidth = 1;
		importPanel.add(importButton, gbc);

		
		//Add export panel to Panel with content
		importPanel.setVisible(false);
		viewPanels.add(importPanel);
	}
	
	private void initHelpPanel() {
		//Panel für Tabelle mit Überschrift
		helpPanel = new JPanel(new GridBagLayout());
		helpPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

		helpHeader = new JLabel("Hilfe");
		helpHeader.setHorizontalAlignment(SwingConstants.LEFT);
		helpHeader.setFont(new Font("Verdana", Font.ITALIC, 40));
		helpHeader.setPreferredSize(new Dimension(550, 40));
		
		//Separator
		helpSeparator = new JSeparator(SwingConstants.HORIZONTAL);
		
		helpLabel = new JLabel("<html>"
				+ "<h3>Importieren:</h3>"
				+ "Es können nur Textdateien importiert werden. Die zu importierende Datei muss einen Header besitzen.<br>"
				+ "Die Vorlesungsdaten müssen in jeweils drei Spalten vorliegen, die von jeweils einem Tab getrennt sind.<br><br><br>"
				+ "<h3>Exportieren:</h3>"
				+ "Beim Exportieren werden die Vorlesungen in eine Textdatei geschrieben.<br>"
				+ "Die Datei besitzt einen Header mit der Beschreibung der Spalten: \"NAME\tPROFESSOR\tSEMESTER\"<br>"
				+ "Die Vorlesungsdaten werden in jeweils eine Reihe geschrieben.<br>"
				+ "Name, Professor und Semester werden jeweils von ein Tab getrennt"
				+ "</html>");

		helpPanel = new JPanel(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		helpPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		gbc.fill = GridBagConstraints.BOTH;
		gbc.insets = new Insets(5, 5, 5, 5);
		gbc.gridx = 0; 
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.weightx = 1;
		helpPanel.add(helpHeader, gbc);
		gbc.gridx = 0; 
		gbc.gridy = 1;
		gbc.gridwidth = 1;
		helpPanel.add(helpSeparator, gbc);
		gbc.gridx = 0; 
		gbc.gridy = 2;
		gbc.gridwidth = 1;
		helpPanel.add(helpLabel, gbc);
		//Add export panel to Panel with content
		helpPanel.setVisible(false);
		viewPanels.add(helpPanel);
	}

	/**
	 * Sorgt dafür, dass die CheckBoxen mit allen aktuell in vorlesungen gespeicherten Professoren und Semstern gefüllt sind.
	 */
	public void updateFilterBoxes() {
		System.out.println("VIEW VL:"+vorlesungen);
		
		//get tree set of all semesters and profs
		semesterBox.removeAllItems();
		TreeSet<String> semester = new TreeSet<String>();
		profBox.removeAllItems();
		TreeSet<String> profs = new TreeSet<String>();
		
		for(Vorlesung vl : model.getVorlesungen()) {
			semester.add(vl.getSemester());
			profs.add(vl.getProf());
			System.out.println("-------------"+vl.getProf());
			System.out.println("-------------"+vl.getSemester());
		}
		
		//update semster combo box
		semesterBox.addItem("Alle Semester");
		for(String sem : semester) {
			semesterBox.addItem(sem);
			System.out.println("++++++"+sem);
		}
		//update prof combo box
		profBox.addItem("Alle Dozenten");
		for(String prof : profs) {
			profBox.addItem(prof);
			System.out.println("++++++"+prof);
		}
		//System.out.println("semesterBox updated:"+semester);
		//System.out.println("profBox updated:"+profs);
	}
	
	
	//Getter- und Setter Methoden
	/**
	 * 
	 * @param vorlesungen jhgfds
	 */
	public void setVorlesungen(TreeSet<Vorlesung> vorlesungen) {
		this.vorlesungen = vorlesungen;
	}
	/**
	 * @return the searchField
	 */
	public JTextField getSearchField() {
		return searchField;
	}

	/**
	 * @return the vorlesungen
	 */
	public TreeSet<Vorlesung> getVorlesungen() {
		return vorlesungen;
	}

	/**
	 * @return the nameInputField
	 */
	public JTextField getNameInputField() {
		return nameInputField;
	}

	/**
	 * @return the dozentInputField
	 */
	public JTextField getDozentInputField() {
		return dozentInputField;
	}

	/**
	 * @return the semesterInputField
	 */
	public JTextField getSemesterInputField() {
		return semesterInputField;
	}
	/**
	 * @param searchField the searchField to set
	 */
	public void setSearchField(JTextField searchField) {
		this.searchField = searchField;
	}

	/**
	 * @param nameInputField the nameInputField to set
	 */
	public void setNameInputField(JTextField nameInputField) {
		this.nameInputField = nameInputField;
	}

	/**
	 * @param dozentInputField the dozentInputField to set
	 */
	public void setDozentInputField(JTextField dozentInputField) {
		this.dozentInputField = dozentInputField;
	}

	/**
	 * @param semesterInputField the semesterInputField to set
	 */
	public void setSemesterInputField(JTextField semesterInputField) {
		this.semesterInputField = semesterInputField;
	}

	/**
	 * @return the tablePanel
	 */
	public JPanel getTablePanel() {
		return tablePanel;
	}

	/**
	 * @param tablePanel the tablePanel to set
	 */
	public void setTablePanel(JPanel tablePanel) {
		this.tablePanel = tablePanel;
	}

	/**
	 * @return the exportPanel
	 */
	public JPanel getExportPanel() {
		return exportPanel;
	}

	/**
	 * @param exportPanel the exportPanel to set
	 */
	public void setExportPanel(JPanel exportPanel) {
		this.exportPanel = exportPanel;
	}

	/**
	 * @return the importPanel
	 */
	public JPanel getImportPanel() {
		return importPanel;
	}

	/**
	 * @param importPanel the importPanel to set
	 */
	public void setImportPanel(JPanel importPanel) {
		this.importPanel = importPanel;
	}

	/**
	 * @return the helpPanel
	 */
	public JPanel getHelpPanel() {
		return helpPanel;
	}

	/**
	 * @param helpPanel the helpPanel to set
	 */
	public void setHelpPanel(JPanel helpPanel) {
		this.helpPanel = helpPanel;
	}

	/**
	 * @return the viewPanels
	 */
	public ArrayList<JPanel> getViewPanels() {
		return viewPanels;
	}

	/**
	 * @param viewPanels the viewPanels to set
	 */
	public void setViewPanels(ArrayList<JPanel> viewPanels) {
		this.viewPanels = viewPanels;
	}

	/**
	 * @param contentPane the contentPane to set
	 */
	public void setContentPane(Container contentPane) {
		this.contentPane = contentPane;
	}

	/**
	 * @return the semesterBox
	 */
	public JComboBox<String> getSemesterBox() {
		return semesterBox;
	}

	/**
	 * @param semesterBox the semesterBox to set
	 */
	public void setSemesterBox(JComboBox<String> semesterBox) {
		this.semesterBox = semesterBox;
	}

	/**
	 * @return the deleteLabel
	 */
	public JLabel getDeleteLabel() {
		return deleteLabel;
	}

	/**
	 * @param deleteLabel the deleteLabel to set
	 */
	public void setDeleteLabel(JLabel deleteLabel) {
		this.deleteLabel = deleteLabel;
	}

	/**
	 * @return the profBox
	 */
	public JComboBox<String> getProfBox() {
		return profBox;
	}

	/**
	 * @param profBox the profBox to set
	 */
	public void setProfBox(JComboBox<String> profBox) {
		this.profBox = profBox;
	}

	/**
	 * @return the vorlesungsTabelle
	 */
	public JTable getVorlesungsTabelle() {
		return vorlesungsTabelle;
	}

	/**
	 * @param vorlesungsTabelle the vorlesungsTabelle to set
	 */
	public void setVorlesungsTabelle(JTable vorlesungsTabelle) {
		this.vorlesungsTabelle = vorlesungsTabelle;
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
		//set table model
		vorlesungsTabelle.setModel(model.getTableModel());
		//set row sorter for filtering
	   	vorlesungsTabelle.setRowSorter(model.getRowSorter());
	   	updateFilterBoxes();
	}
	
	/**
	 * Setzt den controller für den Frame fest und belegt alle Funktionalelemente mit ihren Listenern
	 * @param controller the controller to set
	 */
	public void setController(VL_Suche_Controller controller) {
		this.controller = controller;
		//Frame
		this.addWindowFocusListener(controller.getFocusListener());
		//Side menu
		kitLogoButton.addActionListener(controller.getButtonListener());
		menuButtonVorlesungen.addActionListener(controller.getButtonListener());
		menuButtonExport.addActionListener(controller.getButtonListener());
		menuButtonImport.addActionListener(controller.getButtonListener());
		menuButtonHelp.addActionListener(controller.getButtonListener());
		//Table panel
		addLectureButton.addActionListener(controller.getButtonListener());
		deleteButton.addActionListener(controller.getButtonListener());
		searchField.getDocument().addDocumentListener(controller.getTextFieldListener());
		profBox.addItemListener(controller.getComboBoxListener());
		semesterBox.addItemListener(controller.getComboBoxListener());
		vorlesungsTabelle.getSelectionModel().addListSelectionListener(controller.getRowSelectionListener());
		//Import Panel		
		importButton.addActionListener(controller.getButtonListener());
		//Export Panel
		exportButton.addActionListener(controller.getButtonListener());
	}
}
