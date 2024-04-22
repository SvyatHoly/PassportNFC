package com.slavaholy.passportNFC

import com.facebook.react.bridge.*
import android.nfc.Tag
import android.nfc.tech.IsoDep
import org.jmrtd.PassportService
import org.jmrtd.lds.icao.DG1File
import net.sf.scuba.smartcards.CardService
import org.jmrtd.BACKey
import android.nfc.NfcAdapter
class NFCModule(private val reactContext: ReactApplicationContext) : ReactContextBaseJavaModule(reactContext) {

    override fun getName(): String {
        return "NFCModule"
    }

    @ReactMethod
    fun read(mrzKey: String, promise: Callback) {
        val intent = reactContext.currentActivity?.intent
        val tag: Tag? = intent?.getParcelableExtra(NfcAdapter.EXTRA_TAG)
        val isoDep = IsoDep.get(tag)

        if (tag == null) {
            promise.invoke("NFC_ERROR", "No NFC tag detected")
            return
        }

        try {

            isoDep.connect()

            val cardService = CardService.getInstance(isoDep)
            val passportService = createPassportService(cardService)
            passportService.open()

            val bacKey = BACKey(mrzKey.substring(0, 9), mrzKey.substring(9, 15), mrzKey.substring(15, 21))
            passportService.doBAC(bacKey)

            val dg1File = readDG1(passportService) // Adjusted to use a hypothetical correct method
            val mrzInfo = dg1File?.mrzInfo

            if (mrzInfo != null) {
                val result = WritableNativeMap()
                result.putString("documentNumber", mrzInfo.documentNumber)
                result.putString("dateOfBirth", mrzInfo.dateOfBirth)
                result.putString("dateOfExpiry", mrzInfo.dateOfExpiry)
                result.putString("nationality", mrzInfo.nationality)
                result.putString("surname", mrzInfo.primaryIdentifier)
                // Check how secondaryIdentifier is structured and handle accordingly
                val givenNames = mrzInfo.secondaryIdentifier // Assuming it's a String
                result.putString("givenNames", givenNames)

                promise.invoke(null, result)
            } else {
                promise.invoke("ERROR", "Failed to read MRZ information")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            promise.invoke("ERROR", "Failed to read NFC: ${e.localizedMessage}")
        } finally {
            isoDep?.close()
        }
    }

    fun readDG1(passportService: PassportService): DG1File? {
        try {
            val inputStream = passportService.getInputStream(PassportService.EF_DG1)
            val dg1File = DG1File(inputStream)
            return dg1File
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }
    fun createPassportService(cardService: CardService): PassportService {
        val isSFIEnabled = true // Enable or disable SFI based on your requirement
        val maxBlockSize = 256 // Typical block size for APDU commands
        val maxTranceiveLength = 261 // This could be slightly larger than maxBlockSize
        val shouldCheckMAC = true // Enable MAC checking for security

        return PassportService(
            cardService,
            maxTranceiveLength,
            maxBlockSize,
            isSFIEnabled,
            shouldCheckMAC
        )
    }
}
