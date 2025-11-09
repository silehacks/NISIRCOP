import mongoose from 'mongoose';

const adminSchema = new mongoose.Schema({
  name: { type: String, required: true },
  email: { type: String, required: true, unique: true, lowercase: true },
  password: { type: String, required: true },
  role: { type: String, default: 'admin' }
}, {
  timestamps: true
});

adminSchema.methods.toPublicJSON = function toPublicJSON() {
  return {
    id: this.id,
    name: this.name,
    email: this.email,
    role: this.role
  };
};

const Admin = mongoose.models.Admin || mongoose.model('Admin', adminSchema);

export default Admin;
