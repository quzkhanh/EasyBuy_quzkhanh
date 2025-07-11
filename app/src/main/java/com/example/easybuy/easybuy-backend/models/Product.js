const mongoose = require('mongoose');

const productSchema = new mongoose.Schema({
  product_name: { type: String, required: true },
  price: { type: Number, required: true },
  image_url: { type: String, required: true },
  description: { type: String, required: true },
  created_by: { type: Number, required: true },
  additional_images: [String] // Nếu muốn lưu thêm ảnh phụ
}, { timestamps: true });

module.exports = mongoose.model('Product', productSchema);
