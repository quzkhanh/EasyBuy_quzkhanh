const mongoose = require('mongoose');

const adminSchema = new mongoose.Schema({
  full_name: { type: String, required: true },
  email:      { type: String, required: true, unique: true },
  password:   { type: String, required: true },
  role:       { type: Number, default: 1 }
});

module.exports = mongoose.model('Admin', adminSchema);
