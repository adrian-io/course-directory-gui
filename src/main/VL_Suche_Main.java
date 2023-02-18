package main;

import model.*;
import controller.*;
import view.*;

import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.io.File;

/**
 * Klasse, die die Main Methode enth˜lt.
 * 
 * @author Adrian Scholl
 * @version 27.05.2020
 *
 */
public class VL_Suche_Main {

	private static VL_Suche_Frame view;
	private static VL_Suche_Controller controller;
	private static VL_Suche_Model model;

	/**
	 * Main Methode der VL_Suche, die alle Komponenten erzeugt und miteinander
	 * bekannt macht.
	 * 
	 * @param args dem Programm ˜bergebene Parameter
	 */
	public static void main(String[] args) {
		view = new VL_Suche_Frame();
		view.setVisible(true);

		controller = new VL_Suche_Controller();

		model = new VL_Suche_Model();

		// View, Controller, Model miteinander bekannt machen
		controller.setView(view);
		controller.setModel(model);

		view.setController(controller);
		view.setModel(model);

		model.setView(view);
		model.setController(controller);
	}
}
