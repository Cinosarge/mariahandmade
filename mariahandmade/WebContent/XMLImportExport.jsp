<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<!DOCTYPE html>
<html>
<head>
	<title>Maria Handmade</title>
	<meta charset="utf-8"/>
	<meta name="viewport" content="width=device-width, initial-scale=1.0"/>
	
	<link rel="stylesheet" href="css/screen-gt1200px.css" media="screen"/>
	<link rel="stylesheet" href="css/screen-lt770px.css" media="screen AND (max-width:770px)"/>
	<link rel="stylesheet" href="css/modal-box.css" media="screen"/>
	<link rel="stylesheet" href="css/notification.css" media="screen"/>
	
	<link rel="icon" type="image/x-icon" href="favicon.ico"/>
	
	<script src="scripts/ajax/xmlUpload.js"></script>
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
						<li><a href="XMLImportExport.jsp">Importa o esporta i dati dei prodotti</a></li>
					</ul>
				</nav>
			</div>
			<hr/>
		</section>
		
		<div class="tm-container float-wrapper">
			<section class="site-body">
				<h1>Importazione dei dati dei prodotti da un file XML</h1>
				<form id="xmlUploadForm" method="POST" enctype="multipart/form-data">
					<input type="file" name="xmlfile" />
					<input type="button" value="Carica" onClick="sendProductData();" />
				</form>
				
				<h1>Esportazione dei dati dei prodotti in un file XML</h1>
				<form action="productExport.do" method="GET">
					<input type="submit" value="Esporta e scarica" />
				</form>
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
