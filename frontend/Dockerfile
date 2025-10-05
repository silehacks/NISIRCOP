# Stage 1: Build the Vue.js application
FROM node:20-slim as build

WORKDIR /app

# Copy package.json and package-lock.json (if available)
COPY package*.json ./

# Install dependencies
RUN npm install

# Copy the rest of the application source code
COPY . .

# Build the application
RUN npm run build

# Stage 2: Serve the application using Nginx
FROM nginx:stable-alpine

# Copy the built assets from the build stage
COPY --from=build /app/dist /usr/share/nginx/html

# Expose port 3000
EXPOSE 3000

# The default nginx command will be used to start the server
CMD ["nginx", "-g", "daemon off;"]