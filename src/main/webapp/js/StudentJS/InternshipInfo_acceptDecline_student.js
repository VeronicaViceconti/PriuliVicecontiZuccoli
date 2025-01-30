{
	const pageTitle = document.getElementById("pageTitle");
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
	const applyButton = document.getElementById("applyButton");

	const tab = sessionStorage.getItem("tab");
	const internID = sessionStorage.getItem("internshipID");
	const matchID = sessionStorage.getItem("matchID");
	
	window.onload = function() {

		jobDesc.innerText = "";
		workingConditions.innerText = "";
		company.innerText = "";
		role.innerText = "";
		location.innerText = "";
		period.innerText = "";
		openPositions.innerText = "";

		switch (tab) {
			case "available/newMatch":
				pageTitle.innerHTML = "Internship Info";

				let applyBtn = document.createElement("div");
				applyBtn.classList.add("brownBtn");
				applyBtn.textContent = "Apply";
				loadPubAndWP();
				applyBtn.onclick = function() {
					const optionChosen = document.getElementById("options").value;
					createMatch(optionChosen);
					
				}

				actionBtnsContainer.appendChild(applyBtn);
				break;
			case "matches":
				pageTitle.innerHTML = "Match info";

				let acceptBtn = document.createElement("div");
				acceptBtn.classList.add("brownBtn");
				acceptBtn.textContent = "accept";

				let declineBtn = document.createElement("div");
				declineBtn.classList.add("hollowBtn");
				declineBtn.textContent = "decline";

				acceptBtn.onclick = function() {
					var matchID = sessionStorage.getItem("matchID");
					makeCall("GET", "MatchManager?page=acceptMatch&accept=1&IDmatch=" + matchID, null,
										(req) => {
											if (req.readyState == 4) {
												switch (req.status) {
													case 200: // andato a buon fine
														homeBtn.click();
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

				declineBtn.onclick = function() {
					var matchID = sessionStorage.getItem("matchID");
							makeCall("GET", "MatchManager?page=acceptMatch&accept=0&IDmatch=" + matchID, null,
												(req) => {
													if (req.readyState == 4) {
														switch (req.status) {
															case 200: // andato a buon fine
																homeBtn.click();
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

				actionBtnsContainer.appendChild(acceptBtn);
				actionBtnsContainer.appendChild(declineBtn);
				break;
			case "ongoing":
				pageTitle.innerHTML = "Ongoing internship";

				let complaintBtn = document.createElement("div");
				complaintBtn.classList.add("brownBtn");
				complaintBtn.textContent = "Write complaint";
				complaintBtn.onclick = function() {
					window.location.href = "complainForm.html"; 
				}

				actionBtnsContainer.appendChild(complaintBtn);

				break;
			case "waitingFeed":

				pageTitle.innerHTML = "Request for Feedback";

				let feedbackBtn = document.createElement("div");
				feedbackBtn.classList.add("brownBtn");
				feedbackBtn.textContent = "Write feedback";
				feedbackBtn.onclick = function() {
					window.location.href = "feedbackForm.html"; 
				}

				actionBtnsContainer.appendChild(feedbackBtn);
				break;
			case "waitingInterview":
				pageTitle.innerHTML = "Waiting for interview";
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
							console.log(jsonData);
							if(jsonData != null){
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
	
	function loadPubAndWP() {
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
								for(const pub of jsonData){
									const opzioni = pub.choosenPreferences;	
									const option = document.createElement('option');
									option.innerHTML = increment+"-><br>";
									increment += 1;	
									option.value = pub.id;
								  	opzioni.forEach(opzioneTesto => {
								    	option.innerHTML += opzioneTesto.text+"; "; 
								 	});
								 	
								 	select.appendChild(option);
								}
							  
							selectorContainer.appendChild(select);
							selectorContainer.style = "margin:50px";
							actionBtnsContainer.appendChild(selectorContainer);

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
	
	function createMatch(optionChosen) {
		makeCall("GET", "ProfileManager?page=addInternshipThenHomepage&IDintern="+internID+"&IDpub="+optionChosen, null,
			(req) => {
				if (req.readyState == 4) {
					switch (req.status) {
						case 200: // andato a buon fine
							//fill the page							
							window.location.href = "homePageStudente.html";

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