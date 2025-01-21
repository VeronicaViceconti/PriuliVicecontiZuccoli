{
	const matchesTab = document.getElementById("Matches_Tab");
	const availableInternTab = document.getElementById("Avail_Inter_Tab");
	const homeBtn = document.getElementById("homeBtn");
	const profileBtn = document.getElementById("profileBtn");
	const internList = document.getElementById("available/newMatch");
	const first_subTitle = document.getElementById("first_subTitle");

	window.onload = function(e) {
		e.preventDefault();
		first_subTitle.innerText = "";
		sessionStorage.setItem('tab', "available");
		showMatchesDivFields(false);
		loadAvailableInternships();
	}

	availableInternTab.addEventListener("click", () => {

		//change tab color
		availableInternTab.style.color = "#a37659";
		matchesTab.style.color = "#2e4057";
		sessionStorage.setItem('tab', "available");

		internList.innerHTML = null;
		first_subTitle.innerText = "";
		showMatchesDivFields(false);
		loadAvailableInternships();
	});

	matchesTab.addEventListener("click", () => {

		//change tab color
		availableInternTab.style.color = "#2e4057";
		matchesTab.style.color = "#a37659";
		sessionStorage.setItem('tab', "metches");

		internList.innerHTML = null;
		first_subTitle.innerText = "New matches";
		showMatchesDivFields(true);
		loadMatchInternships();

	});

	homeBtn.addEventListener("click", () => {
		window.location.href = "http://localhost:8080/SandC/homePageStudente.html";
	})

	profileBtn.addEventListener("click", () => {
		window.location.href = "http://localhost:8080/SandC/studentProfile.html";
	})

	/*internList.addEventListener("click", () => {
		const card = event.target.closest(".card");

		if (card) {
			const cardId = card.id;
			sessionStorage.setItem("internshipID", cardId);
			window.location.href = "http://localhost:8080/SandC/internshipInfo_AcceptDecline_student.html";
		}
	})*/

	function AddCardsEventListners() {
		var cards = null;
		cards = document.querySelectorAll(".card");
		//click listener for each card
		cards.forEach(card => {
			card.addEventListener("click", () => {
				//alert("Hai cliccato una card");
				console.log("aggiungo listener card");
			});
		});
	}

	function showMatchesDivFields(choice) {
		var visibility = "hidden";
		if (choice == true) {
			visibility = "visible";
		}
		const matchesElements = document.querySelectorAll('.elenchi [data-tab="matches"]');
		matchesElements.forEach(element => {
			element.style.visibility = visibility;
		});

	}

	function createCard(pageSection, id, name, role, startDate, endDate, location, openSeats) {
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
		const cardContainer = document.getElementById(pageSection);

		// Crea il div principale
		const card = document.createElement("div");
		card.className = "card";
		card.id = cardData.id;
		card.setAttribute("data-section", pageSection);

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

		//aggiungo click listener alla card
		card.addEventListener("click", () => {
			sessionStorage.setItem("internshipID", card.id);
			sessionStorage.setItem("tab", card.getAttribute("data-section"));
			window.location.href = "http://localhost:8080/SandC/internshipInfo_AcceptDecline_student.html";
		});

		// Inserisci la card nel DOM
		cardContainer.appendChild(card);

	}

	function loadAvailableInternships() {
		makeCall("GET", "ProfileManager?page=toHomepage", null,
			(req) => {
				if (req.readyState == 4) {
					switch (req.status) {
						case 200: // andato a buon fine
							var jsonData = JSON.parse(req.responseText);
							for (const internship of jsonData) {
								createCard(
									"available/newMatch",
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

	function loadMatchInternships() {
		makeCall("GET", "ProfileManager?page=showMatches", null,
			(req) => {
				if (req.readyState == 4) {
					switch (req.status) {
						case 200: // andato a buon fine
							var jsonData = JSON.parse(req.responseText);
							var pageLocation;							
							for (const internship of jsonData) {
								
								if("accepted" in internship){
									pageLocation = "waitingResponse";
								}
								else{
									pageLocation = "available/newMatch"
								}
								createCard(
									pageLocation,
									internship.id,
									internship.internship.company.name,
									internship.internship.roleToCover,
									internship.internship.startingDate,
									internship.internship.endingDate,
									internship.internship.company.address,
									internship.internship.openSeats
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


}