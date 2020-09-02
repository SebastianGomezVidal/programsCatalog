function checkCookie(){
	return navigator.cookieEnabled;
}
function banner_button_dev(elid){

	if (!checkCookie()) {
		alert("Cookies are disable on this page, please turn it on!");
		show_banner();
        
	}else if (sessionStorage.getItem("banner_display") == "enable"){
		alert("In order to navigate through this page, you must accept our cookie's policy");
		show_banner();

	}else if (elid == "close" || sessionStorage.getItem("banner_display") == "disable"){ 
		sessionStorage.setItem("banner_display", "disable");
        hide_banner();
        
	}
}

function show_banner(){

	var children = document.getElementsByClassName("cookie-banner-dev");
	for (var i = 0; i < children.length; i++) {
		children[i].style.visibility="visible";
	}

}

function hide_banner(){

	var children = document.getElementsByClassName("cookie-banner-dev");
	for (var i = 0; i < children.length; i++) {
		children[i].style.visibility="hidden";
	}
}

/****************************
 ****   Code Execution   **** 
 ****************************/

 
if (!sessionStorage.getItem("banner_display"))
sessionStorage.setItem("banner_display", "enable");


document.addEventListener('DOMContentLoaded', banner_button_dev);
