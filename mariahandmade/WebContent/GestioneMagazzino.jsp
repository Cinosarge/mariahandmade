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
	<link rel="stylesheet" href="css/product-stripe.css" media="screen"/>
	<link rel="stylesheet" href="css/notification.css" media="screen"/>
	
	<link rel="icon" type="image/x-icon" href="favicon.ico"/>
	
	<script src="scripts/graphics/ModalBoxFactory.js"></script>
	<script src="scripts/ajax/logoutManagement.js"></script>
	<script src="scripts/ajax/deleteProduct.js"></script>
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
						<li><a href="fetchProducts.do?forwardDestination=GestioneMagazzino.jsp">Aggiorna il magazzino</a></li>
					</ul>
				</nav>
			</div>
			<hr/>
		</section>
		
		<div class="tm-container float-wrapper">
			<section class="site-body">
				<!-- ELIMINAZIONE ASINCROMA E ACCESSO ALLA PAGINA DI MODIFICA -->
				<h1>Gestione del magazzino</h1>
				<c:forEach var="product" items="${requestScope.productListBuffer}">
					<div class="product float-wrapper">
						<img src="/static/images/products/${product.images}" height="200px" />
						<div class="product-side-data" >
							<span class="name"><c:out value="${product.name}" default="Prod. P${product.code}" /></span>
							<span class="description"><c:out value="${product.description}" default="${product.type}" /></span>
							<span class="price">Prezzo: &euro;<c:out value="${product.price}" /></span>
							<span class="deal">Quantit&agrave;:&nbsp;<c:out value="${product.availableUnits}" /> disponibili</span>
						</div>
						<button onClick="markProductAsDeleted(${product.code})">Marca come eliminato</button>
						<a href="updateProduct.do?productCode=${product.code}"><button>Modifica</button></a>
					</div>
				</c:forEach>
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
