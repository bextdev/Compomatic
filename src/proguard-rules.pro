# Add any ProGuard configurations specific to this
# extension here.

-keep public class ph.bxtdev.Compomatic.Compomatic {
    public *;
 }
-keeppackagenames gnu.kawa**, gnu.expr**

-optimizationpasses 4
-allowaccessmodification
-mergeinterfacesaggressively

-repackageclasses 'ph/bxtdev/Compomatic/repack'
-flattenpackagehierarchy
-dontpreverify
