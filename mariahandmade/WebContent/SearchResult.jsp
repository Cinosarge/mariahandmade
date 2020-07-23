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
	<link rel="stylesheet" href="css/product.css" media="screen" /> <!-- NEW -->
	
	
	<link rel="icon" type="image/x-icon" href="favicon.ico"/>
	
	<script src="scripts/graphics/ModalBoxFactory.js"></script>
	<script src="scripts/ajax/logoutManagement.js"></script>
	<script src="scripts/ajax/cartManagement.js"></script> <!-- NEW -->
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
						<li><a href="#">Ricerca</a></li>
					</ul>
				</nav>
			</div>
			<hr/>
		</section>
		
		<div class="tm-container float-wrapper">
			<section class="site-body">
				<h1>Risultati della ricerca</h1>
				<c:forEach var="product" items="${requestScope.searchResult}">
					<div class="product">
						<img src="/static/images/products/${product.images}" height="200px" />
						<span class="name"><c:out value="${product.name}" default="Prod. P${product.code}" /></span>
						<span class="description"><c:out value="${product.description}" default="${product.type}" /></span>
						<span class="price">Prezzo: &euro;<c:out value="${product.price}" /></span>
						<button onClick="addToCart('${product.code}')">Aggiungi al carrello.</button>
					</div>
				</c:forEach>
			</section>
		</div>
	</div>
	<!-- nothig goes here -->
	<%@ include file="include/footer.jspf" %>
</body>
</html>
