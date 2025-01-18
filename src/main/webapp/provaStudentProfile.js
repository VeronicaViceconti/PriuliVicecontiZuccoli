(function() {
	
	
    document.getElementById("card").addEventListener('click', (e) => {
		e.preventDefault();


        if (form.checkValidity()) {
		 //metodo post della servlet CheckLogin
	      makeCall("GET", 'ProfileManager?page=internshipInfo&ID='+1010, form,  
	        function(x) { // X è UN OGGETTO XMLHttpRequest
	          if (x.readyState == XMLHttpRequest.DONE) {
	            var message = x.responseText;
	            
	            switch (x.status) {
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
	    } else {
	    	 form.reportValidity();
	    }
        
    });
    
})();