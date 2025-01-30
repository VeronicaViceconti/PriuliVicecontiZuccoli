/**
 * 
 */
(function() {
	const regex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
	const studentRadios = document.querySelectorAll('input[name="isStudent"]');

	showSignIn = function() {
		document.getElementById("login").style.display = 'none';
		document.getElementById("Signin").style.display = 'block';
		document.getElementById("signInError").textContent = "";
		document.getElementById("showIfStudent").style.display = 'none';
		document.getElementById("showIfStudent").style.display = 'none';
	}

	showLogin = function() {

		document.getElementById("login").style.display = 'block';
		document.getElementById("Signin").style.display = 'none';
		document.getElementById("errors").textContent = "";
	}
	
	
	studentRadios.forEach(radio => {
	  radio.addEventListener('change', function() {  
	    if (this.checked) {
	
	      if (this.value === "yes") {
	        document.getElementById("showIfStudent").style.display = 'block';
	      } else {
	        document.getElementById("showIfStudent").style.display = 'none';
	      }
	    }
	  });
	});

	document.getElementById("link-signin").addEventListener('click', (e) => {
		showSignIn();
	});

	document.getElementById("link-login").addEventListener('click', (e) => {
		showLogin();
	});

	document.getElementById("sendBtn").addEventListener('click', (e) => {
		e.preventDefault;
		var token = sessionStorage.getItem("notifToken");
		console.log("nel backend : " + token);

		fetch('https://babbochat.altervista.org/SC_Notifications/php-FCM/send.php', {
			method: 'POST',
			headers: {
				'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'
			},
			body: new URLSearchParams({
				token: token,
				notifTitle: "Bella li",
				notifBody: "Pataterk"
			})
		})
			.then(response => response.text())
			.then(data => console.log(data));
	});

	document.getElementById("signinbutton").addEventListener('click', (e) => {
		e.preventDefault();
		var form = e.target.closest("form");

		var password = form.querySelector("[name='pwd']").value;
		var email = form.querySelector("[name='email']").value;
		var name = form.querySelector("[name='username']").value;
		var address = form.querySelector("[name='address']").value;
		var phoneNumber = form.querySelector("[name='phoneNumber']").value; 
		var studyCourse = form.querySelector("[name='StudyCourse']").value;
		const regexTel = /^[0-9]{10}$/;

		//controllo che l'utente inserisca una mail : somenthing@something.something
		if (!regex.test(email)) {
			document.getElementById("signInError").textContent = "This is not a real email!";
			return;
		}
		
		if (!regexTel.test(phoneNumber)) {
			document.getElementById("signInError").textContent = "This is not a real telephone number!";
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
		if (address.trim() === "") {
			document.getElementById("signInError").textContent = "You can't send an empty address!";
			return;
		}
		if (phoneNumber.trim() === "") {
			document.getElementById("signInError").textContent = "You can't send an empty phone number!";
			return;
		}
		if (studentRadios === 'yes' && studyCourse.trim() === "") {
			document.getElementById("signInError").textContent = "You can't send an empty study course!";
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
									window.location.href = "CompanyHTML/homePageCompany.html";
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