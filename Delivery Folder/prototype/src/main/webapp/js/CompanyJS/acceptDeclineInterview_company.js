{
	const homeBtn = document.getElementById("homeBtn");
	const profileBtn = document.getElementById("profileBtn");
	const acceptBtn = document.getElementById("acceptBtn");
	const declineBtn = document.getElementById("declineBtn");
	const studentPreferences = document.getElementById("studentPreferences");

	//student info
	const studentName = document.getElementById("studentName");
	const studentCourseStudy = document.getElementById("studentCourseStudy");
	const studentEmail = document.getElementById("studentEmail");
	const studentPhone = document.getElementById("studentPhone");
	const studentAddress = document.getElementById("studentAddress");

	const pdf = document.getElementById("pdf-frame");
	var interviewID;

	window.onload = function() {
		var matchID = sessionStorage.getItem("matchID");

		// get match info
		makeCall("GET", "MatchManager?page=openMatch&IDmatch=" + matchID, null,
			(req) => {
				if (req.readyState == 4) {
					switch (req.status) {
						case 200: 
							var jsonData = JSON.parse(req.responseText);
							if (jsonData != null) { //open match info
								studentPreferences.innerText = "";
								studentName.innerText = jsonData.student.name;
								studentCourseStudy.innerText = jsonData.student.studyCourse;
								studentEmail.innerText = jsonData.student.email;
								studentPhone.innerText = jsonData.student.phoneNumber;
								studentAddress.innerText = jsonData.student.address;
								if ("choosenPreferences" in jsonData) {
									for (const preference of jsonData.choosenPreferences)
										studentPreferences.innerText += preference.text + "; ";
								} else {
									studentPreferences.innerText += "no preferences";
								}
								if (jsonData.student.cv != null) {
									var pdfBase64 = jsonData.student.cv;
									var pdfArrayBuffer = base64ToArrayBuffer(pdfBase64);
									var blob = new Blob([pdfArrayBuffer], { type: 'application/pdf' });
									var url = URL.createObjectURL(blob);
									pdf.src = url;
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



		// get interview sunUp
		makeCall("GET", "Interviewer?page=getResponse&match=" + matchID, null,
			(req) => {
				if (req.readyState == 4) {
					switch (req.status) {
						case 200: // andato a buon fine
							var jsonData = JSON.parse(req.responseText);
							if (jsonData != null) { //show all questions and answers
								interviewID = jsonData.id;
								for (const question of jsonData.form.questions) {
									printQuestionAndAnswer(question.text, question.answer);
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

	profileBtn.addEventListener("click", () => {
		window.location.href = "companyProfile.html";
	})

	homeBtn.addEventListener("click", () => {
		window.location.href = "homePageCompany.html";
	})

	//interview accepted, send updated data
	acceptBtn.addEventListener("click", () => {
		makeCall("GET", "Interviewer?page=submitSelection&interview=" + interviewID + "&selected=1", null,
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
							sessionStorage.removeItem("user");
							break;
						case 500:
							alert(req.responseText);
							break;
					}
				}
			});
	})

	//interview declined, send updated data
	declineBtn.addEventListener("click", () => {
		makeCall("GET", "Interviewer?page=submitSelection&interview=" + interviewID + "&selected=0", null,
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
							sessionStorage.removeItem("user");
							break;
						case 500:
							alert(req.responseText);
							break;
					}
				}
			});
	})

	function base64ToArrayBuffer(base64) {
		var binaryString = window.atob(base64);
		var len = binaryString.length;
		var bytes = new Uint8Array(len);
		for (var i = 0; i < len; i++) {
			bytes[i] = binaryString.charCodeAt(i);
		}
		return bytes.buffer;
	}

	
	function printQuestionAndAnswer(question, answer) {
		const interviewSumUp = document.getElementById("interviewSumUp");

		var questionContainer = document.createElement("div");
		questionContainer.className = "question";
		questionContainer.innerText = question;

		var answerContainer = document.createElement("div");
		answerContainer.className = "answer";
		answerContainer.innerText = answer;

		interviewSumUp.appendChild(questionContainer);
		interviewSumUp.appendChild(answerContainer);
	}
}