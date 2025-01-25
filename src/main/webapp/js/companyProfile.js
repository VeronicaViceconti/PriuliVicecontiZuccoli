{
	const homeBtn = document.getElementById("homeBtn");
	const companyName = document.getElementById("companyName");
	const companyAddress = document.getElementById("companyAddress");
	const companyMail = document.getElementById("companyMail");
	const companyPhone = document.getElementById("companyPhone");
	const feedList = document.getElementById("feedList"); // feedbacks students have made to company
	
	window.onload = function() {
		companyName.innerText = "";
		companyAddress.innerText = "";
		companyMail.innerText = "";
		companyPhone.innerText = "";
		feedList.innerText = "";
		feedList.innerHTML = "";
		
		getProfileInfo();
	}
	
	homeBtn.addEventListener("click", () => {
		window.location.href = "homePageCompany.html";
	})
	
	
	
	function createFeedbackCard(feed,studentName) {
		// Dati della card
    const cardData = {
      studentName: studentName,
      feedback: feed
    };

    // Seleziona il contenitore in cui aggiungere la card
    const internList = document.getElementById("feedList");

    // Crea il div principale della card
    const card = document.createElement("div");
    card.className = "card";

    // Aggiungi il titolo della card (nome studente)
    const cardTitle1 = document.createElement("div");
    cardTitle1.className = "card-title";

    const nameDiv = document.createElement("div");
    nameDiv.className = "card-company";
    nameDiv.id = "StudentName";
    nameDiv.textContent = "From:"+cardData.studentName;

    cardTitle1.appendChild(nameDiv);

    // Crea la sezione minor-info
    const minorInfo = document.createElement("div");
    minorInfo.className = "minor-info";
    minorInfo.style = "padding-top:20px;";

    // Aggiungi informazioni università
    const minorInfoTitle1 = document.createElement("div");
    minorInfoTitle1.className = "minor-info";

    const uniDiv = document.createElement("div");
    uniDiv.className = "minor-info";
    uniDiv.textContent = cardData.feedback;

    minorInfoTitle1.appendChild(uniDiv);

    // Aggiungi i minor-info alla card
    minorInfo.appendChild(minorInfoTitle1);

    // Aggiungi tutti gli elementi alla card
    card.appendChild(cardTitle1);
    card.appendChild(minorInfo);
    
    // Inserisci la card nel contenitore
    internList.appendChild(card);
	}
	
	function getProfileInfo() {
		makeCall("GET", "ProfileManager?page=profileInfo", null,
			(req) => {
				if (req.readyState == 4) {
					switch (req.status) {
						case 200: // andato a buon fine
							var jsonData = JSON.parse(req.responseText);
							var profileInfo = jsonData[0];
							var feedbacks = jsonData[1];
							console.log(feedbacks);
							
							if (profileInfo != null) {
								companyName.innerText =profileInfo.name;
								companyAddress.innerText = profileInfo.address;
								companyMail.innerText = profileInfo.email;
								companyPhone.innerText = profileInfo.phoneNumber;
							}
							if(feedbacks != null){
								for(const feedback of feedbacks){
									createFeedbackCard(feedback.form.questions[0].answer,feedback.student1.name)
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

