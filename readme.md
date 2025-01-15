Front-end: https://github.com/Marten221/fileshare-react
# FileShareAPI

The FileShareAPI is a file-sharing service built to make sharing and managing files easier and more convenient. The API allows users to upload, download, and manage files, all while keeping track of important metadata such as file name, extension, and storage usage. It is designed to give you more control over your files by hosting your own service, rather than relying on third-party services with limitations.

## Features

- **User Authentication**  
  - Signup and login with JWT-based token authentication.  
  - Passwords are securely hashed.

- **File Management**  
  - Upload, update, and download files.  
  - Fetch file details.  
  - Find files with support for keyword search, sorting, file extension filtering, and pagination (customizable page size).

- **Account-Based Storage Tracking**  
  - View used and total memory space.

## Technology Stack

- **Backend:** Java Spring Framework  
- **Persistence:** JPA (Java Persistence API)  
- **Security:** Spring Security with JWT Bearer Token Authentication  

## API Endpoints

### Public Endpoints

- `POST /public/register` - Register a new user account.  
- `POST /public/login` - Authenticate and receive a JWT token.  
- `GET /public/download/{fileId}` - Download a file by its ID.  
- `GET /public/findfile` - Search for files with various filters.  
- `GET /public/filedescription/{fileId}` - Retrieve file details.  
- `GET /public/extensions` - Get a list of supported file extensions.

### Protected Endpoints (Require Bearer Token)

- `GET /diskspace` - View the storage usage for the authenticated user.  
- `GET /diskspace/{id}` - View storage usage for a specific user (if permitted).  
- `POST /upload` - Upload a new file.  
- `PUT /update` - Update an existing file.

## Security

- JWT (JSON Web Token) authentication is implemented for secure API access.
- Public endpoints also enforce access control checks to ensure users can only access resources they are authorized for; unauthorized access is denied. 
- Passwords are securely stored using hashing algorithms.  
- No role-based access control is implemented yet.

## Deployment Plan

The project is intended to be containerized using Docker and deployed on a personal server for private file sharing.

## Future Enhancements

- Role-based access control for advanced permission management.  
- Enhanced file sharing options (e.g., temporary download links, sharing with specific users).  

---


