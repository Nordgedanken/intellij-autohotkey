package de.nordgedanken.auto_hotkey.types.ty

import de.nordgedanken.auto_hotkey.types.Kind
import de.nordgedanken.auto_hotkey.types.TypeFlags

/**
 * The name `Ty` is short for `Type`
 */
abstract class Ty(override val flags: TypeFlags = 0) : Kind {
    /**
     * User visible string representation of a type
     */
    final override fun toString(): String = render()
}

fun Ty.render(
    level: Int = Int.MAX_VALUE,
    unknown: String = "<unknown>",
    integer: String = "{integer}",
    includeTypeArguments: Boolean = true,
    includeLifetimeArguments: Boolean = false,
    useAliasNames: Boolean = false
): String = TypeRenderer(
        unknown = unknown,
        integer = integer,
        includeTypeArguments = includeTypeArguments,
        includeLifetimeArguments = includeLifetimeArguments,
        useAliasNames = useAliasNames
).render(this, level)

private data class TypeRenderer(
    val unknown: String,
    val integer: String,
    val includeTypeArguments: Boolean,
    val includeLifetimeArguments: Boolean,
    val useAliasNames: Boolean
) {
    fun render(ty: Ty, level: Int): String {
        require(level >= 0)

        if (ty is TyPrimitive) {
            return when (ty) {
                is TyStr -> "str"
                is TyInteger -> ty.name
                else -> error("unreachable")
            }
        }

        if (level == 0) return "…"

        val render = { subTy: Ty ->
            render(subTy, level - 1)
        }

        return when (ty) {
            else -> error("unreachable")
        }
    }
}
