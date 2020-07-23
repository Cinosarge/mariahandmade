<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html>
<head>
	<title>Maria Handmade</title>
	<meta charset="utf-8"/>
	<meta name="viewport" content="width=device-width, initial-scale=1.0"/>
	
	<link rel="stylesheet" href="css/screen-gt1200px.css" media="screen"/>
	<link rel="stylesheet" href="css/screen-lt770px.css" media="screen AND (max-width:770px)"/>
	<link rel="stylesheet" href="css/product-stripe.css" media="screen" />
	
	<link rel="icon" type="image/x-icon" href="favicon.ico"/>
	
	<link rel="stylesheet" href="css/modal-box.css" media="screen" />
	<script src="scripts/graphics/ModalBoxFactory.js"></script>
	<script src="scripts/ajax/logoutManagement.js"></script>
	
	<script src="scripts/form-check/checkout-summary.js"></script>
	<script>
		window.onload = calculateAmount;
	</script>
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
					</ul>
				</nav>
			</div>
			<hr/>
		</section>
		
		<div class="tm-container float-wrapper">
			<section class="site-body">
				<h1>Checkout Summary</h1>
				<form method="POST" action="checkout.do">
					<c:forEach var="productEntry" items="${sessionScope.cart.cartProductMap}">
						<div class="product float-wrapper">
							<input type="hidden" name="productCode" value="${productEntry.value.product.code}" />
							<img src="/static/images/products/${productEntry.value.product.images}" height="200px" />
							
							<div class="product-side-data" >
								<span class="name"><c:out value="${productEntry.value.product.name}" default="Prod. P${productEntry.value.product.code}" /></span>
								<span class="description"><c:out value="${productEntry.value.product.description}" default="${productEntry.value.product.type}" /></span>
								<span class="price">Prezzo: &euro;<c:out value="${productEntry.value.product.price}" /></span>

								<span class="deal">
									Quantit&agrave;:&nbsp;<input name="deal" type="text" value="${productEntry.value.deal}" onChange="dealCheck(); calculateAmount();" />
									&nbsp;/&nbsp;<c:out value="${productEntry.value.product.availableUnits}" /> disponibili
								</span>
							
							</div>
							
						</div>
					</c:forEach>
					Totale: &euro;<span class="totalAmount"><!-- JS calcola il totale --></span>
					<input type="hidden" name="stage" value="1" />
					<input type="submit" value="Procedi" />
				</form>
			</section>
		</div>
	</div>
	<!-- nothig goes here -->
	<%@ include file="include/footer.jspf" %>
</body>
</html>
