{
	const homeBtn = document.getElementById("homeBtn");
	
	window.onload = function() {
		//TODO caricare dati azienza
	}
	
	homeBtn.addEventListener("click", () => {
		window.location.href = "http://localhost:8080/SandC/homePageCompany.html";
	});
}

