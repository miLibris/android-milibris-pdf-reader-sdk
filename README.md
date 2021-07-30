# android-milibris-pdf-reader-sdk

**PDF Reader Sample APP**

This is a sample using the sdk of Milibris pdf reader including a example of the implementation of the sdk

You can download a jar using gradle:

```gradle
allprojects {
    repositories {
        google()
        jcenter()
        maven { url "https://jitpack.io" }
        maven { url 'http://repo.brightcove.com/releases' }
        maven { url 'https://maven-android-sdk.milibris.net/' }
    }
}

dependencies {
  api 'com.milibris:pdf-reader:3.5.0'
}
```

In order for the sdk to work you need to add the licence key in your manifest as below
```manifest
<meta-data
    android:name="com.milibris.pdfreader.licencekey"
    android:value="YOUR_LICENCE_KEY" />

```

# Implementation

In order to read a content, your application will likely implement the following steps:
1. Download a complete archive (with the *.complete extension) from the miLibris
platform.
2. Unpack the archive using MLFoundation
3. Launch PDFReader to read the unpacked contents

**1. Unpack a complete archive with MLFoundation**

A complete archive can be easily unpacked with the MLFoundation library utilities (see
example below, extracting a sample.complete file in Android assets).
```
FoundationContext foundationContext =
Foundation.createContext(getApplicationContext());
CompleteArchive archive = new CompleteArchive(foundationContext, new
File(getExternalFilesDir(null), "sample.complete"));
try {
    archive.unpackTo(new File(getExternalFilesDir(null), "sample"));
} catch (Throwable e) {
    e.printStackTrace();
}
```

**2. Read unpacked contents**

Once unpacked, you can open the content by starting a new Activity with PdfReader.
```
// Configure the reader with a PdfReader.Configuration instance
PdfReader.Configuration config = new PdfReader.Configuration() {
    // Override methods to change reader configuration
}
// Set content path
File contentFile = new File(getExternalFilesDir(null), "sample");
String contentPath = contentFile().getAbsolutePath();
// Initialize the PDF Reader to open the content
PdfReader reader = new PdfReader(getApplicationContext(), "path/to/content",
config);
// Assign a listener to get notified from reader changes
reader.setReaderListener(new PdfReader.Listener() {
    @Override
    public void onFinishReading(PdfReader reader) {
        Log.i(TAG, "Reader event: finish reading");
    }
    @Override
    public void onPdfLoadingError(Exception e) {
        Log.i(TAG, "Reader event: PDF is corrupted error ");
    }
});
```
We are providing an instance of PdfReader.Configuration to customize the reader and
providing an object implementing the PdfReader.Listener interface. We can then start
the activity.
```
// Start reader activity from parent activity (this)
reader.startReaderActivity(this);
```


**3 Sharing an article

If your miLibris content has articles, you can implement your own sharing solution by implementing shareArticleContent. Two examples are provided in the sample project:

#### Share link to web kiosk

Share the web kiosk URL of an article. The user can use the social app of his/her choice. This feature must be enabled on the web kiosk. Contact miLibris support for more info.

#### Send article by email

Send the content of an article by email.

**4. Sample project**

A sample project is provided to help you implement the reader integration. It contains an example to unpack a complete archive and to open if with PdfReader class.

If your miLibris content has articles, you can implement your own sharing solution by implementing isArticlesSharingEnabled() and shareArticleContent() in the configuration object. A basic example is also provided in the sample project.
