{
	const matchesTab = document.getElementById("Matches_Tab");
	const availableInternTab = document.getElementById("Avail_Inter_Tab");
	const homeBtn = document.getElementById("homeBtn");
	const profileBtn = document.getElementById("profileBtn");
	const internList = document.getElementById("internList")

	window.onload = function(e) {
		e.preventDefault();
		sessionStorage.setItem('tab', "available");
		loadAvailableInternships();
	}

	function createCard(id, name, role, startDate, endDate, location, openSeats) {
		// Dati della card
		const cardData = {
			id: id,
			company: name,
			role: role,
			period: startDate + " - " + endDate,
			location: location,
			positions: openSeats
		};

		// Seleziona il contenitore in cui aggiungere la card
		const internList = document.getElementById("internList");

		// Crea il div principale
		const card = document.createElement("div");
		card.className = "card";
		card.id = cardData.id;

		// Aggiungi il nome dell'azienda
		const companyDiv = document.createElement("div");
		companyDiv.className = "card-company";
		companyDiv.textContent = cardData.company;
		card.appendChild(companyDiv);

		// Aggiungi il contenitore delle informazioni
		const infoDiv = document.createElement("div");
		infoDiv.className = "internship-info";

		// Aggiungi ogni sezione di informazioni
		const sections = [
			{ img: "img/InternRole.png", text: cardData.role },
			{ img: "img/internPeriod.png", text: cardData.period },
			{ img: "img/internLocation.png", text: cardData.location },
			{ img: "img/internOpenPositions.png", text: cardData.positions }
		];

		sections.forEach(section => {
			const sectionDiv = document.createElement("div");
			sectionDiv.className = "card-info";

			const img = document.createElement("img");
			img.src = section.img;

			const textDiv = document.createElement("div");
			textDiv.textContent = section.text;

			sectionDiv.appendChild(img);
			sectionDiv.appendChild(textDiv);
			infoDiv.appendChild(sectionDiv);
		});

		// Aggiungi le informazioni al contenitore principale
		card.appendChild(infoDiv);

		// Inserisci la card nel DOM
		internList.appendChild(card);
	}


	matchesTab.addEventListener("click", () => {

		//change tab color
		availableInternTab.style.color = "#2e4057";
		matchesTab.style.color = "#a37659";
		sessionStorage.setItem('tab', "metches");
		internList.innerHTML = null;
		loadMatchInternships();

	});

	availableInternTab.addEventListener("click", () => {

		//change tab color
		availableInternTab.style.color = "#a37659";
		matchesTab.style.color = "#2e4057";
		sessionStorage.setItem('tab', "available");
		internList.innerHTML = null;
		loadAvailableInternships();

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


	function loadAvailableInternships() {
		makeCall("GET", "ProfileManager?page=toHomepage", null,
			(req) => {
				if (req.readyState == 4) {
					switch (req.status) {
						case 200: // andato a buon fine
							var jsonData = JSON.parse(req.responseText);
							for (const internship of jsonData) {
								createCard(
									internship.id,
									internship.company.name,
									internship.roleToCover,
									internship.startingDate,
									internship.endingDate,
									internship.company.address,
									internship.openSeats
								);
							}

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

	function loadMatchInternships() { }
}