package com.mh.web.gestioneMagazzino;

import java.io.*;
 
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mh.model.gestioneMagazzino.XMLProductIO;

public class AsyncXMLExport extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	public void doGet(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException {
		
		/*
		 * Esportazione in un file .xml dei dati relativi ad un prodotto
		 */
		String filePath = getServletContext().getInitParameter("xmlExportPath");
		File file = XMLProductIO.exportFile(filePath);
		
		/*
		 * Inizio del download del file .xml
		 */
		response.setContentType("application/xml");
		 /*
		  * Eliminiamo eventuali spazi poiché il carattere di spaziatura non è autorizzato
		  * nell'intestazione HTTP per il nome del file.
		  */
		response.setHeader("Content-Disposition", "attachment;filename=" + file.getName().replace(" ", ""));
		
		OutputStream out = response.getOutputStream();
		BufferedInputStream in = new BufferedInputStream(new FileInputStream(file));
		
		int tmp = -1;
		
		while((tmp = in.read()) >= 0) {
			out.write(tmp);
		}
		
		in.close();
		out.flush();
		out.close();
	}
	
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, java.io.IOException {
		
		
	}
}
