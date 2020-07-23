package com.mh.web.gestioneClienti;

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.servlet.*;
import javax.servlet.http.*;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

import com.mh.model.gestioneClienti.DBUser;
import com.mh.model.javaBeans.Account;
import com.mh.model.javaBeans.Address;
import com.mh.model.javaBeans.RegisteredUser;

/**
 * 
 */
public class Signin extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	/**
	 * Le richieste GET servono per decidere se reindirizzare al checktout dopo aver effettuato la
	 * registrazione e la risposta a queste richieste è la pagina Signin.jsp.
	 * 
	 * Sarà il servlet Login a riutilizzare il campo checktouAfterSignin per effetture la giusta
	 * redirezione dopo il login (che avviene automaticamente dopo la registrazione)
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException {
		
		HttpSession session = request.getSession(false);
		if(session != null && session.getAttribute("userData") != null) {
			// Un utente registrato non può aprire un nuovo account
			response.sendError(403);
			return;
		}
		
		boolean checkoutAfterSignin = request.getParameter("checkoutAfterSignin").equals("true");
		// A questo punto abbiamo bisogno di una sessione per memorizzare checkoutAftersignin
		session = request.getSession();
		session.setAttribute("checkoutAfterSignin", checkoutAfterSignin);
		
		RequestDispatcher signinView = request.getRequestDispatcher("Signin.jsp");
		signinView.forward(request, response);
	}
	
	/**
	 * Le richieste POST inviate a questo servlet recano i dati per l'effettiva registrazione
	 * dell'utente. Poiché i dati vengono inviati da un form che contiene anche una immagine,
	 * e per questo di tipo <i>multipart/form-data</i>, per accedere ai campi di tipo testo
	 * sarà necessario utilizzare la libreria Apache FileUpload.
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException {
		
		RegisteredUser user = new RegisteredUser();
		Account account = new Account();
		Address address = new Address();
		
		/*
		 * Estrazione della foto dalla richiesta con la libreria Apache FileUpload.
		 * Il formato della foto non è limiatato. Il nome corrisponde allo hash MD5
		 * della fotografia ed è salvato nel campo FOTOGRAFIA della tabella UTENTE. 
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
			FileItem profilePicture = fileItemMap.get("profilePhotoFile").get(0);
			if(!profilePicture.isFormField()) {
				
				// CALCOLO HASH MD5 DELL'IMMAGINE DEL PROFILO (NOME DEL FILE)
				try {
					MessageDigest md5Alg = MessageDigest.getInstance("MD5");
					byte[] pictureBytes = profilePicture.get();
					byte[] digest = md5Alg.digest(pictureBytes);
					pictureDigestString = digest.toString();
					
				} catch (NoSuchAlgorithmException e) {
					System.out.println("Hashing algorithm not found!");
					e.printStackTrace();
				}
				
				// FORMATO DEL FILE
				pictureFormat = profilePicture.getName();
				int lastIndexOfDot = pictureFormat.lastIndexOf(".");
				
				/*
				 * Se non c'è il punto perché magari la foto non viene caricata o non ha un formato segnato nel nome,
				 * allora il file immagine non viene scritto.
				 */
				if(lastIndexOfDot > -1) {
					pictureFormat = pictureFormat.substring(pictureFormat.lastIndexOf("."));
					imageFile = new File(getServletContext().getInitParameter("profilePicturesPath") + pictureDigestString + pictureFormat);
					try {
						profilePicture.write(imageFile);
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
					 * Al suo posto registriamo noimage.png che è l'immagine segnaposto.
					 */
					pictureDigestString = "noimage";
					pictureFormat = ".png";
				}
			}
		}
		
		// Campi ereditati dal bean User
		user.setRegistrationDate( LocalDateTime.now() );
		user.setLastAccessDate( LocalDateTime.now() );
		
		// Campi del bean RegisteredUser
		user.setFiscalCode(fileItemMap.get("fiscalCode").get(0).getString());
		user.setName(fileItemMap.get("firstName").get(0).getString());
		user.setSecondName(fileItemMap.get("secondName").get(0).getString());
		user.setSurname(fileItemMap.get("surname").get(0).getString());
		user.setGender(fileItemMap.get("gender").get(0).getString());
		user.setMail(fileItemMap.get("mail").get(0).getString());
		user.setPhone(fileItemMap.get("phone").get(0).getString());
		
		try {
			String birthDate = new String();
			birthDate = fileItemMap.get("birthDate").get(0).getString();
			user.setBirthDate(LocalDate.parse(birthDate));
		}
		catch(DateTimeParseException e) {
			/*
			 *  UTENTE.DATANASCITA è un campo richiesto dal database quindi l'inserimento fallirà. Questo dà la possibilità
			 *  di gestire la SQLException che tra le altre cose elimina anche l'immagine del profilo caricata.
			 *  (Vedere a fine metodo doPost())
			 */
		}
		
		user.setBirthCity(fileItemMap.get("birthCity").get(0).getString());
		user.setCreditCard(fileItemMap.get("creditCard").get(0).getString());
		
		// Il nome della foto (HASH MD5) insieme al formato verranno memorizzati nel database
		user.setPhoto(pictureDigestString + pictureFormat);
		
		// Campi del bean Account
		account.setUsername(fileItemMap.get("username").get(0).getString());
		account.setPassword(fileItemMap.get("password").get(0).getString());
		account.setChecked(false);
		account.setManager(false);
		
		ArrayList<Address> addressList = new ArrayList<Address>();
		
		// Campi del bean Address - residenza e spedizione
		address.setProvince(fileItemMap.get("province").get(0).getString());
		address.setCity(fileItemMap.get("city").get(0).getString());
		address.setCap(fileItemMap.get("cap").get(0).getString());
		address.setStreet(fileItemMap.get("street").get(0).getString());;
		address.setHouseNumber(fileItemMap.get("houseNumber").get(0).getString());
		address.setAddressType(fileItemMap.get("addressType").get(0).getString());
		
		addressList.add(address);
		
		// Campi del bean Address - solo spedizione
		if(fileItemMap.get("addressType").get(0).getString().equals("residenza")){
			address = new Address();
			address.setProvince(fileItemMap.get("shipmentProvince").get(0).getString());
			address.setCity(fileItemMap.get("shipmentCity").get(0).getString());
			address.setCap(fileItemMap.get("shipmentCap").get(0).getString());
			address.setStreet(fileItemMap.get("shipmentStreet").get(0).getString());
			address.setHouseNumber(fileItemMap.get("shipmentHouseNumber").get(0).getString());
			address.setAddressType("spedizione");
			addressList.add(address);
		}
		
		user.setAccount(account);
		user.setAddressList(addressList);
		
		DBUser userAPI = new DBUser();
		try{
			userAPI.insertUser(user);
		}
		catch(SQLException sqle){
			System.out.println("Errore durante la registrazione dell'utente.");
			System.out.println(sqle.getClass().getName() + ": " + sqle.getMessage());
			
			if(imageFile != null) {
				imageFile.delete(); // Eliminiamo l'immagine precedentemente caricata
			}
			
			response.sendError(500); // Internal Server Error
			return; // Così evitiamo il flush + forward
		}
		
		/*
		 * Il servlet Login si occupa di reindirizzare l'utente verso la pagina finale
		 * del checkout quando l'attributo di sessione checkoutAfterSignin è impostato.
		 * 
		 * Il servlet di Login si aspetta due attributi nello scope della richiesta e
		 * cioè 'username' e 'password' per effettuare automaticamente il login. Nota che
		 * login.do non è in grado di prenderli con request.getParameter() perché la
		 * codifica del form è multipart/form-data e usare Apache FileUpload in login.do
		 * non è una scelta coerente.
		 */
		RequestDispatcher loginService = request.getRequestDispatcher("login.do");
		request.setAttribute("username", fileItemMap.get("username").get(0).getString());
		request.setAttribute("password", fileItemMap.get("password").get(0).getString());
		loginService.forward(request, response);
	}
}
