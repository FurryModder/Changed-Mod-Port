package net.changed.client.tfanimations;

import com.mojang.datafixers.util.Pair;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import org.joml.Vector3fc;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

public class SplittingSource {
    private static final BiConsumer<EntityGeometry.Cube, EntityGeometry.Cube> DONT_CARE = (t, u) -> {};

    private final Vector3fc originalWantedMin; // The original min this source was created to want
    private final Vector3fc originalWantedMax; // The original max this source was created to want
    private final EntityGeometry.Cube originalSource;
    private EntityGeometry.Cube source = null; // The current cube this source was split to
    private @NotNull BiConsumer<EntityGeometry.Cube, EntityGeometry.Cube> resizeConsumer = DONT_CARE;

    private Direction.Axis splitAxis = null;
    private List<SplittingSource> sources = null;
    private boolean clonedFlag = false; // Indicates this source was actually cloned, to properly compute mass
    private boolean unclaimed = false;
    private boolean parentCube = false;

    public SplittingSource(EntityGeometry.Cube source, Vector3fc originalWantedMin, Vector3fc originalWantedMax) {
        this.originalWantedMin = originalWantedMin;
        this.originalWantedMax = originalWantedMax;
        this.originalSource = source;
        this.source = source;
    }

    public void setResizeConsumer(@Nullable BiConsumer<EntityGeometry.Cube, EntityGeometry.Cube> consumer) {
        if (consumer == null)
            this.resizeConsumer = DONT_CARE;
        else
            this.resizeConsumer = consumer;
    }

    public @Nullable SplittingSource findSourceFor(EntityGeometry.Cube cube) {
        if (this.source == cube)
            return this;
        if (sources == null)
            return null;
        for (var source : sources) {
            var found = source.findSourceFor(cube);
            if (found != null)
                return found;
        }
        return null;
    }

    private static class Empty extends SplittingSource {
        private Empty() {
            super(null, new Vector3f(0.0f, 0.0f, 0.0f), new Vector3f(0.0f, 0.0f, 0.0f));
        }

        @Override
        protected float rateSplitDesireLocal(Vector3fc wantedMin, Vector3fc wantedMax) {
            return Float.MAX_VALUE;
        }

        @Override
        public float rateSplitDesire(Vector3fc wantedMin, Vector3fc wantedMax) {
            return Float.MAX_VALUE;
        }

        @Override
        public boolean isEmpty() {
            return true;
        }

        @Override
        public EntityGeometry.Cube splitFor(Vector3fc wantedMin, Vector3fc wantedMax, BiConsumer<EntityGeometry.Cube, EntityGeometry.Cube> resizeConsumer) {
            throw new IllegalStateException("Attempting to split an empty source");
        }
    }

    private static final SplittingSource EMPTY = new Empty();

    boolean isLeaf() {
        return sources == null;
    }

    public static SplittingSource empty() {
        return EMPTY;
    }

    public boolean isEmpty() {
        return false;
    }

    public static SplittingSource shallowCopy(SplittingSource source) {
        var splittingSource = new SplittingSource(source.originalSource, source.originalWantedMin, source.originalWantedMax);
        splittingSource.resizeConsumer = source.resizeConsumer;
        splittingSource.source = source.source;
        return splittingSource;
    }

    public static SplittingSource forSplit(EntityGeometry.Cube source,
                                           Vector3fc wantedMin, Vector3fc wantedMax,
                                           BiConsumer<EntityGeometry.Cube, EntityGeometry.Cube> resizeConsumer) {
        var splittingSource = new SplittingSource(source, wantedMin, wantedMax);
        splittingSource.resizeConsumer = resizeConsumer;
        return splittingSource;
    }

    public static SplittingSource forCube(EntityGeometry.Cube source, BiConsumer<EntityGeometry.Cube, EntityGeometry.Cube> resizeConsumer) {
        var splittingSource = new SplittingSource(source, source.getMin(), source.getMax());
        splittingSource.resizeConsumer = resizeConsumer;
        return splittingSource;
    }

