/**
 * 
 */
(function() {
	const regex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

	showSignIn = function() {
		document.getElementById("login").style.display = 'none';
		document.getElementById("Signin").style.display = 'block';
		document.getElementById("signInError").textContent = "";
	}

	showLogin = function() {

		document.getElementById("login").style.display = 'block';
		document.getElementById("Signin").style.display = 'none';
		document.getElementById("errors").textContent = "";
	}

	document.getElementById("link-signin").addEventListener('click', (e) => {
		showSignIn();
	});

	document.getElementById("link-login").addEventListener('click', (e) => {
		showLogin();
	});

	document.getElementById("signinbutton").addEventListener('click', (e) => {
		e.preventDefault();
		var form = e.target.closest("form");

		var password = form.querySelector("[name='pwd']").value;
		var email = form.querySelector("[name='email']").value;
		var name = form.querySelector("[name='name']").value;
		var address = form.querySelector("[name='address']").value;
		var phoneNumber = form.querySelector("[name='phoneNumber']").value;


		//controllo che l'utente inserisca una mail : somenthing@something.something
		if (!regex.test(email)) {
			document.getElementById("signInError").textContent = "This is not a real email!";
			return;
		}

		if (name.length < 5 || password.length < 5) {
			document.getElementById("signInError").textContent = "Username and password must be at least 5 characters long.";
			return;
		}

		if (name.trim() === "") {
			document.getElementById("signInError").textContent = "You can't send an empty name!";
			return;
		}
		if (password.trim() === "") {
			document.getElementById("signInError").textContent = "You can't send an empty password!";
			return;
		}


		if (form.checkValidity()) {
			//metodo post della servlet CheckLogin
			makeCall("POST", 'SignupManager', form,
				function(x) { // X è UN OGGETTO XMLHttpRequest
					if (x.readyState == XMLHttpRequest.DONE) {
						var message = x.responseText;

						switch (x.status) {
							case 200:  //richiesta andata a buon fine
								document.getElementById("errors").textContent = "FUNZIA TODOS";
								showLogin();
								break;
							case 400: // bad request
								document.getElementById("signInError").textContent = message;
								break;
							case 500: // server error
								document.getElementById("signInError").textContent = message;
								break;
						}
					}
				}
			);
		} else {
			form.reportValidity();
		}

	});

	document.getElementById("loginbutton").addEventListener('click', (e) => {
		var form = e.target.closest("form");
		if (form.checkValidity()) {
			makeCall("POST", 'LoginManager?page=toHomepage', form,
				function(x) { // X è UN OGGETTO XMLHttpRequest
					if (x.readyState == XMLHttpRequest.DONE) {
						var message = x.responseText;
						switch (x.status) {
							case 200:  //richiesta andata a buon fine
								

								var jsonData = JSON.parse(message);
								if (jsonData === "company")
									window.location.href = "homePageCompany.html";
								else
									window.location.href = "homePageStudente.html";
								sessionStorage.setItem('user', jsonData);  //mi salvo in js il nome dell'utente
								break;
							case 400: // bad request
								document.getElementById("errors").textContent = message;
								this.alert.textContent = message;
								break;
							case 401: // unauthorized
								document.getElementById("errors").textContent = message;
								this.alert.textContent = message;
								break;
							case 500: // server error
								document.getElementById("errors").textContent = message;
								this.alert.textContent = message;
								break;
						}
					}
				}
			);
		} else {
			form.reportValidity();
		}
	});
})();