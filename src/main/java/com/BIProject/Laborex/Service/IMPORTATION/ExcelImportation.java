package com.BIProject.Laborex.Service.IMPORTATION;

import java.io.IOException;
import org.apache.poi.ss.usermodel.Sheet;

public interface ExcelImportation {
	//Reconnaitre le fichier
	boolean supports(String filename);
	//Importation 
	void importData(Sheet sheet) throws IOException;
	//Priorité plus basse = traité en premier
    default int getPriorite() {
        return 100; // valeur par défaut
    }

}
