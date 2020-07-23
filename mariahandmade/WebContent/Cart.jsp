<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html>
<head>
	<title>Maria Handmade</title>
	<meta charset="utf-8"/>
	<meta name="viewport" content="width=device-width, initial-scale=1.0"/>
	
	<link rel="stylesheet" href="css/screen-gt1200px.css" media="screen"/>
	<link rel="stylesheet" href="css/screen-lt770px.css" media="screen AND (max-width:770px)"/>
	<link rel="stylesheet" href="css/product.css" media="screen"/>
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
						<li><a href="cart.do">Carrello</a></li>
					</ul>
				</nav>
			</div>
			<hr/>
		</section>
		
		<div class="tm-container float-wrapper">
			<section class="site-body float-wrapper">
				<c:forEach var="productEntry" items="${sessionScope.cart.cartProductMap}">
					<div class="product">
						<img src="/static/images/products/${productEntry.value.product.images}" height="200px" />
						<span class="name"><c:out value="${productEntry.value.product.name}" default="Prod. P${productEntry.value.product.code}" /></span>
						<span class="description"><c:out value="${productEntry.value.product.description}" default="${productEntry.value.product.type}" /></span>
						<span class="price">Prezzo: &euro;<c:out value="${productEntry.value.product.price}" /></span>
					</div>
				</c:forEach>
			</section>
			<section class="site-body float-wrapper">
				<c:choose>
					<c:when test="${sessionScope.cart != null && not empty sessionScope.cart.cartProductMap && sessionScope.userData != null}">
						<a href="checkout.do"><button>Acquista</button></a>
					</c:when>
					
					<c:when test="${sessionScope.cart != null && not empty sessionScope.cart.cartProductMap && sessionScope.userData == null}">
						<a href="signin.do?checkoutAfterSignin=true"><button>Registrati e acquista</button></a>
					</c:when>
					
					<c:otherwise>
						<h2>Il carrello &egrave; vuoto!</h2>
					</c:otherwise>
				</c:choose>
			</section>
		</div>
	</div>
	<!-- nothig goes here -->
	<%@ include file="include/footer.jspf" %>
</body>
</html>
