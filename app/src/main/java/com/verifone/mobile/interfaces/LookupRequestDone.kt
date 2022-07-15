package com.verifone.mobile.interfaces

import com.verifone.mobile.responses.TestLookupResponse

interface LookupRequestDone {
    fun lookupRequestSuccess(response:TestLookupResponse)
    fun lookupRequestFailed(t:Throwable)
}