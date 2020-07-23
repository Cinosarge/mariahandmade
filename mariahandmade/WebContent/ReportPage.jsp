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
						<li class="back"><a href="AdminHome.jsp">indietro</a></li>
						<li><a href="/mariahandmade/">Home</a></li>
						<li><a href="AdminHome.jsp">Gestione</a></li>
						<li><a href="ReportPage.jsp">Genera report</a></li>
					</ul>
				</nav>
			</div>
			<hr/>
		</section>
		
		<div class="tm-container float-wrapper">
			<section class="site-body">
				<h3>Prodotti carenti</h3>
				Genera una lista di prodotti le cui unità disponibili sono inferiori alla scorta minima prevista.
				La lista include i prodotti con un numero di unità nullo.
				<a href="report.do?reportNumber=1">Genera report</a>
				<h3>Fascia di prezzo</h3>
				Genera una lista di prodotti in base alla fascia di prezzo.
				<form method="GET" action="report.do">
					<input type="hidden" name="reportNumber" value="2" />
					Da &euro;&nbsp;<input type="number" name="min" required="required" />
					A &euro;&nbsp;<input type="number" name="max" required="required" />
					<input type=submit value="Genera report" />
				</form>
				<h3>Ordini da evadere</h3>
				Genera una lista di ordini che non sono stati ancora evasi e permette di marcarli come spediti.
				<a href="report.do?reportNumber=3">Genera report</a>
			</section>
		</div>
	</div>
	<!-- nothig goes here -->
	<%@ include file="include/footer.jspf" %>
</body>
</html>
