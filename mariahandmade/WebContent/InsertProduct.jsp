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
</head>
<body class="viewport-overlap">
	<div class="footer-spacer">
		<%@ include file="include/header.jspf" %>
		
		<section class="path">
			<div class="tm-container float-wrapper">
				<nav class="path-nav">
					<ul>
						<li class="back"><a href="AdminHome.jsp">indietro</a></li>
						<li><a href="/mariahandmade/">Home</a></li>
						<li><a href="AdminHome.jsp">Gestione</a></li>
						<li><a href="InsertProduct.jsp">Aggiunta di un prodotto</a></li>
					</ul>
				</nav>
			</div>
			<hr/>
		</section>
		
		<div class="tm-container float-wrapper">
			<section class="site-body">
				<h1>Inserisci un prodotto</h1>
				<form class="tm-floating-form" method="POST" action="insertProduct.do" enctype="multipart/form-data">
					<fieldset>
						<legend>Foto del prodotto</legend>
						<div>
							<img id="productPhotoPreview" src="/static/images/products/noimage.png" width="150" height="auto" alt="Product image" />
							<input name="productPhotoFile" type="file" accept="image/*" />
						</div>
					</fieldset>
					
					<fieldset>
						<legend>Dati del prodotto<abbr title="Campi con asterisco obbligatori">*</abbr></legend>
						<div>
							<label for="productName">Nome</label>
							<input id="productName" type="text" name="productName" pattern="[A-Za-z ]+" />
						</div>
						<div>
							<label for="line">Linea*</label>
							<select id="line" name="line">
								<option value="uomo">Uomo</option>
								<option value="donna">Donna</option>
								<option value="unisex">Unisex</option>
							</select>
						</div>
						<div>
							<label for="type">Tipo*</label>
							<select id="type" name="type">
								<option value="anello">Anello</option>
								<option value="biglietto">Biglietto augurale</option>
								<option value="bomboniera">Bomboniera</option>
								<option value="bracciale">Bracciale</option>
								<option value="ciondolo">Ciondolo</option>
								<option value="collana">Collana</option>
								<option value="orecchino">Orecchino</option>
								<option value="partecipazione">Partecipazione</option>
								<option value="portachiave">Portachiavi</option>
							</select>
						</div>
						<div>
							<label for="availableUnits">Unit&agrave;*</label>
							<input id="availableUnits" type="number" name="availableUnits" required="required" pattern="[0-9]+" />
						</div>
						<div>
							<label for="minInventory">Scorta minima</label>
							<input id="minInventory" type="number" name="minInventory" pattern="[0-9]+" />
						</div>
						<div>
							<label for="price">Prezzo vendita*</label>
							<input id="price" type="text" name="price" required="required" pattern="[0-9]+([.,][0-9]+)*" />
						</div>
						<div>
							<label for="cost">Prezzo acquisto</label>
							<input id="cost" type="text" name="cost" pattern="[0-9]+([.,][0-9]+)*" />
						</div>
						<div>
							<label for="description">Descrizione</label>
							<input id="description" type="text" name="description" pattern="[אטילעש0-9A-Za-z .:;]+" maxlength="256" />
						</div>
					</fieldset>
					
					<fieldset>
						<legend>Materiali componenti</legend>
						<label for="fimo">Fimo</label><input id="fimo" type="checkbox" name="material" value="fimo" />
						<label for="pietre-dure">Pietre dure</label><input id="pietre-dure" type="checkbox" name="material" value="pietre dure" />
						<label for="swarovski">Swarovski</label><input id="swarovski" type="checkbox" name="material" value="swarovski" />
						<label for="wired">Wired</label><input id="wired" type="checkbox" name="material" value="wired" />
						<label for="altro">Altro...</label><input id="altro" type="checkbox" name="material" value="altro" />
					</fieldset>
					
					<div>
						Inserisci tutti i campi obbligatori e clicca <input type="submit" value="inserisci!" />
					</div>
				</form>
				
				<div class="policies">
					<h2>Politica di gestione dei prodotti</h2>
					<p>Lorem ipsum dolor sit amet, consectetur adipisici elit, sed eiusmod tempor incidunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquid ex ea commodi consequat. Quis aute iure reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint obcaecat cupiditat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.</p>
					<h2>Esposizione dei prezzi al pubblico</h2>
					<p>Lorem ipsum dolor sit amet, consectetur adipisici elit, sed eiusmod tempor incidunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquid ex ea commodi consequat. Quis aute iure reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint obcaecat cupiditat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.</p>
				</div>
			</section>
		</div>
	</div>
	<!-- nothing goes here -->
	<%@ include file="include/footer.jspf" %>
	
	<script>
		/*
		 * Questo codice permette la preview di una immagine. E' stato riadattato da
		 * http://bytes.schibsted.com/the-magic-of-createobjecturl/
		 */
	    var fileInput = document.querySelector('input[type="file"]');
	    var preview = document.getElementById('productPhotoPreview');
	
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
