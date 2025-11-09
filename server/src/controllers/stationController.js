import Station from '../models/Station.js';

export async function listStations(_req, res, next) {
  try {
    const stations = await Station.find().select('-password').sort({ name: 1 });
    res.json(stations);
  } catch (error) {
    next(error);
  }
}

export async function getStation(req, res, next) {
  try {
    const station = await Station.findById(req.params.id).select('-password');
    if (!station) {
      return res.status(404).json({ message: 'Station not found' });
    }
    res.json(station);
  } catch (error) {
    next(error);
  }
}
