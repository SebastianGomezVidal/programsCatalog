# Prologue
This web page was fully developed with XAMPP, PHP, JAVASCRIPT, AJAX, HTML and CSS. For a thorough understanding about what this app could do please refer to Project.md were all requirements are detailed. 

## Things that you might need

In order to run this page, you would need:

1. ***XAMPP*** Which is a completely free, easy to install Apache distribution containing MariaDB, PHP, and Perl.
This would allow you to simulate a server/client architecture in your computer. For further information refer to [xampp](https://www.apachefriends.org/index.html).

2. Clone the Web Programming folder in XAMPP/htdocs/

3. Start all XAMPP services (SQL, APACHE, PROFTPD).

4. go to [https://localhost/phpmyadmin/](https://localhost/phpmyadmin/) and create a DB (..like theater_manager).

5. In your new DB go to the SQL tab and paste the content (plain txt) of the file theater_manager.sql which you can
take with any plain text reader, then click go.

6. Make sure the connection parameters to the DB are right by checking the file _db_connection.php_ inside the php folder.

7. Finally go to [https://localhost/index.php](https://localhost/index.php), this app works under https.

8. Enjoy.


## What should the app do?

![Cinema Booking Finder Demo](Demo/cinema_booking_demo.gif)

![Cinema Booking Finder Demo](Demo/cinema_booking_demo2.gif)

![Cinema Booking DB](Demo/cinema_booking_db.gif)

## Further Work

- Adapt the contact section according to your requirements, for gmail accounts extra code is needed.

- In order to stop a same user to login multiple times, modify in sql the users table with a column when you could
store a boolean or something similar that point-out whenever a user is logged in and modify the code in the
login.php and sign_up.php, respectively.


