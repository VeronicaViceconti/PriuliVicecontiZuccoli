{
	const sendBtn = document.getElementById("sendBtn");
	const pageTitle = document.getElementById("pageTitle");
	const formTitle = document.getElementById("formTitle");
	const homeBtn = document.getElementById("homeBtn");
	const textArea = document.getElementById("textArea");

	const tab = sessionStorage.getItem("tab");
	const user = sessionStorage.getItem("user");
	const internship = sessionStorage.getItem("internshipID")

	window.onload = function() {

		switch (tab) {
			case "ongoing":
				pageTitle.innerHTML = "Complaint form";
				formTitle.innerHTML = "Write your complaint";
				break;
			case "waitingFeed":
				pageTitle.innerHTML = "Feedback form";
				formTitle.innerHTML = "Write your feedback";
				break;
		}
	}

	homeBtn.addEventListener("click", () => {

		switch (user) {
			case "student":
				window.location.href = "http://localhost:8080/SandC/homePageStudente.html";
				break;
			case "company":
				//TOO -> redirect to company homepage
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
}