    public static SplittingSource forSourceCubes(List<EntityGeometry.Cube> cubes) {
        if (cubes.isEmpty())
            return empty();
        if (cubes.size() == 1) {
            var splittingSource = SplittingSource.forCube(cubes.get(0), DONT_CARE);
            splittingSource.unclaimed = true;
            splittingSource.parentCube = true;
            return splittingSource;
        }

        var splittingSource = new SplittingSource(null, new Vector3f(0.0f, 0.0f, 0.0f), new Vector3f(0.0f, 0.0f, 0.0f));
        splittingSource.sources = new ArrayList<>(cubes.size());
        for (var cube : cubes) {
            var source = SplittingSource.forCube(cube, DONT_CARE);
            source.unclaimed = true;
            source.parentCube = true;
            splittingSource.sources.add(source);
        }
        return splittingSource;
    }

    public static SplittingSource forCubes(List<Pair<EntityGeometry.Cube, BiConsumer<EntityGeometry.Cube, EntityGeometry.Cube>>> cubes) {
        var splittingSource = new SplittingSource(null, new Vector3f(0.0f, 0.0f, 0.0f), new Vector3f(0.0f, 0.0f, 0.0f));
        splittingSource.sources = new ArrayList<>(cubes.size());
        for (var cube : cubes) {
            splittingSource.sources.add(SplittingSource.forCube(cube.getFirst(), cube.getSecond()));
        }
        return splittingSource;
    }

    public static SplittingSource forSources(SplittingSource... sources) {
        return forSources(List.of(sources));
    }

    public static SplittingSource forSources(List<SplittingSource> sources) {
        List<SplittingSource> view = new ArrayList<>(sources);
        for (int i = 0; i < view.size(); i++)
            if (view.get(i).isEmpty())
                view.remove(i--);
        if (view.isEmpty())
            return empty();
        if (view.size() == 1)
            return view.get(0);

        var splittingSource = new SplittingSource(null, new Vector3f(0.0f, 0.0f, 0.0f), new Vector3f(0.0f, 0.0f, 0.0f));
        splittingSource.sources = new ArrayList<>(view.size());
        splittingSource.sources.addAll(view);
        return splittingSource;
    }

    private EntityGeometry.Cube cloneFor(Vector3fc wantedMin, Vector3fc wantedMax,
                                         BiConsumer<EntityGeometry.Cube, EntityGeometry.Cube> resizeConsumer) {
        if (!isLeaf() && !this.clonedFlag)
            throw new IllegalStateException("cloneFor() called on a splitting source that is not a leaf node, or a previously cloned source");

        if (isLeaf()) {
            this.clonedFlag = true;

            if (this.unclaimed) {
                EntityGeometry.Cube resultCube = new EntityGeometry.Cube(this.source);

                sources = new ArrayList<>(1);

                sources.add(SplittingSource.forSplit(resultCube, wantedMin, wantedMax, resizeConsumer));

                this.unclaimed = false;
                this.resizeConsumer = DONT_CARE;

                return resultCube;
            }

            // TODO delegate clamp call
            EntityGeometry.Cube resultCube = new EntityGeometry.Cube(this.source);//.clampToFit(wantedMin, wantedMax);

            sources = new ArrayList<>(this.unclaimed ? 1 : 2);

            sources.add(SplittingSource.shallowCopy(this));
            sources.add(SplittingSource.forSplit(resultCube, wantedMin, wantedMax, resizeConsumer));

            this.resizeConsumer = DONT_CARE;

            return resultCube;
        } else {
            EntityGeometry.Cube resultCube = new EntityGeometry.Cube(this.source);//.clampToFit(wantedMin, wantedMax);

            this.sources.add(SplittingSource.forSplit(resultCube, wantedMin, wantedMax, resizeConsumer));

            return resultCube;
        }
    }

    private void recloneDownStream() {
        if (!this.clonedFlag)
            throw new IllegalStateException("recloneDownStream() called on a splitting source that is not a previously cloned source");

        for (int i = 0; i < sources.size(); i++) {
            var source = sources.get(i);
            if (i == 0) {
                source.acceptNewSize(this.source);
            } else {
                EntityGeometry.Cube resultCube = new EntityGeometry.Cube(this.source).clampToFit(source.originalWantedMin, source.originalWantedMax);
                source.acceptNewSize(resultCube);
            }
        }
    }

    private void resizeDownStream() {
        if (this.splitAxis == null)
            throw new IllegalStateException("resizeDownStream() called on a splitting source that has a null splitAxis");

        float remainingMass = 0.0f;
        List<Pair<Float, SplittingSource>> masses = new ArrayList<>(sources.size() + 1);
        for (var source : sources) {
            float mass = source.getWantedMass();
            remainingMass += mass;
            masses.add(Pair.of(mass, source));
        }

        var splittingCube = this.source;
        for (var mass : masses) {
            if (masses.get(masses.size() - 1) == mass) { // Last
                mass.getSecond().acceptNewSize(splittingCube);
                continue;
            }

            var splits = splittingCube.splitOnAxis(splitAxis, mass.getFirst() / remainingMass);
            var lower = splits.getFirst();
            var higher = splits.getSecond();
            remainingMass -= mass.getFirst();

            mass.getSecond().acceptNewSize(lower);

            splittingCube = higher;
        }
    }

