package com.example.easybuy.Manager;

import android.content.Context;
import android.widget.Toast;

import com.example.easybuy.Database.ProductDAO;
import com.example.easybuy.Model.Product;

import java.util.List;

public class ProductManager {
    private final ProductDAO productDAO;
    private final Context context;

    public ProductManager(Context context) {
        this.context = context;
        this.productDAO = new ProductDAO(context);
    }

    public List<Product> getAllProducts() {
        return productDAO.getAllProducts();
    }

    // Thêm phương thức lấy sản phẩm theo adminId
    public List<Product> getProductsByAdmin(int adminId) {
        return productDAO.getProductsByAdmin(adminId);
    }

    // Cập nhật phương thức addProduct để nhận adminId
    public boolean addProduct(Product product, int adminId) {
        long productId = productDAO.addProduct(product, adminId); // Truyền adminId vào ProductDAO
        if (productId == -1) {
            Toast.makeText(context, "Lỗi khi thêm sản phẩm!", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    // Thêm sản phẩm kèm ảnh bổ sung
    public boolean addProductWithImages(Product product, List<String> imageUrls, int adminId) {
        // Thêm sản phẩm trước
        long productId = productDAO.addProduct(product, adminId);
        if (productId == -1) {
            Toast.makeText(context, "Lỗi khi thêm sản phẩm!", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Thêm ảnh bổ sung nếu có
        if (imageUrls != null && !imageUrls.isEmpty()) {
            for (String imageUrl : imageUrls) {
                if (imageUrl != null && !imageUrl.isEmpty()) {
                    long imageId = productDAO.addProductImage((int) productId, imageUrl);
                    if (imageId == -1) {
                        Toast.makeText(context, "Lỗi khi lưu ảnh bổ sung!", Toast.LENGTH_SHORT).show();
                        return false;
                    }
                }
            }
        }
        return true;
    }

    // Phương thức cũ để thêm ảnh bổ sung
    public boolean addProductImages(int productId, List<String> imageUrls, int adminId) {
        // Kiểm tra quyền sở hữu sản phẩm
        if (!productDAO.isProductOwner(productId, adminId)) {
            Toast.makeText(context, "Bạn không có quyền thêm ảnh cho sản phẩm này!", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (imageUrls == null || imageUrls.isEmpty()) {
            return true; // Không có ảnh bổ sung, coi như thành công
        }

        for (String imageUrl : imageUrls) {
            if (imageUrl != null && !imageUrl.isEmpty()) {
                long imageId = productDAO.addProductImage(productId, imageUrl);
                if (imageId == -1) {
                    Toast.makeText(context, "Lỗi khi lưu ảnh bổ sung!", Toast.LENGTH_SHORT).show();
                    return false;
                }
            }
        }
        return true;
    }
}