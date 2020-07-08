package de.nordgedanken.auto_hotkey.types.ty

import com.intellij.psi.PsiElement

/**
 * These are "atomic" ty (not type constructors, singletons).
 *
 * Definition intentionally differs from the reference: we don't treat
 * tuples or arrays as primitive.
 */
abstract class TyPrimitive : Ty() {

    abstract val name: String
}
object TyStr : TyPrimitive() {
    override val name: String = "str"
}
abstract class TyNumeric : TyPrimitive()

sealed class TyInteger(override val name: String, val ordinal: Int) : TyNumeric() {

    // This fixes NPE caused by java classes initialization order. Details:
    // Kotlin `object`s compile into java classes with `INSTANCE` static field
    // and `companion object` fields compile into static field of the host class.
    // Our objects (`U8`, `U16` etc) are extend `TyInteger` class.
    // In java, parent classes are initialized first. So, if we accessing,
    // for example, `U8` object first, we really accessing `U8.INSTANCE` field,
    // that requests to initialize `U8` class, that requests to initialize
    // `TyInteger` before. Then, when we initializing `TyInteger`, `U8` is not
    // initialized and `U8.INSTANCE` is null. So if `VALUES` is a field of
    // `TyInteger` class, it will be filled with null value instead of `U8`
    // We fixing it by moving fields from `companion object` an independent object
    private object TyIntegerValuesHolder {
        val DEFAULT = TyInteger.I32
        val VALUES = listOf(U8, U16, U32, U64, U128, USize, I8, I16, I32, I64, I128, ISize)
        val NAMES = VALUES.map { it.name }
    }

    companion object {
        val DEFAULT: TyInteger get() = TyIntegerValuesHolder.DEFAULT
        val VALUES: List<TyInteger> get() = TyIntegerValuesHolder.VALUES
        val NAMES: List<String> get() = TyIntegerValuesHolder.NAMES

        fun fromName(name: String): TyInteger? {
            return VALUES.find { it.name == name }
        }

        fun fromSuffixedLiteral(literal: PsiElement): TyInteger? {
            val text = literal.text
            return VALUES.find { text.endsWith(it.name) }
        }
    }

    object U8 : TyInteger("u8", 0)
    object U16 : TyInteger("u16", 1)
    object U32 : TyInteger("u32", 2)
    object U64 : TyInteger("u64", 3)
    object U128 : TyInteger("u128", 4)
    object USize : TyInteger("usize", 5)

    object I8 : TyInteger("i8", 6)
    object I16 : TyInteger("i16", 7)
    object I32 : TyInteger("i32", 8)
    object I64 : TyInteger("i64", 9)
    object I128 : TyInteger("i128", 10)
    object ISize : TyInteger("isize", 11)
}
