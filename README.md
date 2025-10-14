Don’t Forget – Subscription Tracker App
App Video Link: https://youtube.com/shorts/x09M3K_RPnM?si=mMSMy4uSxr32xuNP

Overview
Don’t Forget is a native Android application built with Kotlin to help users efficiently track, manage, and receive reminders for recurring subscriptions such as Netflix, DStv, gym memberships, or software renewals.
The application ensures that users never miss a renewal or payment date by providing a secure login system, a structured dashboard, and automated reminders for upcoming billing events.
Key Features
1.	Login and Registration – Provides a secure user authentication system with session management.
2.	View Subscriptions – Displays all active subscriptions, including name, cost, and billing frequency.
3.	Add, Edit, and Delete Subscriptions – Enables users to manage their subscriptions within one interface.
4.	Smart Reminders – Sends notifications before billing dates to prevent missed payments.
5.	Dashboard Overview – Presents a summary of total monthly spending and upcoming payment dates.
Tech Stack
Category	Technology
Language	Kotlin
Platform	Android (Native)
Architecture	MVVM (recommended)
API	RESTful API
Local Storage	PostgreSQL





Getting Started

Prerequisites
Before running the project, ensure the following software and tools are installed:
1.	Android Studio (latest version recommended)
2.	An Android device or emulator with API Level 21 or higher
Run the Application
1.	Clone the project repository.
2.	Open the project in Android Studio.
3.	Allow Gradle to synchronize dependencies.
4.	Run the application on an emulator or physical device.


API Endpoints
Functionality	Method	Endpoint	Description
Register User	POST	/auth/register	Creates a new user account
Login User	POST	/auth/login	Authenticates a user and issues an access token
Get Subscriptions	GET	/subscriptions	Retrieves all subscriptions for a user
Add Subscription	POST	/subscriptions	Adds a new subscription
Delete Subscription	DELETE	/subscriptions/{id}	Deletes a subscription by its ID



Demonstrated Features
1.	Switching between Login and Register screens.
2.	Viewing example subscriptions such as Netflix, DStv, and Showmax.
3.	Adding new subscriptions using the “Add” button.
4.	Enabling reminders and notifications for upcoming billing dates.


Future Enhancements
1.	Implementation of monthly spending analytics and visual reports.
2.	Integration of push notifications for real-time alerts.
3.	Addition of dark mode for improved accessibility.
4.	Support for exporting subscription data in CSV or PDF formats.


Conclusion
Don’t Forget helps users maintain financial organization by simplifying subscription tracking and automating reminders.
Through secure authentication, intuitive design, and flexible management features, 
the application ensures users remain informed about their recurring payments and renewals while minimizing manual effort.

