<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html>
<head>
	<title>Maria Handmade</title>
	<meta charset="utf-8"/>
	<meta name="viewport" content="width=device-width, initial-scale=1.0"/>
		
	<link rel="stylesheet" href="css/screen-gt1200px.css" media="screen"/>
	<link rel="stylesheet" href="css/screen-lt770px.css" media="screen AND (max-width:770px)"/>
	<link rel="stylesheet" href="css/order.css" media="screen"/>
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
						<li class="back"><a href="getUserList.do">indietro</a></li>
						<li><a href="/mariahandmade/">Home</a></li>
						<li><a href="AdminHome.jsp">Gestione</a></li>
						<li><a href="getUserList.do">Lista utenti</a></li>
						<li><a href="#">Dettaglio spese</a></li>
					</ul>
				</nav>
			</div>
			<hr/>
		</section>
		
		<div class="tm-container float-wrapper">
			<section class="site-body">
				<c:choose>
					<c:when test="${empty orders}">
						<h1>Nessun ordine effettuato.</h1>
					</c:when>
					
					<c:otherwise>
						<h1>Ordini effettuati.</h1>
					</c:otherwise>
				</c:choose>
				
				<c:forEach var="order" items="${orders}">
					<h2>Ordine n.${order.value.orderNumber} effettuato in data ${order.value.data}</h2>
					<ul>
						<c:forEach var="purchase" items="${order.value.purchaseList}">
							<li><c:out value="${purchase.units}" /> x <c:out value="${purchase.product.name}" default="Prod. P${purchase.product.code}" /> al prezzo originale di &euro;<c:out value="${purchase.originalPrice}" /></li>
						</c:forEach>
					</ul>
					<p>Tot. &euro;${order.value.total}</p>
				</c:forEach>
			</section>
		</div>
	</div>
	<!-- nothig goes here -->
	<%@ include file="include/footer.jspf" %>
</body>
</html>
