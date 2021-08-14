package foo.bar.example.foreapollo3.feature.launch

import foo.bar.example.foreapollokt.graphql.LaunchDetailsQuery
import foo.bar.example.foreapollokt.graphql.LaunchListQuery

//we don't want the API / graphQL pojo abstractions leaking in to the rest of the app
//so we convert them here to app level items, nothing bellow the feature level
//knows anything about the API

const val NO_ID = "(no launch)"
val NO_LAUNCH = Launch(NO_ID, "no site", false, "")


data class Launch (
        val id: String,
        val site: String,
        val isBooked: Boolean = false,
        val patchImgUrl: String = ""
)

fun LaunchListQuery.Launch.toApp(): Launch {
    return Launch(this.id, this.site ?: "unknown", this.isBooked, this.mission?.missionPatch?: "")
}

fun LaunchDetailsQuery.Launch.toApp(): Launch {
    return Launch(this.id, this.site ?: "unknown", this.isBooked, this.mission?.missionPatch?: "")
}
