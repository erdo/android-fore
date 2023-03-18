package co.early.fore.ui.size

interface SizedValue<T> {
    operator fun invoke(size: WindowSize): T
}

/**
 * @param[xs] value used when display is [Width.XSmall]
 * @param[s] value used when display is [Width.Small]
 * @param[m] value used when display is [Width.Medium]
 * @param[l] value used when display is [Width.Large]
 * @param[xl] value used when display is [Width.XLarge]
 */
class WidthBasedValue<T>(
    private val xs: T,
    private val s: T,
    private val m: T,
    private val l: T,
    private val xl: T,
) : SizedValue<T> {

    /**
     * a single value to use for all screen sizes
     */
    constructor(m: T) : this(m, m, m, m, m)

    /**
     * a two value split: one for phones (and smaller), another for tablets (and larger)
     */
    constructor(s: T, l: T) : this(s, s, s, l, l)

    /**
     * a three value split: watches / phones / tablets (and larger)
     */
    constructor(xs: T, m: T, l: T) : this(xs, m, m, l, l)

    /**
     * a four value split: watches / small phones / large phones / tablets (and larger)
     */
    constructor(xs: T, s: T, m: T, l: T) : this(xs, s, m, l, l)

    override operator fun invoke(size: WindowSize): T {
        return when (size.width) {
            Width.XSmall -> xs
            Width.Small -> s
            Width.Medium -> m
            Width.Large -> l
            Width.XLarge -> xl
        }
    }
}

/**
 * @param[xs] value used when display is [Height.XSmall]
 * @param[s] value used when display is [Height.Small]
 * @param[m] value used when display is [Height.Medium]
 * @param[l] value used when display is [Height.Large]
 * @param[xl] value used when display is [Height.XLarge]
 */
class HeightBasedValue<T>(
    private val xs: T,
    private val s: T,
    private val m: T,
    private val l: T,
    private val xl: T,
) : SizedValue<T> {

    /**
     * a single value to use for all screen sizes
     */
    constructor(m: T) : this(m, m, m, m, m)

    /**
     * a two value split: one for phones (and smaller), another for tablets (and larger)
     */
    constructor(s: T, l: T) : this(s, s, s, l, l)

    /**
     * a three value split: watches / phones / tablets (and larger)
     */
    constructor(xs: T, m: T, l: T) : this(xs, m, m, l, l)

    /**
     * a four value split: watches / small phones / large phones / tablets (and larger)
     */
    constructor(xs: T, s: T, m: T, l: T) : this(xs, s, m, l, l)

    override operator fun invoke(size: WindowSize): T {
        return when (size.height) {
            Height.XSmall -> xs
            Height.Small -> s
            Height.Medium -> m
            Height.Large -> l
            Height.XLarge -> xl
        }
    }
}

/**
 * @param[xs] value used when display is [MinDim.XSmall]
 * @param[s] value used when display is [MinDim.Small]
 * @param[m] value used when display is [MinDim.Medium]
 * @param[l] value used when display is [MinDim.Large]
 * @param[xl] value used when display is [MinDim.XLarge]
 */
class MinDimBasedValue<T>(
    private val xs: T,
    private val s: T,
    private val m: T,
    private val l: T,
    private val xl: T,
) : SizedValue<T> {

    /**
     * a single value to use for all screen sizes
     */
    constructor(m: T) : this(m, m, m, m, m)

    /**
     * a two value split: one for phones (and smaller), another for tablets (and larger)
     */
    constructor(s: T, l: T) : this(s, s, s, l, l)

    /**
     * a three value split: watches / phones / tablets (and larger)
     */
    constructor(xs: T, m: T, l: T) : this(xs, m, m, l, l)

    /**
     * a four value split: watches / small phones / large phones / tablets (and larger)
     */
    constructor(xs: T, s: T, m: T, l: T) : this(xs, s, m, l, l)

    override operator fun invoke(size: WindowSize): T {
        return when (size.minDim) {
            MinDim.XSmall -> xs
            MinDim.Small -> s
            MinDim.Medium -> m
            MinDim.Large -> l
            MinDim.XLarge -> xl
        }
    }
}

/**
 * @param[port] value used when display is [Aspect.Port]
 * @param[land] value used when display is [Aspect.Land]
 * @param[squarish] value used when display is [Aspect.Squarish]
 */
class AspectBasedValue<T>(
    private val port: T,
    private val land: T = port,
    private val squarish: T = port,
) : SizedValue<T> {
    override operator fun invoke(size: WindowSize): T {
        return when (size.aspect) {
            Aspect.Port -> port
            Aspect.Land -> land
            Aspect.Squarish -> squarish
        }
    }
}

/**
 * @param[value] value used in all cases
 */
class FixedValue<T>(
    private val value: T,
) : SizedValue<T> {
    override operator fun invoke(size: WindowSize): T {
        return value
    }
}