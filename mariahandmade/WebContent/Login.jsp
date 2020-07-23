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
	<link rel="stylesheet" href="css/floating-form.css" media="screen"/>
	
	<link rel="icon" type="image/x-icon" href="favicon.ico"/>
	
	<script src="scripts/graphics/ModalBoxFactory.js"></script>
	<script src="scripts/ajax/logoutManagement.js"></script>
	
	<script>
		window.onload = function() {
			<c:if test="${errorMessage != null}">
				alert("${errorMessage}");
			</c:if>
		}
	</script>
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
						<li><a href="Login.jsp">Login</a></li>
					</ul>
				</nav>
			</div>
			<hr/>
		</section>
		
		<div class="tm-container float-wrapper">
			<section class="site-body">
				<form class="tm-floating-form" method="post" action="login.do">
					<fieldset>
						<legend>Accedi</legend>
						<div>
							<label>Username:</label>
							<input type="text" name="username" required />
						</div>
						<div>
							<label>Password: </label>
							<input type="password" name="password" required />
						</div>
						<div>
							<input type="submit" />
						</div>
					</fieldset>
				</form>
				
				<div class="policies">
					<h2>Non sei registrato?</h2>
					<p>Registrati cliccando <a href="Signin.jsp">qui</a>.</p>
					<h2>Regolamento</h2>
					<p>Lorem ipsum dolor sit amet, consectetur adipisici elit, sed eiusmod tempor incidunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquid ex ea commodi consequat. Quis aute iure reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint obcaecat cupiditat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.</p>
				</div>
			</section>
		</div>
	</div>
	<!-- nothig goes here -->
	<%@ include file="include/footer.jspf" %>
</body>
</html>
