package com.megatome.j2d;

public class Main {
    public static void main(String... args) {
        final String docsetName = args[0];
        final String javadocRoot = args[1];
        final String displayName = (args.length >= 3) ? args[2] : null;
        final String keyword = (args.length >= 4) ? args[3] : null;
        final String iconFilePath = (args.length >= 5) ? args[4] : null;
        final Builder builder = new Builder(docsetName, javadocRoot, displayName, keyword, iconFilePath);
        builder.build();
    }
}
