import mongoose from 'mongoose';

const incidentSchema = new mongoose.Schema({
  title: { type: String, required: true },
  description: { type: String, required: true },
  type: {
    type: String,
    enum: ['theft', 'assault', 'traffic', 'emergency', 'other'],
    default: 'other'
  },
  severity: {
    type: String,
    enum: ['low', 'medium', 'high', 'critical'],
    default: 'medium'
  },
  status: {
    type: String,
    enum: ['new', 'assigned', 'in_progress', 'resolved'],
    default: 'new'
  },
  location: {
    type: {
      type: String,
      enum: ['Point'],
      default: 'Point'
    },
    coordinates: {
      type: [Number],
      required: true
    }
  },
  reportedBy: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'Station',
    required: true
  },
  assignedTo: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'Station'
  }
}, {
  timestamps: true
});

incidentSchema.index({ location: '2dsphere' });

const Incident = mongoose.models.Incident || mongoose.model('Incident', incidentSchema);

export default Incident;
