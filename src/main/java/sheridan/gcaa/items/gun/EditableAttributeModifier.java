package sheridan.gcaa.items.gun;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class EditableAttributeModifier extends AttributeModifier {
    private double amount;

    public EditableAttributeModifier(UUID pId, String pName, double pAmount, Operation pOperation) {
        super(pId, pName, pAmount, pOperation);
    }

    @Override
    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    @Override
    public @NotNull CompoundTag save() {
        CompoundTag tag = super.save();
        tag.putDouble("Amount", this.amount);
        return tag;
    }
}
