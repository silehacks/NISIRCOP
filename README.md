# NISIRCOP

Modern policing coordination platform built with the MERN stack. This repository contains both the backend API and the frontend web client that deliver real-time awareness, streamlined reporting, and command center tooling for Addis Ababa police operations.

## Project structure

```
.
├── Instructions.md
├── README.md
├── client/           # React (Vite) single-page application for stations and admin
└── server/           # Express.js API with MongoDB models and Socket.IO integration
```

## Getting started

### Prerequisites

- Node.js 18+
- MongoDB instance (local or cloud)

### Backend

```bash
cd server
npm install
npm run dev
```

Environment variables can be configured in a `.env` file:

```
PORT=4000
MONGO_URI=mongodb://localhost:27017/nisircop
JWT_SECRET=super-secret-key
CLIENT_URL=http://localhost:5173
```

### Frontend

```bash
cd client
npm install
npm run dev
```

Vite will start the development server at `http://localhost:5173`.

## Next steps

- Connect API endpoints to persistent data in MongoDB.
- Implement authentication flows on the client.
- Replace mocked dashboard data with live analytics and mapping visualizations.
- Introduce automated testing and linting workflows.
