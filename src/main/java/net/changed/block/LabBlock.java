package net.changed.block;

public class LabBlock extends ChangedBlock {
    public LabBlock(Properties properties) {
        super(properties.requiresCorrectToolForDrops());
    }
}
