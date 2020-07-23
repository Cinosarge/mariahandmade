<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html>
<head>
	<title>Maria Handmade</title>
	<meta charset="utf-8"/>
	<meta name="viewport" content="width=device-width, initial-scale=1.0"/>
	
	<link rel="stylesheet" href="css/screen-gt1200px.css" media="screen"/>
	<link rel="stylesheet" href="css/notification.css" media="screen"/>
	<link rel="stylesheet" href="css/screen-lt770px.css" media="screen AND (max-width:770px)"/>
	<link rel="stylesheet" href="css/modal-box.css" media="screen"/>
	<link rel="stylesheet" href="css/product.css" media="screen"/>
	<link rel="stylesheet" href="css/slideshow.css" media="screen"/>
	
	<link rel="icon" type="image/x-icon" href="favicon.ico"/>
	
	<script src="scripts/graphics/slideshow.js"></script>
	<script src="scripts/ajax/cartManagement.js"></script>
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
					</ul>
				</nav>
			</div>
			<hr/>
		</section>
		
		<section class="slideshow">
			<div class="tm-container generic-relative-box">
					<img id="sliding-img" src="/static/images/slideshow/0.png" width="1200" alt="slideshow image"/>
					<div class="sw-button sw-prev-button">
						<img src="images/slideshow/prev.png" width="64" alt="slideshow previous image" onclick="prevImg()"/>
					</div>
					<div class="sw-button sw-next-button">
						<img src="images/slideshow/next.png" width="64" alt="slideshow next image" onclick="nextImg()"/>
					</div>
			</div>
		</section>
		
		<div class="tm-container float-wrapper">
			<section class="site-body">
				<!-- Presentazione dei prodotti -->
				<c:forEach var="product" items="${productListBuffer}">
					<div class="product">
						<img src="/static/images/products/${product.images}" />
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
	
	<!-- Notification area - this is fixed and goes top right -->
	<div class="notification">
		<p class="message"></p>
		<p class="details"></p>
	</div>
</body>
</html>
