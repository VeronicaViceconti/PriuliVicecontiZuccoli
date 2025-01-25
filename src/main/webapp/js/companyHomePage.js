{
	const ongoingInternTab = document.getElementById("ongoingInternTab");
	const proposedInternTab = document.getElementById("proposedInternTab");
	const matchesTab = document.getElementById("matchesTab");
	const waitingFeedInternship = document.getElementById("waitingFeedInternship");
	const avail_newMatch_section = document.getElementById("available/newMatch");
	const waitingResponse_section = document.getElementById("waitingResponse");
	const waitingInterview_section = document.getElementById("waitingInterview");
	const homeBtn = document.getElementById("homeBtn");
	const profileBtn = document.getElementById("profileBtn");
	const internList = document.getElementById("internList");

	window.onload = function(e) {
		e.preventDefault();
		sessionStorage.setItem('tab', "ongoing");
		internList.style.visibility = "visible";
		makeCall("GET", "ProfileManager?page=openOngoingInternships", null,
			(req) => {
				if (req.readyState == 4) {
					switch (req.status) {
						case 200: // andato a buon fine
							var jsonData = JSON.parse(req.responseText);	
							if(jsonData != null){
								cleanUp();
								for (const internship of jsonData) {
									createMatchCard(internList,internship.id, internship.student.name, internship.student.studyCourse,internship.roleToCover, internship.startingDate + " - " + internship.endingDate)
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
			
		showMatchesDivFields(false);
	}
	
	function cleanUp() {
		avail_newMatch_section.innerHTML = null;
		waitingResponse_section.innerHTML = null;
		waitingInterview_section.innerHTML = null;
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

	function createCard(Id, name, role, startDate, finishDate, location,openSeats) {
		// Dati della card
		const cardData = {
		id: Id,
		company: name,
		role: role,
		period: startDate + " - " + finishDate,
		location: location,
		positions: openSeats 
		};
			// Aggiungi ogni sezione di informazioni
		const sections = [
		{ img: "img/InternRole.png", text: cardData.role },
		{ img: "img/internPeriod.png", text: cardData.period },
		{ img: "img/internLocation.png", text: cardData.location },
		{ img: "img/internOpenPositions.png", text: cardData.positions }
		];
		if(openSeats === null)
			sections.pop();

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
function createMatchCard(container,id, name, courseOfStudies, roleToCover, period) {
    // Dati della card
    const cardData = {
      id: id,
      studentName: name,
      course: courseOfStudies,
      roleToCover: roleToCover,
      period: period
    };

    // Seleziona il contenitore in cui aggiungere la card
    const internList = document.getElementById("internList");

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
			sessionStorage.setItem("tab", card.getAttribute("data-section"));
			window.location.href = "accept_DeclineStudent_Company.html";
	});
		
    // Inserisci la card nel contenitore
    container.appendChild(card);
  }
  
	matchesTab.addEventListener("click", () => {

		//change tab color
		ongoingInternTab.style.color = "#2e4057";
		proposedInternTab.style.color = "#2e4057";
		waitingFeedInternship.style.color = "#2e4057";
		matchesTab.style.color = "#a37659";
		
		sessionStorage.setItem('tab', "matches");
		internList.innerHTML = null;
		internList.style.visibility = "hidden";
		showMatchesDivFields(true);
		makeCall("GET", "MatchManager?page=showMatches", null,
			(req) => {
				if (req.readyState == 4) {
					switch (req.status) {
						case 200: 
							var jsonData = JSON.parse(req.responseText);
							cleanUp();
							for (const match of jsonData) {		
								pageLocation = avail_newMatch_section;
								if ("acceptedYNCompany" in match && "acceptedYNStudent" in match) {
									pageLocation = waitingInterview_section
								}
								else if ("acceptedYNCompany" in match) {
									pageLocation = waitingResponse_section;
								}
								createMatchCard(
									pageLocation,
									match.id,
									match.publication.student.name, match.publication.student.studyCourse,match.internship.roleToCover, match.internship.startingDate + " - " + match.internship.endingDate
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
	});

	ongoingInternTab.addEventListener("click", () => {

		//change tab color
		ongoingInternTab.style.color = "#a37659";
		proposedInternTab.style.color = "#2e4057";
		waitingFeedInternship.style.color = "#2e4057";
		matchesTab.style.color = "#2e4057";
		sessionStorage.setItem('tab', "ongoing");
		internList.innerHTML = null;
		internList.style.visibility = "visible";
		
		makeCall("GET", "ProfileManager?page=openOngoingInternships", null,
			(req) => {
				if (req.readyState == 4) {
					switch (req.status) {
						case 200: // andato a buon fine
							var jsonData = JSON.parse(req.responseText);
							cleanUp();
							if(jsonData != null){
								for (const internship of jsonData) {
									createMatchCard(internList,internship.id, internship.student.name, internship.student.studyCourse,internship.roleToCover, internship.startingDate + " - " + internship.endingDate)
								}
							}
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
			
		showMatchesDivFields(false);
		
	});
	
	

	proposedInternTab.addEventListener("click", () => {
		cleanUp();
		showMatchesDivFields(false);
		//change tab color
		ongoingInternTab.style.color = "#2e4057";
		proposedInternTab.style.color = "#a37659";
		waitingFeedInternship.style.color = "#2e4057";
		matchesTab.style.color = "#2e4057";
		sessionStorage.setItem('tab', "proposed");
		internList.style.visibility = "visible";
		internList.innerHTML = null;

		makeCall("GET", "PublicationManager?page=proposedInternships", null,
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
		window.location.href = "homePageCompany.html";
	})

	profileBtn.addEventListener("click", () => {
		window.location.href = "companyProfile.html";
	})

	//click on card event
	internList.addEventListener("click", () => {
		const card = event.target.closest(".card");
		var tab = sessionStorage.getItem("tab");


		if (card) {
			if (tab != "matches") {
				sessionStorage.setItem("internshipID", card.id);
				window.location.href = "internshipView_Company.html";
			}
			else {
				sessionStorage.setItem("MatchedUserID", card.id);
				//alert("redirect to MATCHED user page");
				window.location.href = "accept_DeclineStudent_Company.html";
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
	
	function loadMatchInternships() {
		makeCall("GET", "ProfileManager?page=showMatches", null,
			(req) => {
				if (req.readyState == 4) {
					switch (req.status) {
						case 200: // andato a buon fine
							var jsonData = JSON.parse(req.responseText);
							var pageLocation;
							for (const internship of jsonData) {
								console.log(internship);
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