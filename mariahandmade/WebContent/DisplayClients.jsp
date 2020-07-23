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
	<link rel="stylesheet" href="css/pretty-table.css" media="screen"/>
	
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
						<li><a href="getUserList.do">Lista utenti</a></li>
					</ul>
				</nav>
			</div>
			<hr/>
		</section>
		
		<div class="tm-container float-wrapper">
			<section class="site-body">
				<h1>Lista degli utenti</h1>
				<table class="pretty">
					<tr>
						<th>Username</th>
						<th>Nome</th>
						<th>Cognome</th>
						<th>Mail</th>
						<th>Telefono</th>
						<th>Ordini</th>
					</tr>
				
				
					<c:forEach var="user" items="${userList}">
						<tr>
							
							<!-- USERNAME -->
							<td>
								<c:out value="${user.account.username}" />
							</td>
							
							<!-- NOME -->
							<td>
								<c:out value="${user.name}" />
							</td>
							
							<!-- COGNOME -->
							<td>
								<c:out value="${user.surname}" />
							</td>
							
							<!-- MAIL -->
							<td>
								<c:out value="${user.mail}" />
							</td>
							
							<!-- TELEFONO -->
							<td>
								<c:out value="${user.phone}" />
							</td>
							
							<!-- Dettaglio delle spese del cliente -->
							<td>
								<a href="purchaseDetails.do?userid=${user.userID}">Dettaglio ordini</a>
							</td>
						</tr>
					</c:forEach>
				</table>				
			</section>
		</div>
	</div>
	<!-- nothig goes here -->
	<%@ include file="include/footer.jspf" %>
</body>
</html>
