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
	const acceptDeclineInterview_section = document.getElementById("accept/DeclineInterview");

	window.onload = function(e) { //view ongoing internships
		e.preventDefault();
		sessionStorage.setItem('tab', "ongoing");
		showMatchesDivFields(false);
		document.getElementById("overlap").style.visibility = "visible";
		cleanUp();
		askOngoingIntern();
	}

	//this function create the internship card with basic info (internship info)
	function createCard(Id, name, role, startDate, finishDate, location, openSeats) {
		const cardData = {
			id: Id,
			company: name,
			role: role,
			period: startDate + " - " + finishDate,
			location: location,
			positions: openSeats
		};

		const internList = document.getElementById("internList");

		// Create principle div
		const card = document.createElement("div");
		card.className = "card";
		card.id = cardData.id;

		// company name
		const companyDiv = document.createElement("div");
		companyDiv.className = "card-company";
		companyDiv.textContent = cardData.company;
		card.appendChild(companyDiv);

		// div container
		const infoDiv = document.createElement("div");
		infoDiv.className = "internship-info";

		//each section
		const sections = [
			{ img: "img/InternRole.png", text: cardData.role },
			{ img: "img/internPeriod.png", text: cardData.period },
			{ img: "img/internLocation.png", text: cardData.location },
			{ img: "img/internOpenPositions.png", text: cardData.positions }
		];

		//create sections
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

		// add info to contenitor
		card.appendChild(infoDiv);

		// add card to the dom
		internList.appendChild(card);
	}

	function cleanUp() {
		avail_newMatch_section.innerHTML = "";
		waitingResponse_section.innerHTML = "";
		waitingInterview_section.innerHTML = "";
		acceptDeclineInterview_section.innerHTML = "";
	}

	//this function create the match card with basic info (student info)
	function createMatchCard(cardContainer, id, name, courseOfStudies, roleToCover, period) {
		const cardData = {
			id: id,
			studentName: name,
			course: courseOfStudies,
			roleToCover: roleToCover,
			period: period
		};

		const card = document.createElement("div");
		card.className = "card";
		card.id = cardData.id;

		//student name
		const cardTitle1 = document.createElement("div");
		cardTitle1.className = "card-title";

		const profilePic = document.createElement("img");
		profilePic.className = "studentProfilePic";
		profilePic.id = "studentProfilePic";
		profilePic.src = "img/profilePic.png"; 

		const nameDiv = document.createElement("div");
		nameDiv.className = "card-company";
		nameDiv.id = "StudentName";
		nameDiv.textContent = cardData.studentName;

		cardTitle1.appendChild(profilePic);
		cardTitle1.appendChild(nameDiv);

		//study course
		const cardTitle2 = document.createElement("div");
		cardTitle2.className = "card-title";

		const courseImg = document.createElement("img");
		courseImg.src = "img/CourseOfStudies.png"; 

		const courseDiv = document.createElement("div");
		courseDiv.id = "courseOfStudies";
		courseDiv.className = "card-info";
		courseDiv.textContent = cardData.course;

		cardTitle2.appendChild(courseImg);
		cardTitle2.appendChild(courseDiv);

		
		const minorInfo = document.createElement("div");
		minorInfo.className = "minor-info";

		// internship role
		const minorInfoTitle1 = document.createElement("div");
		minorInfoTitle1.className = "card-title";

		const uniImg = document.createElement("img");
		uniImg.src = "img/InternRole.png"; 

		const uniDiv = document.createElement("div");
		uniDiv.id = "roleToCover";
		uniDiv.className = "card-info";
		uniDiv.textContent = cardData.roleToCover;

		minorInfoTitle1.appendChild(uniImg);
		minorInfoTitle1.appendChild(uniDiv);

		//internship period
		const minorInfoTitle2 = document.createElement("div");
		minorInfoTitle2.className = "card-title";

		const addrImg = document.createElement("img");
		addrImg.src = "img/internPeriod.png"; 

		const addrDiv = document.createElement("div");
		addrDiv.id = "internPeriod";
		addrDiv.className = "card-info";
		addrDiv.textContent = cardData.period;

		minorInfoTitle2.appendChild(addrImg);
		minorInfoTitle2.appendChild(addrDiv);

		// add info to the div
		minorInfo.appendChild(minorInfoTitle1);
		minorInfo.appendChild(minorInfoTitle2);

		//add div to the card
		card.appendChild(cardTitle1);
		card.appendChild(cardTitle2);
		card.appendChild(minorInfo);

		card.addEventListener("click", () => { //when clicking each card, company can go to different pages based on current position
			sessionStorage.setItem("matchID", card.id);
			switch (cardContainer) {
				case avail_newMatch_section:
					window.location.href = "accept_DeclineStudent_Company.html";
					break;
				case waitingResponse_section:
					sessionStorage.setItem("MatchType", "WaitResponse");
					window.location.href = "accept_DeclineStudent_Company.html";
					break;
				case waitingInterview_section:
					window.location.href = "interview.html";
					break;
				case acceptDeclineInterview_section:
					window.location.href = "acceptDeclineInterview_company.html";
					break;
			}
		});

		// insert card into main contenitor
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

	//company searching a particular student
	searchBtn.addEventListener("click", () => {

		var searchKey = document.getElementById("searchKey").value;
		if (searchKey === "") { //input control
			return;
		}
		
		makeCall("GET", "ProfileManager?page=filteredInternships&condition=" + searchKey, null,
			(req) => {
				if (req.readyState == 4) {
					document.getElementById("searchKey").value = "";
					document.getElementById("searchKey").placeholder = 'Search for internships';
					switch (req.status) {
						case 200: 
							var jsonData = JSON.parse(req.responseText);
							if (jsonData != null && jsonData.length > 0) {
								cleanUp();
								for (const match of jsonData) { //create matches found
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
							} else {
								alert("No internship found with that student");
								askOngoingIntern();
							}
							break;
						case 403:
							alert(req.responseText);
							break;
						case 412:
							alert(req.responseText);
							window.location.href = "index.html";
							sessionStorage.removeItem("user");
							break;
						case 500:
							alert(req.responseText);
							break;
					}
				}
			});

	})

	//clicked tab matches
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

		//ask all matches
		makeCall("GET", "MatchManager?page=showMatches", null,
			(req) => {
				if (req.readyState == 4) {
					switch (req.status) {
						case 200:
							var jsonData = JSON.parse(req.responseText);
							if (jsonData != null) {
								for (const match of jsonData) { //create match card in the right section (there are three sections)
									var pageLocation = avail_newMatch_section;
									if ("acceptedYNCompany" in match && "acceptedYNStudent" in match && "confirmedYN" in match && match.confirmedYN == true) {
										pageLocation = acceptDeclineInterview_section;
									}
									else if ("acceptedYNCompany" in match && !("acceptedYNStudent" in match)) {
										pageLocation = waitingResponse_section;
									} else if ("acceptedYNCompany" in match && "acceptedYNStudent" && "confirmedYN" in match && match.confirmedYN == false) {
										pageLocation = waitingInterview_section;
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
							alert(req.responseText);
							break;
						case 412:
							alert(req.responseText);
							window.location.href = "index.html";
							sessionStorage.removeItem("user");
							break;
						case 500:
							alert(req.responseText);
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

		//ask ongoing internships
		makeCall("GET", "ProfileManager?page=openOngoingInternships", null,
			(req) => {
				if (req.readyState == 4) {
					switch (req.status) {
						case 200: 
							var jsonData = JSON.parse(req.responseText);
							if (jsonData != null) {
								for (const match of jsonData) { //create internship card
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
								internList.innerText = "No ongoing internships";
							}

							break;
						case 403:
							alert(req.responseText);
							break;
						case 412:
							alert(req.responseText);
							window.location.href = "index.html";
							sessionStorage.removeItem("user");
							break;
						case 500:
							alert(req.responseText);
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
		
		//ask internships proposed by the company
		makeCall("GET", "PublicationManager?page=proposedInternships", null,
			(req) => {
				if (req.readyState == 4) {
					switch (req.status) {
						case 200: 
							var jsonData = JSON.parse(req.responseText);
							for (const internship of jsonData) { //create internship card
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
							alert(req.responseText);
							break;
						case 412:
							alert(req.responseText);
							window.location.href = "index.html";
							sessionStorage.removeItem("user");
							break;
						case 500:
							alert(req.responseText);
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

		//ask company internships without feedback
		makeCall("GET", "PublicationManager?page=waitingFeedbackInternships", null,
			(req) => {
				if (req.readyState == 4) {
					switch (req.status) {
						case 200: 
							var jsonData = JSON.parse(req.responseText);
							if (jsonData != null) {
								for (const internship of jsonData) { //create internship card
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
							alert(req.responseText);
							break;
						case 412:
							alert(req.responseText);
							window.location.href = "index.html";
							sessionStorage.removeItem("user");
							break;
						case 500:
							alert(req.responseText);
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

	//click on a card 
	internList.addEventListener("click", () => {
		const card = event.target.closest(".card");
		var tab = sessionStorage.getItem("tab");

		sessionStorage.setItem("MatchType", "NewMatch");
		if (card) {
			switch (tab) { //based on the current tab, go to different pages
				case "matches":
					sessionStorage.setItem("matchID", card.id);
					window.location.href = "accept_DeclineStudent_Company.html";
					break;
				case "waitingFeed":
					sessionStorage.setItem("matchID", card.id);
					window.location.href = "feedbackForm.html";
					break;
				default:
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