{
	const homeBtn = document.getElementById("homeBtn");

	const studentName = document.getElementById("studentName");
	const studentEmail = document.getElementById("studentEmail");
	const studentPhone = document.getElementById("studentPhone");
	const studentAddress = document.getElementById("studentAddress");
	const preferences = document.getElementById("preferences");
	const modfyBtn = document.getElementById("modifyCurBtn");

	const ongoingList = document.getElementById("ongoing-internship");
	const waitingFeed = document.getElementById("feedbacks");
	
	const pdf = document.getElementById("pdf-frame");

	window.onload = function() { //load student profile info 
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

	//when click on the ongoing internship, need to open it
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

	//when click on the internship waiting for feedback, need to open it
	waitingFeed.addEventListener("click", () => {
		const card = event.target.closest(".card");
		if (card) {
			const cardId = card.id;
			sessionStorage.setItem("internshipID", cardId);
			sessionStorage.setItem("tab", "waitingFeed");
			sessionStorage.setItem("user", "student");
			sessionStorage.setItem("matchID", card.getAttribute("idMatch"));
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
							var studentData = jsonData[0];
							studentName.innerText = studentData.name;
							studentEmail.innerText = studentData.email;
							studentPhone.innerText = studentData.phoneNumber;
							studentAddress.innerText = studentData.address;
							
							var onGoing = jsonData[1];
							var waiting = jsonData[2];
							
							if(!(typeof studentData.publications[0].choosenPreferences === 'undefined')){
								var dl = document.createElement('dl');
								for(var i = 0; i < studentData.publications.length; i++){ // add publications
									var dt = document.createElement('dt');
									dt.innerHTML = "publication " + (i + 1) + ": ";
									dl.appendChild(dt);
									for(var j = 0; j < studentData.publications[i].choosenPreferences.length; j++){ // add preferences
										var dd = document.createElement('dd');
										dd.innerHTML = studentData.publications[i].choosenPreferences[j].text;
										dl.appendChild(dd);
									}
								}
								preferences.appendChild(dl);
							}
							
							if(waiting != null){
								for(var i = 0; i < waiting.length; i++){ //create internship card waiting feedback
									var tmp = waiting[0].internship
									createWaitingForFeedBack(waiting[0].id, tmp.id, tmp.company.name, tmp.roleToCover, tmp.startingDate, tmp.endingDate, tmp.company.address, tmp.openSeats);
								}
							}
							if(onGoing != null){ //create internship card ongoing internship
								createOnGoingInternship(onGoing.internship.id, onGoing.internship.company.name, onGoing.internship.roleToCover, onGoing.internship.startingDate, onGoing.internship.endingDate, onGoing.internship.company.address, onGoing.internship.openSeats);		
								sessionStorage.setItem("onGoingMatch", onGoing.id);						
							}
							if(studentData.cv != null){ //add cv
								var pdfBase64 = studentData.cv;
						        var pdfArrayBuffer = base64ToArrayBuffer(pdfBase64);
						        var blob = new Blob([pdfArrayBuffer], { type: 'application/pdf' });
						        var url = URL.createObjectURL(blob);
						        pdf.src = url;
							}else{
								modfyBtn.innerHTML = "upload CV";
																
								modfyBtn.addEventListener("click", () => {
										window.location.href = "addCvForm.html";
									})
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

	function base64ToArrayBuffer(base64) {
        var binaryString = window.atob(base64);
        var len = binaryString.length;
        var bytes = new Uint8Array(len);
        for (var i = 0; i < len; i++) {
            bytes[i] = binaryString.charCodeAt(i);
        }
        return bytes.buffer;
	}
	
	function createWaitingForFeedBack(idmatch, internshipId, name, role, startingDate, endingDate, address, seats){
		let card = document.createElement('div');
		card.className = "card";
		card.id = internshipId;
		
		let companyName = document.createElement('div');
		companyName.className = "card-company";
		companyName.innerHTML = name;
		
		
		card.appendChild(companyName);
		
		let info = document.createElement("div");
		info.className = "internship-info";
		
		let r = document.createElement('div');
		r.className = "card-info";
		
		let tmp = document.createElement('div');
		let img = document.createElement('img');
		img.src = "img/InternRole.png"
		
		tmp.appendChild(img);
		r.appendChild(tmp);
		
		let desc = document.createElement("div");
		desc.innerHTML = role;
		r.appendChild(desc);
		
		info.appendChild(r);
		
		r = document.createElement('div');
		r.className = "card-info";
		
		tmp = document.createElement('div');
		img = document.createElement('img');
		img.src = "img/internPeriod.png"
		
		tmp.appendChild(img);
		r.appendChild(tmp);
		
		desc = document.createElement("div");
		desc.innerHTML = startingDate + " - " + endingDate;
		r.appendChild(desc);
		
		info.appendChild(r);
		
		
		r = document.createElement('div');
		r.className = "card-info";
		
		tmp = document.createElement('div');
		img = document.createElement('img');
		img.src = "img/internLocation.png"
		
		tmp.appendChild(img);
		r.appendChild(tmp);
		
		desc = document.createElement("div");
		desc.innerHTML = address;
		r.appendChild(desc);
		
		info.appendChild(r);
		
		r = document.createElement('div');
		r.className = "card-info";
		
		tmp = document.createElement('div');
		img = document.createElement('img');
		img.src = "img/internOpenPositions.png"
		
		tmp.appendChild(img);
		r.appendChild(tmp);
		
		desc = document.createElement("div");
		desc.innerHTML = seats;
		r.appendChild(desc);
		
		info.appendChild(r);
				
		card.appendChild(info);
		card.setAttribute('idMatch', idmatch);
		waitingFeed.appendChild(card);
		
	}
	
	//create card ongoing internship
	function createOnGoingInternship(internshipId, name, role, startingDate, endingDate, address, seats){
			let card = document.createElement('div');
			card.className = "card";
			card.id = internshipId;
			
			let companyName = document.createElement('div');
			companyName.className = "card-company";
			companyName.innerHTML = name;
			
			
			card.appendChild(companyName);
			
			let info = document.createElement("div");
			info.className = "internship-info";
			
			let r = document.createElement('div');
			r.className = "card-info";
			
			let tmp = document.createElement('div');
			let img = document.createElement('img');
			img.src = "img/InternRole.png"
			
			tmp.appendChild(img);
			r.appendChild(tmp);
			
			let desc = document.createElement("div");
			desc.innerHTML = role;
			r.appendChild(desc);
			
			info.appendChild(r);
			
			r = document.createElement('div');
			r.className = "card-info";
			
			tmp = document.createElement('div');
			img = document.createElement('img');
			img.src = "img/internPeriod.png"
			
			tmp.appendChild(img);
			r.appendChild(tmp);
			
			desc = document.createElement("div");
			desc.innerHTML = startingDate + " - " + endingDate;
			r.appendChild(desc);
			
			info.appendChild(r);
			
			
			r = document.createElement('div');
			r.className = "card-info";
			
			tmp = document.createElement('div');
			img = document.createElement('img');
			img.src = "img/internLocation.png"
			
			tmp.appendChild(img);
			r.appendChild(tmp);
			
			desc = document.createElement("div");
			desc.innerHTML = address;
			r.appendChild(desc);
			
			info.appendChild(r);
			
			r = document.createElement('div');
			r.className = "card-info";
			
			tmp = document.createElement('div');
			img = document.createElement('img');
			img.src = "img/internOpenPositions.png"
			
			tmp.appendChild(img);
			r.appendChild(tmp);
			
			desc = document.createElement("div");
			desc.innerHTML = seats;
			r.appendChild(desc);
			
			info.appendChild(r);
					
			card.appendChild(info);
			ongoingList.appendChild(card);
			
		}
}