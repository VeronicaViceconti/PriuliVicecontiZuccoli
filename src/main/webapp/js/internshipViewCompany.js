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
	const internID = sessionStorage.getItem("internshipID");

	window.onload = function() {

		jobDesc.innerText = "";
		workCond.innerText = "";
		company.innerText = "";
		role.innerText = "";
		location.innerText = "";
		period.innerText = "";
		openPositions.innerText = "";

		switch (tab) {
			case "ongoing":
				pageTitle.innerHTML = "Ongoing internship Info";

				let complaintBtn = document.createElement("div");
				complaintBtn.classList.add("brownBtn");
				complaintBtn.textContent = "Write Complaint";

				complaintBtn.onclick = function() {
					alert("complaint for internship " + internID);
				}

				actionBtnsContainer.appendChild(complaintBtn);
				break;
			case "proposed":
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
			case "waitingFeed":

				pageTitle.innerHTML = "Request for Feedback";

				let feedbackBtn = document.createElement("div");
				feedbackBtn.classList.add("brownBtn");
				feedbackBtn.textContent = "Write feedback";
				feedbackBtn.onclick = function() {
					alert("feedback for internhip " + internID);
					//window.location.href = "http://localhost:8080/SandC/generalForm.html";
				}

				actionBtnsContainer.appendChild(feedbackBtn);
				break;
		}

		loadInternshipInfo(internID);

	}

	homeBtn.addEventListener("click", () => {
		window.location.href = "http://localhost:8080/SandC/homePageCompany.html";
	})

	profileBtn.addEventListener("click", () => {
		window.location.href = "http://localhost:8080/SandC/companyProfile.html";
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
							period.innerText = jsonData.startingDate + " - " + jsonData.endingDate,
							openPositions.innerText = jsonData.openSeats;

							jobDesc.innerText = jsonData.jobDescription;
							if ("workCond" in jsonData) {
								for (const pref of jsonData.workCond) {
									workCond.innerText += pref.text + " ";
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