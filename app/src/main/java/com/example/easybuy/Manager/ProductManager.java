    package com.example.easybuy.Manager;

    import android.content.Context;
    import android.widget.Toast;

    import com.example.easybuy.Database.ProductDAO;
    import com.example.easybuy.Model.Product;

    import java.util.List;

    public class ProductManager {
        private final ProductDAO productDAO;

        public ProductManager(Context context) {
            this.productDAO = new ProductDAO(context);
        }

        public List<Product> getAllProducts() {
            return productDAO.getAllProducts();
        }

        public boolean addProduct(Product product) {
            long productId = productDAO.addProduct(product);
            return productId != -1;
        }

        // Thêm phương thức để lưu danh sách ảnh bổ sung
        public boolean addProductImages(int productId, List<String> imageUrls) {
            if (imageUrls == null || imageUrls.isEmpty()) {
                return true; // Không có ảnh bổ sung, coi như thành công
            }
            for (String imageUrl : imageUrls) {
                if (imageUrl != null && !imageUrl.isEmpty()) {
                    long imageId = productDAO.addProductImage(productId, imageUrl);
                    if (imageId == -1) {
                        Toast.makeText(productDAO.getContext(), "Lỗi khi lưu ảnh bổ sung!", Toast.LENGTH_SHORT).show();
                        return false;
                    }
                }
            }
            return true;
        }
    }