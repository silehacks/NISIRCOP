import Incident from '../models/Incident.js';
import Station from '../models/Station.js';

export async function createIncident(req, res, next) {
  try {
    const stationId = req.user?.id;
    const payload = { ...req.body, reportedBy: stationId };
    const incident = await Incident.create(payload);
    const populated = await incident.populate('reportedBy', 'name email sector');
    res.status(201).json(populated.toJSON());
  } catch (error) {
    next(error);
  }
}

export async function listIncidents(req, res, next) {
  try {
    const query = {};
    if (req.query.stationId) {
      query.reportedBy = req.query.stationId;
    }

    const incidents = await Incident.find(query)
      .populate('reportedBy', 'name email sector')
      .sort({ createdAt: -1 });

    res.json(incidents);
  } catch (error) {
    next(error);
  }
}

export async function getIncident(req, res, next) {
  try {
    const incident = await Incident.findById(req.params.id).populate('reportedBy', 'name email sector');
    if (!incident) {
      return res.status(404).json({ message: 'Incident not found' });
    }
    res.json(incident);
  } catch (error) {
    next(error);
  }
}

export async function assignIncident(req, res, next) {
  try {
    const { stationId } = req.body;
    const incident = await Incident.findById(req.params.id);
    if (!incident) {
      return res.status(404).json({ message: 'Incident not found' });
    }

    const station = await Station.findById(stationId);
    if (!station) {
      return res.status(404).json({ message: 'Station not found' });
    }

    incident.assignedTo = stationId;
    incident.status = 'assigned';
    await incident.save();

    const populated = await incident.populate([
      { path: 'reportedBy', select: 'name email sector' },
      { path: 'assignedTo', select: 'name email sector' }
    ]);

    res.json(populated);
  } catch (error) {
    next(error);
  }
}
