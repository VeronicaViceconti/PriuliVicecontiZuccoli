{
	const homeBtn = document.getElementById("homeBtn");
	const profileBtn = document.getElementById("profileBtn");
	const downloadBtn = document.getElementById("downloadBtn");
	const acceptBtn = document.getElementById("acceptBtn");
	const declineBtn = document.getElementById("declineBtn");
	const studentPreferences = document.getElementById("studentPreferences");

	window.onload = function() {
		//TODO riempire con i dati dell'utente
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