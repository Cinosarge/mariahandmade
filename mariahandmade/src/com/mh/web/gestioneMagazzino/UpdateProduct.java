package com.mh.web.gestioneMagazzino;

import java.io.*;
import java.math.BigDecimal;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.*;
import javax.servlet.http.*;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import com.mh.model.gestioneMagazzino.DBProduct;
import com.mh.model.javaBeans.Product;

/**
 * Il metodo GET riceve come parametro 'productCode' e si occupa di reperire i dati relativi
 * al prodotto e inserirli nello scope della richiesta per la visualizzazione. Il nome dell'
 * attributo della richiesta è 'productToUpdate'.
 * 
 * Il metodo POST verifica che vi sia l'opportuno livello di autenticazione e aggiorna i dati
 * relativi al prodotto.
 */
public class UpdateProduct extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	/**
	 * Carica i dati del prodotto nello scope della richiesta. Non richiede controlli sul
	 * grado di autenticazione poiché i dati dei prodotti non sono informazioni riservate.
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException {
		
		response.setContentType("text/html");
		
		String productCode = request.getParameter("productCode");
		int productCodeNumber = Integer.parseInt(productCode);
		
		DBProduct productAPI = new DBProduct();
		Product product = productAPI.getProduct(productCodeNumber);
		
		// La sessione a questo punto deve esistere e deve essere quella di un gestore. Se la sessione è scaduta si scatena una NullPointerExceptionj
		// Gestisci questa parte in modo più elegante
		request.getSession(false).setAttribute("productToUpdate", product);
		
		// Request Dispatching
		RequestDispatcher updateView = request.getRequestDispatcher("UpdateProduct.jsp");
		updateView.forward(request, response);
	}
	
	/**
	 * Le richieste POST inviate a questo servlet recano i dati per l'aggiornamento di un prodotto.
	 * Poiché i dati vengono inviati da un form che contiene anche una immagine,
	 * e per questo di tipo <i>multipart/form-data</i>, per accedere ai campi di tipo testo
	 * sarà necessario utilizzare la libreria Apache FileUpload.
	 * 
	 * TODO - Ricordati di rimuovere l'attributo di sessione 'productToUpdate' quando non serve più.
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException {
		
		// Verifica i permessi
		
		Product product = new Product();
		
		/*
		 * Estrazione della foto dalla richiesta con la libreria Apache FileUpload.
		 * Il formato della foto non è limiatato. Il nome corrisponde allo hash MD5
		 * della fotografia ed è salvato nel campo IMMAGINI della tabella PRODOTTO.
		 * 
		 * Si noti che se il prodotto aveva precedentemente una immagine, l'immagine viene
		 * sostituita dalla presente ma la vecchia immagine, pur non venendo usata, viene
		 * conservata. Questo apre a ulteriori sviluppi come per esempio immagini multiple
		 * per singolo prodotto.
		 */
		DiskFileItemFactory factory = new DiskFileItemFactory();
		ServletFileUpload fileUpload = new ServletFileUpload(factory);
		Map<String, List<FileItem>> fileItemMap = null;
		
		String pictureDigestString = "";	// Sarà il nome dell'immagine
		String pictureFormat = "";			// Sarà il formato dell'immagine
		File imageFile = null;
		
		if(ServletFileUpload.isMultipartContent(request)) {
			try {
				fileItemMap = fileUpload.parseParameterMap(request);
			}
			catch(FileUploadException e) {
				System.out.println("Cannot parse profile picture from the request!");
				e.printStackTrace();
			}
		}
		
		if(fileItemMap != null) {
			FileItem productPicture = fileItemMap.get("productPhotoFile").get(0);
			if(!productPicture.isFormField()) {
				
				// CALCOLO HASH MD5 DELL'IMMAGINE DEL PROFILO (NOME DEL FILE)
				try {
					MessageDigest md5Alg = MessageDigest.getInstance("MD5");
					byte[] pictureBytes = productPicture.get();
					byte[] digest = md5Alg.digest(pictureBytes);
					pictureDigestString = digest.toString();
					
				} catch (NoSuchAlgorithmException e) {
					System.out.println("Hashing algorithm not found!");
					e.printStackTrace();
				}
				
				// FORMATO DEL FILE
				pictureFormat = productPicture.getName();
				int lastIndexOfDot = pictureFormat.lastIndexOf(".");
				
				/*
				 * Se non c'è il punto perché magari la foto non viene caricata o non ha un formato segnato nel nome,
				 * allora il file immagine non viene scritto.
				 */
				if(lastIndexOfDot > -1) {
					pictureFormat = pictureFormat.substring(pictureFormat.lastIndexOf("."));
					imageFile = new File(getServletContext().getInitParameter("productPicturesPath") + pictureDigestString + pictureFormat);
					try {
						productPicture.write(imageFile);
					} catch (Exception e) {
						System.out.println("Cannot write profile picture to a file!");
						e.printStackTrace();
					}
				}
				else {
					/*
					 * In questo caso non è stato inviato nessun file immagine e dunque il codice digest appena
					 * calcolato (che sarà il nome dell'immagine) non deve essere registrato nel database.
					 * 
					 * Al suo posto rimane l'immagine precedente del prodotto.
					 * 
					 * La sessione a questo punto deve esistere e deve essere quella di un gestore. Se la sessione è scaduta si scatena una NullPointerExceptionj
					 * Gestisci questa parte in modo più elegante
					 */
					pictureDigestString = ( (Product) request.getSession(false).getAttribute("productToUpdate") ).getImages(); //include l'estensione
					pictureFormat = "";
				}
			}
		}
		
		/*
		 * La sessione a questo punto deve esistere e deve essere quella di un gestore. Se la sessione è scaduta si scatena una NullPointerException
		 * Gestisci questa parte in modo più elegante. Nota che a questo punto già dovresti avere la sessione disponibile per il fatto della foto.
		 * 
		 * Nota che mentre per l'inserimento è necessario inviare un bean senza codice prodotto in questo caso è necessario che il prodotto abbia un codice non
		 * nullo che sia già presente nel database.
		 */
		product.setCode(( (Product) request.getSession(false).getAttribute("productToUpdate") ).getCode());
		
		product.setName(fileItemMap.get("productName").get(0).getString());
		product.setLine(fileItemMap.get("line").get(0).getString());
		product.setType(fileItemMap.get("type").get(0).getString());
		product.setImages(pictureDigestString + pictureFormat);
		product.setAvailableUnits(Integer.parseInt(fileItemMap.get("availableUnits").get(0).getString()));
		
		// il campo minInventory non è un campo obbligatorio
		String minInventory = fileItemMap.get("minInventory").get(0).getString();
		if(minInventory != null) {
			product.setMinInventory(Integer.parseInt(fileItemMap.get("minInventory").get(0).getString()));
		}
		
		
		product.setPrice(BigDecimal.valueOf(Double.parseDouble(fileItemMap.get("price").get(0).getString().replace(",", "."))));
		
		// Il campo cost non è un campo obbligatorio
		String cost = fileItemMap.get("cost").get(0).getString(); // In caso di assenza di un valore restituisce una stringa vuota
		if(!cost.equals("")) {
			product.setCost(BigDecimal.valueOf(Double.parseDouble(fileItemMap.get("cost").get(0).getString().replace(",", "."))));
		}
		
		product.setInsertDate( LocalDateTime.now() );
		product.setDescription(fileItemMap.get("description").get(0).getString());
		product.setDeleted(false);
		
		// Creazione della lista di materiali
		List<String> materials = new ArrayList<String>();
		List<FileItem> materialList = fileItemMap.get("material");
		if(materialList != null) {
			for(FileItem material : materialList) {
				String tmp = material.getString();
				if(tmp != null) {
					materials.add(tmp);
				}
			}
		}
		
		product.setMaterials(materials);
		
		DBProduct productAPI = new DBProduct();
		try{
			productAPI.updateProduct(product);
		}
		catch(SQLException sqle){
			System.out.println("Errore durante l'inserimento del prodoto.");
			System.out.println(sqle.getClass().getName() + ": " + sqle.getMessage());
			
			if(imageFile != null) {
				imageFile.delete(); // Eliminiamo l'immagine precedentemente caricata
			}
			
			response.sendError(500); // Internal Server Error
			return; // Così evitiamo il flush + forward
		}
		
		/*
		 * Fare in modo che nella pagina AdminHome.jsp appaia una notifica di
		 * successo o fallimento nell'inserimento del prodotto sarebbe una
		 * buona pratica.
		 */
		response.setContentType("text/html");
		response.sendRedirect(response.encodeRedirectURL("AdminHome.jsp"));
	}
}
