package com.mh.web.gestioneMagazzino;

import java.io.*;
import java.time.LocalDateTime;
import java.util.*;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
 
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import com.mh.exception.XMLImportException;
import com.mh.model.gestioneMagazzino.XMLProductIO;

public class AsyncXMLImport extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
	}
	
	public void doPost(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException {
		
		String message = "Perfetto";
		String details = "Importazione eseguita!";
		boolean errorOccurred = false;
		
		long MB = 1024 * 1024;
		
		/*
		 * Upload del file .xml contenente i dati relativi ai prodotti.
		 */
		String filePath = getServletContext().getInitParameter("xmlImportPath");
		boolean isMultipartContent = ServletFileUpload.isMultipartContent(request);
		
		if(!isMultipartContent) {
			errorOccurred = true;
			message = "Error processing form";
			details = "Form enctype is not \"multipart/form-data\"";
		}
		
		/*
		 * Otteniamo una lista dei FileItem contenuti nel form
		 */
		List<FileItem> fileItemList = null;
		
		if(!errorOccurred) {
			/*
			 * Default treshold: 10KB
			 * Default temporary direcory: as returned by System.getProperty("java.io.tmpdir")
			 */
			DiskFileItemFactory factory = new DiskFileItemFactory();
			
			ServletFileUpload fileUpload = new ServletFileUpload(factory);
			fileUpload.setSizeMax(1 * MB); // MAX FILE DIMENSION
			
			try {
				fileItemList = fileUpload.parseRequest(request);
			}
			catch(FileUploadException e) {
				errorOccurred = true;
				message = "Issue: problem parsing the request for multipart data.";
				details = e.getClass().getName() + ": " + e.getMessage();
				
				System.out.println(message);
				System.out.println(details);
			}
		}
		
		/*
		 * Viene generato un FileItem per ogni campo di un form e pertanto una iterazione si rende
		 * necessaria; stiamo cercando un elemento HTML così definito
		 * 
		 * <input type="file" name="xmlfile" />
		 * 
		 * al fine di scriverlo all'interno di un file .xml
		 */
		File xmlFile = null;
		
		/*
		 * Con questo controllo su errorOccurred, se parseRequest ha generato un errore,
		 * evitiamo la NullPointerException adesso che proviamo ad accedervi.
		 */
		if(!errorOccurred) {
			for(FileItem fileItem : fileItemList) {
				if(!fileItem.isFormField() && (fileItem.getFieldName().equals("xmlfile"))) {
					String fileName = LocalDateTime.now().toString();
					fileName = fileName.replace(':', '.');
					fileName = fileName.replace('T','_');
					
					xmlFile = new File(filePath + "/[IMPORTED]" + fileName + ".xml");
					try {
						fileItem.write(xmlFile);
					}
					catch(Exception e) {
						errorOccurred = true;
						message = "Issue: problem writing FileItem to actual XML file.";
						details = e.getClass().getName() + ": " + e.getMessage();
						
						System.out.println(message);
						System.out.println(details);
					}
				}
			}
		}
		
		/*
		 * Importazione dei dati presenti nell'appena caricdato file .xml
		 * contenente i dati relativi ai prodotti.
		 */
		if(!errorOccurred) {
			if(xmlFile != null) {
				try {
					XMLProductIO.importFile(xmlFile);
				}
				catch(XMLImportException e) {
					errorOccurred = true; // Una scelta di coerenza.. ma non serve!
					message = "Issue: problem writing FileItem to actual XML file.";
					details = e.getClass().getName() + ": " + e.getMessage();
					
					System.out.println(message);
					System.out.println(details);
				}
			}
		}
		
		/*
		 * Facciamo il Request Dispatching della richiesta verso la servlet che gestisce le
		 * notifiche AJAX
		 */
		RequestDispatcher notificationSender = request.getRequestDispatcher("notify.do");
		
		request.setAttribute("errorOccurred", errorOccurred);
		request.setAttribute("message", message);
		request.setAttribute("details", details);
		
		notificationSender.forward(request, response);
	}
}
