# passman-frontend
> Password Manager Frontend

This is the React-based frontend application for the password manager, developed for the Secure Programming + Web Application Security course.

#### Libraries and Tools

- **Node.js**: Runtime environment for React.
- **React-router-dom**: Handles page navigation.
- **Axios**: Simplifies HTTP requests to the backend.

## Requirements

- Node.js (v18 or later)
- npm or yarn
- Modern web browser

## Build and Run the Docker Container

Build the Docker image:
```
docker build -t passman-frontend .
```
Run the Docker Container:
```
docker run -d -p 80:3000 --name passman-frontend-container passman-frontend
```
Stop the container:
```
docker stop passman-frontend-container
```