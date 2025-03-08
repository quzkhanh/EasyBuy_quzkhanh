# 1. Tạo & Truy Cập Các Bảng Dữ Liệu
Ứng dụng sử dụng **SQLite** để lưu trữ dữ liệu **cục bộ** trên thiết bị ảo.  
⚠ **Lưu ý:** Không thể truy cập từ thiết bị khác.

## 📁 Cấu trúc thư mục Database:
- **Adapter**: Lấy dữ liệu từ database → Hiển thị danh sách trong `RecyclerView`.
- **DAO (Data Access Object)**: Chứa các hàm **thêm / sửa / xóa** dữ liệu trong SQLite.
- **DatabaseHelper**: Chứa các câu lệnh SQL thực thi các thao tác **CRUD**.

---

# 2. Các bước thực hiện
## 🛠 Cấu hình cơ sở dữ liệu
- `DatabaseHelper.java`:
    - Chịu trách nhiệm **tạo bảng** và **quản lý phiên bản cơ sở dữ liệu**.
    - Các câu lệnh SQL được lưu trong `DatabaseConstants.java`.

## 🏗 Thao tác dữ liệu
| Bảng | File DAO | File DatabaseHelper |
|------|---------|---------------------|
| `User`, `Admin` | `UserDAO.java`, `AdminDAO.java` | 

(Sử dụng trực tiếp trong DAO) |
| `Order`, `Product`, ... | `OrderDAO.java`, `ProductDAO.java`, ... 
| `OrderDatabaseHelper.java`, `ProductDatabaseHelper.java`, ... |

## 🔍 Nguyên tắc tổ chức
- **DAO chịu trách nhiệm** thực hiện các thao tác **Insert / Update / Delete**.
- **DatabaseHelper chỉ dùng để tạo bảng** và nâng cấp database.
- Thay vì viết trực tiếp SQL trong `DatabaseHelper`, các thao tác được tổ chức trong **DAO** để dễ bảo trì.

📌 **Ví dụ: Cách thêm sản phẩm vào SQLite**

```java
public void addProduct(Product product) {
    SQLiteDatabase db = this.getWritableDatabase();
    ContentValues values = new ContentValues();
    values.put("name", product.getName());
    values.put("price", product.getPrice());
    db.insert("Product", null, values);
    db.close();
}
