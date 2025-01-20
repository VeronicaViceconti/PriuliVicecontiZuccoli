
/**
 * 
 */
(function() {
	const regex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
	document.getElementById("uploadCvFormButton").addEventListener('click', (e) =>{
		e.preventDefault();
		let form = e.target.closest("form");
		if(form.checkValidity()){
			var self = this;
			makeCall("POST", "PublicationManager?page=sendCvForm", form, 
				function(x) {
					if (x.readyState == XMLHttpRequest.DONE) {
			            var message = x.responseText;
			            
			            switch (x.status) {
			              case 200:  //richiesta andata a buon fine
			              	document.getElementById("serverResponse").textContent = "publicato con successo";
			                //window.location.href = "addCvForm.html";
			                break;
			              case 400: // bad request
			                document.getElementById("serverResponse").textContent = message;
			                //this.alert.textContent = message;
			                break;
			              case 401: // unauthorized
			                document.getElementById("serverResponse").textContent = message;
			                //this.alert.textContent = message;
			                break;
			              case 500: // server error
			            	document.getElementById("serverResponse").textContent = message;
			            	//this.alert.textContent = message;
			                break;
			            }
			          }
				} 
			)
		} else {
			form.reportValidity();
		}
	})
})();


