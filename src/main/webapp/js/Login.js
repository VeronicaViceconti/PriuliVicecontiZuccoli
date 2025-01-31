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
	
	//the right input to show
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

	function saveFCMToken(token) {
		makeCall("POST", 'MatchManager?page=saveToken&token=' + token, null,
			function(x) { // X è UN OGGETTO XMLHttpRequest
				if (x.readyState == XMLHttpRequest.DONE) {
					if (x.status === 200) return;
					
					const errorMessage = x.responseText;
					document.getElementById("errors").textContent = errorMessage;
				}
			});
	}

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

		//mail : somenthing@something.something
		if (!regex.test(email)) {
			document.getElementById("signInError").textContent = "This is not a real email!";
			return;
		}
		
		//all input control
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
			makeCall("POST", 'SignupManager', form,
				function(x) { // 
					if (x.readyState == XMLHttpRequest.DONE) {
						var message = x.responseText;

						switch (x.status) {
							case 200:  
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
		document.getElementById("userEmail").innerText = form.elements["email"].value;
		if (form.checkValidity()) {
			makeCall("POST", 'LoginManager?page=toHomepage', form,
				function(x) {
					if (x.readyState == XMLHttpRequest.DONE) {
						var message = x.responseText;
						switch (x.status) {

							case 200: 
								var jsonData = JSON.parse(message);
								sessionStorage.setItem('user', jsonData);  //mi salvo in js il nome dell'utente
								saveFCMToken(document.getElementById("token").innerText);

								if (jsonData === "company")
									window.location.href = "homePageCompany.html";
								else
									window.location.href = "homePageStudente.html";

								sessionStorage.setItem('user', jsonData);  //save user in session
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