    private EntityGeometry.Cube resizeFor(Vector3fc wantedMin, Vector3fc wantedMax,
                                          BiConsumer<EntityGeometry.Cube, EntityGeometry.Cube> resizeConsumer) {
        if (this.splitAxis == null)
            throw new IllegalStateException("resizeFor() called on a splitting source that has a null splitAxis");

        Vector3f wantedSize = new Vector3f();
        Vector3f wantedCenter = new Vector3f();
        wantedMax.sub(wantedMin, wantedSize);
        wantedMax.add(wantedMin, wantedCenter).mul(0.5f);
        float wantedMass = wantedSize.x * wantedSize.y * wantedSize.z;

        float remainingMass = wantedMass;
        List<Pair<Float, SplittingSource>> masses = new ArrayList<>(sources.size() + 1);
        for (var source : sources) {
            float mass = source.getWantedMass();
            remainingMass += mass;
            masses.add(Pair.of(mass, source));
        }

        int insertIndex = -1;
        for (int i = 0; i < sources.size(); i++) {
            Vector3f deltaCenter = sources.get(i).getWantedCenter().sub(wantedCenter);
            // Resize splits are ordered from lowest to highest on the axis, so find the first split that is above wanted and insert before
            if ((float)splitAxis.choose(deltaCenter.x, deltaCenter.y, deltaCenter.z) > 0.0f) {
                insertIndex = i;
                masses.add(insertIndex, Pair.of(wantedMass, null));
                break;
            }
        }

        if (insertIndex == -1) {
            insertIndex = masses.size();
            masses.add(insertIndex, Pair.of(wantedMass, null));
        }

        EntityGeometry.Cube resultCube = null;
        var splittingCube = this.source;
        for (var mass : masses) {
            if (masses.get(masses.size() - 1) == mass) { // Last
                if (mass.getSecond() != null) {
                    mass.getSecond().acceptNewSize(splittingCube);
                } else {
                    resultCube = splittingCube;
                    var subSource = SplittingSource.forSplit(splittingCube, wantedMin, wantedMax, resizeConsumer);
                    sources.add(insertIndex, subSource);
                }

                continue;
            }

            var splits = splittingCube.splitOnAxis(splitAxis, mass.getFirst() / remainingMass);
            var lower = splits.getFirst();
            var higher = splits.getSecond();
            remainingMass -= mass.getFirst();

            if (mass.getSecond() != null) {
                mass.getSecond().acceptNewSize(lower);
            } else {
                resultCube = lower;
                var subSource = SplittingSource.forSplit(lower, wantedMin, wantedMax, resizeConsumer);
                sources.add(insertIndex, subSource);
            }

            splittingCube = higher;
        }

        return resultCube;
    }

    private void acceptNewSize(EntityGeometry.Cube newCube) {
        resizeConsumer.accept(this.source, newCube);
        this.source = newCube;

        if (this.clonedFlag) {
            this.recloneDownStream();
        } else if (this.splitAxis != null) {
            this.resizeDownStream();
        }
    }

    private static final float AXIS_THRESHOLD = 2.0f;
    private static final float PLANAR_RATIO_THRESHOLD = 0.35f;
    private static final float PLANAR_SIZE_RATIO_THRESHOLD = 0.6f;

