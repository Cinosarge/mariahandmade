/**
 * L'oggetto ModalBoxDescriptor permette di descrivere le caratteristiche che deve
 * avere una modal box. I metodi disponibili sono
 *
 * setTitle() - imposta il titolo della modal (opzionale)
 * setMessage()- imposta il messaggio della modal (consigliato)
 * setLeftOptionText() - stringa da inserire nel bottone in basso a sinistra (opzionale)
 * setLeftOptionFunction() - funzione che deve eseguire il bottone sinistro (opzionale)
 * setRightOptionText() - stringa da inserire nel bottone in basso a destra (consigliato)
 * setRightOptionFunction() - funzione che deve eseguire il bottone destro (consigliato)
 * setExitFunction() - funzione che deve eseguire il bottone di chiusura della modal (opzionale)
 *
 * Quando si imposta del testo per un bottone il bottone viene creato, altrimenti no.
 * Se un bottone viene creato deve essere specificata anche una funzione da svolgere altrimenti
 * appare una alert che informa della sua mancanza. Questo ad eccezione del pulsante di chiusura
 * per il quale si può non specificare una funzione e di default elimina tutto il sotto-albero
 * della modal-box dalla pagina HTML.
 */


function ModalBoxDescriptor() {
	
	// Setter methods
	this.setTitle = function(title) {
		this.title = title;
	}
	
	this.setMessage = function(message) {
		this.message = message;
	}
	
	this.setLeftOptionText = function(leftOptionText) {
		this.leftOptionText = leftOptionText;
	}
	
	this.setRightOptionText = function(rightOptionText) {
		this.rightOptionText = rightOptionText;
	}
	
	this.setLeftOptionFunction = function(leftOptionFunction) {
		this.leftOptionFunction = leftOptionFunction;
	}
	
	this.setRightOptionFunction = function(rightOptionFunction) {
		this.rightOptionFunction = rightOptionFunction;
	}
	
	this.setExitOptionFunction = function(exitOptionFunction) {
		this.exitOptionFunction = exitOptionFunction;
	}
	
	// Getter methods
	this.getTitle = function() {
		return this.title;
	}
	
	this.getMessage = function() {
		return this.message;
	}
	
	this.getLeftOptionText = function() {
		return this.leftOptionText;
	}
	
	this.getRightOptionText = function() {
		return this.rightOptionText;
	}
	
	this.getLeftOptionFunction = function() {
		return this.leftOptionFunction;
	}
	
	this.getRightOptionFunction = function() {
		return this.rightOptionFunction;
	}
	
	this.getExitOptionFunction = function() {
		return this.exitOptionFunction;
	}
}

/**
 * Un oggetto ModalBoxFactory è in grado di creare il codice HTML necessario alla
 * rappresentazione della modal box descritta da modal-box.css.
 
 * Il costruttre riceve in input un oggetto ModalBoxDescriptor opportunamente
 * inizializzato e il metodo makeModalBox() ne genera una sulla base delle specifiche.
 */
function ModalBoxFactory(modalBoxDescriptor) {
	this.modalSpecs = modalBoxDescriptor;
	
	this.makeModalBox = function() {
		var modalBox = null;
		
		//Header
		var header = document.createElement("header");
		
		var title = this.modalSpecs.getTitle();
		if(title != undefined) {
			title = document.createTextNode(title);
			header.appendChild(title);
		}
		
		var exitButton = document.createElement("button");
		exitButton.setAttribute("name", "exit");
		exitButton.innerHTML = "X";
		var exitFunction = this.modalSpecs.getExitOptionFunction();
		if(exitFunction != undefined) {
			exitButton.addEventListener("click", exitFunction);
		}
		else {
			exitButton.setAttribute( "onclick", "document.getElementById('grey-pane').parentElement.removeChild(document.getElementById('grey-pane'));" );
		}
		
		header.appendChild(exitButton);
		
		//Section
		var section = document.createElement("section");
		var messageBox = document.createElement("p");
		
		var message = this.modalSpecs.getMessage();
		if(message != undefined) {
			message = document.createTextNode(message);
		}
		else {
			message = document.createTextNode("Messaggio non disponibile");
		}
		
		messageBox.appendChild(message);
		section.appendChild(messageBox);
		
		//Footer
		var footer = document.createElement("footer");
		
		var leftButtonText = this.modalSpecs.getLeftOptionText();
		if(leftButtonText != undefined) {
			var leftButton = document.createElement("button");
			leftButton.setAttribute("name", "leftOption");
			leftButton.innerHTML = leftButtonText;
			
			var leftButtonFunction = this.modalSpecs.getLeftOptionFunction();
			if(leftButtonFunction != undefined) {
				leftButton.addEventListener("click", leftButtonFunction);
			}
			else {
				leftButton.addEventListener("click", function() {alert('Il pulsante sinistro non ha un listener sul click.')});
			}
			footer.appendChild(leftButton);
		}
	
		var rightButtonText = this.modalSpecs.getRightOptionText();
		if(rightButtonText != undefined) {
			var rightButton = document.createElement("button");
			rightButton.setAttribute("name", "rightOption");
			rightButton.innerHTML = rightButtonText;
			
			var rightButtonFunction = this.modalSpecs.getRightOptionFunction();
			if(rightButtonFunction != undefined) {
				rightButton.addEventListener("click", rightButtonFunction);
			}
			else {
				rightButton.addEventListener("click", function() {alert('Il pulsante destro non ha un listener sul click.')});
			}
			footer.appendChild(rightButton);
		}
		
		modalBox = document.createElement("div");
		modalBox.setAttribute("class", "modal-box");
		
		modalBox.appendChild(header);
		modalBox.appendChild(section);
		modalBox.appendChild(footer);
		
		modalBoxGreyPane = document.createElement("div");
		modalBoxGreyPane.setAttribute("id", "grey-pane");
		
		modalBoxGreyPane.appendChild(modalBox);
		
		return modalBoxGreyPane;
	}
}
