package com.scoretally.data.remote.bgg.dto

import org.simpleframework.xml.Attribute
import org.simpleframework.xml.Element
import org.simpleframework.xml.ElementList
import org.simpleframework.xml.Root

@Root(name = "items", strict = false)
data class BggSearchResponse(
    @field:ElementList(entry = "item", inline = true, required = false)
    var items: List<BggSearchItem>? = null
)

@Root(name = "item", strict = false)
data class BggSearchItem(
    @field:Attribute(name = "id")
    var id: String = "",

    @field:Element(name = "name", required = false)
    var name: BggName? = null,

    @field:Element(name = "yearpublished", required = false)
    var yearPublished: BggYear? = null
)

@Root(name = "name", strict = false)
data class BggName(
    @field:Attribute(name = "value")
    var value: String = ""
)

@Root(name = "yearpublished", strict = false)
data class BggYear(
    @field:Attribute(name = "value")
    var value: String = ""
)
