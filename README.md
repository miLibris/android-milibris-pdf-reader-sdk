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
        maven {
            url 'https://maven.milibris.com/'
            credentials {
                username = maven_user
                password = maven_password
            }
        }
    }
}

dependencies {
  api 'com.milibris:pdf-reader:2.6.16'
}
```

In order for the sdk to work you need to add the API-KEY in your manifest as below
```manifest
<meta-data
    android:name="com.milibris.pdfreader.apikey"
    android:value="YOU_API_KEY" />

```
