package edu.ttp.gengame

class SchemaElement constructor(val type: String? = null,
                                val length: Int? = null,
                                val min: Double? = null,
                                val range: Double? = null,
                                val factor: Int? = null,
                                val limit: Int? = null,
                                val inclusive: Boolean? = null,
                                val offset: Int? = null)