package dipen.kotlin.contactpicker.ui

import android.Manifest
import android.annotation.SuppressLint
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.fondesa.kpermissions.allGranted
import com.fondesa.kpermissions.extension.permissionsBuilder
import com.fondesa.kpermissions.extension.send
import dipen.kotlin.contactpicker.R
import dipen.kotlin.contactpicker.common.empty
import dipen.kotlin.contactpicker.common.openAppSettingPage
import dipen.kotlin.contactpicker.common.showRationaleDialog
import dipen.kotlin.contactpicker.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initViews()
    }

    //Initialize View and Listener
    private fun initViews() {
        binding.pickContactBtn.setOnClickListener {
            permissionsBuilder(Manifest.permission.READ_CONTACTS).build().send {
                if (it.allGranted()) {
                    resultLauncher.launch(null)
                } else {
                    showRationaleDialog(R.string.rationale_title, R.string.rationale_message) {
                        openAppSettingPage()
                    }
                }
            }
        }
    }

    /**
     * startActivityForResult is deprecated so instead we can use registerForActivityResult.
     * [https://developer.android.com/training/basics/intents/result]
     *
     * registerForActivityResult should be called before activity resume otherwise it will throw below exception,
     * so it is better to initialize it as property like below or initialize before onResume method
     *
     * [@exception java.lang.IllegalStateException: LifecycleOwner is attempting to register while current state is RESUMED.
     * LifecycleOwners must call register before they are STARTED. ]
     **/
    private val resultLauncher =
        registerForActivityResult(ActivityResultContracts.PickContact()) { uri ->
            uri?.let {
                binding.contactResultTv.text = retrievePhoneNumber(uri)
            }
        }

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
}
