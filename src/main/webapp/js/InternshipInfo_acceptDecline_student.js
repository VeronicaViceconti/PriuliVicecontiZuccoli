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

	const tab = sessionStorage.getItem("tab");
	const internID = sessionStorage.getItem("internshipID");

	window.onload = function() {

		console.log(sessionStorage.getItem("internshipID"));
		console.log(sessionStorage.getItem("tab"));

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

				applyBtn.onclick = function() {
					alert("apply for internship " + internID);
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
					alert("accept internship " + internID);
				}

				declineBtn.onclick = function() {
					alert("decline internship " + internID);
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
					window.location.href = "http://localhost:8080/SandC/generalForm.html";
				}

				actionBtnsContainer.appendChild(complaintBtn);

				break;
			case "waitingFeed":

				pageTitle.innerHTML = "Request for Feedback";

				let feedbackBtn = document.createElement("div");
				feedbackBtn.classList.add("brownBtn");
				feedbackBtn.textContent = "Write feedback";
				feedbackBtn.onclick = function() {
					window.location.href = "http://localhost:8080/SandC/generalForm.html";
				}

				actionBtnsContainer.appendChild(feedbackBtn);
				break;
			case "waitingFeed":
				pageTitle.innerHTML = "Waiting for response";
				break;
			case "waitingInterview":
				pageTitle.innerHTML = "Waiting for interview";
				break;

		}

		loadInternshipInfo(sessionStorage.getItem("internshipID"));


	}

	homeBtn.addEventListener("click", () => {
		window.location.href = "http://localhost:8080/SandC/homePageStudente.html";
	})

	profileBtn.addEventListener("click", () => {
		window.location.href = "http://localhost:8080/SandC/studentProfile.html";
	})

	function loadInternshipInfo(internshipId) {
		makeCall("GET", "ProfileManager?page=internshipInfo&ID=" + internshipId, null,
			(req) => {
				if (req.readyState == 4) {
					switch (req.status) {
						case 200: // andato a buon fine
							console.log(req.responseText);
							var jsonData = JSON.parse(req.responseText);
							console.log(jsonData);
							//fill the page							
							company.innerText = jsonData.company.name;
							role.innerText = jsonData.roleToCover;
							location.innerText = jsonData.company.address;
							period.innerText = jsonData.startingDate + " - " + jsonData.endingaData;
							openPositions.innerText = jsonData.openSeats;

							jobDesc.innerText = jsonData.jobDescription;
							if ("preferences" in jsonData) {
								for (const pref of jsonData.preferences) {
									workingConditions.innerText += pref.text + " ";
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