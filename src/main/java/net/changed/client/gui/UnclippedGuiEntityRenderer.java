package net.changed.client.gui;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.world.entity.LivingEntity;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public final class UnclippedGuiEntityRenderer {
    private UnclippedGuiEntityRenderer() {
    }

    public static void renderEntityInInventoryFollowsMouse(GuiGraphics graphics,
                                                           int x1,
                                                           int y1,
                                                           int x2,
                                                           int y2,
                                                           int scale,
                                                           float yOffset,
                                                           float mouseX,
                                                           float mouseY,
                                                           LivingEntity entity) {
        float centerX = (float)(x1 + x2) / 2.0F;
        float centerY = (float)(y1 + y2) / 2.0F;
        float angleX = (float)Math.atan((centerX - mouseX) / 40.0F);
        float angleY = (float)Math.atan((centerY - mouseY) / 40.0F);
        renderEntityInInventoryFollowsAngle(graphics, centerX, centerY, scale, yOffset, angleX, angleY, entity);
    }

    public static void renderEntityCenteredInInventoryFollowsMouse(GuiGraphics graphics,
                                                                   float centerX,
                                                                   float centerY,
                                                                   int scale,
                                                                   float yOffset,
                                                                   float mouseX,
                                                                   float mouseY,
                                                                   LivingEntity entity) {
        float angleX = (float)Math.atan((centerX - mouseX) / 40.0F);
        float angleY = (float)Math.atan((centerY - mouseY) / 40.0F);
        renderEntityInInventoryFollowsAngle(graphics, centerX, centerY, scale, yOffset, angleX, angleY, entity);
    }

    public static void renderEntityInInventoryFollowsAngle(GuiGraphics graphics,
                                                           float centerX,
                                                           float centerY,
                                                           int scale,
                                                           float yOffset,
                                                           float angleX,
                                                           float angleY,
                                                           LivingEntity entity) {
        Quaternionf pose = new Quaternionf().rotateZ((float)Math.PI);
        Quaternionf cameraOrientation = new Quaternionf().rotateX(angleY * 20.0F * (float)(Math.PI / 180.0));
        pose.mul(cameraOrientation);

        float yBodyRot = entity.yBodyRot;
        float yRot = entity.getYRot();
        float xRot = entity.getXRot();
        float yHeadRotO = entity.yHeadRotO;
        float yHeadRot = entity.yHeadRot;
        entity.yBodyRot = 180.0F + angleX * 20.0F;
        entity.setYRot(180.0F + angleX * 40.0F);
        entity.setXRot(-angleY * 20.0F);
        entity.yHeadRot = entity.getYRot();
        entity.yHeadRotO = entity.getYRot();

        float entityScale = entity.getScale();
        Vector3f translate = new Vector3f(0.0F, entity.getBbHeight() / 2.0F + yOffset * entityScale, 0.0F);
        InventoryScreen.renderEntityInInventory(graphics, centerX, centerY, (float)scale / entityScale, translate, pose, cameraOrientation, entity);

        entity.yBodyRot = yBodyRot;
        entity.setYRot(yRot);
        entity.setXRot(xRot);
        entity.yHeadRotO = yHeadRotO;
        entity.yHeadRot = yHeadRot;
    }
}
