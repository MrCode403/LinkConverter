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
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import xyz.illuminate.dlinks.databinding.ActivityMainBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;

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
                } else if (item.getItemId() == R.id.rateapp) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=xyz.illuminate.dlinks")));
                }

                return false;
            }
        });
        
        binding.output.setMaxLines(2);
        binding.convert.setOnClickListener(v->{
                if(!binding.link.getText().toString().contains("http")|| binding.link.getText().toString().isEmpty()) {
                	binding.linkroot.setErrorEnabled(true);
                    binding.linkroot.setError("Invalid Link");
                    return;
                }
                binding.linkroot.setErrorEnabled(false);
                if(linkType=="m") {
                    if(!binding.link.getText().toString().contains("mediafire.com")) {
                    	binding.linkroot.setErrorEnabled(true);
                    binding.linkroot.setError("Invalid Mediafire Link");
                        return;
                    }else {
                    showLoading(true);
                    new Thread(new Runnable() {
                @Override
                public void run() {
                    convertMediafire(binding.link.getText().toString());
                }
            }).start();
                    
                    new Handler().postDelayed(()->{
                            binding.output.setText(convertedLink);
                            binding.outputroot.setEnabled(true);
                            showLoading(false);
                            Toast.makeText(this,"Link generated",Toast.LENGTH_SHORT).show();
                        
                    },3000);
                        }
                }else if(linkType=="g"){
                    if(!binding.link.getText().toString().contains("drive.google.com")) {
                    	binding.linkroot.setErrorEnabled(true);
                    binding.linkroot.setError("Invalid Google Drive Link");
                        return;
                    }else{
                    binding.output.setText(driveLink(binding.link.getText().toString(),null));
                    Toast.makeText(this,"Link generated",Toast.LENGTH_SHORT).show();
                    }
                } else if (linkType=="d"){
                    if(!binding.link.getText().toString().contains("dl=0")||!binding.link.getText().toString().contains("dropbox.com")) {
                    	binding.linkroot.setErrorEnabled(true);
                    binding.linkroot.setError("Invalid Dropbox Link");
                    }else{
                        binding.output.setText(convertDropbox(binding.link.getText().toString()));
                    Toast.makeText(this,"Link generated",Toast.LENGTH_SHORT).show();
                    }
                }
        });
        
        binding.copylink.setOnClickListener(v->{
            if(!binding.output.getText().toString().isEmpty()) {
            	ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clipData = ClipData.newPlainText("text", binding.output.getText().toString());
                clipboardManager.setPrimaryClip(clipData);
                Toast.makeText(this, "Link copied to clipboard", Toast.LENGTH_SHORT).show();
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
    }
    
    private void switchView(String type) {
        linkType=type;
        if(type=="m") {
        	binding.title.setText("Mediafire");
            binding.subtitle.setText("Convert mediafire link to direct downloadable link");
            binding.icon.setImageResource(R.drawable.mediafire_transparent);
        } else if(type=="g") {
        	binding.title.setText("Google Drive");
            binding.subtitle.setText("Convert google drive link to direct downloadable link");
            binding.icon.setImageResource(R.drawable.gdrive_transparent);
        } else if(type=="d") {
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
            Log.wtf("convertMediafire","convertedLink == "+convertedLink);
        } catch (Exception ex) {
            convertedLink = ex.getMessage();
            Log.wtf("convertMediafire",ex);
        }
    }
    
    private String convertDropbox(String link) {
    	return link.replace("dl=0","dl=1");
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
