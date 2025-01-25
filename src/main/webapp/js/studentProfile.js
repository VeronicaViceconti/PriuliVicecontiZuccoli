{
	const homeBtn = document.getElementById("homeBtn");

	const studentName = document.getElementById("studentName");
	const studentEmail = document.getElementById("studentEmail");
	const studentPhone = document.getElementById("studentPhone");
	const studentAddress = document.getElementById("studentAddress");
	
	const preferences = document.getElementById("preferences");

	const modfyBtn = document.getElementById("modifyCurBtn");
	const downloadBtn = document.getElementById("downloadCurBtn");

	const ongoingList = document.getElementById("ongoing-internship");
	const waitingFeed = document.getElementById("feedbacks");

	window.onload = function() {
		preferences.innerText = "";
		studentName.innerText = "";
		studentEmail.innerText = "";
		studentPhone.innerText = "";
		studentAddress.innerText = "";
		loadUserInfo();
	}


	homeBtn.addEventListener("click", () => {
		window.location.href = "homePageStudente.html";
	})

	modfyBtn.addEventListener("click", () => {
		alert("modify");
	})

	downloadBtn.addEventListener("click", () => {
		alert("Download");
	})

	ongoingList.addEventListener("click", () => {
		const card = event.target.closest(".card");
		if (card) {
			const cardId = card.id;
			sessionStorage.setItem("internshipID", cardId);
			sessionStorage.setItem("tab", "ongoing");
			sessionStorage.setItem("user", "student");
			window.location.href = "internshipInfo_AcceptDecline_student.html";
		}
	})

	waitingFeed.addEventListener("click", () => {
		const card = event.target.closest(".card");
		if (card) {
			const cardId = card.id;
			sessionStorage.setItem("internshipID", cardId);
			sessionStorage.setItem("tab", "waitingFeed");
			sessionStorage.setItem("user", "student");
			window.location.href = "internshipInfo_AcceptDecline_student.html";
		}
	})

	function loadUserInfo() {
		makeCall("GET", 'ProfileManager?page=profileInfo' , null,
			(req) => {
				if (req.readyState == 4) {
					switch (req.status) {
						case 200: // andato a buon fine
							var jsonData = JSON.parse(req.responseText);
							console.log(jsonData);
							var studentData = jsonData[0];
							console.log(studentData);
							studentName.innerText = studentData.name;
							studentEmail.innerText = studentData.email;
							studentPhone.innerText = studentData.phoneNumber;
							studentAddress.innerText = studentData.address;
							preferences.innerText = studentData.publications[0].choosenPreferences[0].text;
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