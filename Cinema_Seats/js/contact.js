var message ="";

function checkCookie(){
	return navigator.cookieEnabled;
}

function banner_button_con(clicked_id){
 
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


function show_banner_and_hide_navigation(){

    var children = document.getElementsByClassName("cookie-banner-con");
	for (var i = 0; i < children.length; i++) {
		children[i].style.visibility="visible";
    }
    
    children = document.getElementsByClassName("float_table_menu_con");
	for (var i = 0; i < children.length; i++) {
		children[i].style.visibility="hidden";
	}
}

function hide_banner_and_show_navigation(){

    var children = document.getElementsByClassName("cookie-banner-con");
	for (var i = 0; i < children.length; i++) {
		children[i].style.visibility="hidden";
	}

	children = document.getElementsByClassName("float_table_menu_con");
	for (var i = 0; i < children.length; i++) {
		children[i].style.visibility="visible";
	}
}

function validate_email () {

if(checkCookie() && sessionStorage.getItem("banner_display") == "disable")
{
    var name = document.email_data.name.value;
    var email = document.email_data.email.value;
    var subject = document.getElementById("subject");
    var text= subject.options[subject.selectedIndex].text;
    var mess = document.getElementById("mess").value;

    // Regular Expression For Email
    const regex = /(?!.*\.{2})^([a-z\d!#$%&'*+\-\/=?^_`{|}~\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF]+(\.[a-z\d!#$%&'*+\-\/=?^_`{|}~\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF]+)*|"((([ \t]*\r\n)?[ \t]+)?([\x01-\x08\x0b\x0c\x0e-\x1f\x7f\x21\x23-\x5b\x5d-\x7e\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF]|\\[\x01-\x09\x0b\x0c\x0d-\x7f\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF]))*(([ \t]*\r\n)?[ \t]+)?")@(([a-z\d\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF]|[a-z\d\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF][a-z\d\-._~\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF]*[a-z\d\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])\.)+([a-z\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF]|[a-z\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF][a-z\d\-._~\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF]*[a-z\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])\.?$/i;

    if( name == "" ) 
    {
        alert( "Please provide a name!" );
        document.email_data.name.focus();
        event.preventDefault();
        return false;
     }

    else if( email == "" ) 
    {
        alert( "Please provide an email account!" );
        document.email_data.email.focus();
        event.preventDefault();
        return false;
     }
     
     else if (!(regex.test(email)))
      {
        alert( "Email syntax is not correct. provide a right one!" );
        document.email_data.email.focus();
        event.preventDefault();
        return false;
    }

    else if (text == "Choose a subject...")
    {
      alert( "Please provide a correct subject!" );
      document.email_data.subject.focus();
      event.preventDefault();
      return false;
    }

    else if (mess == "")
    {
      alert( "Please include a message!");
      document.email_data.mess.focus();
      event.preventDefault();
      return false;
    }

    return true;

}else{
        event.preventDefault();
        window.location.href = 'https://localhost/Project2020/contact.html';
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
                else if(message.includes("contact"))
                    document.cookie ='message=; Path=/; Expires=Thu, 01 Jan 1970 00:00:01 GMT;';
                else if(message.includes("error"))
					document.cookie ='message=; Path=/; Expires=Thu, 01 Jan 1970 00:00:01 GMT;';
			message="";
				
		}
    }
    
    banner_button_con();
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


