package xyz.illuminate.dlinks;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ConsumeParams;
import com.android.billingclient.api.ConsumeResponseListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.ProductDetails;
import com.android.billingclient.api.ProductDetailsResponseListener;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.QueryProductDetailsParams;
import com.android.billingclient.api.QueryPurchasesParams;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.progressindicator.LinearProgressIndicator;

import java.util.ArrayList;
import java.util.List;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import xyz.illuminate.dlinks.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    
    private ActivityMainBinding binding;
    BottomNavigationView bottomnav;
    String linkType;
    String convertedLink;

    private static final Pattern[] FORMAT_REGEXES = {
            Pattern.compile("https://drive\\.google\\.com/file/d/(?<id>.*?)/(?:edit|view)\\?usp=sharing"),
            Pattern.compile("https://drive\\.google\\.com/open\\?id=(?<id>.*)$")
    };
    private static final Pattern ALPHANUMERIC_REGEX = Pattern.compile("^[\\w-]+$");
    
    
    boolean readyToPurchase;
    
    String PRODUCT_ID = "PRODUCT1";
    String NO_ADS = "NO_ADS";
    
    private ArrayList<String> purchaseItemIDs = new ArrayList<String>() {{
        add(PRODUCT_ID);
        add(NO_ADS);
    }};
    
    BillingClient billingClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Inflate and get instance of binding
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        switchView("m");

        binding.toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.action_tg) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://t.me/team_illuminate")));
                } if (item.getItemId()==R.id.buyCoffe) {
                    GetSingleInAppDetail();
                } else if (item.getItemId() == R.id.rateapp) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=xyz.illuminate.dlinks")));
                }

                return false;
            }
        });

        binding.output.setMaxLines(2);
        binding.convert.setOnClickListener(v -> {
            if (!binding.link.getText().toString().contains("http") || binding.link.getText().toString().isEmpty()) {
                binding.linkroot.setErrorEnabled(true);
                binding.linkroot.setError("Invalid Link");
                return;
            }
            binding.linkroot.setErrorEnabled(false);
            if (linkType == "m") {
                if (!binding.link.getText().toString().contains("mediafire.com")) {
                    binding.linkroot.setErrorEnabled(true);
                    binding.linkroot.setError("Invalid Mediafire Link");
                    return;
                } else {
                    showLoading(true);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            convertMediafire(binding.link.getText().toString());
                        }
                    }).start();

                    new Handler().postDelayed(() -> {
                        binding.output.setText(convertedLink);
                        binding.outputroot.setEnabled(true);
                        showLoading(false);
                        Toast.makeText(MainActivity.this, "Link generated", Toast.LENGTH_SHORT).show();

                    }, 3000);
                }
            } else if (linkType == "g") {
                if (!binding.link.getText().toString().contains("drive.google.com")) {
                    binding.linkroot.setErrorEnabled(true);
                    binding.linkroot.setError("Invalid Google Drive Link");
                    return;
                } else {
                    binding.output.setText(driveLink(binding.link.getText().toString(), null));
                    Toast.makeText(MainActivity.this, "Link generated", Toast.LENGTH_SHORT).show();
                }
            } else if (linkType == "d") {
                if (!binding.link.getText().toString().contains("dl=0") || !binding.link.getText().toString().contains("dropbox.com")) {
                    binding.linkroot.setErrorEnabled(true);
                    binding.linkroot.setError("Invalid Dropbox Link");
                } else {
                    binding.output.setText(convertDropbox(binding.link.getText().toString()));
                    Toast.makeText(MainActivity.this, "Link generated", Toast.LENGTH_SHORT).show();
                }
            }
        });

        binding.copylink.setOnClickListener(v -> {
            if (!binding.output.getText().toString().isEmpty()) {
                ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clipData = ClipData.newPlainText("text", binding.output.getText().toString());
                clipboardManager.setPrimaryClip(clipData);
                Toast.makeText(MainActivity.this, "Link copied to clipboard", Toast.LENGTH_SHORT).show();
            }
        });

        binding.bottomnav.setOnNavigationItemSelectedListener(
                item -> {
                    int itemId = item.getItemId();
                    if (itemId == R.id.tab_mediafire) {
                        switchView("m");
                        return true;
                    } else if (itemId == R.id.tab_gdrive) {
                        switchView("g");
                        return true;
                    } else if (itemId == R.id.tab_dropbox) {
                        switchView("d");
                        return true;
                    } else {
                        return false;
                    }
                });
        
        
        billingClient = BillingClient.newBuilder(this)
                .enablePendingPurchases()
                .setListener(
                        (billingResult, list) -> {

                            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && list != null) {
                                for (Purchase purchase : list) {

                                    Log.d("Falcon", "Response is OK");
                                    handlePurchase(purchase);
                                }
                            } else {

                                Log.d("Falcon", "Response NOT OK");
                    
                            }
                        }
                ).build();

        //start the connection after initializing the billing client
        establishConnection();
        
    }
    
    void establishConnection() {
        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(@NonNull BillingResult billingResult) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {

                    // The BillingClient is ready. You can query purchases here.

                    //Use any of function below to get details upon successful connection

                    GetSingleInAppDetail();
                    //GetListsInAppDetail();

                    Log.d("Falcon", "Connection Established");
                }
            }

            @Override
            public void onBillingServiceDisconnected() {
                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.
                Log.d("Falcon", "Connection NOT Established");
                establishConnection();
            }
        });
    }
    
    void GetSingleInAppDetail() {
        ArrayList<QueryProductDetailsParams.Product> productList = new ArrayList<>();

        //Set your In App Product ID in setProductId()
        productList.add(
                QueryProductDetailsParams.Product.newBuilder()
                        .setProductId(PRODUCT_ID)
                        .setProductType(BillingClient.ProductType.INAPP)
                        .build()
        );

        QueryProductDetailsParams params = QueryProductDetailsParams.newBuilder()
                .setProductList(productList)
                .build();

        billingClient.queryProductDetailsAsync(params, new ProductDetailsResponseListener() {
            @Override
            public void onProductDetailsResponse(@NonNull BillingResult billingResult, @NonNull List<ProductDetails> list) {

                //Do Anything that you want with requested product details

                //Calling this function here so that once products are verified we can start the purchase behavior.
                //You can save this detail in separate variable or list to call them from any other location
                //Create another function if you want to call this in establish connections' success state
                LaunchPurchaseFlow(list.get(0));


            }
        });
    }

    void GetListsInAppDetail() {
        ArrayList<QueryProductDetailsParams.Product> productList = new ArrayList<>();

        //Set your In App Product ID in setProductId()
        for (String ids : purchaseItemIDs) {
            productList.add(
                    QueryProductDetailsParams.Product.newBuilder()
                            .setProductId(ids)
                            .setProductType(BillingClient.ProductType.INAPP)
                            .build());
        }

        QueryProductDetailsParams params = QueryProductDetailsParams.newBuilder()
                .setProductList(productList)
                .build();

        billingClient.queryProductDetailsAsync(params, new ProductDetailsResponseListener() {
            @Override
            public void onProductDetailsResponse(@NonNull BillingResult billingResult, @NonNull List<ProductDetails> list) {

                for (ProductDetails li : list) {
                    Log.d("Falcon", "IN APP item Price" + li.getOneTimePurchaseOfferDetails().getFormattedPrice());
                }
                //Do Anything that you want with requested product details
            }
        });
    }

    //This function will be called in handlepurchase() after success of any consumeable purchase
    void ConsumePurchase(Purchase purchase) {
        ConsumeParams params = ConsumeParams.newBuilder()
                .setPurchaseToken(purchase.getPurchaseToken())
                .build();
        billingClient.consumeAsync(params, new ConsumeResponseListener() {
            @Override
            public void onConsumeResponse(@NonNull BillingResult billingResult, @NonNull String s) {

                Log.d("TAG", "Consuming Successful: "+s);
                Toast.makeText(MainActivity.this,"Product Consumed",Toast.LENGTH_SHORT).show();
            }
        });
    }

    void LaunchPurchaseFlow(ProductDetails productDetails) {
        ArrayList<BillingFlowParams.ProductDetailsParams> productList = new ArrayList<>();

        productList.add(
                BillingFlowParams.ProductDetailsParams.newBuilder()
                        .setProductDetails(productDetails)
                        .build());

        BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder()
                .setProductDetailsParamsList(productList)
                .build();

        billingClient.launchBillingFlow(this, billingFlowParams);
    }

    void handlePurchase(Purchase purchases) {
        if (!purchases.isAcknowledged()) {
            billingClient.acknowledgePurchase(AcknowledgePurchaseParams
                    .newBuilder()
                    .setPurchaseToken(purchases.getPurchaseToken())
                    .build(), billingResult -> {

                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    for (String pur : purchases.getProducts()) {
                        if (pur.equalsIgnoreCase(PRODUCT_ID)) {
                            Log.d("Falcon", "Purchase is successful");
                            //tv_status.setText("Yay! Purchased");
                                Toast.makeText(MainActivity.this,"Yay! Purchased",Toast.LENGTH_SHORT).show();
                            //Calling Consume to consume the current purchase
                            // so user will be able to buy same product again
                            ConsumePurchase(purchases);
                        }
                    }
                }
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    void restorePurchases() {

        billingClient = BillingClient.newBuilder(this).enablePendingPurchases().setListener((billingResult, list) -> {
        }).build();
        final BillingClient finalBillingClient = billingClient;
        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingServiceDisconnected() {
            }

            @Override
            public void onBillingSetupFinished(@NonNull BillingResult billingResult) {

                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    finalBillingClient.queryPurchasesAsync(
                            QueryPurchasesParams.newBuilder().setProductType(BillingClient.ProductType.INAPP).build(), (billingResult1, list) -> {
                                if (billingResult1.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                                    if (list.size() > 0) {

                                        Log.d("TAG", "IN APP SUCCESS RESTORE: " + list);
                                        for (int i = 0; i < list.size(); i++) {

                                            if (list.get(i).getProducts().contains(PRODUCT_ID)) {
                                                //tv_status.setText("Premium Restored");
                                                Toast.makeText(MainActivity.this,"Premium Restored",Toast.LENGTH_SHORT).show();
                                                Log.d("TAG", "Product id "+PRODUCT_ID+" will restore here");
                                            }

                                        }
                                    } else {
                                        //tv_status.setText("Nothing found to Restored");
                                        Log.d("TAG", "In APP Not Found To Restore");
                                        Toast.makeText(MainActivity.this,"Nothing found to Restored",Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });
    }

    private void switchView(String type) {
        linkType = type;
        if (type.equals("m")) {
            binding.title.setText("Mediafire");
            binding.subtitle.setText("Convert mediafire link to direct downloadable link");
            binding.icon.setImageResource(R.drawable.mediafire_transparent);
        } else if (type.equals("g")) {
            binding.title.setText("Google Drive");
            binding.subtitle.setText("Convert google drive link to direct downloadable link");
            binding.icon.setImageResource(R.drawable.gdrive_transparent);
        } else if (type.equals("d")) {
            binding.title.setText("Dropbox");
            binding.subtitle.setText("Convert dropbox link to direct downloadable link");
            binding.icon.setImageResource(R.drawable.dropbox_transparent);
        }
    }

    private void convertMediafire(String link) {
        try {
            Document mfdl = Jsoup.connect(link).get();
            org.jsoup.select.Elements links = mfdl.select("a[href]");
            Element linnk = links.get(7);
            convertedLink = linnk.attr("abs:href");
            Log.wtf("convertMediafire", "convertedLink == " + convertedLink);
        } catch (Exception ex) {
            convertedLink = ex.getMessage();
            Log.wtf("convertMediafire", ex);
        }
    }

    private String convertDropbox(String link) {
        return link.replace("dl=0", "dl=1");
    }

    public String extractId(String urlOrId) {
        for (Pattern format : FORMAT_REGEXES) {
            Matcher matcher = format.matcher(urlOrId);
            if (matcher.find()) {
                return matcher.group("id");
            }
        }

        Matcher alphanumericMatcher = ALPHANUMERIC_REGEX.matcher(urlOrId);
        if (alphanumericMatcher.find()) {
            return urlOrId;
        }

        return "Invalid URL provided.";
    }

    public String driveLink(String urlOrId, String apiKey) {
        String id = extractId(urlOrId.trim());
        String parsedKey = apiKey != null ? apiKey.trim() : null;

        if (parsedKey != null && !ALPHANUMERIC_REGEX.matcher(parsedKey).matches()) {
            return "Invalid API key provided.";
        }

        if (parsedKey != null) {
            return "https://www.googleapis.com/drive/v3/files/" + id + "?alt=media&key=" + parsedKey;
        } else {
            return "https://drive.google.com/uc?export=download&id=" + id;
        }
    }

    private void showLoading(boolean show) {
        LinearProgressIndicator progress = binding.linearIndicator;
        progress.setIndeterminate(true);
        progress.setTrackThickness(4);
        progress.setMax(100);
        progress.setVisibility(show?View.VISIBLE:View.INVISIBLE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.binding = null;
    }
}
