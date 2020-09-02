/************************
 *** Global Variables *** 
 ************************/

var userid = "anonymous";		// User name in the App db 

var email ="";					// Registered mail by user

var message = "";				// Action confirmation messages

var available = 0;
var reserved = 0;
var unavailable = 0;

/*****************
 *** Functions *** 
 *****************/

function banner_button(elid){

	if (!checkCookie()) {
		alert("Cookies are disable on this page, please turn it on!");
		show_banner_and_hide_navigation();
		reset_counters();
	}else if (checkCookie() && elid != "close")
	{
		alert("In order to navigate through this page, you must accept our cookie's policy");
		show_banner_and_hide_navigation();

	}else if (elid == "close"){ 
		sessionStorage.setItem("banner_display", "disable");
		hide_banner_and_show_navigation();
		update_seats_layout();

	}
}

function show_banner_and_hide_navigation(){

	var children = document.getElementsByClassName("cookie-banner");
	for (var i = 0; i < children.length; i++) {
		children[i].style.visibility="visible";
	}

	children = document.getElementsByClassName("float_table_menu");
	for (var i = 0; i < children.length; i++) {
		children[i].style.visibility="hidden";
	}

	children = document.getElementsByClassName("float_table_stats");
	for (var i = 0; i < children.length; i++) {
		children[i].style.visibility="hidden";
	}
}

 function hide_banner_and_show_navigation(){

	var children = document.getElementsByClassName("cookie-banner");
	for (var i = 0; i < children.length; i++) {
		children[i].style.visibility="hidden";
	}

	children = document.getElementsByClassName("float_table_menu");
	for (var i = 0; i < children.length; i++) {
		children[i].style.visibility="visible";
	}

	children = document.getElementsByClassName("float_table_stats");
	for (var i = 0; i < children.length; i++) {
		children[i].style.visibility="visible";
	}
}

function db_update (){
							$.ajax({
							type: "POST",
							url: 'php/booking_status.php',
							async: false,
							success:  function (result) {
										// Parsing php result into json
										console.log(JSON.parse(result));
										if(result)	seats = JSON.parse(result);
										else seats ="";
									}
							})
						
}

// Method for updating status of seats
function update_seats_location() { 
							
							
							// outer loop applies to outer array
							for (var i=0, len = seats.length; i < len; i++) 
							{
								// inner loop applies to sub-arrays
								for (var j=1, len2 = seats[i].length; j < len2; j++) 
								{
									// accesses each element of each sub-array in turn
									 if(seats[i][j] == 0)
									{
										document.getElementById(i+""+(j-1)).style.backgroundColor = "#00e676";
										document.getElementById(i+""+(j-1)).setAttribute("aria-label","It is available!");
										document.getElementById("c-"+i+""+(j-1)).style.backgroundColor = "#00e676";
										available++;
									}
									else if(userid == seats[i][j])
									{
										document.getElementById(i+""+(j-1)).style.backgroundColor = "#ff3d00";
										document.getElementById(i+""+(j-1)).setAttribute("aria-label","Reserved by You!");
										document.getElementById("c-"+i+""+(j-1)).style.backgroundColor = "#ff3d00";
										reserved++;
									}
									else
									{
										document.getElementById(i+""+(j-1)).style.backgroundColor = "#f50057";
										document.getElementById(i+""+(j-1)).setAttribute("aria-label","It is unavailable!");
										document.getElementById("c-"+i+""+(j-1)).style.backgroundColor = "#f50057";
										unavailable++;
									}
								}
							}
							
						}

function update_seats_stats() {
										document.getElementById("available").innerHTML = "Available: " + available;
										document.getElementById("reserved").innerHTML  = "Reserved: "  + reserved;
										document.getElementById("unavailable").innerHTML = "Unavailable: " + unavailable;
										}	

function change_menu_item_log (){

	if (userid != "anonymous"){
								var cookieValue = document.cookie
															.split('; ')
															.find(row => row.startsWith(userid));
												
								document.getElementById("Log").innerHTML = "Log Out";
								}
	else{
			document.getElementById("Log").innerHTML = "Log In";
	}
}										

function update_seats_layout(){
										if (checkCookie() && sessionStorage.getItem("banner_display") == "disable"){
										reset_counters();
										db_update();
										update_seats_location();
										update_seats_stats();
										hide_banner_and_show_navigation();
										change_menu_item_log();
										}
																			
}

function reset_counters(){
	available = 0;
	reserved = 0;
	unavailable = 0;
}

/**************************
 *** Cookies Management *** 
 *************************/

// Asking the browser if cookies are enable?
function checkCookie(){
	return navigator.cookieEnabled;
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
				else if (message.includes("expired")){
					document.cookie ='message=; Path=/; Expires=Thu, 01 Jan 1970 00:00:01 GMT;';
					window.location.href = "https://localhost/Project2020/login.html";
				}else if (message.includes("wrong"))
					document.cookie ='message=; Path=/; Expires=Thu, 01 Jan 1970 00:00:01 GMT;';
				else if (message.includes("exists"))
                    document.cookie ='message=; Path=/; Expires=Thu, 01 Jan 1970 00:00:01 GMT;';
                else if (message.includes("connection"))
					document.cookie ='message=; Path=/; Expires=Thu, 01 Jan 1970 00:00:01 GMT;';
				else if (message.includes("password"))
					document.cookie ='message=; Path=/; Expires=Thu, 01 Jan 1970 00:00:01 GMT;';
				else if (message.includes("Welcome"))
					document.cookie ='message=; Path=/; Expires=Thu, 01 Jan 1970 00:00:01 GMT;';
				else if (message.includes("user"))
						document.cookie ='message=; Path=/; Expires=Thu, 01 Jan 1970 00:00:01 GMT;';
			message="";
				
		}
	}
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

function time_out (){
	document.cookie ='message=; Path=/; Expires=Thu, 01 Jan 1970 00:00:01 GMT;';
  	document.cookie ='userid=anonymous; Path=/; Expires=Thu, 01 Jan 1970 00:00:01 GMT;';
   	document.cookie ='email=; Path=/; Expires=Thu, 01 Jan 1970 00:00:01 GMT;';
	document.cookie ='banner_display=disable; Path=/; Expires=Thu, 01 Jan 1970 00:00:01 GMT;';
	document.cookie ='seats_for_db=; Path=/; Expires=Thu, 01 Jan 1970 00:00:01 GMT;';
   	window.location.replace("https://localhost/Project2020/login.html");
}

/****************************
 ****   Code Execution   **** 
 ****************************/

 
 if (!sessionStorage.getItem("banner_display"))
			sessionStorage.setItem("banner_display", "enable");


document.addEventListener('DOMContentLoaded', function() {
				read_cookies();
				update_seats_layout();
			 }, false);
















	





