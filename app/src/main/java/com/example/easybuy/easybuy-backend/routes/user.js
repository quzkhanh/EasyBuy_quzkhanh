const express = require('express');
const bcrypt = require('bcrypt');
const router = express.Router();
const User = require('../models/User');

// 📌 Đăng ký user
router.post('/register', async (req, res) => {
  const { full_name, email, password, phoneNumber } = req.body;

  try {
    const existingUser = await User.findOne({ email });
    if (existingUser) return res.status(400).json({ message: 'Email đã tồn tại!' });

    const hashedPassword = await bcrypt.hash(password, 10);
    const newUser = new User({
      full_name,
      email,
      password: hashedPassword,
      phoneNumber,
      role: 0
    });

    await newUser.save();

    res.status(201).json({ message: 'Đăng ký thành công', user: newUser });
  } catch (err) {
    console.error('❌ Register error:', err.message);
    res.status(500).json({ message: 'Lỗi server' });
  }
});

// 📌 Đăng nhập user
router.post('/login', async (req, res) => {
  const { email, password } = req.body;

  try {
    const user = await User.findOne({ email });
    if (!user) return res.status(400).json({ message: 'Email không tồn tại!' });

    const isMatch = await bcrypt.compare(password, user.password);
    if (!isMatch) return res.status(400).json({ message: 'Mật khẩu không đúng!' });

    res.status(200).json({ message: 'Đăng nhập thành công', user });
  } catch (err) {
    console.error('❌ Login error:', err.message);
    res.status(500).json({ message: 'Lỗi server' });
  }
});

module.exports = router;
