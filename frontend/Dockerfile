# Multi-stage build for Angular frontend

# Stage 1: Build and Test
FROM node:20-alpine AS build

WORKDIR /app

# Copy package files
COPY package*.json ./

# Install dependencies
RUN npm i

# Copy source code
COPY . .

# Run tests (you can add ng test here if configured)
# RUN npm run test -- --watch=false --browsers=ChromeHeadless

# Build the application for production
RUN npm run build

# Stage 2: Serve with Nginx
FROM nginx:alpine



# Copy built application from build stage
COPY --from=build /app/dist/frontend/browser /usr/share/nginx/html

# Expose port
EXPOSE 80

# Start nginx
CMD ["nginx", "-g", "daemon off;"]
