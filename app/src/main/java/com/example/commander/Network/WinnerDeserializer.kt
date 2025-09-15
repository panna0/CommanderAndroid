package com.example.commander.Network

import com.example.commander.Models.Player
import com.example.commander.Models.Team
import com.example.commander.Models.Winner



import com.google.gson.*
import java.lang.reflect.Type

class WinnerDeserializer : JsonDeserializer<Winner?> {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): Winner? {
        if (json == null || json.isJsonNull) return null

        return when {
            json.isJsonPrimitive -> Winner.Single(json.asString)

            json.isJsonArray -> {
                val arr = json.asJsonArray
                if (arr.size() == 0) return null

                val firstObj = arr[0].asJsonObject
                return when {
                    firstObj.has("username") -> {
                        val players = arr.map { element ->
                            context!!.deserialize<Player>(element, Player::class.java)
                        }
                        Winner.Players(players)
                    }
                    firstObj.has("team_name") -> {
                        val teams = arr.map { element ->
                            context!!.deserialize<Team>(element, Team::class.java)
                        }
                        Winner.Teams(teams)
                    }
                    else -> null
                }
            }

            else -> null
        }
    }
}
