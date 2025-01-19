(function() {
	
	
    document.getElementsByClassName("card")[0].addEventListener('click', (e) => {
		e.preventDefault();

		console.log("ciaoo");
		 //metodo post della servlet CheckLogin
	      makeCall("GET", 'ProfileManager?page=internshipInfo&ID='+1010,null, 
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
    
})();