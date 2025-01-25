{
	const homeBtn = document.getElementById("homeBtn");
	const profileBtn = document.getElementById("profileBtn");
	const downloadBtn = document.getElementById("downloadBtn");
	const acceptBtn = document.getElementById("acceptBtn");
	const declineBtn = document.getElementById("declineBtn");
	const studentPreferences = document.getElementById("studentPreferences");

	//student info
	const studentName = document.getElementById("studentName");
	const studentCourseStudy = document.getElementById("studentCourseStudy");
	const studentEmail = document.getElementById("studentEmail");
	const studentPhone = document.getElementById("studentPhone");
	const studentAddress = document.getElementById("studentAddress");

	window.onload = function() {
		var matchID = sessionStorage.getItem("matchID");
		makeCall("GET", "MatchManager?page=openMatch&IDmatch=" + matchID, null,
			(req) => {
				if (req.readyState == 4) {
					switch (req.status) {
						case 200: // andato a buon fine
							var jsonData = JSON.parse(req.responseText);
							if(jsonData != null){
								studentName.innerText = jsonData.name;
								studentCourseStudy.innerText = jsonData.studyCourse;
								studentEmail.innerText = jsonData.email;
								studentPhone.innerText = jsonData.phoneNumber;
								studentAddress.innerText = jsonData.address;
								studentPreferences.innerText = jsonData.publications[0].choosenPreferences[0].text;
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

	profileBtn.addEventListener("click", () => {
		window.location.href = "companyProfile.html";
	})

	homeBtn.addEventListener("click", () => {
		window.location.href = "homePageCompany.html";
	})
	downloadBtn.addEventListener("click", () => {
		alert("downloadBtn");
	})

	acceptBtn.addEventListener("click", () => {
		alert("acceptBtn");
	})
	declineBtn.addEventListener("click", () => {
		alert("declineBtn");
	})


}