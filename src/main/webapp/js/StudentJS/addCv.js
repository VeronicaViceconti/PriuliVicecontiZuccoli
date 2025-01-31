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
				window.location.href = "hopePageCompany.html";
				break;
		}
	})

	//send cv form
	sendBtn.addEventListener("click", (e) => {
		e.preventDefault();
		let form = e.target.closest("form");
		if(form.checkValidity()){
			makeCall("POST", "PublicationManager?page=sendCvForm", form,
				function(x) {
					if (x.readyState == XMLHttpRequest.DONE) {
			            var message = x.responseText;
			            
			            switch (x.status) {
			              case 200: 
			              	alert("successfully published");
			                homeBtn.click();
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
	});
}