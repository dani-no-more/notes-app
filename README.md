# INTRODUCTION

This is a very simple notes app for **Android** written in **Kotlin** as programming language.


## DATA MODEL

Although a little bit ankward for a personal device, the app supports multiple _users_. Once started, the app will ask you to rather login or register. Users are _unique_ by their _email_ account.

An **user** can create, delete and modify any number of **notes**. A _**Note**_ consists just in a title and a body for writing free text. _Notes_ appear as a list, ordered by last modification date. _Notes_ have owners, so users can only access their own notes.

All users are stored on the internal **SQLite** database, using **Room** as persistence layer above that.


### USERS ROLES

There are two types of users:

* **TEAM MEMBER**: is the common user, accessing and managing its notes.

* **ADMIN**: this user is also capable of creating, deleting or modifying other users, even granting them _Admin_ privileges.


## USER GUIDE

We'll see now a breakdown for all the screens in the app. _**Single Activity**_ pattern was followed to develop the application.


### LOGIN

It's just a single form for input your **user** (_email_) and **password**. Fields cannot be _empty or blank_.

If you don't already have an account, besides the _Login button_ you can click on the _Register button_ to create a new one.


### REGISTER

Creates a new user by entering an **username**, a valid **email** (it really doesn't confirm that the account exists) and a **password**. None of the fields cannot be empty, email should has a valid format and password lenght must be at least 8 characters long.


### NOTES LIST

Here, the user can view all the notes created by him listed by last modification date in descending order.

Users can create new notes by clicking in the _Floating Button_ with the **plus** symbol (+).

By clicking in a note's title, the user can modified or delete a note.


### NOTE

The Note Fragment is reuse for all CRUD operations. To create a Note just give it a **title** and write whatever you want to annotate in the **body**. Either title and body _cannot be empty or blank_. When finished click on the _Floating Button_ with a  **pencil**  to save.

If in _Editing mode_ (the note already exists), user can modified both _title_ and _body_ and then saved the changes by clicking the _pencil button_. Also, a new _Floating Button_ appears allowing the user to **delete** the note.


## ACTION BAR

Android's **Action Bar** is supported to add a few extra options:

* **Profile**: lets you edit your user name and credentials anytime.
* **Logout**: ends your session and redirects to _Login screen_.
* **User Management**(Admin only): takes you to the _**user management**_ screen.


### USER MANAGEMENT

This is only accessible for _**Admin users**_ by selecting the special icon appearing in the **Action Bar**.

The main screen shows the complete list of users registered in the app. An icon next to the username indicates that user is also an administrator.

A new user can be added by clicking in the _Floating Button_ with the **plus** symbol (+). Also, any user can be edited by clicking in its name on the list. Both methods go to the same user register _Fragment_, but allowing this time to also select that user's **role**. Also, when editing, an additional _Floating Button_ appears allowing deletion of the user.

While in this screen, the _user management icon_ in the _Action Bar_ is replaced by a _note icon_, which allows going back to the notes by clicking it.

*__NOTE__:*
A _default admin user_ is created with credentials:
* **User:** _admin_
* **Password:** _adminadmin_

