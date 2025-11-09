import Conversation from '../models/Conversation.js';
import Station from '../models/Station.js';

export async function listConversations(req, res, next) {
  try {
    const filter = req.user?.role === 'station' ? { station: req.user.id } : {};
    const conversations = await Conversation.find(filter)
      .populate('station', 'name email sector')
      .sort({ updatedAt: -1 });

    res.json(conversations);
  } catch (error) {
    next(error);
  }
}

export async function addMessage(req, res, next) {
  try {
    const { conversationId } = req.params;
    const { text } = req.body;

    const conversation = await Conversation.findById(conversationId);
    if (!conversation) {
      return res.status(404).json({ message: 'Conversation not found' });
    }

    if (req.user?.role === 'station' && conversation.station.toString() !== req.user.id) {
      return res.status(403).json({ message: 'Not authorized to post in this conversation' });
    }

    conversation.messages.push({
      sender: req.user.id,
      senderRole: req.user.role,
      text
    });
    conversation.updatedAt = new Date();
    await conversation.save();

    const populated = await conversation.populate('station', 'name email sector');

    res.status(201).json(populated);
  } catch (error) {
    next(error);
  }
}

export async function startConversation(req, res, next) {
  try {
    if (req.user?.role !== 'admin') {
      return res.status(403).json({ message: 'Only admin can start conversations' });
    }

    const { stationId } = req.body;
    const station = await Station.findById(stationId);
    if (!station) {
      return res.status(404).json({ message: 'Station not found' });
    }

    const existing = await Conversation.findOne({ station: stationId });
    if (existing) {
      return res.json(await existing.populate('station', 'name email sector'));
    }

    const conversation = await Conversation.create({ station: stationId, messages: [] });
    const populated = await conversation.populate('station', 'name email sector');
    res.status(201).json(populated);
  } catch (error) {
    next(error);
  }
}
