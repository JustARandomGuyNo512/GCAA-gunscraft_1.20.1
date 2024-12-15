package sheridan.gcaa.service.product;

import com.google.gson.JsonObject;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.TagParser;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import sheridan.gcaa.GCAA;

public class NBTAttachedProduct extends CommonProduct{
    private CompoundTag tag;
    public NBTAttachedProduct(Item item, int price, CompoundTag tag) {
        super(item, price);
        this.tag = tag;
    }

    public CompoundTag getTag() {
        return tag;
    }

    @Override
    public ItemStack getItemStack(int count) {
        ItemStack res = super.getItemStack(count);
        res.setTag(tag);
        return res;
    }

    @Override
    public void writeData(JsonObject jsonObject) {
        super.writeData(jsonObject);
        jsonObject.addProperty("nbt", tag.toString());
    }

    @Override
    public void loadData(JsonObject jsonObject) {
        super.loadData(jsonObject);
        if (jsonObject.has("nbt")) {
            String nbt = jsonObject.get("nbt").getAsString();
            try {
                this.tag = TagParser.parseTag(nbt);
            } catch (Exception e) {
                GCAA.LOGGER.info("Failed to load NBT from json: " + nbt + "in loading NBTAttachedProduct item: " + this.item);
            }
        }
    }
}
