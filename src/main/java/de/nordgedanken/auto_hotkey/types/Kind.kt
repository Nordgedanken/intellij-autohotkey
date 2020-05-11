package de.nordgedanken.auto_hotkey.types

import de.nordgedanken.auto_hotkey.types.ty.Ty

typealias TypeFlags = Int

interface Kind {
    val flags: TypeFlags
}

interface TypeFolder {
    fun foldTy(ty: Ty): Ty = ty
}


interface TypeVisitor {
    fun visitTy(ty: Ty): Boolean = false
}
