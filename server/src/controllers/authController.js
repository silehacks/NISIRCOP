import jwt from 'jsonwebtoken';
import bcrypt from 'bcryptjs';
import { loadEnv } from '../config/loadEnv.js';
import Station from '../models/Station.js';
import Admin from '../models/Admin.js';

const { JWT_SECRET } = loadEnv();

function signToken(payload) {
  return jwt.sign(payload, JWT_SECRET, { expiresIn: '12h' });
}

export async function registerStation(req, res, next) {
  try {
    const { name, email, password, sector } = req.body;

    const existing = await Station.findOne({ email });
    if (existing) {
      return res.status(409).json({ message: 'Station already registered' });
    }

    const hashedPassword = await bcrypt.hash(password, 10);
    const station = await Station.create({ name, email, password: hashedPassword, sector });

    res.status(201).json({
      station: station.toPublicJSON(),
      token: signToken({ id: station.id, role: 'station' })
    });
  } catch (error) {
    next(error);
  }
}

export async function login(req, res, next) {
  try {
    const { email, password } = req.body;

    const station = await Station.findOne({ email });
    const admin = station ? null : await Admin.findOne({ email });
    const user = station || admin;

    if (!user) {
      return res.status(401).json({ message: 'Invalid credentials' });
    }

    const matches = await bcrypt.compare(password, user.password);
    if (!matches) {
      return res.status(401).json({ message: 'Invalid credentials' });
    }

    const role = user.constructor.modelName === 'Station' ? 'station' : 'admin';
    const token = signToken({ id: user.id, role });

    res.json({
      token,
      role,
      user: user.toPublicJSON()
    });
  } catch (error) {
    next(error);
  }
}
