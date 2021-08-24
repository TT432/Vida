package teamHTBP.vida.TileEntity;

import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import teamHTBP.vida.element.ElementHelper;
import teamHTBP.vida.element.EnumElements;
import teamHTBP.vida.element.IElement;
import teamHTBP.vida.item.ItemLoader;

import javax.annotation.Nullable;

public class TileEntityCollector extends TileEntity implements ITickableTileEntity {
    //最大收集值
    public final int MAX_COLLECTION = 20000;
    //是否在收集
    public boolean isCollect = false;
    //收集的元素的元素核心
    public ItemStack coreItem = ItemStack.EMPTY;
    public int extraSpeed = 1;
    //收集的元素
    protected IElement element;
    //收集的元素值
    protected int collection = 0;

    public TileEntityCollector() {
        super(TileEntityLoader.TileEntityCollector.get());
    }

    @Override
    public void read(BlockState state, CompoundNBT compound) {
        if (compound.contains("coreItem"))
            coreItem = ItemStack.read(compound.getCompound("coreItem"));
        isCollect = compound.getBoolean("isCollect");
        collection = compound.getInt("collectionU");
        element = ElementHelper.read(compound);
        super.read(state, compound);
    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        if (coreItem != ItemStack.EMPTY || !coreItem.isEmpty())
            compound.put("coreItem", coreItem.serializeNBT());
        compound.putInt("collectionU", collection);
        compound.putBoolean("isCollect", isCollect);
        ElementHelper.write(compound, element);
        return super.write(compound);
    }

    @Nullable
    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        return new SUpdateTileEntityPacket(this.pos, 1, this.getUpdateTag());
    }

    @Override
    public CompoundNBT getUpdateTag() {
        return this.write(new CompoundNBT());
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
        super.onDataPacket(net, pkt);
        handleUpdateTag(world.getBlockState(pos), pkt.getNbtCompound());
    }

    @Override
    public void handleUpdateTag(BlockState state, CompoundNBT tag) {
        if (tag.contains("gemItem"))
            coreItem = ItemStack.read(tag.getCompound("gemItem"));
        else
            coreItem = ItemStack.EMPTY;

        isCollect = tag.getBoolean("isCollect");
        collection = tag.getInt("collectionU");
        element = ElementHelper.read(tag);
        super.handleUpdateTag(state, tag);
        super.read(state, tag);
    }


    public boolean setCore(ItemStack itemStack) {
        if (itemStack.getItem() == ItemLoader.ELEMENTCORE_VOID.get()) {
            if (this.coreItem == ItemStack.EMPTY || this.coreItem.isEmpty()) {
                this.coreItem = itemStack;
                return true;
            } else
                return false;
        } else
            return false;

    }

    public ItemStack getCore() {
        if (this.coreItem == ItemStack.EMPTY && this.coreItem.isEmpty()) return ItemStack.EMPTY;
        else {
            return this.coreItem;
        }
    }

    //是否放入的是空的ElementCore
    public boolean hasEmptyElementCore() {
        try {
            return this.coreItem != ItemStack.EMPTY && this.coreItem.getItem() == ItemLoader.ELEMENTCORE_VOID.get();
        } catch (Exception ex) {
            return false;
        }
    }

    public void resetCollect() {
        this.collection = 0;
        this.isCollect = false;
        if (element instanceof EnumElements) {
            switch ((EnumElements) this.element) {
                case GOLD:
                    this.coreItem = new ItemStack(ItemLoader.ELEMENTCORE_GOLD.get(), 1);
                    break;
                case WOOD:
                    this.coreItem = new ItemStack(ItemLoader.ELEMENTCORE_WOOD.get(), 1);
                    break;
                case AQUA:
                    this.coreItem = new ItemStack(ItemLoader.ELEMENTCORE_AQUA.get(), 1);
                    break;
                case FIRE:
                    this.coreItem = new ItemStack(ItemLoader.ELEMENTCORE_FIRE.get(), 1);
                    break;
                case EARTH:
                    this.coreItem = new ItemStack(ItemLoader.ELEMENTCORE_EARTH.get(), 1);
                    break;
                default:
                    this.coreItem = new ItemStack(ItemLoader.ELEMENTCORE_EARTH.get(), 1);
                    break;
            }
        }
        this.element = EnumElements.NONE;


    }


    public int getCollection() {
        return collection;
    }


    @Override
    public void tick() {
        boolean flag = false;
        if (!world.isRemote) {
            if (!isCollect && hasEmptyElementCore()) {
                this.isCollect = true;
                this.element = ElementHelper.getBiomeElement(world.getBiome(pos));
                flag = true;
            } else if (isCollect && element != EnumElements.NONE) {
                this.collection += 1 * extraSpeed;
                flag = true;
            }

            if (this.collection >= this.MAX_COLLECTION) {
                resetCollect();
            }

            if ((this.coreItem == ItemStack.EMPTY || this.coreItem.isEmpty()) && this.collection >= 0 && this.isCollect) {
                this.element = EnumElements.NONE;
                this.isCollect = false;
                this.collection = 0;
                flag = true;
            }

        }

        if (flag) {
            world.notifyBlockUpdate(pos, getBlockState(), getBlockState(), 3);
            this.markDirty();
        }
    }
}
