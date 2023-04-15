# SegSoft - Authenticator

## Requirements

- [Tomcat 9.0.73](https://tomcat.apache.org/)
- [Sqlite3 driver](https://github.com/xerial/sqlite-jdbc)
- [Google Guava](https://github.com/google/guava)

You must download the jar file corresponding to the specific requirement and place it
in the Tomcat's `lib` folder.

## How to run

- Place this repo inside the `webapps` folder of your tomcat installation
- Run `mvn compile`. This will place all necessary files in the `WEB-INF/classes` folder
- Start the tomcat server using `catalina.bat run` (Windows) or `catalina.sh run` (Mac/Linux)
- Open `http://localhost:8080/myApp/` in your browser

## Database setup

The database doesn't have any tables on the first run.
You must create them by using the file present in `sql/create-db.sql` file.
Use the IntelliJ builtin database tools to help with that.