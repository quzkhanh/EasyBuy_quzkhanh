const express = require('express');
const bcrypt = require('bcrypt');
const router = express.Router();
const Admin = require('../models/Admin');

// ✅ Đăng ký Admin
router.post('/register', async (req, res) => {
  const { full_name, email, password } = req.body;
  try {
    // Kiểm tra email đã tồn tại
    const existing = await Admin.findOne({ email });
    if (existing) return res.status(400).json({ error: 'Email đã tồn tại' });

    // Hash mật khẩu
    const hashedPassword = await bcrypt.hash(password, 10);
    const newAdmin = new Admin({ full_name, email, password: hashedPassword });

    await newAdmin.save();
    res.status(201).json({ message: 'Đăng ký thành công' });
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});

// ✅ Đăng nhập
router.post('/login', async (req, res) => {
  const { email, password } = req.body;
  try {
    const admin = await Admin.findOne({ email });
    if (!admin) return res.status(400).json({ error: 'Tài khoản không tồn tại' });

    const isMatch = await bcrypt.compare(password, admin.password);
    if (!isMatch) return res.status(400).json({ error: 'Sai mật khẩu' });

    res.json({
      message: 'Đăng nhập thành công',
      admin: {
        id: admin._id,
        full_name: admin.full_name,
        email: admin.email,
        role: admin.role
      }
    });
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});

// ✅ Lấy thông tin Admin theo email
router.get('/admin/:email', async (req, res) => {
  try {
    const admin = await Admin.findOne({ email: req.params.email });
    if (!admin) return res.status(404).json({ error: 'Không tìm thấy admin' });

    res.json({
      id: admin._id,
      full_name: admin.full_name,
      email: admin.email,
      role: admin.role
    });
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});

module.exports = router;
