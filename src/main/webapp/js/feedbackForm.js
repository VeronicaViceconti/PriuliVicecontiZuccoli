
 const sendBtn = document.getElementById("form");
 const user = sessionStorage.getItem('user');
 const idMatch = sessionStorage.getItem('matchID');
 
 sendBtn.addEventListener("click", (e) => {
	 	e.preventDefault();
	 	var form = e.target.closest("form");
		
		if(form.checkValidity()){
			makeCall("POST", "FeedbackManager?idMatch="+idMatch, form,
				function(x) {
					if (x.readyState == XMLHttpRequest.DONE) {
			            var message = x.responseText;
			            
			            switch (x.status) {
			              case 200:  //richiesta andata a buon fine		
			              		switch (user) {
									case "student":
										window.location.href = "homePageStudente.html";
										break;
									case "company":
										window.location.href = "homePageCompany.html";
										break;
								}
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
} )