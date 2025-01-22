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
	var interview;
	
	window.onload = function () {
			console.log("laoding...")
		    makeCall("GET", "Interviewer?page=requestForm&match=2", null, 
		        function(x) {
		            if (x.readyState == XMLHttpRequest.DONE) {
		                var message = x.responseText;
		                switch (x.status) {
		                    case 200:  //richiesta andata a buon fineù
								interview = JSON.parse(x.responseText);
								document.getElementById("question1").name = interview.form.questions[0].id;
								document.getElementById("question2").name = interview.form.questions[1].id;
								document.getElementById("question3").name = interview.form.questions[2].id;
		                        
		                        break;
		                    case 400: // bad request
		                        document.getElementById("serverResponse").textContent = message;
		                        break;
		                    case 401: // unauthorized
		                        document.getElementById("serverResponse").textContent = message;
		                        break;
		                    case 500: // server error
		                        document.getElementById("serverResponse").textContent = message;
		                        break;
		                }
		            }
		        }
		    );
		};

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
			makeCall("POST", "Interviewer?page=submitInterview&interview=" + interview.id, form,
				function(x) {
					if (x.readyState == XMLHttpRequest.DONE) {
			            var message = x.responseText;
			            
			            switch (x.status) {
			              case 200:  //richiesta andata a buon fine
			              	console.log("publicato con successo");
			                window.location.href = "homePageCompany.html";
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