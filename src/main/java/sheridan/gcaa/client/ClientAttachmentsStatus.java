package sheridan.gcaa.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import sheridan.gcaa.attachmentSys.AttachmentSlot;
import sheridan.gcaa.attachmentSys.common.AttachmentsHandler;
import sheridan.gcaa.attachmentSys.common.AttachmentsRegister;
import sheridan.gcaa.client.model.ISlotProviderModel;
import sheridan.gcaa.client.model.attachments.IAttachmentModel;
import sheridan.gcaa.client.model.attachments.SightModel;
import sheridan.gcaa.client.model.gun.IGunModel;
import sheridan.gcaa.client.model.registry.GunModelRegister;
import sheridan.gcaa.client.render.DisplayData;
import sheridan.gcaa.items.attachments.IArmReplace;
import sheridan.gcaa.items.attachments.IAttachment;
import sheridan.gcaa.items.attachments.Scope;
import sheridan.gcaa.items.attachments.Sight;
import sheridan.gcaa.items.gun.IGun;
import sheridan.gcaa.network.PacketHandler;
import sheridan.gcaa.network.packets.c2s.SetEffectiveSightPacket;
import sheridan.gcaa.network.packets.c2s.SetScopeMagnificationPacket;
import sheridan.gcaa.utils.RenderAndMathUtils;

import java.util.*;

@OnlyIn(Dist.CLIENT)
public class ClientAttachmentsStatus {
    public IGun gun;
    public ClientWeaponStatus status;
    public ItemStack itemStack;
    public AttachmentSlot slot;
    public Map<String, AttachmentSlot> slotFlatDir;
    public List<AttachmentSlot> sights;
    public AttachmentSlot effectiveSight;
    public AttachmentSlot leftArmReplace;
    public AttachmentSlot rightArmReplace;
    public int effectiveSightIndex;
    public float sightSwitchingProgress;
    private float tempSightSwitchingProgress;
    private String lastModifiedUUID = AttachmentSlot.NONE;
    private float[] tempSightAimPos;
    private float[] sightAimPos;
    private float scopeMagnificationRate;
    private float originalScopeMagnification;

    public ClientAttachmentsStatus(ClientWeaponStatus status) {
        this.status = status;
        sights = new ArrayList<>();
        itemStack = ItemStack.EMPTY;
        sightAimPos = null;
        slotFlatDir = new HashMap<>();
        sightSwitchingProgress = 1f;
        tempSightSwitchingProgress = 1f;
    }

    public void checkAndUpdate(ItemStack stack, IGun gun, Player player) {
        itemStack = stack;
        this.gun = gun;
        String modifiedUUID = gun.getAttachmentsModifiedUUID(stack);
        if (!lastModifiedUUID.equals(modifiedUUID)) {
            slot = AttachmentsHandler.INSTANCE.getAttachmentSlots(itemStack);
            sights.clear();
            slotFlatDir.clear();
            if (effectiveSight != null && AttachmentsRegister.get(effectiveSight.getAttachmentId()) instanceof Scope scope) {
                scope.onLoseEffective();
            }
            leftArmReplace = null;
            rightArmReplace = null;
            effectiveSight = null;
            tempSightAimPos = null;
            scopeMagnificationRate = Float.NaN;
            originalScopeMagnification = Float.NaN;
            sightSwitchingProgress = 0.9f;
            tempSightSwitchingProgress = 0.9f;
            String sightUUID = gun.getEffectiveSightUUID(stack);
            if (slot != null) {
                slot.onTravel(slot -> {
                    slotFlatDir.put(slot.getId(), slot);
                    IAttachment attachment = AttachmentsRegister.get(slot.getAttachmentId());
                    if (attachment != null) {
                        if (attachment instanceof Sight) {
                            sights.add(slot);
                            if (effectiveSight == null) {
                                effectiveSight = slot;
                                effectiveSightIndex = sights.size() - 1;
                            } else {
                                if (sightUUID.equals(slot.getId())) {
                                    effectiveSight = slot;
                                    effectiveSightIndex = sights.size() - 1;
                                }
                            }
                        }
                        if (attachment instanceof IArmReplace armReplace) {
                            if (armReplace.replaceArmRender(false)) {
                                if (leftArmReplace == null) {
                                    leftArmReplace = slot;
                                } else {
                                    IArmReplace leftLast = (IArmReplace) AttachmentsRegister.get(leftArmReplace.getAttachmentId());
                                    leftArmReplace = leftLast.orderForArmRender(false) > armReplace.orderForArmRender(false) ?
                                            slot : leftArmReplace;
                                }
                            }
                            if (armReplace.replaceArmRender(true)) {
                                if (rightArmReplace == null) {
                                    rightArmReplace = slot;
                                } else {
                                    IArmReplace rightLast = (IArmReplace) AttachmentsRegister.get(rightArmReplace.getAttachmentId());
                                    rightArmReplace = rightLast.orderForArmRender(true) > armReplace.orderForArmRender(true) ?
                                            slot : rightArmReplace;
                                }
                            }
                        }
                    }
                });
            }
            lastModifiedUUID = modifiedUUID;
            updateScopeMagnification();
        }
        updateSightAimPos();
    }

    private void updateScopeMagnification() {
        if (getEffectiveSight() instanceof Scope && gun != null) {
            String id = effectiveSight.getAttachmentId();
            scopeMagnificationRate = gun.getGun().getMagnificationsRateFor(id, itemStack);
            originalScopeMagnification = scopeMagnificationRate;
        }
    }

