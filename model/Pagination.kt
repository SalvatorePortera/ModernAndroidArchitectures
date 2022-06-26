package com.nereus.craftbeer.model

import com.nereus.craftbeer.constant.DEFAULT_API_LIMIT
import com.nereus.craftbeer.constant.DEFAULT_API_PAGE

abstract class Pagination {

    var page: Int = DEFAULT_API_PAGE

    var limit: Int = DEFAULT_API_LIMIT

    var total: Int = DEFAULT_API_PAGE
}