{
	const homeBtn = document.getElementById("homeBtn");

	const studentName = document.getElementById("studentName");
	const studentSurname = document.getElementById("studentSurname");
	const studentUni = document.getElementById("studentUniversity");
	const studentCourseOfStudies = document.getElementById("studentCourseOfStudies");

	const modfyBtn = document.getElementById("modifyCurBtn");
	const downloadBtn = document.getElementById("downloadCurBtn");

	const ongoingList = document.getElementById("ongoing-internship");
	const waitingFeed = document.getElementById("feedbacks");

	window.onload = function() {
		// TODO riempire la pagina con le info prese dal DB

		preferences.innerText = "";
		studentName.innerText = "";
		studentSurname.innerText = "";
		studentUni.innerText = "";
		studentCourseOfStudies.innerText = "";
	}


	homeBtn.addEventListener("click", () => {
		window.location.href = "http://localhost:8080/SandC/homePageStudente.html";
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
			window.location.href = "http://localhost:8080/SandC/internshipInfo_AcceptDecline_student.html";
		}
	})

	waitingFeed.addEventListener("click", () => {
		const card = event.target.closest(".card");
		if (card) {
			const cardId = card.id;
			sessionStorage.setItem("internshipID", cardId);
			sessionStorage.setItem("tab", "waitingFeed");
			sessionStorage.setItem("user", "student");
			window.location.href = "http://localhost:8080/SandC/internshipInfo_AcceptDecline_student.html";
		}
	})
}