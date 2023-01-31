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
import com.verifone.connectors.threeds.dataobjects.EncodedThreedsData
import com.verifone.connectors.threeds.dataobjects.ThreedsConfigurationData


class Verifone3DSecureManager private constructor(
    configurationData: ThreedsConfigurationData,
    onInitComplete: (session: String) -> Unit,
    onValidationComplete: (authData: EncodedThreedsData) -> Unit,
    threedsEnvironment:Int
) {

    companion object {
        const val environmentStaging = 0
        const val environmentProduction = 1
        fun createInstance(configurationData: ThreedsConfigurationData,
                           onInitComplete: (session: String) -> Unit,
                           onValidationComplete: (authData: EncodedThreedsData) -> Unit,environmentParam:Int):Verifone3DSecureManager? {

            if (environmentParam != environmentStaging && environmentParam != environmentProduction) return null
            try {
                val m3DSInstance = Verifone3DSecureManager(
                    configurationData,
                    onInitComplete,
                    onValidationComplete,
                    environmentParam
                )
                return m3DSInstance
            } catch (e:NoClassDefFoundError){
                return null
            }

        }
    }

    private var threedsInitComplete:(sessionID: String) -> Unit = onInitComplete
    private var validationComplete:(authData: EncodedThreedsData) -> Unit = onValidationComplete

    private val cardinal = Cardinal.getInstance()

    private val serverJwt:String
    private lateinit var mValidateResponse: ValidateResponse

    init {
        val cardinalConfigurationParameters = CardinalConfigurationParameters()

        if(threedsEnvironment==0) {
            cardinalConfigurationParameters.environment = CardinalEnvironment.STAGING
        }
        else {
            cardinalConfigurationParameters.environment = CardinalEnvironment.PRODUCTION
        }

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
        val encodedData = EncodedThreedsData()
        cardinal.cca_continue(transactionID,payload,ctx,object: CardinalValidateReceiver {

            override fun onValidated(p0: Context?, p1: ValidateResponse?, p2: String?) {
                if (p1?.actionCode?.name == "SUCCESS") {
                    encodedData.validationStatus = "success"
                    encodedData.encodedThreedsString = p2.toString()

                    validationComplete(encodedData)

                } else if (p1?.actionCode?.name == "CANCEL") {
                    val threedAuthCancel = EncodedThreedsData()
                    threedAuthCancel.validationStatus = p1?.actionCode?.name.toString()
                    validationComplete(threedAuthCancel)
                }
            }
        })
    }

    fun validateTS() {
        val encodedData = EncodedThreedsData()
        cardinal.init(serverJwt, object: CardinalInitService {
            override fun onSetupCompleted(consumerSessionId: String) {
                initError += "onSetupCompleteCall:$consumerSessionId\n"
                threedsInitComplete(consumerSessionId)
            }

            override fun onValidated(validateResponse: ValidateResponse, serverJwt: String?) {
                initError += "onValidatedCall:${validateResponse}\n"
                //eventReporting.onThreeDValidationFailed(initError)
                /*val threedAuth = ThreedsValidationData(
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    ""
                )*/
                encodedData.validationStatus = "failed"
                validationComplete(encodedData)
                mValidateResponse = validateResponse
            }
        })
    }
}