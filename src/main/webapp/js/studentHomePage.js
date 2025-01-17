const matchesTab = document.getElementById("Matches_Tab");
const availableInternTab = document.getElementById("Avail_Inter_Tab");
const homeBtn = document.getElementById("homeBtn");
const profileBtn = document.getElementById("profileBtn");

matchesTab.addEventListener("click", () => {
	
	//change tab color
    availableInternTab.style.color = "#2e4057" ;
    matchesTab.style.color = "#a37659";
	
	//TODO

});


availableInternTab.addEventListener("click", () => {

	//change tab color
    availableInternTab.style.color = "#a37659";
    matchesTab.style.color = "#2e4057";

	//TODO
});

homeBtn.addEventListener("click", () =>{
	//TODO
})

homeprofileBtn.addEventListener("click", () =>{
	//TODO
})