const express = require('express');
const mongoose = require('mongoose');
const cors = require('cors');
require('dotenv').config(); // Load env variables

const app = express();
app.use(cors());
app.use(express.json());

const authRoutes = require('./routes/auth');
app.use('/auth', authRoutes);

const productRoutes = require('./routes/product');
app.use('/products', productRoutes);

const userRoutes = require('./routes/user');
app.use('/users', userRoutes);

// Default route
app.get('/', (req, res) => res.send('EasyBuy API is running 🚀'));

// MongoDB connection
mongoose.connect(process.env.MONGO_URI)
  .then(() => {
    console.log('✅ MongoDB Atlas connected');
    app.listen(process.env.PORT || 3000, () => {
      console.log(`🚀 Server started at http://localhost:${process.env.PORT || 3000}`);
    });
  })
  .catch(err => console.error('❌ MongoDB error:', err));
