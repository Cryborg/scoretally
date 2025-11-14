package com.scoretally.data.remote.bgg.dto

import org.simpleframework.xml.Attribute
import org.simpleframework.xml.Element
import org.simpleframework.xml.ElementList
import org.simpleframework.xml.Root

@Root(name = "items", strict = false)
data class BggDetailsResponse(
    @field:ElementList(entry = "item", inline = true, required = false)
    var items: List<BggDetailsItem>? = null
)

@Root(name = "item", strict = false)
data class BggDetailsItem(
    @field:Attribute(name = "id")
    var id: String = "",

    @field:ElementList(entry = "name", inline = true, required = false)
    var names: List<BggDetailName>? = null,

    @field:Element(name = "description", required = false)
    var description: String? = null,

    @field:Element(name = "image", required = false)
    var image: String? = null,

    @field:Element(name = "thumbnail", required = false)
    var thumbnail: String? = null,

    @field:Element(name = "minplayers", required = false)
    var minPlayers: BggMinPlayers? = null,

    @field:Element(name = "maxplayers", required = false)
    var maxPlayers: BggMaxPlayers? = null,

    @field:Element(name = "playingtime", required = false)
    var playingTime: BggPlayingTime? = null,

    @field:ElementList(entry = "link", inline = true, required = false)
    var links: List<BggLink>? = null,

    @field:Element(name = "statistics", required = false)
    var statistics: BggStatistics? = null
)

@Root(name = "name", strict = false)
data class BggDetailName(
    @field:Attribute(name = "type")
    var type: String = "",

    @field:Attribute(name = "value")
    var value: String = ""
)

@Root(name = "minplayers", strict = false)
data class BggMinPlayers(
    @field:Attribute(name = "value")
    var value: String = ""
)

@Root(name = "maxplayers", strict = false)
data class BggMaxPlayers(
    @field:Attribute(name = "value")
    var value: String = ""
)

@Root(name = "playingtime", strict = false)
data class BggPlayingTime(
    @field:Attribute(name = "value")
    var value: String = ""
)

@Root(name = "link", strict = false)
data class BggLink(
    @field:Attribute(name = "type")
    var type: String = "",

    @field:Attribute(name = "value")
    var value: String = ""
)

@Root(name = "statistics", strict = false)
data class BggStatistics(
    @field:Element(name = "ratings", required = false)
    var ratings: BggRatings? = null
)

@Root(name = "ratings", strict = false)
data class BggRatings(
    @field:Element(name = "average", required = false)
    var average: BggAverage? = null
)

@Root(name = "average", strict = false)
data class BggAverage(
    @field:Attribute(name = "value")
    var value: String = ""
)
