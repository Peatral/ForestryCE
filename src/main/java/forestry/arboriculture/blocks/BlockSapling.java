/*******************************************************************************
 * Copyright (c) 2011-2014 SirSengir.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Various Contributors including, but not limited to:
 * SirSengir (original work), CovertJaguar, Player, Binnie, MysteriousAges
 ******************************************************************************/
package forestry.arboriculture.blocks;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import forestry.api.arboriculture.genetics.ITree;
import forestry.api.arboriculture.genetics.TreeLifeStage;
import forestry.arboriculture.tiles.TileSapling;
import forestry.core.tiles.TileUtil;
import forestry.core.utils.SpeciesUtil;

public class BlockSapling extends BlockTreeContainer implements BonemealableBlock {
	protected static final VoxelShape SHAPE = Block.box(2.0D, 0.0D, 2.0D, 14.0D, 12.0D, 14.0D);

	public BlockSapling() {
		super(Block.Properties.of(Material.PLANT).noCollission().strength(0.0F).sound(SoundType.GRASS));
	}

	@Override
	public VoxelShape getShape(BlockState blockState, BlockGetter blockReader, BlockPos pos, CollisionContext context) {
		return SHAPE;
	}

	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new TileSapling(pos, state);
	}

	/* PLANTING */
	public static boolean canBlockStay(BlockGetter world, BlockPos pos) {
		TileSapling tile = TileUtil.getTile(world, pos, TileSapling.class);
		if (tile == null) {
			return false;
		}

		ITree tree = tile.getTree();
		return tree != null && tree.canStay(world, pos);
	}

	@Override
	public void neighborChanged(BlockState state, Level worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean p_220069_6_) {
		super.neighborChanged(state, worldIn, pos, blockIn, fromPos, p_220069_6_);
		if (!worldIn.isClientSide && !canBlockStay(worldIn, pos)) {
			worldIn.destroyBlock(pos, true);
		}
	}

	@Override
	public List<ItemStack> getDrops(BlockState blockState, LootContext.Builder builder) {
		ItemStack drop = getDrop(builder.getParameter(LootContextParams.BLOCK_ENTITY));
		if (!drop.isEmpty()) {
			return Collections.singletonList(drop);
		}
		return Collections.emptyList();
	}

	@Override
	public ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter world, BlockPos pos, Player player) {
		TileSapling sapling = TileUtil.getTile(world, pos, TileSapling.class);
		if (sapling == null || sapling.getTree() == null) {
			return ItemStack.EMPTY;
		}
		return SpeciesUtil.TREE_TYPE.get().createStack(sapling.getTree(), TreeLifeStage.SAPLING);
	}

	private static ItemStack getDrop(BlockEntity blockEntity) {
		if (blockEntity instanceof TileSapling sapling) {
			ITree tree = sapling.getTree();
			if (tree != null) {
				return SpeciesUtil.TREE_TYPE.get().createStack(tree, TreeLifeStage.SAPLING);
			}
		}

		return ItemStack.EMPTY;
	}

	/* GROWNING */
	@Override
	public boolean isValidBonemealTarget(BlockGetter world, BlockPos pos, BlockState state, boolean isClient) {
		return true;
	}

	@Override
	public boolean isBonemealSuccess(Level world, RandomSource rand, BlockPos pos, BlockState state) {
		if (world.random.nextFloat() >= 0.45F) {
			return false;
		}
		TileSapling saplingTile = TileUtil.getTile(world, pos, TileSapling.class);
		return saplingTile == null || saplingTile.canAcceptBoneMeal(rand);
	}

	@Override
	public void performBonemeal(ServerLevel world, RandomSource rand, BlockPos pos, BlockState blockState) {
		TileSapling saplingTile = TileUtil.getTile(world, pos, TileSapling.class);
		if (saplingTile != null) {
			saplingTile.tryGrow(rand, true);
		}
	}
}
