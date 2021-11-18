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
            url 'https://openrepo.paxengine.com.cn/api/v4/projects/18/packages/maven'
            name "GitLab"
            credentials(HttpHeaderCredentials) {
                name = 'Deploy-Token'
                value = 'tKxMYSwBrxYcDZyVzZAm'
            }
            authentication {
                header(HttpHeaderAuthentication)
            }

        }
    }
}

allprojects {
    repositories {
        google()
        jcenter()
        maven {
            url 'https://openrepo.paxengine.com.cn/api/v4/projects/18/packages/maven'
            name "GitLab"
            credentials(HttpHeaderCredentials) {
                name = 'Deploy-Token'
                value = 'tKxMYSwBrxYcDZyVzZAm'
            }
            authentication {
                header(HttpHeaderAuthentication)
            }

        }
    }
}
```

build.gradle of module.

```groovy
implementation 'com.pax.jc:easy-scan:1.0.0'
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