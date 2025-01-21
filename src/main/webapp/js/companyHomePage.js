{
	const ongoingInternTab = document.getElementById("ongoingInternTab");
	const proposedInternTab = document.getElementById("proposedInternTab");
	const matchesTab = document.getElementById("matchesTab");
	const waitingFeedInternship = document.getElementById("waitingFeedInternship");

	const homeBtn = document.getElementById("homeBtn");
	const profileBtn = document.getElementById("profileBtn");
	const internList = document.getElementById("internList")

	window.onload = function(e) {
		e.preventDefault();
		sessionStorage.setItem('tab', "ongoing");
		internList.innerHTML = null;

		/*makeCall("GET", "ProfileManager?page=toHomepage", null,
			(req) => {
				if (req.readyState == 4) {
					switch (req.status) {
						case 200: // andato a buon fine
							var jsonData = JSON.parse(req.responseText);
							console.log(jsonData[0]);
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
			});*/
	}

	function createCard(Id, name, role, startDate, finishDate, location, openSeats) {
		// Dati della card
		const cardData = {
			id: Id,
			company: name,
			role: role,
			period: startDate + " - " + finishDate,
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
		ongoingInternTab.style.color = "#2e4057";
		proposedInternTab.style.color = "#2e4057";
		waitingFeedInternship.style.color = "#2e4057";
		matchesTab.style.color = "#a37659";

		sessionStorage.setItem('tab', "matches");
		internList.innerHTML = null;
		
		makeCall("GET", "MatchManager?page=acceptMatch&IDmatch="+3+"&accept="+0, null,
			(req) => {
				if (req.readyState == 4) {
					switch (req.status) {
						case 200: // andato a buon fine
							console.log("da fare");
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
	});

	ongoingInternTab.addEventListener("click", () => {

		//change tab color
		ongoingInternTab.style.color = "#a37659";
		proposedInternTab.style.color = "#2e4057";
		waitingFeedInternship.style.color = "#2e4057";
		matchesTab.style.color = "#2e4057";
		sessionStorage.setItem('tab', "ongoing");
		internList.innerHTML = null;
		//TODO
	});

	proposedInternTab.addEventListener("click", () => {

		//change tab color
		ongoingInternTab.style.color = "#2e4057";
		proposedInternTab.style.color = "#a37659";
		waitingFeedInternship.style.color = "#2e4057";
		matchesTab.style.color = "#2e4057";
		sessionStorage.setItem('tab', "proposed");
		
		internList.innerHTML = null;
				
		makeCall("GET", "PublicationManager?page=proposedInternships", null,
			(req) => {
				if (req.readyState == 4) {
					switch (req.status) {
						case 200: // andato a buon fine
							var jsonData = JSON.parse(req.responseText);
							console.log(jsonData[0]);
							for(const internship of jsonData){
								createCard(
									internship.id,
									internship.company.name,
									internship.roleToCover,
									internship.startingDate,
									internship.endingDate,
									internship.company.address,
									internship.openSeats
								)
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
	});

	waitingFeedInternship.addEventListener("click", () => {

		//change tab color
		ongoingInternTab.style.color = "#2e4057";
		proposedInternTab.style.color = "#2e4057";
		waitingFeedInternship.style.color = "#a37659";
		matchesTab.style.color = "#2e4057";

		sessionStorage.setItem('tab', "waitingFeed");
		internList.innerHTML = null;
		//TODO
	});

	homeBtn.addEventListener("click", () => {
		window.location.href = "http://localhost:8080/SandC/homePageCompany.html";
	})

	profileBtn.addEventListener("click", () => {
		window.location.href = "http://localhost:8080/SandC/companyProfile.html";
	})

	//click on card event
	internList.addEventListener("click", () => {
		const card = event.target.closest(".card");
		var tab = sessionStorage.getItem("tab");

		
		if (card) {
			if (tab != "matches") {
				sessionStorage.setItem("internshipID", card.id);
				window.location.href = "http://localhost:8080/SandC/internshipView_Company.html";
			}
			else {
				sessionStorage.setItem("MatchedUserID", card.id);
				alert("redirect to user page");
				//window.location.href = "http://localhost:8080/SandC/internshipView_Company.html";
			}
		}
	})
}