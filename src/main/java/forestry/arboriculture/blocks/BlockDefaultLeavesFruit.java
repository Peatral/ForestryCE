package forestry.arboriculture.blocks;

import javax.annotation.Nullable;
import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.phys.BlockHitResult;

import com.mojang.authlib.GameProfile;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.items.ItemHandlerHelper;

import forestry.api.arboriculture.ILeafSpriteProvider;
import forestry.api.arboriculture.genetics.IFruit;
import forestry.api.arboriculture.genetics.ITree;
import forestry.api.arboriculture.genetics.TreeLifeStage;
import forestry.api.genetics.IGenome;
import forestry.api.genetics.alleles.TreeChromosomes;
import forestry.arboriculture.features.ArboricultureBlocks;
import forestry.core.utils.BlockUtil;

/**
 * Genetic leaves with no tile entity, used for worldgen trees.
 * Similar to decorative leaves, but these will drop saplings and can be used for pollination.
 */
public class BlockDefaultLeavesFruit extends BlockAbstractLeaves {
	private final ForestryLeafType leafType;

	public BlockDefaultLeavesFruit(ForestryLeafType leafType) {
		this.leafType = leafType;
	}

	@Override
	public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult traceResult) {
		ItemStack mainHand = player.getItemInHand(InteractionHand.MAIN_HAND);
		ItemStack offHand = player.getItemInHand(InteractionHand.OFF_HAND);
		if (mainHand.isEmpty() && offHand.isEmpty()) {
			BlockUtil.sendDestroyEffects(level, pos, state);
			ITree tree = getTree(level, pos);
			if (tree == null) {
				return InteractionResult.FAIL;
			}
			IFruit fruitProvider = tree.getGenome().getActiveValue(TreeChromosomes.FRUITS);
			List<ItemStack> products = tree.produceStacks(level, pos, fruitProvider.getRipeningPeriod());
			level.setBlock(pos, ArboricultureBlocks.LEAVES_DEFAULT.get(leafType).defaultState()
					.setValue(LeavesBlock.PERSISTENT, state.getValue(LeavesBlock.PERSISTENT))
					.setValue(LeavesBlock.DISTANCE, state.getValue(LeavesBlock.DISTANCE)), Block.UPDATE_CLIENTS);
			for (ItemStack fruit : products) {
				ItemHandlerHelper.giveItemToPlayer(player, fruit);
			}
			return InteractionResult.SUCCESS;
		}

		return InteractionResult.PASS;
	}

	@Override
	protected void getLeafDrop(NonNullList<ItemStack> drops, Level world, @Nullable GameProfile playerProfile, BlockPos pos, float saplingModifier, int fortune, LootContext.Builder builder) {
		ITree tree = getTree(world, pos);
		if (tree == null) {
			return;
		}

		// Add saplings
		List<ITree> saplings = tree.getSaplings(world, playerProfile, pos, saplingModifier);
		for (ITree sapling : saplings) {
			if (sapling != null) {
				drops.add(sapling.copyWithStage(TreeLifeStage.SAPLING));
			}
		}

		// Add fruits
		IGenome genome = tree.getGenome();
		IFruit fruitProvider = genome.getActiveValue(TreeChromosomes.FRUITS);
		if (fruitProvider.isFruitLeaf(genome, world, pos)) {
			List<ItemStack> produceStacks = tree.produceStacks(world, pos, Integer.MAX_VALUE);
			drops.addAll(produceStacks);
		}
	}

	public ForestryLeafType getLeafType() {
		return this.leafType;
	}

	@Override
	protected ITree getTree(BlockGetter world, BlockPos pos) {
		return this.leafType.getIndividual();
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public int colorMultiplier(BlockState state, @Nullable BlockGetter level, @Nullable BlockPos pos, int tintIndex) {
		if (tintIndex == BlockAbstractLeaves.FRUIT_COLOR_INDEX) {
			IFruit genome = leafType.getFruit();
			return genome.getDecorativeColor();
		}

		ILeafSpriteProvider spriteProvider = leafType.getLeafSpriteProvider();
		return spriteProvider.getColor(false);
	}
}
