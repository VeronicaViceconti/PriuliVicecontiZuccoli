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
	
		
	homeBtn.addEventListener("click", () => {

		switch (user) {
			case "student":
				window.location.href = "homePageStudente.html";
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
			makeCall("POST", "PublicationManager?page=sendCvForm", form,
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