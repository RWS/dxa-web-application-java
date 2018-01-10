package com.sdl.dxa.javadoc;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.ProgramElementDoc;

import java.util.Arrays;

public final class Utils {

    private Utils() {
    }

    public static boolean forcesChildrenToBePublic(ClassDoc doc) {
        return doc.isInterface() || (doc.isFinal()
                && doc.constructors() != null && Arrays.stream(doc.constructors())
                .allMatch(ProgramElementDoc::isPrivate));
    }
}
