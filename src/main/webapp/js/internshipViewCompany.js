{
	const pageTitle = document.getElementById("pageTitle");
	const minQualifications = document.getElementById("minimumQualifications");
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

	}

	homeBtn.addEventListener("click", () => {
		window.location.href = "http://localhost:8080/SandC/homePageCompany.html";
	})

	profileBtn.addEventListener("click", () => {
		alert("company profile");
		//window.location.href = "http://localhost:8080/SandC/studentProfile.html";
	})
}