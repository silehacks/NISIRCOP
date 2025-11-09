import express from 'express';
import cors from 'cors';
import morgan from 'morgan';
import incidentRouter from './routes/incidentRoutes.js';
import stationRouter from './routes/stationRoutes.js';
import authRouter from './routes/authRoutes.js';
import chatRouter from './routes/chatRoutes.js';
import { errorHandler } from './middleware/errorHandler.js';
import { notFoundHandler } from './middleware/notFoundHandler.js';

const app = express();

app.use(cors());
app.use(express.json());
app.use(morgan('dev'));

app.get('/health', (_req, res) => {
  res.json({ status: 'ok', timestamp: Date.now() });
});

app.use('/api/auth', authRouter);
app.use('/api/incidents', incidentRouter);
app.use('/api/stations', stationRouter);
app.use('/api/chat', chatRouter);

app.use(notFoundHandler);
app.use(errorHandler);

export default app;
