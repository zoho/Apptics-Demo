Cross-Promotion shows a curated gallery of your organisation's other apps inside your app. The
gallery's contents — the app cards, badges, and images — are configured per-app on the Apptics
console; the SDK simply launches an Apptics-owned gallery screen on demand.

Before you begin, make sure Apptics is integrated by following the
[Integration Guide](https://www.zoho.com/apptics/resources/SDK/android-integrations.html).

---

## Add the Cross-Promotion SDK

Using Apptics BoM (recommended):

```groovy
dependencies {
    implementation platform('com.zoho.apptics:apptics-bom:[latest-version]')
    implementation 'com.zoho.apptics:apptics-crosspromo'
}
```

Without BoM:

```groovy
dependencies {
    implementation 'com.zoho.apptics:apptics-crosspromo:[latest-version]'
}
```

---

## Launch the gallery

`AppticsCrossPromotion.startActivity(activity)` opens the Apptics-owned cross-promotion gallery.
It takes an `Activity` (used to start the gallery screen), so call it from a context you can
resolve to an `Activity`.

```kotlin
// Kotlin
import com.zoho.apptics.crosspromotion.AppticsCrossPromotion

AppticsCrossPromotion.startActivity(activity)
```

```java
// Java
import com.zoho.apptics.crosspromotion.AppticsCrossPromotion;

AppticsCrossPromotion.startActivity(activity);
```

In Compose, resolve the `Activity` from the current context before calling:

```kotlin
val context = LocalContext.current

(context as? Activity)?.let { activity ->
    AppticsCrossPromotion.startActivity(activity)
}
```

That single call is the entire integration — there is no setup or callback to wire up.

---

## Configuration

The gallery's cards, badges, and images are managed per-app on the Apptics web console, not in
code. The SDK only launches the screen; whatever you configure on the console is what appears.

---

## API reference

| Method | What it does |
|--------|--------------|
| `startActivity(activity)` | Opens the cross-promotion gallery. Contents come from the Apptics console. |

📖 Docs: <https://www.zoho.com/apptics/resources/SDK/android-cross_promotion.html>
