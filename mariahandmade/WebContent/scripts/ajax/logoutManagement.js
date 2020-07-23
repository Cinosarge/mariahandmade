/**
 * Invio asincrono della richiesta di logout. Alla pressione del pulsante di Logout il
 * parametro 'option' ha valore 'checkCart'. Questo comporta l'invio di un messaggio
 * per la visualizzazione di un box di scelta.
 * 
 * Alla pressione del pulsante Esci nel box di scelta il parametro 'option' ha valore
 * 'discardCart'.
 * 
 * E' atteso un messaggio di risposta dal server con questa forma
 * <notification name="redirect">MyPage.jsp</notification>
 *   oppure
 * <notification name="showDialog" />
 */

function sendLogoutRequest(option) {
	var request = new XMLHttpRequest();

	request.onreadystatechange = function() {
		if(this.readyState == 4) {
			var operationXML = this.responseXML;
			
			var opName = operationXML.getElementsByTagName("operation")[0].getAttribute("name");
			if(opName == "redirect") {
				window.location = operationXML.getElementsByTagName("operation")[0].innerHTML;
			}
			else {
				/**
				 * Fa uso dei costruttori definiti in ModalBoxFactory.js per creare il codice HTML
				 * necessario alla rappresentazione della modal box descritta da modal-box.css
				 */
				var modalBoxDescriptor = new ModalBoxDescriptor();
				modalBoxDescriptor.setTitle("ATTENZIONE!");
				modalBoxDescriptor.setMessage("Uscendo perderai i prodotti aggiunti al carrello! Vuoi acquistare prima di uscire?");
				modalBoxDescriptor.setLeftOptionText("Acquista");
				modalBoxDescriptor.setLeftOptionFunction(function() {window.location = "cart.do";});
				modalBoxDescriptor.setRightOptionText("Esci");
				modalBoxDescriptor.setRightOptionFunction(function() {sendLogoutRequest("discardCart");});
				modalBoxDescriptor.setExitOptionFunction();
				
				var modalBoxFactory = new ModalBoxFactory(modalBoxDescriptor);
				var modalBox = modalBoxFactory.makeModalBox();
				
				document.getElementsByTagName("body")[0].appendChild(modalBox);
			}
		}
	}
	
	request.open("GET", "logout.do?option=" + option, true);
	request.send(null);
}
