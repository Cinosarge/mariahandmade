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
						<li class="back"><a href="/mariahandmade/">indietro</a></li>
						<li><a href="/mariahandmade/">Home</a></li>
						<li><a href="AdminHome.jsp">Gestione</a></li>
					</ul>
				</nav>
			</div>
			<hr/>
		</section>
		
		<div class="tm-container float-wrapper">
			<section class="site-body">
				<h1>Gestione del magazzino</h1>
				<ul>
					<li><a href="InsertProduct.jsp">Inserisci un nuovo prodotto</a></li>
					<li><a href="fetchProducts.do?forwardDestination=GestioneMagazzino.jsp">Aggiorna il magazzino</a></li>
					<li><a href="DatabaseManager.jsp">Gestisci il database</a></li>
					<li><a href="XMLImportExport.jsp">Importazione ed esportazione dei dati</a></li>
					<li><a href="ReportPage.jsp">Genera report</a></li>
				</ul>
				
				<br />
				
				<h1>Gestione clienti registrati</h1>
				<ul>
					<li><a href="getUserList.do">Visualizzazione della lista di utenti e relative spese.</a></li>
				</ul>
			</section>
		</div>
	</div>
	<!-- nothig goes here -->
	<%@ include file="include/footer.jspf" %>
</body>
</html>
