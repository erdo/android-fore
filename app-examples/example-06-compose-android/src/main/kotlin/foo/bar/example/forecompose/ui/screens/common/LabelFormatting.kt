package foo.bar.example.forecompose.ui.screens.common

import co.early.fore.ui.size.*

fun Width.toLabel(extended: Boolean = false): String {
    return when(this){
        Width.XSmall -> if (extended) { "Width:XSmall" } else "W:XS"
        Width.Small -> if (extended) { "Width:Small" } else "W:S"
        Width.Medium -> if (extended) { "Width:Medium" } else "W:M"
        Width.Large -> if (extended) { "Width:Large" } else "W:L"
        Width.XLarge -> if (extended) { "Width:XLarge" } else "W:XL"
    }
}

fun Height.toLabel(extended: Boolean = false): String {
    return when(this){
        Height.XSmall -> if (extended) { "Height:XSmall" } else "H:XS"
        Height.Small -> if (extended) { "Height:Small" } else "H:S"
        Height.Medium -> if (extended) { "Height:Medium" } else "H:M"
        Height.Large -> if (extended) { "Height:Large" } else "H:L"
        Height.XLarge -> if (extended) { "Height:XLarge" } else "H:XL"
    }
}

fun MinDim.toLabel(extended: Boolean = false): String {
    return when(this){
        MinDim.XSmall -> if (extended) { "MinDim:XSmall" } else "M:XS"
        MinDim.Small -> if (extended) { "MinDim:Small" } else "M:S"
        MinDim.Medium -> if (extended) { "MinDim:Medium" } else "M:M"
        MinDim.Large -> if (extended) { "MinDim:Large" } else "M:L"
        MinDim.XLarge -> if (extended) { "MinDim:XLarge" } else "M:XL"
    }
}

fun Aspect.toLabel(extended: Boolean = false): String {
    return when(this){
        Aspect.Port -> if (extended) { "Aspect:Port" } else "A:P"
        Aspect.Land -> if (extended) { "Aspect:Land" } else "A:L"
        Aspect.Squarish -> if (extended) { "Aspect:Squarish" } else "A:S"
    }
}

fun WindowSize.toLabel(extended: Boolean = false, multipleLines: Boolean = false): String {
    return  "${dpSize.width}:${dpSize.height}|" +
            (if (multipleLines) "\n" else "") +
            "${width.toLabel(extended)}|" +
            "${height.toLabel(extended)}|" +
            (if (multipleLines) "\n" else "") +
            "${minDim.toLabel(extended)}|" +
            aspect.toLabel(extended)
}
