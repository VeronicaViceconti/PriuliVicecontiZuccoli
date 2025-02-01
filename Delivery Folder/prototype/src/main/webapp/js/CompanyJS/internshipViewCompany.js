{
	const pageTitle = document.getElementById("pageTitle");
	const workCond = document.getElementById("workingConditions");
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
	const IDMatch = sessionStorage.getItem("matchID");

	window.onload = function() { //open the right infos based on the current tab selected

		jobDesc.innerText = "";
		workCond.innerText = "";
		company.innerText = "";
		role.innerText = "";
		location.innerText = "";
		period.innerText = "";
		openPositions.innerText = "";

		switch (tab) {
			case "ongoing": //if internship is in ongoing, can ask for writing complain
				pageTitle.innerHTML = "Ongoing internship Info";

				let complaintBtn = document.createElement("div");
				complaintBtn.classList.add("brownBtn");
				complaintBtn.textContent = "Write Complaint";

				complaintBtn.onclick = function() {
					window.location.href = "complainForm.html";
				}

				actionBtnsContainer.appendChild(complaintBtn);
				break;
			case "proposed": //if internship is proposed, can modify or delete it
				pageTitle.innerHTML = "Proposed internship info";

				let modifyBtn = document.createElement("div");
				modifyBtn.classList.add("brownBtn");
				modifyBtn.textContent = "Modify";

				modifyBtn.onclick = function() {
					alert("modify internship " + internID);
				}

				let deleteBtn = document.createElement("div");
				deleteBtn.classList.add("hollowBtn");
				deleteBtn.textContent = "Delete";

				deleteBtn.onclick = function() {
					alert("Delete internship " + internID);
				}

				actionBtnsContainer.appendChild(modifyBtn);
				actionBtnsContainer.appendChild(deleteBtn);

				break;
			case "waitingFeed": //if internship need feedback, you can add it

				pageTitle.innerHTML = "Request for Feedback";

				let feedbackBtn = document.createElement("div");
				feedbackBtn.classList.add("brownBtn");
				feedbackBtn.textContent = "Write feedback";
				feedbackBtn.onclick = function() {
					window.location.href = "feedbackForm.html"; 
				}

				actionBtnsContainer.appendChild(feedbackBtn);
				break;
		}
		//in each tab,need to load internship info
		loadInternshipInfo(IDMatch);

	}

	homeBtn.addEventListener("click", () => {
		window.location.href = "homePageCompany.html";
	})

	profileBtn.addEventListener("click", () => {
		window.location.href = "companyProfile.html";
	})

	function loadInternshipInfo(IDMatch) {
		switch (tab){
			case "proposed" :
				makeCall("GET", "ProfileManager?page=internshipInfo&ID=" + IDMatch, null,
					(req) => {
						if (req.readyState == 4) {
							switch (req.status) {
								case 200: // andato a buon fine
									var jsonData = JSON.parse(req.responseText);
									//fill the page							
									company.innerText = jsonData.company.name;
									role.innerText = jsonData.roleToCover;
									location.innerText = jsonData.company.address;
									period.innerText = jsonData.startingDate + " - " + jsonData.endingDate,
									openPositions.innerText = jsonData.openSeats;
		
									jobDesc.innerText = jsonData.jobDescription;
									if ("preferences" in jsonData) {
										for (const pref of jsonData.preferences) {
											workCond.innerHTML += pref.text + ";<br>";
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
				break;
			default : //ask for internship info
				makeCall("GET", "ProfileManager?page=internshipInfo&IDMatch=" + IDMatch, null,
					(req) => {
						if (req.readyState == 4) {
							switch (req.status) {
								case 200: 
									var jsonData = JSON.parse(req.responseText);
									//fill the page							
									company.innerText = jsonData.internship.company.name;
									role.innerText = jsonData.internship.roleToCover;
									location.innerText = jsonData.internship.company.address;
									period.innerText = jsonData.internship.startingDate + " - " + jsonData.internship.endingDate,
									openPositions.innerText = jsonData.internship.openSeats;
		
									jobDesc.innerText = jsonData.internship.jobDescription;
									if ("preferences" in jsonData.internship) {
										for (const pref of jsonData.internship.preferences) {
											workCond.innerHTML += pref.text + ";<br>";
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
		}
	}
}