# 🛒 EasyBuy - Ecommerce Android App

EasyBuy là một ứng dụng thương mại điện tử được phát triển bằng Java trên nền tảng Android. Ứng dụng chia thành hai vai trò: người dùng (User) và quản trị viên (Admin), hỗ trợ các chức năng cơ bản như đăng nhập, quản lý sản phẩm, đặt hàng, và phân tích doanh thu qua biểu đồ. Dự án được xây dựng theo kiến trúc **MVVM** để tách biệt giao diện người dùng với logic xử lý và dữ liệu.

## 🚀 Chức năng chính

### 🎯 Người dùng (User)
- Đăng ký, đăng nhập
- Xem danh sách sản phẩm
- Chi tiết sản phẩm
- Thêm vào yêu thích
- Đặt hàng và xem lịch sử đơn hàng

### 🔧 Quản trị viên (Admin)
- Đăng nhập, đăng ký
- Thêm / sửa / xóa sản phẩm
- Quản lý đơn hàng
- Thống kê đơn hàng theo biểu đồ
- Phân tích doanh thu bằng biểu đồ đường, biểu đồ tròn

## 🧱 Kiến trúc phần mềm

Dự án sử dụng mô hình **MVVM** (Model - View - ViewModel), giúp tổ chức mã rõ ràng và dễ mở rộng.

```plaintext
com.example.easybuy
├── view          // Các Activity, Fragment cho UI
├── viewmodel     // Logic xử lý và kết nối UI với Model
├── model         // Các lớp dữ liệu (Product, User, Order, ...)
├── database      // Các lớp DAO và Helper cho SQLite
└── utils         // Các class tiện ích và dialog tùy chỉnh