    public float[] getLerpedSightAimPos(float particleTick) {
        if (sightAimPos != null) {
            if (tempSightAimPos != null) {
                float progress = Mth.lerp(particleTick, tempSightSwitchingProgress, sightSwitchingProgress);
                progress = RenderAndMathUtils.sLerp(progress);
                return new float[] {
                        Mth.lerp(progress, sightAimPos[0], tempSightAimPos[0]),
                        Mth.lerp(progress, sightAimPos[1], tempSightAimPos[1]),
                        Mth.lerp(progress, sightAimPos[2], tempSightAimPos[2])
                };
            } else {
                return sightAimPos;
            }
        }
        return null;
    }

    private void updateSightAimPos() {
        if (effectiveSight != null) {
            if (sightAimPos == null) {
                sightAimPos = calcSightAimPos();
            } else {
                if (sightSwitchingProgress < 1f) {
                    if (tempSightAimPos == null) {
                        tempSightAimPos = calcSightAimPos();
                    }
                    if (sightAimPos[0] == tempSightAimPos[0] && sightAimPos[1] == tempSightAimPos[1] && sightAimPos[2] == tempSightAimPos[2]) {
                        sightSwitchingProgress = 1f;
                        tempSightAimPos = null;
                        return;
                    }
                    tempSightSwitchingProgress = sightSwitchingProgress;
                    sightSwitchingProgress = Mth.clamp(sightSwitchingProgress + 0.3f, 0f, 1f);
                    if (sightSwitchingProgress == 1f) {
                        sightAimPos[0] = tempSightAimPos[0];
                        sightAimPos[1] = tempSightAimPos[1];
                        sightAimPos[2] = tempSightAimPos[2];
                    }
                } else {
                    tempSightSwitchingProgress = 1;
                    tempSightAimPos = null;
                }
            }
        } else {
            tempSightAimPos = null;
            sightAimPos = null;
        }
    }

    private float[] calcSightAimPos() {
        IGunModel gunModel = GunModelRegister.getModel(gun);
        DisplayData displayData = GunModelRegister.getDisplayData(gun);
        PoseStack poseStack = new PoseStack();
        if (displayData != null) {
            float[] floats = displayData.get(DisplayData.FIRST_PERSON_MAIN);
            poseStack.mulPose(new Quaternionf().rotateXYZ(floats[3], floats[4], floats[5]));
            poseStack.scale(floats[6], floats[7], floats[8]);
        }
        Stack<AttachmentSlot> stack = new Stack<>();
        AttachmentSlot slot = effectiveSight;
        while (slot.hasParent()) {
            stack.push(slot);
            if (slot == slot.getParent()) {
                break;
            }
            slot = slot.getParent();
        }
        while (!stack.isEmpty()) {
            slot = stack.pop();
            if (gunModel.hasSlot(slot.modelSlotName)) {
                gunModel.handleSlotTranslate(poseStack, slot.modelSlotName);
            } else {
                IAttachmentModel model = AttachmentsRegister.getModel(slot.getParent().getAttachmentId());
                if (model instanceof ISlotProviderModel slotProviderModel && slotProviderModel.hasSlot(slot.modelSlotName)) {
                    slotProviderModel.handleSlotTranslate(poseStack, slot.modelSlotName);
                }
            }
        }
        if (AttachmentsRegister.getModel(effectiveSight.getAttachmentId()) instanceof SightModel sightModel) {
            sightModel.handleCrosshairTranslation(poseStack);
        }
        Vector3f translation = poseStack.last().pose().getTranslation(new Vector3f(0, 0, 0));
        Quaternionf rot = poseStack.last().pose().getNormalizedRotation(new Quaternionf());
        return new float[]{translation.x(), translation.y(), rot.getEulerAnglesXYZ(new Vector3f()).z};
    }

    public String getEffectiveSightUUID() {
        return effectiveSight == null ? "none" : effectiveSight.getId();
    }

    public void onSwitchEffectiveSight() {
        if (sights.size() > 1) {
            effectiveSightIndex = (effectiveSightIndex + 1) % sights.size();
            effectiveSight = sights.get(effectiveSightIndex);
            sightSwitchingProgress = 0f;
            tempSightSwitchingProgress = 0;
            tempSightAimPos = null;
            PacketHandler.simpleChannel.sendToServer(new SetEffectiveSightPacket(effectiveSight.getId()));
        } else if (sights.size() == 1) {
            String sightUUID = gun.getEffectiveSightUUID(itemStack);
            if (!sightUUID.equals(sights.get(0).getId())) {
                PacketHandler.simpleChannel.sendToServer(new SetEffectiveSightPacket(sights.get(0).getId()));
            }
        }
    }

    public IAttachment getEffectiveSight() {
        return effectiveSight == null ? null : AttachmentsRegister.get(effectiveSight.getAttachmentId());
    }

    public float getScopeMagnificationRate() {
        return scopeMagnificationRate;
    }

    public boolean setScopeMagnificationRate(float magnification) {
        if (!Float.isNaN(scopeMagnificationRate)) {
            magnification = Mth.clamp(magnification, 0, 1);
            if (magnification == scopeMagnificationRate) {
                return false;
            }
            scopeMagnificationRate = magnification;
            return true;
        }
        return false;
    }

    public void sendSetScopeMagnificationPacket() {
        if (!Float.isNaN(scopeMagnificationRate) && originalScopeMagnification != scopeMagnificationRate && effectiveSight != null) {
            PacketHandler.simpleChannel.sendToServer(new SetScopeMagnificationPacket(effectiveSight.getAttachmentId(), scopeMagnificationRate));
            gun.getGun().setMagnificationsRateFor(effectiveSight.getAttachmentId(), itemStack, scopeMagnificationRate);
        }
    }
}
