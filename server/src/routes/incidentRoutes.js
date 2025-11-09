import { Router } from 'express';
import { body } from 'express-validator';
import { createIncident, listIncidents, getIncident, assignIncident } from '../controllers/incidentController.js';
import { authenticate } from '../middleware/authenticate.js';
import { validateRequest } from '../middleware/validateRequest.js';

const router = Router();

router.use(authenticate());

router.get('/', listIncidents);
router.get('/:id', getIncident);

router.post('/', [
  body('title').notEmpty(),
  body('description').isLength({ min: 5 }),
  body('type').isString(),
  body('severity').isString(),
  body('location.coordinates').isArray({ min: 2 })
], validateRequest, createIncident);

router.post('/:id/assign', [
  body('stationId').notEmpty()
], validateRequest, assignIncident);

export default router;
