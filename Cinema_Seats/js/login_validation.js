var message ="";

function checkCookie(){
	return navigator.cookieEnabled;
}

function banner_button_login(clicked_id){
 
    if(!checkCookie()) {
        
        show_banner_and_hide_navigation();
        alert("Cookies are disable on this page, please turn it on!");
    
    }else if(sessionStorage.getItem("banner_display") == "disable"){
        
        hide_banner_and_show_navigation();
    
    }else if (checkCookie() && sessionStorage.getItem("banner_display") == "enable" && clicked_id != "close"){

        alert("In order to navigate through this login page, you must accept our cookie's policy");
		show_banner_and_hide_navigation();

	}else if (clicked_id == "close"){ 

        sessionStorage.setItem("banner_display", "disable");
        hide_banner_and_show_navigation();
    }
}


function pass_data(clicked_from){

    if (clicked_from == "log_main"){
    
        window.location.href = 'https://localhost/Project2020/index.php';

    }else if (clicked_from == "log_signup"){

        window.location.href = 'https://localhost/Project2020/sign_up.html';

    }
}

function show_banner_and_hide_navigation(){

    var children = document.getElementsByClassName("cookie-banner-log");
	for (var i = 0; i < children.length; i++) {
		children[i].style.visibility="visible";
    }
    
    children = document.getElementsByClassName("float_table_menu_log");
	for (var i = 0; i < children.length; i++) {
		children[i].style.visibility="hidden";
	}
}

function hide_banner_and_show_navigation(){

    var children = document.getElementsByClassName("cookie-banner-log");
	for (var i = 0; i < children.length; i++) {
		children[i].style.visibility="hidden";
	}

	children = document.getElementsByClassName("float_table_menu_log");
	for (var i = 0; i < children.length; i++) {
		children[i].style.visibility="visible";
	}
}

function validate_login (login) {

if(checkCookie() && sessionStorage.getItem("banner_display") == "disable")
{
    var email = document.login.email.value;
    var password = document.login.password.value;
              
    // Regular Expression For Email
    const regex = /(?!.*\.{2})^([a-z\d!#$%&'*+\-\/=?^_`{|}~\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF]+(\.[a-z\d!#$%&'*+\-\/=?^_`{|}~\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF]+)*|"((([ \t]*\r\n)?[ \t]+)?([\x01-\x08\x0b\x0c\x0e-\x1f\x7f\x21\x23-\x5b\x5d-\x7e\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF]|\\[\x01-\x09\x0b\x0c\x0d-\x7f\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF]))*(([ \t]*\r\n)?[ \t]+)?")@(([a-z\d\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF]|[a-z\d\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF][a-z\d\-._~\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF]*[a-z\d\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])\.)+([a-z\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF]|[a-z\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF][a-z\d\-._~\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF]*[a-z\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])\.?$/i;

    if( email == "" ) 
    {
        alert( "Please provide an email account." );
        document.login.email.focus() ;
        event.preventDefault();
        return false;
     }
     
     if (!(regex.test(email)))
      {
        alert( "Email syntax is not correct. provide a right one." );
        document.login.email.focus() ;
        event.preventDefault();
        return false;
    }
     
    if( password == "" ) 
    {
        alert( "Please enter a password" );
        document.login.password.focus() ;
        event.preventDefault();
        return false;
     }
     
    //Project works with a basic password
    /*
    if(!(password.match(/[a-z]/g)) || !(password.match(/[A-Z]/g)) || !(password.match(/[0-9]/g)) || (password.length <= 8))
    {
        alert( "The length of the first password should be at least of "+
        "8 alphanumeric characters, contain one lowercase letter, one uppercase letter and one numeric digit");
        document.login.password.focus() ;
        event.preventDefault();
        return false;
    }*/
    
    return true;
    }else{
        event.preventDefault();
        window.location.href = 'https://localhost/Project2020/login.html';
        return false;
    }
}
   
// Collecting available cookies
function read_cookies(){
    
	if(document.cookie.length > 42)
	{
		// banner_display cookie
		if(split_cookies(document.cookie, "banner_display")) 
			sessionStorage.setItem("banner_display",split_cookies(document.cookie, "banner_display"));
	
		if(split_cookies(document.cookie, "userid")) 
			userid = split_cookies(document.cookie, "userid");
	
		if(split_cookies(document.cookie, "email")){ 
			email = split_cookies(document.cookie, "email");
			email = email.replace('%40', '@');
		}

		if(split_cookies(document.cookie, "message")){ 
			message = split_cookies(document.cookie, "message");
			message = message.replaceAll('%21', '!');
			message = message.replaceAll('%2C', ',');
			message = message.replaceAll('+', '  ');
			message = message.replaceAll('%2F', '/');
			alert(message);
				if(message.includes("log") && message.includes("out"))
					document.cookie ='message=; Path=/; Expires=Thu, 01 Jan 1970 00:00:01 GMT;';
				else if (message.includes("expired"))
                    document.cookie ='message=; Path=/; Expires=Thu, 01 Jan 1970 00:00:01 GMT;';
                else if (message.includes("wrong"))
					document.cookie ='message=; Path=/; Expires=Thu, 01 Jan 1970 00:00:01 GMT;';
				else if (message.includes("exists"))
                    document.cookie ='message=; Path=/; Expires=Thu, 01 Jan 1970 00:00:01 GMT;';
                else if (message.includes("connection"))
                    document.cookie ='message=; Path=/; Expires=Thu, 01 Jan 1970 00:00:01 GMT;';
                else if (message.includes("password"))
					document.cookie ='message=; Path=/; Expires=Thu, 01 Jan 1970 00:00:01 GMT;';
				else if (message.includes("Welcome"))
					document.cookie ='message=; Path=/; Expires=Thu, 01 Jan 1970 00:00:01 GMT;';
			message="";
				
		}
    }
    
    banner_button_login();
}
												
// Extracting data from cookies
function split_cookies(cookie, value){
	let first_value = cookie.split('; ').find(row => row.startsWith(value));
	if(typeof first_value !== 'undefined'){
		let equal = first_value.lastIndexOf('=');
		return first_value.substring(equal + 1);
	}else{
		return false;
	}
}

/***************************
****   Code Execution   **** 
****************************/


if(checkCookie())
{
    if(!sessionStorage.getItem("banner_display"))
        sessionStorage.setItem("banner_display", "enable")
}

document.addEventListener("DOMContentLoaded", read_cookies);


