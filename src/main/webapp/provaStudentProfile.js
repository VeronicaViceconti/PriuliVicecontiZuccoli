(function() {
	
	
    document.getElementsByClassName("card")[0].addEventListener('click', (e) => {
		e.preventDefault();
		 //metodo post della servlet CheckLogin
	      makeCall("GET", 'ProfileManager?page=showMatches',null, 
	      	(req) => {
	            
	          	if (req.readyState == 4) {
	            var message = req.responseText;
	            
	            switch (req.status) {
	              case 200:  //richiesta andata a buon fine
	              	//document.getElementById("errors").textContent = "FUNZIA TODOS";
	                break;
	              case 400: // bad request
	                //document.getElementById("signInError").textContent = message;
	                break;
	              case 500: // server error
	            	//document.getElementById("signInError").textContent = message;
	                break;
	            }
	          }
	        }
	      ); 
    });
   
    /*document.getElementById("uploadCv").addEventListener('click', (e) => {
		e.preventDefault();
        var form = e.target.closest("form");
        
        var internshipNAME = form.querySelector("[name='condition']").value;
		console.log(internshipNAME);
        if (form.checkValidity()) {
		 //metodo post della servlet CheckLogin
	      makeCall("POST", 'ProfileManager?page=matches', form,  
	        function(x) { // X è UN OGGETTO XMLHttpRequest
	          if (x.readyState == XMLHttpRequest.DONE) {
	            var message = x.responseText;
	            
	            switch (x.status) {
	              case 200:  //richiesta andata a buon fine
	                break;
	              case 400: // bad request
	                //document.getElementById("signInError").textContent = message;
	                break;
	              case 500: // server error
	            	//document.getElementById("signInError").textContent = message;
	                break;
	            }
	          }
	        }
	      ); 
	    } else {
	    	 form.reportValidity();
	    }
        
    });*/
    
})();