# EasyScan

EasyScan is a library that scans and parses the contents of the QR code.

# Download

build.gradle of project

```groovy
buildscript {
    repositories {
        google()
        jcenter()
        maven {
            url = uri("https://maven.pkg.github.com/Chenziquan/EasyScan")
            credentials {
                username = "Chenziquan"
                password = "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"
            }
        }
    }
}

allprojects {
    repositories {
        google()
        jcenter()
        maven {
            url = uri("https://maven.pkg.github.com/Chenziquan/EasyScan")
            credentials {
                username = "Chenziquan"
                password = "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"
            }
        }
    }
}
```

build.gradle of module.

```groovy
implementation 'com.pax.jc:easy-scan:1.0.2'
```

# Sample

```kotlin
private fun goScannerActivity() {
        startActivityForResult(
            Intent(this@MainActivity, QRScannerActivity::class.java),
            REQUEST_SCAN_CODE
        )
}

override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    if (requestCode == REQUEST_SCAN_CODE) {
        if (Activity.RESULT_OK == resultCode) {
            if (data != null) {
                val code =
                    data.extras?.get(QRScannerActivity.EXTRA_SCAN_RESULT).toString()
                if (!TextUtils.isEmpty(code)) {
                    result?.text = code
                }
            }
        }
    }
}
```