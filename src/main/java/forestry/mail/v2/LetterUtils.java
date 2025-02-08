package forestry.mail.v2;

import com.mojang.datafixers.util.Pair;
import forestry.api.mail.ILetter;
import forestry.mail.Letter;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

public class LetterUtils {
    public static Optional<ILetter> getLetter(ItemStack stack) {
        CompoundTag nbt = stack.getTag();
        if (nbt == null) {
            return Optional.empty();
        }
        return Letter.CODEC.decode(NbtOps.INSTANCE, nbt.getCompound("letter")).result().map(Pair::getFirst);
    }

    public static ItemStack setLetter(ItemStack stack, ILetter letter) {
        stack.getOrCreateTag().put("letter", Letter.CODEC.encodeStart(NbtOps.INSTANCE, letter).result().orElse(new CompoundTag()));
        return stack;
    }
}
