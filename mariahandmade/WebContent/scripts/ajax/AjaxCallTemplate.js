/**
 * Nell'invio di richieste ascincrone GET la creazione dell'oggetto XMLHttpRequest e la sua inizializzazione
 * sono operazioni di routine e pongono un problema di duplicazione del codice e riuso.
 *
 * @param uri lo URL HTTP verso cui inviare la richiesta GET
 * @param queryString la query string priva del simbolo '?'
 * @param responseHandler il metodo di di callback per l'elaborazione della risposta HTTP. Il metodo di callback
 * deve essere adibito alla ricezione di due parametri: il primo per la risposta testuale (responseText) e il
 * secondo per la risposta in formato XML (responseXML).
 */
function ajaxCallGet(uri, queryString, responseHandler) {
	var xhr = new XMLHttpRequest();
	showWaitGadget(true);
	xhr.onreadystatechange = getReadyStateHandler(responseHandler);
	xhr.open("GET", uri + "?" + queryString, true);
	xhr.send(null);
}

/**
 * Nell'invio di richieste ascincrone POST la creazione dell'oggetto XMLHttpRequest e la sua inizializzazione
 * sono operazioni di routine e pongono un problema di duplicazione del codice e riuso.
 *
 * @param uri lo URL HTTP verso cui inviare la richiesta POST
 * @param body il corpo della richiesta POST. Utilizzare il costruttore FormData() per dati che provengono da un form
 * @param responseHandler il metodo di di callback per l'elaborazione della risposta HTTP. Il metodo di callback
 * deve essere adibito alla ricezione di due parametri: il primo per la risposta testuale (responseText) e il
 * secondo per la risposta in formato XML (responseXML).
 */
function ajaxCallPost(uri, body, responseHandler) {
	var xhr = new XMLHttpRequest();
	showWaitGadget(true);
	xhr.onreadystatechange = getReadyStateHandler(responseHandler);
	xhr.open("POST", uri, true);
	xhr.send(body);
}

/*
 * Nell'invio di richieste asincrone il controllo di
 * XMLHttpRequest.readyState
 * XMLHttpRequest.status
 * rappresenta codice di routine e implica un problema di duplicazione e riutilizzo.
 * 
 * Ecco un tipico esempio d'uso di questo metodo:
 * var myxhr = new XMLHttpRequest();
 * myxhr.onreadystatechange = getReadyStateHandler(responseXMLHandler);
 * 
 * @returns una funzione di callback per la gestione della risposta. Controlla lo stato di
 * invio e ricezione della risposta ovvero se essa è stata ricevuta e se essa è valida.
 */
function getReadyStateHandler(responseHandler) {
	return function() {
		if(this.readyState == 4) {
			setTimeout(function() {showWaitGadget(false);}, 150);
			
			if(this.status == 200 || this.status == 304) {
					responseHandler(this.responseText, this.responseXML);
			}
			else {
				alert("Errore nella ricezione di una risposta asincrona.");
			}
		}
	};
}

/*
 * Mostra una gadget di attesa o lo nasconde. Il gadget viene inserito se non è già
 * presente
 * 
 * @param show valore booleano che indica se (continuare a) mostrare il gadget o se
 * nasconderlo
 */
function showWaitGadget(show) {
	if(show) {
		if(document.getElementById("waitGadget") == null) {
			var waitGadget = document.createElement("div");
			waitGadget.setAttribute("id", "waitGadget");
			waitGadget.style.position = "fixed";
			waitGadget.style.right = "10px";
			waitGadget.style.top = "10px";
			
			var waitImage = document.createElement("img");
			waitImage.setAttribute("src", "images/wait.gif");
			waitImage.width = "40";
			waitImage.height = "40";
			
			waitGadget.appendChild(waitImage);
			document.getElementsByTagName("body")[0].appendChild(waitGadget);
		}
	}
	else {
		var gadget = document.getElementById("waitGadget");
		if(gadget != null) {
			gadget.parentNode.removeChild(gadget);
		}
	}
}
