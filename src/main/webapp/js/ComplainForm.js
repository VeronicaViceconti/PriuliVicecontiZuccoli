const sendBtn = document.getElementById("complainButton");
 const user = sessionStorage.getItem('user');
 const matchID = sessionStorage.getItem('matchID');
 const onGoingMatch = sessionStorage.getItem("onGoingMatch");
 
 //send complain form
 sendBtn.addEventListener("click", (e) => {
	 	e.preventDefault();
	 	var form = e.target.closest("form");
		
		if(form.checkValidity()){
			makeCall("POST",(user == "student")? "ComplainManager?idMatch=" + onGoingMatch: "ComplainManager?idMatch="+ matchID, form,
				function(x) {
					if (x.readyState == XMLHttpRequest.DONE) {
			            var message = x.responseText;
			            
			            switch (x.status) {
			              case 200:  
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
			                alert(message);
			                break;
			              case 401: // unauthorized
			                alert(message);
			                break;
			              case 500: // server error
			            	alert(message);
			                break;
			            }
			          }
				} 
			 )
		}
} )