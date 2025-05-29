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
    <td align="center">
      <strong>Start Screen</strong><br>
      <img src="https://raw.githubusercontent.com/quzkhanh/EasyBuy_quzkhanh/master/img/start.png" width="300">
    </td>
    <td align="center">
      <strong>Sign In</strong><br>
      <img src="https://raw.githubusercontent.com/quzkhanh/EasyBuy_quzkhanh/master/img/signIn.png" width="300">
    </td>
  </tr>
  <tr>
    <td align="center">
      <strong>Product List</strong><br>
      <img src="https://raw.githubusercontent.com/quzkhanh/EasyBuy_quzkhanh/master/img/product.png" width="300">
    </td>
    <td align="center">
      <strong>Account Page</strong><br>
      <img src="https://raw.githubusercontent.com/quzkhanh/EasyBuy_quzkhanh/master/img/account.png" width="300">
    </td>
  </tr>
  <tr>
    <td align="center" colspan="2">
      <strong>Revenue Chart</strong><br>
      <img src="https://raw.githubusercontent.com/quzkhanh/EasyBuy_quzkhanh/master/img/roundChart.png" width="300">
    </td>
  </tr>
</table>

> **Note:** All demo images are in the `/img/` folder. Make sure they’re uploaded to your GitHub repo!

> **Note:** All demo images are in the `/img/` folder. Make sure they’re uploaded to your GitHub repo!

> **Note:** All demo images are in the `/img/` folder. Make sure they’re uploaded to your GitHub repo!

> **Note:** All demo images are in the `/img/` folder. Make sure to upload them to your GitHub repo!

---

## 🧠 Tech Stack

- **Language**: Java
- **UI**: XML Layouts
- **Database**: SQLite (with custom DAO classes)
- **Architecture**: MVVM-inspired

---

## 📁 Project Structure

```
com.example.easybuy
├── view          // Activities and Fragments for UI
│   ├── MainActivity.java
│   ├── ProductListFragment.java
│   └── ...
├── viewmodel     // ViewModels for UI logic and model interaction
│   ├── ProductViewModel.java
│   ├── OrderViewModel.java
│   └── ...
├── model         // Data classes for core app entities
│   ├── User.java
│   ├── Product.java
│   ├── Order.java
│   └── ...
├── database      // SQLite helpers and DAO classes
│   ├── DBHelper.java
│   ├── ProductDAO.java
│   └── ...
└── utils         // Utility classes, dialogs, and reusable components
    ├── DialogUtils.java
    ├── Constants.java
    └── ...
```

---

## 🚀 Getting Started

1. Clone the repo:
   ```bash
   git clone https://github.com/your-username/easybuy.git
   ```

2. Open in **Android Studio**.
3. Build and run on an emulator or device (API 21+).

---

## 🛠️ Badges

[![Build Status](https://img.shields.io/badge/build-passing-brightgreen)](https://github.com/your-username/easybuy)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Java](https://img.shields.io/badge/language-Java-blue)](https://www.java.com/)
[![SQLite](https://img.shields.io/badge/database-SQLite-lightgrey)](https://www.sqlite.org/)
[![GitHub Stars](https://img.shields.io/github/stars/your-username/easybuy)](https://github.com/your-username/easybuy)

---

## 📩 Contact

Created by **[Your Name]** – Got questions? Slide into my DMs! 😎  
📧 Email: your.email@example.com  
🌐 GitHub: [your-username](https://github.com/your-username)

---

## 💡 Tips to Level Up Your README
- **Upload images**: Push the `/img/` folder to GitHub and verify image links.
- **Add a demo video**: Record a quick app demo and embed it as a YouTube link or GIF.
- **More badges**: Use [shields.io](https://shields.io/) for badges like version or contributors.
- **Invite contributors**: Add a "Contributing" section to welcome others. Example:
  ```markdown
  ## 🤝 Contributing
  Wanna make EasyBuy even dope? Fork the repo, submit a PR, or drop an issue with your ideas!
  ```
- **FAQ section**: Answer common questions like "Can the app run on older Android versions?"

---

## 📜 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
