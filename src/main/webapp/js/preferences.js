{
	const sendBtn = document.getElementById("sendBtn");
	const homeBtn = document.getElementById("homeBtn");
	const framelist = document.getElementById("frameList")
	const profileBtn = document.getElementById("profileBtn");
	const user = sessionStorage.getItem("user");
	const internship = sessionStorage.getItem("internshipID");
	var preferences;
	
	window.onload = function () { //open all preferences
		    makeCall("GET", "PublicationManager?page=getPreferences&type=all", null, 
		        function(x) {
		            if (x.readyState == XMLHttpRequest.DONE) {
		                var message = x.responseText;
		                switch (x.status) {
		                    case 200:  
								preferences = JSON.parse(x.responseText);
		                        addPreferences();
		                        break;
		                    case 400: // bad request
		                        alert(message);
		                    case 401: // unauthorized
		                       alert(message);
		                        break;
		                    case 500: // server error
		                        alert(message);
		                        break;
		                }
		            }
		        }
		    );
		};
	
	let addPreferences = function (){
		let button = framelist.firstChild;
		preferences.forEach((preference) => {
			
			const checkbox = document.createElement('input');
	        checkbox.type = 'checkbox';
	        checkbox.id = preference.text;
	        checkbox.name = preference.text;
	        checkbox.value = preference.id;

	        const checkboxLabel = document.createElement('label');
	        checkboxLabel.htmlFor = preference.text;
	        checkboxLabel.textContent = preference.text;
			framelist.insertBefore(checkbox, button);
			framelist.insertBefore(checkboxLabel, button);
		});
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

	//send preferences form
	sendBtn.addEventListener("click", (e) => {
		e.preventDefault();
		let form = e.target.closest("form");
		if(form.checkValidity()){
			makeCall("POST", "" + (internship != null)?"PublicationManager?page=sendPreferences&idInternship=" + internship:"PublicationManager?page=sendPreferences", form,
				function(x) {
					if (x.readyState == XMLHttpRequest.DONE) {
			            var message = x.responseText;
			            
			            switch (x.status) {
			              case 200: 
			              if(message.includes("You already have an equal publication"))	
			               		alert(message);
			              else						
			                homeBtn.click();
			                break;
			              case 400: // bad request
			                alert(message);
			                break;
			              case 401: // unauthorized
			                alert(message);
			                break;
			              case 412:
							 alert(message);
							 window.location.href = "index.html";
							 sessionStorage.removeItem("user");
							 break;
			              case 500: // server error
			            	alert(message);
			                break;
			            }
			          }
				} 
			 )
		}
	});
}