import { Router } from 'express';
import { listStations, getStation } from '../controllers/stationController.js';
import { authenticate } from '../middleware/authenticate.js';

const router = Router();

router.use(authenticate());

router.get('/', listStations);
router.get('/:id', getStation);

export default router;
