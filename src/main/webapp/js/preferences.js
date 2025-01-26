{
	const sendBtn = document.getElementById("sendBtn");
	const pageTitle = document.getElementById("pageTitle");
	const formTitle = document.getElementById("formTitle");
	const homeBtn = document.getElementById("homeBtn");
	const textArea = document.getElementById("textArea");
	const framelist = document.getElementById("frameList")
	const profileBtn = document.getElementById("profileBtn");
	const tab = sessionStorage.getItem("tab");
	const user = sessionStorage.getItem("user");
	const internship = sessionStorage.getItem("internshipID");
	var preferences;
	
	window.onload = function () {
		    makeCall("GET", "PublicationManager?page=getPreferences&type=all", null, 
		        function(x) {
		            if (x.readyState == XMLHttpRequest.DONE) {
		                var message = x.responseText;
		                switch (x.status) {
		                    case 200:  //richiesta andata a buon fineù
								preferences = JSON.parse(x.responseText);
		                        addPreferences();
		                        break;
		                    case 400: // bad request
		                        console.log(message);
		                    case 401: // unauthorized
		                       console.log(message);
		                        break;
		                    case 500: // server error
		                        console.log(message);
		                        break;
		                }
		            }
		        }
		    );
		};
	
	let addPreferences = function (){
		let list = [];
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

	sendBtn.addEventListener("click", (e) => {
		e.preventDefault();
		let form = e.target.closest("form");
		if(form.checkValidity()){
			makeCall("POST", "" + (internship != null)?"PublicationManager?page=sendPreferences&idInternship=" + internship:"PublicationManager?page=sendPreferences", form,
				function(x) {
					if (x.readyState == XMLHttpRequest.DONE) {
			            var message = x.responseText;
			            
			            switch (x.status) {
			              case 200:  //richiesta andata a buon fine							
			                homeBtn.click();
			                break;
			              case 400: // bad request
			                console.log(message);
			                //this.alert.textContent = message;
			                break;
			              case 401: // unauthorized
			                console.log(message);
			                //this.alert.textContent = message;
			                break;
			              case 500: // server error
			            	console.log(message);
			            	//this.alert.textContent = message;
			                break;
			            }
			          }
				} 
			 )
		}
	});
}