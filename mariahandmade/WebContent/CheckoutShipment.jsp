<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html>
<head>
	<title>Maria Handmade</title>
	<meta charset="utf-8"/>
	<meta name="viewport" content="width=device-width, initial-scale=1.0"/>
	<link rel="stylesheet" href="css/screen-gt1200px.css" media="screen"/>
	<link rel="stylesheet" href="css/screen-lt770px.css" media="screen AND (max-width:770px)"/>
	
	<link rel="icon" type="image/x-icon" href="favicon.ico"/>
	
	<link rel="stylesheet" href="css/modal-box.css" media="screen" />
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
						<li class="back"><a href="cart.do">indietro</a></li>
						<li><a href="/mariahandmade/">Home</a></li>
						<li><a href="cart.do">Carrello</a></li>
						
						 <!-- Per il primo passaggio funziona anche senza passare un parametro stage -->
						<li><a href="checkout.do">Ricapitolazione</a></li>
						
						<!-- INVIA IL PARAMETRO stage VIA POST PER OTTENERE LA PAGINA SHIPMENT -->
					</ul>
				</nav>
			</div>
			<hr/>
		</section>
		
		<div class="tm-container float-wrapper">
			<section class="site-body">
				<h1>Checkout Shipment</h1>
				<form method="POST" action="checkout.do">
					<select name="addressID">
						<c:forEach var="address" items="${sessionScope.userData.addressList}">
							<option value="${address.addressID}">
								<c:out value="${address.street}" />
								<c:out value="${address.houseNumber}" />
								<c:out value="${address.city}" />
								<c:out value="${address.cap}" />
								<c:out value="(${address.province})" />
							</option>
						</c:forEach>
					</select>
					<input type="hidden" name="stage" value="2" />
					<input type="submit" value="Procedi" />
				</form>
			</section>
		</div>
	</div>
	<!-- nothig goes here -->
	<%@ include file="include/footer.jspf" %>
</body>
</html>
