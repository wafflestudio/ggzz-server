package com.wafflestudio.ggzz.domain.auth.exception

import com.wafflestudio.ggzz.domain.auth.model.Provider
import com.wafflestudio.ggzz.global.common.exception.CustomException.BadRequestException
import com.wafflestudio.ggzz.global.common.exception.ErrorType.BadRequest.INVALID_PROVIDER

class InvalidProviderException(provider: Provider): BadRequestException(INVALID_PROVIDER, "Invalid provider: $provider")