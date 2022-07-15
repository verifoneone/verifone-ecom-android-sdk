package com.verifone.connectors.threeds

import android.app.Activity
import android.content.Context
import org.json.JSONArray

import com.cardinalcommerce.cardinalmobilesdk.Cardinal
import com.cardinalcommerce.cardinalmobilesdk.enums.CardinalEnvironment
import com.cardinalcommerce.cardinalmobilesdk.enums.CardinalRenderType
import com.cardinalcommerce.cardinalmobilesdk.enums.CardinalUiType
import com.cardinalcommerce.cardinalmobilesdk.models.CardinalConfigurationParameters
import com.cardinalcommerce.cardinalmobilesdk.models.ValidateResponse
import com.cardinalcommerce.cardinalmobilesdk.services.CardinalInitService
import com.cardinalcommerce.cardinalmobilesdk.services.CardinalValidateReceiver
import com.cardinalcommerce.shared.userinterfaces.UiCustomization
import com.verifone.connectors.threeds.dataobjects.ThreedsConfigurationData
import com.verifone.connectors.threeds.dataobjects.ThreedsValidationData


class Verifone3DSecureManager private constructor(
    configurationData: ThreedsConfigurationData,
    onInitComplete: (session: String) -> Unit,
    onValidationComplete: (authData: ThreedsValidationData) -> Unit
) {

    companion object {
        fun createInstance(configurationData: ThreedsConfigurationData,
                           onInitComplete: (session: String) -> Unit,
                           onValidationComplete: (authData: ThreedsValidationData) -> Unit):Verifone3DSecureManager? {
            try {
                val m3DSInstance = Verifone3DSecureManager(
                    configurationData,
                    onInitComplete,
                    onValidationComplete
                )
                return m3DSInstance
            } catch (e:NoClassDefFoundError){
                return null
            }

        }
    }

    private var threedsInitComplete:(sessionID: String) -> Unit = onInitComplete
    private var validationComplete:(authData: ThreedsValidationData) -> Unit = onValidationComplete

    private val cardinal = Cardinal.getInstance()

    private val serverJwt:String
    private lateinit var mValidateResponse: ValidateResponse

    init {
        val cardinalConfigurationParameters = CardinalConfigurationParameters()
        cardinalConfigurationParameters.environment = CardinalEnvironment.STAGING
        cardinalConfigurationParameters.requestTimeout = 8000
        cardinalConfigurationParameters.challengeTimeout = 5
        val rTYPE = JSONArray()
        rTYPE.put(CardinalRenderType.OTP)
        rTYPE.put(CardinalRenderType.SINGLE_SELECT)
        rTYPE.put(CardinalRenderType.MULTI_SELECT)
        rTYPE.put(CardinalRenderType.OOB)
        rTYPE.put(CardinalRenderType.HTML)
        cardinalConfigurationParameters.renderType = rTYPE
        cardinalConfigurationParameters.uiType = CardinalUiType.BOTH
        val yourUICustomizationObject = UiCustomization()
        cardinalConfigurationParameters.uiCustomization = yourUICustomizationObject
        cardinal.configure(configurationData.mCtx, cardinalConfigurationParameters)
        serverJwt = configurationData.jwtToken
    }

    //var continueValidError:String =""
    var initError:String =""

    fun continueTSValidation(transactionID:String, payload:String, ctx: Activity) {
        cardinal.cca_continue(transactionID,payload,ctx,object: CardinalValidateReceiver {
            override fun onValidated(p0: Context?, p1: ValidateResponse?, p2: String?) {
                if (p1?.actionCode?.name == "SUCCESS") {
                    val cavv = p1?.payment?.extendedData?.cavv
                    val enrolled = p1?.payment?.extendedData?.enrolled
                    val eciFlag = p1?.payment?.extendedData?.eciFlag
                    val paresStatus = p1?.payment?.extendedData?.paResStatus
                    val sigVerification = p1?.payment?.extendedData?.signatureVerification
                    val dsTransactionID = p1?.payment?.processorTransactionId
                    val xid = p1?.payment?.extendedData?.xid
                    val threedAuth = ThreedsValidationData(
                        eciFlag,
                        enrolled,
                        cavv,
                        paresStatus,
                        sigVerification,
                        dsTransactionID,
                        xid
                    )
                    threedAuth.validationStatus = "success"
                    //continueValidError = "Cardinal continue valid:\n\n "+"action"+p1?.actionCode+"\n\n Error Description:"+p1?.errorDescription+" isValid:${p1?.isValidated} errorCode:${p1?.errorNumber}"
                    validationComplete(threedAuth)

                } else if (p1?.actionCode?.name == "CANCEL") {
                    val threedAuthCancel = ThreedsValidationData()
                    threedAuthCancel.validationStatus = p1?.actionCode?.name.toString()
                    validationComplete(threedAuthCancel)
                }
            }
        })
    }

    fun validateTS() {
        cardinal.init(serverJwt, object: CardinalInitService {
            override fun onSetupCompleted(consumerSessionId: String) {
                initError += "onSetupCompleteCall:$consumerSessionId\n"
                threedsInitComplete(consumerSessionId)
            }

            override fun onValidated(validateResponse: ValidateResponse, serverJwt: String?) {
                initError += "onValidatedCall:${validateResponse}\n"
                //eventReporting.onThreeDValidationFailed(initError)
                val threedAuth = ThreedsValidationData(
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    ""
                )
                threedAuth.validationStatus = "failed"
                validationComplete(threedAuth)
                mValidateResponse = validateResponse
            }
        })
    }
}