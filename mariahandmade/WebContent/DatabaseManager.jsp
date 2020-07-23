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
	<link rel="stylesheet" href="css/notification.css" media="screen"/>
	
	<link rel="icon" type="image/x-icon" href="favicon.ico"/>
	
	<script src="scripts/ajax/dbManagement.js"></script>
	
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
						<li><a href="DatabaseManager.jsp">Gestione database</a></li>
					</ul>
				</nav>
			</div>
			<hr/>
		</section>
		
		<div class="tm-container float-wrapper">
			<section class="site-body">
				<h1>Crea il database</h1>
				<p>Crea il database ed aggiunge ad esso le informazioni essenziali per poter accedere come amministratore.</p>
				<button onClick="manageDatabase('create');">Crea</button>
				<h1>Popola il database</h1>
				<p>Il database viene popolato con dati d'esempio per testasre il funzionamento del sito.</p>
				<button onClick="manageDatabase('populate');">Popola</button>
				<h1>Distruggi il database</h1>
				<p>L'operazione è irreversibile e non chiede conferma. Assicurarsi di possedere almeno un backup in fomrato xml dei dati.</p>
				<button onClick="manageDatabase('destroy');">Distruggi</button>
			</section>
		</div>
	</div>
	<!-- nothig goes here -->
	<%@ include file="include/footer.jspf" %>
	
	<!-- Notification area - this is fixed and goes top right -->
	<div class="notification">
		<p class="message"></p>
		<p class="details"></p>
	</div>
</body>
</html>
