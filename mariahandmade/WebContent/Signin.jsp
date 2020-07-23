<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html>
<head>
	<title>Maria Handmade</title>
	<meta charset="utf-8"/>
	<meta name="viewport" content="width=device-width, initial-scale=1.0"/>
	
	<link rel="stylesheet" href="css/screen-gt1200px.css" media="screen"/>
	<link rel="stylesheet" href="css/screen-lt770px.css" media="screen AND (max-width:770px)"/>
	<link rel="stylesheet" href="css/modal-box.css" media="screen"/>
	<link rel="stylesheet" href="css/floating-form.css" media="screen"/>
	
	<link rel="icon" type="image/x-icon" href="favicon.ico"/>
	
	<script src="scripts/graphics/ModalBoxFactory.js"></script>
	<script src="scripts/ajax/logoutManagement.js"></script>
	
	<script src="scripts/ajax/AjaxCallTemplate.js"></script>
	<script src="scripts/ajax/usernameManagement.js"></script>
	
	<script src="scripts/form-check/password-match.js"></script>
</head>
<body class="viewport-overlap">
	<div class="footer-spacer">
		<%@ include file="include/header.jspf" %>
		
		<section class="path">
			<div class="tm-container float-wrapper">
				<nav class="path-nav">
					<ul>
						<li class="back"><a href="/mariahandmade/">indietro</a></li>
						<li><a href="/mariahandmade/">Home</a></li>
						<li><a href="Signin.jsp">Registrazione</a></li>
					</ul>
				</nav>
			</div>
			<hr/>
		</section>
		
		<div class="tm-container float-wrapper">
			<section class="site-body">
				<h1>Registrazione</h1>
				<form class="tm-floating-form" method="POST" action="signin.do" enctype="multipart/form-data">
					<fieldset>
						<legend>Foto del profilo</legend>
						<div>
							<img id="profilePhotoPreview" src="/static/images/profiles/noimage.png" width="150" height="auto" alt="Profile image" />
							<input name="profilePhotoFile" type="file" accept="image/*" />
						</div>
					</fieldset>
					
					<fieldset>
						<legend>Dati personali<abbr title="Campi con asterisco obbligatori">*</abbr></legend>
						<div>
							<label for="fiscalCode">Codice fiscale*</label>
							<input id="fiscalCode" type="text" name="fiscalCode" required="required" pattern="[A-Z][A-Z][A-Z][A-Z][A-Z][A-Z][0-9][0-9][A-Z][0-9][0-9][A-Z][0-9][0-9][0-9][A-Z]"/>
						</div>
						<div>
							<label for="firstName">Nome*</label>
							<input id="firstName" type="text" name="firstName" required="required" pattern="[A-Za-z]+" minlength="3" maxlength="15" />
						</div>
						<div>
							<label for="secondName">Secondo nome</label>
							<input id="secondName" type="text" name="secondName" maxlength="15" pattern="[A-Za-z]+" minlength="3" maxlength="15" />
						</div>
						<div>
							<label for="surname">Cognome*</label>
							<input id="surname" type="text" name="surname" required="required" maxlength="15" pattern="[A-Za-z]+" minlength="3" maxlength="15" />
						</div>
						<div>
							<label>Sesso*</label>
							<label for="male">M</label><input id="male" type="radio" name="gender" value="male" required="required"  />
							<label for="female">F</label><input id="female" type="radio" name="gender" value="female" required="required" />
							<label for="other">Altro</label><input id="other" type="radio" name="gender" value="other" required="required" checked="checked" />
						</div>
						<div>
							<label for="mail">Email*</label>
							<input id="mail" type="email" name="mail" required="required" />
						</div>
						<div>
							<label for="phone">Telefono</label>
							<input id="phone" type="text" name="phone" minlength="10" />
						</div>
						<div>
							<label for="birthDate">Data di nascita*</label>
							<input id="birthDate" type="date" name="birthDate" size="10" required="required" pattern="(0[1-9]-0[1-9]|[12][0-9]-0[^02]|[12][0-9]-1[0-2]|30-0[^2]|31-0[^02469]|31-10|31-^11|31-12|0[1-9]-10|30-10|[12][0-9]-10|0[1-9]-11|30-11|[12][0-9]-11|0[1-9]-12|30-12|1[0-9]-02|2[0-8]-02)-[12][901][0-9][0-9]" />
						</div>
						<div>
							<label for="birthCity">Citt&agrave;  di nascita*</label>
							<input id="birthCity" type="text" name="birthCity" required="required" pattern="([A-Z]{0,1}[a-z]+[ ]{0,1})+" />
						</div>
						<div>
							<label for="creditCard">Carta di credito</label>
							<input id="creditCard" type="text" name="creditCard" pattern="[0-9]{16}" />
						</div>
					</fieldset>
					
					<fieldset>
						<legend>Indirizzo<abbr title="Campi con asterisco obbligatori">*</abbr></legend>
						<div>
							<label for="province">Provincia*</label>
							<input id="province" type="text" name="province" required="required" minlength="2" maxlength="2" pattern="[A-Z][A-Z]" />
						</div>
						<div>
							<label for="city">Citt&agrave;/Comune*</label>
							<input id="city" type="text" name="city" required="required" pattern="([A-Z]{0,1}[a-z]+[ ]{0,1})+" />
						</div>
						<div>
							<label for="cap">CAP*</label>
							<input id="cap" type="text" name="cap" required="required" pattern="[0-9][0-9][0-9][0-9][0-9]" />
						</div>
						<div>
							<label for="street">Via/Piazza*</label>
							<input id="street" type="text" name="street" required="required" pattern="([A-Z]{0,1}[a-z]+[ ]{0,1})+" />
						</div>
						<div>
							<label for="houseNumber">Numero civico*</label>
							<input id="houseNumber" type="text" name="houseNumber" required="required" pattern="[1-9][0-9]*" />
						</div>
						<div>
							<label for="addressTypeSelect">Tipologia indirizzo</label>
							<select id="addressTypeSelect" name="addressType" size="1" onChange="showShipmentAddress(this);">
								<option value="both">Residenza e spedizione</option>
								<option value="residenza">Solo residenza</option>
							</select>
						</div>
						
						<div id="shipmentAddress" style="display: none;">
							<hr />
							<h3>Indicare un indirizzo di spedizione.</h3>
							<div>
								<label for="shipmentProvince">Provincia*</label>
								<input id="shipmentProvince" class="shipmentField" type="text" name="shipmentProvince" pattern="[A-Z][A-Z]" />
							</div>
							<div>
								<label for="shipmentCity">Citt&agrave;/Comune*</label>
								<input id="shipmentCity" class="shipmentField" type="text" name="shipmentCity" pattern="([A-Z]{0,1}[a-z]+[ ]{0,1})+" />
							</div>
							<div>
								<label for="shipmentCap">CAP*</label>
								<input id="shipmentCap" class="shipmentField" type="text" name="shipmentCap" pattern="[0-9][0-9][0-9][0-9][0-9]" />
							</div>
							<div>
								<label for="shipmentStreet">Via/Piazza*</label>
								<input id="shipmentStreet" class="shipmentField" type="text" name="shipmentStreet" pattern="([A-Z]{0,1}[a-z]+[ ]{0,1})+"/>
							</div>
							<div>
								<label for="shipmentHouseNumber">Numero civico*</label>
								<input id="shipmentHouseNumber" class="shipmentField" type="text" name="shipmentHouseNumber" pattern="[1-9][0-9]*" />
							</div>
						</div>
					</fieldset>
					
					<fieldset>
						<legend>Dati di accesso<abbr title="Campi con asterisco obbligatori">*</abbr></legend>
						<div>
							<label for="username">Username*</label>
							<input id="username" type="text" name="username" required="required" minlength="5" maxlength="15" pattern="[A-Za-z0-9אטילעש]{3, 18}" oninput="checkUsername(this)" />
						</div>
						<div>
							<label for="password">Password*</label>
							<input id="password" type="password" name="password" required="required" pattern="[A-Za-z0-9!£$%&/()=?^אטילעש.,:;]{8, 15}" />
						</div>
						<div>
							<label for="passwordRetype">Conferma password*</label>
							<input id="passwordRetype" type="password" name="passwordRetype" required="required" pattern="[A-Za-z0-9!£$%&/()=?^אטילעש.,:;]{8, 15}" oninput="matchPasswords()" />
						</div>
					</fieldset>
					
					<div>
						Inserisci tutti i campi obbligatori e clicca <input type="submit" value="registrami!" />
					</div>
				</form>
				
				<div class="policies">
					<h2>Regolamento</h2>
					<p>Lorem ipsum dolor sit amet, consectetur adipisici elit, sed eiusmod tempor incidunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquid ex ea commodi consequat. Quis aute iure reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint obcaecat cupiditat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.</p>
					<h2>Privacy policy</h2>
					<p>Lorem ipsum dolor sit amet, consectetur adipisici elit, sed eiusmod tempor incidunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquid ex ea commodi consequat. Quis aute iure reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint obcaecat cupiditat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.</p>
				</div>
			</section>
		</div>
	</div>
	<!-- nothing goes here -->
	<%@ include file="include/footer.jspf" %>
	
	<script>
	function showShipmentAddress(addressSelect) {
		if(addressSelect.value == "residenza") {
			document.getElementById("shipmentAddress").style.display = "block";
			var shipmentFields = document.getElementsByClassName("shipmentField");
			shipmentFields[0].required = true;
			shipmentFields[1].required = true;
			shipmentFields[2].required = true;
			shipmentFields[3].required = true;
			shipmentFields[4].required = true;
		}
		else {
			document.getElementById("shipmentAddress").style.display = "none";
			var shipmentFields = document.getElementsByClassName("shipmentField");
			shipmentFields[0].required = false
			shipmentFields[1].required = false;
			shipmentFields[2].required = false;
			shipmentFields[3].required = false;
			shipmentFields[4].required = false;
		}
	}
	
	/*
	 * Questa chiamata a metodo verifica il tipo di indirizzo selezionato
	 * dopo il referesh della pagina
	 */
	var addressSelect = document.getElementById("addressTypeSelect");
	showShipmentAddress(addressSelect);
	
	/*
	 * Questo codice permette la preview di una immagine. E' stato riadattato da
	 * http://bytes.schibsted.com/the-magic-of-createobjecturl/
	 */
    var fileInput = document.querySelector('input[type="file"]');
    var preview = document.getElementById('profilePhotoPreview');

    fileInput.addEventListener('change', function(event) {
        preview.onload = function() {        
            window.URL.revokeObjectURL(this.src);
        };
        var url = URL.createObjectURL(event.target.files[0]);
        preview.setAttribute('src', url);
    }, false);
</script>
	
</body>
</html>
