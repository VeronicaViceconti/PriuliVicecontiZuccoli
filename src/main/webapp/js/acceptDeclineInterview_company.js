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

	const pdf = document.getElementById("pdf-frame");
	var interviewID;

	window.onload = function() {
		var matchID = sessionStorage.getItem("matchID");


		// get user info
		makeCall("GET", "MatchManager?page=openMatch&IDmatch=" + matchID, null,
			(req) => {
				if (req.readyState == 4) {
					switch (req.status) {
						case 200: // andato a buon fine
							var jsonData = JSON.parse(req.responseText);
							console.log(jsonData);
							if (jsonData != null) {
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
									// Crea un URL oggetto per il Blob
									var url = URL.createObjectURL(blob);
									// Imposta l'URL nell'iframe
									pdf.src = url;
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



		// get interview sunUp
		makeCall("GET", "Interviewer?page=getResponse&match=" + matchID, null,
			(req) => {
				if (req.readyState == 4) {
					switch (req.status) {
						case 200: // andato a buon fine
							var jsonData = JSON.parse(req.responseText);
							console.log(jsonData);
							if (jsonData != null) {
								interviewID = jsonData.id;
								//inserimento interview sumUp
								for (const question of jsonData.form.questions) {
									printQuestionAndAnswer(question.text, question.answer);
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

	profileBtn.addEventListener("click", () => {
		window.location.href = "companyProfile.html";
	})

	homeBtn.addEventListener("click", () => {
		window.location.href = "homePageCompany.html";
	})

	acceptBtn.addEventListener("click", () => {
		makeCall("GET", "Interviewer?page=submitSelection&interview=" + interviewID + "&selected=1", null,
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
	})

	declineBtn.addEventListener("click", () => {
		makeCall("GET", "Interviewer?page=submitSelection&interview=" + interviewID + "&selected=0", null,
			(req) => {
				if (req.readyState == 4) {
					switch (req.status) {
						case 200:
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