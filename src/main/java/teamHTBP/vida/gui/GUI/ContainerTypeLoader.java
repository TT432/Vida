package teamHTBP.vida.gui.GUI;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import teamHTBP.vida.TileEntity.SlotNumberArray.PrismTableArray;
import teamHTBP.vida.Vida;

public class ContainerTypeLoader {
    public final static DeferredRegister<ContainerType<?>> CONTAINER_TYPES = new DeferredRegister<>(ForgeRegistries.CONTAINERS, Vida.modId);

    public static RegistryObject<ContainerType<ContainerPrismTable>> prismTable = CONTAINER_TYPES.register("container_prismtable", () ->{
        return IForgeContainerType.create((int id, PlayerInventory inventory, PacketBuffer buffer)->{
            return new ContainerPrismTable(id, inventory, buffer.readBlockPos(), Minecraft.getInstance().world,new PrismTableArray());
        });
    });

}