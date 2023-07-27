package com.verifone.connectors.util


import android.util.Log
import androidx.annotation.Nullable
import okhttp3.internal.EMPTY_BYTE_ARRAY
import org.bouncycastle.bcpg.*
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.bouncycastle.openpgp.*
import org.bouncycastle.openpgp.PGPException
import org.bouncycastle.openpgp.jcajce.JcaPGPPublicKeyRingCollection
import org.bouncycastle.openpgp.operator.jcajce.JcePGPDataEncryptorBuilder
import org.bouncycastle.openpgp.operator.jcajce.JcePublicKeyKeyEncryptionMethodGenerator
import org.bouncycastle.util.encoders.Base64
import org.bouncycastle.util.encoders.DecoderException
import org.bouncycastle.util.encoders.Hex
import org.bouncycastle.util.io.Streams
import org.json.JSONObject
import java.io.*
import java.security.NoSuchAlgorithmException
import java.security.SecureRandom
import java.security.spec.InvalidKeySpecException
import java.text.SimpleDateFormat
import java.util.*


internal class CardEncryption {

    private val cardDataObject: JSONObject = JSONObject()
    private lateinit var merchantPubKey: String
    var isPublicKeyValid = false

    companion object {
        const val cardDataParam1 = "expiryMonth"
        const val cardDataParam2 = "expiryYear"
        const val cardDataParam3 = "cvv"
        const val cardDataParam4 = "cardNumber"
        const val cardDataParam5 = "captureTime"
        const val cardDataParam6 = "svcAccessCode"
    }

    constructor (
        expiryMonthParam: Int? = null,
        expiryYearParam: Int? = null,
        cardNumberParam: String,
        cvvNumberParam: String? = null,
        publicKey: String,
        pinNumberParam: String? = null,
    ) {
        cardDataObject.put(cardDataParam1, expiryMonthParam)
        cardDataObject.put(cardDataParam2, expiryYearParam)
        cardDataObject.put(cardDataParam3, cvvNumberParam)
        cardDataObject.put(cardDataParam4, cardNumberParam)
        cardDataObject.put(cardDataParam6, pinNumberParam)
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US)
        sdf.timeZone = TimeZone.getTimeZone("GMT")
        val capturedTime = sdf.format(Date())

        cardDataObject.put(cardDataParam5, capturedTime)
        if (publicKey.isNotEmpty()) {
            merchantPubKey = publicKey
            try {
                pgpKey = getPubKeyFromPem(merchantPubKey)
                isPublicKeyValid = true
            } catch (e: Exception) {
                isPublicKeyValid = false
            }

        }
    }

    private lateinit var ecDHKey: ECDHPublicBCPGKey
    private var pgpKey: PGPPublicKey? = null
    private lateinit var encryptedCardPayload: String
    private fun encodeBase64(paramValue: ByteArray): ByteArray {
        return Base64.encode(paramValue)
    }

    private fun decodeBase64(encryptedVal: String): String {
        return String(Base64.decode(encryptedVal))
    }

    fun encryptionCard(): String {
        val cardString = cardDataObject.toString()
        val tempCard: ByteArray
        try {
            tempCard = encrypt(cardString.toByteArray(), pgpKey, true) ?: "".toByteArray()
        } catch (e: java.lang.Exception) {
            return ""
        }

        val encryptedCard = encodeBase64(tempCard)
        val str = String(encryptedCard)
        encryptedCardPayload = str
        return encryptedCardPayload

    }

    private fun getPubKeyFromPem(@Nullable pem: String?): PGPPublicKey? {
        if (pem != null) {
            val inputStream: InputStream =
                ArmoredInputStream(ByteArrayInputStream(decodeBase64(pem).toByteArray()))
            try {
                try {
                    val ringCollection = JcaPGPPublicKeyRingCollection(inputStream)
                    val keyRingsIterator = ringCollection.keyRings
                    while (keyRingsIterator.hasNext()) {
                        val pgpPublicKeyRing = keyRingsIterator.next()
                        val pubKeysIterator = pgpPublicKeyRing.publicKeys
                        while (pubKeysIterator.hasNext()) {
                            val pgpPublicKey = pubKeysIterator.next()
                            if (pgpPublicKey.isEncryptionKey) {
                                val bcKey = pgpPublicKey.publicKeyPacket.key
                                if (bcKey is ECDHPublicBCPGKey) {
                                    ecDHKey = bcKey

                                    return pgpPublicKey
                                }
                                return pgpPublicKey
                            }
                        }
                    }
                    return null
                } catch (e: PGPException) {
                    e.printStackTrace()
                    return null
                } catch (e: InvalidKeySpecException) {
                    e.printStackTrace()
                    return null

                } catch (e: NoSuchAlgorithmException) {
                    e.printStackTrace()
                    return null
                }
            } catch (e: IOException) {
                e.printStackTrace()
                return null
            } finally {
                try {
                    inputStream.close()
                } catch (ignore: IOException) {
                }
            }
        } else {

            return null
        }
    }

    private val provider = BouncyCastleProvider()

    @Throws(PGPException::class)
    private fun encrypt(
        message: ByteArray?,
        publicKey: PGPPublicKey?,
        armored: Boolean
    ): ByteArray? {
        return try {
            val messageStream = ByteArrayInputStream(message)
            val bOut = ByteArrayOutputStream()
            val literal = PGPLiteralDataGenerator()
            val comData = PGPCompressedDataGenerator(CompressionAlgorithmTags.UNCOMPRESSED)
            val pOut: OutputStream = literal.open(
                comData.open(bOut), PGPLiteralData.TEXT, "card_encryption",
                messageStream.available().toLong(),
                Date()
            )
            Streams.pipeAll(messageStream, pOut)
            comData.close()
            val bytes: ByteArray = bOut.toByteArray()
            val generator = PGPEncryptedDataGenerator(
                JcePGPDataEncryptorBuilder(SymmetricKeyAlgorithmTags.IDEA).setWithIntegrityPacket(
                    true
                )
                    .setSecureRandom(
                        SecureRandom()
                    )
                    .setProvider(provider)
            )
            generator.addMethod(
                JcePublicKeyKeyEncryptionMethodGenerator(publicKey).setProvider(
                    provider
                )
            )
            val out = ByteArrayOutputStream()
            val theOut: OutputStream = if (armored) ArmoredOutputStream(out) else out
            val cOut: OutputStream = generator.open(theOut, bytes.size.toLong())
            cOut.write(bytes)
            cOut.close()
            theOut.close()
            out.toByteArray()
        } catch (e: java.lang.Exception) {
            throw PGPException("Error in encrypt", e)
        }
    }
}