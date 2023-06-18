# SegSoft - Authenticator

## Requirements

- [Tomcat 9.0.73](https://tomcat.apache.org/)
- [Sqlite3 driver](https://github.com/xerial/sqlite-jdbc)
- [Google Guava](https://github.com/google/guava)

You must download the jar file corresponding to the specific requirement and place it
in the Tomcat's `lib` folder.

## How to run

- Clone the project to the directory of your choice
- Run `mvn compile package`. This will create a `target` folder with the `.war` file.
- Copy the `.war` file to the Tomcat's `webapps` folder
- Start the tomcat server using `catalina.bat run` (Windows) or `catalina.sh run` (Mac/Linux)
- Open `http://localhost:8080/myApp/manage-users` in your browser
