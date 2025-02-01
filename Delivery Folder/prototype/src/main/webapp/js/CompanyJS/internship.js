{
	const sendBtn = document.getElementById("sendBtn");
	const homeBtn = document.getElementById("homeBtn");

	const user = sessionStorage.getItem("user");
	var preferences;
		
	homeBtn.addEventListener("click", () => {

		switch (user) {
			case "student":
				window.location.href = "homePageStudente.html";
				break;
			case "company":
				window.location.href = "homePageCompany.html";
				break;
		}
	});
		
	//need to publish new internship
	sendBtn.addEventListener("click", (e) => {
		e.preventDefault();
		let form = e.target.closest("form");
		var data1 = new Date(document.getElementById("data1").value);
		var data2 = new Date(document.getElementById("data2").value);	
		
		
		if(form.checkValidity() && data1 < data2){
			makeCall("POST", "PublicationManager?page=sendProjectForm", form,
				function(x) {
					if (x.readyState == XMLHttpRequest.DONE) {
			            var message = x.responseText;
			            
			            switch (x.status) {
			              case 200:  
			              	//go to ask preferences
							sessionStorage.setItem("internshipID", x.responseText);
			               	window.location.href = "preferencePublication.html";
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