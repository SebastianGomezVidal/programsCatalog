<?php
echo '<html>';
echo '<head>';
	echo '<noscript>';
    	echo '<style>';
        		echo 'body *{ /*hides all elements inside the body*/';
								echo 'display: none;';
							echo '}';
				echo '#main_header{';
					echo 'display: none;';
				echo '}';
                echo 'h1{ /* even if this h1 is inside head tags it will be first hidden, so we have to display it again after all body elements are hidden*/';
								echo 'display: block;';
								echo 'position: absolute;';
								echo 'top: 40%;';
								echo 'left: 20%;';
								echo 'padding: 0.5vw;';
								echo 'background: #311b92;';
								echo 'color: #ffc400;';
								echo 'border-radius: 15px;';
        echo '</style>';
        echo '<h1> JavaScript is not enabled, please check your browser settings. </h1>';
	echo '</noscript>';      	
							
echo'<title>welcom to theatry</title>';
	echo'<meta charset="UTF-8">';
	echo'<meta name="viewport" content="width=device-width, user-scalable=no, initial-scale=1, maximum-scale=1, minimum-scale=1">';
	echo'<script src="php/booking_status.php"></script>';
	echo'<script   src="https://code.jquery.com/jquery-3.4.1.min.js"   integrity="sha256-CSXorXvZcTkaix6Yvo6HppcZGetbYMGWSFlBw8HfCJo="   crossorigin="anonymous"></script>';
	echo'<link rel="stylesheet" href="css/main.css"/>';
	echo'<link rel="stylesheet" href="css/balloon.css"/>';
	echo '<script src="js/seat_selection.js"></script>';
	echo '<script src="js/seats_layout.js"></script>';
echo '</head>';

echo '<body style="background-image: url(img/cinema.png);">';

	echo '<div class="cookie-banner">';
		echo '<p>';
		echo '<b>Like most websites Theatry uses cookies</b>. In order to deliver a personalised, responsive service
		and to improve the site, we remeber and store information about how you use it. this is done using
		simple text files called cookies which sit on your computer. These cookies are completely safe and secure
		and will never contain any sensitive information. they are used only by Theatry or the trusted partners
		we work with.'; 
    	echo '<a href="https://www.termsfeed.com/blog/gdpr-compliance-plan/#Who_does_GDPR_apply_to/"> cookie policy </a>';
  		echo '</p>';
		echo '<button id="close" onclick="banner_button(this.id)">Accept & Close ></button>';
	echo '</div>';

	echo '<h1 id="main_header"><img src="img/theater64.png" alt="theater"/> Theatry &copy;</h1>';

	echo '<div class="row">';

				echo '<nav class="float_table_menu col-3" style="visibility:hidden">';
					echo '<ul style="list-style-type: none;">';
						echo '<img src="img/clapperboard.png" alt="Clapper board" style="margin: 0.5vw;">';
						echo '<li> <a href="index.php">Home</a></li>';
						echo '<li> <a id="Log" onclick="pass_data(this.id)">Login</a></li>';
			 			echo '<li> <a href="developer.html">Developer</a></li>';
						echo '<li> <a href="contact.html">Contact me</a></li>';
						echo '<button aria-label="Book!" data-balloon-pos="down" id="ticket_button" onclick="pass_data(this.id)"><img id="pick" src="img/ticket.png"></button>';
					echo '</ul>';
				echo '</nav>';
	  
	  			echo '<div class="center_div col-6">';
	  				$rows = 9;
					$columns = 6;
					$seats = $rows*$columns;
					$total = $seats;
					echo '<div class="seats-block">';
						for( $i = $rows-1; $i >= 0 ; $i--)
						{	
							echo '<div class="seats">'; 
							for( $j = $columns-1; $j >= 0 ; $j--)
							{
								echo '<div class="wrapper">'; 
								echo '<div class="circle" id="c-'."$i"."$j".'">'.$total.'</div>';
								echo '<button data-balloon-pos="down" class="button" id="'."$i"."$j".'" onclick="update(this.id)"><img src="img/cinema32.png"></button>';
								echo '</div>'; 
								$total -= 1;
							}
							echo '</div>';
						}
						echo '<div id="screen"> SCREEN </div>';
					echo '</div>';
				echo '</div>';

	  			echo '<div class="float_table_stats col-3" style="visibility:hidden">';
					echo '<img id="stat_image" src="img/popcorn.png" alt="popcorn" style="margin: 0.5vw;">';
					echo '<div id="stat_title">SEATS ('.$seats.')</div>';
					echo '<div id="available"></div>';
					echo '<div id="unavailable"></div>';                         
					echo '<div id="reserved"></div>';
					
				echo '</div>';
	echo '</div>';
	
	echo '<div style="	width: 100%;
						margin-top: 1.5vw;
						border: solid;
						font-family: everbright;
						font-size: 2vw;
						text-align: center;
						color: #ffffff;
						background:  #4a148c;
						border-radius: 10px;
						border: solid #000000;">© 2020 Juan Sebastian Gomez Vidal - Politecnico di Torino - Distributed Programming</div>';
echo '</body>';
echo '</html>';
?>