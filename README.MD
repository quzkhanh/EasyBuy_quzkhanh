# 🛒 EasyBuy - Android Shopping App

**EasyBuy** is a slick, lightweight shopping app crafted with **Java**, **XML**, and **SQLite**. It offers a seamless experience for both customers and admins, with offline support and blazing-fast performance. Your go-to app for shopping on the fly! ⚡

---

## 👥 Roles & Features

### 👤 User Features:
- 🔐 **Sign Up / Sign In**: Register or log in securely.
- 🛍️ **Browse Products**: Check out a curated product list.
- 🔍 **Product Details**: Get the full scoop before buying.
- ❤️ **Add to Favorites**: Save products for later.
- 🛒 **Place Orders**: Add to cart and checkout in a snap.
- 📦 **View Order History**: Track all your past orders.
- ✏️ **Edit Account Info**: Update your profile with ease.

### 🔧 Admin Features:
- 🔐 **Sign Up / Sign In**: Secure admin access.
- ➕ **Add / Edit / Delete Products**: Full control over the product catalog.
- 📦 **Order Management**: Monitor and manage all orders.
- 📊 **Sales Statistics**: Visualize sales with bar charts.
- 📈 **Revenue Analysis**: Analyze income with line and pie charts.
- ✏️ **Edit Account Info**: Tweak admin profile details.

---

## 📷 Demo Screenshots

<table>
  <tr>
    <td align="center" style="padding: 10px">
      <strong>Start Screen</strong><br>
      <img src="https://raw.githubusercontent.com/quzkhanh/EasyBuy_quzkhanh/master/img/start.png" width="300">
    </td>
    <td align="center" style="padding: 10px">
      <strong>Sign In</strong><br>
      <img src="https://raw.githubusercontent.com/quzkhanh/EasyBuy_quzkhanh/master/img/signIn.png" width="300">
    </td>
  </tr>
  <tr>
    <td align="center" style="padding: 10px">
      <strong>Product List</strong><br>
      <img src="https://raw.githubusercontent.com/quzkhanh/EasyBuy_quzkhanh/master/img/product.png" width="300">
    </td>
    <td align="center" style="padding: 10px">
      <strong>Account Page</strong><br>
      <img src="https://raw.githubusercontent.com/quzkhanh/EasyBuy_quzkhanh/master/img/account.png" width="300">
    </td>
  </tr>
  <tr>
    <td align="center" colspan="2" style="padding: 10px">
      <strong>Revenue Chart</strong><br>
      <img src="https://raw.githubusercontent.com/quzkhanh/EasyBuy_quzkhanh/master/img/roundChart.png" width="300">
    </td>
  </tr>
</table>

> **Note:** All demo images are in the `/img/` folder. Make sure they’re uploaded to your GitHub repo!

---

## 🧠 Tech Stack

- **Language**: Java
- **UI**: XML Layouts
- **Database**: SQLite (with custom DAO classes)
- **Architecture**: MVVM-inspired

---

## 📁 Project Structure

### Repo Structure
```
EasyBuy_quzkhanh/
├── .idea/         // IntelliJ IDEA config files
├── .vscode/       // VS Code config files
├── app/           // Main app source code
├── gradle/        // Gradle build system files
├── img/           // Demo images for README
│   ├── start.png
│   ├── signIn.png
│   ├── product.png
│   ├── account.png
│   ├── roundChart.png
│   └── ...
├── LICENSE        // MIT License file
└── README.md      // This file
```

### Code Structure
```
com.example.easybuy/
├── view/          // Activities and Fragments for UI
│   ├── MainActivity.java
│   ├── ProductListFragment.java
│   └── ...
├── viewmodel/     // ViewModels for UI logic and model interaction
│   ├── ProductViewModel.java
│   ├── OrderViewModel.java
│   └── ...
├── model/         // Data classes for core app entities
│   ├── User.java
│   ├── Product.java
│   ├── Order.java
│   └── ...
├── database/      // SQLite helpers and DAO classes
│   ├── DBHelper.java
│   ├── ProductDAO.java
│   └── ...
└── utils/         // Utility classes, dialogs, and reusable components
    ├── DialogUtils.java
    ├── Constants.java
    └── ...
```

---

## 🚀 Getting Started

1. Clone the repo:
   ```bash
   git clone https://github.com/quzkhanh/EasyBuy_quzkhanh.git
   ```

2. Open in **Android Studio**.
3. Build and run on an emulator or device (API 21+).

---

## 🛠️ Badges

[![Build Status](https://img.shields.io/badge/build-passing-brightgreen)](https://github.com/quzkhanh/EasyBuy_quzkhanh)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Java](https://img.shields.io/badge/language-Java-blue)](https://www.java.com/)
[![SQLite](https://img.shields.io/badge/database-SQLite-lightgrey)](https://www.sqlite.org/)
[![GitHub Stars](https://img.shields.io/github/stars/quzkhanh/EasyBuy_quzkhanh)](https://github.com/quzkhanh/EasyBuy_quzkhanh)

---

## 📩 Contact

Created by **QuzKhanh** – Got questions? Slide into my DMs! 😎  
📧 Email: quzkhanh.dev@gmail.com  
🌐 GitHub: [quzkhanh](https://github.com/quzkhanh)

---

## 🤝 Contributing

Wanna make EasyBuy even cooler? Fork the repo, submit a pull request, or drop an issue with your ideas! Let’s build something awesome together! 🚀

---

## 📜 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

*Last Updated: May 29, 2025*