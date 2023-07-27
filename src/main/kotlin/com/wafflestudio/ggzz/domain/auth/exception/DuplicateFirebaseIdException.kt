package com.wafflestudio.ggzz.domain.auth.exception

import com.wafflestudio.ggzz.global.common.exception.CustomException.ConflictException
import com.wafflestudio.ggzz.global.common.exception.ErrorType.Conflict.DUPLICATE_FIREBASE_ID

class DuplicateFirebaseIdException(id: String): ConflictException(DUPLICATE_FIREBASE_ID, "Firebase ID '$id' exists.")