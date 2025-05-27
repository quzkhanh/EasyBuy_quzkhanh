package com.example.easybuy.viewmodel;

import android.content.Context;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.easybuy.database.dao.ProductDAO;
import com.example.easybuy.model.Product;
import java.util.ArrayList;
import java.util.List;

public class ProductViewModel extends ViewModel {
    private ProductDAO productDAO;
    private MutableLiveData<List<Product>> allProducts;
    private MutableLiveData<List<Product>> adminProducts;
    private MutableLiveData<Product> selectedProduct;
    private MutableLiveData<List<String>> additionalImages;
    private MutableLiveData<Boolean> operationSuccess;
    private List<Product> originalAdminProducts; // Lưu danh sách gốc để lọc

    public ProductViewModel() {
        // Khởi tạo các LiveData
        allProducts = new MutableLiveData<>();
        adminProducts = new MutableLiveData<>();
        selectedProduct = new MutableLiveData<>();
        additionalImages = new MutableLiveData<>();
        operationSuccess = new MutableLiveData<>();
    }

    // Khởi tạo ProductDAO với context
    public void init(Context context) {
        productDAO = new ProductDAO(context);
    }

    // Lấy tất cả sản phẩm
    public LiveData<List<Product>> getAllProducts() {
        loadAllProducts();
        return allProducts;
    }

    // Lấy sản phẩm theo admin
    public LiveData<List<Product>> getProductsByAdmin(int adminId) {
        loadProductsByAdmin(adminId);
        return adminProducts;
    }

    // Lấy chi tiết sản phẩm theo ID
    public LiveData<Product> getProductById(int productId) {
        loadProductById(productId);
        return selectedProduct;
    }

    // Lấy danh sách hình ảnh bổ sung của sản phẩm
    public LiveData<List<String>> getAdditionalImages(int productId) {
        loadAdditionalImages(productId);
        return additionalImages;
    }

    // Theo dõi trạng thái thao tác (thêm, cập nhật, xóa)
    public LiveData<Boolean> getOperationSuccess() {
        return operationSuccess;
    }

    // Lọc sản phẩm theo query
    public void filterProducts(String query, int adminId) {
        if (originalAdminProducts == null) {
            loadProductsByAdmin(adminId); // Đảm bảo danh sách gốc đã được tải
        }
        List<Product> filteredList = new ArrayList<>();
        for (Product product : originalAdminProducts) {
            if (product.getProductName().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(product);
            }
        }
        adminProducts.setValue(filteredList);
        
    }

    // Load tất cả sản phẩm
    private void loadAllProducts() {
        List<Product> products = productDAO.getAllProducts();
        allProducts.setValue(products);
    }

    // Load sản phẩm theo admin
    private void loadProductsByAdmin(int adminId) {
        List<Product> products = productDAO.getProductsByAdmin(adminId);
        originalAdminProducts = new ArrayList<>(products); // Lưu danh sách gốc
        adminProducts.setValue(products);
    }

    // Load chi tiết sản phẩm theo ID
    private void loadProductById(int productId) {
        Product product = productDAO.getProductById(productId);
        selectedProduct.setValue(product);
    }

    // Load danh sách hình ảnh bổ sung
    private void loadAdditionalImages(int productId) {
        List<String> images = productDAO.getAdditionalImages(productId);
        additionalImages.setValue(images);
    }

    // Thêm sản phẩm mới
    public void addProduct(String productName, double price, String imageUrl, String description, int adminId) {
        long result = productDAO.addProduct(productName, price, imageUrl, description, adminId);
        operationSuccess.setValue(result != -1);
        if (result != -1) {
            // Cập nhật lại danh sách sản phẩm sau khi thêm
            loadAllProducts();
            loadProductsByAdmin(adminId);
        }
    }

    // Thêm sản phẩm với hình ảnh bổ sung
    public void addProductWithImages(Product product, List<String> additionalImageUrls, int adminId) {
        long result = productDAO.addProductWithImages(product, additionalImageUrls, adminId);
        operationSuccess.setValue(result != -1);
        if (result != -1) {
            loadAllProducts();
            loadProductsByAdmin(adminId);
        }
    }

    // Cập nhật sản phẩm
    public void updateProduct(Product product, int adminId) {
        int rowsAffected = productDAO.updateProduct(product, adminId);
        operationSuccess.setValue(rowsAffected > 0);
        if (rowsAffected > 0) {
            loadAllProducts();
            loadProductsByAdmin(adminId);
        }
    }

    // Xóa sản phẩm
    public void deleteProduct(int productId, int adminId) {
        boolean success = productDAO.deleteProduct(productId, adminId);
        operationSuccess.setValue(success);
        if (success) {
            loadAllProducts();
            loadProductsByAdmin(adminId);
        }
    }
}