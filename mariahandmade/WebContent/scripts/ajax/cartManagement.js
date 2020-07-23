/**
 * Invia una richiesta GET verso la risora addToCart.do che fa capo alla servlet AddToCart
 * e gestisce il carrello. E' atteso un messaggio di risposta dal server
 * con questa forma
 * 
 * <notification>
 *   <errorOccurred>false</errorOccurred>
 *   <message>Some text</message>
 *   <details>Some details</details>
 * </notification>
 * 
 * Il messaggio viene utilizzato per mostrare una notifica sullo stato
 * dell'operazione.
 */
function addToCart(productCode) {
	var request = new XMLHttpRequest();
	request.onreadystatechange = function() {
		if(this.readyState == 4) {
			var xmlResponse = this.responseXML;
			
			var errorOccurred = xmlResponse.getElementsByTagName("errorOccurred")[0].innerHTML;
			var message = xmlResponse.getElementsByTagName("message")[0].innerHTML;
			var details = xmlResponse.getElementsByTagName("details")[0].innerHTML;
			
			if(errorOccurred == "true") { // Le stringe sono tipi primitivi per javascript
				document.getElementsByClassName("notification")[0].style.background = "red";
			}
			else {
				document.getElementsByClassName("notification")[0].style.background = "green";
			}
			
			document.getElementsByClassName("message")[0].innerHTML = message;
			document.getElementsByClassName("details")[0].innerHTML = details;
			
			// Rendiamo visibile la notifica
			document.getElementsByClassName("notification")[0].style.visibility = "visible";
			
			/*
			 * Impostiamo un timeout per far sparire la notifica.
			 * 
			 * TODO - Un effetto fade-in fade-out sarebbe cosa buona
			 */
			var timeout = 5000;
			
			if(errorOccurred == "true") {
				timeout = 15000;
			}
				
			setTimeout(function() {document.getElementsByClassName("notification")[0].style.visibility = "hidden";}, timeout);
		}
	}
	
	request.open("GET", "addToCart.do?productCode=" + productCode, true);
	request.send(null);
}
