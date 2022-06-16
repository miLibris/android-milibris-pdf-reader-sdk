package com.milibris.pdfreader.sampleapp;

import android.app.Activity;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.milibris.foundation.CompleteArchive;
import com.milibris.foundation.Foundation;
import com.milibris.foundation.FoundationContext;
import com.milibris.lib.pdfreader.PdfReader;
import com.milibris.lib.pdfreader.stats.StatsListener;
import com.milibris.lib.pdfreader.ui.PageListener;
import com.milibris.pdfreader.sampleapp.sharing.SharingUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

/**
 * Sample activity unpacking and reading a content with PDF reader.
 * <p>
 * miLibris
 */
public class MainActivity extends AppCompatActivity implements PageListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private final String pdfName = "sample";
    private int currentPageIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.buttonOpen).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openReader();
            }
        });

    }

    @Override
    public void onPdfReaderSwipePage(int pageIndex) {
        currentPageIndex = pageIndex;
    }

    private void openReader() {
        // Initialize the PDF reader to open the contents
        PdfReader reader = new PdfReader(getApplicationContext(),
                getContentPath(),
                null,
                currentPageIndex, //restore previous page
                new PdfReader.Configuration() {

                    @Override
                    public boolean isArticlesSharingEnabled() {
                        return true;
                    }

                    @Override
                    public void shareArticleContent(PdfReader.Article article, Activity activity) {
                        Log.i(TAG, "Share article: " + article.title);
                        //
                        //   This is an example for sharing the article content via the email
                        //   SharingUtils.INSTANCE.shareArticleViaMail(activity, article);

                        //Second example to share the Article Url
                        SharingUtils.INSTANCE.shareArticleUrl(activity,
                                "IssueMidExample",
                                "https://www.examplekiosk.milibris.com",
                                article);
                        // Implement custom sharing logic (Android Intent?)
                    }

                    @Override
                    public boolean articleTTSEnabled() {
                        return true;
                    }
                    // Uncomment to change navigation colors
            /*
            @Override
            public int getNavigationTintColor() {
                return Color.RED;
            }

            @Override
            public int getNavigationTextColor() {
                return Color.WHITE;
            }

            @Override
            public int getSelectedPageBorderColor() {
                return Color.RED;
            }
            //*/
                });

        //reader will clean this listener when closed automatically
        reader.setPageListener(this);

        reader.setReaderListener(new PdfReader.Listener() {
            @Override
            public void onFinishReading(PdfReader reader) {
                Log.i(TAG, "Reader event: finish reading");
            }

            @Override
            public void onPdfLoadingError(Exception e) {
                Log.e(TAG, "Reader event: can't load PDF", e);

                Toast.makeText(MainActivity.this, "PDF file is corrupted, please try again to download and open.", Toast.LENGTH_SHORT).show();
            }
        });
        reader.setStatsListener(new StatsListener() {

            @Override
            public void onMoveToPageNumber(PdfReader pdfReader, int pageNumber) {
                // index of page number opened
            }

            @Override
            public void onOpenArticle(PdfReader pdfReader, Map<String, Object> article) {
                //Article opened
            }

            @Override
            public void onSwipeArticle(PdfReader pdfReader, Map<String, Object> article) {
                //Swiped to new article
            }

            @Override
            public void onOpenLinkBoxWithContentUrl(PdfReader pdfReader, String contentUrl) {
                //url box clicked
            }

            @Override
            public void onOpenSlideshowBoxWithResourceName(PdfReader pdfReader, String resourceName) {
                //Slide show enrichment is opened

            }

            @Override
            public void onOpenVideoBoxWithContentUrl(PdfReader pdfReader, String contentUrl) {
                //Video enrichment is opened
            }

            @Override
            public void onOpenHtml5BoxWithResourceName(PdfReader pdfReader, String resourceName) {
                //html5 enrichment is opened
            }
        });

        reader.startReaderActivity(this);
    }

    private String getContentPath() {

        File contentFile = new File(getExternalFilesDir(null), pdfName);

        // Unpack content if not done already
        if (!contentFile.isDirectory()) {

            // Copy archive file to writable directory
            copySampleArchiveToDisk();

            // Unpack archive
            unpackArchive();
        }

        if (contentFile.isDirectory()) {
            Log.i(TAG, "Content available at " + contentFile.getAbsolutePath());
        }

        return contentFile.getAbsolutePath();
    }

    private void copySampleArchiveToDisk() {
        AssetManager assetManager = getAssets();

        String filename = pdfName + ".complete";
        InputStream in = null;
        OutputStream out = null;
        try {
            in = assetManager.open(filename);
            File outFile = new File(getExternalFilesDir(null), filename);
            out = new FileOutputStream(outFile);

            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }

        } catch (IOException e) {
            Log.e(TAG, "Failed to copy asset file: " + filename, e);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                }
            }
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                }
            }
        }
    }

    private void unpackArchive() {

        FoundationContext foundationContext = Foundation.createContext(getApplicationContext());

        try {
            CompleteArchive archive = new CompleteArchive(foundationContext, getAssets().openFd(pdfName + ".complete").createInputStream());
            archive.unpackTo(new File(getExternalFilesDir(null), pdfName));
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
