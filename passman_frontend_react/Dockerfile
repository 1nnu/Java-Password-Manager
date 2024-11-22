# Use Node.js base image
FROM node:16

# Set the working directory in the container
WORKDIR /app

# Copy package.json and package-lock.json (or yarn.lock)
COPY package*.json ./

# Install dependencies
RUN npm install

# Copy the rest of the app's source code to the container
COPY . .

# Expose port 3000 internally for the development server
EXPOSE 3000

# Run the development server on port 3000
CMD ["npm", "start"]