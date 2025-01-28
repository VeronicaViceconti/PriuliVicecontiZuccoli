{
	const ongoingInternTab = document.getElementById("ongoingInternTab");
	const proposedInternTab = document.getElementById("proposedInternTab");
	const matchesTab = document.getElementById("matchesTab");
	const waitingFeedInternshipTab = document.getElementById("waitingFeedInternshipTab");
	const newInternship = document.getElementById("newInternTab");
	const searchBtn = document.getElementById("searchBtn");
	const homeBtn = document.getElementById("homeBtn");
	const profileBtn = document.getElementById("profileBtn");
	const internList = document.getElementById("internList")
	const searchfiltered = document.getElementById("searchKey");
	const avail_newMatch_section = document.getElementById("internList");
	const waitingResponse_section = document.getElementById("waitingResponse");
	const waitingInterview_section = document.getElementById("waitingInterview");

	window.onload = function(e) {
		e.preventDefault();
		sessionStorage.setItem('tab', "ongoing");
		showMatchesDivFields(false);
		document.getElementById("overlap").style.visibility = "visible";
		cleanUp();
		//askOngoingIntern();
		
		
		makeCall("GET", "Interviewer?page=getInterviewInfo&ID="+13, null,
			(req) => {
				if (req.readyState == 4) {
					switch (req.status) {
						case 200: // andato a buon fine
							var jsonData = JSON.parse(req.responseText);
							console.log(jsonData);
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
		
		const first_div = infoDiv.querySelector('div:first-of-type');
		
		first_div.style.width = "200px";
		first_div.querySelector('div:first-of-type').style.overflow = "hidden"; 
		first_div.querySelector('div:first-of-type').style.textOverflow = "ellipsis";

		// Aggiungi le informazioni al contenitore principale
		card.appendChild(infoDiv);

		// Inserisci la card nel DOM
		internList.appendChild(card);
	}

	function cleanUp() {
		avail_newMatch_section.innerHTML = "";
		waitingResponse_section.innerHTML = "";
		waitingInterview_section.innerHTML = "";
	}

	function createMatchCard(cardContainer, id, name, courseOfStudies, roleToCover, period) {
		// Dati della card
		const cardData = {
			id: id,
			studentName: name,
			course: courseOfStudies,
			roleToCover: roleToCover,
			period: period
		};

		// Crea il div principale della card
		const card = document.createElement("div");
		card.className = "card";
		card.id = cardData.id;

		// Aggiungi il titolo della card (nome studente)
		const cardTitle1 = document.createElement("div");
		cardTitle1.className = "card-title";

		const profilePic = document.createElement("img");
		profilePic.className = "studentProfilePic";
		profilePic.id = "studentProfilePic";
		profilePic.src = "img/profilePic.png"; // Immagine del profilo

		const nameDiv = document.createElement("div");
		nameDiv.className = "card-company";
		nameDiv.id = "StudentName";
		nameDiv.textContent = cardData.studentName;

		cardTitle1.appendChild(profilePic);
		cardTitle1.appendChild(nameDiv);

		// Aggiungi il secondo titolo (corso di studi)
		const cardTitle2 = document.createElement("div");
		cardTitle2.className = "card-title";

		const courseImg = document.createElement("img");
		courseImg.src = "img/CourseOfStudies.png"; // Icona del corso

		const courseDiv = document.createElement("div");
		courseDiv.id = "courseOfStudies";
		courseDiv.className = "card-info";
		courseDiv.textContent = cardData.course;

		cardTitle2.appendChild(courseImg);
		cardTitle2.appendChild(courseDiv);

		// Crea la sezione minor-info
		const minorInfo = document.createElement("div");
		minorInfo.className = "minor-info";

		// Aggiungi informazioni università
		const minorInfoTitle1 = document.createElement("div");
		minorInfoTitle1.className = "card-title";

		const uniImg = document.createElement("img");
		uniImg.src = "img/InternRole.png"; // Icona dell'università

		const uniDiv = document.createElement("div");
		uniDiv.id = "roleToCover";
		uniDiv.className = "card-info";
		uniDiv.textContent = cardData.roleToCover;

		minorInfoTitle1.appendChild(uniImg);
		minorInfoTitle1.appendChild(uniDiv);

		// Aggiungi informazioni indirizzo
		const minorInfoTitle2 = document.createElement("div");
		minorInfoTitle2.className = "card-title";

		const addrImg = document.createElement("img");
		addrImg.src = "img/internPeriod.png"; // Icona dell'indirizzo

		const addrDiv = document.createElement("div");
		addrDiv.id = "internPeriod";
		addrDiv.className = "card-info";
		addrDiv.textContent = cardData.period;

		minorInfoTitle2.appendChild(addrImg);
		minorInfoTitle2.appendChild(addrDiv);

		// Aggiungi i minor-info alla card
		minorInfo.appendChild(minorInfoTitle1);
		minorInfo.appendChild(minorInfoTitle2);

		// Aggiungi tutti gli elementi alla card
		card.appendChild(cardTitle1);
		card.appendChild(cardTitle2);
		card.appendChild(minorInfo);

		card.addEventListener("click", () => {
			sessionStorage.setItem("matchID", card.id);
			switch (cardContainer.id) {
				case "waitingResponse":
					sessionStorage.setItem("MatchType", "WaitResponse");
					window.location.href = "accept_DeclineStudent_Company.html";
					break;
				case "waitingInterview":
					window.location.href = "interview.html";
					break;
			}
		});

		// Inserisci la card nel contenitore
		cardContainer.appendChild(card);
	}

	searchfiltered.addEventListener("click", () => {
		searchfiltered.placeholder = '';
	});

	searchfiltered.addEventListener('blur', function() {
		if (searchfiltered.value === '') {
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
					document.getElementById("searchKey").placeholder = 'Search for internships';
					switch (req.status) {
						case 200: // andato a buon fine
							var jsonData = JSON.parse(req.responseText);
							if (jsonData != null && jsonData.length > 0) {
								cleanUp();
								for (const match of jsonData) {
									createMatchCard(
										match.id,
										match.internship.student.name,
										match.internship.student.studyCourse,
										match.internship.roleToCover,
										match.internship.startingDate + " - " + match.internship.endingDate
									)
								}
							} else {
								alert("No internship found with that company");
								askOngoingIntern();
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

	matchesTab.addEventListener("click", () => {

		//change tab color
		ongoingInternTab.style.color = "#2e4057";
		proposedInternTab.style.color = "#2e4057";
		waitingFeedInternshipTab.style.color = "#2e4057";
		matchesTab.style.color = "#a37659";

		showMatchesDivFields(true);
		sessionStorage.setItem('tab', "matches");
		document.getElementById("overlap").style.visibility = "hidden";
		cleanUp();

		makeCall("GET", "MatchManager?page=showMatches", null,
			(req) => {
				if (req.readyState == 4) {
					switch (req.status) {
						case 200:
							var jsonData = JSON.parse(req.responseText);
							console.log(jsonData);
							if (jsonData != null) {
								for (const match of jsonData) {
									var pageLocation = avail_newMatch_section;
									if ("acceptedYNCompany" in match && "acceptedYNStudent" in match) {
										pageLocation = waitingInterview_section
									}
									else if ("acceptedYNCompany" in match) {
										pageLocation = waitingResponse_section;
									}
									createMatchCard(
										pageLocation,
										match.id,
										match.publication.student.name,
										match.publication.student.studyCourse,
										match.internship.roleToCover,
										match.internship.startingDate + " - " + match.internship.endingDate);
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
	});

	function askOngoingIntern() {
		//change tab color
		ongoingInternTab.style.color = "#a37659";
		proposedInternTab.style.color = "#2e4057";
		waitingFeedInternshipTab.style.color = "#2e4057";
		matchesTab.style.color = "#2e4057";
		sessionStorage.setItem('tab', "ongoing");
		internList.innerHTML = null;
		showMatchesDivFields(false);
		document.getElementById("overlap").style.visibility = "visible";

		makeCall("GET", "ProfileManager?page=openOngoingInternships", null,
			(req) => {
				if (req.readyState == 4) {
					switch (req.status) {
						case 200: // andato a buon fine
							var jsonData = JSON.parse(req.responseText);
							if (jsonData != null) {
								for (const match of jsonData) {
									createCard(
										match.id,
										match.internship.student.name,
										match.internship.roleToCover,
										match.internship.startingDate,
										match.internship.endingDate,
										match.internship.company.address,
										match.internship.openSeats
									)
								}
							}
							else {
								internList.innerText = "No ongoing interrmships";
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

	ongoingInternTab.addEventListener("click", () => {
		askOngoingIntern();
	});

	proposedInternTab.addEventListener("click", () => {

		//change tab color
		ongoingInternTab.style.color = "#2e4057";
		proposedInternTab.style.color = "#a37659";
		waitingFeedInternshipTab.style.color = "#2e4057";
		matchesTab.style.color = "#2e4057";
		sessionStorage.setItem('tab', "proposed");
		showMatchesDivFields(false);
		document.getElementById("overlap").style.visibility = "hidden";
		cleanUp();

		makeCall("GET", "PublicationManager?page=proposedInternships", null,
			(req) => {
				if (req.readyState == 4) {
					switch (req.status) {
						case 200: // andato a buon fine
							var jsonData = JSON.parse(req.responseText);
							console.log(jsonData);
							for (const internship of jsonData) {
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

	waitingFeedInternshipTab.addEventListener("click", () => {

		//change tab color
		ongoingInternTab.style.color = "#2e4057";
		proposedInternTab.style.color = "#2e4057";
		waitingFeedInternshipTab.style.color = "#a37659";
		matchesTab.style.color = "#2e4057";

		showMatchesDivFields(false);
		sessionStorage.setItem('tab', "waitingFeed");
		cleanUp();

		makeCall("GET", "PublicationManager?page=waitingFeedbackInternships", null,
			(req) => {
				if (req.readyState == 4) {
					switch (req.status) {
						case 200: // andato a buon fine
							var jsonData = JSON.parse(req.responseText);
							console.log(jsonData);
							if(jsonData != null){
								for (const internship of jsonData) {
									createCard( 
										internship.id,
										internship.publication.student.name,
										internship.internship.roleToCover,
										internship.internship.startingDate,
										internship.internship.endingDate,
										internship.internship.company.address,
										internship.internship.openSeats
									)
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
	});

	newInternship.addEventListener("click", () => {
		window.location.href = "InternshipPublication.html";
	})

	homeBtn.addEventListener("click", () => {
		window.location.href = "homePageCompany.html";
	})

	profileBtn.addEventListener("click", () => {
		window.location.href = "companyProfile.html";
	})

	//click on card event
	internList.addEventListener("click", () => {
		const card = event.target.closest(".card");
		var tab = sessionStorage.getItem("tab");
		
		sessionStorage.setItem("MatchType", "NewMatch");
		if (card) {
			switch (tab) {
				case "matches":
					console.log("matches");
					sessionStorage.setItem("matchID", card.id);
					window.location.href = "accept_DeclineStudent_Company.html";
					break;
				case "waitingFeed":
					console.log("waitingfeed");
					sessionStorage.setItem("matchID", card.id);
					window.location.href = "feedbackForm.html";
					break;
				default:
					console.log("internshipView");
					sessionStorage.setItem("matchID", card.id);
					window.location.href = "internshipView_Company.html";
					break;
			}

		}
	})

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
}