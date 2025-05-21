# Engine Part User Purchase Portal

This project is a web application designed to manage the purchase of engine parts. It provides two different interfaces: one for users and another for admins.

- **User Portal**: Allows users to view products, purchase items, and track orders.  
- **Admin Portal**: Allows admins to manage orders, add engine parts, and update product listings.

## Accessing the Application

When the application is started, a local server runs at: 
http://localhost:8080/

- By default, it redirects to the **User Portal**.
- To access the **Admin Portal**, you must manually visit:
http://localhost:8080/demosqlite2/admin/login


## Database

- The application uses a local database stored on the **C drive**.
- The database path is:
C:\Sqllite3_db\ecommerce.db
- The application automatically creates the `Sqllite3_db` folder on your C drive if it does not already exist.
