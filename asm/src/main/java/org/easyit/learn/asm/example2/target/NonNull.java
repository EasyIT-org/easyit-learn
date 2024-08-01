package org.easyit.learn.asm.example2.target;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

@Target({ElementType.TYPE_USE,
         ElementType.TYPE_PARAMETER})
@interface NonNull {}
