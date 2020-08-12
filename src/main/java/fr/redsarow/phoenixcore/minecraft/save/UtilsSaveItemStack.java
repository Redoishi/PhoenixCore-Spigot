package fr.redsarow.phoenixcore.minecraft.save;

import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @author redsarow
 * @since 1.0
 */
public class UtilsSaveItemStack {


    public static String toString(ItemStack[] itemStacks) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);

            dataOutput.writeInt(itemStacks.length);

            for (ItemStack itemStack : itemStacks) {
                dataOutput.writeObject(itemStack);
            }

            dataOutput.close();
            return Base64Coder.encodeLines(outputStream.toByteArray());
        } catch (Exception e) {
            throw new IllegalStateException("Unable to save item stacks.", e);
        }
    }

    public static ItemStack[] toObject(String data) throws IOException {
        if (data == null) {
            return new ItemStack[0];
        }
        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(data));
            BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
            ItemStack[] itemStacks = new ItemStack[dataInput.readInt()];

            for (int i = 0; i < itemStacks.length; i++) {
                itemStacks[i] = (ItemStack) dataInput.readObject();
            }
            dataInput.close();
            return itemStacks;
        } catch (ClassNotFoundException e) {
            throw new IOException("Unable to decode class type.", e);
        }
    }
}
