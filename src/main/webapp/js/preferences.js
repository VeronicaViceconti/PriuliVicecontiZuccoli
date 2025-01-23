{
	const sendBtn = document.getElementById("sendBtn");
	const pageTitle = document.getElementById("pageTitle");
	const formTitle = document.getElementById("formTitle");
	const homeBtn = document.getElementById("homeBtn");
	const textArea = document.getElementById("textArea");
	const framelist = document.getElementById("frameList")
	
	const tab = sessionStorage.getItem("tab");
	const user = sessionStorage.getItem("user");
	const internship = sessionStorage.getItem("internshipID")
	var preferences;
	
	window.onload = function () {
			console.log("laoding...")
		    makeCall("GET", "PublicationManager?page=getPreferences&type=all", null, 
		        function(x) {
		            if (x.readyState == XMLHttpRequest.DONE) {
		                var message = x.responseText;
		                switch (x.status) {
		                    case 200:  //richiesta andata a buon fineù
								preferences = JSON.parse(x.responseText);
								console.log(preferences);
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
				window.location.href = "http://localhost:8080/SandC/homePageStudente.html";
				break;
			case "company":
				//TOO -> redirect to company homepage
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
			              	console.log("publicato con successo");
							
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