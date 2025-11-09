import { Router } from 'express';
import { body } from 'express-validator';
import { listConversations, addMessage, startConversation } from '../controllers/chatController.js';
import { authenticate } from '../middleware/authenticate.js';
import { validateRequest } from '../middleware/validateRequest.js';

const router = Router();

router.use(authenticate());

router.get('/', listConversations);
router.post('/', [
  body('stationId').notEmpty()
], validateRequest, startConversation);
router.post('/:conversationId/messages', [
  body('text').isLength({ min: 1 })
], validateRequest, addMessage);

export default router;
