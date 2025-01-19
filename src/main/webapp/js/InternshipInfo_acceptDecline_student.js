{
	const pageTitle = document.getElementById("pageTitle");
	const minQualifications = document.getElementById("minimumQualifications");
	const prefQualifications = document.getElementById("preferredQualifications");
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
		minQualifications.innerText = "";
		prefQualifications.innerText = "";
		company.innerText = "";
		role.innerText = "";
		location.innerText = "";
		period.innerText = "";
		openPositions.innerText = "";

		switch (tab) {
			case "available":
				pageTitle.innerHTML = "Internship Info";

				let applyBtn = document.createElement("div");
				applyBtn.classList.add("brownBtn");
				applyBtn.textContent = "Apply";

				applyBtn.onclick = function() {
					alert("apply for internship " + internID );
				}

				actionBtnsContainer.appendChild(applyBtn);
				break;
			case "metches":
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

		}

		//TODO riempire la pagina con i dati corretti dal DB
	}

	homeBtn.addEventListener("click", () => {
		window.location.href = "http://localhost:8080/SandC/homePageStudente.html";
	})

	profileBtn.addEventListener("click", () => {
		window.location.href = "http://localhost:8080/SandC/studentProfile.html";
	})
}