{
	const homeBtn = document.getElementById("homeBtn");
	const profileBtn = document.getElementById("profileBtn");
	const downloadBtn = document.getElementById("downloadBtn");
	const acceptBtn = document.getElementById("acceptBtn");
	const declineBtn = document.getElementById("declineBtn");
	const studentPreferences = document.getElementById("studentPreferences");

	window.onload = function() {
		//TODO riempire con i dati dell'utente
		makeCall("GET", "MatchManager?page=openMatch&IDmatch=" + 12, null, //need to send not 12 but the id of the match selected
			(req) => {
				if (req.readyState == 4) {
					switch (req.status) {
						case 200: // andato a buon fine
							console.log(req.responseText);
							var jsonData = JSON.parse(req.responseText);
							console.log("mi hai richiesto id ->"+jsonData);
							//fill the page							
							
							break;
						case 403:
							console.log("errore 403");
							break;
						case 412:
							console.log("errore 412");
							break;
						case 500:
							console.log("errore 500");
							break;
					}
				}
			});
	}

	profileBtn.addEventListener("click", () => {
		window.location.href = "companyProfile.html";
	})

	homeBtn.addEventListener("click", () => {
		window.location.href = "homePageCompany.html";
	})
	downloadBtn.addEventListener("click", () => {
		alert("downloadBtn");
	})

	acceptBtn.addEventListener("click", () => {
		alert("acceptBtn");
	})
	declineBtn.addEventListener("click", () => {
		alert("declineBtn");
	})


}