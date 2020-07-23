/**
 * Invia una richiesta GET verso la risora checkUsername.do che fa capo alla servlet CheckUsername
 * E' atteso un messaggio di risposta dal server con questa forma
 * 
 * <notification>
 *   <errorOccurred>false</errorOccurred>
 *   <message>Some text</message>
 *   <details>Some details</details>
 * </notification>
 * 
 * Il messaggio viene utilizzato per notificare la validità o meno dello username.
 *
 * DIPENDENZE: questo file dipende dallo script AjaxCallTemplate.js
 *
 * @param usernameInputField
 */
function checkUsername(usernameInputField) {
	ajaxCallGet("checkUsername.do", "username=" + usernameInputField.value, responseHandler);
}

/*
 * Funzione di callback per gestire il messaggio XML di risposta, Questa volta la notifica
 * non viene visualizzata in modo "standard" ma si manifesta come campo non valido.
 * 
 * TODO - completa
 * 
 * @param responseXML
 * @returns
 */
function responseHandler(responseText, responseXML) {
	var errorOccurred = responseXML.getElementsByTagName("errorOccurred")[0].innerHTML;
	if(errorOccurred == "true") {
		alert("Username duplicato");
		// Username duplicato
		//document.getElementById("username").valid = "false";
		//alert("Username duplicato");
	}
}
