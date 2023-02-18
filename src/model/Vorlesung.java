package model;

/**
 * Die Klasse spezifiziert eine Vorlesung in der Universität
 * 
 * @author Adrian Scholl
 * @version 27.05.2020
 *
 */
public class Vorlesung implements Comparable<Vorlesung>{
	
	private String name;
	private String prof;
	private String semester;
	
	/**
	 * Konstruktor zum Erstellen eines gefüllten Vorlesungsobjekts
	 * @param name name der vorlesung
	 * @param prof professor der vorlesung
	 * @param semester semester der vorlesung
	 */
	public Vorlesung(String name, String prof, String semester) {
		this.name = name;
		this.prof = prof;
		this.semester = semester;
	}
	
	/**
	 * Standardkonstruktor zum Erstellen eines leern Vorlesungsobjekts
	 */
	public Vorlesung() {
		
	}
	
	//Getter-Methoden
	/**
	 * 
	 * @return name name of vorlesung
	 */
	public String getName() {
		return name;
	}
	/**
	 * 
	 * @return prof professor of vorlesung
	 */
	public String getProf() {
		return prof;
	}
	/**
	 * 
	 * @return semester semester of vorlesung
	 */
	public String getSemester() {
		return semester;
	}
	
	//Setter-Methoden
	/**
	 * 
	 * @param name name to be set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * 
	 * @param prof professor to be set
	 */
	public void setProf(String prof) {
		this.prof = prof;
	}
	/**
	 * 
	 * @param semester semester to be set
	 */
	public void setSemester(String semester) {
		this.semester = semester;
	}
	
	//Instanz-Methoden
	/**
	 * Legt alphabetische, lexikographische Ordnung für Vorlesungen fest: 1. name -> 2. prof -> 3. semester
	 */
	@Override
	public int compareTo(Vorlesung o) {
		if(this.name.compareTo(o.name) >= 1) {
			return 1;
		}
		else if(this.name.compareTo(o.name) <= -1) {
			return -1;
		}
		else { 
			//Name ist gleich, prüfe Prof
			if(this.prof.compareTo(o.prof) >= 1) {
				return 1;
			}
			else if(this.prof.compareTo(o.prof) <= -1) {
				return -1;
			}
			else {
				//Name und Prof gleich, prüfe Semester
				if(this.semester.compareTo(o.semester) >= 1) {
					return 1;
				}
				else if(this.semester.compareTo(o.semester) <= -1) {
					return -1;
				}
				else {
					//Name, Prof, Semester gleich --> Vorlesungen sind gleich
					return 0;
				}
			}
		}
	}	
	
	@Override
	public String toString() {
		return "(" + name + ", " + prof + ", "  + semester + ")";
	}
}
