package net.malisis.doors.block;

import net.malisis.core.util.EntityUtils;
import net.malisis.doors.MalisisDoors;
import net.malisis.doors.entity.BlockMixerTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockMixer extends BlockContainer
{
	private IIcon frontIcon;

	public BlockMixer()
	{
		super(Material.iron);
		this.setCreativeTab(CreativeTabs.tabRedstone);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerBlockIcons(IIconRegister iconRegister)
	{
		this.blockIcon = iconRegister.registerIcon(MalisisDoors.modid + ":" + (this.getUnlocalizedName().substring(5)) + "_side");
		this.frontIcon = iconRegister.registerIcon(MalisisDoors.modid + ":" + (this.getUnlocalizedName().substring(5)));
	}

	public IIcon getIcon(int side, int metadata)
	{
		if ((metadata != 0 && side == metadata) || (metadata == 0 && side == 3))
			return frontIcon;
		return blockIcon;
	}

	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase player, ItemStack itemStack)
	{
		int side = MathHelper.floor_double((double) (player.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
		int metadata = 0;
		if (side == 0)
			metadata = 2;
		if (side == 1)
			metadata = 5;
		if (side == 2)
			metadata = 3;
		if (side == 3)
			metadata = 4;
		world.setBlockMetadataWithNotify(x, y, z, metadata, 2);
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int metadata, float hitX, float hitY, float hitZ)
	{
		if(world.isRemote)
			return true;
		
		TileEntity tileEntity = world.getTileEntity(x, y, z);
		if (tileEntity == null || player.isSneaking())
			return false;
		
		player.openGui(MalisisDoors.instance, 0, world, x, y, z);
		return true;
	}

	@Override
	public void breakBlock(World world, int x, int y, int z, Block block, int metadata)
	{
		dropItems(world, x, y, z);
		super.breakBlock(world, x, y, z, block, metadata);
	}

	private void dropItems(World world, int x, int y, int z)
	{
		TileEntity tileEntity = world.getTileEntity(x, y, z);
		if (!(tileEntity instanceof IInventory))
			return;

		IInventory inventory = (IInventory) tileEntity;

		for (int i = 0; i < inventory.getSizeInventory(); i++)
		{
			ItemStack itemStack = inventory.getStackInSlot(i);

			if (itemStack != null && itemStack.stackSize > 0)
			{
				EntityUtils.spawnEjectedItem(world, x, y, z, itemStack);
				itemStack.stackSize = 0;
			}
		}
	}

	@Override
	public TileEntity createNewTileEntity(World world, int metdata)
	{
		return new BlockMixerTileEntity();
	}
}