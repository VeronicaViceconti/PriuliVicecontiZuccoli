{
	const matchesTab = document.getElementById("Matches_Tab");
	const availableInternTab = document.getElementById("Avail_Inter_Tab");
	const homeBtn = document.getElementById("homeBtn");
	const profileBtn = document.getElementById("profileBtn");
	const internList = document.getElementById("internList")

	window.onload = function() {
		sessionStorage.setItem('tab', "available");
		//TODO: caricare available internships dal databse
	}

	matchesTab.addEventListener("click", () => {

		//change tab color
		availableInternTab.style.color = "#2e4057";
		matchesTab.style.color = "#a37659";
		sessionStorage.setItem('tab', "metches");

		//TODO
	});

	availableInternTab.addEventListener("click", () => {

		//change tab color
		availableInternTab.style.color = "#a37659";
		matchesTab.style.color = "#2e4057";
		sessionStorage.setItem('tab', "available");
		//TODO
	});

	homeBtn.addEventListener("click", () => {
		window.location.href = "http://localhost:8080/SandC/homePageStudente.html";
	})

	profileBtn.addEventListener("click", () => {
		window.location.href = "http://localhost:8080/SandC/studentProfile.html";
	})

	internList.addEventListener("click", () => {
		const card = event.target.closest(".card");

		if (card) {
			const cardId = card.id;
			sessionStorage.setItem("internshipID", cardId);
			window.location.href = "http://localhost:8080/SandC/internshipInfo_AcceptDecline_student.html";
		}
	})
}