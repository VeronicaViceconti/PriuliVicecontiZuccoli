{
	const sendBtn = document.getElementById("sendBtn");
	const pageTitle = document.getElementById("pageTitle");
	const homeBtn = document.getElementById("homeBtn");
	const profileBtn = document.getElementById("profileBtn");
	const textArea = document.getElementById("textArea");

	const tab = sessionStorage.getItem("tab");
	const user = sessionStorage.getItem("user");
	const internship = sessionStorage.getItem("internshipID")

	window.onload = function() {

		switch (user) {
			case "student":
				profileBtn.src = "img/profilePic.png"; 
				break;
			case "company":
				profileBtn.src = "img/companyProfilePic.png";
				break;
		}

		switch (tab) {
			case "ongoing":
				pageTitle.innerText = "Complaint form"
				createQuestionTextArea("Write a compplaint",1);
				break;
			case "waitingFeed":
				pageTitle.innerText = "Feedback form";
				createQuestionTextArea("Write feedback",1);
				break;
		}
		
		
		
	}

	homeBtn.addEventListener("click", () => {

		switch (user) {
			case "student":
				window.location.href = "homePageStudente.html";
				break;
			case "company":
				window.location.href = "homePageCompany.html";
				break;
		}
	})

	profileBtn.addEventListener("click", () => {

		switch (user) {
			case "student":
				window.location.href = "studentProfile.html";
				break;
			case "company":
				window.location.href = "companyProfile.html";
				break;
		}
	})

	sendBtn.addEventListener("click", () => {
		const textAreas = document.querySelectorAll(".textArea");
		const answers = {};
		textAreas.forEach((textarea) => {
			const questionNumber = textarea.getAttribute("data-num"); // Ottieni il numero della domanda
			const answer = textarea.value; // Ottieni la risposta inserita
			answers[`question_${questionNumber}`] = answer; // Aggiungi al JSON
		});

		const jsonString = JSON.stringify(answers, null, 2); // 'null, 2' per leggibilità

		console.log(jsonString);
		alert("send: " + jsonString);


	})
	
	function createQuestionTextArea(questionText, questionNum){
		const container = document.querySelector(".frameList");

		// question text div		
		const questionDiv = document.createElement("div");
		questionDiv.className = "question"; // Assegna la classe
		questionDiv.textContent = questionText;

		//answer container div
		const answerContainer = document.createElement("div");
		answerContainer.className = "frame";
		
		//answer textarea
		const textarea = document.createElement("textarea");
		textarea.className = "textArea"; // Assegna la classe
		textarea.id = "answer";
		textarea.setAttribute("data-num", questionNum);

		answerContainer.appendChild(textarea);

		container.appendChild(questionDiv);
		container.appendChild(answerContainer);
	}
}