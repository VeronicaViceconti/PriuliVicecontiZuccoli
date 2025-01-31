{
	const workingConditions = document.getElementById("workingConditions");
	const jobDesc = document.getElementById("jobDescription");
	const actionBtnsContainer = document.getElementById("actionButtonContainer");
	const homeBtn = document.getElementById("homeBtn");
	const profileBtn = document.getElementById("profileBtn");
	const company = document.getElementById("CompanyName");
	const role = document.getElementById("internshipRole");
	const location = document.getElementById("internshipLocation");
	const period = document.getElementById("internshipPeriod");
	const openPositions = document.getElementById("internshipOpenPosition");

	const tab = sessionStorage.getItem("tab");
	const internID = sessionStorage.getItem("internshipID");
	var companyToken;

	window.onload = function() {//open different infos based on the current tab clicked


		jobDesc.innerText = "";
		workingConditions.innerText = "";
		company.innerText = "";
		role.innerText = "";
		location.innerText = "";
		period.innerText = "";
		openPositions.innerText = "";

		loadInternshipInfo(internID);

		switch (tab) {
			case "available/newMatch": //need to open internship info and working preferences
				document.title = "Internship Info";

				let applyBtn = document.createElement("div");
				applyBtn.classList.add("brownBtn");
				applyBtn.textContent = "Apply";
				loadPubAndWP(); //load the user pubblications
				applyBtn.onclick = function() {//if student apply, need to create match

					const optionChosen = document.getElementById("options").value;
					createMatch(optionChosen);
				}

				actionBtnsContainer.appendChild(applyBtn);
				break;
			case "matches": //need to open match info
				document.title = "Match info";

				let acceptBtn = document.createElement("div");
				acceptBtn.classList.add("brownBtn");
				acceptBtn.textContent = "accept";

				let declineBtn = document.createElement("div");
				declineBtn.classList.add("hollowBtn");
				declineBtn.textContent = "decline";

				//match accepted
				acceptBtn.onclick = function() {
					var matchID = sessionStorage.getItem("matchID");
					makeCall("GET", "MatchManager?page=acceptMatch&accept=1&IDmatch=" + matchID, null,
							(req) => {
								if (req.readyState == 4) {
									switch (req.status) {
										case 200:
											homeBtn.click();
											break;
										case 403:
											alert(req.responseText);
											break;
										case 412:
											alert(req.responseText);
											window.location.href = "index.html";
											break;
										case 500:
											alert(req.responseText);
											break;
									}
								}
							});
				}

				//match declined
				declineBtn.onclick = function() {
					var matchID = sessionStorage.getItem("matchID");
					makeCall("GET", "MatchManager?page=acceptMatch&accept=0&IDmatch=" + matchID, null,
						(req) => {
							if (req.readyState == 4) {
								switch (req.status) {
									case 200:
										homeBtn.click();
										break;
									case 403:
										alert(req.responseText);
										break;
									case 412:
										alert(req.responseText);
										window.location.href = "index.html";
										break;
									case 500:
										alert(req.responseText);
										break;
								}
							}
						});
				}

				actionBtnsContainer.appendChild(acceptBtn);
				actionBtnsContainer.appendChild(declineBtn);
				break;
			case "ongoing": //open ongoing internship info and can write complain on it
				document.title  = "Ongoing internship";

				let complaintBtn = document.createElement("div");
				complaintBtn.classList.add("brownBtn");
				complaintBtn.textContent = "Write complaint";
				complaintBtn.onclick = function() {
					window.location.href = "complainForm.html";
				}

				actionBtnsContainer.appendChild(complaintBtn);

				break;
			case "waitingFeed": //internships that waiting for feedback, can write it

				document.title  = "Request for Feedback";

				let feedbackBtn = document.createElement("div");
				feedbackBtn.classList.add("brownBtn");
				feedbackBtn.textContent = "Write feedback";
				feedbackBtn.onclick = function() {
					window.location.href = "feedbackForm.html"; 
				}

				actionBtnsContainer.appendChild(feedbackBtn);
				break;
			case "waitingInterview": //internships that are waiting interview
				document.title = "Waiting for interview";
				break;

		}

		loadInternshipInfo(sessionStorage.getItem("internshipID"));
	}

	homeBtn.addEventListener("click", () => {
		window.location.href = "homePageStudente.html";
	})

	profileBtn.addEventListener("click", () => {
		window.location.href = "studentProfile.html";
	})

	function loadInternshipInfo(internshipId) {
		makeCall("GET", "ProfileManager?page=internshipInfo&ID=" + internshipId, null,
			(req) => {
				if (req.readyState == 4) {
					switch (req.status) {
						case 200: // andato a buon fine
							var jsonData = JSON.parse(req.responseText);
							if (jsonData != null) {
								
								companyToken = jsonData.company.token;
								company.innerText = jsonData.company.name;
								role.innerText = jsonData.roleToCover;
								location.innerText = jsonData.company.address;
								period.innerText = jsonData.startingDate + " - " + jsonData.endingDate;
								openPositions.innerText = jsonData.openSeats;

								jobDesc.innerText = jsonData.jobDescription;
								if ("preferences" in jsonData) {
									for (const pref of jsonData.preferences) {
										workingConditions.innerHTML += pref.text + ";<br>";
									}
								}
							}

							break;
						case 403:
							alert(req.responseText);
							break;
						case 412:
							alert(req.responseText);
							window.location.href = "index.html";
							break;
						case 500:
							alert(req.responseText);
							break;
					}
				}
			});
	}
	
	function loadPubAndWP() { //ask all student publications and working preferences 
		makeCall("GET", "ProfileManager?page=openPubAndWP", null,
			(req) => {
				if (req.readyState == 4) {
					switch (req.status) {
						case 200: // andato a buon fine
							var jsonData = JSON.parse(req.responseText);
							//fill the page							
							const selectorContainer = document.createElement('div');
							selectorContainer.id = 'options-container';

							const label = document.createElement('label');
							label.textContent = "Choose one publication:";
							selectorContainer.appendChild(label);

							const select = document.createElement('select');
							select.id = "options";
							select.style.width = "300px";
							select.style.overflow = "hidden";
							select.style.textOverflow = "ellipsis";
							var increment = 1;
							for (const pub of jsonData) {
								const opzioni = pub.choosenPreferences;
								const option = document.createElement('option');
								option.innerHTML = increment + "-><br>";
								increment += 1;
								option.value = pub.id;
								opzioni.forEach(opzioneTesto => {
									option.innerHTML += opzioneTesto.text + "; ";
								});

								select.appendChild(option);
							}

							selectorContainer.appendChild(select);
							selectorContainer.style = "margin:50px";
							actionBtnsContainer.appendChild(selectorContainer);

							break;
						case 403:
							alert(req.responseText);
							break;
						case 412:
							alert(req.responseText);
							window.location.href = "index.html";
							break;
						case 500:
							alert(req.responseText);
							break;
					}
				}
			});
	}

	function createMatch(optionChosen) {
		sendNotif("New Match!", "New match available", companyToken);
		
		makeCall("GET", "ProfileManager?page=addInternshipThenHomepage&IDintern=" + internID + "&IDpub=" + optionChosen, null,
			(req) => {
				if (req.readyState == 4) {
					switch (req.status) {
						case 200: 					
							sendNotif("New Match!", "New match available", companyToken);
							window.location.href = "homePageStudente.html";
							break;
						case 403:
							alert(req.responseText);
							break;
						case 412:
							alert(req.responseText);
							window.location.href = "index.html";
							break;
						case 500:
							alert(req.responseText);
							break;
					}
				}
			});
	}

	function sendNotif(title, body, recipientToken) {
		console.log("mario");
		fetch('https://babbochat.altervista.org/SC_Notifications/php-FCM/send.php', {
			method: 'POST',
			headers: {
				'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'
			},
			body: new URLSearchParams({
				token: recipientToken,
				notifTitle: title,
				notifBody: body
			})
		})
	}
}