package io.github.abductcows.easyargs

import java.lang.annotation.ElementType
import javax.annotation.Nonnull
import javax.annotation.meta.TypeQualifierDefault


@Nonnull
@TypeQualifierDefault(ElementType.FIELD, ElementType.TYPE_USE, ElementType.METHOD, ElementType.PARAMETER)
annotation class CustomNonNullAPI