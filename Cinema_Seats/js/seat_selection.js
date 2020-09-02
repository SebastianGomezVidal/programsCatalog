// Global Variables
var status; 					// This variable tells the condition of a seat when querying the general database which holds
								// the seats status. 0 means free, 1 means purchased

var control_click = 0; 			// Variable for controling multiple click over the same/different button

var user_selection = [0];		// Seats selected by the user 

/*****************
***  Functions  **
*****************/

/*This function checks if the pages is reload or not */
function window_load_check () {
								if (window.performance) {
  								console.info("window.performance works fine on this browser");
								}
  								if (performance.navigation.type == 1) {
    							console.info( "This page is reloaded" );
  								} else {
    							console.info( "This page is not reloaded");
  								}	

							}

function pass_data(click_from){
								
								console.log(user_selection);				
								// Check if the array is in Zero
								if(user_selection[0]=="0")user_selection.shift();
								// Save the user picks in sessionstorage
								if (!sessionStorage.getItem("seats_for_db"))
									sessionStorage.setItem("seats_for_db", user_selection);
									
								if((click_from == "ticket_button" || click_from == "Log") && userid == "anonymous" ){
									
									sessionStorage.setItem("seats_for_db", user_selection);
									document.cookie = "seats_for_db="+user_selection+"; path=/";
									window.location.href = "https://localhost/Project2020/login.html";

								}else if (click_from == "Log" && userid != "anonymous"){ 

									window.location.href = "https://localhost/Project2020/logout.php";

								}else if ((click_from == "ticket_button")  && userid != "anonymous") {
										byPassLogin();
								}
								
							}
								
function byPassLogin(){
								$.ajax({	// Making a server request trough AJAX to know the status of the seat clicked on
										type: "POST",
										url: 'https://localhost/Project2020/php/seats_validation_bypass.php',
										async: false,
										data: {seats_for_db: user_selection},
										success:  function (result) {	alert(result);
																		message = result;
																	}
										})
										if(message.includes("Error")){
											time_out();
										}else update_seats_layout();
											// Restore user selection to zero
											user_selection = [0];
											message="";
}

function delete_seat_selection(position){

	if(user_selection[0]=="0")user_selection.shift();							
	console.log(user_selection);

								$.ajax({	// Making a server request trough AJAX to delete the chosen seat
									type: "POST",
									url: 'https://localhost/Project2020/php/delete_seat.php',
									async: false,
									data: {row: position.charAt(0), column: position.charAt(1)},
									success:  function (result) { 	alert(result);
																	message = result;
																}})
									if(message.includes("Error")) time_out();
									else update_seats_layout();
}

function update(clicked_id){

	if (checkCookie() && sessionStorage.getItem("banner_display") == "disable")
	{

		if(control_click == 0)	// Button debouncing
		{
			control_click = 1;
	
			$.ajax({	// Making a server request trough AJAX to know the status of the seat clicked on
    					type: "POST",
						url: 'php/seat_status.php',
						async: false,
						data: {row: clicked_id.charAt(0), column: clicked_id.charAt(1)},
						success:  function (result) { 		
												// Parsing php result into json
												status = JSON.parse(result);}
					})

			switch (status)
			{
				case "0":	
							var button = document.getElementById(clicked_id);
							var circle = document.getElementById("c-"+clicked_id);

							let rgb = window.getComputedStyle(button).backgroundColor;
							rgb = rgb.match(/\d+/g);

							let color = rgbToHex(parseInt(rgb[0]), parseInt(rgb[1]), parseInt(rgb[2]));
					
							if (color == "#00e676")
							{
								button.style.backgroundColor = "#ffff00";
								button.setAttribute("aria-label"," Your reserve!");
								circle.style.backgroundColor = "#ffff00";
								reserved+=1;
								available-=1;
								update_seats_stats();
								user_selection.push(clicked_id);
							}
							else
							{
								button.style.backgroundColor = "#00e676";
								button.setAttribute("aria-label","It is available!");
								circle.style.backgroundColor = "#00e676";
								reserved-=1;
								available+=1;
								update_seats_stats();
								user_selection = user_selection.filter(value => value !== clicked_id);
							}
							break;
			
				case userid:
								delete_seat_selection(clicked_id);
								reserved-=1;
								available+=1;
								break;

				default:
								update_seats_layout();
								alert("This seat is not available");
								break;
			}
	
		window_load_check ();
		control_click=0;
		}
	} else {
				banner_button(clicked_id);
			}
}

function rgbToHex(r, g, b) {
	return "#" + ((1 << 24) + (r << 16) + (g << 8) + b).toString(16).slice(1);
  }








