package com.mh.listener;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Crea un Tomcat Context denominato "static" per permettere alle pagine web di reperire
 * risorse statiche come le immagini dei prodotti e le immagini dei profili utente.
 * 
 * Il nome del contesto è determinato dal nome del file. In questo caso static.
 * 
 * La cartella di contesto si trova
 * - in sistemi Windows in		C:/sbrighienrico/static
 * - in sistemi OSX e *nix in	/home/sbrighienrico/static
 * - in tutti gli altri			/sbrighienrico/static
 */
public class ContextXMLFile implements ServletContextListener {
	/**
	 * Crea un oggetto "Document" mediante la libreria JDOM e ne scrive il contenuto,
	 * personalizzato in base al sistema operativo, all'interno di un file xml nella
	 * cartella $CATALINA_HOME/conf/[enginename]/[hostname]
	 *
     * @see ServletContextListener#contextInitialized(ServletContextEvent)
     */
    public void contextInitialized(ServletContextEvent event)  {
    	Document contextDocument = new Document();
    	Element contextElement = new Element("Context");
    	
    	/*
    	 * Definizione del percorso per la cartella di file statici in base al sistema
    	 * oparativo in uso. Aggiunta del percorso come valore dell'attributo
    	 * "docBase" dell'elemento "Context".
    	 */
    	String os = null;
    	try {
    		os = System.getProperty("os.name").toLowerCase();
    	}
    	catch(Exception e) {
    		System.out.println(e.getClass().getName() + ": " + e.getMessage());
    	}
    	
    	/*
    	 * Non c'è bisogno di un attributo path in quanto il nome del contesto è determinato dal
    	 * nome del file.
    	 */
    	if(os.contains("windows")) {
    		contextElement.setAttribute("docBase", "C:/sbrighienrico/static");
    		contextElement.setAttribute("path", "/static");
    	}
    	else if(os.contains("osx") || os.contains("nix") || os.contains("nux")) {
    		contextElement.setAttribute("docBase", "home/sbrighienrico/static");
    	}
    	else {
    		contextElement.setAttribute("docBase", "/sbrighienrico/static");
    	}
    	contextDocument.setRootElement(contextElement);
    	
    	/*
    	 * Identificazione del percorso della cartella
    	 * $CATALINA_HOME/conf/[enginename]/[hostname]
    	 */
    	String path = System.getProperty("catalina.base") + event.getServletContext().getInitParameter("contextFilesPath");
    	
    	System.out.println("MariaHandmade - Context for static files outside .war file has been described in:");
    	System.out.println(path);
    	/*
    	 * Creazione del file $CATALINA_HOME/conf/[enginename]/[hostname]/static.xml
    	 * con copia del contenuto dell'oggetto contextDocument in formato testuale.
    	 */
    	XMLOutputter xout = new XMLOutputter(Format.getPrettyFormat().setIndent("    "));
    	File file = new File(path + "/static.xml");
		try{
			OutputStream xmlOutputStream = new FileOutputStream(file); // L'encoding di default di FileOutputStream � UTF-8
			xout.output(contextDocument, xmlOutputStream);
			xmlOutputStream.close();
		}
		catch(IOException ioe){
			System.out.println("I/O issue: failed to write static.xml file.\n");
			System.out.println(ioe.getClass().getName() + ": " + ioe.getMessage());
		}
		catch(NullPointerException npe){
			System.out.println("JDOM issue: Document object is null.\n");
			System.out.println(npe.getClass().getName() + ": " + npe.getMessage());
		}
    }
	/**
	 * Elimina il file $CATALINA_HOME/conf/[enginename]/[hostname]/static.xml
	 * 
     * @see ServletContextListener#contextDestroyed(ServletContextEvent)
     */
    public void contextDestroyed(ServletContextEvent event)  {
    	String contextFilePath = System.getProperty("catalina.base") + event.getServletContext().getInitParameter("contextFilesPath");
    	File contextFile = new File(contextFilePath + "/static.xml");
    	contextFile.delete();
    }
}
