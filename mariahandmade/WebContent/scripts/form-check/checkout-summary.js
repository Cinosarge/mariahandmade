/*
 * TODO - CI SONO DEGLI ERRORI PER VALORI NULLI, VALORI STRINGA
 * E LA STRINGA VUOTA.
 */ 

/**
 * Questa soluzione è semplice ma è molto dipendente dal codice HTML.
 * 
 * Una soluzione alternativa potrebbe essere l'invio di una richiesta
 * asincrona con il numero di unità desiderate dei prodotti nel
 * carrello nell'attesa di una risposta con il totale della spesa.
 */
function calculateAmount() {
	var priceArray = document.getElementsByClassName("price");
	var dealArray = document.getElementsByClassName("deal");
	var total = 0;
	
	for(var i = 0; i < priceArray.length; i++) {
		total += priceArray[i].innerHTML.substring(9) *
			dealArray[i].getElementsByTagName("input")[0].value;
	}
	
	document.getElementsByClassName("totalAmount")[0].innerHTML = total;
}

/**
 * Controlla che i prodotti richiesti siano in numero inferiore o uguale rispetto
 * alle unità disponibili.
 * 
 * Questa soluzione è semplice ma è molto dipendente dal codice HTML.
 * 
 * Una soluzione alternativa potrebbe essere l'invio di una richiesta
 * asincrona con il numero di unità desiderate dei prodotti nel
 * carrello nell'attesa di una risposta con le unità che possono essere,
 * in modo possibile, inserite nel carrello.
 */
function dealCheck() {
	var dealArray = document.getElementsByClassName("deal");
	
	for(var i = 0; i < dealArray.length; i++) {
		if(dealArray[i].getElementsByTagName("input")[0].value < 0) {
			dealArray[i].getElementsByTagName("input")[0].value = 0;
		}
		
		if(dealArray[i].getElementsByTagName("input")[0].value > dealArray[i].innerHTML.split("/")[1].split(" ")[0].split(";")[1]) {
			dealArray[i].getElementsByTagName("input")[0].value = dealArray[i].innerHTML.split("/")[1].split(" ")[0].split(";")[1];
		}
	}
}
