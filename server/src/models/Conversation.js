import mongoose from 'mongoose';

const messageSchema = new mongoose.Schema({
  sender: {
    type: mongoose.Schema.Types.ObjectId,
    required: true
  },
  senderRole: {
    type: String,
    enum: ['station', 'admin'],
    required: true
  },
  text: { type: String, required: true },
  createdAt: { type: Date, default: Date.now }
});

const conversationSchema = new mongoose.Schema({
  station: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'Station',
    required: true
  },
  messages: [messageSchema]
}, {
  timestamps: true
});

const Conversation = mongoose.models.Conversation || mongoose.model('Conversation', conversationSchema);

export default Conversation;
