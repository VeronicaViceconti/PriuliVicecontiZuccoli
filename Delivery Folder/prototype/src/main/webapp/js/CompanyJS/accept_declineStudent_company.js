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

	window.onload = function() {
		var matchID = sessionStorage.getItem("matchID");
		//first thing to visualize is the match clicked by the user
		makeCall("GET", "MatchManager?page=openMatch&IDmatch=" + matchID, null,
			(req) => {
				if (req.readyState == 4) {
					switch (req.status) {
						case 200: 
							var jsonData = JSON.parse(req.responseText);
							if(jsonData != null){ //set all the student info of that match
								studentPreferences.innerText = "";
								studentName.innerText = jsonData.student.name;
								studentCourseStudy.innerText = jsonData.student.studyCourse;
								studentEmail.innerText = jsonData.student.email;
								studentPhone.innerText = jsonData.student.phoneNumber;
								studentAddress.innerText = jsonData.student.address;
								if ("choosenPreferences" in jsonData){
									for(const preference of jsonData.choosenPreferences)
										studentPreferences.innerText += preference.text+"; ";
								}else{
									studentPreferences.innerText += "no preferences";
								}
								if(jsonData.student.cv != null){
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
			
			//if in waiting response section, the match can't be accepted (already accepted)
			if(sessionStorage.getItem("MatchType") == "WaitResponse"){
				acceptBtn.remove();
				declineBtn.remove();
			}
	}

	profileBtn.addEventListener("click", () => {
		window.location.href = "companyProfile.html";
	})

	homeBtn.addEventListener("click", () => {
		window.location.href = "homePageCompany.html";
	})

	acceptBtn.addEventListener("click", () => {
		var matchID = sessionStorage.getItem("matchID");
		makeCall("GET", "MatchManager?page=acceptMatch&accept=1&IDmatch=" + matchID, null,
					(req) => {
						if (req.readyState == 4) {
							switch (req.status) {
								case 200: // andato a buon fine
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
	
	//match not accepted, send the updated data
	declineBtn.addEventListener("click", () => {
		var matchID = sessionStorage.getItem("matchID");
		makeCall("GET", "MatchManager?page=acceptMatch&accept=0&IDmatch=" + matchID, null,
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
}