package gg.pufferfish.pufferfish.simd;

import jdk.incubator.vector.FloatVector;
import jdk.incubator.vector.IntVector;
import jdk.incubator.vector.VectorSpecies;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

/**
 * Basically, java is annoying and we have to push this out to its own class.
 */
public class SIMDChecker {
    public static boolean canEnable(@Nullable Logger logger) {
        try {
            if (SIMDDetection.getJavaVersion() < 17) return false;

            SIMDDetection.testRun = true;
            VectorSpecies<Integer> ISPEC = IntVector.SPECIES_PREFERRED;
            VectorSpecies<Float> FSPEC = FloatVector.SPECIES_PREFERRED;

            if (logger != null) {
                logger.info("Max SIMD vector size on this system is {} bits (int)", ISPEC.vectorBitSize());
                logger.info("Max SIMD vector size on this system is {} bits (float)", FSPEC.vectorBitSize());
            }

            if (ISPEC.elementSize() < 2 || FSPEC.elementSize() < 2) {
                if (logger != null) logger.info("SIMD is not properly supported on this system!");
                return false;
            }

            return true;
        } catch (NoClassDefFoundError | Exception ignored) {
        } // Basically, we don't do anything. This lets us detect if it's not functional and disable it.
        return false;
    }
}