    private @Nullable Direction.Axis findBestAxisLocal(Vector3fc wantedMin, Vector3fc wantedMax) {
        Vector3f deltaMin = new Vector3f();
        Vector3f deltaMax = new Vector3f();
        wantedMin.sub(originalWantedMin, deltaMin);
        wantedMax.sub(originalWantedMax, deltaMax);

        Vector3f thisSize = new Vector3f();
        this.source.getMax().sub(this.source.getMin(), thisSize);

        Vector3f originalWantedSize = new Vector3f();
        originalWantedMax.sub(originalWantedMin, originalWantedSize);
        Vector3f wantedSize = new Vector3f();
        wantedMax.sub(wantedMin, wantedSize);

        Vector3f movement = new Vector3f();
        deltaMax.add(deltaMin, movement).absolute();
        
        float thisXZRatio = thisSize.z <= 0.001f ? -1f : thisSize.x() / thisSize.z();
        float thisYXRatio = thisSize.x <= 0.001f ? -1f : thisSize.y() / thisSize.x();
        float thisYZRatio = thisSize.z <= 0.001f ? -1f : thisSize.y() / thisSize.z();
        float wantedXZRatio = wantedSize.z <= 0.001f ? -1f : wantedSize.x() / wantedSize.z();
        float wantedYXRatio = wantedSize.x <= 0.001f ? -1f : wantedSize.y() / wantedSize.x();
        float wantedYZRatio = wantedSize.z <= 0.001f ? -1f : wantedSize.y() / wantedSize.z();

        float originalWantedXZSize = originalWantedSize.x() * originalWantedSize.z();
        float originalWantedYXSize = originalWantedSize.y() * originalWantedSize.x();
        float originalWantedYZSize = originalWantedSize.y() * originalWantedSize.z();
        float wantedXZSize = wantedSize.x() * wantedSize.z();
        float wantedYXSize = wantedSize.y() * wantedSize.x();
        float wantedYZSize = wantedSize.y() * wantedSize.z();

        float sizeXZRatio = Math.min(originalWantedXZSize, wantedXZSize) / Math.max(originalWantedXZSize, wantedXZSize);
        float sizeYXRatio = Math.min(originalWantedYXSize, wantedYXSize) / Math.max(originalWantedYXSize, wantedYXSize);
        float sizeYZRatio = Math.min(originalWantedYZSize, wantedYZSize) / Math.max(originalWantedYZSize, wantedYZSize);

        if (movement.y > movement.x * AXIS_THRESHOLD && movement.y > movement.z * AXIS_THRESHOLD) {
            if (wantedXZRatio > 0.0f && thisXZRatio > 0.0f &&
                    sizeXZRatio > PLANAR_SIZE_RATIO_THRESHOLD &&
                    Mth.abs(wantedXZRatio - thisXZRatio) < PLANAR_RATIO_THRESHOLD)
                return Direction.Axis.Y;
        }

        if (movement.z > movement.x * AXIS_THRESHOLD && movement.z > movement.y * AXIS_THRESHOLD) {
            if (wantedYXRatio > 0.0f && thisYXRatio > 0.0f &&
                    sizeYXRatio > PLANAR_SIZE_RATIO_THRESHOLD &&
                    Mth.abs(wantedYXRatio - thisYXRatio) < PLANAR_RATIO_THRESHOLD)
                return Direction.Axis.Z;
        }

        if (movement.x > movement.y * AXIS_THRESHOLD && movement.x > movement.z * AXIS_THRESHOLD) {
            if (wantedYZRatio > 0.0f && thisYZRatio > 0.0f &&
                    sizeYZRatio > PLANAR_SIZE_RATIO_THRESHOLD &&
                    Mth.abs(wantedYZRatio - thisYZRatio) < PLANAR_RATIO_THRESHOLD)
                return Direction.Axis.X;
        }

        return null;
    }

    /**
     * Allocates a cube for the wanted min/max
     * @param wantedMin wanted minimum
     * @param wantedMax wanted maximum
     * @param resizeConsumer consumer for the results when another cube splits down the line, where the first param is the original cube
     * @return the allocated cube
     */
    public EntityGeometry.Cube splitFor(Vector3fc wantedMin, Vector3fc wantedMax,
                  BiConsumer<EntityGeometry.Cube, EntityGeometry.Cube> resizeConsumer) {
        if (isLeaf()) {
            if (this.unclaimed) {
                return cloneFor(wantedMin, wantedMax, resizeConsumer);
            }

            // TODO maybe clone split if wanted is far from current, or if current is at full mass

            // Split on closest axis
            var axis = findBestAxisLocal(wantedMin, wantedMax);
            if (axis == null)
                return cloneFor(wantedMin, wantedMax, resizeConsumer);

            this.splitAxis = axis;
            this.sources = new ArrayList<>(2);

            sources.add(SplittingSource.shallowCopy(this));

            this.resizeConsumer = DONT_CARE;

            return resizeFor(wantedMin, wantedMax, resizeConsumer);
        } else {
            float mostWantedValue = Float.MAX_VALUE;
            SplittingSource mostWanted = null;
            for (var source : sources) {
                float value = source.rateSplitDesire(wantedMin, wantedMax);
                if (value < mostWantedValue || mostWanted == null) {
                    mostWanted = source;
                    mostWantedValue = value;
                }
            }

            if (this.rateSplitDesire(wantedMin, wantedMax) < mostWantedValue || mostWanted == null) {
                // This source wants to split the most
                if (this.clonedFlag)
                    return cloneFor(wantedMin, wantedMax, resizeConsumer);
                else {
                    /*float splitWantedMass = 0.0f;
                    for (var source : sources)
                        splitWantedMass += source.getWantedMass();

                    if ((splitWantedMass / this.getMass()) > 1.1f && mostWanted != null) { // Full split
                        return mostWanted.splitFor(wantedMin, wantedMax, resizeConsumer);
                    }*/

                    return resizeFor(wantedMin, wantedMax, resizeConsumer);
                }
            } else {
                return mostWanted.splitFor(wantedMin, wantedMax, resizeConsumer);
            }
        }
    }

