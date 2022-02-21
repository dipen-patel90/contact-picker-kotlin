# Contact picker in Kotlin
### Phone contact picker sample with registerForActivityResult

Previously we were able to pick contact from device contact list using startActivityForResult, but ~~**startActivityForResult**~~ is deprecated now, instead google suggest to use registerForActivityResult. 

https://developer.android.com/training/basics/intents/result



We can open contact list and retrieve the URI of selected contact using below code.

```
private val resultLauncher =
    registerForActivityResult(ActivityResultContracts.PickContact()) { uri ->
        uri?.let {
            Log.d(TAG, "Selected contact URI: $uri")
            binding.contactResultTv.text = retrievePhoneNumber(uri)
        }
    }
resultLauncher.launch(null)
```

We can retrieve actual contact from URI using below function.

```
@SuppressLint("Range")
private fun retrievePhoneNumber(uri: Uri): String {
    var phoneNumber = String.empty()

    val phone: Cursor? = this.contentResolver?.query(uri, null, null, null, null)
    phone?.let {
        if (it.moveToFirst()) {

            val id = it.getString(it.getColumnIndex(ContactsContract.Contacts._ID))
            val hasNumber =
                (it.getInt(it.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)))

            if (hasNumber > 0) {
                val phoneCursor: Cursor? = this.contentResolver?.query(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    null,
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", arrayOf(id),
                    null
                )
                phoneCursor?.let {
                    if (phoneCursor.moveToNext()) {
                        phoneNumber = phoneCursor.getString(
                            phoneCursor.getColumnIndex(
                                ContactsContract.CommonDataKinds.Phone.NUMBER
                            )
                        )
                    }
                    phoneCursor.close()
                }
            }
        }
        it.close()
    }
    return phoneNumber
}
```



https://user-images.githubusercontent.com/7566567/154792332-16e8a53b-8ab2-4c48-a6ff-e3d064d01662.mp4

