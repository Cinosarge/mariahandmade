/**
 * Controlla l'eguaglianza delle password inserite in fase di registrazione
 */
function matchPasswords() {
	var password = document.getElementById("password");
	var passwordRetype = document.getElementById("passwordRetype");
	if(password.value == passwordRetype.value) {
		passwordRetype.setCustomValidity("");
		alert("Le password corrispondono!");
	}
	else {
		passwordRetype.setCustomValidity("Le password non corrispondono");
	}
}
