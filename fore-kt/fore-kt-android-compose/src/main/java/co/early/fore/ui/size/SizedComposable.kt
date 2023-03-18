package co.early.fore.ui.size

import androidx.compose.runtime.Composable

interface SizedComposable

/**
 * @param[xs] function run when display is [Width.XSmall]
 * @param[s] function run when display is [Width.Small]
 * @param[m] function run when display is [Width.Medium]
 * @param[l] function run when display is [Width.Large]
 * @param[xl] function run when display is [Width.XLarge]
 */
class WidthBasedComposable(
    private val xs: @Composable (size: WindowSize) -> Unit,
    private val s: @Composable (size: WindowSize) -> Unit,
    private val m: @Composable (size: WindowSize) -> Unit,
    private val l: @Composable (size: WindowSize) -> Unit,
    private val xl: @Composable (size: WindowSize) -> Unit,
) : SizedComposable {

    /**
     * a single value to use for all screen sizes
     */
    constructor(m: @Composable (size: WindowSize) -> Unit) : this(m, m, m, m, m)

    /**
     * a two value split: one for phones (and smaller), another for tablets (and larger)
     */
    constructor(
        s: @Composable (size: WindowSize) -> Unit,
        l: @Composable (size: WindowSize) -> Unit,
    ) : this(s, s, s, l, l)

    /**
     * a three value split: watches / phones / tablets (and larger)
     */
    constructor(
        xs: @Composable (size: WindowSize) -> Unit,
        m: @Composable (size: WindowSize) -> Unit,
        l: @Composable (size: WindowSize) -> Unit,
    ) : this(xs, m, m, l, l)

    /**
     * a four value split: watches / small phones / large phones / tablets (and larger)
     */
    constructor(
        xs: @Composable (size: WindowSize) -> Unit,
        s: @Composable (size: WindowSize) -> Unit,
        m: @Composable (size: WindowSize) -> Unit,
        l: @Composable (size: WindowSize) -> Unit,
    ) : this(xs, s, m, l, l)

    @Composable
    operator fun invoke(size: WindowSize) {
        when (size.width) {
            Width.XSmall -> xs(size)
            Width.Small -> s(size)
            Width.Medium -> m(size)
            Width.Large -> l(size)
            Width.XLarge -> xl(size)
        }
    }
}

/**
 * @param[xs] function run when display is [Height.XSmall]
 * @param[s] function run when display is [Height.Small]
 * @param[m] function run when display is [Height.Medium]
 * @param[l] function run when display is [Height.Large]
 * @param[xl] function run when display is [Height.XLarge]
 */
class HeightBasedComposable(
    private val xs: @Composable (size: WindowSize) -> Unit,
    private val s: @Composable (size: WindowSize) -> Unit,
    private val m: @Composable (size: WindowSize) -> Unit,
    private val l: @Composable (size: WindowSize) -> Unit,
    private val xl: @Composable (size: WindowSize) -> Unit,
) : SizedComposable {

    /**
     * a single value to use for all screen sizes
     */
    constructor(m: @Composable (size: WindowSize) -> Unit) : this(m, m, m, m, m)

    /**
     * a two value split: one for phones (and smaller), another for tablets (and larger)
     */
    constructor(
        s: @Composable (size: WindowSize) -> Unit,
        l: @Composable (size: WindowSize) -> Unit,
    ) : this(s, s, s, l, l)

    /**
     * a three value split: watches / phones / tablets (and larger)
     */
    constructor(
        xs: @Composable (size: WindowSize) -> Unit,
        m: @Composable (size: WindowSize) -> Unit,
        l: @Composable (size: WindowSize) -> Unit,
    ) : this(xs, m, m, l, l)

    /**
     * a four value split: watches / small phones / large phones / tablets (and larger)
     */
    constructor(
        xs: @Composable (size: WindowSize) -> Unit,
        s: @Composable (size: WindowSize) -> Unit,
        m: @Composable (size: WindowSize) -> Unit,
        l: @Composable (size: WindowSize) -> Unit,
    ) : this(xs, s, m, l, l)

    @Composable
    operator fun invoke(size: WindowSize) {
        when (size.height) {
            Height.XSmall -> xs(size)
            Height.Small -> s(size)
            Height.Medium -> m(size)
            Height.Large -> l(size)
            Height.XLarge -> xl(size)
        }
    }
}

/**
 * @param[xs] function run when display is [MinDim.XSmall]
 * @param[s] function run when display is [MinDim.Small]
 * @param[m] function run when display is [MinDim.Medium]
 * @param[l] function run when display is [MinDim.Large]
 * @param[xl] function run when display is [MinDim.XLarge]
 */
class MinDimBasedComposable(
    private val xs: @Composable (size: WindowSize) -> Unit,
    private val s: @Composable (size: WindowSize) -> Unit,
    private val m: @Composable (size: WindowSize) -> Unit,
    private val l: @Composable (size: WindowSize) -> Unit,
    private val xl: @Composable (size: WindowSize) -> Unit,
) : SizedComposable {

    /**
     * a single value to use for all screen sizes
     */
    constructor(m: @Composable (size: WindowSize) -> Unit) : this(m, m, m, m, m)

    /**
     * a two value split: one for phones (and smaller), another for tablets (and larger)
     */
    constructor(
        s: @Composable (size: WindowSize) -> Unit,
        l: @Composable (size: WindowSize) -> Unit,
    ) : this(s, s, s, l, l)

    /**
     * a three value split: watches / phones / tablets (and larger)
     */
    constructor(
        xs: @Composable (size: WindowSize) -> Unit,
        m: @Composable (size: WindowSize) -> Unit,
        l: @Composable (size: WindowSize) -> Unit,
    ) : this(xs, m, m, l, l)

    /**
     * a four value split: watches / small phones / large phones / tablets (and larger)
     */
    constructor(
        xs: @Composable (size: WindowSize) -> Unit,
        s: @Composable (size: WindowSize) -> Unit,
        m: @Composable (size: WindowSize) -> Unit,
        l: @Composable (size: WindowSize) -> Unit,
    ) : this(xs, s, m, l, l)

    @Composable
    operator fun invoke(size: WindowSize) {
        when (size.minDim) {
            MinDim.XSmall -> xs(size)
            MinDim.Small -> s(size)
            MinDim.Medium -> m(size)
            MinDim.Large -> l(size)
            MinDim.XLarge -> xl(size)
        }
    }
}

/**
 * @param[port] function run when display is [Aspect.Port]
 * @param[land] function run when display is [Aspect.Land]
 * @param[squarish] function run when display is [Aspect.Squarish]
 */
class AspectBasedComposable(
    private val port: @Composable (size: WindowSize) -> Unit,
    private val land: @Composable (size: WindowSize) -> Unit = port,
    private val squarish: @Composable (size: WindowSize) -> Unit = port,
) : SizedComposable {

    @Composable
    operator fun invoke(size: WindowSize) {
        when (size.aspect) {
            Aspect.Port -> port(size)
            Aspect.Land -> land(size)
            Aspect.Squarish -> squarish(size)
        }
    }
}

/**
 * @param[value] composable to be used in all cases
 */
class FixedComposable(
    private val value: @Composable (size: WindowSize) -> Unit,
) : SizedComposable {
    @Composable
    operator fun invoke(size: WindowSize) {
        value(size)
    }
}
