package com.example.easybuy.viewmodel;

import android.content.Context;
import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.easybuy.database.dao.ProductDAO;
import com.example.easybuy.model.Product;
import com.example.easybuy.network.ApiClient;
import com.example.easybuy.network.ProductApi;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ProductViewModel extends ViewModel {
    private ProductApi productApi;
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
        productDAO = new ProductDAO(context); // vẫn dùng local tạm
        productApi = ApiClient.getClient().create(ProductApi.class);
    }

    //    Thêm phương thức này để gọi API thêm sản phẩm:
    public void addProductToServer(Product product) {
        ProductApi api = ApiClient.getClient().create(ProductApi.class);
        api.addProduct(product).enqueue(new Callback<Product>() {
            @Override
            public void onResponse(Call<Product> call, Response<Product> response) {
                operationSuccess.postValue(response.isSuccessful());
            }

            @Override
            public void onFailure(Call<Product> call, Throwable t) {
                operationSuccess.postValue(false);
            }
        });
    }

    private void fetchAllProductsFromServer() {
        productApi.getAllProducts().enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Product> products = response.body();
                    allProducts.postValue(products);
                    originalAdminProducts = new ArrayList<>(products); // phục vụ tìm kiếm
                } else {
                    Log.e("ProductViewModel", "Lỗi phản hồi sản phẩm: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                Log.e("ProductViewModel", "Lỗi tải sản phẩm: " + t.getMessage());
            }
        });
    }

    // Lấy tất cả sản phẩm
    public LiveData<List<Product>> getAllProductsFromServer() {
        MutableLiveData<List<Product>> live = new MutableLiveData<>();

        productApi.getAllProducts().enqueue(new retrofit2.Callback<List<Product>>() {
            @Override public void onResponse(Call<List<Product>> c, Response<List<Product>> r) {
                if (r.isSuccessful() && r.body()!=null) {
                    live.postValue(r.body());
                    originalAdminProducts = new ArrayList<>(r.body());  // phục vụ search
                } else {
                    live.postValue(new ArrayList<>());
                }
            }
            @Override public void onFailure(Call<List<Product>> c, Throwable t) {
                live.postValue(new ArrayList<>());
            }
        });
        return live;
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
        fetchAllProductsFromServer();
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