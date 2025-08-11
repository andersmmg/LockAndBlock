package com.andersmmg.lockandblock.client.render;

import com.andersmmg.lockandblock.LockAndBlock;
import com.andersmmg.lockandblock.block.custom.LaserBlock;
import com.andersmmg.lockandblock.block.entity.LaserBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public class LaserBlockEntityRenderer implements BlockEntityRenderer<LaserBlockEntity> {
    public static final Identifier BEAM_TEXTURE = new Identifier(LockAndBlock.MOD_ID, "textures/entity/laser.png");

    public LaserBlockEntityRenderer(BlockEntityRendererFactory.Context ignoredContext) {
    }

    public static void renderBeam(MatrixStack matrices, VertexConsumerProvider vertexConsumers, Identifier textureId, int yOffset, int maxY, float[] color, float radius, Direction direction, float alpha) {
        int i = yOffset + maxY;
        matrices.push();
        matrices.translate(0.5F, 0.0F, (double) 0.5F);
        switch (direction) {
            case DOWN:
                matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(180.0F));
                matrices.translate(0.0D, -1.0D, 0.0D);
                break;
            case UP:
                break;
            case NORTH:
                matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-90.0F));
                matrices.translate(0.0D, -0.5D, 0.5D);
                break;
            case SOUTH:
                matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(90.0F));
                matrices.translate(0.0D, -0.5D, -0.5D);
                break;
            case WEST:
                matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(90.0F));
                matrices.translate(0.5D, -0.5D, 0.0D);
                break;
            case EAST:
                matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(-90.0F));
                matrices.translate(-0.5D, -0.5D, 0.0D);
                break;
        }
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(45.0F));
        renderBeamLayer(matrices, vertexConsumers.getBuffer(RenderLayer.getBeaconBeam(textureId, true)), color[0], color[1], color[2], alpha, yOffset, i, radius, radius, -radius, -radius);
        matrices.pop();
    }

    private static void renderBeamLayer(MatrixStack matrices, VertexConsumer vertices, float red, float green, float blue, float alpha, int yOffset, int height, float z1, float x2, float x3, float z4) {
        MatrixStack.Entry entry = matrices.peek();
        Matrix4f matrix4f = entry.getPositionMatrix();
        Matrix3f matrix3f = entry.getNormalMatrix();
        renderBeamFace(matrix4f, matrix3f, vertices, red, green, blue, alpha, yOffset, height, (float) 0.0, z1, x2, (float) 0.0);
        renderBeamFace(matrix4f, matrix3f, vertices, red, green, blue, alpha, yOffset, height, (float) 0.0, z4, x3, (float) 0.0);
        renderBeamFace(matrix4f, matrix3f, vertices, red, green, blue, alpha, yOffset, height, x2, (float) 0.0, (float) 0.0, z4);
        renderBeamFace(matrix4f, matrix3f, vertices, red, green, blue, alpha, yOffset, height, x3, (float) 0.0, (float) 0.0, z1);
    }

    private static void renderBeamFace(Matrix4f positionMatrix, Matrix3f normalMatrix, VertexConsumer vertices, float red, float green, float blue, float alpha, int yOffset, int height, float x1, float z1, float x2, float z2) {
        renderBeamVertex(positionMatrix, normalMatrix, vertices, red, green, blue, alpha, height, x1, z1, (float) 1.0, (float) 1.0);
        renderBeamVertex(positionMatrix, normalMatrix, vertices, red, green, blue, alpha, yOffset, x1, z1, (float) 1.0, (float) -1.0);
        renderBeamVertex(positionMatrix, normalMatrix, vertices, red, green, blue, alpha, yOffset, x2, z2, (float) 0.0, (float) -1.0);
        renderBeamVertex(positionMatrix, normalMatrix, vertices, red, green, blue, alpha, height, x2, z2, (float) 0.0, (float) 1.0);
    }

    private static void renderBeamVertex(Matrix4f positionMatrix, Matrix3f normalMatrix, VertexConsumer vertices, float red, float green, float blue, float alpha, int y, float x, float z, float u, float v) {
        vertices.vertex(positionMatrix, x, (float) y, z).color(red, green, blue, alpha).texture(u, v).overlay(OverlayTexture.DEFAULT_UV).light(15728880).normal(normalMatrix, 0.0F, 1.0F, 0.0F).next();
    }

    @Override
    public void render(LaserBlockEntity entity, float tickDelta, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int light, int overlay) {
        if (!LockAndBlock.CONFIG.laserCustomRender()) return;
        if (entity.isActive(entity.getCachedState())) {
            int totalDistance = LaserBlockEntity.getDistance(entity);
            if (totalDistance < LockAndBlock.CONFIG.maxLaserDistance()) {
                renderSection(entity, matrixStack, vertexConsumerProvider, 0, totalDistance, 1.0f);
                return;
            }
            int firstSectionDistance = totalDistance > 4 ? totalDistance - 4 : totalDistance;
            renderSection(entity, matrixStack, vertexConsumerProvider, 0, firstSectionDistance + 1, 1.0f);
            for (int i = 1; i < 4; i++) {
                float alpha = (float) (totalDistance - i) / (totalDistance);
                alpha *= (float) (4 - i) / 4;
                renderSection(entity, matrixStack, vertexConsumerProvider, firstSectionDistance + i, 1, alpha);
            }
        }
    }

    public void renderSection(LaserBlockEntity entity, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int yOffset, int maxY, float alpha) {
        float[] color = argbToFloat(LaserBlockEntity.getColor(entity.getCachedState()));
        Direction direction = entity.getCachedState().get(LaserBlock.FACING);
        // Increase beam radius with distance, for better view
        double distance = 0;
        if (MinecraftClient.getInstance().cameraEntity != null) {
            distance = getDistance(entity, MinecraftClient.getInstance().cameraEntity.getPos());
        }
        double radius = 0.03F + 0.0005F * distance;
        renderBeam(matrixStack, vertexConsumerProvider, BEAM_TEXTURE, yOffset, maxY, color, (float) radius, direction, alpha);
    }

    private float[] argbToFloat(int color) {
        float[] floatColor = new float[3];
        floatColor[0] = (float) (color >> 16 & 0xFF) / 255.0F;
        floatColor[1] = (float) (color >> 8 & 0xFF) / 255.0F;
        floatColor[2] = (float) (color & 0xFF) / 255.0F;
        return floatColor;
    }

    @Override
    public boolean rendersOutsideBoundingBox(LaserBlockEntity blockEntity) {
        return true;
    }

    @Override
    public int getRenderDistance() {
        return 128;
    }

    @Override
    public boolean isInRenderDistance(LaserBlockEntity blockEntity, Vec3d pos) {
        return Vec3d.ofCenter(blockEntity.getPos()).multiply(1.0F, 0.0F, 1.0F).isInRange(pos.multiply(1.0F, 0.0F, 1.0F), this.getRenderDistance());
    }

    public double getDistance(LaserBlockEntity blockEntity, Vec3d pos) {
        return Vec3d.ofCenter(blockEntity.getPos()).multiply(1.0F, 0.0F, 1.0F).distanceTo(pos.multiply(1.0F, 0.0F, 1.0F));
    }
}
