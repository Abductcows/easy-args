package io.github.abductcows.easyargs.annotations;


import javax.annotation.Nonnull;
import javax.annotation.meta.TypeQualifierDefault;
import java.lang.annotation.ElementType;

@TypeQualifierDefault({
        ElementType.FIELD,
        ElementType.METHOD,
        ElementType.PARAMETER,
        ElementType.TYPE_USE
})
@Nonnull
public @interface CustomNonNullAPI {}
