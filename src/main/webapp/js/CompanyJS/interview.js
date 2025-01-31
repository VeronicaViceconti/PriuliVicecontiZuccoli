{
	const sendBtn = document.getElementById("sendBtn");
	const homeBtn = document.getElementById("homeBtn");
	const profileBtn = document.getElementById("profileBtn");
	const user = sessionStorage.getItem("user");
	var interview;
	
	window.onload = function () { //open interview form
			var matchID = sessionStorage.getItem("matchID");
		    makeCall("GET", "Interviewer?page=requestForm&match=" + matchID  , null, 
		        function(x) {
		            if (x.readyState == XMLHttpRequest.DONE) {
		                var message = x.responseText;
		                switch (x.status) {
		                    case 200:  
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
				window.location.href = "homePageStudente.html";
				break;
			case "company":
				window.location.href = "homePageCompany.html";
				break;
		}
	})
	
	profileBtn.addEventListener("click", () =>{
		window.location.href = "companyProfile.html";
	})

	//send the interview form with all the answers
	sendBtn.addEventListener("click", (e) => {
		e.preventDefault();
		let form = e.target.closest("form");
		if(form.checkValidity()){
			makeCall("POST", "Interviewer?page=submitInterview&interview=" + interview.id, form,
				function(x) {
					if (x.readyState == XMLHttpRequest.DONE) {
			            var message = x.responseText;
			            console.log(message);
			            switch (x.status) {
			              case 200:  
			              	alert("publicato con successo");
			                window.location.href = "homePageCompany.html";
			                break;
			              case 400: // bad request
			                console.log(message);
			                break;
			              case 401: // unauthorized
			                console.log(message);
			                break;
			              case 500: // server error
			            	console.log(message);
			                break;
			            }
			          }
				} 
			 )
		}
	});
}