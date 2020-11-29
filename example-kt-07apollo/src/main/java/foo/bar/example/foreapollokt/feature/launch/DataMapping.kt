package foo.bar.example.foreapollokt.feature.launch

import foo.bar.example.foreapollokt.graphql.LaunchListQuery

//we don't want the API / graphQL pojo abstractions leaking in to the rest of the app
//so we convert them here to app level items, nothing bellow the feature level
//knows anything about the API

val NO_LAUNCH = Launch("(no launch)", "no site")

data class Launch (
    val id: String,
    val site: String,
    val isCitrus: Boolean = true,
    val tastyPercentScore: Int = 50
)

fun LaunchListQuery.Launch.toApp(): Launch {
    return Launch(this.id, this.site ?: "unknown")
}
