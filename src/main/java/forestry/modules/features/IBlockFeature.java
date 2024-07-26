package forestry.modules.features;

import java.util.Collection;
import java.util.Collections;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.state.properties.Property;


import forestry.api.core.IBlockProvider;

public interface IBlockFeature<B extends Block, I extends BlockItem> extends IItemFeature<I>, IBlockProvider<B, I> {
	@Override
	default Collection<B> collect() {
		return Collections.singleton(block());
	}

	@SuppressWarnings("unchecked")
	default <T extends Block> T cast() {
		return (T) block();
	}

	BlockState defaultState();

	<V extends Comparable<V>> BlockState setValue(Property<V> property, V value);
}
