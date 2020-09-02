{\rtf1\ansi\ansicpg1252\cocoartf2513
\cocoatextscaling0\cocoaplatform0{\fonttbl\f0\froman\fcharset0 TimesNewRomanPSMT;\f1\froman\fcharset0 Times-Roman;}
{\colortbl;\red255\green255\blue255;\red0\green0\blue0;}
{\*\expandedcolortbl;;\cssrgb\c0\c0\c0;}
{\*\listtable{\list\listtemplateid1\listhybrid{\listlevel\levelnfc0\levelnfcn0\leveljc0\leveljcn0\levelfollow0\levelstartat1\levelspace360\levelindent0{\*\levelmarker \{decimal\}.}{\leveltext\leveltemplateid1\'02\'00.;}{\levelnumbers\'01;}\fi-360\li720\lin720 }{\listname ;}\listid1}
{\list\listtemplateid2\listhybrid{\listlevel\levelnfc0\levelnfcn0\leveljc0\leveljcn0\levelfollow0\levelstartat7\levelspace360\levelindent0{\*\levelmarker \{decimal\}.}{\leveltext\leveltemplateid101\'02\'00.;}{\levelnumbers\'01;}\fi-360\li720\lin720 }{\listname ;}\listid2}}
{\*\listoverridetable{\listoverride\listid1\listoverridecount0\ls1}{\listoverride\listid2\listoverridecount0\ls2}}
\margl1440\margr1440\vieww10800\viewh8400\viewkind0
\deftab720
\pard\pardeftab720\sa240\partightenfactor0

\f0\fs26\fsmilli13333 \cf2 \expnd0\expndtw0\kerning0
Build a simplified version of a website to manage seat reservations for a theatre show. For the sake of simplicity, consider only the reservations for a single show. The audience room of the theatre has a rectangular shape of dimensions 9x6 seats. These dimensions must be easily configurable by setting two PHP variables. The web site must satisfy the following requirements: 
\f1\fs24 \
\pard\tx220\tx720\pardeftab720\li720\fi-720\sa266\partightenfactor0
\ls1\ilvl0
\f0\fs26\fsmilli13333 \cf2 \kerning1\expnd0\expndtw0 {\listtext	1.	}\expnd0\expndtw0\kerning0
In the main page of the web site the theatre map is shown to any visitor of the website, without authentication. In the map, different colors are used for the reserved seats (red) and the free ones (green). The user shall be able to select one or more free seats (green) by clicking on them. At each selection click, without reloading the page, the application must send a request to the server in order to check that the seat has not been reserved by someone else in the meanwhile. If the selected seat is still free, it must become yellow, which means selected, otherwise it must become red. It must be possible to de-select the selected seats, bringing them back to the green color (i.e. to the free status), with another click. The page must also show the total number of the seats, the number of those already reserved, the ones that are free and those currently selected by the user. These numbers must be updated consistently at each click of the user. \uc0\u8232 \
\ls1\ilvl0\kerning1\expnd0\expndtw0 {\listtext	2.	}\expnd0\expndtw0\kerning0
The reservation operation must be enabled only through registration or authentication. This means that when a user presses the "Reserve" button in the main page, after having selected some seats, the server must be contacted in order to take the user to another page where the user can register, or authenticate with a previously registered email and password. The email is used as username. If registration or authentication is completed successfully, the operation must continue, attempting to carry out the reservation of the seats previously selected in the main page, taking the user back to the main page, both in case of success or failure, with the updated state of the seats and a message of confirmation or negation. The seats reserved by the user who is currently interacting with the web site are displayed with the orange color. From the main page it must be possible to reach the page of registration/authentication directly through a button, even without any attempt to reserve seats, in order to register/authenticate, if necessary, and visualize the reservations made previously, if any. \uc0\u8232 \
\ls1\ilvl0\kerning1\expnd0\expndtw0 {\listtext	3.	}\expnd0\expndtw0\kerning0
Once registered or authenticated, a user must be able to return to the main page and continue to make additional reservations with the procedure described above, without having to register/authenticate again. The user can also decide to cancel a reservation by clicking on the proper orange seat (which becomes green). The server must be contacted, at each cancellation click, without reloading the page, in order to effectively the status of the seat in the server DB. \uc0\u8232 \
\ls1\ilvl0\kerning1\expnd0\expndtw0 {\listtext	4.	}\expnd0\expndtw0\kerning0
In the project to be submitted, two users must already exist, u1@p.it u2@p.it, with password p1, p2, who have reserved, respectively, 9 and 6 seats, with no adjacency between seats of the two users. \uc0\u8232 \
\ls1\ilvl0\kerning1\expnd0\expndtw0 {\listtext	5.	}\expnd0\expndtw0\kerning0
Authentication or registration by username and password must remain valid if no more than two minutes of user inactivity elapse. If the user requests an operation that requires registration/authentication after the deadline of 2 minutes since the previous page load, the operation does not have effect and the user is forced to re-authenticate with username and password. HTTPS is mandatory for the registration/authentication and in every part of the web site containing information related to a registered/authenitcated user only. \uc0\u8232 \
\ls1\ilvl0\kerning1\expnd0\expndtw0 {\listtext	6.	}\expnd0\expndtw0\kerning0
The general layout of the web pages must contain: an header in the upper part, a navigation bar on the left side with links or buttons to carry out the possible operations and a central part which is used for the main operation. \uc0\u8232 \
\pard\tx220\tx720\pardeftab720\li720\fi-720\sa266\partightenfactor0
\ls2\ilvl0\cf2 \kerning1\expnd0\expndtw0 {\listtext	7.	}\expnd0\expndtw0\kerning0
Cookies and Javascript must be enabled, otherwise the website may not work properly (in that case, for what concerns cookies, the user must be alerted and the website navigation must be forbidden, for what concerns Javascript the user must be informed). Forms should be provided with small informational messages in order to explain the meaning of the different fields. These messages may be put within the fields themselves or may appear when the mouse pointer is over them. \uc0\u8232 \
\ls2\ilvl0\kerning1\expnd0\expndtw0 {\listtext	8.	}\expnd0\expndtw0\kerning0
The more uniform the views and the layouts are by varying the adopted browser, the better. \uc0\u8232 \
}