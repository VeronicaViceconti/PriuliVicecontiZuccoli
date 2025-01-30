{
	const matchesTab = document.getElementById("Matches_Tab");
	const availableInternTab = document.getElementById("Avail_Inter_Tab");
	const addPublicationTab = document.getElementById("New_Pub");
	const homeBtn = document.getElementById("homeBtn");
	const profileBtn = document.getElementById("profileBtn");
	const avail_newMatch_section = document.getElementById("available/newMatch");
	const waitingResponse_section = document.getElementById("waitingResponse");
	const waitingInterview_section = document.getElementById("waitingInterview");
	const first_subTitle = document.getElementById("first_subTitle");
	const searchBtn = document.getElementById("searchBtn");
	const searchfiltered = document.getElementById("searchKey");

	window.onload = function(e) {
		e.preventDefault();
		availableInternTab.style.color = "#a37659";
		first_subTitle.innerText = "";
		sessionStorage.setItem('tab', "available");
		cleanUp();
		showMatchesDivFields(false);
		loadAvailableInternships();
	}

	availableInternTab.addEventListener("click", () => {

		//change tab color
		availableInternTab.style.color = "#a37659";
		matchesTab.style.color = "#2e4057";
		addPublicationTab.style.color = "#2e4057";
		sessionStorage.setItem('tab', "available");
		document.getElementById("overlap").style.visibility = "visible";

		first_subTitle.innerText = "";
		cleanUp();
		showMatchesDivFields(false);
		loadAvailableInternships();
	});
	
	addPublicationTab.addEventListener("click",()=>{
		document.getElementById("overlap").style.visibility = "hidden";
		availableInternTab.style.color = "#2e4057";
		matchesTab.style.color = "#2e4057";
		addPublicationTab.style.color = "#a37659";
		sessionStorage.setItem('tab', "addPublication");
		first_subTitle.innerText = "";
		
		window.location.href = "preferencePublication.html";
		
		cleanUp();
		showMatchesDivFields(false);
	});

	matchesTab.addEventListener("click", () => {

		//change tab color
		availableInternTab.style.color = "#2e4057";
		matchesTab.style.color = "#a37659";
		addPublicationTab.style.color = "#2e4057";
		sessionStorage.setItem('tab', "matches");
		document.getElementById("overlap").style.visibility = "hidden";


		first_subTitle.innerText = "New matches";
		cleanUp();
		showMatchesDivFields(true);
		loadMatchInternships();

	});

	function cleanUp() {
		avail_newMatch_section.innerHTML = "";
		waitingResponse_section.innerHTML = "";
		waitingInterview_section.innerHTML = "";
	}

	homeBtn.addEventListener("click", () => {
		window.location.href = "homePageStudente.html";
	})

	profileBtn.addEventListener("click", () => {
		window.location.href = "studentProfile.html";
	})


	searchfiltered.addEventListener("click", () =>{
  		searchfiltered.placeholder = ''; 
	});
	
	searchfiltered.addEventListener('blur', function() {
		 if(searchfiltered.value === ''){
		    searchfiltered.placeholder = 'Search for internships'; 
		 }
	});

	searchBtn.addEventListener("click", () => {
		
		var searchKey = document.getElementById("searchKey").value;
		if (searchKey === "") {
			return;
		}
		makeCall("GET", "ProfileManager?page=filteredInternships&condition=" + searchKey, null,
			(req) => {
				if (req.readyState == 4) {
					document.getElementById("searchKey").value = "";
					document.getElementById("searchKey").placeholder = "Search for internships";
					switch (req.status) {
						case 200: // andato a buon fine
							var jsonData = JSON.parse(req.responseText);
							if (jsonData != null && jsonData.length > 0) {
								cleanUp();
								for (const internship of jsonData) {
									createCard(
										avail_newMatch_section,
										internship.id,
										internship.company.name,
										internship.roleToCover,
										internship.startingDate,
										internship.endingDate,
										internship.company.address,
										internship.openSeats,
										null
									);
								}
							} else {
								alert("No internships with the current seach key!");
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

	})

	searchBtn.addEventListener("click", () => {
		if (sessionStorage.getItem('tab') == "matches")
			return;
		
		var searchKey = document.getElementById("searchKey").value;
		if (searchKey === "") {
			return;
		}
		makeCall("GET", "ProfileManager?page=filteredInternships&condition=" + searchKey, null,
			(req) => {
				if (req.readyState == 4) {
					switch (req.status) {
						case 200: // andato a buon fine
							var jsonData = JSON.parse(req.responseText);
							if (jsonData != null && jsonData.length > 0) {
								cleanUp();
								for (const internship of jsonData) {
									console.log(internship);
									createCard(
										avail_newMatch_section,
										internship.id,
										internship.company.name,
										internship.roleToCover,
										internship.startingDate,
										internship.endingDate,
										internship.company.address,
										internship.openSeats,
										null
									);
								}
							} else {
								alert("No internships with the current seach key!");
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

	})
	
	function AddCardsEventListners() {
		var cards = null;
		cards = document.querySelectorAll(".card");
		//click listener for each card
		cards.forEach(card => {
			card.addEventListener("click", () => {
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

	function createCard(cardContainer, id, name, role, startDate, endDate, location, openSeats, idMatch) {
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
		//const cardContainer = document.getElementById(pageSection);

		// Crea il div principale
		const card = document.createElement("div");
		card.className = "card";
		card.id = cardData.id;
		card.setAttribute("data-section", cardContainer.id);
		card.setAttribute("match", idMatch);

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
		const first_div = infoDiv.querySelector('div:first-of-type');
		
		first_div.style.width = "200px";
		first_div.querySelector('div:first-of-type').style.overflow = "hidden"; 
		first_div.querySelector('div:first-of-type').style.textOverflow = "ellipsis";
		

		// Aggiungi le informazioni al contenitore principale
		card.appendChild(infoDiv);

		//aggiungo click listener alla card
		card.addEventListener("click", () => {
			sessionStorage.setItem("internshipID", card.id);
			if(idMatch != null){
				sessionStorage.setItem("matchID", idMatch);
			}
			if(!(sessionStorage.getItem("tab") == "matches" && card.getAttribute("data-section") == "available/newMatch")){
				sessionStorage.setItem("tab", card.getAttribute("data-section"));
			}
			window.location.href = "internshipInfo_AcceptDecline_student.html";
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
							if(jsonData != null){
								for (const internship of jsonData) {
									createCard(
										avail_newMatch_section,
										internship.id,
										internship.company.name,
										internship.roleToCover,
										internship.startingDate,
										internship.endingDate,
										internship.company.address,
										internship.openSeats,
										null
									);
								}
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
							cleanUp();
							var pageLocation;
							if(jsonData != null){
								for (const internship of jsonData) {
								pageLocation = avail_newMatch_section;
								if ("acceptedYNCompany" in internship && "acceptedYNStudent" in internship) {
									pageLocation = waitingInterview_section
								}
								else if ("acceptedYNStudent" in internship) {
									pageLocation = waitingResponse_section;
								}
								createCard(
									pageLocation,
									internship.internship.id,
									internship.internship.company.name,
									internship.internship.roleToCover,
									internship.internship.startingDate,
									internship.internship.endingDate,
									internship.internship.company.address,
									internship.internship.openSeats,
									internship.id
								);
							}
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