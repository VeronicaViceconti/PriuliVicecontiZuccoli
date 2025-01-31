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
								if(interview != null){
									document.getElementById("question1").name = interview.form.questions[0].id;
									document.getElementById("question2").name = interview.form.questions[1].id;
									document.getElementById("question3").name = interview.form.questions[2].id;	
									interview = interview.id;
								}
		                        break;
		                    case 400: // bad request
		                        alert(message);
		                        window.location.href = "homePageCompany.html";
		                        break;
		                    case 401: // unauthorized
		                   	case 412: // not logged
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
			makeCall("POST", "Interviewer?page=submitInterview&interview=" + interview, form,
				function(x) {
					if (x.readyState == XMLHttpRequest.DONE) {
			            var message = x.responseText;
			            switch (x.status) {
			              case 200:  
			              	alert("successfully published");
			                window.location.href = "homePageCompany.html";
			                break;
			              case 400: // bad request
			                alert(message);
			                break;
			              case 412: // not logged
			              case 401: // unauthorized
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