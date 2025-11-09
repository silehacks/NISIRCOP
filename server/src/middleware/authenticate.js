import jwt from 'jsonwebtoken';
import { loadEnv } from '../config/loadEnv.js';

const { JWT_SECRET } = loadEnv();

export function authenticate(required = true) {
  return (req, res, next) => {
    const token = req.headers.authorization?.split(' ')[1];

    if (!token) {
      if (required) {
        return res.status(401).json({ message: 'Authentication required' });
      }
      return next();
    }

    try {
      req.user = jwt.verify(token, JWT_SECRET);
      next();
    } catch (error) {
      if (required) {
        return res.status(401).json({ message: 'Invalid or expired token' });
      }
      next();
    }
  };
}
