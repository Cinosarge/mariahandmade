package com.mh.model.gestioneMagazzino;

import com.mh.exception.XMLImportException;
import com.mh.model.javaBeans.Product;

import java.io.*;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.ArrayList;
import java.math.BigDecimal;

import org.jdom2.*;
import org.jdom2.input.SAXBuilder;
import org.jdom2.input.sax.XMLReaders;
import org.jdom2.output.XMLOutputter;
import org.jdom2.output.Format;
import org.jdom2.JDOMException;

import java.sql.SQLException; // NECESSARIO GESTIRLA QUI?

/**
 * Permette l'importazione e l'esportazione in formato XML dei dati
 * relativi ai prodotti presenti nel database.
 * In fase di importazione viene effettuata la validazione tramite
 * la DTD presente in C:/enricosbrighi/mhdbio.dtd
 */
public class XMLProductIO{
	/**
	 * Importa il contenuto di un file xml conforme alle specifiche esposte nella
	 * DTD mhdbio.dtd
	 * Per i prodotti già presenti nel database viene modificato soltanto il numero
	 * di unità disponibili e ignorati tutti gli altri dati.
	 */
	public static void importFile(File productXMLFile) throws XMLImportException {
		/*
		 * Validazione del documento in input.
		 */
		SAXBuilder sax = new SAXBuilder(XMLReaders.DTDVALIDATING);
		Document productXML = null;
		try{
			productXML = sax.build(productXMLFile);
		}
		catch(JDOMException jde){
			String errorMessage = "XML import file not well-formed or invalid. DETAILS: ";
			errorMessage += jde.getClass().getName() + ": " + jde.getMessage();
			throw new XMLImportException(errorMessage);
		}
		catch(IOException ioe){
			String errorMessage = "Problem accessing the product file. DETAILS: ";
			errorMessage += ioe.getClass().getName() + ": " + ioe.getMessage();
			throw new XMLImportException(errorMessage);
		}
		
		/*
		 * Si produce una lista di prodotti a partire dal documento XML.
		 */
		List<Product> productList = new ArrayList<Product>();
		
		Element productsEl = productXML.getRootElement().getChild("prodotti");
		List<Element> productElementList = productsEl.getChildren("prodotto");
		
		for(Element e : productElementList){
			Product p = new Product();
			
			Attribute codeAtt = e.getAttribute("codice");
			if(codeAtt != null)
				p.setCode(Integer.parseInt(codeAtt.getValue().substring(1)));
			
			String name = e.getChildTextNormalize("nome");
			if(name == null || name.equals(""))
				p.setName(null);
			else
				p.setName(name);
			
			p.setLine(e.getChildTextNormalize("linea"));
			p.setType(e.getChildTextNormalize("tipo"));
			p.setImages(e.getChildTextNormalize("numimg"));
			p.setAvailableUnits(Integer.parseInt(e.getChildTextNormalize("unita")));
			String minInventory = e.getChildTextNormalize("scorta");
			if(minInventory != null)
				p.setMinInventory(Integer.parseInt(minInventory));
			p.setPrice( ( new BigDecimal(e.getChildTextNormalize("prezzo").replace(',','.')) ).setScale(2) );
			String cost = e.getChildTextNormalize("costo");
			if(cost != null)
				p.setCost( ( new BigDecimal(cost.replace(',','.')) ).setScale(2) );
			
			String insertDate = e.getChildTextNormalize("datainserimento");
			if(insertDate != null && isISO8601(insertDate))
				p.setInsertDate(LocalDateTime.parse(insertDate));
			else
				p.setInsertDate(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
			
			String description = e.getChildTextNormalize("descrizione");
			if(description == null || description.equals(""))
				p.setDescription(null);
			else
				p.setDescription(description);
			
			Element deleted = e.getChild("eliminato");
			if(deleted != null){
				p.setDeleted(true);
				String deletionDate = deleted.getAttributeValue("data");
				if(deletionDate != null && isISO8601(deletionDate))
					p.setDeletionDate(LocalDateTime.parse(deletionDate));
				else
					p.setDeletionDate(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
			}
			else{
				p.setDeleted(false);
				p.setDeletionDate(null);
			}
			
			Element materials = e.getChild("materiali");
			if(materials != null){
				Attribute materialsAtt = materials.getAttribute("lista");
				String[] tokens = materialsAtt.getValue().trim().split(" *,++ *");
				List<String> composition = new ArrayList<String>();
				for(String s : tokens){
					composition.add(s);
				}
				p.setMaterials(composition);
			}
			/*
			 * Aggiungiamo il bean Product ricavato dal DOM XML alla lista di prodotti.
			 */
			productList.add(p);
		}
		/*
		 * Inseriamo i prodotti nuovi e aggiorniamo quelli già presenti.
		 */
		DBProduct productAPI = new DBProduct();
		try{
			productAPI.insertProductList(productList);
		}
		catch(SQLException sqle){
			System.out.println("Failed to insert products from XML!");
			System.out.println(sqle.getClass().getName() + ": " + sqle.getMessage());
		}
	}
	
	/**
	 * Esporta il contenuto delle tabelle PRODOTTO e COMPOSIZIONE in un file XML
	 * su disco e restituisce un riferimento al file.
	 */
	public static File exportFile(String filePath) {
		
		DBProduct productAPI = new DBProduct();
		List<Product> productList = productAPI.getFullProductList();
		
		Element products = new Element("prodotti");
		for(Product p : productList){
			Element name = null;
			if(p.getName() != null){
				name = new Element("nome");
				name.addContent(new Text(p.getName()));
			}
			Element line = new Element("linea");
			line.addContent(new Text(p.getLine()));
			Element type = new Element("tipo");
			type.addContent(new Text(p.getType()));
			Element imgNum = new Element("numimg");
			imgNum.addContent(new Text( p.getImages()));
			Element units = new Element("unita");
			units.addContent(new Text( (new Integer(p.getAvailableUnits())).toString()));
			Element minInventory = null;
			minInventory = new Element("scorta");
			minInventory.addContent(new Text( (new Integer(p.getMinInventory())).toString()));
			Element price = new Element("prezzo");
			price.addContent(new Text(p.getPrice().toString()));
			Element cost = null;
			if(p.getCost() != null){
				cost = new Element("costo");
				cost.addContent(new Text(p.getCost().toString()));
			}
			Element insertDate = new Element("datainserimento");
			insertDate.addContent(new Text(p.getInsertDate().toString()));
			Element description = null;
			if(p.getDescription() != null){
				description = new Element("descrizione");
				description.addContent(new Text(p.getDescription()));
			}
			Element deleted = null;
			if(p.isDeleted()){
				deleted = new Element("eliminato");
				deleted.setAttribute("data", p.getDeletionDate().toString());
			}
			
			Element materials = null;
			List<String> components = p.getMaterials();
			if(components != null){
				materials = new Element("materiali");
				String value = new String(components.remove(0));
				for(String s : components){
					value += "," + s;
				}
				materials.setAttribute("lista", value);
			}
			
			Element product = new Element("prodotto");
			product.setAttribute("codice", "P" + p.getCode().toString());
			if(name != null){
				product.addContent(name);
			}
			product.addContent(line);
			product.addContent(type);
			product.addContent(imgNum);
			product.addContent(units);
			if(minInventory != null)
				product.addContent(minInventory);
			product.addContent(price);
			if(cost != null)
				product.addContent(cost);
			product.addContent(insertDate);
			if(description != null)
				product.addContent(description);
			if(deleted != null)
				product.addContent(deleted);
			if(materials != null)
				product.addContent(materials);
			
			products.addContent(product);
		}
		
		Element root = new Element("mhdb");
		root.setAttribute("version","1.0");
		root.addContent(products);
		
		/*
		 * Generiamo a mezzo di un file .txt un commento da inserire nei file
		 * esportato con le istruzioni per la modifica del file stesso.
		 */
		File instructionFile = new File("c:/sbrighienrico/xmlImportInstructions.txt");
		String instructions = new String("\n\n");
		try{
			FileInputStream fis = new FileInputStream(instructionFile);
			InputStreamReader isr = new InputStreamReader(fis, "UTF-8"); // Le classi Reader utilizzano la codifica standard di sistema.
			BufferedReader br = new BufferedReader(isr);
			
			String line = null;
			while( (line = br.readLine()) != null )
				instructions += line + "\n";
			instructions += "\n";
			br.close();
		}
		catch(IOException ioe){
			System.out.println("I/O issue: failed to read xml input instructions file.\n");
			System.out.println(ioe.getClass().getName() + ": " + ioe.getMessage());
		}
		Comment importInstructions = new Comment(instructions);
		
		Document productXML = new Document();
		DocType mhdbioDt = new DocType("mhdb", "C:/sbrighienrico/mhdbio.dtd");
		productXML.setDocType(mhdbioDt);
		productXML.addContent(importInstructions);
		productXML.setRootElement(root);
		
		/*
		 * Scriviamo su file la rappresentazione XML dei prodotti presenti nel database.
		 */
		XMLOutputter xout = new XMLOutputter(Format.getPrettyFormat().setIndent("    "));
		String dateTime = LocalDateTime.now().toString();
		dateTime = dateTime.replace(':', '.');
		dateTime = dateTime.replace('T','_');
		File file = new File(filePath + "/[EXPORTED]" + dateTime + ".xml");
		try{
			OutputStream xmlOutputStream = new FileOutputStream(file); // L'encoding di default di FileOutputStream è UTF-8
			xout.output(productXML, xmlOutputStream);
			xmlOutputStream.close();
		}
		catch(IOException ioe){
			System.out.println("I/O issue: failed to write build.xml file.\n");
			System.out.println(ioe.getClass().getName() + ": " + ioe.getMessage());
		}
		catch(NullPointerException npe){
			System.out.println("JDOM issue: Document object is null.\n");
			System.out.println(npe.getClass().getName() + ": " + npe.getMessage());
		}
		
		return file;
	}
	
	/**
	 * Determina se la stringa in ingresso è nel formato ISO8601.
	 * Esempio: 2007-06-24T10:35:45
	 *
	 * @param datetime una stringa che rappresenta una data
	 * @return true se datetime è nel formato ISO8601, false altrimenti
	 */
	private static boolean isISO8601(String datetime){
		try{
			LocalDateTime.parse(datetime);
		}
		catch(DateTimeParseException dtpe){
			return false;
		}
		return true;
	}
}