    protected float rateSplitDesireLocal(Vector3fc wantedMin, Vector3fc wantedMax) {
        if (this.parentCube && !this.unclaimed)
            return Float.MAX_VALUE;
        if (this.source == null)
            return Float.MAX_VALUE;
        float bonus = 0.0f;
        if (splitAxis != null && findBestAxisLocal(wantedMin, wantedMax) == splitAxis) {
            bonus -= 10.0f;
        }

        Vector3f wantedSize = new Vector3f();
        Vector3f wantedCenter = new Vector3f();
        wantedMax.sub(wantedMin, wantedSize);
        wantedMax.add(wantedMin, wantedCenter).mul(0.5f);

        Vector3f thisMin = this.source.getMin();
        Vector3f thisMax = this.source.getMax();

        Vector3f thisSize = new Vector3f();
        Vector3f thisCenter = new Vector3f();
        thisMax.sub(thisMin, thisSize);
        thisMax.add(thisMin, thisCenter).mul(0.5f);

        float lowest = Float.MAX_VALUE;
        for (var normal : Direction.values()) {
            Vector3f wantedSurfaceCenter = new Vector3f(wantedCenter).add(normal.step().mul(wantedSize).mul(0.5f));
            Vector3f thisSurfaceCenter = new Vector3f(thisCenter).add(normal.step().mul(thisSize).mul(0.5f));

            float distance = thisSurfaceCenter.distance(wantedSurfaceCenter);
            if (distance < lowest)
                lowest = distance;
        }

        return bonus + lowest;
    }

    public float rateSplitDesire(Vector3fc wantedMin, Vector3fc wantedMax) {
        if (isLeaf()) {
            return this.rateSplitDesireLocal(wantedMin, wantedMax);
        } else {
            float lowest = Float.MAX_VALUE;
            for (var source : sources) {
                var value = source.rateSplitDesire(wantedMin, wantedMax);
                if (value < lowest)
                    lowest = value;
            }

            if (clonedFlag) {
                float local = this.rateSplitDesireLocal(wantedMin, wantedMax);
                if (local < lowest)
                    return local;
            } else if (this.splitAxis != null) {
                float local = this.rateSplitDesireLocal(wantedMin, wantedMax);
                if (local < lowest)
                    return local;
            }

            return lowest;
        }
    }

    public float getMass() {
        if (isLeaf()) {
            var max = source.getMax();
            var min = source.getMin();
            max.sub(min);
            return max.x * max.y * max.z;
        } else if (clonedFlag) {
            return sources.get(0).getMass();
        } else {
            float ttl = 0.0f;
            for (var source : sources)
                ttl += source.getMass();
            return ttl;
        }
    }

    public float getMass(Direction.Axis axis) {
        if (isLeaf()) {
            var max = source.getMax();
            var min = source.getMin();
            max.sub(min);
            return (float)axis.choose(max.x, max.y, max.z);
        } else if (clonedFlag) {
            return sources.get(0).getMass(axis);
        } else {
            float ttl = 0.0f;
            for (var source : sources)
                ttl += source.getMass(axis);
            return ttl;
        }
    }

    public float getWantedMass() {
        var size = new Vector3f();
        originalWantedMax.sub(originalWantedMin, size);
        return size.x * size.y * size.z;
    }

    private Vector3f getWantedCenter() {
        var center = new Vector3f();
        originalWantedMax.add(originalWantedMin, center).mul(0.5f);
        return center;
    }
}
