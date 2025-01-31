{
	const homeBtn = document.getElementById("homeBtn");
	const companyName = document.getElementById("companyName");
	const companyAddress = document.getElementById("companyAddress");
	const companyMail = document.getElementById("companyMail");
	const companyPhone = document.getElementById("companyPhone");
	const feedList = document.getElementById("feedList"); // feedbacks students have made to company
	
	window.onload = function() { //show profile info
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
		
	    const cardData = {
	      studentName: studentName,
	      feedback: feed
	    };
	
	    
	    const internList = document.getElementById("feedList");
	
	    const card = document.createElement("div");
	    card.className = "card";
	
	    const cardTitle1 = document.createElement("div");
	    cardTitle1.className = "card-title";
	
	    const nameDiv = document.createElement("div");
	    nameDiv.className = "card-company";
	    nameDiv.id = "StudentName";
	    nameDiv.textContent = "From:"+cardData.studentName;
	
	    cardTitle1.appendChild(nameDiv);
	
	    const minorInfo = document.createElement("div");
	    minorInfo.className = "minor-info";
	    minorInfo.style = "padding-top:20px;";
	
	    const minorInfoTitle1 = document.createElement("div");
	    minorInfoTitle1.className = "minor-info";
	
	    const uniDiv = document.createElement("div");
	    uniDiv.className = "minor-info";
	    uniDiv.textContent = cardData.feedback;
	
	    minorInfoTitle1.appendChild(uniDiv);
	
	    minorInfo.appendChild(minorInfoTitle1);
	
	    card.appendChild(cardTitle1);
	    card.appendChild(minorInfo);
	    
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
							
							if (profileInfo != null) { //fill the infos
								companyName.innerText =profileInfo.name;
								companyAddress.innerText = profileInfo.address;
								companyMail.innerText = profileInfo.email;
								companyPhone.innerText = profileInfo.phoneNumber;
							}
							if(feedbacks != null){
								for(const feedback of feedbacks){ //create feedbacks done by the students
									createFeedbackCard(feedback.form.questions[0].answer,feedback.student1.name)
								}
							}
							break;
						case 403:
							alert(req.responseText);
							break;
						case 412:
							alert(req.responseText);
							window.location.href = "index.html";
							break;
						case 500:
							alert(req.responseText);
							break;
					}
				}
			});
	}
}

